package com.example.data.model

import androidx.room.Entity
import androidx.room.Index

/**
 * Entity lưu danh sách từ vựng đã thuộc (Đã hoàn thành / Mastered) riêng biệt theo từng tài khoản User.
 * Đảm bảo tiến trình "Mục tiêu hôm nay" và các từ đã thuộc là độc lập cho từng người dùng,
 * khi đăng xuất hoặc đổi tài khoản khác sẽ không bị dùng chung.
 */
@Entity(
    tableName = "user_mastered_cards",
    primaryKeys = ["userId", "cardId"],
    indices = [
        Index("userId"),
        Index("cardId")
    ]
)
data class UserMasteredCardEntity(
    val userId: Long,
    val cardId: Long,
    val masteredAt: Long = System.currentTimeMillis()
)
