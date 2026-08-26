package com.example.ui.leaderboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Fireplace
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

import com.example.ui.components.VipAvatarFrame
import com.example.ui.components.VipLevel

enum class LeaderboardFilterType(val title: String, val iconEmoji: String) {
    POINTS("Tổng điểm", "👑"),
    STREAK("Chuỗi ngày học", "🔥"),
    CARDS("Số thẻ đã học", "🗂️")
}

enum class TimePeriod(val label: String) {
    THIS_WEEK("Tuần này"),
    THIS_MONTH("Tháng này"),
    ALL_TIME("Tất cả")
}

sealed class RankTrend {
    data class Up(val valCount: Int) : RankTrend()
    data class Down(val valCount: Int) : RankTrend()
    object Same : RankTrend()
}

data class LeaderboardUser(
    val rank: Int,
    val name: String,
    val points: Int,
    val streakDays: Int,
    val cardsLearned: Int,
    val trend: RankTrend,
    val avatarBgColor: Color,
    val avatarEmoji: String,
    val vipLevel: Int = 0,
    val isCurrentUser: Boolean = false
)

/** Chỉ số so hạng theo loại filter đang chọn */
internal fun metricOf(user: LeaderboardUser, filterType: LeaderboardFilterType): Int = when (filterType) {
    LeaderboardFilterType.POINTS -> user.points
    LeaderboardFilterType.STREAK -> user.streakDays
    LeaderboardFilterType.CARDS -> user.cardsLearned
}

/**
 * Gộp người chơi THẬT vào bảng cùng đối thủ rồi xếp hạng thật:
 * - Sắp giảm dần theo chỉ số của filter đang chọn
 * - Bằng điểm -> người dùng thật ưu tiên đứng trên
 * - Rank gán lại tuần tự từ 1 (podium & danh sách đều dùng kết quả này)
 */
internal fun computeRankedList(
    mockUsers: List<LeaderboardUser>,
    currentUser: LeaderboardUser,
    filterType: LeaderboardFilterType
): List<LeaderboardUser> {
    return (mockUsers.map { it.copy(isCurrentUser = false) } + currentUser.copy(isCurrentUser = true))
        .sortedWith(
            compareByDescending<LeaderboardUser> { metricOf(it, filterType) }
                .thenBy { if (it.isCurrentUser) 0 else 1 }
        )
        .mapIndexed { index, user -> user.copy(rank = index + 1) }
}

@Composable
fun LeaderboardTab(
    userName: String = "Bạn",
    userVipLevel: Int = 1,
    userScore: Int = 0,
    userWeeklyPoints: Int = 0,
    userMonthlyPoints: Int = 0,
    userStreak: Int = 0,
    userCardsLearned: Int = 0,
    otherUserProfiles: List<com.example.data.model.UserLeaderboardProfile> = emptyList(),
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf(LeaderboardFilterType.POINTS) }
    var selectedTimePeriod by remember { mutableStateOf(TimePeriod.THIS_WEEK) }
    var showTimeMenu by remember { mutableStateOf(false) }
    var selectedUserForDialog by remember { mutableStateOf<LeaderboardUser?>(null) }
    var showAllRewardsDialog by remember { mutableStateOf(false) }

    // Các tài khoản thật khác trên thiết bị này (nếu có)
    val otherUsersList = remember(otherUserProfiles, selectedTimePeriod) {
        otherUserProfiles.map { profile ->
            val pointsForPeriod = when (selectedTimePeriod) {
                TimePeriod.THIS_WEEK -> profile.weeklyPoints
                TimePeriod.THIS_MONTH -> profile.monthlyPoints
                TimePeriod.ALL_TIME -> profile.totalPoints
            }
            val colorHex = try {
                Color(android.graphics.Color.parseColor(profile.avatarBgColorHex))
            } catch (e: Exception) {
                Color(0xFFFED7AA)
            }
            LeaderboardUser(
                rank = 0,
                name = profile.userName,
                points = pointsForPeriod,
                streakDays = profile.streakDays,
                cardsLearned = profile.totalCardsLearned,
                trend = RankTrend.Same,
                avatarBgColor = colorHex,
                avatarEmoji = profile.avatarEmoji,
                vipLevel = profile.vipLevel,
                isCurrentUser = false
            )
        }
    }

    // Đối thủ ảo (app offline không có server). Hạng của bạn vẫn được tính THẬT
    // bằng cách gộp vào danh sách và so theo đúng chỉ số đang chọn.
    val mockUsers = remember(selectedTimePeriod, otherUsersList) {
        val multiplier = when (selectedTimePeriod) {
            TimePeriod.THIS_WEEK -> 1.0f
            TimePeriod.THIS_MONTH -> 3.5f
            TimePeriod.ALL_TIME -> 8.0f
        }

        val baseMock = listOf(
            LeaderboardUser(0, "Minh Anh", (12850 * multiplier).toInt(), (25 * multiplier).toInt().coerceAtMost(365), (320 * multiplier).toInt(), RankTrend.Same, Color(0xFFFDE68A), "👦🏻", vipLevel = 5),
            LeaderboardUser(0, "Bảo Ngọc", (9450 * multiplier).toInt(), (18 * multiplier).toInt().coerceAtMost(365), (240 * multiplier).toInt(), RankTrend.Up(1), Color(0xFFFBCFE8), "👧🏻", vipLevel = 3),
            LeaderboardUser(0, "Hoàng Nam", (7650 * multiplier).toInt(), (15 * multiplier).toInt().coerceAtMost(365), (190 * multiplier).toInt(), RankTrend.Down(1), Color(0xFFFED7AA), "👦🏽", vipLevel = 2),
            LeaderboardUser(0, "Khánh Linh", (6240 * multiplier).toInt(), 14, 160, RankTrend.Up(2), Color(0xFFFDE68A), "👧🏽", vipLevel = 4),
            LeaderboardUser(0, "Gia Huy", (5870 * multiplier).toInt(), 12, 145, RankTrend.Down(1), Color(0xFFBAE6FD), "👦🏼", vipLevel = 1),
            LeaderboardUser(0, "Phương Anh", (4980 * multiplier).toInt(), 10, 130, RankTrend.Same, Color(0xFFE9D5FF), "👧🏿", vipLevel = 6),
            LeaderboardUser(0, "Quang Huy", (4210 * multiplier).toInt(), 9, 115, RankTrend.Up(3), Color(0xFFFED7AA), "👦🏻", vipLevel = 2),
            LeaderboardUser(0, "Thảo Vy", (3860 * multiplier).toInt(), 8, 100, RankTrend.Down(2), Color(0xFFFECDD3), "👧🏻", vipLevel = 1),
            LeaderboardUser(0, "Đức Mạnh", (3450 * multiplier).toInt(), 7, 90, RankTrend.Same, Color(0xFFA7F3D0), "👦🏽", vipLevel = 3),
            LeaderboardUser(0, "Mai Chi", (3120 * multiplier).toInt(), 6, 80, RankTrend.Same, Color(0xFFFDE68A), "👧🏼", vipLevel = 4)
        )
        otherUsersList + baseMock
    }

    // Dữ liệu THẬT của người chơi — đọc trực tiếp từ DB, KHÔNG nhân hệ số giả
    val currentUserItem = remember(
        userName, userVipLevel, userScore, userWeeklyPoints, userMonthlyPoints,
        userStreak, userCardsLearned, selectedTimePeriod
    ) {
        val realPoints = when (selectedTimePeriod) {
            TimePeriod.THIS_WEEK -> userWeeklyPoints
            TimePeriod.THIS_MONTH -> userMonthlyPoints
            TimePeriod.ALL_TIME -> userScore
        }
        LeaderboardUser(
            rank = 0,
            name = userName,
            points = realPoints,
            streakDays = userStreak,
            cardsLearned = userCardsLearned,
            trend = RankTrend.Same,
            avatarBgColor = Color(0xFFDDD6FE),
            avatarEmoji = "🧑🏻‍💻",
            vipLevel = userVipLevel,
            isCurrentUser = true
        )
    }

    // Gộp + xếp hạng thật theo filter đang chọn
    val rankedList = remember(mockUsers, currentUserItem, selectedFilter) {
        computeRankedList(mockUsers, currentUserItem, selectedFilter)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .testTag("leaderboard_screen")
    ) {
        // 1. TOP HEADER: "Bảng xếp hạng 🏆", Subtitle & Dropdown selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Bảng xếp hạng",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1E1B4B)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "🏆", fontSize = 22.sp)
                }
                Text(
                    text = "Cạnh tranh – Học tập – Vươn xa mỗi ngày!",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }

            // Time Period Selector Pill Dropdown
            Box {
                Surface(
                    onClick = { showTimeMenu = true },
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.testTag("time_period_dropdown")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🗓️", fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = selectedTimePeriod.label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4338CA)
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Dropdown",
                            tint = Color(0xFF6366F1),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                DropdownMenu(
                    expanded = showTimeMenu,
                    onDismissRequest = { showTimeMenu = false }
                ) {
                    TimePeriod.values().forEach { period ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = period.label,
                                    fontWeight = if (period == selectedTimePeriod) FontWeight.Bold else FontWeight.Normal,
                                    color = if (period == selectedTimePeriod) Color(0xFF4338CA) else Color(0xFF334155)
                                )
                            },
                            onClick = {
                                selectedTimePeriod = period
                                showTimeMenu = false
                            },
                            leadingIcon = {
                                if (period == selectedTimePeriod) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF4338CA))
                                }
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. SEGMENTED FILTER TABS: "Tổng điểm" | "Chuỗi ngày học" | "Số thẻ đã học"
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFFF1F5F9),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                LeaderboardFilterType.values().forEach { filter ->
                    val isSelected = selectedFilter == filter
                    Surface(
                        onClick = { selectedFilter = filter },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) Color(0xFF6366F1) else Color.Transparent,
                        shadowElevation = if (isSelected) 3.dp else 0.dp,
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = filter.iconEmoji, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = filter.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color(0xFF64748B),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 3. TOP 3 PODIUM SECTION (Rank 2, Rank 1, Rank 3 - Bục nhận giải)
        // Lấy từ danh sách ĐÃ GỘP: bạn hoàn toàn có thể vào bục nếu điểm thật đủ cao!
        val top1 = rankedList.getOrNull(0) ?: currentUserItem
        val top2 = rankedList.getOrNull(1) ?: top1
        val top3 = rankedList.getOrNull(2) ?: top1

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            // Rank 2 (Left - Medium Podium Step)
            PodiumStep(
                user = top2,
                rank = 2,
                podiumBlockHeight = 92.dp,
                podiumGradient = Brush.verticalGradient(
                    colors = listOf(Color(0xFF94A3B8), Color(0xFF475569))
                ),
                podiumBorderColor = Color(0xFFCBD5E1),
                filterType = selectedFilter,
                modifier = Modifier.weight(1f),
                onClick = { selectedUserForDialog = top2 }
            )

            // Rank 1 (Center - Highest Podium Step)
            PodiumStep(
                user = top1,
                rank = 1,
                podiumBlockHeight = 120.dp,
                podiumGradient = Brush.verticalGradient(
                    colors = listOf(Color(0xFFFBBF24), Color(0xFFD97706))
                ),
                podiumBorderColor = Color(0xFFFDE68A),
                filterType = selectedFilter,
                isTop1 = true,
                modifier = Modifier.weight(1.15f),
                onClick = { selectedUserForDialog = top1 }
            )

            // Rank 3 (Right - Lowest Podium Step)
            PodiumStep(
                user = top3,
                rank = 3,
                podiumBlockHeight = 78.dp,
                podiumGradient = Brush.verticalGradient(
                    colors = listOf(Color(0xFFFB923C), Color(0xFFC2410C))
                ),
                podiumBorderColor = Color(0xFFFED7AA),
                filterType = selectedFilter,
                modifier = Modifier.weight(1f),
                onClick = { selectedUserForDialog = top3 }
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 4. LEADERBOARD LIST CONTAINER (Ranks 4 - 10 & Current User)
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 3.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
            ) {
                // Ranks 4 trở đi — bao gồm cả BẠN nếu lọt nhóm này (hàng hiện tại được highlight sẵn)
                val rowsAfterPodium = rankedList.drop(3)
                rowsAfterPodium.forEach { user ->
                    LeaderboardRowItem(
                        user = user,
                        filterType = selectedFilter,
                        onClick = { selectedUserForDialog = user }
                    )
                }

                // Bạn đang ngoài Top hiển thị -> ghim hàng cuối với hạng THẬT
                val me = rankedList.first { it.isCurrentUser }
                if (rowsAfterPodium.none { it.isCurrentUser }) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• • •",
                        fontSize = 12.sp,
                        color = Color(0xFFCBD5E1),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LeaderboardRowItem(
                        user = me,
                        filterType = selectedFilter,
                        isCurrentUser = true,
                        onClick = { selectedUserForDialog = me }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 5. REWARDS SECTION ("Phần thưởng", "Xem tất cả >")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Phần thưởng",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E1B4B)
            )

            Row(
                modifier = Modifier.clickable { showAllRewardsDialog = true },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Xem tất cả",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF6366F1)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Xem tất cả",
                    tint = Color(0xFF6366F1),
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            RewardCard(
                icon = "🏆",
                title = "Top 1",
                points = "500 ⭐",
                rewardText = "7 ngày VIP",
                bgColor = Color(0xFFFFFBEB),
                borderColor = Color(0xFFFDE68A),
                onClick = { showAllRewardsDialog = true }
            )
            RewardCard(
                icon = "🥈",
                title = "Top 2-3",
                points = "300 ⭐",
                rewardText = "3 ngày VIP",
                bgColor = Color(0xFFF8FAFC),
                borderColor = Color(0xFFE2E8F0),
                onClick = { showAllRewardsDialog = true }
            )
            RewardCard(
                icon = "🥉",
                title = "Top 4-10",
                points = "100 ⭐",
                rewardText = "1 ngày VIP",
                bgColor = Color(0xFFFFF7ED),
                borderColor = Color(0xFFFED7AA),
                onClick = { showAllRewardsDialog = true }
            )
            RewardCard(
                icon = "🎁",
                title = "Tham gia",
                points = "50 ⭐",
                rewardText = "Quà động viên",
                bgColor = Color(0xFFFEF2F2),
                borderColor = Color(0xFFFECDD3),
                onClick = { showAllRewardsDialog = true }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    // USER DETAIL DIALOG ON CLICK
    selectedUserForDialog?.let { user ->
        Dialog(
            onDismissRequest = { selectedUserForDialog = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .padding(vertical = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(user.avatarBgColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(user.avatarEmoji, fontSize = 36.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = user.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E1B4B)
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFEEF2FF),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = "Hạng #${user.rank} • ${selectedTimePeriod.label}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4338CA),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("👑 Điểm số", fontSize = 11.sp, color = Color(0xFF64748B))
                            Text("%,d".format(user.points), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF4338CA))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🔥 Chuỗi", fontSize = 11.sp, color = Color(0xFF64748B))
                            Text("${user.streakDays} ngày", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFEA580C))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🗂️ Đã học", fontSize = 11.sp, color = Color(0xFF64748B))
                            Text("${user.cardsLearned} thẻ", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF10B981))
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { selectedUserForDialog = null },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Text("Đóng", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }

    // ALL REWARDS DETAILS DIALOG
    if (showAllRewardsDialog) {
        Dialog(
            onDismissRequest = { showAllRewardsDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .padding(vertical = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🎁 Bảng quà tặng học tập",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E1B4B)
                    )
                    Text(
                        text = "Phần thưởng tự động trao vào cuối tuần",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val rewardList = listOf(
                        Triple("🥇 Top 1 Vô Địch", "500 Điểm thưởng + 7 Ngày VIP", "Cùng huy hiệu vương miện Vàng"),
                        Triple("🥈 Top 2-3 Á Quân", "300 Điểm thưởng + 3 Ngày VIP", "Cùng huy hiệu Bạc danh dự"),
                        Triple("🥉 Top 4-10 Xuất Sắc", "100 Điểm thưởng + 1 Ngày VIP", "Cùng huy hiệu Đồng nỗ lực"),
                        Triple("🎗️ Top 11-50 Cố Gắng", "50 Điểm thưởng động viên", "Dành cho người chăm chỉ")
                    )

                    rewardList.forEach { (title, reward, desc) ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFF8FAFC),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E1B4B))
                                Text(reward, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF4338CA))
                                Text(desc, fontSize = 11.sp, color = Color(0xFF64748B))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showAllRewardsDialog = false },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Text("Hiểu rồi", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun PodiumStep(
    user: LeaderboardUser,
    rank: Int,
    podiumBlockHeight: androidx.compose.ui.unit.Dp,
    podiumGradient: Brush,
    podiumBorderColor: Color,
    filterType: LeaderboardFilterType,
    isTop1: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val displayValue = when (filterType) {
        LeaderboardFilterType.POINTS -> "%,d đ".format(user.points)
        LeaderboardFilterType.STREAK -> "${user.streakDays} ngày"
        LeaderboardFilterType.CARDS -> "${user.cardsLearned} thẻ"
    }

    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        // Crown for Top 1
        if (isTop1) {
            Text(
                text = "👑",
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 2.dp)
            )
        } else {
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Avatar Container with VIP Frame
        VipAvatarFrame(
            vipLevel = VipLevel.fromLevel(user.vipLevel),
            avatarSize = if (isTop1) 62.dp else 50.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(user.avatarBgColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.avatarEmoji,
                    fontSize = if (isTop1) 30.sp else 24.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // User Name
        Text(
            text = user.name,
            fontSize = if (isTop1) 14.sp else 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1B4B),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(6.dp))

        // PODIUM BLOCK (Bục nhận giải chứa Huy chương, Thứ hạng & Điểm số)
        Surface(
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 6.dp, bottomEnd = 6.dp),
            color = Color.Transparent,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, podiumBorderColor),
            shadowElevation = if (isTop1) 6.dp else 3.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(podiumBlockHeight)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(podiumGradient),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 2.dp)
                ) {
                    val medalIcon = when (rank) {
                        1 -> "🥇"
                        2 -> "🥈"
                        else -> "🥉"
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = medalIcon,
                            fontSize = if (isTop1) 18.sp else 14.sp
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "#$rank",
                            fontSize = if (isTop1) 18.sp else 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.25f)
                    ) {
                        Text(
                            text = displayValue,
                            fontSize = if (isTop1) 11.sp else 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardRowItem(
    user: LeaderboardUser,
    filterType: LeaderboardFilterType,
    isCurrentUser: Boolean = false,
    onClick: () -> Unit
) {
    val displayValue = when (filterType) {
        LeaderboardFilterType.POINTS -> "%,d điểm".format(user.points)
        LeaderboardFilterType.STREAK -> "${user.streakDays} ngày"
        LeaderboardFilterType.CARDS -> "${user.cardsLearned} thẻ"
    }

    val rowBg = if (isCurrentUser) Color(0xFFEEF2FF) else Color.Transparent
    val rowBorderColor = if (isCurrentUser) Color(0xFF818CF8) else Color.Transparent

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = rowBg,
        border = if (isCurrentUser) androidx.compose.foundation.BorderStroke(1.2.dp, rowBorderColor) else null,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank Number & Trend Indicator Column
            Column(
                modifier = Modifier.width(36.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${user.rank}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isCurrentUser) Color(0xFF4338CA) else Color(0xFF1E1B4B)
                )

                when (user.trend) {
                    is RankTrend.Up -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("↑", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                            Text("${user.trend.valCount}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                        }
                    }
                    is RankTrend.Down -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("↓", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                            Text("${user.trend.valCount}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                        }
                    }
                    is RankTrend.Same -> {
                        Text("-", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // User Avatar with VIP Border Frame
            VipAvatarFrame(
                vipLevel = VipLevel.fromLevel(user.vipLevel),
                avatarSize = 42.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(user.avatarBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(user.avatarEmoji, fontSize = 22.sp)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // User Name
            Text(
                text = user.name,
                fontSize = 14.sp,
                fontWeight = if (isCurrentUser) FontWeight.ExtraBold else FontWeight.SemiBold,
                color = if (isCurrentUser) Color(0xFF312E81) else Color(0xFF1E293B),
                modifier = Modifier.weight(1f)
            )

            // Star Score Value
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFF6366F1),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = displayValue,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4338CA)
                )
            }
        }
    }
}

@Composable
private fun RewardCard(
    icon: String,
    title: String,
    points: String,
    rewardText: String,
    bgColor: Color,
    borderColor: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        shadowElevation = 1.dp,
        modifier = Modifier.width(135.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = icon, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E1B4B))
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = points, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF6366F1))
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = rewardText, fontSize = 10.sp, color = Color(0xFF64748B), textAlign = TextAlign.Center)
        }
    }
}
