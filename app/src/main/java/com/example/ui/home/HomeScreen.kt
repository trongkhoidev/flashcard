package com.example.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    selectedLanguage: AppLanguage,
    onSelectLanguage: (AppLanguage) -> Unit,
    decks: List<DeckEntity>,
    allDecksList: List<DeckEntity> = emptyList(),
    starredCards: List<FlashCardEntity> = emptyList(),
    streakDays: Int = 7,
    masteredWordsCount: Int = 15,
    totalWordsCount: Int = 20,
    userName: String = "bạn",
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
    onCreateDeckDirect: (String, String, String, String) -> Unit = { _, _, _, _ -> },
    onImportCardsDirect: (String, List<FlashCardEntity>) -> Unit = { _, _ -> },
    onStudyByLang: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedBottomTab by remember { mutableIntStateOf(0) }

    // Dialog & Sheet States
    var showStatsDialog by remember { mutableStateOf(false) }
    var showSavedCardsDialog by remember { mutableStateOf(false) }
    var showCreateDeckDialog by remember { mutableStateOf(false) }
    var showImportCardsDialog by remember { mutableStateOf(false) }
    var showLanguageFilterSheet by remember { mutableStateOf(false) }
    var showAllDecksSheet by remember { mutableStateOf(false) }

    val filterSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val allDecksSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val effectiveDecks = if (allDecksList.isNotEmpty()) allDecksList else decks

    // Filtered decks for search
    val filteredDecks = if (searchQuery.isBlank()) {
        decks
    } else {
        effectiveDecks.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.subtitle.contains(searchQuery, ignoreCase = true) ||
            it.level.contains(searchQuery, ignoreCase = true)
        }
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
            // Main Content Area based on Selected Bottom Tab
            when (selectedBottomTab) {
                0 -> {
                    // TAB 0: TRANG CHỦ (Home Dashboard strictly following the provided mockup)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = 12.dp)
                    ) {
                        Spacer(modifier = Modifier.height(6.dp))

                        // 1. TOP HEADER: "Xin chào, 👋", "Hôm nay học gì nào?", Streak Pill "🔥 7"
                        HomeTopHeader(
                            userName = userName,
                            streakDays = streakDays,
                            onStreakClick = { showStatsDialog = true }
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // 2. SEARCH BAR: "Tìm kiếm bộ thẻ, chủ đề..." with Tune/Filter icon
                        HomeSearchBar(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it },
                            onFilterClick = { showLanguageFilterSheet = true }
                        )

                        // If user is searching, show immediate search results
                        if (searchQuery.isNotBlank()) {
                            SearchResultsView(
                                query = searchQuery,
                                results = filteredDecks,
                                onOpenDeckDetail = onOpenDeckDetail,
                                onStudyDeck = onStudyDeck,
                                onQuizDeck = onQuizDeck,
                                onMatchDeck = onMatchDeck
                            )
                        } else {
                            Spacer(modifier = Modifier.height(8.dp))

                            // 3. STREAK MASCOT BANNER: Purple gradient card with 7-day tracker and 3D Owl
                            StreakMascotBanner(
                                streakDays = streakDays,
                                onBannerClick = { showStatsDialog = true }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // 4. QUICK ACTION GRID: "Tạo bộ thẻ", "Nhập thẻ", "Thống kê", "Đã lưu"
                            QuickActionGrid(
                                onCreateDeck = { showCreateDeckDialog = true },
                                onImportCards = { showImportCardsDialog = true },
                                onViewStats = { showStatsDialog = true },
                                onViewSaved = { showSavedCardsDialog = true }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // 5. "Tiếp tục học" SECTION: Dynamic active language/deck
                            val currentActiveDeck = decks.firstOrNull()
                                ?: effectiveDecks.firstOrNull { it.languageCode == selectedLanguage.code }
                                ?: effectiveDecks.firstOrNull()
                            val studiedCount = ((currentActiveDeck?.cardCount ?: 50) * 0.64f).toInt().coerceAtLeast(1)
                            val totalCount = currentActiveDeck?.cardCount?.takeIf { it > 0 } ?: 50

                            ContinueLearningSection(
                                title = currentActiveDeck?.title ?: "${selectedLanguage.displayName} cơ bản",
                                studiedCount = studiedCount,
                                totalCount = totalCount,
                                onContinueClick = {
                                    if (currentActiveDeck != null) {
                                        onOpenDeckDetail(currentActiveDeck)
                                    } else {
                                        onStudyByLang(selectedLanguage.code)
                                    }
                                },
                                onViewAllClick = { showAllDecksSheet = true }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // 6. "Bộ thẻ của bạn" SECTION: Japanese N5, English Basics, Korean, Vietnamese
                            YourDecksSection(
                                onDeckClick = { langCode ->
                                    val targetDeck = effectiveDecks.firstOrNull { it.languageCode == langCode }
                                    if (targetDeck != null) {
                                        onOpenDeckDetail(targetDeck)
                                    } else {
                                        onStudyByLang(langCode)
                                    }
                                },
                                onViewAllClick = { showAllDecksSheet = true }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // 7. "Mục tiêu hôm nay" CARD: 75% Circular Ring, 15 / 20 thẻ, Cheer text
                            DailyGoalCard(
                                currentCount = if (masteredWordsCount > 0) masteredWordsCount else 15,
                                targetCount = if (totalWordsCount > 0) totalWordsCount else 20,
                                percentage = if (totalWordsCount > 0) (masteredWordsCount * 100 / totalWordsCount).coerceIn(10, 100) else 75,
                                onClick = { showStatsDialog = true }
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                        }
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
                    // TAB 2: ÔN TẬP (Spaced Repetition & Daily Quizzes)
                    ReviewHistoryTab(
                        decks = effectiveDecks,
                        onOpenDeckDetail = onOpenDeckDetail,
                        onStudyDeck = onStudyDeck,
                        onQuizDeck = onQuizDeck,
                        onMatchDeck = onMatchDeck,
                        onViewSaved = { showSavedCardsDialog = true },
                        modifier = Modifier.weight(1f)
                    )
                }

                3 -> {
                    // TAB 3: TÀI KHOẢN (Profile, Achievements, Settings)
                    AccountProfileTab(
                        userName = userName,
                        streakDays = streakDays,
                        masteredCount = masteredWordsCount,
                        totalCount = totalWordsCount,
                        onOpenEditProfile = onOpenProfile,
                        onViewStats = { showStatsDialog = true },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 8. FIXED BOTTOM NAVIGATION BAR: Trang chủ, Khám phá, Ôn tập, Tài khoản
            HomeBottomNavBar(
                selectedTab = selectedBottomTab,
                onTabSelected = { tabIndex ->
                    selectedBottomTab = tabIndex
                }
            )
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
            onDismiss = { showCreateDeckDialog = false },
            onConfirm = { title, subtitle, langCode, level ->
                onCreateDeckDirect(title, subtitle, langCode, level)
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
        val distinctLangCodes = remember(effectiveDecks) {
            effectiveDecks.map { it.languageCode }.distinct()
        }
        val displayedDecks = remember(sheetLangFilter, effectiveDecks) {
            if (sheetLangFilter == null) effectiveDecks else effectiveDecks.filter { it.languageCode == sheetLangFilter }
        }
        val decksByLanguage = remember(displayedDecks) {
            displayedDecks.groupBy { it.languageCode }
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
                                "Đang học ${distinctLangCodes.size} ngôn ngữ (${effectiveDecks.size} bộ thẻ)"
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
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                                text = "🌐 Tất cả (${effectiveDecks.size})",
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
                            val count = effectiveDecks.count { it.languageCode == langCode }

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
                            DeckListCard(
                                deck = deck,
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
    modifier: Modifier = Modifier
) {
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
                        text = "${deck.cardCount} thẻ • ${deck.level}",
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
 * Tab 1: Khám phá
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
            text = "Chọn ngôn ngữ để học các bộ thẻ chuyên sâu",
            fontSize = 13.sp,
            color = Color(0xFF64748B)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Horizontal Language selector chips
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

        Spacer(modifier = Modifier.height(16.dp))

        decks.forEach { deck ->
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

/**
 * Tab 2: Ôn tập
 */
@Composable
private fun ReviewHistoryTab(
    decks: List<DeckEntity>,
    onOpenDeckDetail: (DeckEntity) -> Unit = {},
    onStudyDeck: (DeckEntity) -> Unit,
    onQuizDeck: (DeckEntity) -> Unit,
    onMatchDeck: (DeckEntity) -> Unit,
    onViewSaved: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Luyện tập & Ôn tập",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1B4B)
        )
        Text(
            text = "Ôn lại các từ cần củng cố theo thuật toán lặp lại ngắt quãng",
            fontSize = 13.sp,
            color = Color(0xFF64748B)
        )

        Spacer(modifier = Modifier.height(16.dp))

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

        decks.take(3).forEach { deck ->
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

/**
 * Tab 3: Tài khoản
 */
@Composable
private fun AccountProfileTab(
    userName: String,
    streakDays: Int,
    masteredCount: Int,
    totalCount: Int,
    onOpenEditProfile: () -> Unit,
    onViewStats: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                Box(
                    modifier = Modifier
                        .size(56.dp)
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
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = userName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Học viên xuất sắc 🌟",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
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
    }
}
