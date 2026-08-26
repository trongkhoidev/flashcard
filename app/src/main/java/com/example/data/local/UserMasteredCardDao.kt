package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.UserMasteredCardEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO quản lý danh sách từ vựng đã thuộc (Mastered) theo từng User.
 */
@Dao
interface UserMasteredCardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMasteredCard(userMasteredCard: UserMasteredCardEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMasteredCards(userMasteredCards: List<UserMasteredCardEntity>)

    @Query("DELETE FROM user_mastered_cards WHERE userId = :userId AND cardId = :cardId")
    suspend fun removeMasteredCard(userId: Long, cardId: Long)

    @Query("DELETE FROM user_mastered_cards WHERE userId = :userId AND cardId IN (:cardIds)")
    suspend fun removeMasteredCards(userId: Long, cardIds: List<Long>)

    @Query("DELETE FROM user_mastered_cards WHERE userId = :userId")
    suspend fun clearMasteredCardsForUser(userId: Long)

    @Query("SELECT cardId FROM user_mastered_cards WHERE userId = :userId ORDER BY masteredAt DESC")
    fun getMasteredCardIdsForUser(userId: Long): Flow<List<Long>>

    @Query("SELECT cardId FROM user_mastered_cards WHERE userId = :userId ORDER BY masteredAt DESC")
    suspend fun getMasteredCardIdsForUserDirect(userId: Long): List<Long>

    @Query("SELECT COUNT(*) FROM user_mastered_cards WHERE userId = :userId AND cardId = :cardId")
    suspend fun isCardMastered(userId: Long, cardId: Long): Int

    @Query("SELECT COUNT(*) FROM user_mastered_cards WHERE userId = :userId")
    fun countMasteredCardsForUser(userId: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM user_mastered_cards WHERE userId = :userId")
    suspend fun countMasteredCardsForUserDirect(userId: Long): Int
}
