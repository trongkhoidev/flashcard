# DOC_PROJECT_OVERVIEW — Tổng quan toàn bộ dự án NTK FlashCard

> **Mục đích:** Tài liệu tham chiếu gốc mô tả **luồng hoạt động (flow)**, **toàn bộ chức năng** và **kiến trúc dự án** để mọi thành viên có thể giải thích, chỉnh sửa và mở rộng ứng dụng chính xác. Đọc tài liệu này trước khi đọc các tài liệu cá nhân (`DOC_KHOI_UI`, `DOC_NAM_DATA`, `DOC_TUAN_LOGIC`).

---

## Mục lục

1. [Tổng quan sản phẩm](#1-tổng-quan-sản-phẩm)
2. [Công nghệ & thư viện](#2-công-nghệ--thư-viện)
3. [Kiến trúc hệ thống](#3-kiến-trúc-hệ-thống)
4. [Cấu trúc thư mục chi tiết](#4-cấu-trúc-thư-mục-chi-tiết)
5. [Mô hình dữ liệu (8 bảng)](#5-mô-hình-dữ-liệu-8-bảng)
6. [Cơ sở dữ liệu & DAO](#6-cơ-sở-dữ-liệu--dao)
7. [Điều hướng (Navigation Flow)](#7-điều-hướng-navigation-flow)
8. [Luồng dữ liệu end-to-end](#8-luồng-dữ-liệu-end-to-end)
9. [Mô tả toàn bộ chức năng](#9-mô-tả-toàn-bộ-chức-năng)
10. [Thuật toán SRS (SuperMemo-2)](#10-thuật-toán-srs-supermemo-2)
11. [Hệ thống thông báo thông minh](#11-hệ-thống-thông-báo-thông-minh)
12. [Widget từ vựng](#12-widget-từ-vựng)
13. [Quản lý trạng thái](#13-quản-lý-trạng-thái)
14. [Phát âm Text-To-Speech](#14-phát-âm-text-to-speech)
15. [Kiểm thử](#15-kiểm-thử)
16. [Cẩm nang thay đổi tính năng](#16-cẩm-nang-thay-đổi-tính-năng)
17. [Bảng thuật ngữ](#17-bảng-thuật-ngữ)

---

## 1. Tổng quan sản phẩm

- **Tên:** NTK FlashCard
- **Package:** `com.example` · **Application ID:** `com.aistudio.ntkflashcard.xkqwlp`
- **minSdk 24 · targetSdk/compileSdk 36**
- **Ngôn ngữ hỗ trợ:** 10 (en, ko, ja, vi, zh, fr, es, de, it, pt)
- **Hoạt động offline** cho học & phát âm; thông báo/hẹn giờ chạy bằng `AlarmManager`.

> ⚠️ **Lưu ý quan trọng về dữ liệu:** từ vựng mẫu được **nhúng sẵn trong mã nguồn** (`DefaultVocabData.kt`), **không** nạp từ JSON. File `database/databaseflashcard.sql` chỉ là lược đồ SQL **tham khảo/trình bày** (có vài khác biệt nhỏ so với entity Room thật).

---

## 2. Công nghệ & thư viện

| Thành phần | Công nghệ | Phiên bản |
| --- | --- | --- |
| Ngôn ngữ | Kotlin | 2.2.10 |
| UI | Jetpack Compose (BOM) / Material 3 / Icons | 2024.09.00 |
| Lưu trữ | Room (runtime/ktx/compiler) | 2.7.0 |
| Bất đồng bộ | Kotlin Coroutines + Flow | 1.10.2 |
| Lifecycle | ViewModel Compose / Runtime Compose | 2.8.7 |
| Activity | Activity Compose | 1.10.1 |
| Hình ảnh | Coil | 2.7.0 |
| Thông báo | NotificationCompat, AlarmManager, AppWidget | SDK |
| Test | JUnit4, Robolectric, Roborazzi, Espresso | — |
| Dự phòng | Firebase BOM (App Check, Firebase AI), Moshi, Retrofit, OkHttp | 34.17.0 / 1.15.2 / 2.12.0 |

> Firebase, Moshi, Retrofit, OkHttp hiện **chỉ khai báo dependency, chưa có logic sử dụng** trong mã nguồn (dự phòng cho tính năng mạng/đăng nhập Google sau này). `navigation-compose`, `camera`, `datastore`, `firestore`, `firebase-auth` vẫn bị comment.

---

## 3. Kiến trúc hệ thống

**MVVM + Repository**, luồng dữ liệu một chiều (Unidirectional Data Flow):

```
                        ┌───────────────────────────────────────┐
                        │              VIEW (Compose)           │
                        │  collectAsStateWithLifecycle()        │
                        └───────────────┬───────────────────────┘
                                        │ (1) sự kiện → gọi VM
                                        │ (4) StateFlow → recompose
                        ┌───────────────▼───────────────────────┐
                        │         MainViewModel                 │
                        │  StateFlow · ScreenState · TTS        │
                        │  SmartNotificationEngine · Widget     │
                        └───────────────┬───────────────────────┘
                                        │ (2) suspend / Flow
                        ┌───────────────▼───────────────────────┐
                        │      FlashCardRepository (8 DAO)      │
                        └───────────────┬───────────────────────┘
                                        │ (3)
                        ┌───────────────▼───────────────────────┐
                        │  Room DB — 8 bảng (AppDatabase, 8 DAO)│
                        └───────────────────────────────────────┘
```

**Nguyên tắc:** UI không chạm DAO/DB; ViewModel giữ trạng thái; Repository là Single Source of Truth; mọi ghi chạy trên `Dispatchers.IO`.

---

## 4. Cấu trúc thư mục chi tiết

```
app/src/main/java/com/example/
├── MainActivity.kt                 # Entry + điều hướng + banner thông báo in-app + dialog toàn cục
├── audio/TTSManager.kt             # TextToSpeech wrapper (map Locale, speak, shutdown)
├── data/
│   ├── model/
│   │   ├── FlashCard.kt            # DeckEntity, FlashCardEntity (có SRS), DeckWithStats
│   │   ├── Language.kt             # enum AppLanguage (10 ngôn ngữ) + fromCode()
│   │   ├── QuizRecordEntity.kt     # bảng quiz_records
│   │   ├── StudySchedule.kt        # data class cấu hình lịch (không phải bảng)
│   │   ├── StudyScheduleEntity.kt  # bảng study_schedules
│   │   ├── StudySessionEntity.kt   # bảng study_sessions
│   │   ├── UserAccountEntity.kt    # bảng user_accounts
│   │   ├── UserLanguageEntity.kt   # bảng user_languages
│   │   └── UserProfileEntity.kt    # bảng user_profile
│   ├── local/
│   │   ├── AppDatabase.kt          # @Database version 4, 8 entity, Singleton, Callback seed
│   │   ├── DeckDao.kt / FlashCardDao.kt / QuizRecordDao.kt / StudyScheduleDao.kt
│   │   ├── StudySessionDao.kt / UserAccountDao.kt / UserLanguageDao.kt / UserProfileDao.kt
│   │   └── DefaultVocabData.kt     # dữ liệu mẫu
│   └── repository/FlashCardRepository.kt
├── notification/
│   ├── SmartNotificationEngine.kt  # quyết định thông minh + NotificationPreviewEvent
│   ├── NotificationHelper.kt       # channel, custom notification, snooze (SharedPreferences)
│   ├── StudyAlarmScheduler.kt      # AlarmManager hẹn giờ
│   ├── StudyAlarmReceiver.kt       # BroadcastReceiver khi tới giờ
│   └── NotificationActionReceiver.kt # xử lý nút "Để sau"
├── widget/VocabularyStreakWidgetProvider.kt
└── ui/
    ├── viewmodel/MainViewModel.kt  # ScreenState + StateFlow + nghiệp vụ
    ├── theme/  (Color, Type, Theme)
    ├── components/ (VipAvatarFrame, Flashcard3DView, OwlMascotView, ...)
    ├── welcome/ (Welcome, Login, Register, OnboardingSteps)
    ├── home/ (HomeScreen, HomeComponents)
    ├── detail/ (DeckDetailScreen)
    ├── study/ quiz/ match/
    ├── leaderboard/ (LeaderboardTab)
    └── dialogs/ (HomeDialogs, UserProfileDialog, CreateDeckAndCardDialogs)
```

---

## 5. Mô hình dữ liệu (8 bảng)

| Bảng | Entity | Khoá chính | Ý nghĩa |
| --- | --- | --- | --- |
| `decks` | `DeckEntity` | `id: String` | Bộ thẻ |
| `flashcards` | `FlashCardEntity` | `id: Long` auto | Thẻ từ vựng + thông số SRS |
| `study_sessions` | `StudySessionEntity` | `id: Long` auto | Nhật ký phiên học |
| `quiz_records` | `QuizRecordEntity` | `id: Long` auto | Kết quả quiz/match |
| `user_profile` | `UserProfileEntity` | `id: Int = 1` | Hồ sơ, VIP, streak, điểm |
| `user_accounts` | `UserAccountEntity` | `id: Long` auto (unique username) | Tài khoản đăng ký/đăng nhập |
| `study_schedules` | `StudyScheduleEntity` | `id: Int = 1` | Cài đặt nhắc nhở |
| `user_languages` | `UserLanguageEntity` | `languageCode: String` | Tiến trình đa ngôn ngữ |

### 5.1 `DeckEntity`

`id, languageCode, title, subtitle, iconEmoji, level, colorHex, cardCount(=0), isCustom(=false)`.

### 5.2 `FlashCardEntity` (đã thêm thông số SRS)

`id, deckId, languageCode, frontWord, phonetic, partOfSpeech, frontExample, backMeaning, backExampleTranslation, memoryTip, difficulty(0-3), isStarred, isMastered, reviewCount, lastReviewedTimestamp` **+** `srsInterval(=1), srsEaseFactor(=2.5f), srsRepetitions(=0), nextReviewTimestamp(=0L)`.

### 5.3 Các entity còn lại

- **`StudySessionEntity`:** `deckId, deckTitle, languageCode, cardsStudied, masteredCount, durationSeconds, timestamp`.
- **`QuizRecordEntity`:** `deckId, deckTitle, mode("QUIZ"/"MATCH"), score, totalQuestions, pointsEarned, maxStreak, accuracyPercent, timeSpentSeconds, timestamp`.
- **`UserProfileEntity`:** `userName, avatarEmoji, avatarBgColorHex, vipLevel(1), streakDays(7), maxStreakDays, totalPoints(1500), totalCardsLearned, lastActiveTimestamp`.
- **`UserAccountEntity`:** `username(unique), passwordHash, createdAt, lastLoginAt, isLoggedIn`.
- **`StudyScheduleEntity`:** `isEnabled, reminderHour(19), reminderMinute, remindStreak, remindDueWords, minWordsThreshold, targetLanguageCode("ja"), updatedTimestamp`.
- **`UserLanguageEntity`:** `languageCode, displayName, flagEmoji, isCurrentActive, dailyGoalCards(20), masteredCardsCount, totalWordsEnrolled(50), streakDays, level, enrolledTimestamp, lastStudiedTimestamp`.

> Ngoài ra có 2 class **không phải bảng**: `DeckWithStats` (tính `progressPercent`) và `StudySchedule` (data class cấu hình lịch dùng cho AlarmManager/notification).

---

## 6. Cơ sở dữ liệu & DAO

- **Tên DB:** `ntk_flashcard_db` · **`@Database(version = 4, exportSchema = false)`** · **`.fallbackToDestructiveMigration()`** (xoá & tạo lại khi đổi version — chấp nhận mất dữ liệu khi nâng cấp).
- **Singleton** (`@Volatile` + `synchronized`).
- **Seed:** `RoomDatabase.Callback.onCreate()` → `populateInitialData(deckDao, flashCardDao, userProfileDao, userLanguageDao)`; đồng thời `Repository.checkAndSeedDatabase()` seed lại nếu `COUNT(*) == 0` (cơ chế kép).
- 8 DAO được khai báo trong `AppDatabase`: `deckDao()`, `flashCardDao()`, `studySessionDao()`, `quizRecordDao()`, `userProfileDao()`, `userAccountDao()`, `studyScheduleDao()`, `userLanguageDao()`.

### Nhóm truy vấn tiêu biểu

- **DeckDao:** `getDecksByLanguage`, `getAllDecks`, `getDeckById(Flow)`, `getCustomDecks`, `getDecksByLevel`, `searchDecks`, `getTotalDecksCount`, `insertDeck(s)`, `updateDeck`, `updateCardCount`, `deleteDeck(ById)`.
- **FlashCardDao:** đọc theo deck/ngôn ngữ/trạng thái; **SRS due** (`getDueCardsForLanguage`, `getAllDueCards`, `getDueCardsForDeck`), **ngẫu nhiên** (`getRandomCardsForDeck/Language/Starred`), tìm kiếm `searchCards`, thống kê `getMasteredCount`, `getDueCount...`, `getCardsStudiedTodayCount`; ghi `insert/update/delete`; cập nhật SRS `recordReview` và `updateSrsReview`, `resetDeckProgress`.
- **QuizRecordDao / StudySessionDao:** lấy theo mode/deck/thời gian, đếm tổng, `insert/delete/clear`.
- **UserProfileDao:** `getUserProfile`, `updateName`, `updateVipLevel`, `updateAvatar`, `updateStreak` (giữ `maxStreakDays`), `addPoints`, `incrementCardsLearned`.
- **UserAccountDao:** `getUserByUsername`, `authenticate(username, passwordHash)`, `isUsernameExists`, `registerUser`, `setLoggedIn`, `logoutAllUsers`, `updatePassword`, `deleteAccount`.
- **UserLanguageDao:** `getAllLearningLanguages`, `getActiveLearningLanguage`, `switchActiveLanguage` (`@Transaction`: clear flag + mark active), `updateDailyGoal`, `incrementMasteredCount`, `updateLanguageStreak/Level`, `deleteLanguage`.
- **StudyScheduleDao:** `getSchedule`, `saveSchedule`, `setReminderEnabled`, `updateReminderTime`, `updateTargetLanguage`.

### Repository (`FlashCardRepository`)

Constructor nhận 6 DAO (deck, card, session, quiz, profile, language) hoặc `AppDatabase`. Phân nhóm: Deck / Flashcard+SRS / Multi-Language / Study Sessions / Quiz Records / Profile. Các hàm ghi bọc `withContext(Dispatchers.IO)`.

---

## 7. Điều hướng (Navigation Flow)

Điều hướng bằng **sealed class `ScreenState`** lưu trong `StateFlow`, `MainActivity` dùng `Crossfade`:

```kotlin
sealed class ScreenState {
    object Welcome, Login, Register, Onboarding, Home
    data class DeckDetail(deck, cards)
    data class Study(deck, cards)
    data class Quiz(deck, cards)
    data class Match(deck, cards)
    data class Starred(cards)
}
```

```
Welcome ──onStartLearning──► Onboarding ──onComplete(lang, hour)──► Home
   │                            (7 bước)
   ├──onLoginClick──► Login ──onLoginSuccess──► Home
   │                   └──onNavigateToRegister──► Register ──onRegisterSuccess──► Home
   └──onSelectLanguage──► Onboarding (chọn bubble ngôn ngữ)

Home ──► DeckDetail ──► Study / Quiz / Match
Home ──► onStudyDeck/onQuizDeck/onMatchDeck ──► Study/Quiz/Match
Home ──► onOpenStarred ──► Starred (dùng lại FlashcardStudyScreen)

BackHandler: Login/Register/Onboarding/Home → Welcome ; các màn còn lại → Home.
```

**Các hàm điều hướng trong ViewModel** (`navigateTo`, `openDeckDetail`, `startStudyDeck`, `startQuizDeck`, `startMatchDeck`, `openStarredCards`, `startStudyByLanguage`, `startStudySavedCards`, `startQuizSavedCards`, `startMatchSavedCards`). Mỗi hàm `start*` dùng `.first()` để lấy danh sách thẻ rồi gán `_currentScreen`.

---

## 8. Luồng dữ liệu end-to-end

### Ví dụ 1 — Học một bộ thẻ
1. Bấm "Học ngay" → `onStudyDeck(deck)` → `viewModel.startStudyDeck(deck)`.
2. ViewModel `repository.getCardsForDeck(deck.id).first()` → `_currentScreen = ScreenState.Study(deck, cards)`.
3. `MainActivity` quan sát `currentScreen` → `Crossfade` hiển thị `FlashcardStudyScreen`.
4. Bấm "Đã thuộc" → `onRecordReview(id, 1)` → `viewModel.recordReview` → `repository.recordCardReview` → DAO `recordReview` (`isMastered = (difficulty==1)`), sau đó cập nhật widget.
5. Hoàn thành → `onSessionFinished(count, mastered)` → `viewModel.completeStudySession` ghi `study_sessions`.

### Ví dụ 2 — Đổi ngôn ngữ
`selectLanguage(lang)` → cập nhật `_selectedLanguage` → các `flatMapLatest` (`decksForCurrentLanguage`, `dueCardsForCurrentLanguage`, `starterCardsForCurrentLanguage`, `masteredCountForCurrentLanguage`) tự query lại → đồng thời `repository.switchActiveLanguage(code)`.

### Ví dụ 3 — Thông báo hẹn giờ
`StudyAlarmScheduler` đặt `AlarmManager` → tới giờ `StudyAlarmReceiver.onReceive` (dùng `goAsync`) → `SmartNotificationEngine.evaluateAndSendSmartNotification` → `NotificationHelper.showStudyReminderNotification` → bấm vào mở `MainActivity` (extra `EXTRA_NAV_TARGET=HOME_STUDY`).

---

## 9. Mô tả toàn bộ chức năng

### 9.1 Welcome → Onboarding (lần đầu)
- **WelcomeScreen:** logo, linh vật cú + bubble ngôn ngữ, carousel 3 trang, nút "Bắt đầu học ngay"/"Đăng nhập".
- **OnboardingStepsScreen (7 bước):** (1) chọn ngôn ngữ, (2) cấp độ, (3) chủ đề yêu thích, (4) khung giờ học, (5) xin quyền thông báo, (6) thêm widget, (7) mascot "đang chuẩn bị thẻ" → `onCompleteOnboarding(lang, reminderHour)`.
- **Login/Register:** xác thực form cục bộ (Register kiểm tra username 4-20 ký tự, mật khẩu ≥8 + độ mạnh, khớp xác nhận); Login dùng fallback `"Học viên NTK"` (chưa nối DB thật).

### 9.2 HomeScreen (4 tab dưới + overlay Ôn tập)
- **Tab 0 Trang chủ:** header + streak pill, search, banner streak (hoặc hero "bắt đầu" cho user mới), quick actions (Tạo bộ thẻ / Ôn tập / Thống kê / Đã lưu), "Tiếp tục học", widget SRS due, "Bộ thẻ của bạn", "Mục tiêu hôm nay".
- **Tab 1 Khám phá:** chip ngôn ngữ + danh sách bộ thẻ.
- **Tab 2 BXH:** `LeaderboardTab` (bảng xếp hạng tĩnh, đổi theo Tổng điểm/Chuỗi ngày/Số thẻ, top-3 podium, rank 4-10, thưởng).
- **Tab 3 Tài khoản:** hồ sơ + `VipAvatarFrame`, tổng kết, card cài đặt thông báo (2 nút test notification), banner widget.
- **Overlay Ôn tập** (`showReviewOverlay`): từ đã lưu + luyện nhanh.

### 9.3 DeckDetailScreen (2 tab)
Hero (ảnh bìa, tag ngôn ngữ, rating 4.9, số thẻ, avatar), nút "Học ngay", 2 tab: **Nội dung** (chủ đề mở rộng + sample + chế độ luyện tập) & **Thống kê** (tỉ lệ thành thạo, đã thuộc/đang học).

### 9.4 FlashcardStudyScreen (học thẻ + SRS)
Thẻ 3D (khung VIP), nút phát âm/sao, 3 nút hành động (Trộn thẻ / Lưu từ điển / đổi mặt), 2 nút đánh giá **"Chưa thuộc"(3)** & **"Đã thuộc"(1)**, auto-play, prev/next, overlay hoàn thành → quiz, `onSessionFinished`.

### 9.5 QuizScreen (trắc nghiệm có điểm & chuỗi)
- 4 đáp án (1 đúng + 3 nhiễu từ `backMeaning` các thẻ khác).
- **Điểm & streak multiplier:** đúng → `score++`, `currentStreak++`, `totalPoints += 100 * multiplier` (multiplier 1.0→5.0 theo chuỗi, `getStreakMultiplierInfo`); sai → reset streak.
- Popup điểm nổi, pháo hoa `FireworksCanvas`, overlay kết quả (điểm, độ chính xác, chuỗi dài nhất, xếp hạng).
- `onFinishQuiz(score, total)` + `onStudyNext`.

### 9.6 WordMatchScreen (ghép từ)
Lấy 6 thẻ đầu, tạo cặp từ↔nghĩa (xáo trộn), lưới 2 cột, đếm lượt thử, thắng khi ghép đủ.

### 9.7 Starred mode & từ đã lưu
`openStarredCards()` → `ScreenState.Starred`; `SavedCardsDialog` có lọc theo ngôn ngữ/chủ đề/tìm kiếm, phát âm, bỏ sao. ViewModel có `startStudySavedCards`/`startQuizSavedCards`/`startMatchSavedCards` tạo deck tạm.

### 9.8 VIP (8 cấp)
`VipAvatarFrame` (khung avatar động), `VipCardFrame` (viền thẻ động), `VipLevelSelectorCard` (chọn cấp). `VipLevel` enum 0-7 với gradient/badge/animation riêng.

---

## 10. Thuật toán SRS (SuperMemo-2)

`FlashCardRepository.recordSrsReview(card, rating, isCorrect)`:

- **Đúng & rating ≥ 3:** interval theo số lần đúng liên tiếp (`repetitions` 0→1, 1→3, 2→6, else `interval * easeFactor`); cập nhật ease factor theo công thức SM-2 `EF' = EF + (0.1 - (5-q)*(0.08 + (5-q)*0.02))`, tối thiểu 1.3; `isMastered = repetitions >= 3`.
- **Sai:** reset repetitions=0, interval=1, ôn lại sau 15 phút, không mastered.
- `difficulty` quy đổi: rating 5→1, 4/3→2, còn lại→3.
- Ghi qua `cardDao.updateSrsReview(...)`; nếu mastered thì `languageDao.incrementMasteredCount`.

> **Lưu ý:** `recordSrsReview` (SM-2 đầy đủ) hiện **chưa được gọi từ UI**. Nút bấm trong `FlashcardStudyScreen` gọi `recordReview` (đơn giản hơn) qua `onRecordReview → viewModel.recordReview → repository.recordCardReview`. Nếu cần kích hoạt SRS thật, đổi `recordReview` sang `recordSrsReview` trong ViewModel.

---

## 11. Hệ thống thông báo thông minh

| File | Vai trò |
| --- | --- |
| `SmartNotificationEngine` | Quyết định: bỏ qua nếu `isSnoozedToday` hoặc đã học hôm nay (trừ test); đếm từ due + streak; sinh title/message theo 4 kịch bản; gửi notification + preview. |
| `NotificationHelper` | Tạo 2 channel (`channel_study_reminder`, `channel_achievements`), custom notification (large icon mascot, action "Học ngay"/"Để sau"), snooze qua `SharedPreferences` ("ntk_flashcard_prefs"), `clearAllNotifications`. |
| `StudyAlarmScheduler` | `AlarmManager.setExactAndAllowWhileIdle` hẹn giờ (code 8888), tự chuyển sang ngày mai nếu đã qua giờ; `cancelStudyAlarm`. |
| `StudyAlarmReceiver` | `BroadcastReceiver` (dùng `goAsync()`), gọi engine + lên lịch lại. |
| `NotificationActionReceiver` | Xử lý `ACTION_SNOOZE_TODAY`: tắt notification + đánh dấu snooze + Toast. |

`NotificationPreviewEvent(title, message, isAchievement, formattedTime)` được ViewModel giữ trong `notificationPreview` để hiển thị **banner in-app** (tự ẩn sau 6 giây). `MainActivity.onResume` tự xoá notification hệ thống.

---

## 12. Widget từ vựng

`VocabularyStreakWidgetProvider` (AppWidgetProvider) + layout `widget_vocabulary_streak.xml` + `xml/vocabulary_streak_widget_info.xml`:
- Hiển thị streak (`app_widget_prefs`) + một từ chưa thuộc ngẫu nhiên (đọc từ Room).
- Nút refresh (`ACTION_REFRESH_WORD`) và tự làm mới mỗi **8 giây** (`startAutoScroll`).
- Bấm vào widget mở `MainActivity`.
- Được cập nhật từ ViewModel sau mỗi lần review (`VocabularyStreakWidgetProvider.updateAllWidgets`).

---

## 13. Quản lý trạng thái

`MainViewModel` (AndroidViewModel) giữ toàn bộ `StateFlow`:

| StateFlow | Khởi tạo | Ý nghĩa |
| --- | --- | --- |
| `currentScreen` | `Welcome` | Màn hình hiện tại |
| `selectedLanguage` | `ENGLISH` | Ngôn ngữ đang chọn |
| `learningLanguages` | `[ENGLISH]` | Danh sách ngôn ngữ đang học (in-memory) |
| `learningLanguagesFromDb` | `getAllLearningLanguages()` | Từ bảng `user_languages` |
| `dueCardsForCurrentLanguage` / `dueCountForCurrentLanguage` | `flatMapLatest` theo ngôn ngữ | Thẻ đến hạn ôn |
| `starterCardsForCurrentLanguage` | `flatMapLatest` | Từ mới (reviewCount=0) |
| `masteredCountForCurrentLanguage` | `flatMapLatest` | Từ đã thuộc theo ngôn ngữ |
| `userName` / `userVipLevel` / `streakDays` | MutableStateFlow | `"Bạn Học"` / `1` / `7` |
| `notificationPreview` | `null` | Banner in-app |
| `decksForCurrentLanguage`, `allDecks`, `starredCardsList`, `allCardsList`, `masteredCount`, `totalCardsCount` | `stateIn` | Dữ liệu chính |

`init{}` chạy `checkAndSeedDatabase()` + cập nhật widget + `StudyAlarmScheduler.scheduleStudyAlarm`. `onCleared()` gọi `ttsManager.shutdown()`.

---

## 14. Phát âm Text-To-Speech

`TTSManager` khởi tạo TTS, map `languageTag` → `Locale` (ko/ja/zh/fr/de/es/vi, mặc định US), gọi `tts.speak(..., QUEUE_FLUSH, ..., "NTK_SPEECH_...")`. **Hoạt động offline.** Lưu ý: enum có thêm `it`/`pt` nhưng `TTSManager` chưa map 2 ngôn ngữ này (rơi vào `else → Locale.US`) — điểm cần bổ sung nếu giám khảo hỏi.

---

## 15. Kiểm thử

- Unit/Robolectric/Roborazzi (screenshot), coroutines-test; Instrumentation Espresso + Compose UI test.
- Nhiều composable có `testTag` (ví dụ `btn_memorized`, `btn_not_memorized`, `quiz_option_...`, `streak_badge`, `tab_leaderboard`, `onboarding_next_button`, `saved_cards_search_input`, ...) để test UI.

---

## 16. Cẩm nang thay đổi tính năng

| Yêu cầu giám khảo | Nơi sửa |
| --- | --- |
| Thêm/bớt ngôn ngữ | `data/model/Language.kt` (enum) + màu bubble `ui/theme/Color.kt` + `TTSManager.kt` (map Locale) + seed `DefaultVocabData.kt`/`AppDatabase` |
| Đổi màu thương hiệu | `ui/theme/Color.kt` (NTKPrimary...) |
| Thêm bảng / cột | entity trong `data/model/` + `AppDatabase` (entities, version) + DAO tương ứng |
| Thêm truy vấn | DAO tương ứng trong `data/local/` |
| Thêm màn hình | composable mới + case `ScreenState` + `when(screen)` trong `MainActivity` + hàm điều hướng trong `MainViewModel` |
| Kích hoạt SRS SM-2 thật | `MainViewModel.recordReview` → dùng `recordSrsReview(card, rating, isCorrect)` |
| Sửa logic quiz (điểm/nhiễu) | `ui/quiz/QuizScreen.kt` |
| Sửa logic ghép từ | `ui/match/WordMatchScreen.kt` |
| Sửa thông báo | `notification/*` (engine/helper/scheduler/receiver) |
| Sửa widget | `widget/VocabularyStreakWidgetProvider.kt` + `res/layout/widget_*` |
| Nối đăng nhập với DB thật | `UserAccountDao` đã sẵn; nối `LoginScreen`/`RegisterScreen` với ViewModel + Repository (hash mật khẩu) |
| Thêm tính năng mạng | bỏ comment Retrofit/Moshi + tạo service, gọi qua Repository |

---

## 17. Bảng thuật ngữ

| Thuật ngữ | Giải thích |
| --- | --- |
| SRS / SM-2 | Spaced Repetition / thuật toán SuperMemo-2 (interval, ease factor) |
| StateFlow / flatMapLatest / stateIn | Quản lý trạng thái phản ứng |
| DAO / Repository | Data Access Object / lớp trung gian |
| Distractor | Đáp án nhiễu trong trắc nghiệm |
| Streak | Chuỗi ngày học liên tiếp |
| Streak multiplier | Hệ số nhân điểm theo chuỗi trả lời đúng |
| Due cards | Thẻ đến hạn ôn tập (`nextReviewTimestamp <= now`) |
| VIP level | 8 cấp độ khung hiệu ứng (0-7) |
| AppWidget / RemoteViews | Widget màn hình chính |
| AlarmManager | Lập lịch hẹn giờ hệ thống |
| PendingIntent | Intent đại diện chạy sau (cho notification/widget/alarm) |
