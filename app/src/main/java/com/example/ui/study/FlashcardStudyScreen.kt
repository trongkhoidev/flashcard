package com.example.ui.study

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
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
    userVipLevel: Int = 0,
    allowBack: Boolean = true,
    isOnboardingTrial: Boolean = false,
    onBack: () -> Unit,
    onSpeak: (String, String) -> Unit,
    onToggleStar: (Long, Boolean) -> Unit,
    onRecordReview: ((Long, Int) -> Unit)? = null,
    onStartQuiz: () -> Unit,
    onSessionFinished: ((cardsCount: Int, masteredCount: Int) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var cardList by remember(cards) { mutableStateOf(cards) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }
    var isAutoPlay by remember { mutableStateOf(false) }
    var isCompleted by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(isCompleted) {
        if (isCompleted) {
            val masteredCount = cardList.count { it.isMastered }
            onSessionFinished?.invoke(cardList.size, masteredCount)
        }
    }

    // Auto-dismiss transient toast
    LaunchedEffect(toastMessage) {
        if (toastMessage != null) {
            delay(1500)
            toastMessage = null
        }
    }

    // Auto-pronounce word when card appears initially or on navigation (when not in auto-play mode)
    LaunchedEffect(currentIndex, cardList) {
        if (!isAutoPlay && !isCompleted && cardList.isNotEmpty()) {
            val currentCard = cardList.getOrNull(currentIndex)
            if (currentCard != null) {
                delay(300)
                val cardLang = currentCard.languageCode.ifEmpty { languageTag }
                onSpeak(currentCard.frontWord, cardLang)
            }
        }
    }

    // Auto play slideshow logic
    LaunchedEffect(isAutoPlay, currentIndex, isFlipped, isCompleted) {
        if (isAutoPlay && !isCompleted && cardList.isNotEmpty()) {
            val currentCard = cardList[currentIndex]
            val cardLang = currentCard.languageCode.ifEmpty { languageTag }
            // Speak front word
            if (!isFlipped) {
                onSpeak(currentCard.frontWord, cardLang)
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
                    colors = listOf(Color(0xFFF0F9FF), Color(0xFFE0F2FE))
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

        val handleToggleStar: () -> Unit = {
            val newStarred = !currentCard.isStarred
            onToggleStar(currentCard.id, currentCard.isStarred)
            cardList = cardList.map {
                if (it.id == currentCard.id) it.copy(isStarred = newStarred) else it
            }
            toastMessage = if (newStarred) "✓ Đã thêm \"${currentCard.frontWord}\" vào Từ điển" else "Đã bỏ lưu từ"
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // TOP BAR (Pinned to top)
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
                    } else {
                        Spacer(modifier = Modifier.size(42.dp))
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

                    // Auto-play button in top right
                    IconButton(
                        onClick = { isAutoPlay = !isAutoPlay },
                        modifier = Modifier
                            .size(42.dp)
                            .background(if (isAutoPlay) NTKPrimary else Color.White, CircleShape)
                            .shadow(2.dp, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isAutoPlay) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Tự động học",
                            tint = if (isAutoPlay) Color.White else NTKPrimary,
                            modifier = Modifier.size(22.dp)
                        )
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

            // CENTER SECTION: Vertically centers the Card along with the Controls above & below it
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // 1. VIBRANT ACTION BUTTONS (Situated directly above the card: Trộn thẻ | Lưu từ điển)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // VIBRANT SHUFFLE BUTTON (Warm Golden Amber Theme)
                        Surface(
                            onClick = {
                                cardList = cardList.shuffled()
                                currentIndex = 0
                                isFlipped = false
                            },
                            shape = RoundedCornerShape(18.dp),
                            color = Color(0xFFFFF7ED), // Soft warm amber container
                            shadowElevation = 2.dp,
                            border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFFDBA74)),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_shuffle_cards")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shuffle,
                                    contentDescription = "Trộn thẻ",
                                    tint = Color(0xFFEA580C),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Trộn thẻ",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFC2410C)
                                )
                            }
                        }

                        // VIBRANT SAVE TO DICTIONARY BUTTON (Royal Purple / Fuchsia Theme with Bookmark Icon)
                        Surface(
                            onClick = handleToggleStar,
                            shape = RoundedCornerShape(18.dp),
                            color = if (currentCard.isStarred) Color(0xFFF3E8FF) else Color(0xFFFAF5FF),
                            shadowElevation = 2.dp,
                            border = androidx.compose.foundation.BorderStroke(
                                1.2.dp,
                                if (currentCard.isStarred) Color(0xFFC084FC) else Color(0xFFE9D5FF)
                            ),
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("btn_save_dictionary")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = if (currentCard.isStarred) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "Thêm vào Từ điển",
                                    tint = if (currentCard.isStarred) Color(0xFF7E22CE) else Color(0xFF9333EA),
                                    modifier = Modifier.size(17.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (currentCard.isStarred) "Đã lưu từ" else "+ Lưu từ điển",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (currentCard.isStarred) Color(0xFF6B21A8) else Color(0xFF7E22CE)
                                )
                            }
                        }
                    }

                    // 2. MIDDLE 3D FLASHCARD (Centered in screen)
                    val currentCardLang = currentCard.languageCode.ifEmpty { languageTag }
                    Flashcard3DView(
                        card = currentCard,
                        isFlipped = isFlipped,
                        userVipLevel = userVipLevel,
                        onFlip = { isFlipped = !isFlipped },
                        onSpeak = { text -> onSpeak(text, currentCardLang) },
                        onToggleStar = handleToggleStar,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3. QUICK STUDY UTILITY ACTIONS (Face Toggle: Mặt trước | Mặt sau & Phát âm)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Face Toggle Button (Mặt trước / Mặt sau)
                        Surface(
                            onClick = { isFlipped = !isFlipped },
                            shape = RoundedCornerShape(14.dp),
                            color = if (isFlipped) Color(0xFFECFDF5) else Color(0xFFEFF6FF),
                            border = androidx.compose.foundation.BorderStroke(
                                1.2.dp,
                                if (isFlipped) Color(0xFF86EFAC) else Color(0xFF93C5FD)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("btn_flip_card")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Lật thẻ",
                                    tint = if (isFlipped) Color(0xFF16A34A) else Color(0xFF2563EB),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isFlipped) "Mặt sau" else "Mặt trước",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isFlipped) Color(0xFF15803D) else Color(0xFF1D4ED8)
                                )
                            }
                        }

                        // Pronounce Button
                        Surface(
                            onClick = {
                                val (textToSpeak, speakLang) = if (isFlipped) {
                                    currentCard.backMeaning to "vi-VN"
                                } else {
                                    currentCard.frontWord to currentCardLang
                                }
                                onSpeak(textToSpeak, speakLang)
                            },
                            shape = RoundedCornerShape(14.dp),
                            color = NTKPrimary.copy(alpha = 0.1f),
                            border = androidx.compose.foundation.BorderStroke(1.2.dp, NTKPrimary.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("btn_speak_word")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Phát âm",
                                    tint = NTKPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Phát âm",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NTKPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 4. BOTTOM NAVIGATION BUTTONS (Previous & Next Card)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Previous Card Button
                        OutlinedButton(
                            onClick = {
                                if (currentIndex > 0) {
                                    currentIndex--
                                    isFlipped = false
                                }
                            },
                            enabled = currentIndex > 0,
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp)
                                .testTag("btn_prev_card"),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White,
                                contentColor = NTKTextPrimary,
                                disabledContainerColor = Color(0xFFF8FAFC),
                                disabledContentColor = Color(0xFF94A3B8)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (currentIndex > 0) Color(0xFFCBD5E1) else Color(0xFFE2E8F0)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Thẻ trước",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Thẻ trước",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Next Card / Flip / Complete Button
                        val isLastCard = currentIndex >= cardList.size - 1
                        Button(
                            onClick = {
                                if (!isFlipped) {
                                    // Phải lật thẻ xem nghĩa trước khi sang thẻ khác
                                    isFlipped = true
                                } else {
                                    // Khi đã lật xem nghĩa rồi mới chuyển sang thẻ tiếp theo
                                    if (!isLastCard) {
                                        currentIndex++
                                        isFlipped = false
                                    } else {
                                        isCompleted = true
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1.2f)
                                .height(54.dp)
                                .shadow(
                                    elevation = 4.dp,
                                    shape = RoundedCornerShape(18.dp),
                                    spotColor = if (isFlipped && isLastCard) Color(0x3310B981) else Color(0x336366F1)
                                )
                                .testTag("btn_next_card"),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = when {
                                    isFlipped && isLastCard -> Color(0xFF10B981)
                                    !isFlipped -> Color(0xFF2563EB)
                                    else -> NTKPrimary
                                }
                            )
                        ) {
                            val buttonText = when {
                                !isFlipped -> "Lật xem nghĩa"
                                isLastCard -> "Hoàn thành ✓"
                                else -> "Thẻ tiếp theo"
                            }
                            Text(
                                text = buttonText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = when {
                                    !isFlipped -> Icons.Default.Refresh
                                    isLastCard -> Icons.Default.CheckCircle
                                    else -> Icons.AutoMirrored.Filled.ArrowForward
                                },
                                contentDescription = buttonText,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
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
                    .padding(vertical = 12.dp)
                    .shadow(16.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
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
                        text = if (isOnboardingTrial) "Xuất sắc! Đã học xong 5 thẻ đầu tiên 🎉" else "Xuất sắc! Đã hoàn thành!",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = NTKTextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isOnboardingTrial) {
                            "Bạn đã ghi nhớ trọn vẹn 5 từ vựng cơ bản của $deckTitle. Hãy làm bài kiểm tra trắc nghiệm nhanh ngay bây giờ!"
                        } else {
                            "Bạn vừa hoàn thành ôn luyện ${cardList.size} thẻ ghi nhớ trong bộ \"$deckTitle\"."
                        },
                        fontSize = 14.sp,
                        color = NTKTextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onStartQuiz,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .shadow(6.dp, RoundedCornerShape(16.dp), spotColor = NTKPrimary.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NTKPrimary)
                    ) {
                        Text(
                            text = if (isOnboardingTrial) "⚡ Tiếp tục làm bài kiểm tra" else "⚡ Thử thách Trắc nghiệm ngay",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    if (!isOnboardingTrial) {
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

        // Transient Toast Message for Instant Bookmark Feedback
        AnimatedVisibility(
            visible = toastMessage != null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { 60 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { 60 }),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 28.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xEE1E1B4B), // Deep indigo toast
                shadowElevation = 8.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF818CF8).copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = null,
                        tint = Color(0xFFC084FC),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = toastMessage ?: "",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
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
