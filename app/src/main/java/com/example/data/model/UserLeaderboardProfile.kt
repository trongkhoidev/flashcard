package com.example.data.model

/**
 * Model chứa thông tin Hồ sơ & Điểm số xếp hạng thực tế (Tuần/Tháng/Tất cả) của tài khoản khác
 */
data class UserLeaderboardProfile(
    val id: Int = 1,
    val userName: String = "Bạn Học",
    val avatarEmoji: String = "🦉",
    val avatarBgColorHex: String = "#EEF2FF",
    val vipLevel: Int = 1,
    val streakDays: Int = 7,
    val maxStreakDays: Int = 7,
    val totalPoints: Int = 1500,
    val weeklyPoints: Int = 1500,
    val monthlyPoints: Int = 1500,
    val totalCardsLearned: Int = 45,
    val lastActiveTimestamp: Long = System.currentTimeMillis()
)
