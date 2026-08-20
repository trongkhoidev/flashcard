package com.example.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.model.DeckEntity
import kotlin.math.cos
import kotlin.math.sin

/**
 * Top Header:
 * "Xin chào, 👋"
 * "Hôm nay học gì nào?"
 * Streak Pill: "🔥 7"
 */
@Composable
fun HomeTopHeader(
    userName: String = "bạn",
    streakDays: Int = 7,
    onStreakClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Xin chào, ",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF6B7280)
                )
                Text(
                    text = "👋",
                    fontSize = 15.sp
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Hôm nay học gì nào?",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1E1B4B),
                letterSpacing = (-0.5).sp
            )
        }

        // Streak Pill Badge
        Surface(
            onClick = onStreakClick,
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 4.dp,
            modifier = Modifier
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(20.dp),
                    spotColor = Color(0x1A000000)
                )
                .testTag("streak_badge")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "🔥",
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "$streakDays",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFEA580C)
                )
            }
        }
    }
}

/**
 * Search Bar:
 * Search icon, "Tìm kiếm bộ thẻ, chủ đề...", Filter/Tune icon
 */
@Composable
fun HomeSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(18.dp),
                spotColor = Color(0x14000000)
            )
            .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(18.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                if (query.isEmpty()) {
                    Text(
                        text = "Tìm kiếm bộ thẻ, chủ đề...",
                        color = Color(0xFF94A3B8),
                        fontSize = 14.sp
                    )
                }
                androidx.compose.foundation.text.BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = Color(0xFF1E293B),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    cursorBrush = androidx.compose.ui.graphics.SolidColor(Color(0xFF0284C7)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_search_input")
                )
            }

            IconButton(
                onClick = onFilterClick,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Tune,
                    contentDescription = "Filter",
                    tint = Color(0xFF0284C7),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Streak Mascot Banner:
 * Ocean Cyan/Blue Gradient banner with 7-day tracker and 3D Squirtle Turtle mascot
 */
@Composable
fun StreakMascotBanner(
    streakDays: Int = 7,
    onBannerClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val infiniteTransition = rememberInfiniteTransition(label = "sparkle_anim")
    val sparkleAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparkle"
    )

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color(0x550284C7)
            )
            .clickable { onBannerClick() }
            .testTag("streak_mascot_banner")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0369A1),
                            Color(0xFF0284C7),
                            Color(0xFF0EA5E9),
                            Color(0xFF38BDF8)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(1000f, 1000f)
                    )
                )
                .padding(horizontal = 18.dp, vertical = 18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Column: Text & Week Tracker
                Column(
                    modifier = Modifier.weight(1.15f)
                ) {
                    Text(
                        text = "Chuỗi ngày học cùng Squirtle 🐢",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$streakDays ngày",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Bơi nhanh tiến bước cùng từ vựng!",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White.copy(alpha = 0.9f)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Days of Week Tracker: T2, T3, T4, T5, T6, T7, CN
                    val days = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        days.forEachIndexed { index, day ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = day,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))

                                when {
                                    index < 5 -> {
                                        // Checked days (T2-T6): White circle with ocean blue checkmark
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .background(Color.White, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color(0xFF0284C7),
                                                modifier = Modifier.size(13.dp)
                                            )
                                        }
                                    }
                                    index == 5 -> {
                                        // T7: Yellow Star badge
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .background(Color.White, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = null,
                                                tint = Color(0xFFF59E0B),
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                    else -> {
                                        // CN: Outline circle (Upcoming/today)
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .border(1.5.dp, Color.White.copy(alpha = 0.7f), CircleShape)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Right Column: Cute 3D Squirtle Mascot with water sparkles
                Box(
                    modifier = Modifier
                        .weight(0.85f)
                        .height(130.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Magical Sparkles in background
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawSparkle(Offset(size.width * 0.15f, size.height * 0.2f), 8f * sparkleAlpha, Color.White)
                        drawSparkle(Offset(size.width * 0.85f, size.height * 0.15f), 10f * sparkleAlpha, Color(0xFFBAE6FD))
                        drawSparkle(Offset(size.width * 0.9f, size.height * 0.75f), 6f * sparkleAlpha, Color.White)
                    }

                    // Squirtle Turtle Mascot Image
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(R.drawable.img_squirtle_mascot_1787155903327)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Squirtle Turtle Mascot",
                        modifier = Modifier
                            .size(122.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

/**
 * Quick Action Grid:
 * 4 cards: "Tạo bộ thẻ", "Ôn tập", "Thống kê", "Đã lưu"
 */
@Composable
fun QuickActionGrid(
    onCreateDeck: () -> Unit,
    onReviewCards: () -> Unit,
    onViewStats: () -> Unit,
    onViewSaved: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        QuickActionItem(
            title = "Tạo bộ thẻ",
            icon = Icons.Outlined.Style,
            iconTint = Color(0xFF0284C7),
            bgColor = Color(0xFFE0F2FE),
            modifier = Modifier.weight(1f),
            onClick = onCreateDeck,
            testTag = "quick_create_deck"
        )
        QuickActionItem(
            title = "Ôn tập",
            icon = Icons.Outlined.History,
            iconTint = Color(0xFF8B5CF6),
            bgColor = Color(0xFFF3E8FF),
            modifier = Modifier.weight(1f),
            onClick = onReviewCards,
            testTag = "quick_review_cards"
        )
        QuickActionItem(
            title = "Thống kê",
            icon = Icons.Outlined.BarChart,
            iconTint = Color(0xFF10B981),
            bgColor = Color(0xFFECFDF5),
            modifier = Modifier.weight(1f),
            onClick = onViewStats,
            testTag = "quick_stats"
        )
        QuickActionItem(
            title = "Đã lưu",
            icon = Icons.Outlined.BookmarkBorder,
            iconTint = Color(0xFFF59E0B),
            bgColor = Color(0xFFFFFBEB),
            modifier = Modifier.weight(1f),
            onClick = onViewSaved,
            testTag = "quick_saved"
        )
    }
}

@Composable
private fun QuickActionItem(
    title: String,
    icon: ImageVector,
    iconTint: Color,
    bgColor: Color,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        modifier = modifier
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(18.dp),
                spotColor = Color(0x0D000000)
            )
            .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(18.dp))
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(bgColor, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E293B),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * "Tiếp tục học" Section:
 * Header + Card with Eiffel Tower thumbnail, "Tiếng Pháp cơ bản", "32/50 thẻ", progress bar, "Học tiếp" button
 */
@Composable
fun ContinueLearningSection(
    title: String = "Tiếng Pháp cơ bản",
    studiedCount: Int = 32,
    totalCount: Int = 50,
    onContinueClick: () -> Unit,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val progress = (studiedCount.toFloat() / totalCount.toFloat()).coerceIn(0f, 1f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tiếp tục học",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E1B4B)
            )
            Row(
                modifier = Modifier.clickable { onViewAllClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Xem tất cả",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF0284C7)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color(0xFF0284C7),
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Main Card
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 3.dp,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(20.dp),
                    spotColor = Color(0x14000000)
                )
                .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(20.dp))
                .clickable { onContinueClick() }
                .testTag("continue_learning_card")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Thumbnail image (Eiffel Tower)
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFE0F2FE)),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(R.drawable.french_eiffel)
                            .crossfade(true)
                            .build(),
                        contentDescription = "French Eiffel",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Middle Details: Title, count, progress bar
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$studiedCount/$totalCount thẻ",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Linear Progress Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .background(Color(0xFFE0F2FE), RoundedCornerShape(3.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress)
                                .height(6.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF0284C7), Color(0xFF38BDF8))
                                    ),
                                    RoundedCornerShape(3.dp)
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Action Pill Button "Học tiếp"
                Button(
                    onClick = onContinueClick,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0284C7)
                    ),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    ),
                    modifier = Modifier
                        .height(38.dp)
                        .testTag("btn_continue_study")
                ) {
                    Text(
                        text = "Học tiếp",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

/**
 * "Bộ thẻ của bạn" Section:
 * Header + Horizontal scrolling cards:
 * 1. Tiếng Nhật N5 (Mount Fuji art, Peach background)
 * 2. English Basics (US Flag art, Blue background)
 * 3. 한국어 초급 (South Korea flag art, Mint background)
 * 4. Tiếng Việt (Globe art, Lavender background)
 */
@Composable
fun YourDecksSection(
    onDeckClick: (String) -> Unit,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Bộ thẻ của bạn",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E1B4B)
            )
            Row(
                modifier = Modifier.clickable { onViewAllClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Xem tất cả",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF0284C7)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color(0xFF0284C7),
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Horizontal Row of 4 Decks
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card 1: Tiếng Nhật N5
            DeckThemeCard(
                title = "Tiếng Nhật N5",
                cardCountText = "128 thẻ",
                bgColor = Color(0xFFFFF1F2),
                illustration = { JapaneseFujiArt() },
                onClick = { onDeckClick("ja") },
                testTag = "deck_card_ja"
            )

            // Card 2: English Basics
            DeckThemeCard(
                title = "English Basics",
                cardCountText = "95 thẻ",
                bgColor = Color(0xFFEEF2FF),
                illustration = { UsFlagArt() },
                onClick = { onDeckClick("en") },
                testTag = "deck_card_en"
            )

            // Card 3: 한국어 초급
            DeckThemeCard(
                title = "한국어 초급",
                cardCountText = "76 thẻ",
                bgColor = Color(0xFFECFDF5),
                illustration = { KoreaFlagArt() },
                onClick = { onDeckClick("ko") },
                testTag = "deck_card_ko"
            )

            // Card 4: Tiếng Việt
            DeckThemeCard(
                title = "Tiếng Việt",
                cardCountText = "54 thẻ",
                bgColor = Color(0xFFF5F3FF),
                illustration = { GlobeArt() },
                onClick = { onDeckClick("vi") },
                testTag = "deck_card_vi"
            )
        }
    }
}

@Composable
private fun DeckThemeCard(
    title: String,
    cardCountText: String,
    bgColor: Color,
    illustration: @Composable () -> Unit,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(22.dp),
        color = bgColor,
        shadowElevation = 2.dp,
        modifier = modifier
            .width(135.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(22.dp),
                spotColor = Color(0x10000000)
            )
            .border(1.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(22.dp))
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Top row with options icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    imageVector = Icons.Default.MoreHoriz,
                    contentDescription = "Options",
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Center Illustration
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp),
                contentAlignment = Alignment.Center
            ) {
                illustration()
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title & Count
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = cardCountText,
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}

/**
 * "Mục tiêu hôm nay" Card:
 * Circular 75% progress ring, "15 / 20 thẻ", cheer text, chevron
 */
@Composable
fun DailyGoalCard(
    currentCount: Int = 15,
    targetCount: Int = 20,
    percentage: Int = 75,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 3.dp,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color(0x10000000)
            )
            .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(20.dp))
            .testTag("daily_goal_card")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circular Progress Ring with Percentage
            Box(
                modifier = Modifier.size(62.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 6.dp.toPx()
                    val radius = (size.minDimension - strokeWidth) / 2
                    val center = Offset(size.width / 2, size.height / 2)

                    // Background Track
                    drawCircle(
                        color = Color(0xFFE0F2FE),
                        radius = radius,
                        center = center,
                        style = Stroke(width = strokeWidth)
                    )

                    // Foreground Progress Arc
                    val sweepAngle = 360f * (percentage / 100f)
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(Color(0xFF0284C7), Color(0xFF0EA5E9), Color(0xFF38BDF8))
                        ),
                        startAngle = -90f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                Text(
                    text = "$percentage%",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text column
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Mục tiêu hôm nay",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$currentCount",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0284C7)
                    )
                    Text(
                        text = " / $targetCount thẻ",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Cố lên! Bạn làm rất tốt! ",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = "💪",
                        fontSize = 12.sp
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Details",
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Bottom Navigation Bar:
 * 4 Tabs: Trang chủ, Khám phá, BXH, Tài khoản
 */
@Composable
fun HomeBottomNavBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.White,
        shadowElevation = 16.dp,
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                spotColor = Color(0x1F000000)
            )
            .border(1.dp, Color(0xFFF1F5F9))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavBarItem(
                title = "Trang chủ",
                icon = Icons.Outlined.Home,
                isSelected = selectedTab == 0,
                onClick = { onTabSelected(0) },
                testTag = "tab_home"
            )
            NavBarItem(
                title = "Khám phá",
                icon = Icons.Outlined.Explore,
                isSelected = selectedTab == 1,
                onClick = { onTabSelected(1) },
                testTag = "tab_explore"
            )
            NavBarItem(
                title = "BXH",
                icon = Icons.Outlined.EmojiEvents,
                isSelected = selectedTab == 2,
                onClick = { onTabSelected(2) },
                testTag = "tab_leaderboard"
            )
            NavBarItem(
                title = "Tài khoản",
                icon = Icons.Outlined.Person,
                isSelected = selectedTab == 3,
                onClick = { onTabSelected(3) },
                testTag = "tab_profile"
            )
        }
    }
}

@Composable
private fun NavBarItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isSelected) Color(0xFF0284C7) else Color(0xFF94A3B8),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color(0xFF0284C7) else Color(0xFF94A3B8)
        )
    }
}

// ----------------------------------------------------
// CUSTOM ILLUSTRATIONS FOR DECK CARDS (CRISP CANVASES)
// ----------------------------------------------------

@Composable
fun JapaneseFujiArt() {
    val context = LocalContext.current
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(R.drawable.japan_fuji)
            .crossfade(true)
            .build(),
        contentDescription = "Japan Fuji",
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun UsFlagArt() {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(Color.White)
            .shadow(2.dp, CircleShape)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stripeHeight = size.height / 7
            val redColor = Color(0xFFDC2626)
            val blueColor = Color(0xFF1E3A8A)

            // Red & White Stripes
            for (i in 0..6) {
                drawRect(
                    color = if (i % 2 == 0) redColor else Color.White,
                    topLeft = Offset(0f, i * stripeHeight),
                    size = Size(size.width, stripeHeight)
                )
            }

            // Blue Canton on top-left
            drawRect(
                color = blueColor,
                topLeft = Offset(0f, 0f),
                size = Size(size.width * 0.48f, stripeHeight * 4)
            )

            // Little white stars
            val starColor = Color.White
            drawCircle(starColor, radius = 2f, center = Offset(size.width * 0.15f, stripeHeight * 1f))
            drawCircle(starColor, radius = 2f, center = Offset(size.width * 0.32f, stripeHeight * 1f))
            drawCircle(starColor, radius = 2f, center = Offset(size.width * 0.23f, stripeHeight * 2f))
            drawCircle(starColor, radius = 2f, center = Offset(size.width * 0.15f, stripeHeight * 3f))
            drawCircle(starColor, radius = 2f, center = Offset(size.width * 0.32f, stripeHeight * 3f))
        }
    }
}

@Composable
fun KoreaFlagArt() {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(Color.White)
            .shadow(2.dp, CircleShape)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.width * 0.32f

            // Red upper semi
            drawArc(
                color = Color(0xFFDC2626),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2)
            )
            // Blue lower semi
            drawArc(
                color = Color(0xFF2563EB),
                startAngle = 0f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2)
            )
            // S curves of Taegeuk
            drawCircle(
                color = Color(0xFFDC2626),
                radius = radius / 2,
                center = Offset(center.x - radius / 2, center.y)
            )
            drawCircle(
                color = Color(0xFF2563EB),
                radius = radius / 2,
                center = Offset(center.x + radius / 2, center.y)
            )

            // Trigrams (4 black bar groups)
            val barColor = Color(0xFF1E293B)
            val trigramDist = radius * 1.35f
            drawTrigram(center + Offset(-trigramDist * 0.7f, -trigramDist * 0.7f), barColor)
            drawTrigram(center + Offset(trigramDist * 0.7f, -trigramDist * 0.7f), barColor)
            drawTrigram(center + Offset(-trigramDist * 0.7f, trigramDist * 0.7f), barColor)
            drawTrigram(center + Offset(trigramDist * 0.7f, trigramDist * 0.7f), barColor)
        }
    }
}

private fun DrawScope.drawTrigram(pos: Offset, color: Color) {
    for (i in -1..1) {
        drawLine(
            color = color,
            start = Offset(pos.x - 5f, pos.y + i * 3.5f),
            end = Offset(pos.x + 5f, pos.y + i * 3.5f),
            strokeWidth = 2f
        )
    }
}

@Composable
fun GlobeArt() {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(Color(0xFF60A5FA))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val landColor = Color(0xFF34D399)

            // Continents
            drawCircle(landColor, radius = size.width * 0.22f, center = Offset(size.width * 0.35f, size.height * 0.4f))
            drawCircle(landColor, radius = size.width * 0.26f, center = Offset(size.width * 0.7f, size.height * 0.65f))
            drawCircle(landColor, radius = size.width * 0.15f, center = Offset(size.width * 0.25f, size.height * 0.75f))
            drawCircle(landColor, radius = size.width * 0.18f, center = Offset(size.width * 0.65f, size.height * 0.25f))

            // Soft highlight
            drawCircle(
                color = Color.White.copy(alpha = 0.2f),
                radius = size.width * 0.45f,
                center = Offset(size.width * 0.3f, size.height * 0.3f)
            )
        }
    }
}

private fun DrawScope.drawSparkle(center: Offset, size: Float, color: Color) {
    val path = Path().apply {
        moveTo(center.x, center.y - size)
        quadraticTo(center.x, center.y, center.x + size, center.y)
        quadraticTo(center.x, center.y, center.x, center.y + size)
        quadraticTo(center.x, center.y, center.x - size, center.y)
        quadraticTo(center.x, center.y, center.x, center.y - size)
        close()
    }
    drawPath(path, color)
}
