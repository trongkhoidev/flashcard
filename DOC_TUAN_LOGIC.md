# DOC_TUAN — Tài liệu thành viên Tuấn: Business Logic, Điều hướng, Âm thanh, Notification & Widget

> **Vai trò:** Bạn là "bộ não trung tâm" — kết nối UI (Khôi) với Data (Nam), xử lý nghiệp vụ, điều hướng, phát âm, đồng thời phụ trách **thông báo thông minh** và **widget từ vựng**. Tài liệu này giải thích **vì sao** chọn giải pháp này (mà không dùng giải pháp khác) và **triển khai** từng cơ chế như thế nào.

---

## Mục lục

1. [Phạm vi & thư mục](#1-phạm-vi--thư-mục)
2. [Công nghệ & vì sao chọn](#2-công-nghệ--vì-sao-chọn)
3. [ScreenState — mô hình điều hướng](#3-screenstate--mô-hình-điều-hướng)
4. [MainViewModel chi tiết](#4-mainviewmodel-chi-tiết)
5. [MainActivity — điều hướng & điều phối](#5-mainactivity--điều-hướng--điều-phối)
6. [TTSManager — phát âm](#6-ttsmanager--phát-âm)
7. [Hệ thống thông báo thông minh](#7-hệ-thống-thông-báo-thông-minh)
8. [Widget từ vựng](#8-widget-từ-vựng)
9. [Nghiệp vụ ghi nhận học & SRS](#9-nghiệp-vụ-ghi-nhận-học--srs)
10. [Phân định rõ: logic nằm ở đâu](#10-phân-định-rõ-logic-nằm-ở-đâu)
11. [Cẩm nang sửa đổi Logic](#11-cẩm-nang-sửa-đổi-logic)
12. [Q&A mở rộng — trả lời giám khảo](#12-qa-mở-rộng--trả-lời-giám-khảo)

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

### 2.1 Vì sao dùng `AndroidViewModel` (không phải `ViewModel` thuần)?
Vì ViewModel cần `Application` context để: mở `AppDatabase`, tạo `TTSManager`, `SmartNotificationEngine`, và cập nhật widget (`VocabularyStreakWidgetProvider.updateAllWidgets`). `AndroidViewModel(application)` cung cấp sẵn context mà vẫn sống qua vòng đời cấu hình thay đổi.

### 2.2 Vì sao dùng `StateFlow` (không phải `LiveData`)?
- `StateFlow` là API coroutine gốc, nhiều toán tử hơn (`flatMapLatest`, `stateIn`), dễ test.
- Thống nhất với `Flow` từ Room; giữ một nguồn trạng thái (Single Source of Truth).

### 2.3 Vì sao dùng `flatMapLatest` cho dữ liệu theo ngôn ngữ?
Khi `_selectedLanguage` đổi, `flatMapLatest` **huỷ Flow cũ và chạy Flow mới** (`getDueCardsForLanguage(lang.code)`...). Điều này đảm bảo dữ liệu hiển thị luôn khớp ngôn ngữ đang chọn, không bị dữ liệu cũ "chạy đua" (race condition). `switchMap` của LiveData tuy tương tự nhưng Flow linh hoạt hơn.

### 2.4 Vì sao dùng `stateIn(WhileSubscribed(5000))`?
- `WhileSubscribed` chỉ chạy Flow khi có người thu thập → tiết kiệm tài nguyên.
- `5000` là thời gian giữ lại 5 giây sau khi không còn subscriber, tránh khởi động lại khi xoay màn hình nhanh.

### 2.5 Vì sao điều hướng bằng `ScreenState` sealed class (không dùng Navigation Compose)?
- Truyền dữ liệu **kèm theo** trực tiếp qua data class (`Study(deck, cards)`), không cần route string/`SavedStateHandle` phức tạp.
- Trạng thái điều hướng nằm trong `StateFlow`, dễ kiểm soát Back bằng `BackHandler`.
- Đánh đổi: không có back-stack tự động; nhưng với app nhỏ/ít màn hình thì đủ và đơn giản hơn.

### 2.6 Vì sao dùng `AlarmManager` thay vì `WorkManager`?
- **Cần chính xác giờ phút** (nhắc đúng 19:00) → `setExactAndAllowWhileIdle` của AlarmManager phù hợp.
- WorkManager tối ưu cho tác vụ nền **không cần chính xác** (sync, upload); việc hẹn giờ chính xác lại là điểm yếu của WorkManager.

### 2.7 Vì sao dùng `SharedPreferences` cho cờ "Để sau" thay vì Room?
Cờ snooze là **giá trị nhỏ, tạm thời** (một chuỗi ngày), không cần quan hệ/query → `SharedPreferences` đơn giản và đủ. Room dành cho dữ liệu có cấu trúc/quan hệ.

### 2.8 Vì sao `BroadcastReceiver` dùng `goAsync()`?
`onReceive` chạy trên Main Thread và có thời gian giới hạn (~10s). `goAsync()` cho phép chạy coroutine bất đồng bộ (query DB trong `SmartNotificationEngine`) rồi gọi `pendingResult.finish()` — tránh bị hệ thống giết giữa chừng.

---

## 3. ScreenState — mô hình điều hướng

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

### Sơ đồ điều hướng
```
Welcome ──onStartLearning──► Onboarding ──onComplete(lang, hour)──► Home
   ├──onLoginClick──► Login ──onLoginSuccess──► Home
   │                   └──onNavigateToRegister──► Register ──onRegisterSuccess──► Home
   └──onSelectLanguage──► Onboarding

Home ──► DeckDetail ──► Study / Quiz / Match
Home ──► onStudyDeck/onQuizDeck/onMatchDeck ──► Study/Quiz/Match
Home ──► onOpenStarred ──► Starred

BackHandler: Login/Register/Onboarding/Home → Welcome ; còn lại → Home.
```

Các hàm điều hướng dùng `.first()` để lấy danh sách thẻ rồi gán `_currentScreen`:
```kotlin
fun startStudyDeck(deck: DeckEntity) {
    viewModelScope.launch {
        val cards = repository.getCardsForDeck(deck.id).first()
        _currentScreen.value = ScreenState.Study(deck, cards)
    }
}
```

**Vì sao dùng `.first()`?** `getCardsForDeck` trả `Flow`; `.first()` lấy giá trị đầu tiên rồi dừng — phù hợp cho "nạp một lần rồi chuyển màn hình". Nếu để `collect` liên tục sẽ phát lại mỗi khi thẻ thay đổi và có thể ghi đè màn hình đang xem.

---

## 4. MainViewModel chi tiết

### Khởi tạo
```kotlin
private val database = AppDatabase.getDatabase(application, viewModelScope)
private val repository = FlashCardRepository(database)
private val ttsManager = TTSManager(application)
private val smartNotificationEngine = SmartNotificationEngine(application)
```

### Toàn bộ StateFlow
| StateFlow | Khởi tạo | Ý nghĩa |
| --- | --- | --- |
| `currentScreen` | `Welcome` | Màn hình hiện tại |
| `selectedLanguage` | `ENGLISH` | Ngôn ngữ đang chọn |
| `learningLanguages` | `[ENGLISH]` | Ngôn ngữ đang học (in-memory) |
| `learningLanguagesFromDb` | `getAllLearningLanguages()` | Từ bảng `user_languages` |
| `dueCardsForCurrentLanguage` / `dueCountForCurrentLanguage` | `flatMapLatest` | Thẻ đến hạn ôn |
| `starterCardsForCurrentLanguage` | `flatMapLatest` | Từ mới (reviewCount=0) |
| `masteredCountForCurrentLanguage` | `flatMapLatest` | Từ đã thuộc theo ngôn ngữ |
| `userName` / `userVipLevel` / `streakDays` | `"Bạn Học"` / `1` / `7` | Thông tin người dùng |
| `notificationPreview` | `null` | Banner in-app |
| `decksForCurrentLanguage`, `allDecks`, `starredCardsList`, `allCardsList`, `masteredCount`, `totalCardsCount` | `stateIn` | Dữ liệu chính |

### Các hàm quan trọng
| Hàm | Tác dụng |
| --- | --- |
| `navigateTo`, `selectLanguage`, `addLearningLanguage` | Điều hướng & đổi ngôn ngữ (+ `switchActiveLanguage`) |
| `recordReview(cardId, difficulty)` | `recordCardReview` + cập nhật widget |
| `recordSrsReview(card, rating, isCorrect)` | SM-2 + cập nhật widget |
| `completeStudySession(...)` | Ghi `study_sessions` |
| `triggerSmartNotificationTest()` / `triggerAchievementTest(streak)` | Test notification → `notificationPreview` |
| `snoozeStudyReminderToday()` | `setSnoozedToday` + ẩn banner |
| `updateStudySchedule(hour, minute)` | `StudyAlarmScheduler.scheduleStudyAlarm` |
| `createNewDeckWithCards(deck, cards)` | Tạo deck + chèn thẻ đã chọn |
| `startStudySavedCards` / `startQuizSavedCards` / `startMatchSavedCards` | Deck tạm cho từ đã lưu |
| `openDeckDetail`, `startStudyDeck`, `startQuizDeck`, `startMatchDeck`, `openStarredCards`, `startStudyByLanguage` | Điều hướng |

### `init{}` & `onCleared()`
```kotlin
init {
    viewModelScope.launch {
        repository.checkAndSeedDatabase()
        VocabularyStreakWidgetProvider.updateAllWidgets(getApplication(), _streakDays.value)
        StudyAlarmScheduler.scheduleStudyAlarm(getApplication(), StudySchedule())
    }
}
override fun onCleared() { super.onCleared(); ttsManager.shutdown() }
```

**Vì sao `onCleared` phải `ttsManager.shutdown()`?** TTS giữ tài nguyên hệ thống; nếu không shutdown khi ViewModel huỷ (thoát app) sẽ rò rỉ bộ nhớ.

---

## 5. MainActivity — điều hướng & điều phối

- `onCreate`: `enableEdgeToEdge()` → `handleNotificationIntent(intent)` → `setContent`.
- `onNewIntent`: xử lý khi mở lại từ notification (extra `EXTRA_NAV_TARGET="HOME_STUDY"`).
- `onResume`: `NotificationHelper.clearAllNotifications` (xoá notification khi vào app).
- `NTKFlashCardApp(viewModel)`:
  - Thu thập StateFlow bằng `collectAsStateWithLifecycle()`.
  - `BackHandler` điều hướng Back.
  - `Crossfade(currentScreen)` + `when(screen)` hiển thị màn hình, nối callback tới `viewModel`.
  - **Banner thông báo in-app** (`AnimatedVisibility` slide+fade) hiển thị `notificationPreview`, tự ẩn sau 6 giây; nút "Học ngay"/"Để sau".
  - 3 dialog: `UserProfileDialog`, `CreateDeckDialog`, `CreateCardDialog`.
  - Xin quyền `POST_NOTIFICATIONS` (Android 13+) trước khi test notification.

**Vì sao dùng `Crossfade`?** Chuyển màn hình mượt (fade) khi `currentScreen` đổi, không cần thêm thư viện.

---

## 6. TTSManager — phát âm

`TTSManager` khởi tạo TTS (mặc định `Locale.US`), `speak(text, languageTag)` map Locale:

| Tiền tố | Locale |
| --- | --- |
| ko / ja / zh | KOREA / JAPAN / SIMPLIFIED_CHINESE |
| fr / de | FRANCE / GERMANY |
| es / vi | "es_ES" / "vi_VN" |
| mặc định | US |

Gọi `tts.speak(..., QUEUE_FLUSH, null, "NTK_SPEECH_...")`. `shutdown()` giải phóng.

**Vì sao dùng `QUEUE_FLUSH`?** Đảm bảo phát âm mới thay thế ngay câu đang đọc (tránh xếp hàng dồn khi bấm liên tục).

> **Khuyết điểm:** enum có `it`/`pt` nhưng TTS chưa map → rơi vào `else → US`. Cần bổ sung nếu giám khảo hỏi.

---

## 7. Hệ thống thông báo thông minh

### Luồng tổng thể
```
StudyAlarmScheduler.scheduleStudyAlarm (AlarmManager, code 8888)
        │ tới giờ
        ▼
StudyAlarmReceiver.onReceive (goAsync + coroutine)
        ▼
SmartNotificationEngine.evaluateAndSendSmartNotification
        ▼
NotificationHelper.showStudyReminderNotification (custom notification)
        ▼ bấm "Học ngay"
MainActivity (EXTRA_NAV_TARGET=HOME_STUDY)
```

### 7.1 `SmartNotificationEngine` — quyết định ngữ cảnh
- Bỏ qua nếu `isSnoozedToday` hoặc **đã học hôm nay** (`study_sessions` có bản ghi `>= startOfDay`), trừ `isForcedTest`.
- Đếm `dueWords` (thẻ chưa mastered) + lấy `streakDays` từ `user_profile`.
- Sinh title/message theo 4 kịch bản (có từ cần ôn + streak / chỉ từ / chỉ streak / chung).
- Gửi notification hệ thống + `onPreviewGenerated` (banner in-app).
- `checkAndNotifyStreakMilestone(newStreak)`: notification thành tựu khi đạt mốc streak.

**Vì sao phải có "engine" riêng?** Tách **quyết định** (khi nào gửi, nội dung gì) khỏi **hiển thị** (NotificationHelper) — dễ kiểm thử, dễ thêm rule mới; `StudyAlarmReceiver` chỉ đánh thức, không nhồi logic (đúng nguyên tắc thin receiver).

### 7.2 `NotificationHelper`
- 2 channel: `channel_study_reminder` & `channel_achievements` (IMPORTANCE_HIGH).
- Custom notification: small icon, large icon (mascot penguin), `BigTextStyle`, action "▶ Học ngay" + "⏰ Để sau".
- Snooze qua `SharedPreferences` ("ntk_flashcard_prefs", key `key_snoozed_study_date`).

**Vì sao dùng channel?** Android 8+ bắt buộc có channel để hiện notification; tách 2 channel để người dùng tắt riêng "nhắc học" mà không tắt "thành tựu".

### 7.3 `StudyAlarmScheduler` / `StudyAlarmReceiver` / `NotificationActionReceiver`
- **Scheduler:** `setExactAndAllowWhileIdle` (RTC_WAKEUP); nếu giờ đã qua → chuyển ngày mai; fallback `set()` khi thiếu `EXACT_ALARM`.
- **Receiver:** `goAsync()` + `CoroutineScope(IO)`, gọi engine rồi **lên lịch lại** ngày sau.
- **ActionReceiver:** `ACTION_SNOOZE_TODAY` (tắt notification + `setSnoozedToday` + Toast).

### Cấu hình AndroidManifest
Quyền `POST_NOTIFICATIONS`, `SCHEDULE_EXACT_ALARM`, `USE_EXACT_ALARM`, `RECEIVE_BOOT_COMPLETED`; khai báo 3 receiver.

---

## 8. Widget từ vựng

`VocabularyStreakWidgetProvider`:
- Layout `widget_vocabulary_streak.xml` + `xml/vocabulary_streak_widget_info.xml`.
- Hiển thị streak (từ `app_widget_prefs`) + một **từ chưa thuộc ngẫu nhiên** (Room `getAllCards().filter { !it.isMastered }`).
- Nút refresh (`ACTION_REFRESH_WORD`) và **tự làm mới mỗi 8 giây** (`startAutoScroll`).
- Bấm vào widget → mở `MainActivity`.

**Vì sao tự làm mới bằng coroutine + `sendBroadcast`?** `AppWidgetProvider` không có cơ chế tick sẵn (chỉ có `updatePeriodMillis` tối thiểu 30 phút). Vòng lặp `while(true) { delay(8000); sendBroadcast(...) }` cho phép làm mới nhanh hơn 30 phút để xoay vòng từ mới.

**Vì sao lưu streak vào `SharedPreferences` riêng (`app_widget_prefs`)?** Widget chạy ở process/system, không truy cập trực tiếp ViewModel/StateFlow; lưu streak vào prefs để provider đọc dễ dàng.

---

## 9. Nghiệp vụ ghi nhận học & SRS

- **Đánh giá đơn giản (đang dùng):** UI → `onRecordReview(id, difficulty)` → `viewModel.recordReview` → `repository.recordCardReview` (`isMastered = difficulty==1`).
- **SRS SM-2 (đã cài, chưa nối UI):** `viewModel.recordSrsReview(card, rating, isCorrect)` → `repository.recordSrsReview`.
- **Hoàn thành phiên:** `onSessionFinished(count, mastered)` → `viewModel.completeStudySession` ghi `study_sessions`.
- Sau mỗi review, ViewModel gọi `updateAllWidgets` đồng bộ streak.

**Vì sao tách `recordReview` và `recordSrsReview`?** `recordReview` đủ cho nhu cầu hiện tại (đánh dấu thuộc/chưa thuộc); `recordSrsReview` là thuật toán SM-2 đầy đủ để sẵn sàng mở rộng — tách riêng để không làm rối luồng đang chạy.

---

## 10. Phân định rõ: logic nằm ở đâu

| Logic | Vị trí | File |
| --- | --- | --- |
| Điều hướng | ViewModel + MainActivity | `MainViewModel`, `MainActivity` |
| Đáp án nhiễu Quiz / điểm / pháo hoa | UI (màn hình) | `ui/quiz/QuizScreen.kt` |
| Ghép cặp Match | UI (màn hình) | `ui/match/WordMatchScreen.kt` |
| Ghi nhận SRS | ViewModel → Repository → DAO | `MainViewModel`, `FlashCardRepository` |
| Phiên học / quiz record | ViewModel → Repository → DAO | `MainViewModel`, `FlashCardRepository` |
| Phát âm | TTSManager | `audio/TTSManager.kt` |
| Thông báo | notification package | `SmartNotificationEngine`, `NotificationHelper`, ... |
| Widget | widget package | `VocabularyStreakWidgetProvider` |

---

## 11. Cẩm nang sửa đổi Logic

| Yêu cầu | Nơi sửa |
| --- | --- |
| Thêm màn hình | case `ScreenState` + hàm `MainViewModel` + `when` `MainActivity` |
| Đổi luồng Back | `BackHandler` trong `MainActivity` |
| Kích hoạt SRS SM-2 | `recordReview` → `recordSrsReview(card, rating, isCorrect)` |
| Sửa giờ nhắc | `updateStudySchedule` + `StudySchedule` default |
| Sửa nội dung thông báo | `SmartNotificationEngine` + `NotificationHelper` |
| Sửa widget | `VocabularyStreakWidgetProvider` + `res/layout/widget_*` |
| Nối đăng nhập DB | `LoginScreen`/`RegisterScreen` + ViewModel + `UserAccountDao` |
| Đổi giọng TTS | `audio/TTSManager.kt` (bổ sung it/pt) |
| Làm streak "thật" | `UserProfileDao.updateStreak` + tính chuỗi ngày |

---

## 12. Q&A mở rộng — trả lời giám khảo

### Q1. Vì sao điều hướng bằng sealed class mà không dùng Navigation Compose?
Xem mục 2.5. Truyền dữ liệu trực tiếp qua data class, trạng thái nằm trong `StateFlow`, dễ kiểm soát Back. Với app nhỏ thì đơn giản và đủ.

### Q2. Vì sao dùng `StateFlow` thay vì `LiveData`? Vì sao `flatMapLatest`?
StateFlow là API coroutine gốc, nhiều toán tử, dễ test. `flatMapLatest` đảm bảo khi đổi ngôn ngữ, Flow cũ bị huỷ và Flow mới chạy, tránh race condition.

### Q3. Hệ thống nhắc học hoạt động ra sao?
`AlarmManager` hẹn giờ → `StudyAlarmReceiver` (goAsync) → `SmartNotificationEngine` kiểm tra ngữ cảnh (đã hoãn? đã học?) → nếu hợp lệ thì `NotificationHelper` gửi; bấm "Học ngay" mở app, "Để sau" hoãn trong ngày.

### Q4. Vì sao dùng AlarmManager mà không dùng WorkManager?
Cần hẹn giờ **chính xác** (đúng 19:00) → `setExactAndAllowWhileIdle` phù hợp. WorkManager dành cho tác vụ nền không cần chính xác.

### Q5. Vì sao receiver dùng `goAsync()`?
`onReceive` giới hạn ~10s trên Main Thread; `goAsync()` cho phép chạy coroutine query DB rồi `finish()`, tránh bị giết giữa chừng.

### Q6. Vì sao lưu cờ "Để sau" bằng SharedPreferences mà không dùng Room?
Cờ nhỏ, tạm thời, không quan hệ → SharedPreferences đơn giản, đủ. Room dành cho dữ liệu có cấu trúc.

### Q7. Widget làm mới 8 giây bằng cơ chế gì?
Vòng lặp coroutine `while(true){ delay(8000); sendBroadcast(ACTION_REFRESH_WORD) }` vì `updatePeriodMillis` của widget tối thiểu 30 phút.

### Q8. Làm sao phát âm offline?
Dùng `TextToSpeech` của hệ điều hành, map mã ngôn ngữ sang `Locale`, `QUEUE_FLUSH` để phát mới ngay. Không gọi mạng.

### Q9. `recordReview` và `recordSrsReview` khác gì?
`recordReview` đánh dấu thuộc/chưa thuộc + tăng `reviewCount`. `recordSrsReview` chạy SM-2 đầy đủ (interval/ease factor/repetitions/nextReview). Hiện UI gọi `recordReview`; SM-2 sẵn sàng để kích hoạt.

### Q10. Vì sao phải `ttsManager.shutdown()` trong `onCleared`?
TTS giữ tài nguyên hệ thống; shutdown tránh rò rỉ bộ nhớ khi ViewModel bị huỷ.

### Q11. Vì sao banner in-app tự ẩn sau 6 giây?
Dùng `LaunchedEffect(notificationPreview)` + `delay(6000)` + `dismissNotificationPreview()` để không chiếm màn hình lâu; `LaunchedEffect` tự huỷ khi key đổi.

### Q12. Vì sao sau mỗi review lại gọi `updateAllWidgets`?
Widget không quan sát StateFlow trực tiếp, nên ViewModel chủ động đẩy streak mới vào `app_widget_prefs` + broadcast để widget tự làm mới.
