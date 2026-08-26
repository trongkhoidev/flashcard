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
        val accountDao = database.userAccountDao()
        val languageDao = database.userLanguageDao()

        // 1. Lấy thông tin tài khoản đang đăng nhập (Active Logged In Account)
        val activeUser = accountDao.getActiveLoggedInUserDirect()
        val uid = activeUser?.id ?: 1L
        val profile = profileDao.getUserProfileByIdDirect(uid) ?: profileDao.getUserProfile().firstOrNull()

        val userName = profile?.userName?.takeIf { it.isNotBlank() }
            ?: activeUser?.username?.takeIf { it.isNotBlank() }
            ?: "bạn"
        val streakDays = profile?.streakDays ?: 0

        // 2. Tính mốc 00:00:00 hôm nay
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfToday = calendar.timeInMillis

        // 3. Kiểm tra xem hôm nay user đã hoàn thành phiên học nào chưa?
        val todaySessions = sessionDao.getSessionsSince(startOfToday).firstOrNull() ?: emptyList()
        val hasStudiedToday = todaySessions.isNotEmpty()

        // Nếu hôm nay ĐÃ HỌC RỒI và không phải chế độ test cưỡng bức -> Bỏ qua notification để không làm phiền/spam
        if (hasStudiedToday && !isForcedTest) {
            return
        }

        // 4. Lấy danh sách ngôn ngữ đang học của tài khoản này
        var userLangs = languageDao.getAllLearningLanguagesForUser(uid).firstOrNull() ?: emptyList()
        if (userLangs.isEmpty()) {
            userLangs = languageDao.getAllLearningLanguages().firstOrNull() ?: emptyList()
        }
        val langCodes = userLangs.map { it.languageCode }.toSet()

        // 5. Thống kê số từ thực tế của người dùng đang học (chuẩn xác theo ngôn ngữ đang học)
        val allCardsInDb = cardDao.getAllCards().firstOrNull() ?: emptyList()
        val masteredCardIds = database.userMasteredCardDao().getMasteredCardIdsForUserDirect(uid).toSet()

        val userCards = if (langCodes.isNotEmpty()) {
            allCardsInDb.filter { it.languageCode in langCodes }
        } else {
            allCardsInDb
        }

        val totalWordsCount = userCards.size
        val masteredWordsCount = userCards.count { it.id in masteredCardIds || it.isMastered }
        val dueWordsCount = (totalWordsCount - masteredWordsCount).coerceAtLeast(0)

        // Định dạng giờ hẹn nhắc học của người dùng đã thiết lập khi thực thi step / cài đặt (VD: "19:00", "20:00")
        val formattedScheduleTime = String.format(Locale.getDefault(), "%02d:%02d", schedule.reminderHour, schedule.reminderMinute)

        // 6. Tạo thông điệp ngữ cảnh thông minh (Smart Context-Aware Messaging)
        val title: String
        val message: String

        if (hasStudiedToday && isForcedTest) {
            title = "✨ $userName đã hoàn thành bài học hôm nay!"
            message = "Chào $userName, bạn đã thuộc $masteredWordsCount/$totalWordsCount từ. Hôm nay bạn đã hoàn thành bài học và giữ vững chuỗi $streakDays ngày!"
        } else if (dueWordsCount >= schedule.minWordsThreshold && streakDays > 0) {
            // Case A: Có từ cần ôn VÀ có streak -> Gộp thông báo thông minh
            title = "🔥 $userName ơi, đến giờ học $formattedScheduleTime rồi!"
            message = "Chào $userName, bạn đã thuộc $masteredWordsCount/$totalWordsCount từ. Còn $dueWordsCount từ cần ôn tập. Hãy học 1 bài ngắn để giữ chuỗi $streakDays ngày nhé!"
        } else if (dueWordsCount >= schedule.minWordsThreshold) {
            // Case B: Có từ cần ôn, chưa có streak
            title = "📚 Đến giờ học $formattedScheduleTime rồi!"
            message = "Chào $userName, hệ thống ghi nhận bạn đã thuộc $masteredWordsCount/$totalWordsCount từ vựng (còn $dueWordsCount từ cần ôn). Cùng bắt đầu ngay nào!"
        } else if (streakDays > 0 && schedule.remindStreak) {
            // Case C: Duy trì streak hàng ngày
            title = "⚡ Duy trì chuỗi $streakDays ngày cùng $userName!"
            message = "Chào $userName, bạn đã thuộc $masteredWordsCount/$totalWordsCount từ vựng. Dành 3 phút mở ứng dụng để duy trì streak nhé!"
        } else if (totalWordsCount > 0) {
            // Case D: Nhắc nhở chung với số từ thực tế
            title = "📖 Thời gian học từ vựng ($formattedScheduleTime)"
            message = "Chào $userName, bạn đã thuộc $masteredWordsCount/$totalWordsCount từ vựng trong lộ trình học. Dành một vài phút để khám phá bài học mới hôm nay!"
        } else {
            title = "📖 Thời gian học từ vựng lý tưởng ($formattedScheduleTime)"
            message = "Chào $userName, cùng dành một vài phút khám phá các chủ đề từ vựng mới trong lộ trình học hôm nay!"
        }

        // 7. Gửi Notification hệ thống
        NotificationHelper.showStudyReminderNotification(
            context = context,
            title = title,
            message = message,
            dueWordsCount = dueWordsCount,
            streakDays = streakDays,
            totalWordsCount = totalWordsCount,
            masteredWordsCount = masteredWordsCount
        )

        val timeFormatted = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        // 8. Gửi callback để hiển thị banner in-app xem trước trực quan
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
