# DOC_KHOI — Tài liệu thành viên Khôi: UI/UX & Giao diện (Jetpack Compose)

> **Vai trò:** Bạn phụ trách toàn bộ giao diện (`ui/`). Tài liệu mô tả **từng file, từng hàm** với đầy đủ chữ ký, hành vi bên trong, giá trị hardcode và lý do thiết kế, để trả lời trọn vẹn mọi câu hỏi giám khảo.

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
├── home/           → HomeScreen (1867 dòng), HomeComponents (2037 dòng),
│                     StreakWeeklyTracker (291 dòng)
├── detail/         → DeckDetailScreen
├── study/          → FlashcardStudyScreen (711 dòng)
├── quiz/           → QuizScreen (1197 dòng)
├── match/          → WordMatchScreen (314 dòng)
├── leaderboard/    → LeaderboardTab
└── dialogs/        → HomeDialogs, UserProfileDialog, CreateDeckAndCardDialogs
```

---

## 2. Công nghệ & vì sao chọn

- **Compose thay vì XML:** declarative + tự recomposition + ít boilerplate + animation/test tiện.
- **State Hoisting:** state nâng lên cha, sự kiện phát ngược bằng lambda → tái sử dụng + dễ test.
- **`remember`/`mutableStateOf`** cho state cục bộ; dữ liệu nghiệp vụ để trong `MainViewModel`.
- **Coil** cho ảnh (`AsyncImage` + `crossfade`); **`LazyColumn`/`LazyVerticalGrid`** render lazy.
- **Canvas** vẽ hình nhỏ (cờ, vòng %, cờ Trung Quốc, nốt nhạc) thay ảnh → sắc nét + giảm APK.
- **`graphicsLayer`** (GPU, không recomposition) cho animation VIP frame.

---

## 3. CHI TIẾT TỪNG FILE & TỪNG HÀM

### 3.1 `ui/theme/`

**`Color.kt`** — hằng màu (không hàm):
- Brand: `NTKPrimary(0xFF0284C7)`, `NTKPrimaryDark(0xFF0369A1)`, `NTKPrimaryLight(0xFF0EA5E9)`, `NTKSecondary(0xFF6366F1)`, `NTKTertiary(0xFF10B981)`
- 10 màu Bubble: `BubbleEnglish(0xFF3B82F6)`→`BubblePortuguese(0xFF10B981)`
- Surface: `NTKBackgroundLight(0xFFF0F9FF)`, `NTKSurfaceLight(0xFFFFFFFF)`, `CardBorder(0xFFE0F2FE)`
- Text: `NTKTextPrimary(0xFF0F172A)`, `NTKTextSecondary(0xFF475569)`, `NTKTextMuted(0xFF94A3B8)`
- Feedback: `EasyGreen(0xFF22C55E)`, `GoodYellow(0xFFFBBF24)`, `HardRed(0xFFEF4444)`
- Gradient: `NTKGradientStart(0xFF0EA5E9)`, `NTKGradientEnd(0xFF6366F1)`, `NTKBgGradientTop(0xFFF0F9FF)`, `NTKBgGradientBottom(0xFFE0F2FE)`

**`Type.kt`** — `val Typography` (Material 3, chỉ override `bodyLarge`).

**`Theme.kt`** — `private LightColorScheme`/`DarkColorScheme`; `@Composable fun MyApplicationTheme(darkTheme=false, dynamicColor=false, content)`: chọn scheme (dynamic color trên Android 12+) rồi bọc `MaterialTheme(typography = Typography)`.

### 3.2 `ui/components/VipAvatarFrame.kt` (837 dòng)

**`enum class VipLevel(levelNumber, title, badgeLabel, crownEmoji, badgeBgColor, badgeTextColor, gradientColors)`** — 8 cấp NONE(0)→VIP7(7):
- NONE: gradient rỗng, crown rỗng
- VIP1: đồng cam, crown 👑
- VIP2: bạc, crown 👑
- VIP3: bạch kim cyan, crown 👑
- VIP4: vàng hoàng gia, crown 👑👑
- VIP5: kim cương neon, crown 👑👑
- VIP6: lửa, crown 👑👑👑
- VIP7: cầu vồng, crown 👑👑👑

`companion fun fromLevel(level: Int): VipLevel` — map số → enum, mặc định NONE.

**`@Composable fun VipAvatarFrame(vipLevel, avatarSize=58.dp, modifier, content)`** — khung avatar 5 lớp:
- 3 animation `rememberInfiniteTransition`: `rotationAngle` (0→360°, `LinearEasing` + `Restart`; tốc độ VIP1 4200ms→VIP7 800ms), `pulseAlpha` (0.35→0.90, `Reverse`), `pulseScale` (1.0→1.03..1.12 theo cấp).
- `borderWidth` 2dp(NONE)→5.5dp(VIP7); `shadowElevation` 0→7dp.
- Layer 1: aura phát sáng (`graphicsLayer scale/alpha`). Layer 2: vòng sweep xoay (`graphicsLayer rotationZ`). Layer 3: vòng trắng + `content()`. Layer 4: crown emoji. Layer 5: badge pill.

**`@Composable fun VipLevelSelectorCard(currentVipLevel, onSelectVipLevel, modifier)`** — card chọn cấp VIP: header + badge cấp hiện tại; `Row.horizontalScroll` liệt kê `VipLevel.values()`, mỗi ô 78dp chứa mini `VipAvatarFrame(28.dp)` + tên + check "Đang chọn".

**`@Composable fun VipCardFrame(userVipLevel, modifier, cornerRadius=24.dp, content)`** — viền thẻ flashcard. Nếu NONE → chỉ `Box { content() }`. Ngược lại: 3 animation; cache `cardGlowBrush`, `trackColors`, `beamColors`, `beamCount`(1-4), `beamLengthRatio`(0.32→0.18). 4 lớp: aura, `drawBehind` viền + tia sáng chạy dọc `PathMeasure` (cache `Path` zero-alloc) + spark, `content()` clip, tag VIP góc phải.

### 3.3 `ui/components/Flashcard3DView.kt`

**`@Composable fun Flashcard3DView(card, isFlipped, userVipLevel=0, onFlip, onSpeak, onToggleStar, modifier)`**:
- `rotationY = animateFloatAsState(if(isFlipped) 180f else 0f, tween(400))`.
- Root `Box` `height(340.dp)` + `graphicsLayer { rotationY; cameraDistance = 12f*density }` + `clickable(indication=null){ onFlip() }`.
- `if (rotationY <= 90f)` → `CardFrontSide`, else `CardBackSide` (xoay bù 180°).

**`private fun CardFrontSide(card, userVipLevel, onSpeak, onToggleStar, onFlip)`** — bọc `VipCardFrame`; badge loại từ (uppercase), nút phát âm (`btn_pronounce_front`) + sao (`btn_star_card`), từ 32sp + phiên âm 16sp + câu ví dụ (nghiêng, trong khung), gợi ý "Chạm thẻ để xem giải nghĩa".

**`private fun CardBackSide(...)`** — bọc `VipCardFrame`; nhãn "Ý NGHĨA", nút phát âm/sao, nghĩa 22sp + dịch ví dụ + mẹo nhớ (khung vàng 💡), gợi ý "Lật về mặt trước".

### 3.4 `ui/components/` còn lại

- **`OwlMascotView.kt`** — `OwlMascotView(modifier, onLanguageClick)` : linh vật cú + 4 `LanguageSpeechBubble` (en/ja/ko/vi).
- **`LanguageSpeechBubble.kt`** (126 dòng) — `enum BubbleTailDirection` (4 hướng); `LanguageSpeechBubble(text, backgroundColor, tailDirection, floatOffset=4f, durationMs=2000, modifier, onClick)` : bubble + đuôi Canvas 12x8dp, lơ lửng `infiniteRepeatable`. `testTag("bubble_$text")`.
- **`GlowingCardsHeader.kt`** (153 dòng) — `GlowingCardsHeader(modifier)` : 3 thẻ phát sáng (2pcs 54x68dp xoay ±14° + main 56x72dp), sparkle 3 viên, glow 76dp radial gradient, lơ lửng 2200ms -3dp↔+3dp.
- **`LaurelWreathHeader.kt`** — `LaurelWreathHeader(modifier)` + `private LaurelBranch(isLeft)` : tên app + slogan + vòng nguyệt quế.

### 3.5 `ui/welcome/WelcomeScreen.kt`

**`@Composable fun WelcomeScreen(onStartLearning, onLoginClick, onSelectLanguage:(String)->Unit={}, modifier)`**:
- `currentPage` (`mutableIntStateOf(0)`) + `LaunchedEffect(Unit){ while(true){ delay(3500); currentPage=(currentPage+1)%3 } }` tự chuyển carousel.
- Nền gradient trắng→cyan; `GlowingCardsHeader` + `LaurelWreathHeader`; `OwlMascotView(onLanguageClick=onSelectLanguage)`.
- `AnimatedContent(currentPage)` slide+fade (400ms) hiển thị 3 cặp title/subtitle hardcode.
- Dot indicator `animateDpAsState` (8dp↔24dp, spring bouncy).
- Nút "Bắt đầu học ngay" (`start_learning_button`) → `onStartLearning`; "Đăng nhập" (`login_button`) → `onLoginClick`.

### 3.6 `ui/welcome/LoginScreen.kt`

**`@Composable fun LoginScreen(onLoginSuccess:(String)->Unit, onBackToWelcome, onNavigateToRegister, modifier)`**:
- State: `username`, `password`, `rememberMe=true`, `isPasswordVisible`.
- Form card: ô tên (`login_username_input`), ô mật khẩu (`login_password_input`, toggle ẩn/hiện), checkbox "Ghi nhớ", "Quên mật khẩu?" (Toast), nút "Đăng nhập" (`submit_login_button`), footer "Đăng ký ngay" (`register_link`).
- Nút Đăng nhập: `val userStr = username.ifBlank { "Học viên NTK" }` → Toast + `onLoginSuccess(userStr)`. **Chưa nối DB.**

### 3.7 `ui/welcome/RegisterScreen.kt`

**`@Composable fun RegisterScreen(onRegisterSuccess:(String)->Unit, onBackToWelcome, onNavigateToLogin, modifier)`**:
- State: `username`, `password`, `confirmPassword`, `isPasswordVisible`, `isConfirmPasswordVisible`.
- Validate: `isUsernameValid = length in 4..20 && !contains(" ")`; `isPasswordValid = length >= 8`; `isPasswordsMatch = password == confirmPassword`.
- Độ mạnh mật khẩu: rỗng/"Rất yếu"(1)/"Yếu"(2)/"Trung bình"(3)/"Mạnh"(4, cần chữ hoa+số+đặc biệt).
- Nút "Đăng ký" (`register_button`) kiểm tra tuần tự → Toast + `onRegisterSuccess(username)`.

### 3.8 `ui/welcome/OnboardingStepsScreen.kt` (7 bước)

**`@Composable fun OnboardingStepsScreen(onCompleteOnboarding:(AppLanguage,Int)->Unit, onBackToWelcome, modifier)`**:
- `currentStep` 1-7, `selectedLanguage=ENGLISH`, `selectedLevelId="beginner"`, `selectedTopics=setOf("daily","travel","work")`, `selectedTimeSlots=setOf("slot_morning_2","slot_afternoon","slot_evening_2")`.
- `primaryHour = timeSlots.firstOrNull { selectedTimeSlots.contains(it.id) }?.defaultHour ?: 19`.
- Bước: (1) `StepLanguageSelection` (9 ngôn ngữ, grid 3x3) → (2) `StepLevelSelection` → (3) `StepTopicSelection` (8, đa chọn) → (4) `StepTimeSelection` (7 slot) → (5) `StepNotificationPermission` → (6) `StepAddWidgetHomeScreen` → (7) `StepMascotPreparingFlashcards` loading → `onCompleteOnboarding(selectedLanguage, primaryHour)`.
- `AnimatedContent` slide+fade; `TopStepperBar` 4 chấm.

Các composable private: `TopStepperBar`, `StepLanguageSelection`, `LanguageCard`, `StepLevelSelection`, `StepTopicSelection`, `StepTimeSelection`, `TimeSlotCard`, `WideTimeSlotCard`, `HeaderBannerWithMascot`, `StepNotificationPermission`, `NotificationBenefitCard`, `StepGuidePill`, `StepAddWidgetHomeScreen`, `NotificationBenefitCardCompact`, `WidgetQuickFeatureBadge`, `MockDockAppIcon`, `WidgetBenefitItem`, `StepMascotPreparingFlashcards`, `PreparationCheckItem`.
Data class: `OnboardingLanguageItem`, `StudyLevelItem`, `StudyTopicItem`, `TimeSlotItem`, private `WidgetSampleWord`.

### 3.9 `ui/home/StreakWeeklyTracker.kt` (291 dòng) — FILE MỚI

**`enum class DayStudyStatus`** — 4 trạng thái: `COMPLETED` (ngày quá khứ đã học, ✓ xanh lá), `TODAY_COMPLETED` (hôm nay đã học, ⭐ vàng nhấp nháy), `TODAY_PENDING` (hôm nay chưa học, 🔥 vàng nhấp nháy), `UPCOMING` (ngày tương lai, ○ mờ).

**`data class DayOfWeekInfo(shortLabel, fullLabel, dayOfWeekCalendar, isToday, status)`** — `shortLabel` = "T2".."CN"; `fullLabel` = "Thứ Hai".."Chủ Nhật".

**`object StreakTimeHelper`**:
- `fun getTodayIndex(): Int` — trả 0-6 (Thứ Hai=0, Chủ Nhật=6) dựa trên `Calendar.getInstance()`.
- `fun getTodayFullName(): String` — tên hôm nay bằng tiếng Việt.
- `fun getWeeklyDaysInfo(streakDays: Int, isTodayStudied: Boolean = true): List<DayOfWeekInfo>` — tạo 7 phần tử:
  - Ngày trước hôm nay: `COMPLETED` nếu `streakDays > (todayIndex - index)`, else `UPCOMING`.
  - Hôm nay: `TODAY_COMPLETED` nếu `isTodayStudied || streakDays > 0`, else `TODAY_PENDING`.
  - Ngày sau: luôn `UPCOMING`.

**`@Composable fun WeeklyStreakTrackerBar(streakDays=7, isTodayStudied=true, modifier)`** — hàng ngang 7 cột (T2-CN):
- Mỗi cột: label ngắn bold + trạng thái tròn (✓ xanh/⭐ vàng pulse 🔥 flash/○ mờ) + chấm trắng nhỏ bên dưới hôm nay.
- `rememberInfiniteTransition` cho pulse (1.0→1.08x, 1000ms).
- `testTag("streak_day_T2")` etc.

### 3.10 `ui/home/HomeScreen.kt` (1867 dòng)

**`@Composable fun HomeScreen(selectedLanguage, onSelectLanguage, learningLanguages, onAddLearningLanguage, decks, allDecksList, starredCards, allCardsList, streakDays, masteredWordsCount, totalWordsCount, userName, userVipLevel, onSelectVipLevel, onOpenDeckDetail, onStudyDeck, onQuizDeck, onMatchDeck, onAddCardToDeck, onCreateNewDeck, onOpenProfile, onOpenStarred, onSpeak, onToggleStar, onStartStudySaved, onStartQuizSaved, onStartMatchSaved, onCreateDeckDirect, onImportCardsDirect, onStudyByLang, onTestSmartNotification, onTestMilestoneNotification, modifier)`**

4 tab dưới + overlay Ôn tập + search + dialog/sheet:
- State: `selectedBottomTab(0)`, `showReviewOverlay(false)`, `showStatsDialog(false)`, `showSavedCardsDialog(false)`, `showCreateDeckDialog(false)`, `showImportCardsDialog(false)`, `showLanguageFilterSheet(false)`, `showAllDecksSheet(false)`, `showAddLanguageSheet(false)`.
- Derived: `isFirstTimeUser = masteredWordsCount == 0 && streakDays <= 1`, `currentActiveDeck`, `studiedCount`, `goalCurrent/goalTarget/goalPercentage`.
- Scroll: `showScrollButton` (floating nút lên/xuống), `arrowRotation` animate 90°/-90°.

**Tab 0 (Trang chủ):** `HomeTopHeader` → `HomeSearchBar` → (search) `SearchResultsView` / `StarterWelcomeHeroCard` (first-time) hoặc `StreakMascotBanner` (returning) → `QuickActionGrid` → `ContinueLearningSection` → `SpacedRepetitionDueWidget` (hardcoded dueCount=8) → `YourDecksSection` → `DailyGoalCard` → `AccountProfileTab`.

**Tab 1 (Khám phá):** `ExploreDecksTab` — search bar + language chips + topic filter ("Tất cả" / "Cơ bản" / "Giao tiếp" / "Tự tạo") + deck list.

**Tab 2 (BXH):** `LeaderboardTab`.

**Tab 3 (Tài khoản):** `AccountProfileTab` — avatar VIP, progress overview, nút test smart notification, nút test milestone notification, widget guide dialog.

Private composable: `SearchResultsView`, `DeckListCard` (Study/Quiz/Match buttons), `ExploreDecksTab` (1003-1225), `ReviewHistoryTab` (1231-1453, overlay Ôn tập + language filter + banner "Từ vựng đã lưu"), `AccountProfileTab` (1459-1867).

### 3.11 `ui/home/HomeComponents.kt` (2037 dòng)

**Public composables:**

| Hàm | Chữ ký | Mô tả |
|------|--------|-------|
| `HomeTopHeader` | `(userName, streakDays, onStreakClick, modifier)` | Header chào + streak badge |
| `HomeSearchBar` | `(query, onQueryChange, onFilterClick, modifier)` | `BasicTextField` + cursor brush + icon filter |
| `StreakMascotBanner` | `(streakDays, onBannerClick, modifier)` | Banner gradient cyan + Squirtle mascot (Coil `AsyncImage`) + sparkle canvas (`infiniteRepeatable`) + `WeeklyStreakTrackerBar` |
| `QuickActionGrid` | `(onCreateDeck, onReviewCards, onViewStats, onViewSaved, modifier)` | 4 nút: Tạo bộ thẻ (🆕), Ôn tập (🔄), Thống kê (📊), Đã lưu (⭐). Dùng `Icons.Outlined.*` |
| `StarterWelcomeHeroCard` | `(userName, language, starterDeck, onStartFirstLesson, modifier)` | Card chào mừng user mới: mascot avatar + banner deck 0% + CTA glow "Bắt đầu học ngay" + 3 bước roadmap (`QuickStepItem`) |
| `ContinueLearningSection` | `(title, studiedCount, totalCount, languageEmoji, levelBadge, language?, level?, onContinueClick, onViewAllClick, modifier)` | Section tiếp tục học + progress bar + badge ngôn ngữ |
| `SpacedRepetitionDueWidget` | `(dueCount, onStartReview, onReviewDueCards, modifier)` | Widget SRS tím + nút "Ôn ngay ⚡", hỗ trợ 2 callback |
| `YourDecksSection` | `(selectedLanguage, learningLanguages, onSelectLanguage, onAddLanguageClick, decks, onDeckClick, onOpenDeckDetail, onStudyDeck, onQuizDeck, onMatchDeck, onCreateNewDeck, onCreateDeckClick, onViewAllClick, modifier)` | Section bộ thẻ: language chips + category filter + "➕ Thêm ngôn ngữ" + grid deck + "Xem tất cả" link + deck count badge |
| `DynamicDeckCard` | `(deck, language, onClick, onStudy, onQuiz, onMatch, modifier)` | Deck card ngang: flag badge + level badge + mini progress bar + 3 nút (Học/Quiz/Ghép) |
| `CreateNewDeckCard` | `(onClick, modifier)` | Card "Tạo bộ mới" 140dp fixed width |
| `AddLanguageBottomSheet` | `(learningLanguages, onSelectNewLanguage, onDismiss)` | Bottom sheet 10 ngôn ngữ: flag + native name + "Đang học" / "+ Thêm" |
| `DailyGoalCard` | `(currentCount, targetCount, percentage, onClick, modifier)` | Vòng % Canvas `drawArc` + `sweepGradient` + cheer messages (3 tiers: 0%/1-99%/100%) |
| `HomeBottomNavBar` | `(selectedTab, onTabSelected, modifier)` | 4 tab: Trang chủ, Khám phá, BXH, Tài khoản |
| `JapaneseFujiArt` | `()` | Coil `AsyncImage(R.drawable.japan_fuji)` trong circle |
| `UsFlagArt` | `()` | Canvas vẽ cờ Mỹ: sọc đỏ/trắng + ngôi sao trắng |
| `KoreaFlagArt` | `()` | Canvas vẽ cờ Hàn: Thái cực đỏ/xanh + quẻ |
| `GlobeArt` | `()` | Canvas vẽ trái đất: châu lục xanh trên nền xanh dương |

**Private composables:**
- `QuickActionItem(title, icon, iconTint, bgColor, onClick, testTag)` — nút hành động nhanh.
- `QuickStepItem(step, icon, title, desc)` — bước roadmap trong hero card.
- `NavBarItem(title, icon, isSelected, onClick, testTag)` — item bottom nav.

**DrawScope extensions:**
- `DrawScope.drawTrigram(pos, color)` — vẽ quẻ Hàn Quốc (3 bars).
- `DrawScope.drawSparkle(center, size, color)` — vẽ ngôi sao lấp lánh.

### 3.12 `ui/detail/DeckDetailScreen.kt`

- `data class DeckTopic(id, title, cardCount, progressPercent, description)`.
- `@Composable fun DeckDetailScreen(deck, cards, onBack, onStartStudy, onStartQuiz, onStartMatch, onSpeak, onToggleStar)` — hero + tag chips + nút "Học ngay" + menu ⋮ + 2 tab Nội dung/Thống kê.
- private: `DeckCoverImageCard(deck)`, `AvatarCircle(emoji, bgColor, offset)`, `StatsDetailTab(deck, cards, onStartQuiz, onStartMatch)`.

### 3.13 `ui/study/FlashcardStudyScreen.kt` (711 dòng)

**`@Composable fun FlashcardStudyScreen(deckTitle, languageTag, cards, userVipLevel=0, allowBack=true, isOnboardingTrial=false, onBack, onSpeak, onToggleStar, onRecordReview?=null, onStartQuiz, onSessionFinished?=null, modifier)`**

- `cardList` = `cards` (shuffled in-place by `remember`).
- **Không còn nút "Chưa thuộc"/"Đã thuộc"** — `onRecordReview` **không được gọi** trong UI (chỉ có trong chữ ký). Chỉ còn 3 nút hành động: Trộn thẻ (`btn_shuffle_cards`), Lưu từ điển (`btn_save_dictionary`), đổi mặt (`btn_flip_indicator`).
- Auto-play `LaunchedEffect(isAutoPlay, currentIndex, isFlipped, isCompleted)`: đọc mặt trước + `delay(2600)` → lật; `delay(2200)` → thẻ tiếp.
- `LaunchedEffect(currentIndex, cardList)` — tự phát âm khi chuyển thẻ (nếu không auto-play).
- Overlay hoàn thành: nếu `isOnboardingTrial` → hiển thị bản khác + nút "Tiếp tục" → `onCompleteTrial`.
- Toast tạm (`LaunchedEffect(toastMessage)` 1500ms).

### 3.14 `ui/quiz/QuizScreen.kt` (1197 dòng)

**`data class StreakMultiplierInfo(multiplier, title, badgeColor, emoji)`**
**`fun getStreakMultiplierInfo(streak)`:** ≤1→1.0 "Cơ Bản"⚡, 2→1.5 "Chuỗi Thăng Hoa"🔥, 3→2.0 "Chuỗi Bùng Nổ"🔥🔥, 4→2.5 "Chuỗi Xuất Sắc"⚡🔥, 5→3.0 "Chuỗi SIÊU CẤP"👑🔥, 6→3.5 "Chuỗi HUYỀN THOẠI"💎🔥, ≥7→5.0 "COMBO THẦN THÁO"👑🔥✨.

**`@Composable fun QuizScreen(deckTitle, languageTag, cards, allowBack=true, isOnboardingTrial=false, onBack, onSpeak, onFinishQuiz(score,total,wrongCards), onAnswerCorrect?=null, onAnswerWrong?=null, onStudyWrongCards?=null, onStudyNext?=null, onCompleteTrial?=null, modifier)`**

- Guard `cards.size<2`. `quizCards=cards.shuffled()`.
- `currentOptions` = 3 nghĩa nhiễu + nghĩa đúng (xáo). Guard nếu < 4 options → dùng 3.
- Đúng → `score++`, `currentStreak++`, `earned = 100*multiplier`, `totalPoints += earned`, popup `Animatable`. Sai → reset streak, thêm vào `wrongCards` (dedup by `id` + `frontWord`).
- `onAnswerCorrect`/`onAnswerWrong` callbacks mới (dùng trong onboarding trial).
- Popup điểm: `popupTrigger++` → `popupAlpha` 0→1, `popupScale` 0.7→1.1 → giữ 900ms → `popupOffsetY` bay lên + `popupAlpha` giảm.
- Overlay kết quả: rank "Thần Thoại Multiplier"(100%) / "Bậc Thầy Từ Vựng"(≥80%) / "Học Viên Xuất Sắc"(≥50%) / "Cố Gắng Lần Sau"(<50%).
  - Nếu có wrong cards → nút "Học lại N từ chưa thuộc" → `onStudyWrongCards`.
  - Nếu ≥50% + không sai → gợi ý học deck tiếp → `onStudyNext`.
  - Nếu `isOnboardingTrial` → nút "Tiếp tục" → `onCompleteTrial`.
- **Pháo hoa** `FireworksCanvas` 8s sau khi xong.

**`@Composable fun FireworksCanvas(durationMs=8000L, modifier, onFinished)`**: `Canvas` + `withFrameNanos`, hạt có trọng lực 0.25/lực cản 0.96/vòng đời, ≤90 hạt, 3 hình (tròn/vuông/sao). `private data class FireworkParticle`.

### 3.15 `ui/match/WordMatchScreen.kt`

- `data class MatchItem(id, text, isFront, pairId)`.
- `@Composable fun WordMatchScreen(deckTitle, cards, onBack, modifier)`: `gameCards=cards.take(6)`; tạo cặp từ↔nghĩa 12 items + `shuffled()`; `LazyVerticalGrid(GridCells.Fixed(2))`; đúng khi `pairId` khớp & khác `isFront`; `LaunchedEffect` thắng khi ghép đủ.

### 3.16 `ui/leaderboard/LeaderboardTab.kt`

- `enum LeaderboardFilterType(POINTS/STREAK/CARDS)`, `enum TimePeriod(THIS_WEEK/THIS_MONTH/ALL_TIME)`, `sealed class RankTrend(Up/Down/Same)`, `data class LeaderboardUser(rank, name, points, streakDays, cardsLearned, trend, avatarBgColor, avatarEmoji, vipLevel, isCurrentUser)`.
- `@Composable fun LeaderboardTab(userName, userVipLevel, userScore, userStreak, userCardsLearned, modifier)`: leaderboardList 10 user mock nhân multiplier (Tuần 1.0/Tháng 3.5/Tất cả 8.0); `currentUserItem` rank 23.
- private: `PodiumStep`, `LeaderboardRowItem`, `RewardCard`.

### 3.17 `ui/dialogs/`

- **`HomeDialogs.kt`**: `ImportCardsDialog(decks, onDismiss, onImportCards(deckId,cards))`, `StatsSummaryDialog(streakDays, masteredCount, totalCardsCount, onDismiss)`, `SavedCardsDialog(starredCards, decks, onSpeak, onToggleStar, onStartStudy/Quiz/Match, onDismiss)`.
- **`UserProfileDialog.kt`**: `UserProfileDialog(userName, userVipLevel=1, streakDays, masteredWordsCount, totalWordsCount, onDismiss, onUpdateName, onSelectVipLevel)`.
- **`CreateDeckAndCardDialogs.kt`**: `CreateCardDialog(deckId, languageCode, onDismiss, onSave)`, `CreateDeckDialog(currentLanguageCode, allCards, onDismiss, onSave)`.

---

## 4. Cơ chế nhận dữ liệu & phát sự kiện

Screens **không** tự gọi ViewModel. `MainActivity` nối callback với `viewModel`:

```kotlin
FlashcardStudyScreen(
    deckTitle = screen.deck.title,
    languageTag = screen.deck.languageCode,
    cards = screen.cards,
    userVipLevel = userVipLevel,
    allowBack = true,
    isOnboardingTrial = false,
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
| Pháo hoa | `Canvas` + `withFrameNanos` | hạt trọng lực/lực cản/vòng đời, ≤90 hạt, 8s |
| Popup điểm quiz | `Animatable` (alpha/scale/offsetY) | pop → giữ 900ms → bay lên mờ |
| Streak tracker pulse | `infiniteRepeatable` + `animateFloat` | ⭐/🔥 nhấp nháy scale 1.0→1.08x |
| Banner sparkle | `infiniteRepeatable` + `animateFloat` | alpha 0.4→1.0 pulse |
| Carousel/step | `AnimatedContent` | slide+fade |
| Overlay | `AnimatedVisibility` | `fadeIn + scaleIn` |
| Mở rộng chủ đề | `animateContentSize` | `expand/shrinkVertically` |
| Lơ lửng | `infiniteRepeatable` | `animateFloat` đảo chiều |
| Floating scroll btn | `AnimatedVisibility` + `animateFloatAsState` (rotation) | fade+scale + xoay 90° |

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
      tab_home, tab_explore, tab_leaderboard, tab_profile,
      streak_day_T2..streak_day_CN
Study: btn_back_study, btn_shuffle_cards, btn_save_dictionary, btn_flip_indicator,
       flashcard_3d_container, btn_pronounce_front, btn_star_card
Quiz: quiz_option_<text>, btn_next_quiz, btn_study_next_card
Match: match_card_<id>
DeckDetail: btn_quiz_mode_card, btn_match_mode_card
Leaderboard: leaderboard_screen, time_period_dropdown
Dialogs: input_front_word, input_back_meaning, btn_save_card, input_deck_title, btn_save_deck,
         btn_close_profile, saved_cards_search_input, saved_word_card_<id>, filter_lang_all, filter_lang_<code>
Bubble: bubble_<text>
```

---

## 7. Cẩm nang sửa đổi

| Yêu cầu | Nơi sửa |
| --- | --- |
| Đổi màu | `ui/theme/Color.kt` |
| Đổi font | `ui/theme/Type.kt` |
| Thêm màn hình | composable + `ScreenState` + `when` `MainActivity` + hàm `MainViewModel` |
| Sửa home | `ui/home/HomeScreen.kt` + `HomeComponents.kt` |
| Sửa streak tracker | `ui/home/StreakWeeklyTracker.kt` |
| Sửa thẻ học | `ui/study/FlashcardStudyScreen.kt` + `Flashcard3DView.kt` |
| Sửa quiz | `ui/quiz/QuizScreen.kt` |
| Sửa ghép từ | `ui/match/WordMatchScreen.kt` |
| Sửa VIP | `components/VipAvatarFrame.kt` |
| Sửa BXH | `ui/leaderboard/LeaderboardTab.kt` |
| Sửa dialog | `ui/dialogs/*` |
| Thêm bubble | `LanguageSpeechBubble.kt` |

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
`graphicsLayer` (rotationZ/scale/alpha) chạy trên GPU, **không trigger recomposition** mỗi frame → mượt hơn, tiết kiệm CPU.

### Q12. Vì sao `VipCardFrame` cache `Path`/`PathMeasure` bằng `remember`?
Để tránh cấp phát object (garbage collection) trong `drawBehind` mỗi frame — giữ 60fps cho animation viền.

### Q13. Study screen thay đổi gì so với phiên bản trước?
Phiên bản mới **bỏ nút "Chưa thuộc"/"Đã thuộc"** (chỉ giữ trong chữ ký callback). UX chỉ còn flip + auto-play + shuffle + bookmark. `onRecordReview` không được gọi trong UI — quyền quyết định mastery thuộc về ViewModel/quiz flow.

### Q14. Quiz screen có callback mới gì?
Bổ sung `onAnswerCorrect(card)`, `onAnswerWrong(card)`, `onStudyWrongCards(cards)`, `onStudyNext()`, `onCompleteTrial()` — phục vụ onboarding trial flow (quiz thử → đăng ký).

### Q15. StreakWeeklyTracker hoạt động ra sao?
`StreakTimeHelper.getWeeklyDaysInfo(streakDays)` tạo 7 DayOfWeekInfo dựa trên `Calendar.getInstance()`. Ngày trước hôm nay: `COMPLETED` nếu streak đủ dài. Hôm nay: pulse ⭐ hoặc 🔥. Ngày sau: `UPCOMING`. `WeeklyStreakTrackerBar` render 7 cột ngang với animation pulse.

### Q16. HomeScreen phân biệt user mới/cũ thế nào?
`isFirstTimeUser = masteredWordsCount == 0 && streakDays <= 1`. User mới → `StarterWelcomeHeroCard` (CTA glow + roadmap 3 bước). User cũ → `StreakMascotBanner` (mascot + tracker 7 ngày).
