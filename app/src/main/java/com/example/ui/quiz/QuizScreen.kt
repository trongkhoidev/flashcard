package com.example.ui.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import com.example.ui.theme.EasyGreen
import com.example.ui.theme.HardRed
import com.example.ui.theme.NTKPrimary
import com.example.ui.theme.NTKPrimaryDark
import com.example.ui.theme.NTKTextPrimary
import com.example.ui.theme.NTKTextSecondary
import kotlinx.coroutines.delay

@Composable
fun QuizScreen(
    deckTitle: String,
    languageTag: String,
    cards: List<FlashCardEntity>,
    onBack: () -> Unit,
    onSpeak: (String, String) -> Unit,
    onFinishQuiz: (score: Int, total: Int) -> Unit,
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
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar
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
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }

                    Text(
                        text = "Trắc nghiệm: $deckTitle",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = NTKTextPrimary
                    )

                    // Score pill
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFE0F2FE), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "⭐ $score / ${quizCards.size}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = NTKPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = { (currentIndex + 1).toFloat() / quizCards.size.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = NTKPrimary,
                    trackColor = Color(0xFFE2E8F0)
                )
            }

            // Question Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = NTKPrimary.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(24.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Chọn nghĩa đúng của từ sau:",
                        fontSize = 13.sp,
                        color = NTKTextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = currentCard.frontWord,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Black,
                        color = NTKPrimaryDark,
                        textAlign = TextAlign.Center
                    )

                    if (currentCard.phonetic.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentCard.phonetic,
                            fontSize = 15.sp,
                            color = NTKPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    IconButton(
                        onClick = { onSpeak(currentCard.frontWord, languageTag) },
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFE0F2FE), CircleShape)
                    ) {
                        Icon(
                            Icons.Default.VolumeUp,
                            contentDescription = "Phát âm",
                            tint = NTKPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Answer Options
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                currentOptions.forEach { option ->
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

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !isAnswerSubmitted) {
                                selectedOption = option
                                isAnswerSubmitted = true
                                if (isCorrectOption) {
                                    score++
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
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = option,
                                fontSize = 15.sp,
                                fontWeight = if (isSelected || (isAnswerSubmitted && isCorrectOption)) FontWeight.Bold else FontWeight.Medium,
                                color = NTKTextPrimary,
                                modifier = Modifier.weight(1f)
                            )

                            if (isAnswerSubmitted) {
                                if (isCorrectOption) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EasyGreen)
                                } else if (isSelected) {
                                    Icon(Icons.Default.Close, contentDescription = null, tint = HardRed)
                                }
                            }
                        }
                    }
                }
            }

            // Next Question Button
            Button(
                onClick = {
                    if (currentIndex < quizCards.size - 1) {
                        currentIndex++
                        selectedOption = null
                        isAnswerSubmitted = false
                    } else {
                        isQuizCompleted = true
                        onFinishQuiz(score, quizCards.size)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_next_quiz"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NTKPrimary),
                enabled = isAnswerSubmitted
            ) {
                Text(
                    text = if (currentIndex < quizCards.size - 1) "Câu hỏi tiếp theo →" else "Xem kết quả ⭐",
                    fontSize = 16.sp,
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
                            .size(76.dp)
                            .background(Color(0xFFFEF3C7), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Thành tích",
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(46.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Kết Quả Trắc Nghiệm",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = NTKTextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Bạn đạt được $score / ${quizCards.size} câu chính xác!",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = NTKPrimary
                    )

                    val accuracy = if (quizCards.isNotEmpty()) (score * 100) / quizCards.size else 0
                    Text(
                        text = "Tỷ lệ chính xác: $accuracy%",
                        fontSize = 13.sp,
                        color = NTKTextSecondary
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            currentIndex = 0
                            score = 0
                            selectedOption = null
                            isAnswerSubmitted = false
                            isQuizCompleted = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NTKPrimary)
                    ) {
                        Text("Làm lại bài kiểm tra", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(onClick = onBack) {
                        Text("Trở về danh sách bài học", color = NTKTextSecondary)
                    }
                }
            }
        }
    }
}
