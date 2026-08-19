package com.example.ui.detail

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.DeckEntity
import com.example.data.model.FlashCardEntity

data class DeckTopic(
    val id: Int,
    val title: String,
    val cardCount: Int,
    val progressPercent: Int,
    val description: String = ""
)

data class DeckReview(
    val authorName: String,
    val avatarBgColor: Color,
    val avatarEmoji: String,
    val rating: Int,
    val content: String,
    val timeAgo: String
)

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DeckDetailScreen(
    deck: DeckEntity,
    cards: List<FlashCardEntity>,
    onBack: () -> Unit,
    onStartStudy: () -> Unit,
    onStartQuiz: () -> Unit,
    onStartMatch: () -> Unit,
    onSpeak: (String, String) -> Unit,
    onToggleStar: (Long, Boolean) -> Unit
) {
    val context = LocalContext.current
    var isFavorite by remember { mutableStateOf(false) }
    var isDownloaded by remember { mutableStateOf(false) }
    var isDownloading by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var expandedTopicId by remember { mutableStateOf<Int?>(null) }
    var showAddReviewDialog by remember { mutableStateOf(false) }

    // Dynamic Topic chapters based on deck
    val topics = remember(deck.id) {
        when {
            deck.languageCode == "ja" || deck.id.contains("ja") || deck.title.contains("Nhật") -> listOf(
                DeckTopic(1, "Chữ Hiragana", 46, 100, "Bảng chữ cái mềm cơ bản nhất của tiếng Nhật"),
                DeckTopic(2, "Chữ Katakana", 46, 100, "Bảng chữ cái cứng dùng cho từ mượn quốc tế"),
                DeckTopic(3, "Từ vựng cơ bản", 128, 75, "Từ vựng đời sống và sinh hoạt JLPT N5"),
                DeckTopic(4, "Từ vựng chủ đề", 180, 40, "Giao thông, trường học, gia đình, công sở"),
                DeckTopic(5, "Ngữ pháp cơ bản", 60, 0, "Cấu trúc ngữ pháp trợ từ và mẫu câu N5 thiết yếu")
            )
            deck.languageCode == "fr" || deck.id.contains("fr") || deck.title.contains("Pháp") -> listOf(
                DeckTopic(1, "Phát âm & Bảng chữ cái", 26, 100, "Quy tắc đọc nguyên âm và nối âm chuẩn Pháp"),
                DeckTopic(2, "Chào hỏi & Xưng hô", 30, 80, "Cách chào lịch sự, xưng hô Vous / Tu"),
                DeckTopic(3, "Đồ ăn & Nhà hàng Paris", 45, 60, "Gọi món bánh ngọt, cà phê, rượu vang"),
                DeckTopic(4, "Du lịch & Hỏi đường", 40, 25, "Bến tàu điện ngầm, địa danh nổi tiếng Paris"),
                DeckTopic(5, "Ngữ pháp A1 cơ bản", 50, 10, "Động từ Être, Avoir và thì hiện tại")
            )
            deck.languageCode == "ko" || deck.id.contains("ko") || deck.title.contains("Hàn") -> listOf(
                DeckTopic(1, "Bảng chữ cái Hangul", 40, 100, "Nguyên âm, phụ âm và ghép vần chuẩn"),
                DeckTopic(2, "Chào hỏi & Kính ngữ", 35, 90, "Mẫu câu Yo và Ta trong đời sống"),
                DeckTopic(3, "Ẩm thực & Món ăn Hàn", 50, 65, "Kimchi, Tteokbokki, thịt nướng K-BBQ"),
                DeckTopic(4, "Giao tiếp mua sắm Myeongdong", 45, 30, "Mặc cả, chọn size, thanh toán thẻ"),
                DeckTopic(5, "Ngữ pháp TOPIK 1", 60, 0, "Trợ từ và chia đuôi câu chuẩn ngữ pháp")
            )
            else -> listOf(
                DeckTopic(1, "Phát âm & Ngữ điệu", 30, 100, "Quy tắc phát âm IPA chuẩn bản xứ"),
                DeckTopic(2, "Giao tiếp hàng ngày", 50, 85, "Hội thoại quen thuộc công sở & bạn bè"),
                DeckTopic(3, "Từ vựng thiết yếu", 95, 60, "Bộ 1000 từ vựng tần suất cao nhất"),
                DeckTopic(4, "Thành ngữ & Cụm từ", 60, 30, "Idioms & Phrasal Verbs thông dụng"),
                DeckTopic(5, "Luyện phản xạ nhanh", 40, 0, "Tập phản xạ câu hỏi phỏng vấn & đàm thoại")
            )
        }
    }

    val reviews = remember {
        mutableStateListOf(
            DeckReview(
                authorName = "Mai Anh",
                avatarBgColor = Color(0xFFF3E8FF),
                avatarEmoji = "👩🏻",
                rating = 5,
                content = "Rất dễ hiểu và hữu ích cho người mới bắt đầu!",
                timeAgo = "2 ngày trước"
            ),
            DeckReview(
                authorName = "Tuấn Nam",
                avatarBgColor = Color(0xFFFEF3C7),
                avatarEmoji = "👨🏻",
                rating = 5,
                content = "Học rất nhanh nhớ, phát âm chuẩn và hình minh họa siêu đẹp!",
                timeAgo = "3 ngày trước"
            ),
            DeckReview(
                authorName = "Linh Đan",
                avatarBgColor = Color(0xFFDCFCE7),
                avatarEmoji = "👧🏻",
                rating = 5,
                content = "Flashcard thiết kế thông minh, chia chủ đề rất khoa học.",
                timeAgo = "5 ngày trước"
            )
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Back Button
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(42.dp)
                        .background(Color.White, CircleShape)
                        .shadow(2.dp, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF1E293B)
                    )
                }

                // Actions: Favorite + More
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            isFavorite = !isFavorite
                            Toast.makeText(
                                context,
                                if (isFavorite) "Đã thêm vào danh sách yêu thích ❤️" else "Đã xóa khỏi yêu thích",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier
                            .size(42.dp)
                            .background(Color.White, CircleShape)
                            .shadow(2.dp, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) Color(0xFFEF4444) else Color(0xFF1E293B)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box {
                        IconButton(
                            onClick = { showMoreMenu = true },
                            modifier = Modifier
                                .size(42.dp)
                                .background(Color.White, CircleShape)
                                .shadow(2.dp, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = "More Options",
                                tint = Color(0xFF1E293B)
                            )
                        }

                        DropdownMenu(
                            expanded = showMoreMenu,
                            onDismissRequest = { showMoreMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Luyện Trắc nghiệm (Quiz)") },
                                onClick = {
                                    showMoreMenu = false
                                    onStartQuiz()
                                },
                                leadingIcon = {
                                    Icon(Icons.Filled.Quiz, contentDescription = null, tint = Color(0xFF0284C7))
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Nối từ nhanh (Match)") },
                                onClick = {
                                    showMoreMenu = false
                                    onStartMatch()
                                },
                                leadingIcon = {
                                    Icon(Icons.Filled.Style, contentDescription = null, tint = Color(0xFF10B981))
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Chia sẻ bộ thẻ") },
                                onClick = {
                                    showMoreMenu = false
                                    val sendIntent: Intent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, "Học từ vựng ${deck.title} cùng NTK FlashCard nhé! 🚀")
                                        type = "text/plain"
                                    }
                                    val shareIntent = Intent.createChooser(sendIntent, "Chia sẻ bộ thẻ")
                                    context.startActivity(shareIntent)
                                },
                                leadingIcon = {
                                    Icon(Icons.Filled.Share, contentDescription = null, tint = Color(0xFF3B82F6))
                                }
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                    OutlinedButton(
                        onClick = {
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "Học từ vựng ${deck.title} cực nhanh và hiệu quả trên ứng dụng NTK FlashCard! 🌟")
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Chia sẻ bộ thẻ")
                            context.startActivity(shareIntent)
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF0284C7)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0F2FE)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "Share",
                            tint = Color(0xFF0284C7),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Chia sẻ bộ thẻ",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0284C7)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // TOP HERO SECTION (Image + Details)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Deck Cover Image (Japanese Pagoda + Fuji / French Eiffel / Custom)
                DeckCoverImageCard(deck = deck)

                // Info Column
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Language Tag Pill (Squirtle Ocean Blue)
                    val langLabel = when (deck.languageCode) {
                        "ja" -> "Tiếng Nhật"
                        "fr" -> "Tiếng Pháp"
                        "ko" -> "Tiếng Hàn"
                        "zh" -> "Tiếng Trung"
                        "vi" -> "Tiếng Việt"
                        else -> "Tiếng Anh"
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE0F2FE))
                            .border(1.dp, Color(0xFFBAE6FD), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = langLabel,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0284C7)
                        )
                    }

                    // Deck Title
                    Text(
                        text = deck.title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0F172A),
                        lineHeight = 26.sp
                    )

                    // Rating & Card count
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Rating Pill
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0284C7))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Rating",
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "4.9",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Text(
                            text = "•  ${if (deck.cardCount > 0) deck.cardCount else cards.size} thẻ",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Learners count with avatar stack
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        // 3 Stacked Avatars
                        Box(modifier = Modifier.width(54.dp).height(24.dp)) {
                            AvatarCircle(emoji = "👩🏻", bgColor = Color(0xFFF3E8FF), offset = 0)
                            AvatarCircle(emoji = "👨🏻", bgColor = Color(0xFFFEF3C7), offset = 14)
                            AvatarCircle(emoji = "👧🏻", bgColor = Color(0xFFDCFCE7), offset = 28)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "1.2k người học",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF475569)
                        )
                    }

                    // Subtitle / Description
                    Text(
                        text = if (deck.subtitle.isNotBlank()) deck.subtitle else "Bộ thẻ từ vựng ${deck.title} trình độ ${deck.level}, phù hợp cho người mới bắt đầu.",
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = Color(0xFF64748B),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Tag Chips
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        val tags = when (deck.languageCode) {
                            "ja" -> listOf("Từ vựng", "N5", "JLPT", "Cơ bản")
                            "fr" -> listOf("Từ vựng", "DELF", "A1", "Cơ bản")
                            "ko" -> listOf("Từ vựng", "TOPIK", "Sơ cấp", "Giao tiếp")
                            else -> listOf("Từ vựng", "Giao tiếp", "Thiết yếu", "Cơ bản")
                        }
                        tags.forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFE0F2FE))
                                    .padding(horizontal = 7.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = tag,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF0284C7)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // TWO ACTION BUTTONS: [ Học ngay ] [ Tải xuống ]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Primary Study Button (Ocean Blue filled)
                Button(
                    onClick = onStartStudy,
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0284C7)
                    ),
                    modifier = Modifier
                        .weight(1.3f)
                        .height(54.dp)
                        .shadow(4.dp, RoundedCornerShape(18.dp), spotColor = Color(0x330284C7))
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayCircle,
                        contentDescription = "Study Now",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Học ngay",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Secondary Download Button
                OutlinedButton(
                    onClick = {
                        if (!isDownloaded) {
                            isDownloading = true
                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                isDownloading = false
                                isDownloaded = true
                                Toast.makeText(context, "Đã tải xuống ngoại tuyến bộ thẻ ${deck.title}! 📥", Toast.LENGTH_SHORT).show()
                            }, 1200)
                        } else {
                            Toast.makeText(context, "Bộ thẻ đã được tải sẵn trên máy!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF0284C7)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                ) {
                    if (isDownloading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color(0xFF0284C7),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = if (isDownloaded) Icons.Filled.DownloadDone else Icons.Filled.FileDownload,
                            contentDescription = "Download",
                            tint = if (isDownloaded) Color(0xFF10B981) else Color(0xFF0284C7),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isDownloaded) "Đã tải" else "Tải xuống",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDownloaded) Color(0xFF10B981) else Color(0xFF0284C7)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3 TABS: [ Nội dung ] [ Thống kê ] [ Nhận xét (24) ]
            val tabTitles = listOf("📖 Nội dung", "📈 Thống kê", "💬 Nhận xét (24)")
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.Transparent,
                contentColor = Color(0xFF0284C7),
                divider = {},
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        height = 3.dp,
                        color = Color(0xFF0284C7)
                    )
                }
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 14.sp,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTabIndex == index) Color(0xFF0284C7) else Color(0xFF64748B)
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // TAB CONTENTS
            when (selectedTabIndex) {
                0 -> {
                    // TAB 1: NỘI DUNG (CHỦ ĐỀ & TOPICS)
                    Text(
                        text = "Chủ đề",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Topic List
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        topics.forEach { topic ->
                            val isExpanded = expandedTopicId == topic.id
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color.White,
                                shadowElevation = 1.dp,
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateContentSize()
                                    .clickable {
                                        expandedTopicId = if (isExpanded) null else topic.id
                                    }
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        // Circle Number (1, 2, 3...)
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .background(Color(0xFFE0F2FE), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${topic.id}",
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF0284C7)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        // Topic Title & Card count
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = topic.title,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1E293B)
                                            )
                                            Text(
                                                text = "${topic.cardCount} thẻ",
                                                fontSize = 12.sp,
                                                color = Color(0xFF64748B)
                                            )
                                        }

                                        // Progress Bar & Percent
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.padding(end = 6.dp)
                                        ) {
                                            Text(
                                                text = "${topic.progressPercent}%",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (topic.progressPercent == 100) Color(0xFF10B981) else Color(0xFF0284C7)
                                            )

                                            LinearProgressIndicator(
                                                progress = { topic.progressPercent / 100f },
                                                modifier = Modifier
                                                    .width(60.dp)
                                                    .height(6.dp)
                                                    .clip(RoundedCornerShape(3.dp)),
                                                color = if (topic.progressPercent == 100) Color(0xFF10B981) else Color(0xFF0284C7),
                                                trackColor = Color(0xFFE2E8F0)
                                            )
                                        }

                                        // Dropdown Chevron
                                        Icon(
                                            imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                            contentDescription = "Expand",
                                            tint = Color(0xFF94A3B8),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    // Expandable details with sample cards
                                    AnimatedVisibility(
                                        visible = isExpanded,
                                        enter = fadeIn() + expandVertically(),
                                        exit = fadeOut() + shrinkVertically()
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 12.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(1.dp)
                                                    .background(Color(0xFFF1F5F9))
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))

                                            Text(
                                                text = topic.description,
                                                fontSize = 13.sp,
                                                color = Color(0xFF475569),
                                                lineHeight = 18.sp
                                            )

                                            Spacer(modifier = Modifier.height(10.dp))

                                            // Sample vocabulary list inside this topic
                                            val sampleCards = cards.take(3)
                                            if (sampleCards.isNotEmpty()) {
                                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                                    sampleCards.forEach { card ->
                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                                                                .padding(horizontal = 10.dp, vertical = 8.dp),
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.SpaceBetween
                                                        ) {
                                                            Column(modifier = Modifier.weight(1f)) {
                                                                Text(
                                                                    text = card.frontWord,
                                                                    fontSize = 14.sp,
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = Color(0xFF1E293B)
                                                                )
                                                                Text(
                                                                    text = card.backMeaning,
                                                                    fontSize = 12.sp,
                                                                    color = Color(0xFF64748B)
                                                                )
                                                            }
                                                            IconButton(
                                                                onClick = { onSpeak(card.frontWord, deck.languageCode) },
                                                                modifier = Modifier.size(32.dp)
                                                            ) {
                                                                Icon(
                                                                    imageVector = Icons.Filled.VolumeUp,
                                                                    contentDescription = "Speak",
                                                                    tint = Color(0xFF0284C7),
                                                                    modifier = Modifier.size(18.dp)
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(10.dp))

                                            Button(
                                                onClick = onStartStudy,
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier.fillMaxWidth().height(44.dp)
                                            ) {
                                                Text("Học chủ đề này (${topic.title})", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // SECTION: CHẾ ĐỘ LUYỆN TẬP (QUIZ & MATCH BUTTON CARDS)
                    Text(
                        text = "Chế độ luyện tập",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // 1. CARD BUTTON: Luyện trắc nghiệm (Quiz)
                        Surface(
                            onClick = onStartQuiz,
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFFF0F9FF),
                            shadowElevation = 2.dp,
                            border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFBAE6FD)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_quiz_mode_card")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                // Icon Box
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(
                                            Brush.linearGradient(
                                                listOf(Color(0xFF38BDF8), Color(0xFF0284C7))
                                            ),
                                            RoundedCornerShape(16.dp)
                                        )
                                        .shadow(3.dp, RoundedCornerShape(16.dp), spotColor = Color(0x400284C7)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Quiz,
                                        contentDescription = "Quiz Icon",
                                        tint = Color.White,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }

                                // Title & Description
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Luyện trắc nghiệm (Quiz)",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = "4 lựa chọn phản xạ • Rèn luyện trí nhớ nhanh",
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B),
                                        lineHeight = 16.sp
                                    )
                                }

                                // Arrow Action Pill
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(Color(0xFFE0F2FE), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Bắt đầu Quiz",
                                        tint = Color(0xFF0284C7),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        // 2. CARD BUTTON: Nối từ Nhanh (Match)
                        Surface(
                            onClick = onStartMatch,
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFFECFEFF),
                            shadowElevation = 2.dp,
                            border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFA5F3FC)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_match_mode_card")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                // Icon Box
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(
                                            Brush.linearGradient(
                                                listOf(Color(0xFF06B6D4), Color(0xFF0284C7))
                                            ),
                                            RoundedCornerShape(16.dp)
                                        )
                                        .shadow(3.dp, RoundedCornerShape(16.dp), spotColor = Color(0x400284C7)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Style,
                                        contentDescription = "Match Icon",
                                        tint = Color.White,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }

                                // Title & Description
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Nối từ Nhanh (Match)",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF083344)
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = "Ghép đôi từ và nghĩa • Tốc độ & tập trung",
                                        fontSize = 12.sp,
                                        color = Color(0xFF0E7490),
                                        lineHeight = 16.sp
                                    )
                                }

                                // Arrow Action Pill
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(Color(0xFFCFFAFE), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Bắt đầu Match",
                                        tint = Color(0xFF0284C7),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // TAB 2: THỐNG KÊ CHI TIẾT (STATS TAB)
                    StatsDetailTab(
                        deck = deck,
                        cards = cards,
                        onStartQuiz = onStartQuiz,
                        onStartMatch = onStartMatch
                    )
                }

                2 -> {
                    // TAB 3: TẤT CẢ NHẬN XÉT (REVIEWS TAB)
                    ReviewsDetailTab(
                        reviews = reviews,
                        onAddReview = { showAddReviewDialog = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showAddReviewDialog) {
        AddReviewDialog(
            onDismiss = { showAddReviewDialog = false },
            onSubmit = { author, rating, text ->
                reviews.add(
                    0,
                    DeckReview(
                        authorName = author,
                        avatarBgColor = Color(0xFFFEE2E2),
                        avatarEmoji = "🧑🏻‍💻",
                        rating = rating,
                        content = text,
                        timeAgo = "Vừa xong"
                    )
                )
                showAddReviewDialog = false
                Toast.makeText(context, "Cảm ơn bạn đã đóng góp nhận xét! ✨", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
private fun DeckCoverImageCard(deck: DeckEntity) {
    val imageRes = when {
        deck.languageCode == "ja" || deck.id.contains("ja") || deck.title.contains("Nhật") ->
            R.drawable.japan_n5_cover_1786989133394
        deck.languageCode == "fr" || deck.id.contains("fr") || deck.title.contains("Pháp") ->
            R.drawable.french_eiffel
        else ->
            R.drawable.japan_fuji
    }

    Surface(
        shape = RoundedCornerShape(22.dp),
        shadowElevation = 4.dp,
        modifier = Modifier
            .width(135.dp)
            .height(175.dp)
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(22.dp))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = deck.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradient overlay for smooth contrast
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.25f))
                        )
                    )
            )
        }
    }
}

@Composable
private fun AvatarCircle(emoji: String, bgColor: Color, offset: Int) {
    Box(
        modifier = Modifier
            .offset(x = offset.dp)
            .size(24.dp)
            .background(bgColor, CircleShape)
            .border(1.5.dp, Color.White, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(text = emoji, fontSize = 12.sp)
    }
}

@Composable
private fun StatsDetailTab(
    deck: DeckEntity,
    cards: List<FlashCardEntity>,
    onStartQuiz: () -> Unit,
    onStartMatch: () -> Unit
) {
    val mastered = cards.count { it.isMastered }
    val total = if (cards.isNotEmpty()) cards.size else 128
    val learning = total - mastered

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = "Tiến độ học tập chi tiết",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )

        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
            shadowElevation = 2.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Tỉ lệ thành thạo", fontSize = 13.sp, color = Color(0xFF64748B))
                        Text(
                            text = "${if (total > 0) (mastered * 100 / total) else 35}%",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF10B981)
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Đã thuộc / Tổng", fontSize = 13.sp, color = Color(0xFF64748B))
                        Text(
                            text = "$mastered / $total từ",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LinearProgressIndicator(
                    progress = { if (total > 0) mastered.toFloat() / total.toFloat() else 0.35f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = Color(0xFF10B981),
                    trackColor = Color(0xFFE2E8F0)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(10.dp).background(Color(0xFF10B981), CircleShape))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Đã thành thạo: $mastered", fontSize = 12.sp, color = Color(0xFF475569))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(10.dp).background(Color(0xFFF59E0B), CircleShape))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Đang học: $learning", fontSize = 12.sp, color = Color(0xFF475569))
                    }
                }
            }
        }

        // Action Exercises
        Text(
            text = "Luyện tập & Ôn thi",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                onClick = onStartQuiz,
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF0F9FF),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBAE6FD)),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Icon(Icons.Filled.Quiz, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Trắc nghiệm", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    Text("4 lựa chọn nhanh", fontSize = 12.sp, color = Color(0xFF0284C7))
                }
            }

            Surface(
                onClick = onStartMatch,
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFECFDF5),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7F3D0)),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Icon(Icons.Filled.Style, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Nối thẻ nhớ", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E1B4B))
                    Text("Ghép từ & nghĩa", fontSize = 12.sp, color = Color(0xFF059669))
                }
            }
        }
    }
}

@Composable
private fun ReviewsDetailTab(
    reviews: List<DeckReview>,
    onAddReview: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tất cả đánh giá (${reviews.size})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            Button(
                onClick = onAddReview,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                shape = RoundedCornerShape(10.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("+ Viết nhận xét", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        reviews.forEach { rev ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 1.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(rev.avatarBgColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = rev.avatarEmoji, fontSize = 20.sp)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = rev.authorName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = rev.timeAgo,
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }

                        Row(modifier = Modifier.padding(vertical = 3.dp)) {
                            repeat(rev.rating) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFF59E0B),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Text(
                            text = rev.content,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = Color(0xFF475569)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AddReviewDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, Int, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var rating by remember { mutableIntStateOf(5) }
    var comment by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Viết nhận xét bộ thẻ ✨",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Star Picker
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    (1..5).forEach { star ->
                        IconButton(onClick = { rating = star }) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "$star star",
                                tint = if (star <= rating) Color(0xFFF59E0B) else Color(0xFFCBD5E1),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tên của bạn") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Cảm nghĩ của bạn về bộ thẻ...") },
                    minLines = 3,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Hủy", color = Color(0xFF64748B))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank() && comment.isNotBlank()) {
                                onSubmit(name, rating, comment)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Gửi nhận xét", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
