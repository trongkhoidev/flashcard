package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity lưu trữ thông tin Tài khoản Đăng nhập / Đăng ký người dùng
 */
@Entity(
    tableName = "user_accounts",
    indices = [Index(value = ["username"], unique = true)]
)
data class UserAccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val passwordHash: String,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis(),
    val isLoggedIn: Boolean = false
)
