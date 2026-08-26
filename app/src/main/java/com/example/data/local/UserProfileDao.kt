package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.UserProfileEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO xử lý toàn bộ truy vấn & thao tác dữ liệu cho Hồ sơ người dùng, VIP, Chuỗi ngày & Điểm số (User Profile)
 */
@Dao
interface UserProfileDao {

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileDirect(): UserProfileEntity?

    @Query("SELECT * FROM user_profile WHERE id = :userId LIMIT 1")
    fun getUserProfileById(userId: Long): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = :userId LIMIT 1")
    suspend fun getUserProfileByIdDirect(userId: Long): UserProfileEntity?

    @Query("SELECT * FROM user_profile WHERE id != :currentUserId ORDER BY totalPoints DESC")
    fun getOtherUserProfiles(currentUserId: Long): Flow<List<UserProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

    @Query("UPDATE user_profile SET userName = :name WHERE id = :userId")
    suspend fun updateNameForUser(userId: Long, name: String)

    @Query("UPDATE user_profile SET userName = :name WHERE id = 1")
    suspend fun updateName(name: String)

    @Query("UPDATE user_profile SET vipLevel = :vipLevel WHERE id = :userId")
    suspend fun updateVipLevelForUser(userId: Long, vipLevel: Int)

    @Query("UPDATE user_profile SET vipLevel = :vipLevel WHERE id = 1")
    suspend fun updateVipLevel(vipLevel: Int)

    @Query("UPDATE user_profile SET avatarEmoji = :emoji, avatarBgColorHex = :bgColorHex WHERE id = :userId")
    suspend fun updateAvatarForUser(userId: Long, emoji: String, bgColorHex: String)

    @Query("UPDATE user_profile SET avatarEmoji = :emoji, avatarBgColorHex = :bgColorHex WHERE id = 1")
    suspend fun updateAvatar(emoji: String, bgColorHex: String)

    @Query("UPDATE user_profile SET streakDays = :streak, maxStreakDays = MAX(maxStreakDays, :streak), lastActiveTimestamp = :lastActive WHERE id = :userId")
    suspend fun updateStreakForUser(userId: Long, streak: Int, lastActive: Long)

    @Query("UPDATE user_profile SET streakDays = :streak, maxStreakDays = MAX(maxStreakDays, :streak), lastActiveTimestamp = :lastActive WHERE id = 1")
    suspend fun updateStreak(streak: Int, lastActive: Long)

    @Query("UPDATE user_profile SET totalPoints = totalPoints + :points WHERE id = :userId")
    suspend fun addPointsForUser(userId: Long, points: Int)

    @Query("UPDATE user_profile SET totalPoints = totalPoints + :points WHERE id = 1")
    suspend fun addPoints(points: Int)

    @Query("UPDATE user_profile SET totalCardsLearned = totalCardsLearned + :count WHERE id = :userId")
    suspend fun incrementCardsLearnedForUser(userId: Long, count: Int)

    @Query("UPDATE user_profile SET totalCardsLearned = totalCardsLearned + :count WHERE id = 1")
    suspend fun incrementCardsLearned(count: Int)
}
