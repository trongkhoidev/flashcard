package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class BubbleTailDirection {
    BOTTOM_RIGHT,
    BOTTOM_LEFT,
    TOP_RIGHT,
    TOP_LEFT
}

@Composable
fun LanguageSpeechBubble(
    text: String,
    backgroundColor: Color,
    tailDirection: BubbleTailDirection = BubbleTailDirection.BOTTOM_RIGHT,
    floatOffset: Float = 4f,
    durationMs: Int = 2000,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bubble_float_$text")
    val floatY by infiniteTransition.animateFloat(
        initialValue = -floatOffset,
        targetValue = floatOffset,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float_y_$text"
    )

    Box(
        modifier = modifier
            .offset(y = floatY.dp)
            .clickable { onClick() }
            .testTag("bubble_$text")
    ) {
        Column(
            horizontalAlignment = when (tailDirection) {
                BubbleTailDirection.BOTTOM_RIGHT, BubbleTailDirection.TOP_RIGHT -> Alignment.End
                BubbleTailDirection.BOTTOM_LEFT, BubbleTailDirection.TOP_LEFT -> Alignment.Start
            }
        ) {
            // Main Bubble Container
            Box(
                modifier = Modifier
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(18.dp),
                        spotColor = backgroundColor.copy(alpha = 0.4f),
                        ambientColor = backgroundColor.copy(alpha = 0.2f)
                    )
                    .background(
                        color = backgroundColor,
                        shape = RoundedCornerShape(18.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 7.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Pointer Tail
            if (tailDirection == BubbleTailDirection.BOTTOM_RIGHT || tailDirection == BubbleTailDirection.BOTTOM_LEFT) {
                Canvas(
                    modifier = Modifier
                        .size(width = 12.dp, height = 8.dp)
                        .offset(
                            x = if (tailDirection == BubbleTailDirection.BOTTOM_RIGHT) (-14).dp else 14.dp,
                            y = (-1).dp
                        )
                ) {
                    val path = Path().apply {
                        if (tailDirection == BubbleTailDirection.BOTTOM_RIGHT) {
                            moveTo(0f, 0f)
                            lineTo(size.width, 0f)
                            lineTo(size.width * 0.8f, size.height)
                            close()
                        } else {
                            moveTo(0f, 0f)
                            lineTo(size.width, 0f)
                            lineTo(size.width * 0.2f, size.height)
                            close()
                        }
                    }
                    drawPath(path = path, color = backgroundColor)
                }
            }
        }
    }
}
