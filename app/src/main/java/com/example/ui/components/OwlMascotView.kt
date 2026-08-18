package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.BubbleEnglish
import com.example.ui.theme.BubbleJapanese
import com.example.ui.theme.BubbleKorean
import com.example.ui.theme.BubbleVietnamese

@Composable
fun OwlMascotView(
    modifier: Modifier = Modifier,
    onLanguageClick: (String) -> Unit = {}
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(290.dp),
        contentAlignment = Alignment.Center
    ) {
        // Soft radial glow circle behind mascot
        Box(
            modifier = Modifier
                .size(240.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFE8EBFF),
                            Color(0xFFF3F0FF),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        // Owl 3D Mascot Image (Loaded synchronously & reliably via painterResource)
        Image(
            painter = painterResource(id = R.drawable.ntk_owl_mascot),
            contentDescription = "NTK Owl Mascot",
            modifier = Modifier
                .size(235.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        // Top Left Speech Bubble: "English"
        LanguageSpeechBubble(
            text = "English",
            backgroundColor = BubbleEnglish,
            tailDirection = BubbleTailDirection.BOTTOM_RIGHT,
            floatOffset = 4.5f,
            durationMs = 2100,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 12.dp, y = 38.dp),
            onClick = { onLanguageClick("en") }
        )

        // Mid Left Speech Bubble: "日本語"
        LanguageSpeechBubble(
            text = "日本語",
            backgroundColor = BubbleJapanese,
            tailDirection = BubbleTailDirection.BOTTOM_RIGHT,
            floatOffset = 3.5f,
            durationMs = 2400,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = 6.dp, y = 30.dp),
            onClick = { onLanguageClick("ja") }
        )

        // Top Right Speech Bubble: "한국어"
        LanguageSpeechBubble(
            text = "한국어",
            backgroundColor = BubbleKorean,
            tailDirection = BubbleTailDirection.BOTTOM_LEFT,
            floatOffset = 5f,
            durationMs = 2300,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-16).dp, y = 48.dp),
            onClick = { onLanguageClick("ko") }
        )

        // Mid Right Speech Bubble: "Tiếng Việt"
        LanguageSpeechBubble(
            text = "Tiếng Việt",
            backgroundColor = BubbleVietnamese,
            tailDirection = BubbleTailDirection.BOTTOM_LEFT,
            floatOffset = 4f,
            durationMs = 2600,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = (-10).dp, y = 36.dp),
            onClick = { onLanguageClick("vi") }
        )
    }
}
