package com.example.notification

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.model.StudySchedule
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class NotificationPreviewEvent(
    val title: String,
    val message: String,
    val isAchievement: Boolean = false,
    val formattedTime: String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
)

/**
 * Controller kiểm tra điều kiện thông minh (Context-Aware / Smart Decision Engine)
 * trước khi quyết định gửi thông báo.
 *
 * Rule:
 * 1. Người dùng đã chọn "Để sau" hôm nay chưa? -> Nếu ĐÃ HOÃN -> KHÔNG gửi
 * 2. Đã học hôm nay chưa? (Kiểm tra session >= startOfDay) -> Nếu ĐÃ HỌC -> KHÔNG gửi (trừ khi forcedTest)
 * 3. Đếm số từ cần ôn (dueWords)
 * 4. Nếu có từ cần ôn: "Đến giờ học rồi! Bạn có X từ cần ôn."
 * 5. Nếu có streak: Kết hợp streak để tạo thông điệp mạnh mẽ và tránh spam
 */
class SmartNotificationEngine(private val context: Context) {

    suspend fun evaluateAndSendSmartNotification(
        schedule: StudySchedule = StudySchedule(),
        isForcedTest: Boolean = false,
        onPreviewGenerated: ((NotificationPreviewEvent) -> Unit)? = null
    ) {
        if (!schedule.isEnabled && !isForcedTest) return

        // 0. Kiểm tra xem hôm nay người dùng đã bấm "Để sau" chưa?
        if (NotificationHelper.isSnoozedToday(context) && !isForcedTest) {
            return
        }

        val database = AppDatabase.getDatabase(context.applicationContext)
        val sessionDao = database.studySessionDao()
        val cardDao = database.flashCardDao()
        val profileDao = database.userProfileDao()

        // 1. Tính mốc 00:00:00 hôm nay
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfToday = calendar.timeInMillis

        // 2. Kiểm tra xem hôm nay user đã hoàn thành phiên học nào chưa?
        val todaySessions = sessionDao.getSessionsSince(startOfToday).firstOrNull() ?: emptyList()
        val hasStudiedToday = todaySessions.isNotEmpty()

        // Nếu hôm nay ĐÃ HỌC RỒI và không phải chế độ test cưỡng bức -> Bỏ qua notification để không làm phiền/spam
        if (hasStudiedToday && !isForcedTest) {
            return
        }

        // 3. Lấy thông tin người dùng & Streak
        val profile = profileDao.getUserProfile().firstOrNull()
        val streakDays = profile?.streakDays ?: 7
        val userName = profile?.userName?.takeIf { it.isNotBlank() } ?: "bạn"

        // 4. Đếm số từ chưa thuộc / cần ôn
        val dueCards = cardDao.getAllCards().firstOrNull()?.filter { !it.isMastered } ?: emptyList()
        val dueWordsCount = if (dueCards.isNotEmpty()) dueCards.size else 15

        // 5. Tạo thông điệp ngữ cảnh thông minh (Smart Context-Aware Messaging)
        val title: String
        val message: String

        if (hasStudiedToday && isForcedTest) {
            title = "✨ Bạn đã hoàn thành bài học hôm nay!"
            message = "Chào $userName, hệ thống ghi nhận bạn đã học $dueWordsCount từ hôm nay và giữ vững chuỗi $streakDays ngày!"
        } else if (dueWordsCount >= schedule.minWordsThreshold && streakDays > 0) {
            // Case A: Có từ cần ôn VÀ có streak -> Gộp thông báo thông minh
            title = "🔥 Giữ chuỗi $streakDays ngày cùng NTK FlashCard!"
            message = "Chào $userName, bạn có $dueWordsCount từ cần ôn hôm nay. Hãy hoàn thành 1 bài học ngắn để giữ vững streak nhé!"
        } else if (dueWordsCount >= schedule.minWordsThreshold) {
            // Case B: Có từ cần ôn, chưa có streak
            title = "📚 Đến giờ học rồi!"
            message = "Chào $userName, bạn có $dueWordsCount từ vựng đang chờ ôn luyện. Cùng bắt đầu ngay nào!"
        } else if (streakDays > 0 && schedule.remindStreak) {
            // Case C: Không có từ tồn đọng, nhưng cần duy trì streak hàng ngày
            title = "⚡ Đừng để mất chuỗi $streakDays ngày!"
            message = "Bạn chưa hoàn thành bài học hôm nay. Dành 3 phút mở thêm bài mới để duy trì streak nhé!"
        } else {
            // Case D: Nhắc nhở chung nếu được cấu hình
            title = "📖 Thời gian học từ vựng lý tưởng"
            message = "Dành một vài phút khám phá các chủ đề từ vựng mới hôm nay cùng NTK FlashCard!"
        }

        // 6. Gửi Notification hệ thống
        NotificationHelper.showStudyReminderNotification(
            context = context,
            title = title,
            message = message,
            dueWordsCount = dueWordsCount,
            streakDays = streakDays
        )

        val timeFormatted = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        // 7. Gửi callback để hiển thị banner in-app xem trước trực quan
        onPreviewGenerated?.invoke(
            NotificationPreviewEvent(
                title = title,
                message = message,
                isAchievement = false,
                formattedTime = timeFormatted
            )
        )
    }

    /**
     * Kích hoạt notification thành tựu ngay lập tức khi đạt mốc streak (VD: 3, 7, 10, 30 ngày)
     */
    fun checkAndNotifyStreakMilestone(
        newStreak: Int,
        onPreviewGenerated: ((NotificationPreviewEvent) -> Unit)? = null
    ) {
        val title = "🎉 Chúc mừng! Cột mốc $newStreak ngày!"
        val message = "Bạn đã đạt chuỗi học tập xuất sắc $newStreak ngày liên tiếp! Tiếp tục duy trì phong độ nhé."
        
        NotificationHelper.showAchievementNotification(context, title, message)

        val timeFormatted = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        onPreviewGenerated?.invoke(
            NotificationPreviewEvent(
                title = title,
                message = message,
                isAchievement = true,
                formattedTime = timeFormatted
            )
        )
    }
}
