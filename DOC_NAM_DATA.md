# DOC_NAM — Tài liệu thành viên Nam: Cơ sở dữ liệu & Data Layer

> **Vai trò:** Bạn là "kiến trúc sư dữ liệu". Tài liệu liệt kê **từng file, từng entity, từng hàm DAO/Repository**, kèm giải thích **vì sao** chọn thiết kế này và **triển khai** thuật toán SRS như thế nào.

---

## Mục lục

1. [Phạm vi & thư mục](#1-phạm-vi--thư-mục)
2. [Công nghệ & vì sao chọn](#2-công-nghệ--vì-sao-chọn)
3. [CHI TIẾT TỪNG FILE](#3-chi-tiết-từng-file)
   - 3.1 `model/` (8 entity + enum + class phụ)
   - 3.2 `local/AppDatabase.kt`
   - 3.3 `local/` 8 DAO (từng hàm)
   - 3.4 `local/DefaultVocabData.kt`
   - 3.5 `repository/FlashCardRepository.kt` (từng hàm)
   - 3.6 `database/databaseflashcard.sql`
4. [Thuật toán SRS (SuperMemo-2)](#4-thuật-toán-srs-supermemo-2)
5. [Luồng dữ liệu bất đồng bộ](#5-luồng-dữ-liệu-bất-đồng-bộ)
6. [Cẩm nang sửa đổi](#6-cẩm-nang-sửa-đổi)
7. [Q&A mở rộng](#7-qa-mở-rộng)

---

## 1. Phạm vi & thư mục

```
data/
├── model/          → 8 entity + AppLanguage + StudySchedule + DeckWithStats
├── local/          → AppDatabase + 8 DAO + DefaultVocabData
└── repository/     → FlashCardRepository
database/databaseflashcard.sql
```

---

## 2. Công nghệ & vì sao chọn

- **Room (ORM) thay vì SQLite thuần:** ánh xạ object tự động, check SQL compile-time, Flow/Coroutines.
- **Không dùng Realm/ObjectBox/DataStore:** Realm ít phổ biến; DataStore chỉ cho key-value.
- **Flow thay vì LiveData:** API coroutine gốc, nhiều toán tử (`flatMapLatest`, `stateIn`, `first()`), dễ test.
- **8 DAO (Single Responsibility)** thay vì 1 DAO lớn.
- **Singleton + `@Volatile` + `synchronized`** cho AppDatabase (một instance duy nhất).
- **`fallbackToDestructiveMigration()`** cho dev (tự xoá/tạo lại khi đổi version); production sẽ viết `Migration`.
- **Khoá chính:** deck = `String` (đọc được, tiện seed); flashcard = `Long` autoGenerate (sinh tự động).

---

## 3. CHI TIẾT TỪNG FILE

### 3.1 `data/model/`

**`FlashCard.kt`**
- `@Entity(tableName="decks") data class DeckEntity(id: String, languageCode, title, subtitle, iconEmoji, level, colorHex, cardCount=0, isCustom=false)`.
- `@Entity(tableName="flashcards") data class FlashCardEntity(id: Long=0 autoGenerate, deckId, languageCode, frontWord, phonetic, partOfSpeech, frontExample, backMeaning, backExampleTranslation, memoryTip="", difficulty=0, isStarred=false, isMastered=false, reviewCount=0, lastReviewedTimestamp=0L, srsInterval=1, srsEaseFactor=2.5f, srsRepetitions=0, nextReviewTimestamp=0L)`.
- `data class DeckWithStats(deck, totalCards, masteredCards, learningCards)` + `progressPercent` getter.

**`Language.kt`**
- `enum class AppLanguage(code, displayName, nativeName, flagEmoji, bubbleColor, ttsLanguageTag, description)` — 10 giá trị: ENGLISH, KOREAN, JAPANESE, VIETNAMESE, CHINESE, FRENCH, SPANISH, GERMAN, ITALIAN, PORTUGUESE.
- `companion fun fromCode(code): AppLanguage` (mặc định ENGLISH).

**`QuizRecordEntity.kt`** — `@Entity("quiz_records") QuizRecordEntity(id auto, deckId, deckTitle, mode="QUIZ", score, totalQuestions, pointsEarned, maxStreak, accuracyPercent, timeSpentSeconds, timestamp)`.

**`StudySchedule.kt`** — `data class StudySchedule(isEnabled=true, reminderHour=19, reminderMinute=0, remindStreak=true, remindDueWords=true, minWordsThreshold=1)` (không phải bảng).

**`StudyScheduleEntity.kt`** — `@Entity("study_schedules") StudyScheduleEntity(id=1, isEnabled, reminderHour=19, reminderMinute=0, remindStreak, remindDueWords, minWordsThreshold=1, targetLanguageCode="ja", updatedTimestamp)`.

**`StudySessionEntity.kt`** — `@Entity("study_sessions") StudySessionEntity(id auto, deckId, deckTitle, languageCode, cardsStudied, masteredCount, durationSeconds, timestamp)`.

**`UserAccountEntity.kt`** — `@Entity("user_accounts", indices=[Index("username", unique=true)]) UserAccountEntity(id auto, username, passwordHash, createdAt, lastLoginAt, isLoggedIn=false)`.

**`UserLanguageEntity.kt`** — `@Entity("user_languages") UserLanguageEntity(languageCode PK, displayName, flagEmoji, isCurrentActive=false, dailyGoalCards=20, masteredCardsCount=0, totalWordsEnrolled=50, streakDays=0, level="Mới bắt đầu", enrolledTimestamp, lastStudiedTimestamp)`.

**`UserProfileEntity.kt`** — `@Entity("user_profile") UserProfileEntity(id=1, userName="Bạn Học", avatarEmoji="🦉", avatarBgColorHex="#EEF2FF", vipLevel=1, streakDays=7, maxStreakDays=7, totalPoints=1500, totalCardsLearned=45, lastActiveTimestamp)`.

### 3.2 `data/local/AppDatabase.kt`

- `@Database(entities=[DeckEntity, FlashCardEntity, StudySessionEntity, QuizRecordEntity, UserProfileEntity, UserAccountEntity, StudyScheduleEntity, UserLanguageEntity], version=4, exportSchema=false) abstract class AppDatabase`.
- 8 hàm abstract: `deckDao()`, `flashCardDao()`, `studySessionDao()`, `quizRecordDao()`, `userProfileDao()`, `userAccountDao()`, `studyScheduleDao()`, `userLanguageDao()`.
- `companion object`:
  - `private var INSTANCE: AppDatabase?` (`@Volatile`).
  - `fun getDatabase(context, scope): AppDatabase` — Singleton (`synchronized`), `.fallbackToDestructiveMigration()`, `.addCallback(DatabaseCallback(scope))`.
  - `private class DatabaseCallback` — `onCreate` → `scope.launch { populateInitialData(...) }`.
  - `suspend fun populateInitialData(deckDao, flashCardDao, userProfileDao, userLanguageDao=null)` — chèn 12 deck + flashcard + profile mặc định + 4 ngôn ngữ (en active, ja, ko, vi).

### 3.3 8 DAO — từng hàm

**`DeckDao.kt`**
- `getDecksByLanguage(langCode): Flow<List<DeckEntity>>` (ORDER BY title)
- `getAllDecks(): Flow<List<DeckEntity>>`
- `getDeckByIdFlow(deckId): Flow<DeckEntity?>`
- `getDeckById(deckId): suspend DeckEntity?`
- `getCustomDecks(): Flow<List<DeckEntity>>` (isCustom=1)
- `getDecksByLevel(level): Flow<List<DeckEntity>>`
- `searchDecks(query): Flow<List<DeckEntity>>` (LIKE title/subtitle)
- `getTotalDecksCount(): Flow<Int>`
- `getDecksCountByLanguage(langCode): Flow<Int>`
- `insertDeck(deck)`, `insertDecks(decks)` (REPLACE)
- `updateDeck(deck)`, `updateCardCount(deckId, count)`
- `deleteDeck(deck)`, `deleteDeckById(deckId)`

**`FlashCardDao.kt`**
- Theo deck/ngôn ngữ: `getCardsForDeck`, `getCardsByLanguage`, `getAllCards`, `getCardByIdFlow`, `getCardById`.
- Trạng thái & SRS: `getStarredCards`, `getStarredCardsByLanguage`, `getMasteredCards`, `getMasteredCardsByLanguage`, `getMasteredCardsForDeck`, `getLearningCardsForDeck`, `getDueCardsForLanguage`, `getAllDueCards`, `getDueCardsForDeck`, `getStarterCardsForLanguage`, `getCardsByDifficulty`.
- Ngẫu nhiên: `getRandomCardsForDeck`, `getRandomCardsByLanguage`, `getRandomStarredCards` (ORDER BY RANDOM()).
- Tìm kiếm: `searchCards(query)`.
- Thống kê: `getMasteredCount`, `getMasteredCountByLanguage`, `getMasteredCountForDeck`, `getStarredCount`, `getTotalCardsCount`, `getCardsCountForDeck`, `getCardsCountByLanguage`, `getDueCountForLanguage`, `getTotalDueCount`, `getDueCountForDeck`, `getCardsStudiedTodayCount`, `getCardsStudiedTodayByLanguageCount`.
- Ghi: `insertCard`, `insertCards`, `updateCard`, `deleteCard`, `deleteCardById`, `deleteCardsByDeckId`.
- Ôn tập: `toggleStar(id, starred)`, `recordReview(id, mastered, difficulty, timestamp)`, `updateSrsReview(id, isMastered, difficulty, timestamp, nextReviewTimestamp, srsInterval, srsEaseFactor, srsRepetitions)`, `resetDeckProgress(deckId)`.

**`QuizRecordDao.kt`**
- `getAllRecords`, `getRecentRecords(limit)`, `getRecordsByMode(mode)`, `getRecordsForDeck(deckId)`.
- `getHighestScoreForDeck(deckId): Flow<Int?>`, `getTotalPointsEarned(): Flow<Int?>`, `getHighestStreak(): Flow<Int?>`, `getAverageAccuracy(): Flow<Float?>`, `getTotalGamesPlayed(): Flow<Int>`.
- `insertRecord`, `deleteRecord`, `deleteRecordById`, `clearQuizHistory`.

**`StudySessionDao.kt`**
- `getAllSessions`, `getRecentSessions(limit)`, `getSessionsForDeck`, `getSessionsByLanguage`, `getSessionsSince(sinceTimestamp)`.
- `getTotalCardsStudied(): Flow<Int?>`, `getTotalStudyTimeSeconds(): Flow<Long?>`, `getTotalSessionCount(): Flow<Int>`.
- `insertSession`, `deleteSession`, `deleteSessionById`, `clearAllSessions`.

**`UserProfileDao.kt`**
- `getUserProfile(): Flow<UserProfileEntity?>`, `getUserProfileDirect(): suspend UserProfileEntity?`.
- `insertOrUpdateProfile`, `updateName`, `updateVipLevel`, `updateAvatar(emoji, bgColorHex)`, `updateStreak(streak, lastActive)` (giữ maxStreakDays), `addPoints(points)`, `incrementCardsLearned(count)`.

**`UserAccountDao.kt`**
- `getUserByUsername(username)`, `getUserByUsernameFlow(username)`, `authenticate(username, passwordHash)`, `isUsernameExists(username): Int`, `getActiveLoggedInUser()`, `getActiveLoggedInUserDirect()`, `getAllAccounts()`, `getTotalUserCount()`.
- `registerUser(user)` (ABORT), `insertOrUpdate(user)`, `updateUser(user)`, `setLoggedIn(userId, loginTime)`, `logoutAllUsers()`, `updatePassword(username, newPasswordHash)`, `deleteAccount(user)`, `deleteAccountByUsername(username)`.

**`UserLanguageDao.kt`**
- `getAllLearningLanguages`, `getActiveLearningLanguage()`, `getActiveLearningLanguageDirect()`, `getLanguageByCode(code)`, `getLanguageDirect(code)`, `getEnrolledLanguageCodes()`, `isLanguageEnrolled(code): Flow<Boolean>`, `getEnrolledLanguagesCount()`.
- `insertLanguage`, `insertLanguages`, `updateLanguage`.
- `clearActiveFlag()`, `markActiveLanguage(code, timestamp)`, `switchActiveLanguage(code, timestamp)` (`@Transaction`).
- `updateDailyGoal`, `incrementMasteredCount(code, increment, timestamp)`, `updateTotalWordsEnrolled`, `updateLanguageLevel`, `updateLanguageStreak`, `deleteLanguage`, `deleteLanguageByCode`.

**`StudyScheduleDao.kt`**
- `getSchedule(): Flow<StudyScheduleEntity?>`, `getScheduleDirect(): suspend StudyScheduleEntity?`.
- `saveSchedule`, `updateSchedule`, `setReminderEnabled(enabled, timestamp)`, `updateReminderTime(hour, minute, timestamp)`, `updateTargetLanguage(langCode, timestamp)`.

### 3.4 `data/local/DefaultVocabData.kt`

- `object DefaultVocabData`.
- `fun getDefaultDecks(): List<DeckEntity>` — 12 bộ thẻ (en_basics 95, en_daily, en_travel, en_business, ko_beginner 76, ko_daily, ja_n5 128, ja_daily, vi_basic 54, zh_hsk1, fr_basic 50).
- `fun getDefaultFlashCards(): List<FlashCardEntity>` — từ mẫu đầy đủ phiên âm/ví dụ/nghĩa/mẹo.

### 3.5 `data/repository/FlashCardRepository.kt`

Constructor: `FlashCardRepository(deckDao, cardDao, sessionDao, quizDao, profileDao, languageDao)` + `constructor(database: AppDatabase)`.

- Seed: `checkAndSeedDatabase()`.
- **Deck:** `getDecksByLanguage`, `getAllDecks`, `getCustomDecks`, `getDecksByLevel`, `searchDecks`, `getDeckById`, `getDeckByIdFlow`, `insertDeck`, `insertDecks`, `updateDeck`, `deleteDeck`, `deleteDeckById`.
- **Flashcard & SRS:** `getCardsForDeck`, `getCardsByLanguage`, `getAllCards`, `getStarredCards(ByLanguage)`, `getMasteredCards(ByLanguage)`, `getDueCardsForLanguage`, `getAllDueCards`, `getStarterCardsForLanguage`, `getCardsByDifficulty`, `getRandomCardsForDeck`, `getRandomCardsByLanguage`, `searchCards`, `getMasteredCount(ByLanguage)`, `getStarredCount`, `getTotalCardsCount`, `getCardsCountForDeck`, `getCardsCountByLanguage`, `getDueCountForLanguage`, `getTotalDueCount`, `getCardsStudiedTodayCount(ByLanguage)`, `getCardById`, `insertCards`, `insertCard`, `updateCard`, `deleteCard`, `deleteCardById`, `toggleStar`, `recordSrsReview(card, rating, isCorrect)`, `recordCardReview(id, difficulty)`, `resetDeckProgress`.
- **Multi-Language:** `getAllLearningLanguages`, `getActiveLearningLanguage`, `getEnrolledLanguageCodes`, `isLanguageEnrolled`, `addLearningLanguage(language)`, `switchActiveLanguage`, `updateLanguageDailyGoal`, `deleteLearningLanguage`.
- **Study Sessions:** `getAllStudySessions`, `getRecentStudySessions`, `getSessionsForDeck`, `getTotalCardsStudiedCount`, `getTotalStudyTimeSeconds`, `recordStudySession`, `clearStudyHistory`.
- **Quiz Records:** `getAllQuizRecords`, `getRecentQuizRecords`, `getQuizRecordsByMode`, `getHighestQuizScoreForDeck`, `getTotalQuizPointsEarned`, `getHighestStreakRecord`, `getTotalGamesPlayedCount`, `recordQuizResult`, `clearQuizHistory`.
- **Profile:** `getUserProfile`, `getUserProfileDirect`, `saveUserProfile`, `updateUserName`, `updateUserVipLevel`, `updateUserAvatar`, `updateStreak`, `addPoints`, `incrementCardsLearned`.

### 3.6 `database/databaseflashcard.sql`

Lược đồ SQL **tham khảo/trình bày** (8 bảng, có `FOREIGN KEY ... ON DELETE CASCADE`, `salt`, `email`, ...). Không đồng bộ hoàn toàn với entity Room (Room không khai báo FK cứng). Dùng khi trình bày thiết kế CSDL.

---

## 4. Thuật toán SRS (SuperMemo-2)

`recordSrsReview(card, rating, isCorrect)`:

- **Đúng & rating ≥ 3:** interval theo `repetitions`: 0→1, 1→3, 2→6, ≥3→`interval*easeFactor` (tối thiểu 1). Công thức SM-2: `EF' = EF + (0.1 - (5-q)*(0.08 + (5-q)*0.02))`, `q=rating`, tối thiểu 1.3. `isMastered = repetitions >= 3`. `nextReview = now + interval*24h`.
- **Sai:** `repetitions=0`, `interval=1`, `nextReview = now + 15 phút`, `isMastered=false`.
- `difficulty` quy đổi: rating 5→1, 4/3→2, còn lại→3. Ghi `updateSrsReview`; nếu mastered → `incrementMasteredCount`.

> `recordSrsReview` hiện chưa nối UI; nút học gọi `recordCardReview` (đơn giản). Xem doc Tuấn để kích hoạt.

---

## 5. Luồng dữ liệu bất đồng bộ

1. Đọc trả `Flow` → Room tự phát lại khi bảng đổi.
2. ViewModel chuyển `Flow → StateFlow` qua `.stateIn(WhileSubscribed(5000), initial)`.
3. Ghi là `suspend`, chạy `Dispatchers.IO`.

---

## 6. Cẩm nang sửa đổi

| Yêu cầu | Nơi sửa |
| --- | --- |
| Thêm bảng | entity mới + `@Database.entities` + DAO mới + `abstract fun` |
| Thêm cột | entity + tăng `version` |
| Thêm truy vấn | DAO tương ứng |
| Thêm hàm repository | `FlashCardRepository.kt` |
| Thêm ngôn ngữ | `Language.kt` + `Color.kt` + seed |
| Kích hoạt SRS | nối ViewModel → `recordSrsReview` |
| Nối đăng nhập DB | `UserAccountDao` (hash mật khẩu) |
| Chuyển dữ liệu sang JSON | `assets/` + Moshi + thay `populateInitialData` |
| Viết Migration | `Migration(3,4){...}` + `.addMigrations` |

---

## 7. Q&A mở rộng

### Q1. Vì sao Room mà không SQLite thuần/Realm/DataStore?
ORM chính thức Jetpack, compile-time check, Flow/Coroutines; Realm ít phổ biến; DataStore chỉ key-value.

### Q2. Dữ liệu từ đâu, nạp lúc nào?
Nhúng trong `DefaultVocabData.kt`, nạp qua `onCreate` Callback + `checkAndSeedDatabase` (nếu `COUNT(*)==0`).

### Q3. Vì sao nhúng code thay vì JSON?
Kiểu an toàn (compiler check), không cần parser; JSON chỉ lộ lỗi runtime.

### Q4. SRS cài đặt thế nào?
SM-2: interval theo số lần đúng (1→3→6→`interval*easeFactor`), ease factor theo công thức, sai reset + 15 phút, thuộc khi ≥3 lần đúng.

### Q5. Vì sao đọc Flow mà ghi suspend?
Đọc cần phản ứng; ghi là thao tác một lần chạy IO.

### Q6. Vì sao 8 DAO?
Single Responsibility.

### Q7. Vì sao Singleton + `@Volatile` + `synchronized`?
Một instance DB duy nhất, chống tạo trùng, đảm bảo visibility.

### Q8. `fallbackToDestructiveMigration` có an toàn?
Tiện dev (tự xoá/tạo lại, tránh crash) nhưng mất dữ liệu; production viết Migration.

### Q9. Lưu mật khẩu thế nào?
Trường `passwordHash`; demo chưa nối DAO; nối thật sẽ hash (SHA-256 + salt).

### Q10. Vì sao `UserLanguageEntity` tách riêng khỏi `UserProfile`?
Một user học nhiều ngôn ngữ song song → cần bảng riêng khoá `languageCode`.

### Q11. Vì sao tách `StudySession` khỏi `QuizRecord`?
Học thẻ (số thẻ/thuộc/thời lượng) khác trò chơi (điểm/chuỗi/độ chính xác).

### Q12. Vì sao không khai báo `ForeignKey` cứng?
Room đòi indices + cascade phức tạp; liên kết mức logic (xoá deck tự xoá thẻ). File SQL tham khảo vẫn thể hiện `ON DELETE CASCADE`.
