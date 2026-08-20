package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.NTKPrimary
import com.example.ui.theme.NTKPrimaryDark
import com.example.ui.theme.NTKPrimaryLight
import com.example.ui.theme.NTKTertiary

import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun GlowingCardsHeader(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "cards_float")
    val floatY by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cards_y"
    )

    Box(
        modifier = modifier
            .size(100.dp, 84.dp)
            .graphicsLayer {
                translationY = floatY.dp.toPx()
            },
        contentAlignment = Alignment.Center
    ) {
        // Subtle glow background
        Box(
            modifier = Modifier
                .size(76.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            NTKPrimaryLight.copy(alpha = 0.35f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
        )

        // Back Left Card
        Box(
            modifier = Modifier
                .offset(x = (-10).dp, y = (-2).dp)
                .size(54.dp, 68.dp)
                .rotate(-14f)
                .shadow(6.dp, RoundedCornerShape(14.dp), spotColor = NTKPrimary.copy(alpha = 0.3f))
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF38BDF8), Color(0xFF0EA5E9))
                    ),
                    shape = RoundedCornerShape(14.dp)
                )
        )

        // Back Right Card
        Box(
            modifier = Modifier
                .offset(x = 10.dp, y = (-2).dp)
                .size(54.dp, 68.dp)
                .rotate(14f)
                .shadow(6.dp, RoundedCornerShape(14.dp), spotColor = NTKTertiary.copy(alpha = 0.3f))
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF0284C7), Color(0xFF0369A1))
                    ),
                    shape = RoundedCornerShape(14.dp)
                )
        )

        // Main Center Front Card
        Box(
            modifier = Modifier
                .size(56.dp, 72.dp)
                .shadow(12.dp, RoundedCornerShape(16.dp), spotColor = NTKPrimaryDark.copy(alpha = 0.45f))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0EA5E9),
                            Color(0xFF0284C7)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            // Inner Star Icon
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(28.dp)
                    .offset(y = (-4).dp)
            )
        }

        // Sparkle 1 Top Right
        Box(
            modifier = Modifier
                .offset(x = 42.dp, y = (-18).dp)
                .size(10.dp)
                .rotate(45f)
                .background(Color(0xFFBAE6FD), shape = RoundedCornerShape(2.dp))
        )

        // Sparkle 2 Top Left
        Box(
            modifier = Modifier
                .offset(x = (-38).dp, y = (-14).dp)
                .size(7.dp)
                .rotate(45f)
                .background(Color(0xFFE0F2FE), shape = RoundedCornerShape(1.5.dp))
        )

        // Sparkle 3 Bottom Left
        Box(
            modifier = Modifier
                .offset(x = (-34).dp, y = 16.dp)
                .size(6.dp)
                .rotate(45f)
                .background(Color(0xFFE0E7FF), shape = RoundedCornerShape(1.dp))
        )
    }
}
