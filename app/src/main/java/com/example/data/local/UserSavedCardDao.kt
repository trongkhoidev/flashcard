package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.UserSavedCardEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO quản lý danh sách từ vựng đã lưu (⭐) riêng biệt theo từng User.
 */
@Dao
interface UserSavedCardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCard(userSavedCard: UserSavedCardEntity)

    @Query("DELETE FROM user_saved_cards WHERE userId = :userId AND cardId = :cardId")
    suspend fun removeSavedCard(userId: Long, cardId: Long)

    @Query("DELETE FROM user_saved_cards WHERE userId = :userId")
    suspend fun clearSavedCardsForUser(userId: Long)

    @Query("SELECT cardId FROM user_saved_cards WHERE userId = :userId ORDER BY createdAt DESC")
    fun getSavedCardIdsForUser(userId: Long): Flow<List<Long>>

    @Query("SELECT cardId FROM user_saved_cards WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getSavedCardIdsForUserDirect(userId: Long): List<Long>

    @Query("SELECT COUNT(*) FROM user_saved_cards WHERE userId = :userId AND cardId = :cardId")
    suspend fun isCardSaved(userId: Long, cardId: Long): Int

    @Query("SELECT COUNT(*) FROM user_saved_cards WHERE userId = :userId")
    fun countSavedCardsForUser(userId: Long): Flow<Int>
}
