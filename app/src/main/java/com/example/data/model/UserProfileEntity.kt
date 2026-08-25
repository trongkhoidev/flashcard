package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val userName: String = "Bạn Học",
    val avatarEmoji: String = "🦉",
    val avatarBgColorHex: String = "#EEF2FF",
    val vipLevel: Int = 1,
    val streakDays: Int = 7,
    val maxStreakDays: Int = 7,
    val totalPoints: Int = 1500,
    val totalCardsLearned: Int = 45,
    val lastActiveTimestamp: Long = System.currentTimeMillis()
)
