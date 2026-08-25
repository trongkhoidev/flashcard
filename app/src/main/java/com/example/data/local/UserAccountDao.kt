package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.UserAccountEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO xử lý toàn bộ truy vấn & thao tác dữ liệu cho Quản lý Tài khoản, Đăng nhập & Đăng ký (User Authentication & Accounts)
 */
@Dao
interface UserAccountDao {

    // --- Truy vấn Đăng nhập & Kiểm tra Tài khoản ---
    @Query("SELECT * FROM user_accounts WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserAccountEntity?

    @Query("SELECT * FROM user_accounts WHERE username = :username LIMIT 1")
    fun getUserByUsernameFlow(username: String): Flow<UserAccountEntity?>

    @Query("SELECT * FROM user_accounts WHERE username = :username AND passwordHash = :passwordHash LIMIT 1")
    suspend fun authenticate(username: String, passwordHash: String): UserAccountEntity?

    @Query("SELECT COUNT(*) FROM user_accounts WHERE username = :username")
    suspend fun isUsernameExists(username: String): Int

    @Query("SELECT * FROM user_accounts WHERE isLoggedIn = 1 LIMIT 1")
    fun getActiveLoggedInUser(): Flow<UserAccountEntity?>

    @Query("SELECT * FROM user_accounts WHERE isLoggedIn = 1 LIMIT 1")
    suspend fun getActiveLoggedInUserDirect(): UserAccountEntity?

    @Query("SELECT * FROM user_accounts ORDER BY lastLoginAt DESC")
    fun getAllAccounts(): Flow<List<UserAccountEntity>>

    @Query("SELECT COUNT(*) FROM user_accounts")
    fun getTotalUserCount(): Flow<Int>

    // --- Thao tác Thêm / Đăng ký ---
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun registerUser(user: UserAccountEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(user: UserAccountEntity)

    // --- Cập nhật Trạng thái & Mật khẩu ---
    @Update
    suspend fun updateUser(user: UserAccountEntity)

    @Query("UPDATE user_accounts SET isLoggedIn = 1, lastLoginAt = :loginTime WHERE id = :userId")
    suspend fun setLoggedIn(userId: Long, loginTime: Long = System.currentTimeMillis())

    @Query("UPDATE user_accounts SET isLoggedIn = 0")
    suspend fun logoutAllUsers()

    @Query("UPDATE user_accounts SET passwordHash = :newPasswordHash WHERE username = :username")
    suspend fun updatePassword(username: String, newPasswordHash: String)

    // --- Xóa Tài khoản ---
    @Delete
    suspend fun deleteAccount(user: UserAccountEntity)

    @Query("DELETE FROM user_accounts WHERE username = :username")
    suspend fun deleteAccountByUsername(username: String)
}
