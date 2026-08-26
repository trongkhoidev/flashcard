package com.example.data.model

import androidx.room.Entity
import androidx.room.Index

/**
 * Entity liên kết từ vựng đã lưu (⭐) riêng biệt theo từng tài khoản User.
 * Đảm bảo từ điển đã lưu của user nào sẽ thuộc về riêng user đó,
 * khi logout hoặc chuyển tài khoản khác sẽ không bị lẫn lộn dữ liệu.
 */
@Entity(
    tableName = "user_saved_cards",
    primaryKeys = ["userId", "cardId"],
    indices = [
        Index("userId"),
        Index("cardId")
    ]
)
data class UserSavedCardEntity(
    val userId: Long,
    val cardId: Long,
    val createdAt: Long = System.currentTimeMillis()
)
