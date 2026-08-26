package com.example.ui.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AppLanguage
import com.example.data.model.DeckEntity
import com.example.data.model.DeckWithStats
import com.example.data.model.FlashCardEntity
import java.util.UUID



/**
 * Dialog for importing/batch adding cards
 */
@Composable
fun ImportCardsDialog(
    decks: List<DeckEntity>,
    onDismiss: () -> Unit,
    onImportCards: (deckId: String, cards: List<FlashCardEntity>) -> Unit
) {
    var rawText by remember {
        mutableStateOf(
            "Bonjour | Xin chào | Bonjour, comment allez-vous?\n" +
            "Merci | Cảm ơn | Merci beaucoup!\n" +
            "Au revoir | Tạm biệt | Au revoir et à bientôt."
        )
    }
    var selectedDeckId by remember { mutableStateOf(decks.firstOrNull()?.id ?: "") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Nhập từ vựng nhanh",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E1B4B)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Định dạng mỗi dòng: Từ vựng | Nghĩa | Câu ví dụ",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = rawText,
                    onValueChange = { rawText = it },
                    label = { Text("Danh sách từ vựng") },
                    maxLines = 8,
                    minLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF6366F1),
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val parsedCards = rawText.lines().mapNotNull { line ->
                            val parts = line.split("|").map { it.trim() }
                            if (parts.isNotEmpty() && parts[0].isNotBlank()) {
                                FlashCardEntity(
                                    deckId = selectedDeckId.ifBlank { decks.firstOrNull()?.id ?: "custom" },
                                    languageCode = "fr",
                                    frontWord = parts[0],
                                    phonetic = "",
                                    partOfSpeech = "phrase",
                                    frontExample = parts.getOrNull(2) ?: "",
                                    backMeaning = parts.getOrNull(1) ?: "",
                                    backExampleTranslation = ""
                                )
                            } else null
                        }
                        if (parsedCards.isNotEmpty()) {
                            onImportCards(selectedDeckId, parsedCards)
                            onDismiss()
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Nhập ${rawText.lines().filter { it.isNotBlank() }.size} thẻ vào bộ", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

/**
 * Dialog for Viewing Detailed Stats with 2 Tabs (Tổng quan, Theo ngôn ngữ)
 */
@Composable
fun StatsSummaryDialog(
    streakDays: Int,
    masteredCount: Int,
    totalCardsCount: Int,
    totalPoints: Int = 0,
    vipLevel: Int = 1,
    userName: String = "bạn",
    learningLanguages: List<AppLanguage> = emptyList(),
    allCardsList: List<FlashCardEntity> = emptyList(),
    decksWithStats: List<DeckWithStats> = emptyList(),
    onStartReview: () -> Unit = {},
    onDismiss: () -> Unit
) {
    var selectedStatsTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("📊 Tổng quan", "🌐 Theo ngôn ngữ")

    val filteredCards = remember(allCardsList, learningLanguages) {
        if (learningLanguages.isNotEmpty() && allCardsList.isNotEmpty()) {
            val langCodes = learningLanguages.map { it.code }.toSet()
            allCardsList.filter { card -> card.languageCode in langCodes }
        } else {
            allCardsList
        }
    }
    val effectiveTotalCards = remember(filteredCards, totalCardsCount) {
        if (filteredCards.isNotEmpty()) filteredCards.size else totalCardsCount
    }
    val effectiveMasteredCount = remember(filteredCards, masteredCount) {
        if (filteredCards.isNotEmpty()) filteredCards.count { it.isMastered } else masteredCount
    }

    val retentionRate = if (effectiveTotalCards > 0) (effectiveMasteredCount * 100 / effectiveTotalCards).coerceIn(0, 100) else 0
    val learningCount = (effectiveTotalCards - effectiveMasteredCount).coerceAtLeast(0)
    val needsReviewCount = remember(filteredCards, effectiveTotalCards) {
        val hardFromCards = filteredCards.count { it.difficulty >= 3 || (!it.isMastered && it.reviewCount > 0) }
        if (hardFromCards > 0) hardFromCards else (effectiveTotalCards / 4).coerceAtLeast(0)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 10.dp,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    Brush.linearGradient(listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Outlined.BarChart,
                                contentDescription = "Thống kê",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Thống kê học tập",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E1B4B)
                            )
                            Text(
                                text = "Học viên $userName • VIP Cấp $vipLevel",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFFF1F5F9), CircleShape)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Đóng", tint = Color(0xFF64748B), modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Navigation Tab Row
                TabRow(
                    selectedTabIndex = selectedStatsTab,
                    containerColor = Color(0xFFF8FAFC),
                    contentColor = Color(0xFF4F46E5),
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedStatsTab]),
                            color = Color(0xFF4F46E5),
                            height = 3.dp
                        )
                    },
                    divider = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedStatsTab == index,
                            onClick = { selectedStatsTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 13.sp,
                                    fontWeight = if (selectedStatsTab == index) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedStatsTab == index) Color(0xFF4F46E5) else Color(0xFF64748B)
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tab Content
                when (selectedStatsTab) {
                    0 -> {
                        // TAB 1: TỔNG QUAN
                        // 1. Core KPIs (4 cards grid)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatCard(
                                title = "Chuỗi ngày",
                                value = "$streakDays ngày",
                                subtitle = if (streakDays > 0) "🔥 Đang duy trì" else "Bắt đầu ngay",
                                icon = Icons.Outlined.LocalFireDepartment,
                                color = Color(0xFFEA580C),
                                bgColor = Color(0xFFFFF7ED),
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "Đã thuộc",
                                value = "$effectiveMasteredCount từ",
                                subtitle = "✅ Đã ghi nhớ vững",
                                icon = Icons.Outlined.CheckCircle,
                                color = Color(0xFF10B981),
                                bgColor = Color(0xFFECFDF5),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatCard(
                                title = "Tổng từ vựng",
                                value = "$effectiveTotalCards thẻ",
                                subtitle = "📚 Kho từ đang học",
                                icon = Icons.Outlined.Style,
                                color = Color(0xFF6366F1),
                                bgColor = Color(0xFFEEF2FF),
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "Tỷ lệ nhớ",
                                value = "$retentionRate%",
                                subtitle = if (retentionRate >= 80) "🌟 Xuất sắc" else "Đang tiến bộ",
                                icon = Icons.Outlined.School,
                                color = Color(0xFF8B5CF6),
                                bgColor = Color(0xFFF5F3FF),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // 2. Mastery Distribution Section
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFF8FAFC),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Phân bổ mức độ nhớ",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E293B)
                                    )
                                    Text(
                                        text = "$effectiveTotalCards từ",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF64748B)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Segmented Progress Bar
                                val masteredRatio = if (effectiveTotalCards > 0) effectiveMasteredCount.toFloat() / effectiveTotalCards else 0f
                                val needsReviewRatio = if (effectiveTotalCards > 0) needsReviewCount.toFloat() / effectiveTotalCards else 0f
                                val learningRatio = (1f - masteredRatio - needsReviewRatio).coerceAtLeast(0f)

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(10.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(Color(0xFFE2E8F0))
                                ) {
                                    if (masteredRatio > 0f) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .weight(masteredRatio.coerceAtLeast(0.01f))
                                                .background(Color(0xFF10B981))
                                        )
                                    }
                                    if (learningRatio > 0f) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .weight(learningRatio.coerceAtLeast(0.01f))
                                                .background(Color(0xFFF59E0B))
                                        )
                                    }
                                    if (needsReviewRatio > 0f) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .weight(needsReviewRatio.coerceAtLeast(0.01f))
                                                .background(Color(0xFFEF4444))
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    LegendPill(label = "Đã thuộc: $effectiveMasteredCount", color = Color(0xFF10B981))
                                    LegendPill(label = "Đang rèn: $learningCount", color = Color(0xFFF59E0B))
                                    LegendPill(label = "Cần ôn: $needsReviewCount", color = Color(0xFFEF4444))
                                }
                            }
                        }
                    }

                    1 -> {
                        // TAB 2: THEO NGÔN NGỮ
                        val displayLangs = if (learningLanguages.isNotEmpty()) {
                            learningLanguages
                        } else {
                            listOf(AppLanguage.ENGLISH)
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            displayLangs.forEach { lang ->
                                val langDecks = decksWithStats.filter { it.deck.languageCode.equals(lang.code, ignoreCase = true) }
                                val langTotalCards = if (langDecks.isNotEmpty()) langDecks.sumOf { it.totalCards } else (totalCardsCount / displayLangs.size.coerceAtLeast(1)).coerceAtLeast(0)
                                val langMasteredCards = if (langDecks.isNotEmpty()) langDecks.sumOf { it.masteredCards } else (masteredCount / displayLangs.size.coerceAtLeast(1)).coerceAtLeast(0)
                                val langPercent = if (langTotalCards > 0) (langMasteredCards * 100 / langTotalCards).coerceIn(0, 100) else 0

                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color(0xFFF8FAFC),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(lang.flagEmoji, fontSize = 22.sp)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text(
                                                        text = lang.displayName,
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF1E293B)
                                                    )
                                                    Text(
                                                        text = "${langDecks.size} bộ thẻ • $langTotalCards từ vựng",
                                                        fontSize = 11.sp,
                                                        color = Color(0xFF64748B)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = "$langPercent%",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color(0xFF4F46E5)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        LinearProgressIndicator(
                                            progress = { langPercent / 100f },
                                            color = Color(0xFF10B981),
                                            trackColor = Color(0xFFE2E8F0),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(6.dp)
                                                .clip(RoundedCornerShape(3.dp))
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Đã thuộc: $langMasteredCards từ",
                                                fontSize = 11.sp,
                                                color = Color(0xFF10B981),
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                text = "Còn lại: ${(langTotalCards - langMasteredCards).coerceAtLeast(0)} từ",
                                                fontSize = 11.sp,
                                                color = Color(0xFF64748B)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Action Button
                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Text("Đóng", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun LegendPill(
    label: String,
    color: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color(0xFF475569),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String = "",
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = bgColor,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 11.sp, color = Color(0xFF64748B), fontWeight = FontWeight.SemiBold)
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color(0xFF1E293B))
            if (subtitle.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(subtitle, fontSize = 10.sp, color = color, fontWeight = FontWeight.Medium)
            }
        }
    }
}

/**
 * Dialog for Saved / Starred Vocabulary with Multi-language filter, Topic identification, and Continue Learning Mode
 */
@Composable
fun SavedCardsDialog(
    starredCards: List<FlashCardEntity>,
    decks: List<DeckEntity> = emptyList(),
    onSpeak: (String, String) -> Unit,
    onToggleStar: (Long, Boolean) -> Unit = { _, _ -> },
    onStartStudy: (List<FlashCardEntity>, String, String) -> Unit = { _, _, _ -> },
    onStartQuiz: (List<FlashCardEntity>, String, String) -> Unit = { _, _, _ -> },
    onStartMatch: (List<FlashCardEntity>, String, String) -> Unit = { _, _, _ -> },
    onDismiss: () -> Unit
) {
    var selectedLangCode by remember { mutableStateOf<String?>(null) }
    var selectedTopicId by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    // Distinct language codes available in starred cards
    val availableLanguages = remember(starredCards) {
        val codesInCards = starredCards.map { it.languageCode }.distinct()
        val allStandard = AppLanguage.entries.map { it.code }
        val mergedCodes = (codesInCards + allStandard).distinct()
        mergedCodes.map { code ->
            val lang = AppLanguage.fromCode(code)
            val count = starredCards.count { it.languageCode.equals(code, ignoreCase = true) }
            Triple(lang, code, count)
        }.sortedByDescending { it.third }
    }

    // Filter by Language, Topic & Search Query
    val filteredByLang = remember(starredCards, selectedLangCode) {
        if (selectedLangCode == null) starredCards
        else starredCards.filter { it.languageCode.equals(selectedLangCode, ignoreCase = true) }
    }

    val availableTopicsInLang = remember(filteredByLang, decks) {
        val deckIds = filteredByLang.map { it.deckId }.distinct()
        deckIds.mapNotNull { dId ->
            val deck = decks.find { it.id == dId }
            val count = filteredByLang.count { it.deckId == dId }
            if (deck != null) Pair(deck, count) else null
        }
    }

    val filteredCards = remember(filteredByLang, selectedTopicId, searchQuery) {
        filteredByLang.filter { card ->
            val matchesTopic = selectedTopicId == null || card.deckId == selectedTopicId
            val matchesSearch = searchQuery.isBlank() ||
                card.frontWord.contains(searchQuery, ignoreCase = true) ||
                card.backMeaning.contains(searchQuery, ignoreCase = true) ||
                card.phonetic.contains(searchQuery, ignoreCase = true) ||
                card.frontExample.contains(searchQuery, ignoreCase = true)
            matchesTopic && matchesSearch
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            shadowElevation = 16.dp,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp, vertical = 16.dp)
            ) {
                // 1. TOP HEADER BAR
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFFE0F2FE), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = null,
                                tint = Color(0xFF0284C7),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Từ vựng đã lưu",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "Tổng số ${starredCards.size} từ vựng đã lưu",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFF1F5F9), CircleShape)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Đóng",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 2. MULTI-LANGUAGE FILTER TABS / CHIPS
                Text(
                    text = "Phân loại theo ngôn ngữ:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF475569)
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // "Tất cả" chip
                    val isAllSelected = selectedLangCode == null
                    Surface(
                        onClick = {
                            selectedLangCode = null
                            selectedTopicId = null
                        },
                        shape = RoundedCornerShape(20.dp),
                        color = if (isAllSelected) Color(0xFF0284C7) else Color(0xFFF1F5F9),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isAllSelected) Color(0xFF0284C7) else Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier.testTag("filter_lang_all")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🌐 Tất cả (${starredCards.size})",
                                fontSize = 13.sp,
                                fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isAllSelected) Color.White else Color(0xFF334155)
                            )
                        }
                    }

                    // Specific language chips
                    availableLanguages.forEach { (lang, code, count) ->
                        val isSelected = selectedLangCode.equals(code, ignoreCase = true)
                        Surface(
                            onClick = {
                                selectedLangCode = code
                                selectedTopicId = null
                            },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) Color(0xFF0284C7) else if (count > 0) Color(0xFFE0F2FE).copy(alpha = 0.5f) else Color(0xFFF8FAFC),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) Color(0xFF0284C7) else if (count > 0) Color(0xFFBAE6FD) else Color(0xFFE2E8F0)
                            ),
                            modifier = Modifier.testTag("filter_lang_$code")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(lang.flagEmoji, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${lang.displayName} ($count)",
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else if (count > 0) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else if (count > 0) Color(0xFF0369A1) else Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 3. TOPIC CHIPS & SEARCH ROW (Chủ đề gì)
                if (availableTopicsInLang.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            onClick = { selectedTopicId = null },
                            shape = RoundedCornerShape(14.dp),
                            color = if (selectedTopicId == null) Color(0xFF0284C7).copy(alpha = 0.12f) else Color(0xFFF8FAFC),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (selectedTopicId == null) Color(0xFF0284C7) else Color(0xFFE2E8F0)
                            )
                        ) {
                            Text(
                                text = "Tất cả chủ đề",
                                fontSize = 11.sp,
                                fontWeight = if (selectedTopicId == null) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTopicId == null) Color(0xFF0284C7) else Color(0xFF64748B),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }

                        availableTopicsInLang.forEach { (deck, count) ->
                            val isTopicSelected = selectedTopicId == deck.id
                            Surface(
                                onClick = {
                                    selectedTopicId = if (isTopicSelected) null else deck.id
                                },
                                shape = RoundedCornerShape(14.dp),
                                color = if (isTopicSelected) Color(0xFF0284C7).copy(alpha = 0.15f) else Color(0xFFF8FAFC),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isTopicSelected) Color(0xFF0284C7) else Color(0xFFE2E8F0)
                                )
                            ) {
                                Text(
                                    text = "${deck.iconEmoji} ${deck.title} ($count)",
                                    fontSize = 11.sp,
                                    fontWeight = if (isTopicSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isTopicSelected) Color(0xFF0284C7) else Color(0xFF475569),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // 4. SEARCH BAR
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFF8FAFC),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color(0xFF0284C7),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "Tìm kiếm từ vựng...",
                                    fontSize = 13.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                            androidx.compose.foundation.text.BasicTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                singleLine = true,
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontSize = 14.sp,
                                    color = Color(0xFF0F172A),
                                    fontWeight = FontWeight.Medium
                                ),
                                cursorBrush = androidx.compose.ui.graphics.SolidColor(Color(0xFF0284C7)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("saved_cards_search_input")
                            )
                        }
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { searchQuery = "" },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear search",
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 6. CONTENT AREA
                if (filteredCards.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text("⭐", fontSize = 42.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (starredCards.isEmpty()) "Chưa có từ vựng nào được đánh dấu sao"
                                       else "Không tìm thấy từ vựng phù hợp với bộ lọc",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF334155),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Bấm biểu tượng ngôi sao trên bất kỳ thẻ từ nào để lưu và xem lại tại đây!",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    // LIST VIEW OF SAVED WORDS
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredCards, key = { it.id }) { card ->
                            val matchingDeck = decks.find { it.id == card.deckId }
                            val cardLang = AppLanguage.fromCode(card.languageCode)

                            SavedWordDetailCard(
                                card = card,
                                deck = matchingDeck,
                                language = cardLang,
                                onSpeak = { onSpeak(card.frontWord, card.languageCode) },
                                onToggleStar = { onToggleStar(card.id, card.isStarred) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 6. FOOTER BUTTON
                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    Text("Đóng", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

/**
 * Individual Card in Saved Words List with Topic & Language metadata
 */
@Composable
private fun SavedWordDetailCard(
    card: FlashCardEntity,
    deck: DeckEntity?,
    language: AppLanguage,
    onSpeak: () -> Unit,
    onToggleStar: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFFF8FAFC),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("saved_word_card_${card.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header Row: Topic Badge & Language Flag + Action Icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Topic & Language Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Language Chip
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFE0F2FE),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBAE6FD))
                    ) {
                        Text(
                            text = "${language.flagEmoji} ${language.code.uppercase()}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0369A1),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // Topic Chip (Chủ đề gì)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF1F5F9),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${deck?.iconEmoji ?: "🏷️"} ${deck?.title ?: "Chủ đề từ vựng"}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF334155),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (deck?.level?.isNotBlank() == true) {
                                Text(
                                    text = " • ${deck.level}",
                                    fontSize = 10.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }

                // Actions: TTS Speak + Bookmark
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onSpeak,
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFFE0F2FE), CircleShape)
                    ) {
                        Icon(
                            Icons.Filled.VolumeUp,
                            contentDescription = "Phát âm",
                            tint = Color(0xFF0284C7),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = onToggleStar,
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFFFEF3C7), CircleShape)
                    ) {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = "Bỏ lưu",
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Word & Phonetic & Part of speech
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = card.frontWord,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                if (card.phonetic.isNotBlank()) {
                    Text(
                        text = card.phonetic,
                        fontSize = 13.sp,
                        color = Color(0xFF0284C7),
                        fontWeight = FontWeight.Medium
                    )
                }

                if (card.partOfSpeech.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF1F5F9)
                    ) {
                        Text(
                            text = card.partOfSpeech,
                            fontSize = 10.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Vietnamese Meaning
            Text(
                text = card.backMeaning,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E293B)
            )

            // Example Sentence (if present)
            if (card.frontExample.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF0F9FF).copy(alpha = 0.7f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBAE6FD).copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "“${card.frontExample}”",
                            fontSize = 12.sp,
                            color = Color(0xFF334155),
                            fontStyle = FontStyle.Italic
                        )
                        if (card.backExampleTranslation.isNotBlank()) {
                            Text(
                                text = card.backExampleTranslation,
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }
            }

            // Mastery status tag
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val isMastered = card.isMastered || card.difficulty == 1
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isMastered) Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
                ) {
                    Text(
                        text = if (isMastered) "✓ Đã thuộc" else "⏳ Cần ôn tập",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isMastered) Color(0xFF15803D) else Color(0xFFB45309),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                if (card.reviewCount > 0) {
                    Text(
                        text = "Đã ôn ${card.reviewCount} lần",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}
