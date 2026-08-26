package com.example.ui.match

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.ui.theme.NTKTextPrimary
import com.example.ui.theme.NTKTextSecondary
import kotlinx.coroutines.delay

data class MatchItem(
    val id: Long,
    val text: String,
    val isFront: Boolean,
    val pairId: Long
)

@Composable
fun WordMatchScreen(
    deckTitle: String,
    cards: List<FlashCardEntity>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gameCards = remember(cards) { cards.take(6) }

    val matchItems = remember(gameCards) {
        val list = mutableListOf<MatchItem>()
        var counter = 1L
        gameCards.forEach { card ->
            list.add(MatchItem(id = counter++, text = card.frontWord, isFront = true, pairId = card.id))
            list.add(MatchItem(id = counter++, text = card.backMeaning, isFront = false, pairId = card.id))
        }
        list.shuffled()
    }

    var selectedItem by remember { mutableStateOf<MatchItem?>(null) }
    val matchedPairIds = remember { mutableStateListOf<Long>() }
    var wrongMatchId by remember { mutableStateOf<Long?>(null) }
    var movesCount by remember { mutableIntStateOf(0) }
    var isWon by remember { mutableStateOf(false) }

    LaunchedEffect(matchedPairIds.size, gameCards.size) {
        if (gameCards.isNotEmpty() && matchedPairIds.size == gameCards.size) {
            delay(400)
            isWon = true
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
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

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Ghép thẻ từ vựng",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = NTKTextPrimary
                    )
                    Text(
                        text = "Lượt thử: $movesCount",
                        fontSize = 12.sp,
                        color = NTKPrimary
                    )
                }

                // Matched count pill
                Box(
                    modifier = Modifier
                        .background(Color(0xFFD1FAE5), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "✓ ${matchedPairIds.size}/${gameCards.size}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = EasyGreen
                    )
                }
            }

            Text(
                text = "Chạm vào từ vựng và nghĩa tiếng Việt tương ứng để ghép cặp!",
                fontSize = 13.sp,
                color = NTKTextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            )

            // Grid of cards
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(matchItems) { item ->
                    val isMatched = matchedPairIds.contains(item.pairId)
                    val isSelected = selectedItem?.id == item.id
                    val isWrong = wrongMatchId == item.id

                    val bg = when {
                        isMatched -> Color(0xFFE2E8F0).copy(alpha = 0.4f)
                        isSelected -> Color(0xFFE0F2FE)
                        isWrong -> Color(0xFFFEE2E2)
                        else -> Color.White
                    }

                    val borderColor = when {
                        isMatched -> Color.Transparent
                        isSelected -> NTKPrimary
                        isWrong -> HardRed
                        else -> Color(0xFFE2E8F0)
                    }

                    Surface(
                        modifier = Modifier
                            .height(86.dp)
                            .clickable(enabled = !isMatched) {
                                if (selectedItem == null) {
                                    selectedItem = item
                                } else {
                                    val first = selectedItem!!
                                    if (first.id != item.id) {
                                        movesCount++
                                        if (first.pairId == item.pairId && first.isFront != item.isFront) {
                                            // Matched!
                                            matchedPairIds.add(item.pairId)
                                            selectedItem = null
                                        } else {
                                            // Wrong match
                                            wrongMatchId = item.id
                                            selectedItem = null
                                        }
                                    }
                                }
                            }
                            .testTag("match_card_${item.id}"),
                        shape = RoundedCornerShape(16.dp),
                        color = bg,
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, borderColor),
                        shadowElevation = if (isMatched) 0.dp else 2.dp
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item.text,
                                fontSize = if (item.isFront) 15.sp else 13.sp,
                                fontWeight = if (item.isFront) FontWeight.Bold else FontWeight.Medium,
                                color = if (isMatched) Color(0xFF94A3B8) else NTKTextPrimary,
                                textAlign = TextAlign.Center,
                                maxLines = 3
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        // WIN CELEBRATION MODAL
        AnimatedVisibility(
            visible = isWon,
            enter = fadeIn() + scaleIn(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.88f)
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
                            contentDescription = null,
                            tint = EasyGreen,
                            modifier = Modifier.size(42.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Chúc Mừng!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = NTKTextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Bạn đã hoàn thành ghép đúng toàn bộ các cặp từ trong $movesCount lượt thử!",
                        fontSize = 14.sp,
                        color = NTKTextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NTKPrimary)
                    ) {
                        Text("Tiếp tục học bài khác", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
