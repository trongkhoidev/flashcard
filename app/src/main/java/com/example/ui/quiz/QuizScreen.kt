package com.example.ui.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FlashCardEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.ui.theme.EasyGreen
import com.example.ui.theme.HardRed
import com.example.ui.theme.NTKPrimary
import com.example.ui.theme.NTKPrimaryDark
import com.example.ui.theme.NTKTextPrimary
import com.example.ui.theme.NTKTextSecondary

data class StreakMultiplierInfo(
    val multiplier: Float,
    val title: String,
    val badgeColor: Color,
    val emoji: String
)

fun getStreakMultiplierInfo(streak: Int): StreakMultiplierInfo {
    return when {
        streak <= 1 -> StreakMultiplierInfo(1.0f, "Cơ bản", Color(0xFF0284C7), "⚡")
        streak == 2 -> StreakMultiplierInfo(1.5f, "Chuỗi Thăng Hoa", Color(0xFFEA580C), "🔥")
        streak == 3 -> StreakMultiplierInfo(2.0f, "Chuỗi Bùng Nổ", Color(0xFFDC2626), "🔥🔥")
        streak == 4 -> StreakMultiplierInfo(2.5f, "Chuỗi Xuất Sắc", Color(0xFFD97706), "⚡🔥")
        streak == 5 -> StreakMultiplierInfo(3.0f, "Chuỗi SIÊU CẤP", Color(0xFF7C3AED), "👑🔥")
        streak == 6 -> StreakMultiplierInfo(3.5f, "Chuỗi HUYỀN THOẠI", Color(0xFFEC4899), "💎🔥")
        else -> StreakMultiplierInfo(5.0f, "COMBO THẦN THÁO", Color(0xFF9333EA), "👑🔥✨")
    }
}

@Composable
fun QuizScreen(
    deckTitle: String,
    languageTag: String,
    cards: List<FlashCardEntity>,
    allowBack: Boolean = true,
    isOnboardingTrial: Boolean = false,
    onBack: () -> Unit,
    onSpeak: (String, String) -> Unit,
    onFinishQuiz: (score: Int, total: Int, wrongCards: List<FlashCardEntity>) -> Unit,
    onAnswerCorrect: ((FlashCardEntity) -> Unit)? = null,
    onAnswerWrong: ((FlashCardEntity) -> Unit)? = null,
    onStudyWrongCards: ((List<FlashCardEntity>) -> Unit)? = null,
    onStudyNext: (() -> Unit)? = null,
    onCompleteTrial: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (cards.size < 2) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Cần tối thiểu 2 từ vựng để tạo bài kiểm tra.", color = NTKTextSecondary)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onBack) { Text("Quay lại") }
        }
        return
    }

    val quizCards = remember(cards) { cards.shuffled() }
    var currentIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var totalPoints by remember { mutableIntStateOf(0) }
    val wrongCards = remember { mutableStateListOf<FlashCardEntity>() }
    var currentStreak by remember { mutableIntStateOf(0) }
    var maxStreak by remember { mutableIntStateOf(0) }
    var lastPointsEarned by remember { mutableIntStateOf(0) }
    var lastMultiplier by remember { mutableFloatStateOf(1.0f) }
    var popupTrigger by remember { mutableIntStateOf(0) }

    val popupAlpha = remember { Animatable(0f) }
    val popupOffsetY = remember { Animatable(0f) }
    val popupScale = remember { Animatable(0.7f) }

    LaunchedEffect(popupTrigger) {
        if (popupTrigger > 0) {
            popupAlpha.snapTo(0f)
            popupOffsetY.snapTo(6f)
            popupScale.snapTo(0.7f)

            // Phase 1: Pop in with slight bounce & float up gradually
            launch {
                popupAlpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 280, easing = LinearOutSlowInEasing)
                )
            }
            launch {
                popupScale.animateTo(
                    targetValue = 1.14f,
                    animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
                )
                popupScale.animateTo(
                    targetValue = 1.0f,
                    animationSpec = tween(durationMillis = 180, easing = LinearEasing)
                )
            }
            launch {
                popupOffsetY.animateTo(
                    targetValue = -18f,
                    animationSpec = tween(durationMillis = 800, easing = LinearOutSlowInEasing)
                )
            }

            // Phase 2: Rest & Read
            delay(900)

            // Phase 3: Float up higher & Dissolve (tan đi) mượt mà
            launch {
                popupOffsetY.animateTo(
                    targetValue = -65f,
                    animationSpec = tween(durationMillis = 700, easing = FastOutLinearInEasing)
                )
            }
            launch {
                popupAlpha.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 700, easing = FastOutLinearInEasing)
                )
            }
            launch {
                popupScale.animateTo(
                    targetValue = 0.92f,
                    animationSpec = tween(durationMillis = 700, easing = FastOutLinearInEasing)
                )
            }
        }
    }

    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isAnswerSubmitted by remember { mutableStateOf(false) }
    var isQuizCompleted by remember { mutableStateOf(false) }

    val currentCard = quizCards[currentIndex]

    // Generate 4 multiple choice options (1 correct + 3 distractor meanings)
    val currentOptions = remember(currentIndex) {
        val correct = currentCard.backMeaning
        val wrongOptions = cards
            .filter { it.frontWord != currentCard.frontWord }
            .map { it.backMeaning }
            .shuffled()
            .take(3)
        (wrongOptions + correct).shuffled()
    }

    // Infinite Fire Pulse Animation
    val infiniteTransition = rememberInfiniteTransition(label = "quiz_streak_anim")
    val fireScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fire_scale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFF0F9FF), Color(0xFFE0F2FE))
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar & Progress
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (allowBack) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.White, CircleShape)
                                .shadow(2.dp, CircleShape)
                                .testTag("btn_back_quiz")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                        }
                    } else {
                        Spacer(modifier = Modifier.size(40.dp))
                    }

                    Text(
                        text = "Trắc nghiệm: $deckTitle",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = NTKTextPrimary,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 10.dp)
                    )

                    // Correct Count Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFE0F2FE),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NTKPrimary.copy(alpha = 0.3f)),
                        shadowElevation = 1.dp
                    ) {
                        Text(
                            text = "⭐ $score/${quizCards.size}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NTKPrimary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { (currentIndex + 1).toFloat() / quizCards.size.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = NTKPrimary,
                    trackColor = Color(0xFFE2E8F0)
                )

                // STREAK & POINTS BANNER
                val streakInfo = getStreakMultiplierInfo(currentStreak)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = if (currentStreak > 1) streakInfo.badgeColor.copy(alpha = 0.12f) else Color.White,
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (currentStreak > 1) 1.5.dp else 1.dp,
                        color = if (currentStreak > 1) streakInfo.badgeColor else Color(0xFFE2E8F0)
                    ),
                    shadowElevation = if (currentStreak > 1) 2.dp else 0.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (currentStreak > 0) "🔥" else "⚡",
                                fontSize = 18.sp,
                                modifier = Modifier.graphicsLayer {
                                    if (currentStreak > 1) {
                                        scaleX = fireScale
                                        scaleY = fireScale
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (currentStreak > 0) "Chuỗi: $currentStreak câu" else "Chuỗi: 0 câu",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (currentStreak > 1) streakInfo.badgeColor else NTKTextPrimary
                                )
                                if (currentStreak > 1) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = streakInfo.emoji,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        // Right side: Total Points Badge & Multiplier Pill
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Total Points Badge (Cúp điểm thưởng)
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFFEF3C7),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("🏆", fontSize = 11.sp)
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "${totalPoints}đ",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFFB45309)
                                    )
                                }
                            }

                            // Multiplier Pill
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (currentStreak > 0) streakInfo.badgeColor else Color(0xFF94A3B8)
                            ) {
                                Text(
                                    text = "x${streakInfo.multiplier}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Question Card & Floating Score Pop-Up
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                // Question Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = NTKPrimary.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Chọn nghĩa đúng của từ sau:",
                            fontSize = 13.sp,
                            color = NTKTextSecondary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = currentCard.frontWord,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = NTKPrimaryDark,
                            textAlign = TextAlign.Center
                        )

                        if (currentCard.phonetic.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = currentCard.phonetic,
                                fontSize = 14.sp,
                                color = NTKPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        IconButton(
                            onClick = { onSpeak(currentCard.frontWord, languageTag) },
                            modifier = Modifier
                                .size(38.dp)
                                .background(Color(0xFFE0F2FE), CircleShape)
                        ) {
                            Icon(
                                Icons.Default.VolumeUp,
                                contentDescription = "Phát âm",
                                tint = NTKPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // FLOATING ANIMATED POINTS POPUP OVERLAY (Lên dần dần rồi tan đi mượt mà)
                if (popupAlpha.value > 0.005f) {
                    val popupInfo = getStreakMultiplierInfo(currentStreak)
                    val density = androidx.compose.ui.platform.LocalDensity.current.density
                    Box(
                        modifier = Modifier
                            .offset(y = (-16).dp)
                            .graphicsLayer {
                                alpha = popupAlpha.value
                                translationY = popupOffsetY.value * density
                                scaleX = popupScale.value
                                scaleY = popupScale.value
                            }
                    ) {
                        Surface(
                            shape = RoundedCornerShape(22.dp),
                            color = popupInfo.badgeColor,
                            shadowElevation = 10.dp,
                            border = androidx.compose.foundation.BorderStroke(2.dp, Color.White)
                        ) {
                            Row(
                                modifier = Modifier
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(
                                                popupInfo.badgeColor,
                                                popupInfo.badgeColor.copy(alpha = 0.85f)
                                            )
                                        )
                                    )
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(popupInfo.emoji, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "+${lastPointsEarned} ĐIỂM!",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                if (lastMultiplier > 1.0f) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color.White.copy(alpha = 0.25f)
                                    ) {
                                        Text(
                                            text = "${popupInfo.title} x${lastMultiplier}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("✨", fontSize = 16.sp)
                            }
                        }
                    }
                }
            }

            // Answer Options
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                currentOptions.forEachIndexed { index, option ->
                    val isCorrectOption = option == currentCard.backMeaning
                    val isSelected = selectedOption == option

                    val backgroundColor = when {
                        !isAnswerSubmitted -> if (isSelected) Color(0xFFE0F2FE) else Color.White
                        isCorrectOption -> Color(0xFFD1FAE5) // Green
                        isSelected -> Color(0xFFFEE2E2) // Red
                        else -> Color.White
                    }

                    val borderColor = when {
                        !isAnswerSubmitted -> if (isSelected) NTKPrimary else Color(0xFFE2E8F0)
                        isCorrectOption -> EasyGreen
                        isSelected -> HardRed
                        else -> Color(0xFFE2E8F0)
                    }

                    val optionLabel = when (index) {
                        0 -> "A"
                        1 -> "B"
                        2 -> "C"
                        else -> "D"
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !isAnswerSubmitted) {
                                selectedOption = option
                                isAnswerSubmitted = true
                                if (isCorrectOption) {
                                    score++
                                    currentStreak++
                                    if (currentStreak > maxStreak) {
                                        maxStreak = currentStreak
                                    }
                                    val info = getStreakMultiplierInfo(currentStreak)
                                    lastMultiplier = info.multiplier
                                    val earned = (100 * info.multiplier).toInt()
                                    lastPointsEarned = earned
                                    totalPoints += earned
                                    popupTrigger++
                                    onAnswerCorrect?.invoke(currentCard)
                                } else {
                                    currentStreak = 0
                                    lastPointsEarned = 0
                                    lastMultiplier = 1.0f
                                    popupTrigger = 0
                                    if (!wrongCards.any { it.id == currentCard.id || it.frontWord == currentCard.frontWord }) {
                                        wrongCards.add(currentCard)
                                    }
                                    onAnswerWrong?.invoke(currentCard)
                                }
                            }
                            .testTag("quiz_option_${option.take(6)}"),
                        shape = RoundedCornerShape(16.dp),
                        color = backgroundColor,
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, borderColor),
                        shadowElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Option Letter Badge (A, B, C, D)
                                Surface(
                                    shape = CircleShape,
                                    color = when {
                                        isAnswerSubmitted && isCorrectOption -> EasyGreen
                                        isAnswerSubmitted && isSelected -> HardRed
                                        isSelected -> NTKPrimary
                                        else -> Color(0xFFF1F5F9)
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = optionLabel,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected || (isAnswerSubmitted && (isCorrectOption || isSelected))) Color.White else Color(0xFF64748B)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Text(
                                    text = option,
                                    fontSize = 15.sp,
                                    fontWeight = if (isSelected || (isAnswerSubmitted && isCorrectOption)) FontWeight.Bold else FontWeight.Medium,
                                    color = NTKTextPrimary
                                )
                            }

                            if (isAnswerSubmitted) {
                                if (isCorrectOption) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Đúng",
                                        tint = EasyGreen,
                                        modifier = Modifier.size(24.dp)
                                    )
                                } else if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Sai",
                                        tint = HardRed,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Next Question Button
            Button(
                onClick = {
                    popupTrigger = 0
                    if (currentIndex < quizCards.size - 1) {
                        currentIndex++
                        selectedOption = null
                        isAnswerSubmitted = false
                    } else {
                        isQuizCompleted = true
                        onFinishQuiz(score, quizCards.size, wrongCards.toList())
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("btn_next_quiz"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NTKPrimary),
                enabled = isAnswerSubmitted
            ) {
                Text(
                    text = if (currentIndex < quizCards.size - 1) "Câu hỏi tiếp theo →" else "Xem tổng kết điểm ⭐",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // RESULTS MODAL
        AnimatedVisibility(
            visible = isQuizCompleted,
            enter = fadeIn() + scaleIn(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .shadow(16.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(Color(0xFFFEF3C7), Color(0xFFFDE68A))
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Thành tích",
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(46.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Kết Quả Trắc Nghiệm",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = NTKTextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Total Points Big Callout
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFFEF3C7),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🏆", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "TỔNG ĐIỂM: ${totalPoints}đ",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFB45309)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stats Grid Summary
                    val accuracy = if (quizCards.isNotEmpty()) (score * 100) / quizCards.size else 0
                    val rankTitle = when {
                        accuracy == 100 -> "Thần Thoại Multiplier 👑🔥"
                        accuracy >= 80 -> "Bậc Thầy Từ Vựng 🌟"
                        accuracy >= 50 -> "Học Viên Xuất Sắc ⚡"
                        else -> "Cố Gắng Lần Sau 🎯"
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFF8FAFC),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("⭐ Số câu chính xác:", fontSize = 13.sp, color = NTKTextSecondary)
                                Text("$score / ${quizCards.size} ($accuracy%)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NTKPrimary)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("🔥 Chuỗi đúng dài nhất:", fontSize = 13.sp, color = NTKTextSecondary)
                                Text("$maxStreak câu (x${getStreakMultiplierInfo(maxStreak).multiplier})", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEA580C))
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("⚡ Điểm trung bình/câu:", fontSize = 13.sp, color = NTKTextSecondary)
                                val avgPoints = if (score > 0) totalPoints / score else 0
                                Text("${avgPoints}đ", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0284C7))
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("👑 Danh hiệu đạt được:", fontSize = 13.sp, color = NTKTextSecondary)
                                Text(rankTitle, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF7C3AED))
                            }
                        }
                    }

                    if (isOnboardingTrial) {
                        // In first-time trial mode: ONLY ONE "Tiếp tục" button as requested
                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                onCompleteTrial?.invoke()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = NTKPrimary.copy(alpha = 0.5f))
                                .testTag("btn_trial_quiz_continue"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NTKPrimary)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Tiếp tục",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    } else {
                        // 1. UNMASTERED / WRONG CARDS REVIEW SECTION (TỰ ĐỘNG TẠO BỘ CARD CHƯA THUỘC ĐỂ HỌC LẠI)
                        if (wrongCards.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFFFEF2F2),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFEF4444)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("⚠️", fontSize = 18.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Có ${wrongCards.size} từ chưa thuộc",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFB91C1C)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Đã tự động tạo bộ thẻ \"⚠️ Cần ôn: $deckTitle\" để bạn rèn luyện.",
                                        fontSize = 12.sp,
                                        color = Color(0xFF7F1D1D),
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    // Preview wrong words
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 4.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = wrongCards.take(4).joinToString(", ") { it.frontWord } + if (wrongCards.size > 4) " (+${wrongCards.size - 4})" else "",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFFDC2626),
                                            maxLines = 1,
                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = {
                                            onStudyWrongCards?.invoke(wrongCards.toList())
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(46.dp)
                                            .testTag("btn_study_wrong_cards"),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                                    ) {
                                        Text("🔥 Học lại ${wrongCards.size} từ chưa thuộc ngay", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                    }
                                }
                            }
                        }

                        // 2. PASS >= 50% SUGGESTION CARD (Nếu đã đạt điểm tốt)
                        if (accuracy >= 50 && wrongCards.isEmpty()) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFFF0F9FF),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF0284C7)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("🎉", fontSize = 18.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Bạn có muốn học card tiếp theo không?",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0369A1)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Đạt $accuracy% - Kết quả xuất sắc! Giữ đà học tập để làm chủ toàn bộ từ vựng.",
                                        fontSize = 12.sp,
                                        color = NTKTextSecondary,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = {
                                            onStudyNext?.invoke()
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(46.dp)
                                            .testTag("btn_study_next_card"),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                                    ) {
                                        Text("🎴 Học card tiếp theo ngay", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                currentIndex = 0
                                score = 0
                                totalPoints = 0
                                currentStreak = 0
                                maxStreak = 0
                                selectedOption = null
                                isAnswerSubmitted = false
                                isQuizCompleted = false
                                popupTrigger = 0
                                wrongCards.clear()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = if (accuracy >= 50 && wrongCards.isEmpty()) ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9), contentColor = NTKTextPrimary) else ButtonDefaults.buttonColors(containerColor = NTKPrimary)
                        ) {
                            Text("Làm lại bài kiểm tra", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        TextButton(onClick = onBack) {
                            Text("Trở về danh sách bài học", color = NTKTextSecondary)
                        }
                    }
                }
            }
        }

        // FIREWORKS CELEBRATION OVERLAY ON TOP OF POPUP (AUTO CLEAR AFTER ~10s)
        if (isQuizCompleted) {
            FireworksCanvas(
                durationMs = 8000L,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

private data class FireworkParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val color: Color,
    var alpha: Float,
    val size: Float,
    val maxLife: Float,
    var currentLife: Float = 0f,
    var rotation: Float = 0f,
    val vRot: Float = 0f,
    val shape: Int
)

@Composable
fun FireworksCanvas(
    durationMs: Long = 8000L,
    modifier: Modifier = Modifier,
    onFinished: (() -> Unit)? = null
) {
    val particles = remember { mutableStateListOf<FireworkParticle>() }
    var isRunning by remember { mutableStateOf(true) }

    if (!isRunning) return

    LaunchedEffect(Unit) {
        val colors = listOf(
            Color(0xFFFF2A6D), // Neon Pink
            Color(0xFF05D9E8), // Neon Cyan
            Color(0xFFFFC857), // Gold
            Color(0xFF7C3AED), // Purple
            Color(0xFF10B981), // Emerald
            Color(0xFFFF5722), // Orange
            Color(0xFFEC4899), // Hot Pink
            Color(0xFF0284C7)  // Ocean Blue
        )

        var frame = 0
        val startTime = System.currentTimeMillis()
        val maxParticles = 90 // Cap active particles for optimal performance

        while (true) {
            val elapsed = System.currentTimeMillis() - startTime
            val isSpawningAllowed = elapsed < durationMs

            withFrameNanos { _ ->
                frame++

                // Trigger a new firework burst if time remains
                if (isSpawningAllowed && (frame % 25 == 1 || particles.size < 15) && particles.size < maxParticles) {
                    val cx = 100f + (Math.random().toFloat() * 800f)
                    val cy = 150f + (Math.random().toFloat() * 700f)
                    val burstCount = (25..35).random()
                    val burstColor = colors.random()

                    for (i in 0 until burstCount) {
                        if (particles.size >= maxParticles) break
                        val angle = Math.random() * 2 * Math.PI
                        val speed = 3f + (Math.random().toFloat() * 11f)
                        val vx = (Math.cos(angle) * speed).toFloat()
                        val vy = (Math.sin(angle) * speed).toFloat()
                        val maxLife = 35f + (Math.random().toFloat() * 30f)
                        val pColor = if (Math.random() > 0.3) burstColor else colors.random()

                        particles.add(
                            FireworkParticle(
                                x = cx,
                                y = cy,
                                vx = vx,
                                vy = vy,
                                color = pColor,
                                alpha = 1f,
                                size = 5f + (Math.random().toFloat() * 8f),
                                maxLife = maxLife,
                                currentLife = 0f,
                                rotation = (Math.random() * 360).toFloat(),
                                vRot = -10f + (Math.random().toFloat() * 20f),
                                shape = (0..2).random()
                            )
                        )
                    }
                }

                // Update particle physics
                val iterator = particles.iterator()
                while (iterator.hasNext()) {
                    val p = iterator.next()
                    p.currentLife += 1f
                    if (p.currentLife >= p.maxLife) {
                        iterator.remove()
                    } else {
                        p.x += p.vx
                        p.y += p.vy
                        p.vy += 0.25f // Gravity
                        p.vx *= 0.96f // Drag
                        p.vy *= 0.96f
                        p.rotation += p.vRot
                        p.alpha = (1f - (p.currentLife / p.maxLife)).coerceIn(0f, 1f)
                    }
                }
            }

            // Stop condition: duration passed & remaining particles finished fading out
            if (!isSpawningAllowed && particles.isEmpty()) {
                isRunning = false
                onFinished?.invoke()
                break
            }
        }
    }

    if (particles.isNotEmpty()) {
        Canvas(
            modifier = modifier.fillMaxSize()
        ) {
            val scaleX = size.width / 1000f
            val scaleY = size.height / 1800f

            particles.forEach { p ->
                val pAlpha = p.alpha.coerceIn(0f, 1f)
                if (pAlpha > 0.01f) {
                    drawContext.canvas.save()
                    drawContext.canvas.translate(p.x * scaleX, p.y * scaleY)
                    drawContext.canvas.rotate(p.rotation)

                    when (p.shape) {
                        0 -> {
                            drawCircle(
                                color = p.color.copy(alpha = pAlpha),
                                radius = (p.size / 2f) * scaleX
                            )
                        }
                        1 -> {
                            val sz = p.size * scaleX
                            drawRect(
                                color = p.color.copy(alpha = pAlpha),
                                topLeft = Offset(-sz / 2f, -sz / 2f),
                                size = Size(sz, sz * 1.4f)
                            )
                        }
                        else -> {
                            val r = p.size * scaleX
                            val path = Path().apply {
                                moveTo(0f, -r)
                                lineTo(r * 0.4f, -r * 0.4f)
                                lineTo(r, 0f)
                                lineTo(r * 0.4f, r * 0.4f)
                                lineTo(0f, r)
                                lineTo(-r * 0.4f, r * 0.4f)
                                lineTo(-r, 0f)
                                lineTo(-r * 0.4f, -r * 0.4f)
                                close()
                            }
                            drawPath(
                                path = path,
                                color = p.color.copy(alpha = pAlpha)
                            )
                        }
                    }
                    drawContext.canvas.restore()
                }
            }
        }
    }
}

