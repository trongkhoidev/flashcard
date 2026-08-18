package com.example.ui.study

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FlashCardEntity
import com.example.ui.components.Flashcard3DView
import com.example.ui.theme.EasyGreen
import com.example.ui.theme.GoodYellow
import com.example.ui.theme.HardRed
import com.example.ui.theme.NTKBackgroundLight
import com.example.ui.theme.NTKPrimary
import com.example.ui.theme.NTKPrimaryDark
import com.example.ui.theme.NTKTextMuted
import com.example.ui.theme.NTKTextPrimary
import com.example.ui.theme.NTKTextSecondary
import kotlinx.coroutines.delay

@Composable
fun FlashcardStudyScreen(
    deckTitle: String,
    languageTag: String,
    cards: List<FlashCardEntity>,
    onBack: () -> Unit,
    onSpeak: (String, String) -> Unit,
    onToggleStar: (Long, Boolean) -> Unit,
    onRecordReview: (Long, Int) -> Unit,
    onStartQuiz: () -> Unit,
    modifier: Modifier = Modifier
) {
    var cardList by remember(cards) { mutableStateOf(cards) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }
    var isAutoPlay by remember { mutableStateOf(false) }
    var isCompleted by remember { mutableStateOf(false) }

    // Auto play slideshow logic
    LaunchedEffect(isAutoPlay, currentIndex, isFlipped, isCompleted) {
        if (isAutoPlay && !isCompleted && cardList.isNotEmpty()) {
            val currentCard = cardList[currentIndex]
            // Speak front word
            if (!isFlipped) {
                onSpeak(currentCard.frontWord, languageTag)
                delay(2600)
                isFlipped = true
            } else {
                delay(2200)
                if (currentIndex < cardList.size - 1) {
                    currentIndex++
                    isFlipped = false
                } else {
                    isCompleted = true
                    isAutoPlay = false
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFAF9FF), Color(0xFFF0EFFF))
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        if (cardList.isEmpty()) {
            // Empty state
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Chưa có từ vựng nào trong bộ này.", fontSize = 16.sp, color = NTKTextSecondary)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onBack) { Text("Quay lại") }
            }
            return@Box
        }

        val currentCard = cardList.getOrNull(currentIndex) ?: cardList.first()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // TOP BAR
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(42.dp)
                            .background(Color.White, CircleShape)
                            .shadow(2.dp, CircleShape)
                            .testTag("btn_back_study")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = NTKTextPrimary
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = deckTitle,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = NTKTextPrimary,
                            maxLines = 1
                        )
                        Text(
                            text = "Thẻ ${currentIndex + 1} / ${cardList.size}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = NTKPrimary
                        )
                    }

                    Row {
                        // Shuffle button
                        IconButton(
                            onClick = {
                                cardList = cardList.shuffled()
                                currentIndex = 0
                                isFlipped = false
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.White, CircleShape)
                                .shadow(2.dp, CircleShape)
                        ) {
                            Icon(
                                Icons.Default.Shuffle,
                                contentDescription = "Trộn thẻ",
                                tint = NTKPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Auto-play button
                        IconButton(
                            onClick = { isAutoPlay = !isAutoPlay },
                            modifier = Modifier
                                .size(40.dp)
                                .background(if (isAutoPlay) NTKPrimary else Color.White, CircleShape)
                                .shadow(2.dp, CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isAutoPlay) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Tự động học",
                                tint = if (isAutoPlay) Color.White else NTKPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Progress indicator
                LinearProgressIndicator(
                    progress = { (currentIndex + 1).toFloat() / cardList.size.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = NTKPrimary,
                    trackColor = Color(0xFFE2E8F0)
                )
            }

            // MIDDLE 3D CARD
            Flashcard3DView(
                card = currentCard,
                isFlipped = isFlipped,
                onFlip = { isFlipped = !isFlipped },
                onSpeak = { text -> onSpeak(text, languageTag) },
                onToggleStar = { onToggleStar(currentCard.id, currentCard.isStarred) },
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // BOTTOM RATING & NAVIGATION BUTTONS
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Đánh giá mức độ ghi nhớ:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NTKTextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Spaced Repetition Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Hard (🔴)
                    Button(
                        onClick = {
                            onRecordReview(currentCard.id, 3)
                            advanceNext(cardList.size, currentIndex, onNext = { currentIndex = it; isFlipped = false }, onDone = { isCompleted = true })
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_rate_hard"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2))
                    ) {
                        Text("🔴 Khó", color = HardRed, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    // Good (🟡)
                    Button(
                        onClick = {
                            onRecordReview(currentCard.id, 2)
                            advanceNext(cardList.size, currentIndex, onNext = { currentIndex = it; isFlipped = false }, onDone = { isCompleted = true })
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_rate_good"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEF3C7))
                    ) {
                        Text("🟡 Nhớ vừa", color = GoodYellow, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    // Easy / Mastered (🟢)
                    Button(
                        onClick = {
                            onRecordReview(currentCard.id, 1)
                            advanceNext(cardList.size, currentIndex, onNext = { currentIndex = it; isFlipped = false }, onDone = { isCompleted = true })
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_rate_easy"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD1FAE5))
                    ) {
                        Text("🟢 Đã thuộc", color = EasyGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Navigation Controls (Prev / Next)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            if (currentIndex > 0) {
                                currentIndex--
                                isFlipped = false
                            }
                        },
                        enabled = currentIndex > 0
                    ) {
                        Text("← Thẻ trước")
                    }

                    TextButton(
                        onClick = {
                            if (currentIndex < cardList.size - 1) {
                                currentIndex++
                                isFlipped = false
                            } else {
                                isCompleted = true
                            }
                        }
                    ) {
                        Text(if (currentIndex < cardList.size - 1) "Thẻ tiếp →" else "Hoàn thành ✓")
                    }
                }
            }
        }

        // COMPLETION OVERLAY
        AnimatedVisibility(
            visible = isCompleted,
            enter = fadeIn() + scaleIn(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .shadow(16.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(Color(0xFFD1FAE5), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Hoàn thành",
                            tint = EasyGreen,
                            modifier = Modifier.size(42.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Xuất sắc! Đã hoàn thành!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = NTKTextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Bạn vừa hoàn thành ôn luyện ${cardList.size} thẻ ghi nhớ trong bộ \"$deckTitle\".",
                        fontSize = 14.sp,
                        color = NTKTextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onStartQuiz,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NTKPrimary)
                    ) {
                        Text("⚡ Thử thách Trắc nghiệm ngay", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            currentIndex = 0
                            isFlipped = false
                            isCompleted = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Ôn tập lại từ đầu", color = NTKPrimary)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    TextButton(onClick = onBack) {
                        Text("Về trang chủ", color = NTKTextSecondary)
                    }
                }
            }
        }
    }
}

private fun advanceNext(
    total: Int,
    current: Int,
    onNext: (Int) -> Unit,
    onDone: () -> Unit
) {
    if (current < total - 1) {
        onNext(current + 1)
    } else {
        onDone()
    }
}
