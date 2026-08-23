# DOC_NAM — Tài liệu thành viên Nam: Cơ sở dữ liệu & Data Layer

> **Vai trò:** Bạn là "kiến trúc sư dữ liệu" — thiết kế, lưu trữ và truy xuất toàn bộ dữ liệu. Tài liệu này giải thích **vì sao** chọn công nghệ/thiết kế này (mà không dùng cách khác) và **triển khai** từng bảng/truy vấn/thuật toán như thế nào, để bạn trả lời trọn vẹn mọi câu hỏi của giám khảo.

---

## Mục lục

1. [Phạm vi & thư mục](#1-phạm-vi--thư-mục)
2. [Công nghệ & vì sao chọn](#2-công-nghệ--vì-sao-chọn)
3. [Mô hình dữ liệu (8 bảng)](#3-mô-hình-dữ-liệu-8-bảng)
4. [AppLanguage — enum ngôn ngữ](#4-applanguage--enum-ngôn-ngữ)
5. [8 DAO — toàn bộ truy vấn](#5-8-dao--toàn-bộ-truy-vấn)
6. [AppDatabase — cấu hình & seed](#6-appdatabase--cấu-hình--seed)
7. [DefaultVocabData — dữ liệu mẫu](#7-defaultvocabdata--dữ-liệu-mẫu)
8. [FlashCardRepository — lớp trung gian](#8-flashcardrepository--lớp-trung-gian)
9. [Thuật toán SRS (SuperMemo-2) chi tiết](#9-thuật-toán-srs-supermemo-2-chi-tiết)
10. [File SQL tham khảo](#10-file-sql-tham-khảo)
11. [Luồng dữ liệu bất đồng bộ](#11-luồng-dữ-liệu-bất-đồng-bộ)
12. [Cẩm nang sửa đổi Data Layer](#12-cẩm-nang-sửa-đổi-data-layer)
13. [Q&A mở rộng — trả lời giám khảo](#13-qa-mở-rộng--trả-lời-giám-khảo)

---

## 1. Phạm vi & thư mục

```
data/
├── model/          → 8 entity + AppLanguage + StudySchedule + DeckWithStats
├── local/          → AppDatabase + 8 DAO + DefaultVocabData
└── repository/     → FlashCardRepository
database/databaseflashcard.sql   # lược đồ SQL tham khảo
```

---

## 2. Công nghệ & vì sao chọn

### 2.1 Vì sao dùng Room thay vì SQLite thuần?
- **ORM:** Room ánh xạ kết quả truy vấn thành object Kotlin, không cần đọc `Cursor` thủ công.
- **An toàn compile-time:** câu SQL trong `@Query` được KSP kiểm tra lúc build, sai cú pháp/sai cột sẽ báo lỗi ngay.
- **Tích hợp Flow/Coroutines:** `Flow` cho dữ liệu phản ứng, `suspend` cho ghi — SQLite thuần không có sẵn.

### 2.2 Vì sao không dùng Realm / ObjectBox / DataStore?
- Realm/ObjectBox cũng là ORM nhưng cộng đồng/độ ổn định trong hệ sinh thái Jetpack kém hơn Room, và Room là khuyến nghị chính thức của Google.
- DataStore chỉ lưu key-value nhỏ (preferences), **không phù hợp** cho dữ liệu quan hệ (thẻ ↔ bộ thẻ, lịch sử, tài khoản). Còn việc lưu cờ "Để sau" nhỏ thì em dùng `SharedPreferences` (xem doc của Tuấn).

### 2.3 Vì sao dùng Flow thay vì LiveData?
- `Flow` là API gốc của coroutine, có nhiều toán tử hơn (`flatMapLatest`, `stateIn`, `combine`, `map`, `first()`...), dễ test hơn.
- LiveData hướng Android, khó kết hợp nhiều luồng phức tạp. Room hỗ trợ cả hai, nhưng chọn `Flow` để nhất quán với kiến trúc coroutine.

### 2.4 Vì sao tách thành 8 DAO thay vì 1 DAO lớn?
Theo **Single Responsibility**: mỗi DAO phụ trách một nhóm dữ liệu (bộ thẻ, thẻ từ, phiên học, kết quả quiz, hồ sơ, tài khoản, lịch nhắc, ngôn ngữ). Dễ bảo trì, dễ test, dễ mở rộng từng phần.

### 2.5 Vì sao dùng Singleton + `@Volatile` + `synchronized` cho AppDatabase?
Room khuyến nghị **chỉ tạo một instance** DB (tốn kém khi mở nhiều connection). `synchronized(this)` đảm bảo chỉ một luồng tạo instance; `@Volatile` đảm bảo các luồng khác thấy giá trị mới ngay lập tức (tránh tạo trùng).

### 2.6 Vì sao dùng `fallbackToDestructiveMigration()` thay vì viết Migration?
- Trong giai đoạn phát triển/demo, schema thay đổi liên tục → viết `Migration` từng bước tốn thời gian. `fallbackToDestructiveMigration` tự **xoá & tạo lại** DB khi version tăng, tránh crash.
- Đánh đổi: **mất dữ liệu** khi nâng cấp. Khi lên production em sẽ viết `Migration` thủ công (xem mục 12).

### 2.7 Vì sao khoá chính deck là `String` (tự đặt) còn flashcard là `Long` autoGenerate?
- **Deck** cần id ổn định, đọc được (`"en_basics"`, `"custom_..."`) để tiện seed và tham chiếu trong code.
- **Flashcard** số lượng lớn, cần sinh tự động → `@PrimaryKey(autoGenerate = true) val id: Long`.

---

## 3. Mô hình dữ liệu (8 bảng)

### 3.1 `DeckEntity` — bảng `decks`
`@PrimaryKey id: String`, `languageCode, title, subtitle, iconEmoji, level, colorHex, cardCount(=0), isCustom(=false)`.

### 3.2 `FlashCardEntity` — bảng `flashcards` (có thông số SRS)
`id: Long(auto), deckId, languageCode, frontWord, phonetic, partOfSpeech, frontExample, backMeaning, backExampleTranslation, memoryTip, difficulty(0-3), isStarred, isMastered, reviewCount, lastReviewedTimestamp` **+** 4 cột SRS:
- `srsInterval: Int = 1` — khoảng ngày tới lần ôn sau.
- `srsEaseFactor: Float = 2.5f` — hệ số dễ nhớ.
- `srsRepetitions: Int = 0` — số lần đúng liên tiếp.
- `nextReviewTimestamp: Long = 0L` — mốc đến hạn ôn.

### 3.3 `StudySessionEntity` — bảng `study_sessions`
`deckId, deckTitle, languageCode, cardsStudied, masteredCount, durationSeconds, timestamp`.

### 3.4 `QuizRecordEntity` — bảng `quiz_records`
`deckId, deckTitle, mode("QUIZ"/"MATCH"), score, totalQuestions, pointsEarned, maxStreak, accuracyPercent, timeSpentSeconds, timestamp`.

### 3.5 `UserProfileEntity` — bảng `user_profile` (khoá `id = 1`)
`userName("Bạn Học"), avatarEmoji("🦉"), avatarBgColorHex, vipLevel(1), streakDays(7), maxStreakDays, totalPoints(1500), totalCardsLearned, lastActiveTimestamp`.

### 3.6 `UserAccountEntity` — bảng `user_accounts` (unique `username`)
`username, passwordHash, createdAt, lastLoginAt, isLoggedIn`. Có `@Index(value=["username"], unique=true)`.

### 3.7 `StudyScheduleEntity` — bảng `study_schedules` (khoá `id = 1`)
`isEnabled, reminderHour(19), reminderMinute, remindStreak, remindDueWords, minWordsThreshold, targetLanguageCode("ja"), updatedTimestamp`.

### 3.8 `UserLanguageEntity` — bảng `user_languages` (khoá `languageCode`)
`displayName, flagEmoji, isCurrentActive, dailyGoalCards(20), masteredCardsCount, totalWordsEnrolled(50), streakDays, level, enrolledTimestamp, lastStudiedTimestamp`.

### Class không phải bảng
- **`DeckWithStats`** — tính `progressPercent`.
- **`StudySchedule`** — data class cấu hình lịch (không `@Entity`), dùng cho AlarmManager/notification.

---

## 4. AppLanguage — enum ngôn ngữ

**10 ngôn ngữ:** ENGLISH(en), KOREAN(ko), JAPANESE(ja), VIETNAMESE(vi), CHINESE(zh), FRENCH(fr), SPANISH(es), GERMAN(de), ITALIAN(it), PORTUGUESE(pt). Mỗi giá trị có `code, displayName, nativeName, flagEmoji, bubbleColor, ttsLanguageTag, description`; companion `fromCode(code)` mặc định `ENGLISH`.

**Vì sao dùng enum?** Số ngôn ngữ hữu hạn và cố định → enum cho kiểu an toàn, tránh lỗi gõ sai code, dễ dùng `when`/`values()`.

---

## 5. 8 DAO — toàn bộ truy vấn

### 5.1 `DeckDao`
`getDecksByLanguage` (ORDER BY title), `getAllDecks`, `getDeckByIdFlow`/`getDeckById`, `getCustomDecks` (isCustom=1), `getDecksByLevel`, `searchDecks` (LIKE title/subtitle), `getTotalDecksCount`, `getDecksCountByLanguage`, `insertDeck(s)`, `updateDeck`, `updateCardCount`, `deleteDeck`, `deleteDeckById`.

### 5.2 `FlashCardDao` (lớn nhất)
- **Theo deck/ngôn ngữ:** `getCardsForDeck`, `getCardsByLanguage`, `getAllCards`, `getCardById(Flow)`.
- **Trạng thái & SRS:** `getStarredCards(byLanguage)`, `getMasteredCards(byLanguage/ForDeck)`, `getLearningCardsForDeck` (isMastered=0), `getDueCardsForLanguage/AllDueCards/DueCardsForDeck` (`nextReviewTimestamp <= now OR isMastered=0`), `getStarterCardsForLanguage` (reviewCount=0), `getCardsByDifficulty`.
- **Ngẫu nhiên:** `getRandomCardsForDeck`, `getRandomCardsByLanguage`, `getRandomStarredCards` (ORDER BY RANDOM()).
- **Tìm kiếm:** `searchCards` (LIKE frontWord/backMeaning/frontExample).
- **Thống kê:** `getMasteredCount(byLanguage/ForDeck)`, `getStarredCount`, `getTotalCardsCount`, `getCardsCountForDeck/ByLanguage`, `getDueCountForLanguage/TotalDue/DueForDeck`, `getCardsStudiedTodayCount/ByLanguage`.
- **Ghi:** `insertCard(s)`, `updateCard`, `deleteCard(ById)`, `deleteCardsByDeckId`.
- **Cập nhật ôn tập:** `toggleStar`, `recordReview`, `updateSrsReview`, `resetDeckProgress`.

**Vì sao "due cards" dùng điều kiện `nextReviewTimestamp <= now OR isMastered = 0`?** Một thẻ "đến hạn" khi hoặc đã tới giờ ôn theo SRS, hoặc **chưa thuộc** (isMastered=0) — đảm bảo từ mới luôn được đưa vào ôn, không bị bỏ sót.

### 5.3 `QuizRecordDao`
`getAllRecords`, `getRecentRecords`, `getRecordsByMode`, `getRecordsForDeck`, `getHighestScoreForDeck`, `getTotalPointsEarned`, `getHighestStreak`, `getAverageAccuracy`, `getTotalGamesPlayed`, `insertRecord`, `deleteRecord(ById)`, `clearQuizHistory`.

### 5.4 `StudySessionDao`
`getAllSessions`, `getRecentSessions`, `getSessionsForDeck/ByLanguage/Since`, `getTotalCardsStudied`, `getTotalStudyTimeSeconds`, `getTotalSessionCount`, `insertSession`, `deleteSession(ById)`, `clearAllSessions`.

### 5.5 `UserProfileDao`
`getUserProfile(Flow/Direct)`, `insertOrUpdateProfile`, `updateName`, `updateVipLevel`, `updateAvatar`, `updateStreak` (giữ `maxStreakDays = MAX(maxStreakDays, streak)`), `addPoints`, `incrementCardsLearned`.

### 5.6 `UserAccountDao`
`getUserByUsername(Flow/Direct)`, `authenticate(username, passwordHash)`, `isUsernameExists`, `getActiveLoggedInUser`, `getAllAccounts`, `registerUser` (ABORT), `insertOrUpdate`, `setLoggedIn`, `logoutAllUsers`, `updatePassword`, `deleteAccount(ByUsername)`.

### 5.7 `UserLanguageDao`
`getAllLearningLanguages`, `getActiveLearningLanguage`, `getLanguageByCode/Direct`, `getEnrolledLanguageCodes`, `isLanguageEnrolled`, `getEnrolledLanguagesCount`, `insertLanguage(s)`, `updateLanguage`, `switchActiveLanguage` (`@Transaction`), `updateDailyGoal`, `incrementMasteredCount`, `updateTotalWordsEnrolled`, `updateLanguageLevel/Streak`, `deleteLanguage(ByCode)`.

### 5.8 `StudyScheduleDao`
`getSchedule(Flow/Direct)`, `saveSchedule`, `updateSchedule`, `setReminderEnabled`, `updateReminderTime`, `updateTargetLanguage`.

---

## 6. AppDatabase — cấu hình & seed

```kotlin
@Database(entities = [DeckEntity, FlashCardEntity, StudySessionEntity, QuizRecordEntity,
    UserProfileEntity, UserAccountEntity, StudyScheduleEntity, UserLanguageEntity],
    version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deckDao(); flashCardDao(); studySessionDao(); quizRecordDao();
    abstract fun userProfileDao(); userAccountDao(); studyScheduleDao(); userLanguageDao();
}
```

- **Tên DB:** `ntk_flashcard_db` · **Singleton** (`@Volatile` + `synchronized`) · **`.fallbackToDestructiveMigration()`**.
- **Seed kép:**
  1. `DatabaseCallback.onCreate` → `populateInitialData(...)` chèn 12 deck + flashcard mẫu + `UserProfileEntity` mặc định + 4 `UserLanguageEntity` (en active, ja, ko, vi).
  2. `Repository.checkAndSeedDatabase()` — nếu `COUNT(*) == 0` thì seed lại (phòng DB rỗng do fallback/xoá).

**Vì sao seed ở 2 nơi?** Callback chỉ chạy **lần đầu tạo DB**; `checkAndSeedDatabase` chạy mỗi lần khởi động app, đảm bảo DB không bao giờ rỗng kể cả khi bị reset.

---

## 7. DefaultVocabData — dữ liệu mẫu

`object DefaultVocabData` — dữ liệu **nhúng trong code** (không phải JSON):
- `getDefaultDecks()`: 12 bộ thẻ (en_basics, en_daily, en_travel, en_business, ko_beginner, ko_daily, ja_n5, ja_daily, vi_basic, zh_hsk1, fr_basic).
- `getDefaultFlashCards()`: từ mẫu đầy đủ phiên âm/ví dụ/nghĩa/mẹo nhớ.

**Vì sao nhúng trong code thay vì JSON/assets?**
1. Đơn giản: không cần viết parser, không phụ thuộc file ngoài.
2. Kiểu an toàn: dữ liệu là `List<DeckEntity>` được compiler kiểm tra (sai trường báo lỗi ngay), còn JSON thì sai sót chỉ lộ lúc runtime.
3. Dễ đọc/sửa trực tiếp.
Đánh đổi: file nguồn dài; nếu dữ liệu lớn (hàng nghìn từ) em sẽ chuyển sang JSON trong `assets/` + Moshi (đã có dependency sẵn) — xem mục 12.

---

## 8. FlashCardRepository — lớp trung gian

```kotlin
class FlashCardRepository(deckDao, cardDao, sessionDao, quizDao, profileDao, languageDao) {
    constructor(database: AppDatabase) : this(...)
}
```
6 nhóm: **Deck / Flashcard+SRS / Multi-Language / Study Sessions / Quiz Records / Profile**. Mọi thao tác ghi bọc `withContext(Dispatchers.IO)`.

**Vì sao Repository phải bọc `withContext(Dispatchers.IO)` cho thao tác ghi?** Room yêu cầu các thao tác `suspend` ghi không chạy trên Main Thread. Bọc IO đảm bảo không chặn UI, đồng thời giúp ViewModel không phải quan tâm dispatcher nào.

**Vì sao đọc trả `Flow` trực tiếp không bọc IO?** Room tự xử lý luồng cho `Flow` trả về (query chạy trên nền riêng), nên chỉ cần trả thẳng.

---

## 9. Thuật toán SRS (SuperMemo-2) chi tiết

`recordSrsReview(card, rating, isCorrect)` trong Repository:

**Nếu đúng & rating ≥ 3:**
| repetitions | interval mới |
| --- | --- |
| 0 | 1 ngày |
| 1 | 3 ngày |
| 2 | 6 ngày |
| ≥3 | `interval * easeFactor` (tối thiểu 1) |

- Công thức SM-2: `EF' = EF + (0.1 - (5-q)*(0.08 + (5-q)*0.02))`, `q = rating`, tối thiểu `1.3`.
- `newRepetitions += 1`; `isMastered = repetitions >= 3`; `nextReview = now + interval*24h`.

**Nếu sai:** `repetitions=0`, `interval=1`, `nextReview = now + 15 phút`, `isMastered=false`.

**Quy đổi difficulty:** rating 5→1 (Dễ), 4/3→2 (Vừa), còn lại→3 (Khó). Sau đó ghi `updateSrsReview(...)`; nếu mastered → `incrementMasteredCount`.

**Ví dụ minh hoạ:** thẻ có `easeFactor=2.5`, đã đúng 2 lần (repetitions=2), trả lời đúng với rating 4:
- repetitions=2 → interval=6 ngày; `q=4` → `EF = 2.5 + (0.1 - 1*(0.08 + 1*0.02)) = 2.5 + 0.0 = 2.5`; `nextReview = now + 6 ngày`; `repetitions=3` → `isMastered = true`.

> **Chú ý:** `recordSrsReview` (SM-2) hiện **chưa được gọi từ UI**; nút trong `FlashcardStudyScreen` gọi `recordCardReview` (đơn giản, `isMastered = difficulty==1`). Nếu giám khảo yêu cầu kích hoạt SRS thật, chỉ cần đổi ViewModel gọi `recordSrsReview` (xem doc Tuấn).

---

## 10. File SQL tham khảo

`database/databaseflashcard.sql` là lược đồ **trình bày/thiết kế** (8 bảng, có `FOREIGN KEY ... ON DELETE CASCADE`, `salt`, `email`, ...). Nó **không đồng bộ hoàn toàn** với entity Room (Room không khai báo FK cứng, tên cột/trường khác đôi chút). Dùng để giải thích thiết kế CSDL khi trình bày báo cáo.

---

## 11. Luồng dữ liệu bất đồng bộ

1. Truy vấn đọc trả `Flow` → Room tự phát lại khi bảng thay đổi.
2. ViewModel chuyển `Flow → StateFlow` bằng `.stateIn(viewModelScope, WhileSubscribed(5000), initial)`.
3. Ghi là `suspend`, chạy `Dispatchers.IO`.

Ví dụ gắn sao: UI → `viewModel.toggleStar` → `repository.toggleStar(id, !current)` → DAO `toggleStar` → `getStarredCards()` phát lại → `SavedCardsDialog`/Starred tự cập nhật.

---

## 12. Cẩm nang sửa đổi Data Layer

| Yêu cầu | Nơi sửa | Lưu ý |
| --- | --- | --- |
| Thêm bảng | entity mới + thêm vào `@Database.entities` + DAO mới + `abstract fun` | tăng `version` |
| Thêm cột | entity tương ứng + tăng `version` | fallback tự xoá/tạo lại |
| Thêm truy vấn | DAO tương ứng | `Flow` (đọc) hay `suspend` (ghi) |
| Thêm hàm repository | `FlashCardRepository.kt` | bọc ghi `withContext(IO)` |
| Thêm ngôn ngữ | `Language.kt` + `Color.kt` + seed | nhớ map TTS |
| Kích hoạt SRS thật | nối ViewModel → `recordSrsReview` | |
| Nối đăng nhập DB | `UserAccountDao` đã đủ | hash mật khẩu trước khi lưu |
| Chuyển dữ liệu mẫu sang JSON | `assets/` + Moshi + thay `populateInitialData` | |
| Viết Migration bảo toàn dữ liệu | `AppDatabase` + `Migration(3,4){...}` + `.addMigrations(...)` | bỏ `fallbackToDestructiveMigration` |

---

## 13. Q&A mở rộng — trả lời giám khảo

### Q1. Vì sao dùng Room mà không dùng SQLite thuần / Realm / DataStore?
Xem mục 2. Tóm: Room = ORM chính thức của Jetpack, compile-time check, Flow/Coroutines; Realm kém phổ biến hơn; DataStore chỉ cho key-value.

### Q2. Dữ liệu từ vựng lấy từ đâu, nạp lúc nào?
Nhúng trong `DefaultVocabData.kt`, nạp qua `RoomDatabase.Callback.onCreate()` lần đầu + `checkAndSeedDatabase()` (nếu `COUNT(*)==0`) mỗi lần khởi động.

### Q3. Vì sao nhúng dữ liệu trong code thay vì JSON?
Kiểu an toàn (compiler kiểm tra), không cần parser, dễ sửa. JSON chỉ lộ lỗi lúc runtime; sẽ chuyển sang JSON+Moshi nếu dữ liệu lớn.

### Q4. Thuật toán SRS em cài đặt thế nào?
SuperMemo-2: interval theo số lần đúng liên tiếp (1→3→6→`interval*easeFactor`), ease factor theo công thức SM-2 (tối thiểu 1.3), sai thì reset + ôn sau 15 phút, "thuộc" khi đúng ≥3 lần liên tiếp. Lưu ở 4 cột `srsInterval/srsEaseFactor/srsRepetitions/nextReviewTimestamp`.

### Q5. Vì sao đọc dùng `Flow` mà ghi dùng `suspend`?
Đọc cần **phản ứng** (bảng đổi → UI tự cập nhật); ghi là thao tác một lần, dùng `suspend` chạy IO tránh chặn UI.

### Q6. Vì sao tách 8 DAO?
Single Responsibility — mỗi DAO một nhóm dữ liệu, dễ bảo trì/test/mở rộng.

### Q7. Vì sao Singleton + `@Volatile` + `synchronized`?
Room khuyến nghị một instance DB duy nhất (mở nhiều connection tốn kém). `synchronized` chống tạo trùng, `@Volatile` đảm bảo visibility giữa các luồng.

### Q8. `fallbackToDestructiveMigration` có an toàn không?
Tiện cho dev/demo (tự xoá & tạo lại khi đổi version, tránh crash) nhưng **mất dữ liệu**. Production sẽ viết `Migration` thủ công.

### Q9. Bảng `user_accounts` lưu mật khẩu thế nào?
Trường `passwordHash`; demo chưa nối DAO. Khi nối thật sẽ hash (SHA-256 + salt) trước khi lưu, không lưu mật khẩu thô.

### Q10. Vì sao `UserLanguageEntity` lưu riêng tiến trình thay vì gộp vào `UserProfile`?
Vì người dùng học **nhiều ngôn ngữ** song song (mỗi ngôn ngữ một mục tiêu/streak/cấp độ), cần bảng riêng với khoá `languageCode`. `UserProfile` chỉ giữ thông tin chung.

### Q11. Vì sao có cả `StudySessionEntity` lẫn `QuizRecordEntity` mà không gộp?
`study_sessions` ghi phiên **học thẻ** (số thẻ, số thuộc, thời lượng); `quiz_records` ghi kết quả **trò chơi** (điểm, chuỗi, độ chính xác). Hai mục đích khác nhau, gộp lại sẽ nhiều cột rỗng.

### Q12. Vì sao không khai báo `ForeignKey` cứng giữa `flashcards.deckId` và `decks.id`?
Room hỗ trợ FK nhưng đòi khai báo `indices` + xử lý cascade phức tạp. Ở đây liên kết ở mức logic (DAO khi xoá deck sẽ tự xoá thẻ qua `deleteCardsByDeckId`). File SQL tham khảo thì vẫn thể hiện `ON DELETE CASCADE` để đúng chuẩn thiết kế.
