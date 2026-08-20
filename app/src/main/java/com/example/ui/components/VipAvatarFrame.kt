package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class VipLevel(
    val levelNumber: Int,
    val title: String,
    val badgeLabel: String,
    val crownEmoji: String,
    val badgeBgColor: Color,
    val badgeTextColor: Color,
    val gradientColors: List<Color>
) {
    NONE(
        levelNumber = 0,
        title = "Thường",
        badgeLabel = "MEMBER",
        crownEmoji = "",
        badgeBgColor = Color(0xFF64748B),
        badgeTextColor = Color.White,
        gradientColors = listOf(Color(0xFFE2E8F0), Color(0xFF94A3B8))
    ),
    VIP1(
        levelNumber = 1,
        title = "VIP 1 Đồng",
        badgeLabel = "VIP 1",
        crownEmoji = "🥉",
        badgeBgColor = Color(0xFFEA580C),
        badgeTextColor = Color.White,
        gradientColors = listOf(Color(0xFFEA580C), Color(0xFFFDBA74), Color(0xFFC2410C), Color(0xFFF97316), Color(0xFFEA580C))
    ),
    VIP2(
        levelNumber = 2,
        title = "VIP 2 Bạc",
        badgeLabel = "VIP 2",
        crownEmoji = "🥈",
        badgeBgColor = Color(0xFF475569),
        badgeTextColor = Color.White,
        gradientColors = listOf(Color(0xFFE2E8F0), Color(0xFF94A3B8), Color(0xFF475569), Color(0xFFCBD5E1), Color(0xFFFFFFFF), Color(0xFF64748B))
    ),
    VIP3(
        levelNumber = 3,
        title = "VIP 3 Bạch Kim",
        badgeLabel = "VIP 3",
        crownEmoji = "💠",
        badgeBgColor = Color(0xFF0284C7),
        badgeTextColor = Color.White,
        gradientColors = listOf(Color(0xFF0284C7), Color(0xFF38BDF8), Color(0xFF06B6D4), Color(0xFF67E8F9), Color(0xFF1D4ED8), Color(0xFF0284C7))
    ),
    VIP4(
        levelNumber = 4,
        title = "VIP 4 Hoàng Gia",
        badgeLabel = "VIP 4",
        crownEmoji = "👑",
        badgeBgColor = Color(0xFFD97706),
        badgeTextColor = Color.White,
        gradientColors = listOf(Color(0xFFD97706), Color(0xFFFBBF24), Color(0xFFFEF08A), Color(0xFFFFFFFF), Color(0xFFF59E0B), Color(0xFFB45309), Color(0xFFD97706))
    ),
    VIP5(
        levelNumber = 5,
        title = "VIP 5 Kim Cương",
        badgeLabel = "VIP 5",
        crownEmoji = "💎",
        badgeBgColor = Color(0xFF7C3AED),
        badgeTextColor = Color.White,
        gradientColors = listOf(Color(0xFFEC4899), Color(0xFF06B6D4), Color(0xFFA855F7), Color(0xFFF43F5E), Color(0xFF3B82F6), Color(0xFFEC4899))
    ),
    VIP6(
        levelNumber = 6,
        title = "VIP 6 Huyền Thoại",
        badgeLabel = "VIP 6",
        crownEmoji = "🔥",
        badgeBgColor = Color(0xFFDC2626),
        badgeTextColor = Color.White,
        gradientColors = listOf(Color(0xFFEF4444), Color(0xFFF97316), Color(0xFFFBBF24), Color(0xFFDC2626), Color(0xFF991B1B), Color(0xFFEF4444))
    ),
    VIP7(
        levelNumber = 7,
        title = "VIP 7 Tối Thượng",
        badgeLabel = "VIP 7",
        crownEmoji = "👑🔥✨",
        badgeBgColor = Color(0xFF9333EA),
        badgeTextColor = Color.White,
        gradientColors = listOf(Color(0xFFEF4444), Color(0xFFF97316), Color(0xFFFBBF24), Color(0xFF10B981), Color(0xFF06B6D4), Color(0xFF8B5CF6), Color(0xFFEC4899), Color(0xFFEF4444))
    );

    companion object {
        fun fromLevel(level: Int): VipLevel {
            return values().find { it.levelNumber == level } ?: NONE
        }
    }
}

/**
 * Avatar with VIP Outer Border Frame
 */
@Composable
fun VipAvatarFrame(
    vipLevel: VipLevel,
    avatarSize: Dp = 58.dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // Continuous Infinite Animations for VIP 1+
    val infiniteTransition = rememberInfiniteTransition(label = "vip_frame_anim")

    // 1. Continuous Rotation Angle (0 -> 360 deg)
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (vipLevel) {
                    VipLevel.VIP1 -> 4200 // Metallic Bronze
                    VipLevel.VIP2 -> 3500 // Metallic Silver
                    VipLevel.VIP3 -> 2800 // Platinum Cyan
                    VipLevel.VIP4 -> 2200 // Imperial Gold Sparkle
                    VipLevel.VIP5 -> 1700 // Cyber Neon Diamond
                    VipLevel.VIP6 -> 1200 // Mythic Flame Fire
                    VipLevel.VIP7 -> 800  // Supreme Rainbow Aurora
                    VipLevel.NONE -> 10000
                },
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // 2. Pulse Alpha for Glow Ring
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.90f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (vipLevel) {
                    VipLevel.VIP1 -> 900
                    VipLevel.VIP2 -> 850
                    VipLevel.VIP3 -> 750
                    VipLevel.VIP4 -> 650
                    VipLevel.VIP5 -> 550
                    VipLevel.VIP6 -> 450
                    VipLevel.VIP7 -> 350
                    VipLevel.NONE -> 1000
                },
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    // 3. Pulse Scale for Aura
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = when (vipLevel) {
            VipLevel.VIP1 -> 1.03f
            VipLevel.VIP2 -> 1.04f
            VipLevel.VIP3 -> 1.05f
            VipLevel.VIP4 -> 1.06f
            VipLevel.VIP5 -> 1.08f
            VipLevel.VIP6 -> 1.10f
            VipLevel.VIP7 -> 1.12f
            VipLevel.NONE -> 1.0f
        },
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (vipLevel) {
                    VipLevel.VIP1 -> 900
                    VipLevel.VIP2 -> 800
                    VipLevel.VIP3 -> 700
                    VipLevel.VIP4 -> 600
                    VipLevel.VIP5 -> 500
                    VipLevel.VIP6 -> 450
                    VipLevel.VIP7 -> 350
                    VipLevel.NONE -> 1000
                },
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val borderWidth = when (vipLevel) {
        VipLevel.NONE -> 2.dp
        VipLevel.VIP1 -> 3.dp
        VipLevel.VIP2 -> 3.2.dp
        VipLevel.VIP3 -> 3.5.dp
        VipLevel.VIP4 -> 4.dp
        VipLevel.VIP5 -> 4.5.dp
        VipLevel.VIP6 -> 5.dp
        VipLevel.VIP7 -> 5.5.dp
    }

    val shadowElevation = when (vipLevel) {
        VipLevel.NONE -> 0.dp
        VipLevel.VIP1, VipLevel.VIP2 -> 3.dp
        VipLevel.VIP3, VipLevel.VIP4 -> 5.dp
        else -> 7.dp
    }

    val topPadding = if (vipLevel != VipLevel.NONE && vipLevel.crownEmoji.isNotEmpty()) (avatarSize.value * 0.18f).coerceIn(4f, 12f).dp else 2.dp
    val bottomPadding = if (vipLevel != VipLevel.NONE) (avatarSize.value * 0.15f).coerceIn(4f, 10f).dp else 2.dp

    // Pre-calculate cached brushes for maximum rendering speed
    val glowBrush = remember(vipLevel) {
        Brush.radialGradient(
            colors = when (vipLevel) {
                VipLevel.VIP1 -> listOf(Color(0xFFEA580C).copy(alpha = 0.55f), Color(0xFFFDBA74).copy(alpha = 0.2f), Color.Transparent)
                VipLevel.VIP2 -> listOf(Color(0xFF64748B).copy(alpha = 0.60f), Color(0xFFCBD5E1).copy(alpha = 0.25f), Color.Transparent)
                VipLevel.VIP3 -> listOf(Color(0xFF06B6D4).copy(alpha = 0.70f), Color(0xFF38BDF8).copy(alpha = 0.30f), Color.Transparent)
                VipLevel.VIP4 -> listOf(Color(0xFFFBBF24).copy(alpha = 0.75f), Color(0xFFFEF08A).copy(alpha = 0.35f), Color.Transparent)
                VipLevel.VIP5 -> listOf(Color(0xFFEC4899).copy(alpha = 0.85f), Color(0xFFA855F7).copy(alpha = 0.45f), Color.Transparent)
                VipLevel.VIP6 -> listOf(Color(0xFFEF4444).copy(alpha = 0.90f), Color(0xFFF97316).copy(alpha = 0.50f), Color.Transparent)
                VipLevel.VIP7 -> listOf(Color(0xFFA855F7).copy(alpha = 0.95f), Color(0xFF06B6D4).copy(alpha = 0.60f), Color.Transparent)
                VipLevel.NONE -> listOf(Color.Transparent, Color.Transparent)
            }
        )
    }

    val sweepBrush = remember(vipLevel) {
        Brush.sweepGradient(
            colors = if (vipLevel != VipLevel.NONE) {
                vipLevel.gradientColors + vipLevel.gradientColors.first()
            } else {
                vipLevel.gradientColors
            }
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.padding(
            top = topPadding,
            bottom = bottomPadding,
            start = 2.dp,
            end = 2.dp
        )
    ) {
        // Layer 1: Animated Outer Neon / Water / Flame Glow Aura using GPU graphicsLayer
        if (vipLevel != VipLevel.NONE) {
            Box(
                modifier = Modifier
                    .size(avatarSize + (borderWidth * 2) + 12.dp)
                    .graphicsLayer {
                        this.scaleX = pulseScale
                        this.scaleY = pulseScale
                        this.alpha = pulseAlpha
                    }
                    .background(
                        brush = glowBrush,
                        shape = CircleShape
                    )
            )
        }

        // Layer 2: Rotating Sweep Gradient Ring using GPU graphicsLayer (Zero recomposition during 360 deg rotation)
        Box(
            modifier = Modifier
                .size(avatarSize + (borderWidth * 2) + 4.dp)
                .shadow(shadowElevation, CircleShape, ambientColor = vipLevel.badgeBgColor, spotColor = vipLevel.badgeBgColor)
                .graphicsLayer {
                    this.rotationZ = if (vipLevel != VipLevel.NONE) rotationAngle else 0f
                }
                .background(
                    brush = sweepBrush,
                    shape = CircleShape
                )
                .padding(borderWidth),
            contentAlignment = Alignment.Center
        ) {}

        // Layer 3: Static Inner White Separator Ring & Actual Avatar Inner Content
        Box(
            modifier = Modifier
                .size(avatarSize + 3.dp)
                .background(Color.White, CircleShape)
                .padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(avatarSize)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        }

        // Layer 4: Static Crown / Top Emoji for VIP
        if (vipLevel != VipLevel.NONE && vipLevel.crownEmoji.isNotEmpty()) {
            Text(
                text = vipLevel.crownEmoji,
                fontSize = (avatarSize.value * 0.32f).coerceIn(12f, 22f).sp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-avatarSize.value * 0.20f - 4f).dp)
            )
        }

        // Layer 5: Static Bottom VIP Badge Pill Label
        if (vipLevel != VipLevel.NONE) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = vipLevel.badgeBgColor,
                shadowElevation = 3.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (avatarSize.value * 0.12f + 4f).dp)
            ) {
                Text(
                    text = vipLevel.badgeLabel,
                    fontSize = (avatarSize.value * 0.16f).coerceIn(9f, 11f).sp,
                    fontWeight = FontWeight.Black,
                    color = vipLevel.badgeTextColor,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                )
            }
        }
    }
}

/**
 * Interactive VIP Level Selector Component for Account Tab
 */
@Composable
fun VipLevelSelectorCard(
    currentVipLevel: Int,
    onSelectVipLevel: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Khung Viền VIP Profile",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E1B4B)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("👑", fontSize = 14.sp)
                    }
                    Text(
                        text = "Chọn viền nổi bật trên Bảng Xếp Hạng & Hồ Sơ",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }

                val currentVip = VipLevel.fromLevel(currentVipLevel)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = currentVip.badgeBgColor.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, currentVip.badgeBgColor.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = currentVip.title,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = currentVip.badgeBgColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Scrollable List of All VIP Levels (0..10)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                VipLevel.values().forEach { vip ->
                    val isSelected = currentVipLevel == vip.levelNumber
                    
                    Surface(
                        onClick = { onSelectVipLevel(vip.levelNumber) },
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) Color(0xFFF8FAFC) else Color.White,
                        border = androidx.compose.foundation.BorderStroke(
                            if (isSelected) 2.dp else 1.dp,
                            if (isSelected) vip.badgeBgColor else Color(0xFFE2E8F0)
                        ),
                        shadowElevation = if (isSelected) 2.dp else 0.dp,
                        modifier = Modifier.width(78.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Mini VIP Frame Preview
                            VipAvatarFrame(
                                vipLevel = vip,
                                avatarSize = 28.dp
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(
                                            Brush.linearGradient(
                                                listOf(Color(0xFF0284C7), Color(0xFF38BDF8))
                                            ),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("👤", fontSize = 14.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = vip.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                color = if (isSelected) vip.badgeBgColor else Color(0xFF334155),
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )

                            if (isSelected) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        tint = vip.badgeBgColor,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(1.dp))
                                    Text(
                                        text = "Đang chọn",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = vip.badgeBgColor
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Animated VIP Card Outer Frame Container for Flashcards
 */
@Composable
fun VipCardFrame(
    userVipLevel: Int,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    content: @Composable () -> Unit
) {
    val vipLevel = VipLevel.fromLevel(userVipLevel)
    if (vipLevel == VipLevel.NONE) {
        Box(modifier = modifier) {
            content()
        }
        return
    }

    val infiniteTransition = rememberInfiniteTransition(label = "vip_card_border_anim")

    val borderProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (vipLevel) {
                    VipLevel.VIP1 -> 4000
                    VipLevel.VIP2 -> 3400
                    VipLevel.VIP3 -> 2800
                    VipLevel.VIP4 -> 2200
                    VipLevel.VIP5 -> 1700
                    VipLevel.VIP6 -> 1200
                    VipLevel.VIP7 -> 800
                    VipLevel.NONE -> 10000
                },
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "card_border_progress"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (vipLevel) {
                    VipLevel.VIP1 -> 900
                    VipLevel.VIP2 -> 800
                    VipLevel.VIP3 -> 700
                    VipLevel.VIP4 -> 600
                    VipLevel.VIP5 -> 500
                    VipLevel.VIP6 -> 400
                    VipLevel.VIP7 -> 300
                    else -> 600
                },
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "card_pulseAlpha"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = when (vipLevel) {
            VipLevel.VIP1 -> 1.02f
            VipLevel.VIP2 -> 1.03f
            VipLevel.VIP3 -> 1.04f
            VipLevel.VIP4 -> 1.05f
            VipLevel.VIP5 -> 1.06f
            VipLevel.VIP6 -> 1.08f
            VipLevel.VIP7 -> 1.10f
            else -> 1.0f
        },
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (vipLevel) {
                    VipLevel.VIP1 -> 900
                    VipLevel.VIP2 -> 800
                    VipLevel.VIP3 -> 700
                    VipLevel.VIP4 -> 600
                    VipLevel.VIP5 -> 500
                    VipLevel.VIP6 -> 400
                    VipLevel.VIP7 -> 300
                    else -> 600
                },
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "card_pulseScale"
    )

    val borderWidth = when (vipLevel) {
        VipLevel.NONE -> 0.dp
        VipLevel.VIP1 -> 3.dp
        VipLevel.VIP2 -> 3.2.dp
        VipLevel.VIP3 -> 3.5.dp
        VipLevel.VIP4 -> 4.dp
        VipLevel.VIP5 -> 4.5.dp
        VipLevel.VIP6 -> 5.dp
        VipLevel.VIP7 -> 5.5.dp
    }

    val cardGlowBrush = remember(vipLevel) {
        Brush.radialGradient(
            colors = when (vipLevel) {
                VipLevel.VIP1 -> listOf(Color(0xFFEA580C).copy(alpha = 0.45f), Color(0xFFFDBA74).copy(alpha = 0.15f), Color.Transparent)
                VipLevel.VIP2 -> listOf(Color(0xFF64748B).copy(alpha = 0.50f), Color(0xFFE2E8F0).copy(alpha = 0.20f), Color.Transparent)
                VipLevel.VIP3 -> listOf(Color(0xFF06B6D4).copy(alpha = 0.65f), Color(0xFF38BDF8).copy(alpha = 0.25f), Color.Transparent)
                VipLevel.VIP4 -> listOf(Color(0xFFFBBF24).copy(alpha = 0.70f), Color(0xFFFEF08A).copy(alpha = 0.25f), Color.Transparent)
                VipLevel.VIP5 -> listOf(Color(0xFFEC4899).copy(alpha = 0.75f), Color(0xFF06B6D4).copy(alpha = 0.40f), Color.Transparent)
                VipLevel.VIP6 -> listOf(Color(0xFFEF4444).copy(alpha = 0.80f), Color(0xFFF59E0B).copy(alpha = 0.45f), Color.Transparent)
                VipLevel.VIP7 -> listOf(Color(0xFFA855F7).copy(alpha = 0.85f), Color(0xFFEC4899).copy(alpha = 0.50f), Color.Transparent)
                VipLevel.NONE -> listOf(Color.Transparent, Color.Transparent)
            }
        )
    }

    val trackColors = remember(vipLevel) {
        vipLevel.gradientColors.map { it.copy(alpha = 0.35f) }
    }

    val beamColors = remember(vipLevel) {
        when (vipLevel) {
            VipLevel.VIP1 -> listOf(Color(0xFFEA580C).copy(alpha = 0.15f), Color(0xFFFED7AA), Color.White, Color(0xFFF97316), Color(0xFFEA580C).copy(alpha = 0.15f))
            VipLevel.VIP2 -> listOf(Color(0xFF475569).copy(alpha = 0.15f), Color(0xFFE2E8F0), Color.White, Color(0xFF94A3B8), Color(0xFF475569).copy(alpha = 0.15f))
            VipLevel.VIP3 -> listOf(Color(0xFF0284C7).copy(alpha = 0.15f), Color(0xFF38BDF8), Color.White, Color(0xFF06B6D4), Color(0xFF0284C7).copy(alpha = 0.15f))
            VipLevel.VIP4 -> listOf(Color(0xFFD97706).copy(alpha = 0.15f), Color(0xFFFBBF24), Color.White, Color(0xFFF59E0B), Color(0xFFD97706).copy(alpha = 0.15f))
            VipLevel.VIP5 -> listOf(Color(0xFFC084FC).copy(alpha = 0.15f), Color(0xFFEC4899), Color.White, Color(0xFF38BDF8), Color(0xFFC084FC).copy(alpha = 0.15f))
            VipLevel.VIP6 -> listOf(Color(0xFFEF4444).copy(alpha = 0.15f), Color(0xFFF97316), Color.White, Color(0xFFDC2626), Color(0xFFEF4444).copy(alpha = 0.15f))
            VipLevel.VIP7 -> listOf(Color(0xFFA855F7).copy(alpha = 0.15f), Color(0xFFF43F5E), Color.White, Color(0xFF38BDF8), Color(0xFFA855F7).copy(alpha = 0.15f))
            else -> listOf(Color.White, Color.White)
        }
    }

    val beamCount = remember(vipLevel) {
        when (vipLevel) {
            VipLevel.VIP1 -> 1
            VipLevel.VIP2 -> 1
            VipLevel.VIP3 -> 2
            VipLevel.VIP4 -> 2
            VipLevel.VIP5 -> 3
            VipLevel.VIP6 -> 3
            VipLevel.VIP7 -> 4
            else -> 1
        }
    }

    val beamLengthRatio = remember(vipLevel) {
        when (vipLevel) {
            VipLevel.VIP1 -> 0.32f
            VipLevel.VIP2 -> 0.30f
            VipLevel.VIP3 -> 0.28f
            VipLevel.VIP4 -> 0.25f
            VipLevel.VIP5 -> 0.22f
            VipLevel.VIP6 -> 0.20f
            VipLevel.VIP7 -> 0.18f
            else -> 0.25f
        }
    }

    // Reuse path objects across draws to eliminate Garbage Collection allocations in drawBehind
    val cardPath = remember { Path() }
    val pathMeasure = remember { PathMeasure() }
    val beamSegmentPath = remember { Path() }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Layer 1: Radial Glow Background Aura using GPU graphicsLayer
        Box(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer {
                    this.scaleX = pulseScale
                    this.scaleY = pulseScale
                    this.alpha = pulseAlpha
                }
                .background(
                    brush = cardGlowBrush,
                    shape = RoundedCornerShape(cornerRadius + 4.dp)
                )
        )

        // Layer 2: Light Beam Running Along Card Rectangular Border Path (Optimized Zero-Allocation Canvas)
        Box(
            modifier = Modifier
                .matchParentSize()
                .drawBehind {
                    val strokeWidthPx = borderWidth.toPx()
                    val radiusPx = cornerRadius.toPx()
                    val halfStroke = strokeWidthPx / 2f

                    cardPath.reset()
                    cardPath.addRoundRect(
                        RoundRect(
                            rect = Rect(
                                left = halfStroke,
                                top = halfStroke,
                                right = size.width - halfStroke,
                                bottom = size.height - halfStroke
                            ),
                            cornerRadius = CornerRadius(radiusPx, radiusPx)
                        )
                    )

                    // 1. Draw static background border track
                    drawPath(
                        path = cardPath,
                        brush = Brush.linearGradient(colors = trackColors),
                        style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                    )

                    // 2. Measure perimeter path and run light beam along the border
                    pathMeasure.setPath(cardPath, false)
                    val totalLength = pathMeasure.length

                    if (totalLength > 0f) {
                        val beamLength = totalLength * beamLengthRatio
                        val beamStrokeWidth = strokeWidthPx + 2.dp.toPx()
                        val sparkRadius = strokeWidthPx * 2.8f

                        for (i in 0 until beamCount) {
                            val offsetFraction = i.toFloat() / beamCount
                            val startDist = (borderProgress * totalLength + offsetFraction * totalLength) % totalLength
                            val endDist = startDist + beamLength

                            beamSegmentPath.reset()
                            if (endDist <= totalLength) {
                                pathMeasure.getSegment(startDist, endDist, beamSegmentPath, true)
                            } else {
                                pathMeasure.getSegment(startDist, totalLength, beamSegmentPath, true)
                                pathMeasure.getSegment(0f, endDist - totalLength, beamSegmentPath, true)
                            }

                            // Draw traveling light beam along the border path
                            drawPath(
                                path = beamSegmentPath,
                                brush = Brush.linearGradient(
                                    colors = beamColors,
                                    start = Offset.Zero,
                                    end = Offset(size.width, size.height)
                                ),
                                style = Stroke(width = beamStrokeWidth, cap = StrokeCap.Round)
                            )

                            // Draw glowing spark head at the front of the beam running along the border
                            val headDist = endDist % totalLength
                            val headPos = pathMeasure.getPosition(headDist)
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color.White, beamColors.getOrElse(2) { Color.White }, Color.Transparent),
                                    center = headPos,
                                    radius = sparkRadius
                                ),
                                center = headPos,
                                radius = sparkRadius
                            )
                        }
                    }
                }
        )

        // Layer 3: Card Content clipped to corner radius
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(borderWidth / 2)
                .clip(RoundedCornerShape(cornerRadius)),
            contentAlignment = Alignment.Center
        ) {
            content()
        }

        // Layer 4: Top-right Floating VIP Tag
        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-10).dp, y = (-10).dp),
            shape = RoundedCornerShape(12.dp),
            color = vipLevel.badgeBgColor,
            shadowElevation = 6.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.9f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = vipLevel.crownEmoji,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = vipLevel.title,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        }
    }
}

