package com.example.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.DeckEntity
import com.example.data.model.FlashCardEntity
import com.example.ui.dialogs.CreateDeckDialog
import com.example.ui.dialogs.ImportCardsDialog
import com.example.ui.dialogs.SavedCardsDialog
import com.example.ui.dialogs.StatsSummaryDialog
import com.example.ui.components.VipAvatarFrame
import com.example.ui.components.VipLevel
import com.example.ui.components.VipLevelSelectorCard
import com.example.ui.leaderboard.LeaderboardTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    selectedLanguage: AppLanguage,
    onSelectLanguage: (AppLanguage) -> Unit,
    learningLanguages: List<AppLanguage> = listOf(selectedLanguage),
    onAddLearningLanguage: (AppLanguage) -> Unit = {},
    decks: List<DeckEntity>,
    allDecksList: List<DeckEntity> = emptyList(),
    starredCards: List<FlashCardEntity> = emptyList(),
    allCardsList: List<FlashCardEntity> = emptyList(),
    streakDays: Int = 7,
    masteredWordsCount: Int = 15,
    totalWordsCount: Int = 20,
    userName: String = "bạn",
    userVipLevel: Int = 1,
    userTotalPoints: Int = 1250,
    onSelectVipLevel: (Int) -> Unit = {},
    onOpenDeckDetail: (DeckEntity) -> Unit = {},
    onStudyDeck: (DeckEntity) -> Unit,
    onQuizDeck: (DeckEntity) -> Unit,
    onMatchDeck: (DeckEntity) -> Unit,
    onAddCardToDeck: (DeckEntity) -> Unit,
    onCreateNewDeck: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenStarred: () -> Unit,
    onSpeak: (String, String) -> Unit = { _, _ -> },
    onToggleStar: (Long, Boolean) -> Unit = { _, _ -> },
    onStartStudySaved: (List<FlashCardEntity>, String, String) -> Unit = { _, _, _ -> },
    onStartQuizSaved: (List<FlashCardEntity>, String, String) -> Unit = { _, _, _ -> },
    onStartMatchSaved: (List<FlashCardEntity>, String, String) -> Unit = { _, _, _ -> },
    onCreateDeckDirect: (DeckEntity, List<FlashCardEntity>) -> Unit = { _, _ -> },
    onImportCardsDirect: (String, List<FlashCardEntity>) -> Unit = { _, _ -> },
    onStudyByLang: (String) -> Unit = {},
    studySchedule: com.example.data.model.StudyScheduleEntity? = null,
    onUpdateScheduleTime: (Int, Int) -> Unit = { _, _ -> },
    onTestSmartNotification: () -> Unit = {},
    onTestMilestoneNotification: (Int) -> Unit = {},
    continueLearning: com.example.ui.viewmodel.ContinueLearningInfo? = null,
    decksWithStats: List<com.example.data.model.DeckWithStats> = emptyList(),
    onContinueLearning: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedBottomTab by remember { mutableIntStateOf(0) }
    var showReviewOverlay by remember { mutableStateOf(false) }

    val homeScrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    // Dialog & Sheet States
    var showStatsDialog by remember { mutableStateOf(false) }
    var showSavedCardsDialog by remember { mutableStateOf(false) }
    var showCreateDeckDialog by remember { mutableStateOf(false) }
    var showImportCardsDialog by remember { mutableStateOf(false) }
    var showLanguageFilterSheet by remember { mutableStateOf(false) }
    var showAllDecksSheet by remember { mutableStateOf(false) }
    var showAddLanguageSheet by remember { mutableStateOf(false) }

    val filterSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val allDecksSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val effectiveDecks = remember(allDecksList, decks) {
        if (allDecksList.isNotEmpty()) allDecksList else decks
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF0F9FF),
                        Color(0xFFE0F2FE),
                        Color(0xFFF8FAFC)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Main Content Area based on Selected Bottom Tab or Review Overlay
            if (showReviewOverlay) {
                ReviewHistoryTab(
                    decks = effectiveDecks,
                    learningLanguages = learningLanguages,
                    selectedLanguage = selectedLanguage,
                    onOpenDeckDetail = onOpenDeckDetail,
                    onStudyDeck = onStudyDeck,
                    onQuizDeck = onQuizDeck,
                    onMatchDeck = onMatchDeck,
                    onViewSaved = { showSavedCardsDialog = true },
                    onBack = { showReviewOverlay = false },
                    modifier = Modifier.weight(1f)
                )
            } else {
                when (selectedBottomTab) {
                    0 -> {
                        // TAB 0: TRANG CHỦ
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(homeScrollState)
                                .padding(bottom = 12.dp)
                        ) {
                            Spacer(modifier = Modifier.height(6.dp))

                            // 1. TOP HEADER: "Xin chào, 👋", "Hôm nay học gì nào?", Streak Pill "🔥 7"
                            HomeTopHeader(
                                userName = userName,
                                streakDays = streakDays,
                                onStreakClick = { showStatsDialog = true }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Dynamic First-Time vs Returning User State
                            val isFirstTimeUser = masteredWordsCount == 0 && streakDays <= 1
                            val currentActiveDeck = remember(decks, effectiveDecks, selectedLanguage.code) {
                                decks.firstOrNull()
                                    ?: effectiveDecks.firstOrNull { it.languageCode == selectedLanguage.code }
                                    ?: effectiveDecks.firstOrNull()
                            }

                            if (isFirstTimeUser) {
                                // 2a. First-time Starter Welcome Hero Card
                                StarterWelcomeHeroCard(
                                    userName = userName,
                                    language = selectedLanguage,
                                    onStartFirstLesson = {
                                        if (currentActiveDeck != null) {
                                            onStudyDeck(currentActiveDeck)
                                        } else {
                                            onStudyByLang(selectedLanguage.code)
                                        }
                                    }
                                )
                            } else {
                                // 2b. Returning user Mascot Banner
                                StreakMascotBanner(
                                    streakDays = streakDays,
                                    onBannerClick = { showStatsDialog = true }
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // 3. QUICK ACTION GRID: "Tạo bộ thẻ", "Ôn tập", "Thống kê", "Đã lưu"
                            QuickActionGrid(
                                onCreateDeck = { showCreateDeckDialog = true },
                                onReviewCards = { showReviewOverlay = true },
                                onViewStats = { showStatsDialog = true },
                                onViewSaved = { showSavedCardsDialog = true }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                                // 5. "Tiếp tục học" SECTION: deck học gần nhất + tiến trình THẬT
                                //    (thẻ "Đã thuộc" chỉ tính khi trả lời ĐÚNG trong Quiz)
                                val continueInfo = continueLearning
                                if (continueInfo != null && continueInfo.totalCount > 0) {
                                    ContinueLearningSection(
                                        title = continueInfo.deck.title,
                                        studiedCount = continueInfo.masteredCount,
                                        totalCount = continueInfo.totalCount,
                                        language = selectedLanguage,
                                        level = continueInfo.deck.level,
                                        onContinueClick = onContinueLearning,
                                        onViewAllClick = { showAllDecksSheet = true }
                                    )
                                } else {
                                    val totalCount = totalWordsCount.takeIf { it > 0 } ?: 50
                                    ContinueLearningSection(
                                        title = "${selectedLanguage.displayName} cơ bản",
                                        studiedCount = masteredWordsCount.coerceIn(0, totalCount),
                                        totalCount = totalCount,
                                        language = selectedLanguage,
                                        level = "Mới bắt đầu",
                                        onContinueClick = { onStudyByLang(selectedLanguage.code) },
                                        onViewAllClick = { showAllDecksSheet = true }
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // SRS Due Widget (if returning user with some progress)
                                if (!isFirstTimeUser) {
                                    SpacedRepetitionDueWidget(
                                        dueCount = 8,
                                        onReviewDueCards = { showReviewOverlay = true }
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }

                                // 6. "Bộ thẻ của bạn" SECTION: Multi-language chips + Add Language + Deck Cards
                                val yourDecks = remember(decks, effectiveDecks, selectedLanguage.code) {
                                    decks.ifEmpty { effectiveDecks.filter { it.languageCode == selectedLanguage.code } }
                                }
                                YourDecksSection(
                                    decks = yourDecks,
                                    learningLanguages = learningLanguages,
                                    selectedLanguage = selectedLanguage,
                                    onSelectLanguage = onSelectLanguage,
                                    onAddLanguageClick = { showAddLanguageSheet = true },
                                    onOpenDeckDetail = onOpenDeckDetail,
                                    onStudyDeck = onStudyDeck,
                                    onQuizDeck = onQuizDeck,
                                    onMatchDeck = onMatchDeck,
                                    onCreateDeckClick = { showCreateDeckDialog = true },
                                    onViewAllClick = { showAllDecksSheet = true }
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // 7. "Mục tiêu hôm nay" CARD: Circular Ring, count / target, Dynamic cheer text
                                val goalCurrent = masteredWordsCount
                                val goalTarget = if (totalWordsCount > 0) totalWordsCount else 20
                                val goalPercentage = if (goalTarget > 0) ((goalCurrent * 100) / goalTarget).coerceIn(0, 100) else 0

                                DailyGoalCard(
                                    currentCount = goalCurrent,
                                    targetCount = goalTarget,
                                    percentage = goalPercentage,
                                    onClick = { showStatsDialog = true }
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                        }
                    }

                    1 -> {
                        // TAB 1: KHÁM PHÁ (Explore all multilingual decks & categories)
                        ExploreDecksTab(
                            decks = effectiveDecks,
                            selectedLanguage = selectedLanguage,
                            onSelectLanguage = onSelectLanguage,
                            onOpenDeckDetail = onOpenDeckDetail,
                            onStudyDeck = onStudyDeck,
                            onQuizDeck = onQuizDeck,
                            onMatchDeck = onMatchDeck,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    2 -> {
                        // TAB 2: BẢNG XẾP HẠNG (Leaderboard strictly matching provided mockup)
                        LeaderboardTab(
                            userName = userName,
                            userVipLevel = userVipLevel,
                            userScore = if (userTotalPoints > 0) userTotalPoints else (if (masteredWordsCount > 0) (masteredWordsCount * 100) + (streakDays * 50) else 1250),
                            userStreak = streakDays,
                            userCardsLearned = masteredWordsCount,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    3 -> {
                        // TAB 3: TÀI KHOẢN (Profile, Achievements, Settings)
                        AccountProfileTab(
                            userName = userName,
                            userVipLevel = userVipLevel,
                            streakDays = streakDays,
                            masteredCount = masteredWordsCount,
                            totalCount = totalWordsCount,
                            studySchedule = studySchedule,
                            onUpdateScheduleTime = onUpdateScheduleTime,
                            onOpenEditProfile = onOpenProfile,
                            onSelectVipLevel = onSelectVipLevel,
                            onViewStats = { showStatsDialog = true },
                            onTestSmartNotification = onTestSmartNotification,
                            onTestMilestoneNotification = onTestMilestoneNotification,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // FIXED BOTTOM NAVIGATION BAR: Trang chủ, Khám phá, BXH, Tài khoản
            HomeBottomNavBar(
                selectedTab = if (showReviewOverlay) -1 else selectedBottomTab,
                onTabSelected = { tabIndex ->
                    selectedBottomTab = tabIndex
                    showReviewOverlay = false
                }
            )
        }

        // FLOATING SCROLL BUTTON: Mũi tên dạng thẳng (<-) góc phải chuyển đổi linh hoạt (Xuống dưới / Lên đầu trang)
        val isScrollable by remember { derivedStateOf { homeScrollState.maxValue > 80 } }
        val isNearBottom by remember { derivedStateOf { homeScrollState.maxValue > 0 && homeScrollState.value >= (homeScrollState.maxValue * 0.6f) } }
        val showScrollButton by remember { derivedStateOf { selectedBottomTab == 0 && !showReviewOverlay && isScrollable } }
        val arrowRotation by animateFloatAsState(
            targetValue = if (isNearBottom) 90f else -90f,
            animationSpec = androidx.compose.animation.core.spring(
                dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
                stiffness = androidx.compose.animation.core.Spring.StiffnessMediumLow
            ),
            label = "arrow_rotation"
        )

        AnimatedVisibility(
            visible = showScrollButton,
            enter = fadeIn(androidx.compose.animation.core.tween(200)) + scaleIn(androidx.compose.animation.core.tween(200)),
            exit = fadeOut(androidx.compose.animation.core.tween(150)) + scaleOut(androidx.compose.animation.core.tween(150)),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 80.dp)
        ) {
            Surface(
                onClick = {
                    coroutineScope.launch {
                        if (isNearBottom) {
                            // Khi đang ở phía dưới cùng -> cuộn mượt lên đầu trang
                            homeScrollState.animateScrollTo(
                                0,
                                animationSpec = androidx.compose.animation.core.tween(450, easing = androidx.compose.animation.core.FastOutSlowInEasing)
                            )
                        } else {
                            // Khi đang ở phía trên -> cuộn mượt xuống cuối trang
                            homeScrollState.animateScrollTo(
                                homeScrollState.maxValue,
                                animationSpec = androidx.compose.animation.core.tween(450, easing = androidx.compose.animation.core.FastOutSlowInEasing)
                            )
                        }
                    }
                },
                shape = CircleShape,
                color = Color(0xFF0284C7),
                shadowElevation = 8.dp,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White),
                modifier = Modifier
                    .size(48.dp)
                    .testTag("btn_scroll_navigation")
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = if (isNearBottom) "Cuộn lên đầu trang" else "Cuộn xuống cuối trang",
                        tint = Color.White,
                        modifier = Modifier
                            .size(26.dp)
                            .graphicsLayer {
                                rotationZ = arrowRotation
                            }
                    )
                }
            }
        }
    }

    // ----------------------------------------------------
    // DIALOGS & BOTTOM SHEETS
    // ----------------------------------------------------

    // Stats Dialog
    if (showStatsDialog) {
        StatsSummaryDialog(
            streakDays = streakDays,
            masteredCount = masteredWordsCount,
            totalCardsCount = totalWordsCount,
            onDismiss = { showStatsDialog = false }
        )
    }

    // Saved/Starred Cards Dialog
    if (showSavedCardsDialog) {
        SavedCardsDialog(
            starredCards = starredCards,
            decks = effectiveDecks,
            onSpeak = onSpeak,
            onToggleStar = onToggleStar,
            onStartStudy = onStartStudySaved,
            onStartQuiz = onStartQuizSaved,
            onStartMatch = onStartMatchSaved,
            onDismiss = { showSavedCardsDialog = false }
        )
    }

    // Create Deck Dialog
    if (showCreateDeckDialog) {
        CreateDeckDialog(
            currentLanguageCode = selectedLanguage.code,
            allCards = allCardsList,
            onDismiss = { showCreateDeckDialog = false },
            onSave = { deck, selectedCards ->
                onCreateDeckDirect(deck, selectedCards)
                showCreateDeckDialog = false
            }
        )
    }

    // Import Cards Dialog
    if (showImportCardsDialog) {
        ImportCardsDialog(
            decks = effectiveDecks,
            onDismiss = { showImportCardsDialog = false },
            onImportCards = { deckId, cards ->
                onImportCardsDirect(deckId, cards)
                showImportCardsDialog = false
            }
        )
    }

    // Language Filter Modal Bottom Sheet
    if (showLanguageFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showLanguageFilterSheet = false },
            sheetState = filterSheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Chọn ngôn ngữ hiển thị",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E1B4B)
                )
                Spacer(modifier = Modifier.height(16.dp))

                AppLanguage.values().forEach { lang ->
                    val isSelected = selectedLanguage == lang
                    Surface(
                        onClick = {
                            onSelectLanguage(lang)
                            showLanguageFilterSheet = false
                        },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) Color(0xFFEEF2FF) else Color(0xFFF8FAFC),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) Color(0xFF6366F1) else Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(lang.flagEmoji, fontSize = 22.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = lang.displayName,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E293B)
                                    )
                                    Text(
                                        text = lang.nativeName,
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color(0xFF6366F1),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // View All Decks / Continue Learning by Languages Bottom Sheet
    if (showAllDecksSheet) {
        var sheetLangFilter by remember { mutableStateOf<String?>(null) }
        val learningLangCodes = remember(learningLanguages) {
            learningLanguages.map { it.code }.toSet()
        }
        val userLearningDecks = remember(effectiveDecks, learningLangCodes) {
            effectiveDecks.filter { it.languageCode in learningLangCodes }
        }
        val distinctLangCodes = remember(userLearningDecks, learningLanguages) {
            learningLanguages.map { it.code }.filter { code ->
                userLearningDecks.any { it.languageCode == code }
            }
        }
        val displayedDecks = remember(sheetLangFilter, userLearningDecks) {
            if (sheetLangFilter == null) userLearningDecks else userLearningDecks.filter { it.languageCode == sheetLangFilter }
        }
        val decksByLanguage = remember(displayedDecks) {
            displayedDecks.groupBy { it.languageCode }
        }
        val statsById = remember(decksWithStats) {
            decksWithStats.associateBy { it.deck.id }
        }

        ModalBottomSheet(
            onDismissRequest = {
                showAllDecksSheet = false
                sheetLangFilter = null
            },
            sheetState = allDecksSheetState,
            containerColor = Color(0xFFFAFAFE),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Tiến trình học tập",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E1B4B)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (distinctLangCodes.size <= 1) {
                                val lang = distinctLangCodes.firstOrNull()?.let { AppLanguage.fromCode(it) } ?: selectedLanguage
                                "Đang học ${lang.displayName} (${displayedDecks.size} bộ thẻ)"
                            } else {
                                "Đang học ${distinctLangCodes.size} ngôn ngữ (${userLearningDecks.size} bộ thẻ)"
                            },
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    IconButton(
                        onClick = {
                            showAllDecksSheet = false
                            sheetLangFilter = null
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFF1F5F9), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // If multiple languages exist, show filter chips
                if (distinctLangCodes.size > 1) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // "Tất cả" chip
                        val isAllSelected = sheetLangFilter == null
                        Surface(
                            onClick = { sheetLangFilter = null },
                            shape = RoundedCornerShape(14.dp),
                            color = if (isAllSelected) Color(0xFF0284C7) else Color.White,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isAllSelected) Color(0xFF0284C7) else Color(0xFFE2E8F0)
                            ),
                            shadowElevation = if (isAllSelected) 2.dp else 0.dp
                        ) {
                            Text(
                                text = "🌐 Tất cả (${userLearningDecks.size})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isAllSelected) Color.White else Color(0xFF475569),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }

                        // Each language chip
                        distinctLangCodes.forEach { langCode ->
                            val lang = AppLanguage.fromCode(langCode)
                            val isSelected = sheetLangFilter == langCode
                            val count = userLearningDecks.count { it.languageCode == langCode }

                            Surface(
                                onClick = { sheetLangFilter = langCode },
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) Color(0xFF0284C7) else Color.White,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) Color(0xFF0284C7) else Color(0xFFE2E8F0)
                                ),
                                shadowElevation = if (isSelected) 2.dp else 0.dp
                            ) {
                                Text(
                                    text = "${lang.flagEmoji} ${lang.displayName} ($count)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else Color(0xFF475569),
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }
                        }

                        // ➕ Add Language button in sheet
                        Surface(
                            onClick = {
                                showAllDecksSheet = false
                                showAddLanguageSheet = true
                            },
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFF0FDF4),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Thêm ngôn ngữ",
                                    tint = Color(0xFF16A34A),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Thêm ngôn ngữ",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF16A34A)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                }

                // List of Decks Grouped by Language or Single Language List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(460.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    decksByLanguage.forEach { (langCode, langDecks) ->
                        val lang = AppLanguage.fromCode(langCode)

                        // If "Tất cả" is selected and we have multiple languages, render language header section
                        if (distinctLangCodes.size > 1 && sheetLangFilter == null) {
                            item(key = "header_$langCode") {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 6.dp, bottom = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(text = lang.flagEmoji, fontSize = 20.sp)
                                    Text(
                                        text = lang.displayName,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFE0F2FE)
                                    ) {
                                        Text(
                                            text = "${langDecks.size} bộ thẻ",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF0284C7),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }

                        items(langDecks, key = { it.id }) { deck ->
                            val deckStat = statsById[deck.id]
                            DeckListCard(
                                deck = deck,
                                masteredCount = deckStat?.masteredCards,
                                totalCardCount = deckStat?.totalCards,
                                onClickDetail = {
                                    showAllDecksSheet = false
                                    onOpenDeckDetail(deck)
                                },
                                onStudy = {
                                    showAllDecksSheet = false
                                    onStudyDeck(deck)
                                },
                                onQuiz = {
                                    showAllDecksSheet = false
                                    onQuizDeck(deck)
                                },
                                onMatch = {
                                    showAllDecksSheet = false
                                    onMatchDeck(deck)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Add New Language Sheet
    if (showAddLanguageSheet) {
        AddLanguageBottomSheet(
            learningLanguages = learningLanguages,
            onSelectNewLanguage = { lang ->
                onAddLearningLanguage(lang)
                onSelectLanguage(lang)
                showAddLanguageSheet = false
            },
            onDismiss = { showAddLanguageSheet = false }
        )
    }
}

// ----------------------------------------------------
// SUPPORTING TABS & COMPOSABLES
// ----------------------------------------------------

@Composable
private fun SearchResultsView(
    query: String,
    results: List<DeckEntity>,
    onOpenDeckDetail: (DeckEntity) -> Unit,
    onStudyDeck: (DeckEntity) -> Unit,
    onQuizDeck: (DeckEntity) -> Unit,
    onMatchDeck: (DeckEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(
            text = "Kết quả tìm kiếm cho \"$query\" (${results.size})",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )
        Spacer(modifier = Modifier.height(10.dp))

        if (results.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🔍", fontSize = 36.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Không tìm thấy bộ từ vựng phù hợp",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF64748B)
                    )
                }
            }
        } else {
            results.forEach { deck ->
                DeckListCard(
                    deck = deck,
                    onClickDetail = { onOpenDeckDetail(deck) },
                    onStudy = { onStudyDeck(deck) },
                    onQuiz = { onQuizDeck(deck) },
                    onMatch = { onMatchDeck(deck) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun DeckListCard(
    deck: DeckEntity,
    onStudy: () -> Unit,
    onQuiz: () -> Unit,
    onMatch: () -> Unit,
    onClickDetail: () -> Unit = onStudy,
    masteredCount: Int? = null,
    totalCardCount: Int? = null,
    modifier: Modifier = Modifier
) {
    val showProgress = masteredCount != null && totalCardCount != null && totalCardCount > 0
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(18.dp), spotColor = Color(0x0D000000))
            .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(18.dp))
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClickDetail() }
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color(0xFFEEF2FF), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = deck.iconEmoji, fontSize = 22.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = deck.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = if (totalCardCount != null) "$totalCardCount thẻ • ${deck.level}" else "${deck.cardCount} thẻ • ${deck.level}",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Detail",
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tiến trình thật: "Đã thuộc X/Y thẻ" — chỉ tính thẻ trả lời ĐÚNG trong Quiz
            if (showProgress && masteredCount != null && totalCardCount != null) {
                val percent = ((masteredCount.toFloat() / totalCardCount) * 100f).toInt().coerceIn(0, 100)
                Text(
                    text = "✅ Đã thuộc $masteredCount/$totalCardCount thẻ ($percent%)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (percent >= 100) Color(0xFF16A34A) else Color(0xFF475569)
                )
                Spacer(modifier = Modifier.height(5.dp))
                LinearProgressIndicator(
                    progress = { percent / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (percent >= 100) Color(0xFF16A34A) else Color(0xFF0284C7),
                    trackColor = Color(0xFFE2E8F0)
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Action Buttons: Học, Trắc nghiệm, Ghép thẻ
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onStudy,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    modifier = Modifier.weight(1.2f).height(36.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Học ngay", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onQuiz,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFECFDF5)),
                    modifier = Modifier.weight(1f).height(36.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.Quiz, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Quiz", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF047857))
                }

                Button(
                    onClick = onMatch,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFF7ED)),
                    modifier = Modifier.weight(1f).height(36.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.Extension, contentDescription = null, tint = Color(0xFFEA580C), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ghép", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC2410C))
                }
            }
        }
    }
}

/**
 * Tab 1: Khám phá & Tìm kiếm chủ đề
 */
@Composable
private fun ExploreDecksTab(
    decks: List<DeckEntity>,
    selectedLanguage: AppLanguage,
    onSelectLanguage: (AppLanguage) -> Unit,
    onOpenDeckDetail: (DeckEntity) -> Unit = {},
    onStudyDeck: (DeckEntity) -> Unit,
    onQuizDeck: (DeckEntity) -> Unit,
    onMatchDeck: (DeckEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var exploreSearchQuery by remember { mutableStateOf("") }
    var selectedTopicCategory by remember { mutableStateOf("Tất cả") }

    val topicCategories = listOf(
        "Tất cả",
        "Giao tiếp",
        "Du lịch",
        "Công việc & Kinh doanh",
        "Học thuật & Thi cử",
        "Ẩm thực",
        "Đời sống & Hàng ngày"
    )

    // Filter decks by language first, then search query and topic category
    val languageDecks = remember(decks, selectedLanguage.code) {
        decks.filter { it.languageCode == selectedLanguage.code }
    }

    val displayedDecks = remember(languageDecks, exploreSearchQuery, selectedTopicCategory) {
        languageDecks.filter { deck ->
            val matchesQuery = if (exploreSearchQuery.isBlank()) {
                true
            } else {
                deck.title.contains(exploreSearchQuery, ignoreCase = true) ||
                deck.subtitle.contains(exploreSearchQuery, ignoreCase = true) ||
                deck.level.contains(exploreSearchQuery, ignoreCase = true)
            }

            val matchesTopic = if (selectedTopicCategory == "Tất cả") {
                true
            } else {
                when (selectedTopicCategory) {
                    "Giao tiếp" -> deck.title.contains("giao tiếp", ignoreCase = true) || deck.subtitle.contains("giao tiếp", ignoreCase = true) || deck.title.contains("chào hỏi", ignoreCase = true)
                    "Du lịch" -> deck.title.contains("du lịch", ignoreCase = true) || deck.subtitle.contains("du lịch", ignoreCase = true) || deck.title.contains("sân bay", ignoreCase = true) || deck.title.contains("khách sạn", ignoreCase = true)
                    "Công việc & Kinh doanh" -> deck.title.contains("công việc", ignoreCase = true) || deck.title.contains("kinh doanh", ignoreCase = true) || deck.title.contains("văn phòng", ignoreCase = true) || deck.title.contains("business", ignoreCase = true)
                    "Học thuật & Thi cử" -> deck.title.contains("ielts", ignoreCase = true) || deck.title.contains("toeic", ignoreCase = true) || deck.title.contains("hsk", ignoreCase = true) || deck.title.contains("jlpt", ignoreCase = true) || deck.title.contains("topik", ignoreCase = true) || deck.level.contains("Nâng cao", ignoreCase = true)
                    "Ẩm thực" -> deck.title.contains("ăn", ignoreCase = true) || deck.title.contains("uống", ignoreCase = true) || deck.title.contains("ẩm thực", ignoreCase = true) || deck.title.contains("món", ignoreCase = true)
                    "Đời sống & Hàng ngày" -> deck.title.contains("hàng ngày", ignoreCase = true) || deck.title.contains("cơ bản", ignoreCase = true) || deck.title.contains("đời sống", ignoreCase = true)
                    else -> true
                }
            }

            matchesQuery && matchesTopic
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Khám phá kho từ vựng",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )
        Text(
            text = "Tìm kiếm chủ đề và bộ thẻ theo ngôn ngữ",
            fontSize = 13.sp,
            color = Color(0xFF64748B)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 1. SEARCH BAR: "Tìm kiếm bộ thẻ, chủ đề..."
        HomeSearchBar(
            query = exploreSearchQuery,
            onQueryChange = { exploreSearchQuery = it },
            onFilterClick = { /* Clear or focus */ },
            modifier = Modifier.padding(horizontal = 0.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Horizontal Language selector chips
        Text(
            text = "Ngôn ngữ học tập",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF475569)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppLanguage.values().forEach { lang ->
                val isSelected = selectedLanguage == lang
                Surface(
                    onClick = { onSelectLanguage(lang) },
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) Color(0xFF0284C7) else Color.White,
                    shadowElevation = 2.dp,
                    modifier = Modifier.border(
                        1.dp,
                        if (isSelected) Color(0xFF0284C7) else Color(0xFFE2E8F0),
                        RoundedCornerShape(14.dp)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(lang.flagEmoji, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = lang.displayName,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else Color(0xFF1E293B)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Topic Category Filter Chips
        Text(
            text = "Chủ đề thẻ từ vựng",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF475569)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            topicCategories.forEach { category ->
                val isSelected = selectedTopicCategory == category
                Surface(
                    onClick = { selectedTopicCategory = category },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) Color(0xFFE0F2FE) else Color.White,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) Color(0xFF0284C7) else Color(0xFFE2E8F0)
                    )
                ) {
                    Text(
                        text = category,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color(0xFF0284C7) else Color(0xFF64748B),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Deck list or search results
        if (displayedDecks.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🔍", fontSize = 36.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (exploreSearchQuery.isNotBlank()) "Không tìm thấy bộ thẻ phù hợp với \"$exploreSearchQuery\"" else "Chưa có bộ thẻ cho chủ đề này",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Danh sách bộ thẻ (${displayedDecks.size})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            displayedDecks.forEach { deck ->
                DeckListCard(
                    deck = deck,
                    onClickDetail = { onOpenDeckDetail(deck) },
                    onStudy = { onStudyDeck(deck) },
                    onQuiz = { onQuizDeck(deck) },
                    onMatch = { onMatchDeck(deck) }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

/**
 * Tab 2: Ôn tập & Rèn luyện
 */
@Composable
private fun ReviewHistoryTab(
    decks: List<DeckEntity>,
    learningLanguages: List<AppLanguage> = listOf(AppLanguage.ENGLISH),
    selectedLanguage: AppLanguage = AppLanguage.ENGLISH,
    onOpenDeckDetail: (DeckEntity) -> Unit = {},
    onStudyDeck: (DeckEntity) -> Unit,
    onQuizDeck: (DeckEntity) -> Unit,
    onMatchDeck: (DeckEntity) -> Unit,
    onViewSaved: () -> Unit,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedLangFilter by remember { mutableStateOf<String?>(null) }
    val learningLangCodes = remember(learningLanguages) {
        learningLanguages.map { it.code }.toSet()
    }
    val userLearningDecks = remember(decks, learningLangCodes) {
        decks.filter { it.languageCode in learningLangCodes }
    }
    val distinctLangCodes = remember(userLearningDecks, learningLanguages) {
        learningLanguages.map { it.code }.filter { code ->
            userLearningDecks.any { it.languageCode == code }
        }
    }
    val displayedDecks = remember(selectedLangFilter, userLearningDecks) {
        if (selectedLangFilter == null) userLearningDecks else userLearningDecks.filter { it.languageCode == selectedLangFilter }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (onBack != null) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Quay lại",
                        tint = Color(0xFF1E1B4B)
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Luyện tập & Ôn tập",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E1B4B)
                )
                Text(
                    text = if (distinctLangCodes.size <= 1) {
                        val lang = distinctLangCodes.firstOrNull()?.let { AppLanguage.fromCode(it) } ?: selectedLanguage
                        "Đang ôn tập ${lang.displayName} (${displayedDecks.size} bộ thẻ)"
                    } else {
                        "Đang ôn tập ${distinctLangCodes.size} ngôn ngữ (${userLearningDecks.size} bộ thẻ)"
                    },
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Language Filter Chips (Synchronized with Homepage learning languages)
        if (distinctLangCodes.size > 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // "Tất cả" chip
                val isAllSelected = selectedLangFilter == null
                Surface(
                    onClick = { selectedLangFilter = null },
                    shape = RoundedCornerShape(14.dp),
                    color = if (isAllSelected) Color(0xFF0284C7) else Color.White,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isAllSelected) Color(0xFF0284C7) else Color(0xFFE2E8F0)
                    ),
                    shadowElevation = if (isAllSelected) 2.dp else 0.dp
                ) {
                    Text(
                        text = "🌐 Tất cả (${userLearningDecks.size})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isAllSelected) Color.White else Color(0xFF475569),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }

                // Each language chip
                distinctLangCodes.forEach { langCode ->
                    val lang = AppLanguage.fromCode(langCode)
                    val isSelected = selectedLangFilter == langCode
                    val count = userLearningDecks.count { it.languageCode == langCode }

                    Surface(
                        onClick = { selectedLangFilter = langCode },
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) Color(0xFF0284C7) else Color.White,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) Color(0xFF0284C7) else Color(0xFFE2E8F0)
                        ),
                        shadowElevation = if (isSelected) 2.dp else 0.dp
                    ) {
                        Text(
                            text = "${lang.flagEmoji} ${lang.displayName} ($count)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else Color(0xFF475569),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Saved Starred Vocabulary Banner
        Surface(
            onClick = onViewSaved,
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFFFFFBEB),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(Color(0xFFFEF3C7), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("⭐", fontSize = 22.sp)
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Từ vựng yêu thích & đã lưu",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF92400E)
                    )
                    Text(
                        text = "Ôn tập danh sách từ khó bạn đã gắn dấu sao",
                        fontSize = 12.sp,
                        color = Color(0xFFB45309)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Chế độ luyện tập nhanh",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1B4B)
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (displayedDecks.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("📚", fontSize = 32.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Chưa có bộ thẻ nào cần ôn",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF1E1B4B)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Hãy thêm ngôn ngữ mới hoặc học thêm các bài để bắt đầu ôn tập.",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            displayedDecks.forEach { deck ->
                DeckListCard(
                    deck = deck,
                    onClickDetail = { onOpenDeckDetail(deck) },
                    onStudy = { onStudyDeck(deck) },
                    onQuiz = { onQuizDeck(deck) },
                    onMatch = { onMatchDeck(deck) }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

/**
 * Tab 3: Tài khoản
 */
@Composable
private fun AccountProfileTab(
    userName: String,
    userVipLevel: Int = 1,
    streakDays: Int,
    masteredCount: Int,
    totalCount: Int,
    studySchedule: com.example.data.model.StudyScheduleEntity? = null,
    onUpdateScheduleTime: (Int, Int) -> Unit = { _, _ -> },
    onOpenEditProfile: () -> Unit,
    onSelectVipLevel: (Int) -> Unit = {},
    onViewStats: () -> Unit,
    onTestSmartNotification: () -> Unit = {},
    onTestMilestoneNotification: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showWidgetGuideDialog by remember { mutableStateOf(false) }
    val reminderHour = studySchedule?.reminderHour ?: 19
    val reminderMinute = studySchedule?.reminderMinute ?: 0
    val minuteFormatted = if (reminderMinute < 10) "0$reminderMinute" else "$reminderMinute"
    val notificationScheduledTime = "$reminderHour:$minuteFormatted hàng ngày"
    val context = androidx.compose.ui.platform.LocalContext.current
    val vipLevelObj = VipLevel.fromLevel(userVipLevel)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Profile Card
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = Color.White,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // VIP Outer Border Avatar Ring
                VipAvatarFrame(
                    vipLevel = vipLevelObj,
                    avatarSize = 56.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF0284C7), Color(0xFF38BDF8))
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userName.take(1).uppercase(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = userName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = if (vipLevelObj == VipLevel.NONE) "Học viên xuất sắc 🌟" else "Học viên ${vipLevelObj.title} ${vipLevelObj.crownEmoji}",
                        fontSize = 13.sp,
                        fontWeight = if (vipLevelObj != VipLevel.NONE) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (vipLevelObj != VipLevel.NONE) vipLevelObj.badgeBgColor else Color(0xFF64748B)
                    )
                }

                TextButton(onClick = onOpenEditProfile) {
                    Text("Sửa", fontWeight = FontWeight.Bold, color = Color(0xFF0284C7))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Progress Overview
        Surface(
            onClick = onViewStats,
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Tổng kết thành tích",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("🔥 Chuỗi ngày", fontSize = 12.sp, color = Color(0xFF64748B))
                        Text("$streakDays ngày", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFFEA580C))
                    }
                    Column {
                        Text("🎯 Đã thuộc", fontSize = 12.sp, color = Color(0xFF64748B))
                        Text("$masteredCount từ", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFF10B981))
                    }
                    Column {
                        Text("📚 Tổng số thẻ", fontSize = 12.sp, color = Color(0xFF64748B))
                        Text("$totalCount thẻ", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFF0284C7))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Smart Notification Settings & Test Card
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFE2E8F0)),
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFFFEF3C7), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🔔", fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Thông báo nhắc học thông minh",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E1B4B)
                            )
                            Text(
                                text = "Tự động kiểm tra số từ cần ôn & streak",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Schedule details badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF8FAFC),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("⏰ Giờ nhắc nhở:", fontSize = 12.sp, color = Color(0xFF64748B))
                            Text(notificationScheduledTime, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFECFDF5)
                        ) {
                            Text(
                                text = "✓ Không spam khi đã học",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF047857),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action buttons for testing notifications
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            onTestSmartNotification()
                            android.widget.Toast.makeText(context, "🔔 Đã kích hoạt đánh giá thông báo thông minh!", android.widget.Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                        modifier = Modifier.weight(1f).height(40.dp)
                    ) {
                        Text("Thử chuông 19:00", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            onTestMilestoneNotification(streakDays)
                            android.widget.Toast.makeText(context, "🎉 Đã gửi thông báo thành tựu Streak!", android.widget.Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                        modifier = Modifier.weight(1f).height(40.dp)
                    ) {
                        Text("Thành tựu Streak", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Home Screen Widget Banner
        Surface(
            onClick = { showWidgetGuideDialog = true },
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFFEEF2FF),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFC7D2FE)),
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(Color(0xFFE0E7FF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📱", fontSize = 22.sp)
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Widget Màn hình chính",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF312E81)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFFEDD5)
                        ) {
                            Text(
                                text = "🔥 $streakDays ngày",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFEA580C),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Hiển thị từ ngẫu nhiên chưa thuộc & Streak góc nhỏ ngoài màn hình điện thoại",
                        fontSize = 12.sp,
                        color = Color(0xFF4338CA)
                    )
                }
            }
        }
    }

    if (showWidgetGuideDialog) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showWidgetGuideDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "📱 Widget Màn Hình Chính",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E1B4B)
                    )
                    Text(
                        text = "Ôn tập từ chưa thuộc & xem Chuỗi ngày góc nhỏ",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Simulated Live Widget Box Preview
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFE0E7FF)),
                        shadowElevation = 4.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("📖 Từ đang học", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4338CA))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFFFF7ED),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFEDD5))
                                ) {
                                    Text("🔥 $streakDays ngày", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFEA580C), modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Resilient", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E1B4B))
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFEEF2FF)) {
                                    Text("adj", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4338CA), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                            Text("/rɪˈzɪl.jənt/", fontSize = 11.sp, color = Color(0xFF64748B))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Kiên cường, phục hồi nhanh", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                            Text("💡 He is resilient in facing challenges.", fontSize = 11.sp, color = Color(0xFF475569))

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🇬🇧 Tiếng Anh • Chưa thuộc", fontSize = 10.sp, color = Color(0xFF64748B))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFEEF2FF),
                                    border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFC7D2FE))
                                ) {
                                    Text("🔄 Từ khác", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF4338CA), modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "💡 Hướng dẫn cài đặt ngoài màn hình:\n1. Nhấn giữ màn hình chính điện thoại\n2. Chọn mục Widget / Tiện ích\n3. Tìm ứng dụng 'NTK FlashCard'\n4. Kéo Widget ra vị trí bạn yêu thích!",
                        fontSize = 12.sp,
                        color = Color(0xFF334155),
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            com.example.widget.VocabularyStreakWidgetProvider.updateAllWidgets(context, streakDays)
                            android.widget.Toast.makeText(context, "🔄 Đã đồng bộ & làm mới dữ liệu Widget!", android.widget.Toast.LENGTH_SHORT).show()
                            showWidgetGuideDialog = false
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Text("Cập nhật Widget ngay 🔄", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
