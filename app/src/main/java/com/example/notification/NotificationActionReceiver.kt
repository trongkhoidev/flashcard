package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat

/**
 * Xử lý các action buttons từ Notification hệ thống:
 * 1. ACTION_SNOOZE_TODAY: Người dùng chọn "Để sau" -> Hủy thông báo và lưu trạng thái không nhắc lại trong ngày hôm nay.
 */
class NotificationActionReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_SNOOZE_TODAY = "com.example.notification.ACTION_SNOOZE_TODAY"
        const val ACTION_START_STUDY = "com.example.notification.ACTION_START_STUDY"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            ACTION_SNOOZE_TODAY -> {
                // 1. Tắt thông báo trên thanh trạng thái
                try {
                    val notificationManager = NotificationManagerCompat.from(context)
                    notificationManager.cancel(NotificationHelper.NOTIFICATION_ID_SMART_STUDY)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                // 2. Đánh dấu không nhắc lại hôm nay vào SharedPreferences
                NotificationHelper.setSnoozedToday(context)

                // 3. Thông báo Toast phản hồi người dùng
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(
                        context.applicationContext,
                        "⏰ Đã hoãn nhắc nhở học hôm nay. Chúc bạn một ngày tốt lành!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
