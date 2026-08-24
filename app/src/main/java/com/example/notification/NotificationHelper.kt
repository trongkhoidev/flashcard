package com.example.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Quản lý khởi tạo NotificationChannel, hiển thị Custom Notification Layouts
 * (Collapsed & Expanded với Mascot, Streak Bar 7 ngày và Action Buttons)
 */
object NotificationHelper {

    const val CHANNEL_STUDY_REMINDER = "channel_study_reminder"
    const val CHANNEL_ACHIEVEMENTS = "channel_achievements"

    const val NOTIFICATION_ID_SMART_STUDY = 1001
    const val NOTIFICATION_ID_ACHIEVEMENT = 1002

    private const val PREFS_NAME = "ntk_flashcard_prefs"
    private const val KEY_SNOOZED_STUDY_DATE = "key_snoozed_study_date"

    /**
     * Kiểm tra xem người dùng đã chọn "Để sau" trong ngày hôm nay hay chưa.
     */
    fun isSnoozedToday(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val snoozedDate = prefs.getString(KEY_SNOOZED_STUDY_DATE, "") ?: ""
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return snoozedDate == todayStr
    }

    /**
     * Đánh dấu người dùng đã chọn "Để sau" cho ngày hôm nay.
     */
    fun setSnoozedToday(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        prefs.edit().putString(KEY_SNOOZED_STUDY_DATE, todayStr).apply()
    }

    /**
     * Xóa cờ "Để sau" (dùng khi người dùng mở học hoặc reset).
     */
    fun clearSnooze(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_SNOOZED_STUDY_DATE).apply()
    }

    /**
     * Xóa tất cả Notification đang hiển thị trên thanh hệ thống khi người dùng đã vào app
     */
    fun clearAllNotifications(context: Context) {
        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.cancel(NOTIFICATION_ID_SMART_STUDY)
            notificationManager.cancel(NOTIFICATION_ID_ACHIEVEMENT)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Channel 1: Nhắc nhở học tập & Streak (Đặt HIGH để hiện heads-up banner)
            val studyChannel = NotificationChannel(
                CHANNEL_STUDY_REMINDER,
                "Nhắc nhở học tập & Chuỗi ngày",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Thông báo lịch học thông minh và nhắc duy trì streak từ vựng"
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
            }

            // Channel 2: Thành tựu & Cột mốc Streak
            val achievementChannel = NotificationChannel(
                CHANNEL_ACHIEVEMENTS,
                "Thành tựu & Vinh danh Streak",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Thông báo chúc mừng khi đạt mốc Streak và hoàn thành mục tiêu"
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
            }

            notificationManager.createNotificationChannel(studyChannel)
            notificationManager.createNotificationChannel(achievementChannel)
        }
    }

    /**
     * Hiển thị Notification nhắc học thông minh với Custom RemoteViews Layouts:
     * 1. Trạng thái thu gọn (Collapsed): Logo + Header + Title + Text + Mascot
     * 2. Trạng thái mở rộng (Expanded): Logo + Title + Message + Mascot + Streak Bar 7 ngày + 2 Nút bấm lớn
     */
    fun showStudyReminderNotification(
        context: Context,
        title: String,
        message: String,
        dueWordsCount: Int,
        streakDays: Int
    ) {
        createNotificationChannels(context)

        val appName = context.getString(R.string.app_name)
        val currentTime = System.currentTimeMillis()

        // 1. PendingIntent khi bấm vào thân thông báo hoặc bấm nút "Học ngay"
        val studyIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("EXTRA_NAV_TARGET", "HOME_STUDY")
            putExtra("EXTRA_NOTIFICATION_ID", NOTIFICATION_ID_SMART_STUDY)
        }

        val studyPendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_SMART_STUDY,
            studyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 2. PendingIntent khi bấm nút "Để sau"
        val snoozeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = NotificationActionReceiver.ACTION_SNOOZE_TODAY
        }

        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            NOTIFICATION_ID_SMART_STUDY + 1,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Decode mascot bitmap safely for large icon
        val mascotBitmap = try {
            val originalBitmap = android.graphics.BitmapFactory.decodeResource(
                context.resources,
                R.drawable.img_mascot_penguin_1787286222548
            )
            if (originalBitmap != null) {
                android.graphics.Bitmap.createScaledBitmap(originalBitmap, 128, 128, true)
            } else null
        } catch (e: Throwable) {
            null
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_STUDY_REMINDER)
            .setSmallIcon(R.drawable.ic_notification_card)
            .setShowWhen(true)
            .setWhen(currentTime)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$message\n\n🔥 Streak hiện tại: $streakDays ngày  •  📚 Cần ôn: $dueWordsCount từ")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(studyPendingIntent)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(
                android.R.drawable.ic_media_play,
                "▶ Học ngay",
                studyPendingIntent
            )
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "⏰ Để sau",
                snoozePendingIntent
            )

        if (mascotBitmap != null) {
            builder.setLargeIcon(mascotBitmap)
        }

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            if (notificationManager.areNotificationsEnabled()) {
                notificationManager.notify(NOTIFICATION_ID_SMART_STUDY, builder.build())
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    /**
     * Hiển thị Notification chúc mừng thành tựu với đầy đủ:
     * Small Icon, App Name, Time Stamp, Title, Text
     */
    fun showAchievementNotification(
        context: Context,
        title: String,
        message: String
    ) {
        createNotificationChannels(context)

        val appName = context.getString(R.string.app_name)
        val currentTime = System.currentTimeMillis()

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("EXTRA_NAV_TARGET", "ACHIEVEMENTS")
            putExtra("EXTRA_NOTIFICATION_ID", NOTIFICATION_ID_ACHIEVEMENT)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_ACHIEVEMENT,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val mascotBitmap = try {
            val originalBitmap = android.graphics.BitmapFactory.decodeResource(
                context.resources,
                R.drawable.img_mascot_penguin_1787286222548
            )
            if (originalBitmap != null) {
                android.graphics.Bitmap.createScaledBitmap(originalBitmap, 128, 128, true)
            } else null
        } catch (e: Throwable) {
            null
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ACHIEVEMENTS)
            .setSmallIcon(R.drawable.ic_notification_card)
            .setShowWhen(true)
            .setWhen(currentTime)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(
                android.R.drawable.ic_menu_view,
                "🎉 Xem thành tựu",
                pendingIntent
            )

        if (mascotBitmap != null) {
            builder.setLargeIcon(mascotBitmap)
        }

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            if (notificationManager.areNotificationsEnabled()) {
                notificationManager.notify(NOTIFICATION_ID_ACHIEVEMENT, builder.build())
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}
