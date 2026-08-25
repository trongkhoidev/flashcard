package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.UserLanguageEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO quản lý toàn bộ truy vấn & cập nhật cho các ngôn ngữ người dùng đang học (Multi-Language Learning)
 */
@Dao
interface UserLanguageDao {

    // --- Truy vấn Danh sách & Ngôn ngữ Đang học ---
    @Query("SELECT * FROM user_languages ORDER BY isCurrentActive DESC, lastStudiedTimestamp DESC")
    fun getAllLearningLanguages(): Flow<List<UserLanguageEntity>>

    @Query("SELECT * FROM user_languages WHERE isCurrentActive = 1 LIMIT 1")
    fun getActiveLearningLanguage(): Flow<UserLanguageEntity?>

    @Query("SELECT * FROM user_languages WHERE isCurrentActive = 1 LIMIT 1")
    suspend fun getActiveLearningLanguageDirect(): UserLanguageEntity?

    @Query("SELECT * FROM user_languages WHERE languageCode = :code LIMIT 1")
    fun getLanguageByCode(code: String): Flow<UserLanguageEntity?>

    @Query("SELECT * FROM user_languages WHERE languageCode = :code LIMIT 1")
    suspend fun getLanguageDirect(code: String): UserLanguageEntity?

    @Query("SELECT languageCode FROM user_languages")
    fun getEnrolledLanguageCodes(): Flow<List<String>>

    @Query("SELECT EXISTS(SELECT 1 FROM user_languages WHERE languageCode = :code)")
    fun isLanguageEnrolled(code: String): Flow<Boolean>

    @Query("SELECT COUNT(*) FROM user_languages")
    fun getEnrolledLanguagesCount(): Flow<Int>

    // --- Thêm / Cập nhật ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLanguage(language: UserLanguageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLanguages(languages: List<UserLanguageEntity>)

    @Update
    suspend fun updateLanguage(language: UserLanguageEntity)

    // --- Chuyển đổi Ngôn ngữ Hiện tại (Active Language) ---
    @Query("UPDATE user_languages SET isCurrentActive = 0")
    suspend fun clearActiveFlag()

    @Query("UPDATE user_languages SET isCurrentActive = 1, lastStudiedTimestamp = :timestamp WHERE languageCode = :code")
    suspend fun markActiveLanguage(code: String, timestamp: Long)

    @Transaction
    suspend fun switchActiveLanguage(code: String, timestamp: Long = System.currentTimeMillis()) {
        clearActiveFlag()
        markActiveLanguage(code, timestamp)
    }

    // --- Cập nhật Mục tiêu & Tiến trình ---
    @Query("UPDATE user_languages SET dailyGoalCards = :goal WHERE languageCode = :code")
    suspend fun updateDailyGoal(code: String, goal: Int)

    @Query("UPDATE user_languages SET masteredCardsCount = masteredCardsCount + :increment, lastStudiedTimestamp = :timestamp WHERE languageCode = :code")
    suspend fun incrementMasteredCount(code: String, increment: Int = 1, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE user_languages SET totalWordsEnrolled = :totalWords WHERE languageCode = :code")
    suspend fun updateTotalWordsEnrolled(code: String, totalWords: Int)

    @Query("UPDATE user_languages SET level = :level WHERE languageCode = :code")
    suspend fun updateLanguageLevel(code: String, level: String)

    @Query("UPDATE user_languages SET streakDays = :streak WHERE languageCode = :code")
    suspend fun updateLanguageStreak(code: String, streak: Int)

    // --- Xóa Ngôn ngữ Khỏi Lộ trình ---
    @Delete
    suspend fun deleteLanguage(language: UserLanguageEntity)

    @Query("DELETE FROM user_languages WHERE languageCode = :code")
    suspend fun deleteLanguageByCode(code: String)
}
