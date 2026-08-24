# DOC_TUAN — Tài liệu thành viên Tuấn: Business Logic, Điều hướng, Âm thanh, Notification & Widget

> **Vai trò:** Bạn là "bộ não trung tâm" — kết nối UI (Khôi) với Data (Nam), xử lý nghiệp vụ, điều hướng, phát âm, thông báo thông minh và widget. Tài liệu liệt kê **từng file, từng hàm**, kèm giải thích **vì sao** chọn giải pháp này và **triển khai** như thế nào.

---

## Mục lục

1. [Phạm vi & thư mục](#1-phạm-vi--thư-mục)
2. [Công nghệ & vì sao chọn](#2-công-nghệ--vì-sao-chọn)
3. [CHI TIẾT TỪNG FILE & TỪNG HÀM](#3-chi-tiết-từng-file--từng-hàm)
   - 3.1 `ui/viewmodel/MainViewModel.kt`
   - 3.2 `MainActivity.kt`
   - 3.3 `audio/TTSManager.kt`
   - 3.4 `notification/SmartNotificationEngine.kt`
   - 3.5 `notification/NotificationHelper.kt`
   - 3.6 `notification/StudyAlarmScheduler.kt`
   - 3.7 `notification/StudyAlarmReceiver.kt`
   - 3.8 `notification/NotificationActionReceiver.kt`
   - 3.9 `widget/VocabularyStreakWidgetProvider.kt`
4. [Luồng nghiệp vụ tiêu biểu](#4-luồng-nghiệp-vụ-tiêu-biểu)
5. [Phân định rõ: logic nằm ở đâu](#5-phân-định-rõ-logic-nằm-ở-đâu)
6. [Cẩm nang sửa đổi](#6-cẩm-nang-sửa-đổi)
7. [Q&A mở rộng](#7-qa-mở-rộng)

---

## 1. Phạm vi & thư mục

```
MainActivity.kt                 # Entry + điều hướng + banner thông báo in-app + onboarding trial
audio/TTSManager.kt             # Phát âm
ui/viewmodel/MainViewModel.kt   # ScreenState + StateFlow + nghiệp vụ (37 hàm)
notification/                   # SmartNotificationEngine, NotificationHelper,
                                # StudyAlarmScheduler, StudyAlarmReceiver, NotificationActionReceiver
widget/VocabularyStreakWidgetProvider.kt
```

---

## 2. Công nghệ & vì sao chọn

- **`AndroidViewModel`** (cần `Application` context để mở DB/TTS/notification/widget).
- **`StateFlow`** (API coroutine gốc, nhiều toán tử, dễ test) thay vì `LiveData`.
- **`flatMapLatest`** để dữ liệu theo ngôn ngữ tự query lại, huỷ Flow cũ (tránh race condition).
- **`ScreenState` sealed class** (truyền dữ liệu trực tiếp) thay vì Navigation Compose.
- **`AlarmManager.setExactAndAllowWhileIdle`** (hẹn giờ chính xác) thay vì WorkManager (không chính xác).
- **`SharedPreferences`** cho cờ "Để sau" (nhỏ, tạm) thay vì Room.
- **`goAsync()`** trong BroadcastReceiver để chạy coroutine query DB.

---

## 3. CHI TIẾT TỪNG FILE & TỪNG HÀM

### 3.1 `ui/viewmodel/MainViewModel.kt`

**`sealed class ScreenState`** — 12 trạng thái:
| State | Data | Mô tả |
|-------|------|-------|
| `Welcome` | (none) | Màn hình chào |
| `Login` | (none) | Đăng nhập |
| `Register` | (none) | Đăng ký |
| `Onboarding` | (none) | Onboarding 7 bước |
| `Home` | object | Trang chủ |
| `DeckDetail` | `(deck, cards)` | Chi tiết deck |
| `Study` | `(deck, cards)` | Học thẻ |
| `Quiz` | `(deck, cards)` | Trắc nghiệm |
| `Match` | `(deck, cards)` | Ghép từ |
| `Starred` | `(cards)` | Từ đã lưu |
| **`OnboardingTrialStudy`** | `(language, cards)` | **MỚI:** Học trial during onboarding |
| **`OnboardingTrialQuiz`** | `(language, cards)` | **MỚI:** Quiz trial during onboarding |

**`class MainViewModel(application) : AndroidViewModel`**
- Khởi tạo: `database`, `repository`, `ttsManager`, `smartNotificationEngine`.
- `init` block: `repository.checkAndSeedDatabase()`, `VocabularyStreakWidgetProvider.updateAllWidgets(...)`, `StudyAlarmScheduler.scheduleStudyAlarm(...)`.

**StateFlow (18):**
| StateFlow | Type | Nguồn |
|-----------|------|--------|
| `currentScreen` | `StateFlow<ScreenState>` | `_currentScreen` |
| `selectedLanguage` | `StateFlow<AppLanguage>` | `_selectedLanguage` |
| `learningLanguages` | `StateFlow<List<AppLanguage>>` | `_learningLanguages` |
| `learningLanguagesFromDb` | `StateFlow<List<UserLanguageEntity>>` | `repository.getAllLearningLanguages()` |
| `dueCardsForCurrentLanguage` | `StateFlow<List<FlashCardEntity>>` | `_selectedLanguage` → `flatMapLatest` → `repository.getDueCardsForLanguage` |
| `dueCountForCurrentLanguage` | `StateFlow<Int>` | `_selectedLanguage` → `flatMapLatest` → `repository.getDueCountForLanguage` |
| `starterCardsForCurrentLanguage` | `StateFlow<List<FlashCardEntity>>` | `_selectedLanguage` → `flatMapLatest` → `repository.getStarterCardsForLanguage` |
| `masteredCountForCurrentLanguage` | `StateFlow<Int>` | `_selectedLanguage` → `flatMapLatest` → `repository.getMasteredCountByLanguage` |
| `decksForCurrentLanguage` | `StateFlow<List<DeckEntity>>` | `_selectedLanguage` → `flatMapLatest` → `repository.getDecksByLanguage` |
| `allDecks` | `StateFlow<List<DeckEntity>>` | `repository.getAllDecks()` |
| `starredCardsList` | `StateFlow<List<FlashCardEntity>>` | `repository.getStarredCards()` |
| `allCardsList` | `StateFlow<List<FlashCardEntity>>` | `repository.getAllCards()` |
| `masteredCount` | `StateFlow<Int>` | `repository.getMasteredCount()` |
| `totalCardsCount` | `StateFlow<Int>` | `repository.getTotalCardsCount()` |
| `userName` | `StateFlow<String>` | `_userName` (default "Bạn Học") |
| `userVipLevel` | `StateFlow<Int>` | `_userVipLevel` (default 1) |
| `streakDays` | `StateFlow<Int>` | `_streakDays` (default 7) |
| `notificationPreview` | `StateFlow<NotificationPreviewEvent?>` | `_notificationPreview` |

**Tất cả 37 hàm:**

| # | Hàm | Chữ ký | Mô tả chi tiết |
|---|------|--------|----------------|
| 1 | `navigateTo` | `(screen: ScreenState)` | Đặt `_currentScreen.value = screen` |
| 2 | `setInitialLearningLanguage` | `(language: AppLanguage)` | Đặt `_selectedLanguage` + `_learningLanguages`, thêm vào DB, chuyển active language. Dùng trong onboarding. |
| 3 | `selectLanguage` | `(language: AppLanguage)` | Chuyển ngôn ngữ. Thêm vào learning list nếu chưa có, update DB. |
| 4 | `addLearningLanguage` | `(language: AppLanguage)` | Thêm ngôn ngữ mới, set selected, persist DB. |
| 5 | `updateStudySchedule` | `(reminderHour, reminderMinute=0)` | Tạo `StudySchedule` + schedule alarm. |
| 6 | `updateUserName` | `(name: String)` | Đổi `_userName`. |
| 7 | `updateUserVipLevel` | `(level: Int)` | Đổi `_userVipLevel`. |
| 8 | `speak` | `(text, languageTag="en-US")` | Delegate `ttsManager.speak()`. |
| 9 | `toggleStar` | `(cardId, currentStarred)` | Đổi bookmark qua repository. |
| 10 | `recordReview` | `(cardId, difficulty)` | Ghi review + update widget. |
| 11 | `markCardMastered` | `(cardId, langCode)` | Đánh dấu mastered + update widget. |
| 12 | `markCardUnmastered` | `(cardId)` | Đánh dấu unmastered. |
| 13 | `completeStudySession` | `(deckId, deckTitle, langCode, cardsStudied, masteredCount, durationSecs)` | Ghi study session vào DB. |
| 14 | `startStudyByLanguage` | `(langCode)` | Fetch decks cho ngôn ngữ, bắt đầu học deck đầu tiên. |
| 15 | `openDeckDetail` | `(deck)` | Load cards, navigate → DeckDetail. |
| 16 | `startStudyDeck` | `(deck)` | Load cards, navigate → Study. |
| 17 | `startQuizDeck` | `(deck)` | Load cards, navigate → Quiz. |
| 18 | `startMatchDeck` | `(deck)` | Load cards, navigate → Match. |
| 19 | `createNewDeck` | `(deck)` | Insert deck vào DB. |
| 20 | `createNewDeckWithCards` | `(deck, selectedCards)` | Tạo deck + map + insert cards. |
| 21 | `createNewCard` | `(card)` | Insert 1 card. |
| 22 | `importCards` | `(cards)` | Bulk insert. |
| 23 | `openStarredCards` | `()` | Fetch starred, navigate → Starred. |
| 24 | `startStudySavedCards` | `(cards, title, langCode)` | Tạo deck tạm từ starred, navigate → Study. |
| 25 | `startQuizSavedCards` | `(cards, title, langCode)` | Tạo deck tạm, navigate → Quiz. |
| 26 | `startMatchSavedCards` | `(cards, title, langCode)` | Tạo deck tạm, navigate → Match. |
| 27 | `startStudyUnmasteredDeck` | `(deck, wrongCards)` | Tạo deck tạm unmastered, navigate → Study. |
| **28** | **`startOnboardingTrial`** | `(language: AppLanguage, reminderHour: Int)` | **MỚI:** Set language, schedule alarm, load starter cards, navigate → OnboardingTrialStudy. |
| **29** | **`startOnboardingTrialQuiz`** | `(language, cards)` | **MỚI:** Navigate → OnboardingTrialQuiz. |
| **30** | **`finishOnboardingTrialAndGoToAuth`** | `()` | **MỚI:** Navigate → Register. |
| **31** | **`completeTrialRegistration`** | `(username)` | **MỚI:** Update userName, add learning language, insert starter cards (id=0L for auto-gen), set streak=1, navigate → Home. |
| 32 | `processQuizResult` | `(deck, score, total, wrongCards, durationSecs=90)` | 5 bước: (1) ghi study session, (2) save quiz record, (3) mark wrong cards unmastered, (4) auto-tạo/merge deck "unmastered review", (5) cộng điểm + update widget. |
| 33 | `triggerSmartNotificationTest` | `()` | Force smart notification, store preview. |
| 34 | `triggerAchievementTest` | `(streak)` | Test milestone notification, store preview. |
| 35 | `dismissNotificationPreview` | `()` | Clear preview. |
| 36 | `snoozeStudyReminderToday` | `()` | Snooze + clear preview + clear system notifications. |
| 37 | `onCleared` | `()` | Cleanup TTS. |

### 3.2 `MainActivity.kt` (754 dòng)

**`class MainActivity : ComponentActivity`**
- `private val viewModel: MainViewModel` (via `by viewModels()`).
- `onCreate` — `enableEdgeToEdge()`, `handleNotificationIntent(intent)`, `NotificationHelper.createNotificationChannels(this)`, `setContent { MyApplicationTheme { NTKFlashCardApp(viewModel) } }`.
- `onNewIntent` — `handleNotificationIntent(intent)`.
- `onResume` — `NotificationHelper.clearAllNotifications(this)` (tự clear khi app foreground).
- `private fun handleNotificationIntent(intent)` — đọc `EXTRA_NAV_TARGET` ("HOME_STUDY" → navigate Home; "ACHIEVEMENTS" → navigate Home).

**`@Composable fun NTKFlashCardApp(viewModel)`** — Entry composable:

1. **Thu thập 13 StateFlow** bằng `collectAsStateWithLifecycle()`: `currentScreen`, `selectedLanguage`, `learningLanguages`, `decks`, `allDecks`, `starredCards`, `allCards`, `streakDays`, `masteredCount`, `totalCount`, `userName`, `userVipLevel`, `notificationPreview`.

2. **Local state**: `showProfileDialog(false)`, `showCreateDeckDialog(false)`, `targetDeckForCardCreation(null)`.

3. **Notification permission**: `rememberLauncherForActivityResult` cho `POST_NOTIFICATIONS` (Android 13+).

4. **LaunchedEffect(notificationPreview)** — Auto-dismiss banner sau 6s: `delay(6000)` → `viewModel.dismissNotificationPreview()`.

5. **BackHandler** — Xử lý nút Back:
   - Không enable cho: `Welcome`, `OnboardingTrialStudy`, `OnboardingTrialQuiz`.
   - `Login`/`Register`/`Onboarding`/`Home` → về `Welcome`.
   - Còn lại → về `Home`.

6. **`Crossfade(currentScreen)` + `when(screen)`** — 12 branch:
   - `Welcome` → `WelcomeScreen` (onStartLearning → Onboarding, onLoginClick → Login)
   - `Login` → `LoginScreen` (onLoginSuccess → Home, onNavigateToRegister → Register)
   - `Register` → `RegisterScreen` (onRegisterSuccess → Home, onNavigateToLogin → Login)
   - `Onboarding` → `OnboardingStepsScreen` (onCompleteOnboarding → viewModel.startOnboardingTrial)
   - **`OnboardingTrialStudy`** → `FlashcardStudyScreen(isOnboardingTrial=true, allowBack=false, onStartQuiz → startOnboardingTrialQuiz, onSessionFinished → startOnboardingTrialQuiz)`
   - **`OnboardingTrialQuiz`** → `QuizScreen(isOnboardingTrial=true, onCompleteTrial → finishOnboardingTrialAndGoToAuth)`
   - `Home` → `HomeScreen` (nối ~30 callback lambda tới viewModel)
   - `DeckDetail` → `DeckDetailScreen`
   - `Study` → `FlashcardStudyScreen(isOnboardingTrial=false)`
   - `Quiz` → `QuizScreen(isOnboardingTrial=false)`
   - `Match` → `WordMatchScreen`
   - `Starred` → `FlashcardStudyScreen` (deck mock "starred")

7. **Banner thông báo in-app** (`AnimatedVisibility`, lines 440-707):
   - Hiển thị khi `notificationPreview != null`.
   - 2 theme: achievement (dark bg) vs study reminder (light bg).
   - Features: app icon, mascot penguin, streak tracker (7 ngày), expand/collapse, nút "Học ngay"/"Để sau"/"Cảm ơn".
   - Auto-dismiss 6s (LaunchedEffect).

8. **3 Dialog**: `UserProfileDialog`, `CreateDeckDialog`, `CreateCardDialog`.

### 3.3 `audio/TTSManager.kt`

**`class TTSManager(context)`**
- `init` — khởi tạo `TextToSpeech`, `Locale.US` mặc định.
- `fun speak(text, languageTag="en-US")` — map `Locale` (en→US, ko→KOREA, ja→JAPAN, zh→CHINA, fr→FRANCE, de→GERMANY, es→SPAIN, vi→VIETNAM, **`it`→ITALY`, `pt`→PORTUGAL`** được map đúng), `QUEUE_FLUSH`.
- `fun stop()`, `fun shutdown()`.

### 3.4 `notification/SmartNotificationEngine.kt`

- `data class NotificationPreviewEvent(title, message, isAchievement=false, formattedTime)`.
- `class SmartNotificationEngine(context)`:
  - `suspend fun evaluateAndSendSmartNotification(schedule=StudySchedule(), isForcedTest=false, onPreviewGenerated)`:
    1. Check schedule disabled + not forced → skip.
    2. Check snooze (`NotificationHelper.isSnoozedToday`) → skip.
    3. Check đã học (`getSessionsSince(startOfToday)`) → skip (nếu không forced).
    4. Fetch user profile (streak, userName), count dueWords.
    5. Chọn thông điệp 4 trường hợp:
       - Forced + studied: chúc mừng hoàn thành.
       - dueWords ≥ threshold + streak > 0: "Duy trì X ngày liên tục!".
       - dueWords ≥ threshold, streak = 0: "X từ đang chờ học!".
       - dueWords = 0, streak > 0: "Không mất streak X ngày!".
       - General: "Thời điểm lý tưởng để học từ vựng".
    6. Gửi qua `NotificationHelper.showStudyReminderNotification()`.
    7. Gọi `onPreviewGenerated` callback.
  - `fun checkAndNotifyStreakMilestone(newStreak, onPreviewGenerated)` — Notification thành tựu khi đạt milestone (3, 7, 10, 30 ngày).

### 3.5 `notification/NotificationHelper.kt`

**`object NotificationHelper`**
- Hằng: `CHANNEL_STUDY_REMINDER`, `CHANNEL_ACHIEVEMENTS`, `NOTIFICATION_ID_SMART_STUDY=1001`, `NOTIFICATION_ID_ACHIEVEMENT=1002`.
- `fun isSnoozedToday(context): Boolean` — SharedPreferences key `"key_snoozed_study_date"`, check `yyyy-MM-dd` == today.
- `fun setSnoozedToday(context)` — Ghi today string.
- `fun clearSnooze(context)` — Xoá key.
- `fun clearAllNotifications(context)` — Cancel cả 2 ID (wrapped in try-catch).
- `fun createNotificationChannels(context)` — 2 channel `IMPORTANCE_HIGH`: Study Reminder (lights, vibration, badge) + Achievements.
- `fun showStudyReminderNotification(context, title, message, dueWordsCount, streakDays)`:
  - PendingIntent study: mở MainActivity + `EXTRA_NAV_TARGET="HOME_STUDY"`.
  - PendingIntent snooze: broadcast `NotificationActionReceiver` + `ACTION_SNOOZED_TODAY`.
  - 2 action buttons: "Học ngay" / "Để sau".
  - BigTextStyle expanded, penguin mascot bitmap 128x128, auto-cancel.
- `fun showAchievementNotification(context, title, message)`:
  - Opens MainActivity with `EXTRA_NAV_TARGET="ACHIEVEMENTS"`.
  - One action button: "Xem thành tựu".

### 3.6 `notification/StudyAlarmScheduler.kt`

**`object StudyAlarmScheduler`**
- `fun scheduleStudyAlarm(context, schedule)` — `setExactAndAllowWhileIdle` (requestCode 8888), nếu giờ đã qua → chuyển ngày mai, fallback `set()`.
- `fun cancelStudyAlarm(context)`.

### 3.7 `notification/StudyAlarmReceiver.kt`

**`class StudyAlarmReceiver : BroadcastReceiver`**
- `onReceive` — `goAsync()`, `receiverScope.launch { engine.evaluateAndSendSmartNotification(StudySchedule()); scheduleStudyAlarm(...) }`, `pendingResult.finish()`.

### 3.8 `notification/NotificationActionReceiver.kt`

**`class NotificationActionReceiver : BroadcastReceiver`**
- `companion`: `ACTION_SNOOZE_TODAY`, `ACTION_START_STUDY`.
- `onReceive` — `ACTION_SNOOZE_TODAY`: `clearAllNotifications` + `setSnoozedToday` + Toast.

### 3.9 `widget/VocabularyStreakWidgetProvider.kt`

**`class VocabularyStreakWidgetProvider : AppWidgetProvider`**
- `onUpdate`, `onEnabled`, `onDisabled`, `onReceive`.
- `companion object`:
  - `const ACTION_REFRESH_WORD`.
  - `fun startAutoScroll(context)` — vòng lặp `delay(8000)` + broadcast (dùng coroutine scope).
  - `fun stopAutoScroll()`.
  - `fun updateAllWidgets(context, streakDays?)` — lưu streak vào prefs + broadcast.
  - `fun updateAppWidget(context, appWidgetManager, appWidgetId)` — RemoteViews, đọc streak + từ chưa thuộc ngẫu nhiên.

---

## 4. Luồng nghiệp vụ tiêu biểu

### 4.1 Onboarding → Trial Study → Trial Quiz → Đăng ký
```
WelcomeScreen → [Bắt đầu học ngay] → OnboardingStepsScreen (7 bước)
  → onCompleteOnboarding(lang, hour) → viewModel.startOnboardingTrial(lang, hour)
  → OnboardingTrialStudy (FlashcardStudyScreen, isOnboardingTrial=true, allowBack=false)
  → [Tiếp tục] → startOnboardingTrialQuiz(lang, cards)
  → OnboardingTrialQuiz (QuizScreen, isOnboardingTrial=true)
  → [Hoàn thành] → finishOnboardingTrialAndGoToAuth()
  → RegisterScreen → [Đăng ký] → completeTrialRegistration(username)
  → HomeScreen
```

### 4.2 Nhắc học
```
StudyAlarmScheduler (AlarmManager) → tới giờ
  → StudyAlarmReceiver (goAsync)
  → SmartNotificationEngine (kiểm tra snooze/đã học, sinh message)
  → NotificationHelper (gửi notification)
  → Bấm "Học ngay" → MainActivity.handleNotificationIntent → navigateTo(Home)
  → Bấm "Để sau" → NotificationActionReceiver → setSnoozedToday + clear notifications
```

### 4.3 Mở màn hình học
```
Bấm "Học ngay" → onStudyDeck(deck)
  → viewModel.startStudyDeck(deck)
  → repository.getCardsForDeck(deck.id).first()
  → _currentScreen = Study(deck, cards)
  → MainActivity Crossfade hiển thị FlashcardStudyScreen
```

### 4.4 Quiz → Xử lý kết quả
```
QuizScreen hoàn thành → onFinishQuiz(score, total, wrongCards)
  → viewModel.processQuizResult(deck, score, total, wrongCards, durationSecs=90)
  → 5 bước pipeline:
    1. recordStudySession(deckId, deckTitle, langCode, cardsStudied, masteredCount, duration)
    2. recordQuizResult(QuizRecordEntity(...))
    3. wrongCards.forEach { markCardUnmastered(id) }
    4. Auto-tạo/merge deck "wrong_cards_<deckId>" nếu wrongCards.isNotEmpty()
    5. addPoints(totalPoints) + updateAllWidgets()
```

### 4.5 Ghi nhận học & widget
```
onRecordReview → recordCardReview(id, difficulty)
  → difficulty==1 → isMastered=true
  → VocabularyStreakWidgetProvider.updateAllWidgets() → widget tự làm mới
```

---

## 5. Phân định rõ: logic nằm ở đâu

| Logic | Vị trí | File |
| --- | --- | --- |
| Điều hướng | ViewModel + MainActivity | `MainViewModel`, `MainActivity` |
| Onboarding trial flow | ViewModel (state) + MainActivity (routing) | `MainViewModel`, `MainActivity` |
| Đáp án nhiễu Quiz / điểm / pháo hoa | UI (màn hình) | `ui/quiz/QuizScreen.kt` |
| Ghép cặp Match | UI (màn hình) | `ui/match/WordMatchScreen.kt` |
| Ghi nhận SRS | ViewModel → Repository → DAO | `MainViewModel`, `FlashCardRepository` |
| Phiên học / quiz record | ViewModel → Repository → DAO | `MainViewModel`, `FlashCardRepository` |
| Phát âm | TTSManager | `audio/TTSManager.kt` |
| Thông báo | notification package | `SmartNotificationEngine`, `NotificationHelper` |
| Widget | widget package | `VocabularyStreakWidgetProvider` |
| Banner in-app | MainActivity | `NTKFlashCardApp` (AnimatedVisibility) |
| Snooze | NotificationActionReceiver + NotificationHelper | SharedPreferences |
| Quiz result processing | MainViewModel.processQuizResult | 5-step pipeline |

---

## 6. Cẩm nang sửa đổi

| Yêu cầu | Nơi sửa |
| --- | --- |
| Thêm màn hình | case `ScreenState` + hàm `MainViewModel` + `when` `MainActivity` |
| Đổi luồng Back | `BackHandler` `MainActivity` |
| Onboarding trial | `startOnboardingTrial` + `startOnboardingTrialQuiz` + `finishOnboardingTrialAndGoToAuth` + `completeTrialRegistration` |
| Kích hoạt SRS SM-2 | `recordReview` → `recordSrsReview(card, rating, isCorrect)` |
| Sửa giờ nhắc | `updateStudySchedule` + `StudySchedule` |
| Sửa nội dung thông báo | `SmartNotificationEngine` + `NotificationHelper` |
| Sửa widget | `VocabularyStreakWidgetProvider` + `res/layout/widget_*` |
| Nối đăng nhập DB | `LoginScreen`/`RegisterScreen` + ViewModel + `UserAccountDao` |
| Đổi giọng TTS | `audio/TTSManager.kt` (bổ sung it/pt) |
| Làm streak "thật" | `UserProfileDao.updateStreak` + tính chuỗi ngày |
| Snooze thông báo | `NotificationHelper.setSnoozedToday` + `StudyAlarmReceiver` |
| Banner in-app | `NTKFlashCardApp` → `AnimatedVisibility` overlay |

---

## 7. Q&A mở rộng

### Q1. Vì sao điều hướng bằng sealed class mà không dùng Navigation Compose?
Truyền dữ liệu trực tiếp qua data class, trạng thái nằm trong `StateFlow`, dễ kiểm soát Back. Với app nhỏ thì đơn giản và đủ.

### Q2. Vì sao `StateFlow` thay vì `LiveData`? Vì sao `flatMapLatest`?
StateFlow là API coroutine gốc, nhiều toán tử, dễ test. `flatMapLatest` đảm bảo đổi ngôn ngữ thì Flow cũ huỷ, Flow mới chạy, tránh race condition.

### Q3. Nhắc học hoạt động ra sao?
AlarmManager hẹn giờ → Receiver (goAsync) → Engine kiểm tra ngữ cảnh → NotificationHelper gửi; bấm "Học ngay" mở app, "Để sau" hoãn.

### Q4. Vì sao AlarmManager thay vì WorkManager?
AlarmManager + `setExactAndAllowWhileIdle` hẹn giờ chính xác; WorkManager chỉ đảm bảo "đợi" chứ không chính xác đến phút.

### Q5. Widget cập nhật thế nào?
`updateAllWidgets()` lưu streak vào prefs + gửi broadcast → `onReceive` → `updateAppWidget` đọc prefs + render RemoteViews. Tự refresh mỗi 8s qua coroutine.

### Q6. TTS hỗ trợ ngôn ngữ nào?
en→US, ko→KOREA, ja→JAPAN, zh→CHINA, fr→FRANCE, de→GERMANY, es→SPAIN, vi→VIETNAM, it→ITALY, pt→PORTUGAL. Thiếu locale → fallback Locale.US.

### Q7. Onboarding trial flow hoạt động ra sao?
User chọn ngôn ngữ → `startOnboardingTrial` (load 5 starter cards từ `StarterVocabData`) → trial study (FlashcardStudyScreen, `isOnboardingTrial=true`, `allowBack=false`) → trial quiz (QuizScreen, `isOnboardingTrial=true`) → `finishOnboardingTrialAndGoToAuth` → RegisterScreen → `completeTrialRegistration` (insert starter cards vào DB, set streak=1) → Home.

### Q8. `processQuizResult` làm những gì?
5 bước: (1) ghi study session, (2) save quiz record với accuracy, (3) mark wrong cards unmastered, (4) auto-tạo/merge deck "wrong_cards_<deckId>" để ôn lại, (5) cộng points + update widget.

### Q9. Banner in-app hoạt động thế nào?
Khi `notificationPreview != null` → `AnimatedVisibility` hiển thị overlay. Tự ẩn sau 6s (LaunchedEffect). Phân biệt achievement (dark) vs study reminder (light). Có nút "Học ngay", "Để sau", "Cảm ơn".

### Q10. Snooze hoạt động thế nào?
Bấm "Để sau" → `NotificationActionReceiver.onReceive` → `clearAllNotifications` + `setSnoozedToday` (SharedPreferences). SmartNotificationEngine check `isSnoozedToday` → skip notification nếu snooze hôm nay.

### Q11. `completeTrialRegistration` làm gì?
Update userName, add learning language (qua repository), insert 5 starter cards (id=0L for auto-gen) vào DB, set streak=1, navigate Home.

### Q12. `handleNotificationIntent` xử lý gì?
Đọc `EXTRA_NAV_TARGET` từ Intent: "HOME_STUDY" → navigate Home; "ACHIEVEMENTS" → navigate Home. Được gọi cả trong `onCreate` và `onNewIntent`.
