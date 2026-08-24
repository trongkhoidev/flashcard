# DOC_PROJECT_OVERVIEW — Tổng quan dự án NTK FlashCard

> **Mục đích:** Tài liệu tổng quan toàn diện nhất về kiến trúc, luồng dữ liệu, mô hình CSDL, thuật toán SRS, hệ thống thông báo, widget và guide thay đổi — phục vụ giám khảo và thành viên mới.

---

## 1. Tổng quan ứng dụng

**NTK FlashCard** — Ứng dụng học từ vựng đa ngôn ngữ trên Android, xây dựng bằng Kotlin + Jetpack Compose, kiến trúc MVVM + Repository.

- **Package:** `com.example`, applicationId: `com.aistudio.ntkflashcard.xkqwlp`
- **Min SDK:** 26 (Android 8.0), **Target SDK:** 35
- **Ngôn ngữ hỗ trợ:** 10 (EN, KO, JA, ZH, FR, ES, DE, IT, PT, VI)
- **Database:** Room SQLite, version 4, 8 entities, 8 DAOs, `fallbackToDestructiveMigration()`
- **Dữ liệu seed:** Hardcoded trong `DefaultVocabData.kt` (12 deck, hàng trăm thẻ) + `StarterVocabData.kt` (50 thẻ starter)

---

## 2. Kiến trúc tổng thể

```
┌─────────────────────────────────────────────────────┐
│                    UI Layer                          │
│  MainActivity (754 dòng)                            │
│  ├── NTKFlashCardApp (Crossfade 12 screens)         │
│  ├── Banner thông báo in-app (AnimatedVisibility)    │
│  └── 3 Dialog (Profile, CreateDeck, CreateCard)     │
│                                                     │
│  ui/                                                │
│  ├── theme/ (Color, Type, Theme)                    │
│  ├── components/ (VipAvatarFrame, Flashcard3DView,  │
│  │   OwlMascot, LanguageSpeechBubble,               │
│  │   GlowingCardsHeader, LaurelWreathHeader)        │
│  ├── welcome/ (Welcome, Login, Register, Onboarding)│
│  ├── home/ (HomeScreen 1867, HomeComponents 2037,   │
│  │   StreakWeeklyTracker 291)                       │
│  ├── detail/ (DeckDetailScreen)                     │
│  ├── study/ (FlashcardStudyScreen 711)              │
│  ├── quiz/ (QuizScreen 1197)                        │
│  ├── match/ (WordMatchScreen 314)                   │
│  ├── leaderboard/ (LeaderboardTab)                  │
│  └── dialogs/ (HomeDialogs, UserProfileDialog,      │
│      CreateDeckAndCardDialogs)                      │
└────────────────────┬────────────────────────────────┘
                     │ Lambda callbacks
┌────────────────────▼────────────────────────────────┐
│               ViewModel Layer                        │
│  MainViewModel (AndroidViewModel, 37 hàm, 18 StateFlow)│
│  ├── ScreenState sealed class (12 states)            │
│  ├── Language management (flatMapLatest)              │
│  ├── Study/Quiz/Match navigation                     │
│  ├── Onboarding trial flow                           │
│  └── Notification orchestration                      │
└────────────────────┬────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────┐
│               Data Layer                             │
│  FlashCardRepository (340 dòng, 60+ hàm)            │
│  ├── Deck operations (12)                            │
│  ├── Flashcard & SRS (30+)                           │
│  ├── Multi-language learning (9)                     │
│  ├── Study sessions (7)                              │
│  ├── Quiz records (9)                                │
│  └── User profile & streak (9)                       │
│                                                     │
│  AppDatabase (version=4, 8 entities, 8 DAOs)         │
│  ├── DefaultVocabData (12 deck seed)                 │
│  └── StarterVocabData (50 thẻ starter onboarding)   │
└────────────────────┬────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────┐
│            Notification Layer                        │
│  SmartNotificationEngine (context-aware decisions)   │
│  NotificationHelper (channels, snooze, rich notif)   │
│  StudyAlarmScheduler (AlarmManager exact)            │
│  StudyAlarmReceiver (goAsync coroutine)              │
│  NotificationActionReceiver (snooze action)          │
│                                                     │
│  Widget                                              │
│  VocabularyStreakWidgetProvider (auto-refresh 8s)    │
└─────────────────────────────────────────────────────┘
```

---

## 3. Mô hình CSDL (8 bảng)

| Bảng | Khoá chính | Mô tả |
|------|-----------|-------|
| `decks` | `id: String` | Bộ thẻ (EN/KO/JA/...), 12 deck seed + custom |
| `flashcards` | `id: Long (auto)` | Từ vựng, SRS fields, mastered/starred flags |
| `study_sessions` | `id: Long (auto)` | Phiên học (deckId, cardsStudied, masteredCount, duration) |
| `quiz_records` | `id: Long (auto)` | Kết quả quiz (score, points, maxStreak, accuracy) |
| `user_profile` | `id=1` | Profile user (name, vip, streak, points, cardsLearned) |
| `user_accounts` | `id: Long (auto)` | Tài khoản (username, passwordHash, isLoggedIn) |
| `study_schedules` | `id=1` | Lịch nhắc (hour, minute, enabled) |
| `user_languages` | `languageCode: String PK` | Ngôn ngữ đang học (dailyGoal, masteredCount, level) |

**Liên kết logic:** `flashcards.deckId` → `decks.id`; `study_sessions.deckId` → `decks.id`. Room không khai báo FK cứng — cascade ở mức logic.

---

## 4. Thuật toán SRS (SuperMemo-2)

```
Nếu ĐÚNG và rating ≥ 3:
  interval: repetitions 0→1, 1→3, 2→6, ≥3 → interval × easeFactor
  EF' = EF + (0.1 - (5-q) × (0.08 + (5-q) × 0.02)), q=rating, min 1.3
  isMastered = repetitions ≥ 3
  nextReview = now + interval × 24h

Nếu SAI:
  repetitions=0, interval=1
  nextReview = now + 15 phút
  isMastered = false
```

**Lưu ý:** `recordSrsReview` đã implement trong Repository nhưng **chưa nối UI**. Study screen hiện dùng `recordCardReview` (đơn giản: difficulty=1 → mastered, else → unmastered).

---

## 5. Onboarding Trial Flow

```
WelcomeScreen
  → [Bắt đầu học ngay]
  → OnboardingStepsScreen (7 bước: ngôn ngữ → cấp độ → chủ đề → giờ → quyền → widget → loading)
  → startOnboardingTrial(lang, hour)
  → OnboardingTrialStudy (FlashcardStudyScreen, isOnboardingTrial=true, allowBack=false)
    → 5 starter cards từ StarterVocabData (5 từ/ngôn ngữ)
    → [Hoàn thành học] → startOnboardingTrialQuiz
  → OnboardingTrialQuiz (QuizScreen, isOnboardingTrial=true)
    → [Hoàn thành quiz] → finishOnboardingTrialAndGoToAuth
  → RegisterScreen
  → [Đăng ký] → completeTrialRegistration(username)
    → Insert starter cards vào DB, set streak=1
  → HomeScreen
```

---

## 6. Hệ thống thông báo

### Chuỗi thông báo
```
StudyAlarmScheduler (AlarmManager.setExactAndAllowWhileIdle)
  → StudyAlarmReceiver.onReceive (goAsync)
  → SmartNotificationEngine.evaluateAndSendSmartNotification
    → Kiểm tra snooze/đã học/dueWords/streak
    → Sinh message 4 kịch bản
  → NotificationHelper.showStudyReminderNotification
    → PendingIntent study (mở app)
    → PendingIntent snooze (broadcast)
    → 2 action buttons
```

### Snooze mechanism
```
Bấm "Để sau" → NotificationActionReceiver.onReceive
  → clearAllNotifications
  → setSnoozedToday (SharedPreferences)
  → isSnoozedToday → true → skip notification hôm nay
```

### Banner in-app (MainActivity)
```
notificationPreview != null → AnimatedVisibility overlay
  → Rich card (mascot, streak tracker, expand/collapse)
  → Auto-dismiss 6s (LaunchedEffect)
  → Phân biệt achievement (dark) vs study reminder (light)
```

---

## 7. Widget

`VocabularyStreakWidgetProvider`:
- `updateAllWidgets(context, streakDays?)` — lưu streak vào prefs + broadcast.
- `updateAppWidget(context, manager, widgetId)` — RemoteViews, đọc streak + từ chưa thuộc ngẫu nhiên.
- Auto-refresh mỗi 8s qua coroutine + broadcast.

---

## 8. Navigation (ScreenState)

12 trạng thái điều hướng, quản lý bởi `MainViewModel.currentScreen` (StateFlow):

```
Welcome → Login / Register / Onboarding
Onboarding → OnboardingTrialStudy → OnboardingTrialQuiz → Register → Home
Home → DeckDetail / Study / Quiz / Match / Starred
DeckDetail → Study / Quiz / Match
Study → Quiz (callback)
```

Back handling (BackHandler):
- Welcome/OnboardingTrialStudy/OnboardingTrialQuiz: không enable.
- Login/Register/Onboarding/Home: về Welcome.
- Còn lại: về Home.

---

## 9. Phân công thành viên

| Thành viên | File chính | Phạm vi |
|-----------|-----------|---------|
| **Khôi** | `ui/*` (14+ files, ~7000 dòng) | UI/UX: theme, components, welcome, home, study, quiz, match, leaderboard, dialogs |
| **Nam** | `data/*` (15+ files, ~2000 dòng) | Data: 8 entities, 8 DAOs, 2 seed data files, repository |
| **Tuấn** | `MainActivity.kt`, `MainViewModel.kt`, `audio/*`, `notification/*`, `widget/*` (9 files, ~1500 dòng) | Logic: navigation, business logic, TTS, notifications, widget |

---

## 10. Bảng tra cứu thay đổi nhanh

| Muốn thay đổi... | File cần sửa |
|-------------------|-------------|
| Màu sắc chủ đạo | `ui/theme/Color.kt` |
| Font chữ | `ui/theme/Type.kt` |
| Theme light/dark | `ui/theme/Theme.kt` |
| Thêm màn hình mới | `ScreenState` sealed class + `MainViewModel` + `MainActivity.when` |
| Sửa luồng Back | `BackHandler` trong `MainActivity` |
| Sửa trang chủ | `ui/home/HomeScreen.kt` + `HomeComponents.kt` |
| Sửa streak tracker | `ui/home/StreakWeeklyTracker.kt` |
| Sửa flashcard view | `ui/components/Flashcard3DView.kt` |
| Sửa VIP frame | `ui/components/VipAvatarFrame.kt` |
| Sửa study screen | `ui/study/FlashcardStudyScreen.kt` |
| Sửa quiz | `ui/quiz/QuizScreen.kt` |
| Sửa ghép từ | `ui/match/WordMatchScreen.kt` |
| Sửa BXH | `ui/leaderboard/LeaderboardTab.kt` |
| Sửa dialog | `ui/dialogs/*` |
| Thêm entity mới | Entity + `@Database.entities` + DAO + Repository |
| Thêm cột | Entity + tăng DB version |
| Thêm hàm DAO/Repository | File tương ứng |
| Thêm ngôn ngữ | `Language.kt` + `Color.kt` + seed data |
| Sửa seed data | `DefaultVocabData.kt` / `StarterVocabData.kt` |
| Kích hoạt SRS SM-2 | `MainViewModel.recordReview` → `recordSrsReview` |
| Sửa giờ nhắc | `MainViewModel.updateStudySchedule` + `StudySchedule` |
| Sửa nội dung thông báo | `SmartNotificationEngine` + `NotificationHelper` |
| Sửa widget | `VocabularyStreakWidgetProvider` + `res/layout/widget_*` |
| Nối đăng nhập DB | `LoginScreen`/`RegisterScreen` + ViewModel + `UserAccountDao` |
| Đổi giọng TTS | `TTSManager.kt` (map Locale) |

---

## 11. Q&A giám khảo

### Q1. Kiến trúc app?
MVVM + Repository. UI (Compose) → ViewModel (StateFlow) → Repository → DAO → Room DB.

### Q2. Tại sao dùng sealed class cho navigation?
Đơn giản, truyền data trực tiếp, không cần library bên ngoài. Phù hợp với app quy mô vừa.

### Q3. Room database migrations?
Hiện dùng `fallbackToDestructiveMigration()` (dev). Production sẽ viết `Migration(oldVersion, newVersion)`.

### Q4. Dữ liệu seed từ đâu?
Hardcoded trong Kotlin (`DefaultVocabData.kt` + `StarterVocabData.kt`), nạp qua `DatabaseCallback.onCreate` + `checkAndSeedDatabase()`.

### Q5. Notification thông minh hoạt động thế nào?
SmartNotificationEngine kiểm tra nhiều yếu tố (snooze, đã học hôm nay, due words, streak) trước khi gửi. AlarmManager hẹn giờ chính xác.

### Q6. Widget cập nhật thế nào?
`updateAllWidgets` lưu streak prefs + broadcast. Provider nhận broadcast → render RemoteViews. Auto-refresh 8s.

### Q7. TTS hỗ trợ ngôn ngữ nào?
10 ngôn ngữ: EN, KO, JA, ZH, FR, ES, DE, IT, PT, VI. Thiếu locale → fallback US.

### Q8. VIP levels hoạt động thế nào?
8 cấp (0-7), mỗi cấp có animation speed/gradient/badge riêng. UI render bởi `VipAvatarFrame` + `VipCardFrame`.

### Q9. Onboarding trial flow?
User thử học 5 từ starter + quiz thử trước khi đăng ký. Dùng `StarterVocabData` (50 thẻ, 5/ngôn ngữ). Sau trial → đăng ký → cards được insert vào DB.

### Q10. Phân biệt study session và quiz record?
Study session: cardsStudied, masteredCount, duration. Quiz record: score, points, maxStreak, accuracy. Tách riêng để analytics chi tiết.
