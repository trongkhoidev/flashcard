package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.QuizRecordEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO xử lý toàn bộ truy vấn & thao tác dữ liệu cho Lịch sử thi Trắc nghiệm & Trò chơi ghép thẻ (Quiz & Game Records)
 */
@Dao
interface QuizRecordDao {

    @Query("SELECT * FROM quiz_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<QuizRecordEntity>>

    @Query("SELECT * FROM quiz_records ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentRecords(limit: Int): Flow<List<QuizRecordEntity>>

    @Query("SELECT * FROM quiz_records WHERE mode = :mode ORDER BY timestamp DESC")
    fun getRecordsByMode(mode: String): Flow<List<QuizRecordEntity>>

    @Query("SELECT * FROM quiz_records WHERE deckId = :deckId ORDER BY timestamp DESC")
    fun getRecordsForDeck(deckId: String): Flow<List<QuizRecordEntity>>

    @Query("SELECT MAX(score) FROM quiz_records WHERE deckId = :deckId")
    fun getHighestScoreForDeck(deckId: String): Flow<Int?>

    @Query("SELECT SUM(pointsEarned) FROM quiz_records")
    fun getTotalPointsEarned(): Flow<Int?>

    @Query("SELECT MAX(maxStreak) FROM quiz_records")
    fun getHighestStreak(): Flow<Int?>

    @Query("SELECT AVG(accuracyPercent) FROM quiz_records")
    fun getAverageAccuracy(): Flow<Float?>

    @Query("SELECT COUNT(*) FROM quiz_records")
    fun getTotalGamesPlayed(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: QuizRecordEntity): Long

    @Delete
    suspend fun deleteRecord(record: QuizRecordEntity)

    @Query("DELETE FROM quiz_records WHERE id = :id")
    suspend fun deleteRecordById(id: Long)

    @Query("DELETE FROM quiz_records")
    suspend fun clearQuizHistory()
}
