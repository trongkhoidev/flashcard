package com.example.ui.home

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar

/**
 * Trạng thái học tập của từng ngày trong tuần
 */
enum class DayStudyStatus {
    COMPLETED, // Đã học xong (Tích xanh)
    TODAY_COMPLETED, // Hôm nay đã học xong (Ngôi sao vàng / Lửa cam sáng)
    TODAY_PENDING, // Hôm nay chưa học (Viền phát sáng nhấp nháy nhắc học)
    UPCOMING // Ngày sắp tới trong tuần (Vòng tròn mờ)
}

/**
 * Dữ liệu ngày trong tuần
 */
data class DayOfWeekInfo(
    val shortLabel: String, // "T2", "T3", ..., "CN"
    val fullLabel: String,  // "Thứ Hai", ..., "Chủ Nhật"
    val dayOfWeekCalendar: Int, // Calendar.MONDAY...
    val isToday: Boolean,
    val status: DayStudyStatus
)

/**
 * Tiện ích hỗ trợ tính toán thời gian và ngày trong tuần
 */
object StreakTimeHelper {

    /**
     * Lấy chỉ số ngày hôm nay trong tuần (0: Thứ 2 -> 6: Chủ Nhật)
     */
    fun getTodayIndex(): Int {
        val calendar = Calendar.getInstance()
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            Calendar.SUNDAY -> 6
            else -> 0
        }
    }

    /**
     * Tên đầy đủ của ngày hôm nay (VD: "Thứ Hai", "Thứ Ba", "Chủ Nhật")
     */
    fun getTodayFullName(): String {
        val calendar = Calendar.getInstance()
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "Thứ Hai"
            Calendar.TUESDAY -> "Thứ Ba"
            Calendar.WEDNESDAY -> "Thứ Tư"
            Calendar.THURSDAY -> "Thứ Năm"
            Calendar.FRIDAY -> "Thứ Sáu"
            Calendar.SATURDAY -> "Thứ Bảy"
            Calendar.SUNDAY -> "Chủ Nhật"
            else -> "Hôm nay"
        }
    }

    /**
     * Khởi tạo danh sách 7 ngày trong tuần cùng trạng thái dựa vào số ngày streak
     */
    fun getWeeklyDaysInfo(streakDays: Int, isTodayStudied: Boolean = true): List<DayOfWeekInfo> {
        val todayIndex = getTodayIndex()
        val labels = listOf(
            Pair("T2", "Thứ Hai"),
            Pair("T3", "Thứ Ba"),
            Pair("T4", "Thứ Tư"),
            Pair("T5", "Thứ Năm"),
            Pair("T6", "Thứ Sáu"),
            Pair("T7", "Thứ Bảy"),
            Pair("CN", "Chủ Nhật")
        )

        return labels.mapIndexed { index, (shortName, fullName) ->
            val isToday = index == todayIndex
            val status = when {
                index < todayIndex -> {
                    // Ngày đã qua trong tuần
                    if (streakDays > (todayIndex - index)) DayStudyStatus.COMPLETED else DayStudyStatus.UPCOMING
                }
                index == todayIndex -> {
                    if (isTodayStudied || streakDays > 0) DayStudyStatus.TODAY_COMPLETED else DayStudyStatus.TODAY_PENDING
                }
                else -> DayStudyStatus.UPCOMING
            }

            DayOfWeekInfo(
                shortLabel = shortName,
                fullLabel = fullName,
                dayOfWeekCalendar = when (index) {
                    0 -> Calendar.MONDAY
                    1 -> Calendar.TUESDAY
                    2 -> Calendar.WEDNESDAY
                    3 -> Calendar.THURSDAY
                    4 -> Calendar.FRIDAY
                    5 -> Calendar.SATURDAY
                    else -> Calendar.SUNDAY
                },
                isToday = isToday,
                status = status
            )
        }
    }
}

/**
 * Component hiển thị thanh 7 ngày trong tuần trực quan & động
 */
@Composable
fun WeeklyStreakTrackerBar(
    streakDays: Int = 7,
    isTodayStudied: Boolean = true,
    modifier: Modifier = Modifier
) {
    val daysInfo = remember(streakDays, isTodayStudied) {
        StreakTimeHelper.getWeeklyDaysInfo(streakDays, isTodayStudied)
    }

    // Hiệu ứng nhấp nháy nhẹ cho ngày hôm nay
    val infiniteTransition = rememberInfiniteTransition(label = "today_pulse")
    val pulseScale = infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        daysInfo.forEach { dayInfo ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.testTag("streak_day_${dayInfo.shortLabel}")
            ) {
                // Nhãn thứ (T2, T3... CN)
                Text(
                    text = dayInfo.shortLabel,
                    fontSize = 11.sp,
                    fontWeight = if (dayInfo.isToday) FontWeight.ExtraBold else FontWeight.Bold,
                    color = if (dayInfo.isToday) Color.White else Color.White.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(5.dp))

                // Icon / Circle trạng thái của ngày
                when (dayInfo.status) {
                    DayStudyStatus.COMPLETED -> {
                        // Ngày đã hoàn thành trong tuần: Nền trắng với dấu tích xanh dương
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .background(Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Đã hoàn thành",
                                tint = Color(0xFF0284C7),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    DayStudyStatus.TODAY_COMPLETED -> {
                        // Hôm nay đã học: Nổi bật với ngôi sao vàng / ngọn lửa cam và hiệu ứng pulse
                        Box(
                            modifier = Modifier
                                .graphicsLayer {
                                    scaleX = pulseScale.value
                                    scaleY = pulseScale.value
                                }
                                .size(22.dp)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFFFEF08A), Color(0xFFFDE047))
                                    ),
                                    CircleShape
                                )
                                .border(1.5.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Hôm nay đã hoàn thành",
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    DayStudyStatus.TODAY_PENDING -> {
                        // Hôm nay chưa học: Vòng tròn viền vàng sáng nhấp nháy nhắc học
                        Box(
                            modifier = Modifier
                                .graphicsLayer {
                                    scaleX = pulseScale.value
                                    scaleY = pulseScale.value
                                }
                                .size(22.dp)
                                .background(Color.White.copy(alpha = 0.25f), CircleShape)
                                .border(2.dp, Color(0xFFFDE047), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = "Cần học hôm nay",
                                tint = Color(0xFFFDE047),
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }

                    DayStudyStatus.UPCOMING -> {
                        // Ngày sắp tới trong tuần: Vòng tròn viền mờ
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .background(Color.White.copy(alpha = 0.12f), CircleShape)
                                .border(1.2.dp, Color.White.copy(alpha = 0.45f), CircleShape)
                        )
                    }
                }

                // Chấm nhỏ hoặc nhãn "Hôm nay" bên dưới nếu là ngày hiện tại
                if (dayInfo.isToday) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .background(Color.White, CircleShape)
                    )
                } else {
                    Spacer(modifier = Modifier.height(7.dp))
                }
            }
        }
    }
}
