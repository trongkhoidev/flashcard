# DOC_KHOI — Tài liệu thành viên Khôi: UI/UX & Giao diện (Jetpack Compose)

> **Vai trò:** Bạn phụ trách toàn bộ "bộ mặt" ứng dụng — mọi màn hình, component tái sử dụng, theme và dialog bằng **Jetpack Compose**. Tài liệu này không chỉ liệt kê mà còn giải thích **vì sao** dùng cách này (mà không dùng cách khác) và **triển khai** từng hiệu ứng/nghiệp vụ UI như thế nào, để bạn trả lời trọn vẹn mọi câu hỏi của giám khảo.

---

## Mục lục

1. [Phạm vi & thư mục](#1-phạm-vi--thư-mục)
2. [Công nghệ & vì sao chọn](#2-công-nghệ--vì-sao-chọn)
3. [Theme](#3-theme)
4. [Components tái sử dụng](#4-components-tái-sử-dụng)
5. [Màn hình Welcome / Login / Register / Onboarding](#5-màn-hình-welcome--login--register--onboarding)
6. [HomeScreen & HomeComponents](#6-homescreen--homecomponents)
7. [DeckDetailScreen](#7-deckdetailscreen)
8. [FlashcardStudyScreen](#8-flashcardstudyscreen)
9. [QuizScreen](#9-quizscreen)
10. [WordMatchScreen](#10-wordmatchscreen)
11. [Leaderboard & VIP](#11-leaderboard--vip)
12. [Dialogs](#12-dialogs)
13. [Cơ chế nhận dữ liệu & phát sự kiện](#13-cơ-chế-nhận-dữ-liệu--phát-sự-kiện)
14. [Tổng hợp hiệu ứng & cách triển khai](#14-tổng-hợp-hiệu-ứng--cách-triển-khai)
15. [testTag](#15-testtag)
16. [Cẩm nang sửa đổi](#16-cẩm-nang-sửa-đổi)
17. [Q&A mở rộng — trả lời giám khảo](#17-qa-mở-rộng--trả-lời-giám-khảo)

---

## 1. Phạm vi & thư mục

Bạn sở hữu toàn bộ `ui/` (gốc `app/src/main/java/com/example/`):

```
ui/
├── theme/          → Color.kt, Type.kt, Theme.kt
├── components/     → VipAvatarFrame, Flashcard3DView, OwlMascotView,
│                     LanguageSpeechBubble, GlowingCardsHeader, LaurelWreathHeader
├── welcome/        → WelcomeScreen, LoginScreen, RegisterScreen, OnboardingStepsScreen
├── home/           → HomeScreen, HomeComponents
├── detail/         → DeckDetailScreen
├── study/ quiz/ match/
├── leaderboard/    → LeaderboardTab
└── dialogs/        → HomeDialogs, UserProfileDialog, CreateDeckAndCardDialogs
```

---

## 2. Công nghệ & vì sao chọn

### 2.1 Vì sao dùng Jetpack Compose thay vì XML Layout?
- **Khai báo (declarative):** UI là hàm `@Composable` phụ thuộc trực tiếp vào state; state đổi → Compose tự **recomposition** phần thay đổi. XML (imperative) bắt em tự `findViewById` và cập nhật từng View thủ công, dễ lỗi, nhiều code "glue".
- **Ít boilerplate:** không cần `RecyclerView.Adapter`/`ViewHolder` — dùng `LazyColumn`/`LazyVerticalGrid` với lambda `items`.
- **Animation đơn giản:** `animate*AsState`, `AnimatedVisibility`, `AnimatedContent` thay cho `ObjectAnimator`/`AnimatorSet`.
- **Kiểm thử dễ:** có `testTag` + Compose UI Test (đã cấu hình Roborazzi/Robolectric).

### 2.2 Vì sao dùng "State Hoisting" (nâng state lên)?
Trạng thái giữ ở màn hình cha, truyền xuống qua tham số; sự kiện phát ngược bằng **lambda callback**:
1. **Tái sử dụng:** `Flashcard3DView` dùng cho cả Study lẫn Starred.
2. **Dễ test:** truyền dữ liệu/callback giả lập.
3. **Tránh "state ẩn":** ví dụ `isFlipped` không nằm trong `Flashcard3DView` mà do `FlashcardStudyScreen` giữ, nên auto-play có thể lật thẻ từ bên ngoài.

### 2.3 Vì sao dùng `remember`/`mutableStateOf` cho state cục bộ?
State chỉ sống trong một màn hình (chỉ số trang carousel, ô tìm kiếm, tab đang chọn...) **không cần** sống sót qua vòng đời hay chia sẻ cho màn hình khác → dùng `remember` là đủ. Ngược lại, dữ liệu nghiệp vụ (danh sách thẻ, ngôn ngữ, tên người dùng) đặt trong `MainViewModel` vì phải chia sẻ nhiều màn hình.

### 2.4 Vì sao dùng Coil? Vì sao dùng Lazy*?
- **Coil:** nhẹ, API gọn (`AsyncImage`), có `crossfade` + `ContentScale`; tương lai đổi sang URL chỉ cần đổi `model`, không đổi cấu trúc.
- **`LazyColumn`/`LazyVerticalGrid`:** chỉ render item đang hiển thị (lazy), tiết kiệm bộ nhớ với danh sách dài. `Column` render hết một lần, dễ lag/tràn bộ nhớ.

### 2.5 Vì sao vẽ cờ (Mỹ/Hàn/địa cầu) bằng Canvas thay vì ảnh?
Vì các hình này nhỏ (56dp), vẽ vector bằng `Canvas.drawRect/drawCircle/drawArc/drawLine` vừa sắc nét trên mọi mật độ màn hình, vừa không cần thêm file ảnh tăng dung lượng APK. `JapaneseFujiArt` thì vẫn dùng ảnh thật vì cảnh phức tạp.

---

## 3. Theme

### 3.1 `Color.kt` — palette "Squirtle" (xanh đại dương)

| Nhóm | Tên | Giá trị |
| --- | --- | --- |
| Brand | `NTKPrimary` | `0xFF0284C7` |
| Brand | `NTKPrimaryDark` / `NTKPrimaryLight` | `0xFF0369A1` / `0xFF38BDF8` |
| Brand | `NTKSecondary` / `NTKTertiary` | `0xFF0EA5E9` / `0xFF06B6D4` |
| Bubble | `BubbleEnglish`...`BubblePortuguese` | 10 màu |
| Surface | `NTKBackgroundLight` `0xFFF0F9FF`, `NTKSurfaceLight`, `NTKCardBorder` `0xFFE0F2FE` | — |
| Text | `NTKTextPrimary` `0xFF0F172A`, `NTKTextSecondary` `0xFF475569`, `NTKTextMuted` | — |
| Feedback | `EasyGreen` `0xFF10B981`, `GoodYellow` `0xFFF59E0B`, `HardRed` `0xFFEF4444` | — |

> Brand đã chuyển từ tím sang **xanh cyan**. Vẫn còn hex cứng rải rác trong code; khi đổi màu phải rà cả hai.

### 3.2 `Type.kt` & `Theme.kt`
`Typography` chỉ override `bodyLarge`. `MyApplicationTheme(darkTheme=false, dynamicColor=false)` định nghĩa `LightColorScheme`/`DarkColorScheme` + hỗ trợ dynamic color (Android 12+). Ứng dụng đang chạy **Light**.

---

## 4. Components tái sử dụng

### 4.1 `VipAvatarFrame.kt` — hệ thống VIP 8 cấp

```kotlin
enum class VipLevel(levelNumber, title, badgeLabel, crownEmoji, badgeBgColor, badgeTextColor, gradientColors)
// NONE(0) → VIP7(7), companion fromLevel(level)
@Composable fun VipAvatarFrame(vipLevel, avatarSize = 58.dp, content)
@Composable fun VipLevelSelectorCard(currentVipLevel, onSelectVipLevel)
@Composable fun VipCardFrame(userVipLevel, cornerRadius = 24.dp, content)
```

**Triển khai chi tiết:**
- `VipAvatarFrame` xếp 3 lớp bằng `Box`: (1) aura `Brush.radialGradient` phát sáng, (2) vòng `Brush.sweepGradient` xoay bằng `graphicsLayer.rotationZ`, (3) nội dung avatar + crown + badge "VIP n". Dùng `rememberInfiniteTransition` cho 3 animation (xoay `LinearEasing + Restart`, pulse alpha `Reverse`, pulse scale). Tốc độ theo cấp: VIP1 xoay 4200ms → VIP7 800ms, viền 2dp → 5.5dp.
- `VipCardFrame` vẽ viền có "tia sáng chạy": dùng `drawBehind` + `PathMeasure` trên rounded rect, `beamCount` 1-4 và `beamLengthRatio` thay đổi theo cấp.

**Vì sao dùng enum + hàm `fromLevel`?** Để chỗ gọi chỉ cần truyền số (`userVipLevel: Int`) mà vẫn có kiểu an toàn; dễ thêm cấp mới bằng cách thêm 1 dòng enum.

### 4.2 `Flashcard3DView.kt` — thẻ lật 3D

```kotlin
@Composable fun Flashcard3DView(card, isFlipped, userVipLevel = 0, onFlip, onSpeak, onToggleStar)
```

**Triển khai:**
1. `val rotationY by animateFloatAsState(targetValue = if (isFlipped) 180f else 0f, tween(400))`.
2. Root `Box` dùng `Modifier.graphicsLayer { this.rotationY = rotationY; cameraDistance = 12f * density }` → có phối cảnh 3D.
3. `if (rotationY <= 90f)` vẽ `CardFrontSide`, ngược lại vẽ `CardBackSide` (được xoay bù `rotationY = 180f` để chữ không bị lộn ngược).
4. `CardFrontSide`: badge loại từ, nút phát âm, nút sao, từ + phiên âm + ví dụ. `CardBackSide`: nghĩa + dịch + mẹo nhớ.
5. Mặt thẻ bọc `VipCardFrame` theo `userVipLevel`.

**Vì sao dùng `cameraDistance = 12f * density`?** Tạo độ sâu phối cảnh cho phép quay 3D; nếu thiếu, thẻ chỉ "co ngang" như ép 2D.

### 4.3 Các component khác
- `OwlMascotView` — linh vật cú + 4 bubble ngôn ngữ (en/ja/ko/vi), `onLanguageClick(code)`.
- `LanguageSpeechBubble` — bubble có đuôi vẽ bằng `Canvas` (Path tam giác), lơ lửng `infiniteRepeatable`.
- `GlowingCardsHeader` — cụm 3 thẻ phát sáng + sparkle.
- `LaurelWreathHeader` — tên app + slogan + vòng nguyệt quế vẽ `Canvas`.

---

## 5. Màn hình Welcome / Login / Register / Onboarding

### 5.1 `WelcomeScreen`
```kotlin
fun WelcomeScreen(onStartLearning, onLoginClick, onSelectLanguage: (String) -> Unit = {})
```
- Carousel 3 trang: `currentPage` + `LaunchedEffect(Unit)` tự tăng sau `delay(3500)`, `AnimatedContent` slide+fade; dot indicator co giãn bằng `animateDpAsState` (8dp ↔ 24dp).
- Nút "Bắt đầu học ngay" (`start_learning_button`) → `onStartLearning` (giờ đi `Onboarding`); "Đăng nhập" → `onLoginClick`; chọn bubble ngôn ngữ → `onSelectLanguage(code)`.

### 5.2 `LoginScreen`
```kotlin
fun LoginScreen(onLoginSuccess: (String) -> Unit, onBackToWelcome, onNavigateToRegister)
```
- Form username/password (ẩn/hiện), "Ghi nhớ đăng nhập", "Quên mật khẩu?" (Toast).
- Đăng nhập → `onLoginSuccess(username)` (rỗng → fallback `"Học viên NTK"`). **Chưa nối DB.**

### 5.3 `RegisterScreen`
```kotlin
fun RegisterScreen(onRegisterSuccess: (String) -> Unit, onBackToWelcome, onNavigateToLogin)
```
- Validate: username 4-20 ký tự không khoảng trắng; password ≥8 (thanh độ mạnh 1-4); xác nhận khớp. Thành công → `onRegisterSuccess(username)`.

**Vì sao validate ngay trên UI?** Vì chưa có backend nên cần phản hồi tức thì; `isUsernameValid`/`isPasswordValid`/`isPasswordsMatch` là các biểu thức dẫn xuất (derived state) tính lại mỗi lần recomposition, không cần lưu thêm state.

### 5.4 `OnboardingStepsScreen` (7 bước)
```kotlin
fun OnboardingStepsScreen(onCompleteOnboarding: (AppLanguage, Int) -> Unit, onBackToWelcome)
```
| Bước | Composable | Thu thập |
| --- | --- | --- |
| 1 | `StepLanguageSelection` | Ngôn ngữ (9 lựa chọn, mặc định English) |
| 2 | `StepLevelSelection` | Cấp độ (beginner/elementary/intermediate/advanced) |
| 3 | `StepTopicSelection` | Chủ đề (8, đa chọn) |
| 4 | `StepTimeSelection` | Khung giờ (7 slot, đa chọn) |
| 5 | `StepNotificationPermission` | Quyền POST_NOTIFICATIONS |
| 6 | `StepAddWidgetHomeScreen` | Thêm widget + xem trước từ mẫu |
| 7 | `StepMascotPreparingFlashcards` | Mascot loading → `onReady` |

- `currentStep` (1-7) hiển thị qua `AnimatedContent` (slide+fade, hướng tiến/lùi).
- `primaryHour = timeSlots.firstOrNull { selectedTimeSlots.contains(it.id) }?.defaultHour ?: 19`.
- Hoàn thành → `onCompleteOnboarding(selectedLanguage, primaryHour)`.

**Vì sao tách 7 bước?** Mỗi bước chỉ thu thập một loại thông tin → đơn giản, giảm "ma sát" cho người dùng lần đầu, và mỗi bước là một composable riêng dễ bảo trì/test.

---

## 6. HomeScreen & HomeComponents

### 6.1 `HomeScreen` (4 tab dưới + overlay Ôn tập)
```kotlin
fun HomeScreen(selectedLanguage, onSelectLanguage, learningLanguages, onAddLearningLanguage,
    decks, allDecksList, starredCards, allCardsList, streakDays, masteredWordsCount, totalWordsCount,
    userName, userVipLevel, onSelectVipLevel, onOpenDeckDetail, onStudyDeck, onQuizDeck, onMatchDeck,
    onAddCardToDeck, onCreateNewDeck, onOpenProfile, onOpenStarred, onSpeak, onToggleStar,
    onStartStudySaved, onStartQuizSaved, onStartMatchSaved, onCreateDeckDirect, onImportCardsDirect,
    onStudyByLang, onTestSmartNotification, onTestMilestoneNotification)
```

**Bottom nav (`HomeBottomNavBar`):** Trang chủ(0) / Khám phá(1) / BXH(2) / Tài khoản(3).

| Tab | Nội dung |
| --- | --- |
| 0 Trang chủ | `HomeTopHeader` → `HomeSearchBar` → (search) `SearchResultsView` / (thường) `StarterWelcomeHeroCard` hoặc `StreakMascotBanner` → `QuickActionGrid` → `ContinueLearningSection` → `SpacedRepetitionDueWidget` → `YourDecksSection` → `DailyGoalCard` |
| 1 Khám phá | `ExploreDecksTab` |
| 2 BXH | `LeaderboardTab` |
| 3 Tài khoản | `AccountProfileTab` |

- **"Ôn tập" không còn là tab** — là overlay toàn màn hình `showReviewOverlay` mở từ Quick Action "Ôn tập".
- `QuickActionGrid`: Tạo bộ thẻ / **Ôn tập** / Thống kê / Đã lưu.
- Tìm kiếm: lọc `title`/`subtitle`/`level` không phân biệt hoa thường.
- `selectedBottomTab` có thể nhận `-1` khi overlay Ôn tập mở (để không tab nào active).

**Vì sao tách HomeComponents ra file riêng?** `HomeScreen` rất lớn; tách các khối UI (header, banner, card...) thành `HomeComponents.kt` giúp dễ đọc, dễ tái sử dụng và giảm phạm vi recomposition.

### 6.2 Các composable chính trong `HomeComponents.kt`
`HomeTopHeader`, `HomeSearchBar`, `StreakMascotBanner`, `QuickActionGrid`, `StarterWelcomeHeroCard`, `ContinueLearningSection`, `SpacedRepetitionDueWidget`, `YourDecksSection`, `DynamicDeckCard`, `CreateNewDeckCard`, `AddLanguageBottomSheet`, `DailyGoalCard`, `HomeBottomNavBar`. Minh hoạ vẽ Canvas: `JapaneseFujiArt`, `UsFlagArt`, `KoreaFlagArt`, `GlobeArt`.

**Vì sao `DailyGoalCard` vẽ vòng tròn % bằng Canvas?** `drawArc` + `Brush.sweepGradient` + `StrokeCap.Round` cho phép tuỳ chỉnh gradient tròn mượt, không phụ thuộc drawable tĩnh.

---

## 7. DeckDetailScreen

```kotlin
fun DeckDetailScreen(deck, cards, onBack, onStartStudy, onStartQuiz, onStartMatch, onSpeak, onToggleStar)
```
- Hero: ảnh bìa (`DeckCoverImageCard` theo ngôn ngữ), tag ngôn ngữ, rating 4.9, số thẻ, avatar, tag chips (`FlowRow`).
- Nút "Học ngay"; menu ⋮ (Quiz/Match).
- **2 tab:** Nội dung (chủ đề `DeckTopic` mở rộng + sample + 2 card `btn_quiz_mode_card`/`btn_match_mode_card`) & Thống kê (`StatsDetailTab`).

> `DeckTopic` và danh sách chủ đề là **hardcode theo ngôn ngữ**; nút yêu thích chỉ đổi icon + Toast (chưa gọi `onToggleStar`).

---

## 8. FlashcardStudyScreen

```kotlin
fun FlashcardStudyScreen(deckTitle, languageTag, cards, userVipLevel = 0, onBack, onSpeak,
    onToggleStar, onRecordReview, onStartQuiz, onSessionFinished: ((Int, Int) -> Unit)? = null)
```
- 3 nút hành động: Trộn thẻ / Lưu từ điển (sao) / đổi mặt.
- 2 nút đánh giá: **"Chưa thuộc"** (`onRecordReview(id, 3)`) và **"Đã thuộc"** (`onRecordReview(id, 1)`, tự chuyển thẻ).
- **Auto-play** (`LaunchedEffect(isAutoPlay, currentIndex, isFlipped, isCompleted)`): chưa lật → đọc `frontWord` + `delay(2600)` → lật; đã lật → `delay(2200)` → thẻ tiếp/hoàn thành.
- Overlay hoàn thành → `onStartQuiz`; `LaunchedEffect(isCompleted)` gọi `onSessionFinished`.

**Vì sao dùng `LaunchedEffect` cho auto-play/toast?** `LaunchedEffect` chạy coroutine gắn với vòng đời composable: khi key (danh sách tham số) đổi hoặc composable rời khỏi cây, coroutine tự huỷ → không bị rò rỉ `delay`.

---

## 9. QuizScreen

```kotlin
fun QuizScreen(deckTitle, languageTag, cards, onBack, onSpeak, onFinishQuiz(score, total), onStudyNext = null)
```
- `quizCards = cards.shuffled()`; mỗi câu `currentOptions = (wrongOptions + correct).shuffled()` với `wrongOptions` = 3 `backMeaning` của các thẻ khác.
- **Điểm & chuỗi:** đúng → `score++`, `currentStreak++`, `totalPoints += 100 * multiplier`; `getStreakMultiplierInfo(streak)` trả hệ số 1.0→5.0; sai → reset chuỗi.
- Popup điểm nổi (`Animatable`), **pháo hoa `FireworksCanvas`**, overlay kết quả.

**Vì sao tạo đáp án nhiễu từ chính `backMeaning` của bộ thẻ?** Vì nhiễu "cùng ngữ cảnh" (các nghĩa cùng chủ đề) khiến câu hỏi khó hơn và hợp lý hơn so với sinh ngẫu nhiên từ một danh sách không liên quan; đồng thời không cần thêm bảng "từ nhiễu".

**Vì sao có streak multiplier?** Tạo động lực trả lời liên tiếp đúng (gamification); hệ số nhân là logic thuần UI (không lưu DB), nằm ở `getStreakMultiplierInfo`.

---

## 10. WordMatchScreen

```kotlin
fun WordMatchScreen(deckTitle, cards, onBack)
```
- `gameCards = cards.take(6)`; tạo cặp từ↔nghĩa (`MatchItem(id, text, isFront, pairId)`), xáo trộn, `LazyVerticalGrid(GridCells.Fixed(2))`.
- Chọn 2 thẻ: đúng khi `pairId` khớp và khác `isFront`; đếm lượt thử; thắng khi ghép đủ.

**Vì sao giới hạn 6 thẻ?** Để màn chơi ngắn gọn trên màn hình nhỏ; `cards.take(6)` đủ 12 ô (6 cặp) hiển thị vừa lưới 2 cột mà không cần cuộn.

---

## 11. Leaderboard & VIP

### `LeaderboardTab` (tab BXH)
```kotlin
fun LeaderboardTab(userName, userVipLevel, userScore, userStreak, userCardsLearned)
```
- **Dữ liệu tĩnh (hardcode):** 10 user mẫu + bản thân (rank 23), nhân theo `TimePeriod` (Tuần/Tháng/Tất cả).
- Lọc `LeaderboardFilterType` (Tổng điểm/Chuỗi ngày/Số thẻ), top-3 `PodiumStep`, rank 4-10 `LeaderboardRowItem`, thưởng `RewardCard`.

**Vì sao hiện dùng dữ liệu tĩnh?** Vì chưa có backend; cấu trúc `LeaderboardUser` + bộ lọc đã tách bạch, nên khi có nguồn thật chỉ cần thay phần dữ liệu bằng query từ `UserProfileDao`/`QuizRecordDao` mà không phải sửa giao diện.

---

## 12. Dialogs

- **`ImportCardsDialog(decks, onDismiss, onImportCards(deckId, cards))`** — nhập `Từ | Nghĩa | Ví dụ`; hardcode `languageCode="fr"`, `partOfSpeech="phrase"`.
- **`StatsSummaryDialog(streakDays, masteredCount, totalCardsCount, onDismiss)`** — 4 StatCard + biểu đồ tuần (hardcode).
- **`SavedCardsDialog(starredCards, decks, onSpeak, onToggleStar, onStartStudy/Quiz/Match, onDismiss)`** — lọc ngôn ngữ/chủ đề + tìm kiếm + `SavedWordDetailCard`.
- **`UserProfileDialog(userName, userVipLevel, streakDays, masteredWordsCount, totalWordsCount, onDismiss, onUpdateName, onSelectVipLevel)`** — avatar VIP, đổi tên, chọn VIP, thống kê.
- **`CreateCardDialog(deckId, languageCode, onDismiss, onSave(FlashCardEntity))`** — form từ vựng.
- **`CreateDeckDialog(currentLanguageCode, allCards, onDismiss, onSave(DeckEntity, List<FlashCardEntity>))`** — tạo bộ thẻ + chọn thẻ từ 2 tab "⭐ Đã lưu"/"💡 Chưa thuộc".

**Vì sao `ModalBottomSheet` cho lọc ngôn ngữ/xem tất cả, nhưng `Dialog` cho form?** Bottom sheet hợp với thao tác chọn nhanh/duyệt danh sách (giữ ngữ cảnh); Dialog hợp với form nhiều trường cần tập trung và có nút huỷ/xác nhận.

---

## 13. Cơ chế nhận dữ liệu & phát sự kiện

Screens **không** tự gọi ViewModel. Mẫu trong `MainActivity`:
```kotlin
QuizScreen(
    deckTitle = screen.deck.title,
    languageTag = screen.deck.languageCode,
    cards = screen.cards,
    onBack = { viewModel.navigateTo(ScreenState.Home) },
    onSpeak = { text, tag -> viewModel.speak(text, tag) },
    onFinishQuiz = { score, total -> viewModel.completeStudySession(...) },
    onStudyNext = { viewModel.startStudyDeck(screen.deck) }
)
```
→ UI là "da", ViewModel là "xương" — điểm nhấn khi giải thích kiến trúc.

---

## 14. Tổng hợp hiệu ứng & cách triển khai

| Hiệu ứng | API | Cách làm chi tiết |
| --- | --- | --- |
| Lật thẻ 3D | `graphicsLayer` + `animateFloatAsState` | `rotationY` 0↔180, `cameraDistance = 12f*density`, ngưỡng 90f đổi mặt |
| Khung VIP | `rememberInfiniteTransition` + `PathMeasure` | 3 lớp: aura, vòng sweep xoay, nội dung; tia sáng chạy trên viền |
| Pháo hoa | `Canvas` + `withFrameNanos` | hạt có vận tốc/trọng lực 0.25/lực cản 0.96/alpha giảm, ≤90 hạt, 8s |
| Popup điểm | `Animatable` | pop (scale 0.7→1.14→1.0) → giữ 900ms → bay lên mờ dần |
| Carousel/step | `AnimatedContent` | slide+fade, tự chuyển 3.5s |
| Overlay hiện | `AnimatedVisibility` | `fadeIn + scaleIn` |
| Mở rộng chủ đề | `animateContentSize` | `expandVertically/shrinkVertically` |
| Banner in-app | `slideInVertically` + `fadeIn` | từ trên xuống, tự ẩn 6s |
| Lơ lửng | `infiniteRepeatable` | `animateFloat` đảo chiều |

---

## 15. testTag

```
Welcome: start_learning_button, login_button
Login: login_username_input, login_password_input, submit_login_button, register_link
Register: username_input, password_input, confirm_password_input, register_button, login_link
Onboarding: onboarding_next_button, onboarding_back_button, enable_notification_button,
            skip_notification_button, add_widget_button, skip_widget_button, onboarding_start_learning_button
Home: streak_badge, home_search_input, streak_mascot_banner, quick_create_deck, quick_review_cards,
      quick_stats, quick_saved, starter_welcome_hero_card, btn_start_first_lesson, continue_learning_card,
      btn_continue_study, srs_due_review_card, lang_chip_<code>, btn_add_language_chip, deck_filter_<f>,
      card_create_new_deck, deck_item_card_<id>, add_lang_item_<code>, daily_goal_card,
      tab_home, tab_explore, tab_leaderboard, tab_profile
Study: btn_back_study, btn_shuffle_cards, btn_save_dictionary, btn_flip_indicator,
       btn_not_memorized, btn_memorized, btn_prev_card, btn_next_card, flashcard_3d_container,
       btn_pronounce_front, btn_star_card
Quiz: quiz_option_<text>, btn_next_quiz, btn_study_next_card
Match: match_card_<id>
DeckDetail: btn_quiz_mode_card, btn_match_mode_card
Leaderboard: leaderboard_screen, time_period_dropdown
Dialogs: input_front_word, input_back_meaning, btn_save_card, input_deck_title, btn_save_deck,
         btn_close_profile, saved_cards_search_input, saved_word_card_<id>, filter_lang_all, filter_lang_<code>
```

**Vì sao đặt `testTag`?** Để test UI (Robolectric/Roborazzi/Espresso) định vị chính xác node mà không phụ thuộc text hiển thị (có thể đổi ngôn ngữ).

---

## 16. Cẩm nang sửa đổi

| Yêu cầu | Nơi sửa |
| --- | --- |
| Đổi màu | `ui/theme/Color.kt` (+ rà hex cứng) |
| Đổi font | `ui/theme/Type.kt` |
| Thêm màn hình | composable + case `ScreenState` + `when` `MainActivity` + hàm `MainViewModel` |
| Sửa home | `ui/home/HomeScreen.kt` + `HomeComponents.kt` |
| Sửa thẻ học | `ui/study/FlashcardStudyScreen.kt` + `Flashcard3DView.kt` |
| Sửa quiz | `ui/quiz/QuizScreen.kt` |
| Sửa ghép từ | `ui/match/WordMatchScreen.kt` |
| Sửa VIP | `components/VipAvatarFrame.kt` |
| Sửa BXH | `ui/leaderboard/LeaderboardTab.kt` |
| Sửa dialog | `ui/dialogs/*` |
| Đổi ảnh | `res/drawable/*` |

---

## 17. Q&A mở rộng — trả lời giám khảo

### Q1. Vì sao chọn Compose mà không dùng XML?
Xem mục 2.1. Tóm gọn: declarative + tự recomposition + ít boilerplate + animation/kiểm thử tiện.

### Q2. Hiệu ứng lật thẻ 3D làm thế nào?
`graphicsLayer.rotationY` + `animateFloatAsState` (0↔180°, tween 400ms), `cameraDistance = 12f*density` tạo phối cảnh; `rotationY <= 90f` hiện mặt trước, ngược lại mặt sau (xoay bù 180°).

### Q3. Khung VIP em làm thế nào?
Enum `VipLevel` (0-7), mỗi cấp có gradient/badge/crown/tốc độ animation. `VipAvatarFrame` xếp 3 lớp (aura phát sáng + vòng sweep xoay `rotationZ` + nội dung) với `rememberInfiniteTransition`; `VipCardFrame` vẽ tia sáng chạy bằng `PathMeasure`.

### Q4. Pháo hoa trong quiz làm thế nào?
`FireworksCanvas` dùng `Canvas` + `withFrameNanos` mô phỏng vật lý hạt: mỗi hạt có vị trí/vận tốc/trọng lực (+0.25)/lực cản (*0.96)/vòng đời, 3 hình dạng (tròn/vuông/sao), tối đa 90 hạt, chạy 8s rồi `onFinished`.

### Q5. Điểm & streak multiplier hoạt động ra sao?
Đúng → `score++`, `currentStreak++`, `totalPoints += 100 * multiplier`; `getStreakMultiplierInfo` trả 1.0→5.0 theo chuỗi; sai → reset chuỗi. Là gamification thuần UI, không lưu DB.

### Q6. Vì sao auto-play/onboarding loading dùng `LaunchedEffect`?
`LaunchedEffect` gắn coroutine với vòng đời composable, tự huỷ khi key đổi hoặc composable rời cây → không rò rỉ `delay`.

### Q7. Vì sao dùng `remember` cho state cục bộ thay vì đưa hết vào ViewModel?
Chỉ state cần sống lâu/chia sẻ nhiều màn hình mới đưa vào ViewModel; state dùng tạm trong một màn hình thì `remember` gọn và đúng chỗ, tránh ViewModel phình to.

### Q8. Bảng xếp hạng lấy dữ liệu từ đâu?
Hiện dùng dữ liệu tĩnh (10 user mẫu + bản thân, nhân theo khoảng thời gian). Cấu trúc đã tách bạch nên khi có dữ liệu thật chỉ cần nối `UserProfileDao`/`QuizRecordDao` mà không sửa giao diện.

### Q9. Vì sao dùng `LazyVerticalGrid` cho ghép từ?
Vì lưới ô ghép từ có số ô thay đổi (12 ô), `LazyVerticalGrid(GridCells.Fixed(2))` tự chia cột và render lazy, tiết kiệm bộ nhớ.

### Q10. Vì sao vẽ cờ bằng Canvas mà không dùng ảnh?
Hình nhỏ nên vector vẽ bằng Canvas sắc nét trên mọi mật độ, không tăng dung lượng APK. Ảnh phức tạp (núi Phú Sĩ) thì dùng ảnh thật.
