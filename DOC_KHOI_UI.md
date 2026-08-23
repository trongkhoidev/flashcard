# DOC_KHOI — Tài liệu thành viên Khôi: UI/UX & Giao diện (Jetpack Compose)

> **Vai trò:** Bạn phụ trách toàn bộ giao diện (`ui/`). Tài liệu mô tả **từng file, từng hàm** với đầy đủ chữ ký, hành vi bên trong, giá trị hardcode và lý do thiết kế, để trả lời trọn vẹn mọi câu hỏi giám khảo (kể cả "vì sao dùng cách này" và "em triển khai thế nào").

---

## Mục lục

1. [Tổng quan phạm vi](#1-tổng-quan-phạm-vi)
2. [Công nghệ & vì sao chọn](#2-công-nghệ--vì-sao-chọn)
3. [CHI TIẾT TỪNG FILE & TỪNG HÀM](#3-chi-tiết-từng-file--từng-hàm)
4. [Cơ chế nhận dữ liệu & phát sự kiện](#4-cơ-chế-nhận-dữ-liệu--phát-sự-kiện)
5. [Bảng tổng hợp hiệu ứng](#5-bảng-tổng-hợp-hiệu-ứng)
6. [testTag](#6-testtag)
7. [Cẩm nang sửa đổi](#7-cẩm-nang-sửa-đổi)
8. [Q&A mở rộng](#8-qa-mở-rộng)

---

## 1. Tổng quan phạm vi

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

- **Compose thay vì XML:** declarative + tự recomposition + ít boilerplate + animation/test tiện.
- **State Hoisting:** state nâng lên cha, sự kiện phát ngược bằng lambda → tái sử dụng + dễ test.
- **`remember`/`mutableStateOf`** cho state cục bộ; dữ liệu nghiệp vụ để trong `MainViewModel`.
- **Coil** cho ảnh (`AsyncImage` + `crossfade`); **`LazyColumn`/`LazyVerticalGrid`** render lazy.
- **Canvas** vẽ hình nhỏ (cờ, vòng %) thay ảnh → sắc nét + giảm APK.

---

## 3. CHI TIẾT TỪNG FILE & TỪNG HÀM

### 3.1 `ui/theme/`

**`Color.kt`** — hằng màu (không hàm): Brand `NTKPrimary(0xFF0284C7)/Dark/Light/Secondary/Tertiary`; 10 màu Bubble (`BubbleEnglish`...`BubblePortuguese`); Surface `NTKBackgroundLight(0xFFF0F9FF)/SurfaceLight/CardBorder(0xFFE0F2FE)`; Text `NTKTextPrimary(0xFF0F172A)/Secondary/Muted`; Feedback `EasyGreen/GoodYellow/HardRed`; Gradient `NTKGradientStart/End`, `NTKBgGradientTop/Bottom`.

**`Type.kt`** — `val Typography` (Material 3, chỉ override `bodyLarge`).

**`Theme.kt`** — `private LightColorScheme`/`DarkColorScheme`; `@Composable fun MyApplicationTheme(darkTheme=false, dynamicColor=false, content)`: chọn scheme (dynamic color trên Android 12+) rồi bọc `MaterialTheme(typography = Typography)`.

### 3.2 `ui/components/VipAvatarFrame.kt`

**`enum class VipLevel(levelNumber, title, badgeLabel, crownEmoji, badgeBgColor, badgeTextColor, gradientColors)`** — 8 cấp NONE(0)→VIP7(7), mỗi cấp có bộ gradient riêng (VIP1 đồng cam, VIP2 bạc, VIP3 bạch kim cyan, VIP4 vàng, VIP5 kim cương neon, VIP6 lửa, VIP7 cầu vồng). `companion fun fromLevel(level: Int): VipLevel` — map số → enum, mặc định NONE.

**`@Composable fun VipAvatarFrame(vipLevel, avatarSize=58.dp, modifier, content)`** — khung avatar 5 lớp:
- 3 animation `rememberInfiniteTransition`: `rotationAngle` (0→360°, `LinearEasing` + `Restart`; tốc độ theo cấp VIP1 4200ms → VIP7 800ms), `pulseAlpha` (0.35→0.90, `Reverse`), `pulseScale` (1.0→1.03..1.12 theo cấp).
- `borderWidth` 2dp(NONE)→5.5dp(VIP7); `shadowElevation` 0→7dp; `glowBrush` radialGradient theo cấp; `sweepBrush` sweepGradient (nối thêm màu đầu để quay liền mạch).
- Layer 1: aura phát sáng (`graphicsLayer scale/alpha`). Layer 2: vòng sweep xoay (`graphicsLayer rotationZ`). Layer 3: vòng trắng + `content()`. Layer 4: crown emoji ở đỉnh. Layer 5: badge pill "VIP n" ở đáy.

**`@Composable fun VipLevelSelectorCard(currentVipLevel, onSelectVipLevel, modifier)`** — card chọn cấp VIP: header "Khung Viền VIP Profile" + badge cấp hiện tại; `Row.horizontalScroll` liệt kê `VipLevel.values()`, mỗi ô 78dp chứa mini `VipAvatarFrame(28.dp)` + tên + check "Đang chọn". Bấm → `onSelectVipLevel(vip.levelNumber)`.

**`@Composable fun VipCardFrame(userVipLevel, modifier, cornerRadius=24.dp, content)`** — viền thẻ cho flashcard. Nếu NONE → chỉ `Box { content() }`. Ngược lại: 3 animation (`borderProgress` 0→1 chạy tia, `pulseAlpha`, `pulseScale`); cache `cardGlowBrush`, `trackColors`, `beamColors`, `beamCount`(1-4), `beamLengthRatio`(0.32→0.18). 4 lớp: aura, `drawBehind` vẽ viền track + tia sáng chạy dọc `PathMeasure` (reuse `Path` để zero-alloc) + chấm spark, `content()` clip, tag VIP nổi góc phải.

### 3.3 `ui/components/Flashcard3DView.kt`

**`@Composable fun Flashcard3DView(card, isFlipped, userVipLevel=0, onFlip, onSpeak, onToggleStar, modifier)`**:
- `rotationY = animateFloatAsState(if(isFlipped) 180f else 0f, tween(400))`.
- Root `Box` `height(340.dp)` + `graphicsLayer { rotationY; cameraDistance = 12f*density }` + `clickable(indication=null){ onFlip() }`.
- `if (rotationY <= 90f)` → `CardFrontSide`, else `CardBackSide` (xoay bù 180°).

**`private fun CardFrontSide(card, userVipLevel, onSpeak, onToggleStar, onFlip)`** — bọc `VipCardFrame`; badge loại từ (uppercase), nút phát âm (`btn_pronounce_front`) + sao (`btn_star_card`), từ 32sp + phiên âm 16sp + câu ví dụ (nghiêng, trong khung), gợi ý "Chạm thẻ để xem giải nghĩa".

**`private fun CardBackSide(...)`** — bọc `VipCardFrame`; nhãn "Ý NGHĨA", nút phát âm/sao, nghĩa 22sp + dịch ví dụ + mẹo nhớ (khung vàng 💡), gợi ý "Lật về mặt trước".

### 3.4 `ui/components/` còn lại

- **`OwlMascotView.kt`** — `OwlMascotView(modifier, onLanguageClick)` : linh vật cú + 4 `LanguageSpeechBubble` (en/ja/ko/vi).
- **`LanguageSpeechBubble.kt`** — `enum BubbleTailDirection`; `LanguageSpeechBubble(text, backgroundColor, tailDirection, floatOffset=4f, durationMs=2000, modifier, onClick)` : bubble + đuôi Canvas, lơ lửng `infiniteRepeatable`.
- **`GlowingCardsHeader.kt`** — `GlowingCardsHeader(modifier)` : 3 thẻ phát sáng + sparkle.
- **`LaurelWreathHeader.kt`** — `LaurelWreathHeader(modifier)` + `private LaurelBranch(isLeft)` : tên app + slogan + vòng nguyệt quế.

### 3.5 `ui/welcome/WelcomeScreen.kt`

**`@Composable fun WelcomeScreen(onStartLearning, onLoginClick, onSelectLanguage:(String)->Unit={}, modifier)`**:
- `currentPage` (`mutableIntStateOf(0)`) + `LaunchedEffect(Unit){ while(true){ delay(3500); currentPage=(currentPage+1)%3 } }` tự chuyển carousel.
- Nền gradient trắng→cyan; `GlowingCardsHeader` + `LaurelWreathHeader`; `OwlMascotView(onLanguageClick=onSelectLanguage)`.
- `AnimatedContent(currentPage)` slide+fade (400ms) hiển thị 3 cặp title/subtitle hardcode.
- Dot indicator `animateDpAsState` (8dp↔24dp, spring bouncy), bấm đổi trang.
- Nút "Bắt đầu học ngay" (`start_learning_button`) → `onStartLearning`; "Đăng nhập" (`login_button`) → `onLoginClick`.

### 3.6 `ui/welcome/LoginScreen.kt`

**`@Composable fun LoginScreen(onLoginSuccess:(String)->Unit, onBackToWelcome, onNavigateToRegister, modifier)`**:
- State: `username`, `password`, `rememberMe=true`, `isPasswordVisible`.
- `Scaffold` + top bar back (`back_button`). Header "Đăng nhập" + mascot penguin.
- Form card: ô tên đăng nhập (`login_username_input`), ô mật khẩu (`login_password_input`, có toggle ẩn/hiện + `PasswordVisualTransformation`), dòng "Ghi nhớ đăng nhập" (checkbox tự vẽ) + "Quên mật khẩu?" (Toast), nút "Đăng nhập" (`submit_login_button`), card info "Học mọi lúc, mọi nơi", footer "Đăng ký ngay" (`register_link`).
- Nút Đăng nhập: `val userStr = username.ifBlank { "Học viên NTK" }` → Toast + `onLoginSuccess(userStr)`. **Chưa nối DB.**

### 3.7 `ui/welcome/RegisterScreen.kt`

**`@Composable fun RegisterScreen(onRegisterSuccess:(String)->Unit, onBackToWelcome, onNavigateToLogin, modifier)`**:
- State: `username`, `password`, `confirmPassword`, `isPasswordVisible`, `isConfirmPasswordVisible`.
- Validate dẫn xuất: `isUsernameValid = length in 4..20 && !contains(" ")`; `isPasswordValid = length >= 8`; `isPasswordsMatch = password == confirmPassword && confirmPassword.isNotEmpty()`.
- Độ mạnh mật khẩu `remember(password)` → `Triple(text, color, segments)`: rỗng/"Rất yếu"(1)/"Yếu"(2)/"Trung bình"(3)/"Mạnh" hoặc "Rất mạnh"(4, cần chữ hoa + số + ký tự đặc biệt).
- Nút "Đăng ký" (`register_button`) kiểm tra tuần tự 3 điều kiện (Toast lỗi tương ứng), thành công → Toast + `onRegisterSuccess(username)`.

### 3.8 `ui/welcome/OnboardingStepsScreen.kt` (7 bước)

**`@Composable fun OnboardingStepsScreen(onCompleteOnboarding:(AppLanguage,Int)->Unit, onBackToWelcome, modifier)`**:
- `currentStep` 1-7, `selectedLanguage=ENGLISH`, `selectedLevelId="beginner"`, `selectedTopics=setOf("daily","travel","work")`, `selectedTimeSlots=setOf("slot_morning_2","slot_afternoon","slot_evening_2")`.
- `primaryHour = timeSlots.firstOrNull { selectedTimeSlots.contains(it.id) }?.defaultHour ?: 19`.
- Bước: (1) `StepLanguageSelection` chọn ngôn ngữ (9, grid 3x3) → (2) `StepLevelSelection` cấp độ → (3) `StepTopicSelection` chủ đề (8, đa chọn) → (4) `StepTimeSelection` khung giờ (7 slot) → (5) `StepNotificationPermission` xin quyền → (6) `StepAddWidgetHomeScreen` thêm widget → (7) `StepMascotPreparingFlashcards` loading → `onCompleteOnboarding(selectedLanguage, primaryHour)`.
- `AnimatedContent` slide+fade chuyển bước; `TopStepperBar` 4 chấm.

Các composable private (từng bước): `TopStepperBar`, `StepLanguageSelection`, `LanguageCard`, `StepLevelSelection`, `StepTopicSelection`, `StepTimeSelection`, `TimeSlotCard`, `WideTimeSlotCard`, `HeaderBannerWithMascot`, `StepNotificationPermission`, `NotificationBenefitCard`, `StepGuidePill`, `StepAddWidgetHomeScreen`, `NotificationBenefitCardCompact`, `WidgetQuickFeatureBadge`, `MockDockAppIcon`, `WidgetBenefitItem`, `StepMascotPreparingFlashcards`, `PreparationCheckItem`. Data class: `OnboardingLanguageItem`, `StudyLevelItem`, `StudyTopicItem`, `TimeSlotItem`, private `WidgetSampleWord`.

### 3.9 `ui/home/HomeScreen.kt`

**`@Composable fun HomeScreen(...)`** — 4 tab dưới + overlay Ôn tập + search + dialog/sheet. State: `searchQuery`, `selectedBottomTab`, `showReviewOverlay`, `showStatsDialog`, `showSavedCardsDialog`, `showCreateDeckDialog`, `showImportCardsDialog`, `showLanguageFilterSheet`, `showAllDecksSheet`, `showAddLanguageSheet`.
- Tab 0 Trang chủ: `HomeTopHeader` → `HomeSearchBar` → (search) `SearchResultsView` / `StarterWelcomeHeroCard` hoặc `StreakMascotBanner` → `QuickActionGrid` → `ContinueLearningSection` → `SpacedRepetitionDueWidget` → `YourDecksSection` → `DailyGoalCard`.
- Tab 1 Khám phá: `ExploreDecksTab`. Tab 2 BXH: `LeaderboardTab`. Tab 3 Tài khoản: `AccountProfileTab`.

Private: `SearchResultsView`, `DeckListCard` (3 nút Học/Quiz/Ghép), `ExploreDecksTab`, `ReviewHistoryTab` (overlay Ôn tập), `AccountProfileTab`.

### 3.10 `ui/home/HomeComponents.kt`

Public: `HomeTopHeader(userName, streakDays, onStreakClick)`, `HomeSearchBar(query, onQueryChange, onFilterClick)`, `StreakMascotBanner(streakDays, onBannerClick)` (banner gradient + tracker T2-CN + linh vật + sparkle `infiniteRepeatable`), `QuickActionGrid(onCreateDeck, onReviewCards, onViewStats, onViewSaved)`, `StarterWelcomeHeroCard(userName, language, starterDeck, onStartFirstLesson)`, `ContinueLearningSection(...)`, `SpacedRepetitionDueWidget(dueCount, onStartReview)`, `YourDecksSection(...)`, `DynamicDeckCard(deck, language, onClick, onStudy, onQuiz, onMatch)`, `CreateNewDeckCard(onClick)`, `AddLanguageBottomSheet(learningLanguages, onSelectNewLanguage, onDismiss)`, `DailyGoalCard(currentCount, targetCount, percentage, onClick)` (vòng % Canvas `drawArc`+`sweepGradient`), `HomeBottomNavBar(selectedTab, onTabSelected)`.
Private: `QuickActionItem`, `QuickStepItem`, `NavBarItem`. Minh hoạ: `JapaneseFujiArt`, `UsFlagArt`, `KoreaFlagArt`, `GlobeArt`; `DrawScope.drawTrigram`, `drawSparkle`.

### 3.11 `ui/detail/DeckDetailScreen.kt`

- `data class DeckTopic(id, title, cardCount, progressPercent, description)`.
- `@Composable fun DeckDetailScreen(deck, cards, onBack, onStartStudy, onStartQuiz, onStartMatch, onSpeak, onToggleStar)` — `Scaffold`, hero (ảnh bìa + rating 4.9 + số thẻ + tag chips `FlowRow`), nút "Học ngay", menu ⋮ (Quiz/Match), 2 tab Nội dung/Thống kê.
- private: `DeckCoverImageCard(deck)`, `AvatarCircle(emoji, bgColor, offset)`, `StatsDetailTab(deck, cards, onStartQuiz, onStartMatch)`.

### 3.12 `ui/study/FlashcardStudyScreen.kt`

**`@Composable fun FlashcardStudyScreen(deckTitle, languageTag, cards, userVipLevel=0, onBack, onSpeak, onToggleStar, onRecordReview, onStartQuiz, onSessionFinished:((Int,Int)->Unit)?=null, modifier)`**:
- 3 nút hành động: Trộn thẻ (`btn_shuffle_cards`), Lưu từ điển (`btn_save_dictionary`), đổi mặt (`btn_flip_indicator`).
- 2 nút đánh giá: "Chưa thuộc" (`btn_not_memorized`, `onRecordReview(id,3)`, không chuyển) & "Đã thuộc" (`btn_memorized`, `onRecordReview(id,1)`, tự chuyển).
- Auto-play `LaunchedEffect(isAutoPlay, currentIndex, isFlipped, isCompleted)`: đọc mặt trước + `delay(2600)` → lật; `delay(2200)` → thẻ tiếp.
- Overlay hoàn thành + `LaunchedEffect(isCompleted)` gọi `onSessionFinished`. Toast tạm (`LaunchedEffect(toastMessage)` 1500ms).
- `private advanceNext(...)` — helper (hiện dead code).

### 3.13 `ui/quiz/QuizScreen.kt`

- `data class StreakMultiplierInfo(multiplier, title, badgeColor, emoji)`; `fun getStreakMultiplierInfo(streak)`: ≤1→1.0, 2→1.5, 3→2.0, 4→2.5, 5→3.0, 6→3.5, ≥7→5.0 ("COMBO THẦN THÁO").
- `@Composable fun QuizScreen(deckTitle, languageTag, cards, onBack, onSpeak, onFinishQuiz(score,total), onStudyNext=null, modifier)`: guard `cards.size<2`; `quizCards=cards.shuffled()`; `currentOptions` = 3 nghĩa nhiễu + nghĩa đúng (xáo); đúng → `score++`, `currentStreak++`, `totalPoints += 100*multiplier`; sai → reset chuỗi; popup điểm `Animatable`; pháo hoa `FireworksCanvas`; overlay kết quả.
- `@Composable fun FireworksCanvas(durationMs=8000L, modifier, onFinished)`: `Canvas`+`withFrameNanos`, hạt có trọng lực 0.25/lực cản 0.96/alpha giảm, ≤90 hạt, 3 hình (tròn/vuông/sao).
- `private data class FireworkParticle`.

### 3.14 `ui/match/WordMatchScreen.kt`

- `data class MatchItem(id, text, isFront, pairId)`.
- `@Composable fun WordMatchScreen(deckTitle, cards, onBack, modifier)`: `gameCards=cards.take(6)`; tạo cặp từ↔nghĩa + `shuffled()`; `LazyVerticalGrid(GridCells.Fixed(2))`; đúng khi `pairId` khớp & khác `isFront`; `LaunchedEffect` thắng khi ghép đủ.

### 3.15 `ui/leaderboard/LeaderboardTab.kt`

- `enum LeaderboardFilterType(POINTS/STREAK/CARDS)`, `enum TimePeriod(THIS_WEEK/THIS_MONTH/ALL_TIME)`, `sealed class RankTrend(Up/Down/Same)`, `data class LeaderboardUser(rank, name, points, streakDays, cardsLearned, trend, avatarBgColor, avatarEmoji, vipLevel, isCurrentUser)`.
- `@Composable fun LeaderboardTab(userName, userVipLevel, userScore, userStreak, userCardsLearned, modifier)`: state `selectedFilter/selectedTimePeriod/showTimeMenu/selectedUserForDialog/showAllRewardsDialog`; `leaderboardList` 10 user mock nhân theo `multiplier` (Tuần 1.0/Tháng 3.5/Tất cả 8.0); `currentUserItem` rank 23. Header + dropdown `TimePeriod` + segmented filter + top-3 `PodiumStep` + rank 4-10 `LeaderboardRowItem` + `RewardCard` + 2 dialog (user detail, all rewards).
- private: `PodiumStep` (avatar VIP + bục + huy chương), `LeaderboardRowItem` (rank + trend ↑↓- + avatar VIP + tên + điểm), `RewardCard`.

### 3.16 `ui/dialogs/`

- **`HomeDialogs.kt`**: `ImportCardsDialog(decks, onDismiss, onImportCards(deckId,cards))` (nhập `Từ|Nghĩa|Ví dụ`, hardcode fr/phrase); `StatsSummaryDialog(streakDays, masteredCount, totalCardsCount, onDismiss)` (4 StatCard + biểu đồ tuần); `SavedCardsDialog(starredCards, decks, onSpeak, onToggleStar, onStartStudy/Quiz/Match, onDismiss)` (lọc ngôn ngữ/chủ đề + tìm kiếm + `SavedWordDetailCard`); private `StatCard`, `SavedWordDetailCard`.
- **`UserProfileDialog.kt`**: `UserProfileDialog(userName, userVipLevel=1, streakDays, masteredWordsCount, totalWordsCount, onDismiss, onUpdateName, onSelectVipLevel)` — avatar VIP, đổi tên, `VipLevelSelectorCard`, thống kê.
- **`CreateDeckAndCardDialogs.kt`**: `CreateCardDialog(deckId, languageCode, onDismiss, onSave(FlashCardEntity))` (form từ vựng); `CreateDeckDialog(currentLanguageCode, allCards, onDismiss, onSave(DeckEntity, List<FlashCardEntity>))` (tạo bộ thẻ + chọn thẻ 2 tab "⭐ Đã lưu"/"💡 Chưa thuộc").

---

## 4. Cơ chế nhận dữ liệu & phát sự kiện

Screens **không** tự gọi ViewModel. `MainActivity` nối callback với `viewModel`:

```kotlin
FlashcardStudyScreen(
    deckTitle = screen.deck.title,
    languageTag = screen.deck.languageCode,
    cards = screen.cards,
    userVipLevel = userVipLevel,
    onBack = { viewModel.navigateTo(ScreenState.Home) },
    onSpeak = { text, tag -> viewModel.speak(text, tag) },
    onToggleStar = { id, starred -> viewModel.toggleStar(id, starred) },
    onRecordReview = { id, diff -> viewModel.recordReview(id, diff) },
    onStartQuiz = { viewModel.startQuizDeck(screen.deck) },
    onSessionFinished = { count, mastered -> viewModel.completeStudySession(...) }
)
```

---

## 5. Bảng tổng hợp hiệu ứng

| Hiệu ứng | API | Cách làm |
| --- | --- | --- |
| Lật thẻ 3D | `graphicsLayer.rotationY` + `animateFloatAsState` + `cameraDistance` | 0↔180°, ngưỡng 90° |
| Khung VIP avatar | `rememberInfiniteTransition` (rotation/pulse alpha/pulse scale) | aura + vòng sweep xoay + nội dung |
| Viền thẻ VIP | `drawBehind` + `PathMeasure` | tia sáng chạy dọc viền + spark |
| Pháo hoa | `Canvas` + `withFrameNanos` | hạt có trọng lực/lực cản/vòng đời, ≤90 hạt, 8s |
| Popup điểm | `Animatable` | pop → giữ 900ms → bay lên mờ |
| Carousel/step | `AnimatedContent` | slide+fade |
| Overlay | `AnimatedVisibility` | `fadeIn + scaleIn` |
| Mở rộng chủ đề | `animateContentSize` | `expand/shrinkVertically` |
| Lơ lửng | `infiniteRepeatable` | `animateFloat` đảo chiều |

---

## 6. testTag

```
Welcome: start_learning_button, login_button
Login: back_button, login_username_input, login_password_input, forgot_password_link, submit_login_button, register_link
Register: back_button, username_input, password_input, confirm_password_input, register_button, login_link
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

---

## 7. Cẩm nang sửa đổi

| Yêu cầu | Nơi sửa |
| --- | --- |
| Đổi màu | `ui/theme/Color.kt` |
| Đổi font | `ui/theme/Type.kt` |
| Thêm màn hình | composable + `ScreenState` + `when` `MainActivity` + hàm `MainViewModel` |
| Sửa home | `ui/home/*` |
| Sửa thẻ học | `ui/study/FlashcardStudyScreen.kt` + `Flashcard3DView.kt` |
| Sửa quiz | `ui/quiz/QuizScreen.kt` |
| Sửa ghép từ | `ui/match/WordMatchScreen.kt` |
| Sửa VIP | `components/VipAvatarFrame.kt` |
| Sửa BXH | `ui/leaderboard/LeaderboardTab.kt` |
| Sửa dialog | `ui/dialogs/*` |

---

## 8. Q&A mở rộng

### Q1. Vì sao Compose thay vì XML?
Declarative, tự recomposition, ít boilerplate, animation/test tiện.

### Q2. Lật thẻ 3D làm thế nào?
`graphicsLayer.rotationY` + `animateFloatAsState` (0↔180°, 400ms) + `cameraDistance=12f*density`; `rotationY<=90f` mặt trước, ngược lại mặt sau (xoay bù 180°).

### Q3. Khung VIP làm thế nào?
`VipLevel` enum (0-7) mỗi cấp có gradient/badge/crown/tốc độ; `VipAvatarFrame` xếp aura + vòng sweep xoay `rotationZ` + nội dung; `VipCardFrame` tia sáng bằng `PathMeasure`.

### Q4. Pháo hoa làm thế nào?
`FireworksCanvas`: `Canvas` + `withFrameNanos`, hạt có vận tốc/trọng lực(+0.25)/lực cản(*0.96)/vòng đời, 3 hình dạng, ≤90 hạt, 8s.

### Q5. Điểm & streak multiplier?
Đúng → `score++`, `currentStreak++`, `totalPoints += 100*multiplier` (1.0→5.0); sai reset. Gamification thuần UI.

### Q6. Vì sao `LaunchedEffect` cho auto-play/toast/carousel?
Gắn coroutine với vòng đời composable, tự huỷ khi key đổi → không rò rỉ `delay`.

### Q7. Vì sao state cục bộ dùng `remember` mà không đưa hết vào ViewModel?
State tạm trong một màn hình thì `remember` gọn; dữ liệu chia sẻ nhiều màn hình mới vào ViewModel.

### Q8. Bảng xếp hạng lấy dữ liệu từ đâu?
Dữ liệu tĩnh (10 user mock + bản thân, nhân theo Tuần/Tháng/Tất cả). Cấu trúc tách bạch → nối `UserProfileDao`/`QuizRecordDao` là xong.

### Q9. Vì sao ghép từ dùng `LazyVerticalGrid`?
Số ô thay đổi (12 ô), chia cột tự động, render lazy.

### Q10. Vì sao vẽ cờ bằng Canvas?
Hình nhỏ → vector sắc nét trên mọi mật độ, không tăng APK; ảnh phức tạp mới dùng ảnh thật.

### Q11. Vì sao VIP dùng `graphicsLayer` thay vì animation trực tiếp?
`graphicsLayer` (rotationZ/scale/alpha) chạy trên GPU, **không trigger recomposition** mỗi frame → mượt hơn, tiết kiệm CPU. Đây là kỹ thuật tối ưu hiệu năng quan trọng.

### Q12. Vì sao `VipCardFrame` cache `Path`/`PathMeasure` bằng `remember`?
Để tránh cấp phát object (garbage collection) trong `drawBehind` mỗi frame — giữ 60fps cho animation viền.
