package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity lưu trữ cài đặt Nhắc nhở / Lịch học thông minh
 */
@Entity(tableName = "study_schedules")
data class StudyScheduleEntity(
    @PrimaryKey val id: Int = 1,
    val isEnabled: Boolean = true,
    val reminderHour: Int = 19,
    val reminderMinute: Int = 0,
    val remindStreak: Boolean = true,
    val remindDueWords: Boolean = true,
    val minWordsThreshold: Int = 1,
    val targetLanguageCode: String = "ja",
    val updatedTimestamp: Long = System.currentTimeMillis()
)
