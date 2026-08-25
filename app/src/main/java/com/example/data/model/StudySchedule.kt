package com.example.data.model

/**
 * Model chứa cấu hình lịch nhắc học thông minh (Smart Study Schedule)
 */
data class StudySchedule(
    val isEnabled: Boolean = true,
    val reminderHour: Int = 19, // 19:00 default
    val reminderMinute: Int = 0,
    val remindStreak: Boolean = true,
    val remindDueWords: Boolean = true,
    val minWordsThreshold: Int = 1 // Số từ tối thiểu cần ôn để nhắc nhở
)
