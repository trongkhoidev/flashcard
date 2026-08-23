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
MainActivity.kt                 # Entry + điều hướng + banner thông báo in-app
audio/TTSManager.kt             # Phát âm
ui/viewmodel/MainViewModel.kt   # ScreenState + StateFlow + nghiệp vụ
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

**`sealed class ScreenState`** — `Welcome, Login, Register, Onboarding, Home` (object) + `DeckDetail(deck, cards), Study(deck, cards), Quiz(deck, cards), Match(deck, cards), Starred(cards)` (data class).

**`class MainViewModel(application) : AndroidViewModel`**
- Khởi tạo: `database`, `repository`, `ttsManager`, `smartNotificationEngine`.
- StateFlow: `currentScreen`, `selectedLanguage`, `learningLanguages`, `learningLanguagesFromDb`, `dueCardsForCurrentLanguage`, `dueCountForCurrentLanguage`, `starterCardsForCurrentLanguage`, `masteredCountForCurrentLanguage`, `userName`, `userVipLevel`, `streakDays`, `notificationPreview`, `decksForCurrentLanguage`, `allDecks`, `starredCardsList`, `allCardsList`, `masteredCount`, `totalCardsCount`.
- Hàm:
  - `navigateTo(screen)`, `selectLanguage(language)`, `addLearningLanguage(language)`.
  - `recordReview(cardId, difficulty)`, `recordSrsReview(card, rating, isCorrect)`.
  - `updateStudySchedule(hour, minute)`, `updateUserName(name)`, `updateUserVipLevel(level)`.
  - `speak(text, languageTag)`, `toggleStar(cardId, currentStarred)`.
  - `completeStudySession(deckId, deckTitle, langCode, cardsStudied, masteredCount, durationSecs)`.
  - `triggerSmartNotificationTest()`, `triggerAchievementTest(streak)`, `snoozeStudyReminderToday()`, `dismissNotificationPreview()`.
  - `createNewDeck(deck)`, `createNewDeckWithCards(deck, selectedCards)`, `createNewCard(card)`, `importCards(cards)`.
  - `startStudyByLanguage(langCode)`, `openDeckDetail(deck)`, `startStudyDeck(deck)`, `startQuizDeck(deck)`, `startMatchDeck(deck)`, `openStarredCards()`.
  - `startStudySavedCards(cards, title, langCode)`, `startQuizSavedCards(...)`, `startMatchSavedCards(...)`.
  - `override fun onCleared()`.

### 3.2 `MainActivity.kt`

**`class MainActivity : ComponentActivity`**
- `onCreate` — `enableEdgeToEdge()`, `handleNotificationIntent(intent)`, `setContent { MyApplicationTheme { NTKFlashCardApp(viewModel) } }`.
- `onNewIntent` — xử lý mở lại từ notification.
- `onResume` — `NotificationHelper.clearAllNotifications`.
- `private fun handleNotificationIntent(intent)` — đọc `EXTRA_NAV_TARGET` ("HOME_STUDY") → `navigateTo(Home)`.

**`@Composable fun NTKFlashCardApp(viewModel)`**
- Thu thập StateFlow bằng `collectAsStateWithLifecycle()`.
- `BackHandler` — Login/Register/Onboarding/Home → Welcome; còn lại → Home.
- `Crossfade(currentScreen)` + `when(screen)` hiển thị: Welcome, Login, Register, Onboarding, Home, DeckDetail, Study, Quiz, Match, Starred (nối callback tới `viewModel`).
- Banner thông báo in-app (`AnimatedVisibility`), tự ẩn 6s, nút "Học ngay"/"Để sau".
- 3 dialog: `UserProfileDialog`, `CreateDeckDialog`, `CreateCardDialog`.
- Xin quyền `POST_NOTIFICATIONS` (Android 13+) khi test notification.

### 3.3 `audio/TTSManager.kt`

**`class TTSManager(context)`**
- `init` — khởi tạo `TextToSpeech`, mặc định `Locale.US`.
- `fun speak(text, languageTag="en-US")` — map Locale (ko/ja/zh/fr/de/es/vi, mặc định US), `QUEUE_FLUSH`.
- `fun stop()`, `fun shutdown()`.

### 3.4 `notification/SmartNotificationEngine.kt`

- `data class NotificationPreviewEvent(title, message, isAchievement=false, formattedTime)`.
- `class SmartNotificationEngine(context)`:
  - `suspend fun evaluateAndSendSmartNotification(schedule=StudySchedule(), isForcedTest=false, onPreviewGenerated)` — kiểm tra snooze/đã học, đếm dueWords + streak, sinh thông điệp 4 kịch bản, gửi + preview.
  - `fun checkAndNotifyStreakMilestone(newStreak, onPreviewGenerated)` — notification thành tựu.

### 3.5 `notification/NotificationHelper.kt`

**`object NotificationHelper`**
- Hằng: `CHANNEL_STUDY_REMINDER`, `CHANNEL_ACHIEVEMENTS`, `NOTIFICATION_ID_SMART_STUDY=1001`, `NOTIFICATION_ID_ACHIEVEMENT=1002`.
- `fun isSnoozedToday(context)`, `setSnoozedToday(context)`, `clearSnooze(context)` (SharedPreferences "ntk_flashcard_prefs").
- `fun clearAllNotifications(context)`.
- `fun createNotificationChannels(context)` — 2 channel IMPORTANCE_HIGH.
- `fun showStudyReminderNotification(context, title, message, dueWordsCount, streakDays)` — custom notification + action "Học ngay"/"Để sau".
- `fun showAchievementNotification(context, title, message)`.

### 3.6 `notification/StudyAlarmScheduler.kt`

**`object StudyAlarmScheduler`**
- `fun scheduleStudyAlarm(context, schedule)` — `setExactAndAllowWhileIdle` (code 8888), nếu giờ đã qua chuyển ngày mai, fallback `set()`.
- `fun cancelStudyAlarm(context)`.

### 3.7 `notification/StudyAlarmReceiver.kt`

**`class StudyAlarmReceiver : BroadcastReceiver`**
- `onReceive` — `goAsync()`, `receiverScope.launch { engine.evaluateAndSendSmartNotification(StudySchedule()); scheduleStudyAlarm(...) }`, `pendingResult.finish()`.

### 3.8 `notification/NotificationActionReceiver.kt`

**`class NotificationActionReceiver : BroadcastReceiver`**
- `companion`: `ACTION_SNOOZE_TODAY`, `ACTION_START_STUDY`.
- `onReceive` — `ACTION_SNOOZE_TODAY`: tắt notification + `setSnoozedToday` + Toast.

### 3.9 `widget/VocabularyStreakWidgetProvider.kt`

**`class VocabularyStreakWidgetProvider : AppWidgetProvider`**
- `onUpdate`, `onEnabled`, `onDisabled`, `onReceive`.
- `companion object`:
  - `const ACTION_REFRESH_WORD`.
  - `fun startAutoScroll(context)` — vòng lặp `delay(8000)` + broadcast.
  - `fun stopAutoScroll()`.
  - `fun updateAllWidgets(context, streakDays?)` — lưu streak vào prefs + broadcast.
  - `fun updateAppWidget(context, appWidgetManager, appWidgetId)` — RemoteViews, đọc streak + từ chưa thuộc ngẫu nhiên.

---

## 4. Luồng nghiệp vụ tiêu biểu

### 4.1 Mở màn hình học
Bấm "Học ngay" → `onStudyDeck(deck)` → `viewModel.startStudyDeck(deck)` → `repository.getCardsForDeck(deck.id).first()` → `_currentScreen = Study(deck, cards)` → `MainActivity` quan sát → `Crossfade` hiển thị.

### 4.2 Nhắc học
`StudyAlarmScheduler` (AlarmManager) → tới giờ `StudyAlarmReceiver` (goAsync) → `SmartNotificationEngine` (kiểm tra snooze/đã học, sinh message) → `NotificationHelper` (gửi) → bấm "Học ngay" mở app, "Để sau" hoãn.

### 4.3 Ghi nhận học & widget
`onRecordReview` → `recordCardReview` (isMastered = difficulty==1) → `updateAllWidgets` đồng bộ streak → widget tự làm mới.

---

## 5. Phân định rõ: logic nằm ở đâu

| Logic | Vị trí | File |
| --- | --- | --- |
| Điều hướng | ViewModel + MainActivity | `MainViewModel`, `MainActivity` |
| Đáp án nhiễu Quiz / điểm / pháo hoa | UI (màn hình) | `ui/quiz/QuizScreen.kt` |
| Ghép cặp Match | UI (màn hình) | `ui/match/WordMatchScreen.kt` |
| Ghi nhận SRS | ViewModel → Repository → DAO | `MainViewModel`, `FlashCardRepository` |
| Phiên học / quiz record | ViewModel → Repository → DAO | `MainViewModel`, `FlashCardRepository` |
| Phát âm | TTSManager | `audio/TTSManager.kt` |
| Thông báo | notification package | `SmartNotificationEngine`, `NotificationHelper` |
| Widget | widget package | `VocabularyStreakWidgetProvider` |

---

## 6. Cẩm nang sửa đổi

| Yêu cầu | Nơi sửa |
| --- | --- |
| Thêm màn hình | case `ScreenState` + hàm `MainViewModel` + `when` `MainActivity` |
| Đổi luồng Back | `BackHandler` `MainActivity` |
| Kích hoạt SRS SM-2 | `recordReview` → `recordSrsReview(card, rating, isCorrect)` |
| Sửa giờ nhắc | `updateStudySchedule` + `StudySchedule` |
| Sửa nội dung thông báo | `SmartNotificationEngine` + `NotificationHelper` |
| Sửa widget | `VocabularyStreakWidgetProvider` + `res/layout/widget_*` |
| Nối đăng nhập DB | `LoginScreen`/`RegisterScreen` + ViewModel + `UserAccountDao` |
| Đổi giọng TTS | `audio/TTSManager.kt` (bổ sung it/pt) |
| Làm streak "thật" | `UserProfileDao.updateStreak` + tính chuỗi ngày |

---

## 7. Q&A mở rộng

### Q1. Vì sao điều hướng bằng sealed class mà không dùng Navigation Compose?
Truyền dữ liệu trực tiếp qua data class, trạng thái nằm trong `StateFlow`, dễ kiểm soát Back. Với app nhỏ thì đơn giản và đủ.

### Q2. Vì sao `StateFlow` thay vì `LiveData`? Vì sao `flatMapLatest`?
StateFlow là API coroutine gốc, nhiều toán tử, dễ test. `flatMapLatest` đảm bảo đổi ngôn ngữ thì Flow cũ huỷ, Flow mới chạy, tránh race condition.

### Q3. Nhắc học hoạt động ra sao?
AlarmManager hẹn giờ → Receiver (goAsync) → Engine kiểm tra ngữ cảnh → NotificationHelper gửi; bấm "Học ngay" mở app, "Để sau" hoãn.

### Q4. Vì sao AlarmManager thay vì WorkManager?
Cần hẹn giờ chính xác → `setExactAndAllowWhileIdle`; WorkManager không chính xác.

### Q5. Vì sao receiver dùng `goAsync()`?
`onReceive` giới hạn ~10s Main Thread; `goAsync()` cho phép coroutine query DB rồi `finish()`.

### Q6. Vì sao cờ "Để sau" dùng SharedPreferences?
Cờ nhỏ, tạm, không quan hệ → SharedPreferences đủ. Room dành cho dữ liệu có cấu trúc.

### Q7. Widget làm mới 8 giây bằng gì?
Vòng lặp coroutine `while(true){ delay(8000); sendBroadcast(...) }` vì `updatePeriodMillis` tối thiểu 30 phút.

### Q8. Phát âm offline thế nào?
`TextToSpeech` map Locale, `QUEUE_FLUSH` phát mới ngay, không gọi mạng.

### Q9. `recordReview` vs `recordSrsReview`?
`recordReview` đánh dấu thuộc/chưa thuộc; `recordSrsReview` chạy SM-2 đầy đủ. Hiện UI gọi `recordReview`.

### Q10. Vì sao `ttsManager.shutdown()` trong `onCleared`?
Giải phóng tài nguyên TTS, tránh rò rỉ bộ nhớ.

### Q11. Banner in-app tự ẩn 6s bằng gì?
`LaunchedEffect(notificationPreview)` + `delay(6000)` + `dismissNotificationPreview()`.

### Q12. Vì sao sau review lại `updateAllWidgets`?
Widget không quan sát StateFlow trực tiếp, nên ViewModel chủ động đẩy streak + broadcast.
