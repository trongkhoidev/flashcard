package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.StudyScheduleEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO xử lý toàn bộ truy vấn & thao tác dữ liệu cho Cấu hình Lịch học & Nhắc nhở thông minh
 */
@Dao
interface StudyScheduleDao {

    @Query("SELECT * FROM study_schedules WHERE id = 1 LIMIT 1")
    fun getSchedule(): Flow<StudyScheduleEntity?>

    @Query("SELECT * FROM study_schedules WHERE id = 1 LIMIT 1")
    suspend fun getScheduleDirect(): StudyScheduleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSchedule(schedule: StudyScheduleEntity)

    @Update
    suspend fun updateSchedule(schedule: StudyScheduleEntity)

    @Query("UPDATE study_schedules SET isEnabled = :enabled, updatedTimestamp = :timestamp WHERE id = 1")
    suspend fun setReminderEnabled(enabled: Boolean, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE study_schedules SET reminderHour = :hour, reminderMinute = :minute, updatedTimestamp = :timestamp WHERE id = 1")
    suspend fun updateReminderTime(hour: Int, minute: Int, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE study_schedules SET targetLanguageCode = :langCode, updatedTimestamp = :timestamp WHERE id = 1")
    suspend fun updateTargetLanguage(langCode: String, timestamp: Long = System.currentTimeMillis())
}
