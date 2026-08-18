package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.rounded.Flip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FlashCardEntity
import com.example.ui.theme.NTKCardBorder
import com.example.ui.theme.NTKPrimary
import com.example.ui.theme.NTKPrimaryDark
import com.example.ui.theme.NTKPrimaryLight
import com.example.ui.theme.NTKTextMuted
import com.example.ui.theme.NTKTextPrimary
import com.example.ui.theme.NTKTextSecondary

@Composable
fun Flashcard3DView(
    card: FlashCardEntity,
    isFlipped: Boolean,
    onFlip: () -> Unit,
    onSpeak: (String) -> Unit,
    onToggleStar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotationY by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "card_rotation_y"
    )

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(340.dp)
            .graphicsLayer {
                this.rotationY = rotationY
                cameraDistance = 12f * density
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onFlip()
            }
            .testTag("flashcard_3d_container"),
        contentAlignment = Alignment.Center
    ) {
        if (rotationY <= 90f) {
            // FRONT SIDE OF CARD
            CardFrontSide(
                card = card,
                onSpeak = onSpeak,
                onToggleStar = onToggleStar,
                onFlip = onFlip
            )
        } else {
            // BACK SIDE OF CARD (Rotated 180 so it appears upright)
            CardBackSide(
                card = card,
                onSpeak = onSpeak,
                onToggleStar = onToggleStar,
                onFlip = onFlip,
                modifier = Modifier.graphicsLayer { this.rotationY = 180f }
            )
        }
    }
}

@Composable
private fun CardFrontSide(
    card: FlashCardEntity,
    onSpeak: (String) -> Unit,
    onToggleStar: () -> Unit,
    onFlip: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxSize()
            .shadow(
                elevation = 14.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color(0x33334155),
                ambientColor = Color(0x1F334155)
            ),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFCBD5E1)) // Crisp Gray Focus Border
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar: Part of speech badge & Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Part of speech pill
                Box(
                    modifier = Modifier
                        .background(Color(0xFFEEF2FF), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = card.partOfSpeech.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NTKPrimary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Pronounce Audio Button
                    IconButton(
                        onClick = { onSpeak(card.frontWord) },
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFFF1F5F9), CircleShape)
                            .testTag("btn_pronounce_front")
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Phát âm",
                            tint = NTKPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Star Bookmark Button
                    IconButton(
                        onClick = onToggleStar,
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFFF1F5F9), CircleShape)
                            .testTag("btn_star_card")
                    ) {
                        Icon(
                            imageVector = if (card.isStarred) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Yêu thích",
                            tint = if (card.isStarred) Color(0xFFF59E0B) else NTKTextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Center: Main Word & Phonetic
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = card.frontWord,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = NTKTextPrimary,
                    textAlign = TextAlign.Center
                )

                if (card.phonetic.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = card.phonetic,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = NTKPrimary,
                        textAlign = TextAlign.Center
                    )
                }

                if (card.frontExample.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "\"${card.frontExample}\"",
                            fontSize = 13.sp,
                            fontStyle = FontStyle.Italic,
                            color = NTKTextSecondary,
                            textAlign = TextAlign.Center,
                            maxLines = 3
                        )
                    }
                }
            }

            // Bottom Prompt: Tap to flip hint
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onFlip() }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Flip,
                    contentDescription = null,
                    tint = NTKPrimaryLight,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Chạm thẻ để xem giải nghĩa",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NTKPrimaryLight
                )
            }
        }
    }
}

@Composable
private fun CardBackSide(
    card: FlashCardEntity,
    onSpeak: (String) -> Unit,
    onToggleStar: () -> Unit,
    onFlip: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxSize()
            .shadow(
                elevation = 14.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color(0x33334155),
                ambientColor = Color(0x1F334155)
            ),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFFAF5FF), // Soft lavender background
        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFCBD5E1)) // Crisp Gray Focus Border
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFFE0E7FF), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Ý NGHĨA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NTKPrimary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onSpeak(card.frontWord) },
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color.White, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Phát âm",
                            tint = NTKPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = onToggleStar,
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color.White, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (card.isStarred) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Yêu thích",
                            tint = if (card.isStarred) Color(0xFFF59E0B) else NTKTextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Center Meaning & Translation
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = card.backMeaning,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = NTKTextPrimary,
                    textAlign = TextAlign.Center
                )

                if (card.backExampleTranslation.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = card.backExampleTranslation,
                        fontSize = 13.sp,
                        color = NTKTextSecondary,
                        textAlign = TextAlign.Center
                    )
                }

                if (card.memoryTip.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFEF3C7), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "💡 ${card.memoryTip}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF92400E),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Bottom Prompt: Flip back
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onFlip() }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Flip,
                    contentDescription = null,
                    tint = NTKPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Lật về mặt trước",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NTKPrimary
                )
            }
        }
    }
}
