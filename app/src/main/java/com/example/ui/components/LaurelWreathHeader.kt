package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NTKPrimary
import com.example.ui.theme.NTKTextPrimary
import com.example.ui.theme.NTKTextSecondary

@Composable
fun LaurelWreathHeader(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Name "NTK" and "FlashCard"
        Text(
            text = "NTK",
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            color = NTKPrimary,
            letterSpacing = 2.sp,
            textAlign = TextAlign.Center
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "FlashCard",
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                color = NTKTextPrimary,
                letterSpacing = (-0.5).sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Laurel wreath section with Slogan
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            // Left Laurel Branch
            LaurelBranch(isLeft = true)

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Học nhanh – Nhớ lâu",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = NTKPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Mọi ngôn ngữ trong tầm tay",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = NTKTextSecondary,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right Laurel Branch
            LaurelBranch(isLeft = false)
        }
    }
}

@Composable
private fun LaurelBranch(isLeft: Boolean, modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier.size(width = 24.dp, height = 36.dp)
    ) {
        val strokeColor = Color(0xFFA5B4FC)
        val leafColor = Color(0xFFC7D2FE)

        val stemPath = Path().apply {
            if (isLeft) {
                moveTo(size.width * 0.85f, size.height * 0.95f)
                cubicTo(
                    size.width * 0.4f, size.height * 0.7f,
                    size.width * 0.2f, size.height * 0.3f,
                    size.width * 0.7f, size.height * 0.05f
                )
            } else {
                moveTo(size.width * 0.15f, size.height * 0.95f)
                cubicTo(
                    size.width * 0.6f, size.height * 0.7f,
                    size.width * 0.8f, size.height * 0.3f,
                    size.width * 0.3f, size.height * 0.05f
                )
            }
        }

        // Draw Stem
        drawPath(
            path = stemPath,
            color = strokeColor,
            style = Stroke(width = 2.5f, cap = StrokeCap.Round)
        )

        // Draw Leaves along the branch
        val leafPositions = listOf(
            0.2f to if (isLeft) -4f else 4f,
            0.45f to if (isLeft) -5f else 5f,
            0.7f to if (isLeft) -4f else 4f,
            0.9f to if (isLeft) 2f else -2f
        )

        for ((t, offsetAngle) in leafPositions) {
            val y = size.height * (1f - t)
            val x = if (isLeft) size.width * (0.35f + (t * 0.3f)) else size.width * (0.65f - (t * 0.3f))
            drawCircle(
                color = leafColor,
                radius = 3.5f,
                center = androidx.compose.ui.geometry.Offset(x, y)
            )
        }
    }
}
