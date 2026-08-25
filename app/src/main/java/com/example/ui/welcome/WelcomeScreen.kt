package com.example.ui.welcome

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import kotlinx.coroutines.delay
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlowingCardsHeader
import com.example.ui.components.LaurelWreathHeader
import com.example.ui.components.OwlMascotView
import com.example.ui.theme.NTKBgGradientBottom
import com.example.ui.theme.NTKBgGradientTop
import com.example.ui.theme.NTKGradientEnd
import com.example.ui.theme.NTKGradientStart
import com.example.ui.theme.NTKPrimary
import com.example.ui.theme.NTKTextPrimary
import com.example.ui.theme.NTKTextSecondary

@Composable
fun WelcomeScreen(
    onStartLearning: () -> Unit,
    onLoginClick: () -> Unit,
    onSelectLanguage: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var currentPage by remember { mutableIntStateOf(0) }
    val totalPages = 3

    // Auto-advance slide text carousel every 3.5 seconds
    LaunchedEffect(Unit) {
        while (true) {
            delay(3500)
            currentPage = (currentPage + 1) % totalPages
        }
    }

    // Background Gradient matching the Squirtle ocean theme
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF),
                        Color(0xFFF0F9FF),
                        Color(0xFFE0F2FE),
                        Color(0xFFBAE6FD).copy(alpha = 0.35f)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // TOP SECTION: Glowing Triple Cards & App Title with Laurel Slogan
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                // Stacked Glowing Cards Icon
                GlowingCardsHeader()

                Spacer(modifier = Modifier.height(10.dp))

                // Brand Title & Slogan with Laurel Wreath
                LaurelWreathHeader()
            }

            // MIDDLE SECTION: Owl Mascot with Floating Speech Bubbles & Pager Carousel
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                // Interactive 3D Owl mascot with floating multilingual bubbles
                OwlMascotView(
                    onLanguageClick = { langCode ->
                        onSelectLanguage(langCode)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Slide feature description text with smooth slide + fade transition
                AnimatedContent(
                    targetState = currentPage,
                    transitionSpec = {
                        (slideInHorizontally(animationSpec = tween(400)) { width -> width / 4 } + fadeIn(animationSpec = tween(400)))
                            .togetherWith(slideOutHorizontally(animationSpec = tween(400)) { width -> -width / 4 } + fadeOut(animationSpec = tween(400)))
                    },
                    label = "page_description"
                ) { page ->
                    val (title, subtitle) = when (page) {
                        0 -> "Đa ngôn ngữ trực quan" to "Khám phá Tiếng Anh, Hàn, Nhật, Trung, Pháp với thẻ ghi nhớ sống động"
                        1 -> "Thuộc từ nhanh gấp 3 lần" to "Hệ thống lặp lại ngắt quãng (SRS) giúp từ vựng ghi sâu vào trí nhớ dài hạn"
                        else -> "Luyện phát âm & Phản xạ" to "Nghe phát âm chuẩn bản xứ và thử thách với mini-game đố vui hấp dẫn"
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = NTKTextPrimary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = subtitle,
                            fontSize = 12.sp,
                            color = NTKTextSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Page Indicator Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until totalPages) {
                        val isSelected = i == currentPage
                        val dotWidth by animateDpAsState(
                            targetValue = if (isSelected) 24.dp else 8.dp,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                            label = "dot_width_$i"
                        )

                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(dotWidth)
                                .shadow(if (isSelected) 2.dp else 0.dp, RoundedCornerShape(4.dp))
                                .background(
                                    color = if (isSelected) NTKPrimary else Color(0xFFBAE6FD),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .clickable { currentPage = i }
                        )
                    }
                }
            }

            // BOTTOM SECTION: Action Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Primary Action Button: "Bắt đầu học ngay →"
                Button(
                    onClick = onStartLearning,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .shadow(
                            elevation = 10.dp,
                            shape = RoundedCornerShape(20.dp),
                            spotColor = NTKPrimary.copy(alpha = 0.5f)
                        )
                        .testTag("start_learning_button"),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NTKPrimary
                    ),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Bắt đầu học ngay",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Secondary Action: "👤 Đăng nhập"
                TextButton(
                    onClick = onLoginClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("login_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "Đăng nhập",
                            tint = NTKPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Đăng nhập",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = NTKPrimary
                        )
                    }
                }
            }
        }
    }
}
