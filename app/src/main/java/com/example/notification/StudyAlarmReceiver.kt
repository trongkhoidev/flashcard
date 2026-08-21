package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.model.StudySchedule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * BroadcastReceiver nhận tín hiệu thức dậy từ Android AlarmManager khi đến giờ hẹn.
 *
 * Lưu ý kiến trúc quan trọng:
 * - StudySchedule là dữ liệu
 * - AlarmManager là bộ hẹn giờ
 * - BroadcastReceiver CHỈ là nơi Android đánh thức app
 * - Toàn bộ logic kiểm tra thông minh được ủy nhiệm cho SmartNotificationEngine (tránh nhồi nhét business logic vào Receiver)
 */
class StudyAlarmReceiver : BroadcastReceiver() {

    private val receiverScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onReceive(context: Context, intent: Intent?) {
        val pendingResult = goAsync()

        receiverScope.launch {
            try {
                val engine = SmartNotificationEngine(context)
                engine.evaluateAndSendSmartNotification(StudySchedule())

                // Lên lịch lại cho ngày tiếp theo
                StudyAlarmScheduler.scheduleStudyAlarm(context, StudySchedule())
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }
    }
}
