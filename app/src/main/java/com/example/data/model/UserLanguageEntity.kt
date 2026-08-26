package com.example.data.model

import androidx.room.Entity
import androidx.room.Index

/**
 * Entity lưu danh sách các ngôn ngữ người dùng đang theo học,
 * tiến trình mục tiêu & cấp độ theo từng ngôn ngữ (Multi-Language Learning Track).
 * Phân biệt riêng biệt theo từng userId để không bị lẫn lộn giữa các tài khoản.
 */
@Entity(
    tableName = "user_languages",
    primaryKeys = ["userId", "languageCode"],
    indices = [Index("userId")]
)
data class UserLanguageEntity(
    val userId: Long = 1L,
    val languageCode: String,
    val displayName: String,
    val flagEmoji: String,
    val isCurrentActive: Boolean = false,
    val dailyGoalCards: Int = 20,
    val masteredCardsCount: Int = 0,
    val totalWordsEnrolled: Int = 50,
    val streakDays: Int = 0,
    val level: String = "Mới bắt đầu", // Mới bắt đầu, Sơ cấp, Trung cấp, Nâng cao
    val enrolledTimestamp: Long = System.currentTimeMillis(),
    val lastStudiedTimestamp: Long = System.currentTimeMillis()
)
