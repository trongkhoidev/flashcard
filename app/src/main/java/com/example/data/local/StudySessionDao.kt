package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.StudySessionEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO xử lý toàn bộ truy vấn & thao tác dữ liệu cho Phiên học & Ôn tập (Study Sessions)
 */
@Dao
interface StudySessionDao {

    @Query("SELECT * FROM study_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<StudySessionEntity>>

    @Query("SELECT * FROM study_sessions ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentSessions(limit: Int): Flow<List<StudySessionEntity>>

    // Phiên học gần nhất — dùng cho mục "Tiếp tục học" ở Home
    @Query("SELECT * FROM study_sessions ORDER BY timestamp DESC LIMIT 1")
    fun getLastStudySession(): Flow<StudySessionEntity?>

    @Query("SELECT * FROM study_sessions WHERE deckId = :deckId ORDER BY timestamp DESC")
    fun getSessionsForDeck(deckId: String): Flow<List<StudySessionEntity>>

    @Query("SELECT * FROM study_sessions WHERE languageCode = :langCode ORDER BY timestamp DESC")
    fun getSessionsByLanguage(langCode: String): Flow<List<StudySessionEntity>>

    @Query("SELECT * FROM study_sessions WHERE timestamp >= :sinceTimestamp ORDER BY timestamp DESC")
    fun getSessionsSince(sinceTimestamp: Long): Flow<List<StudySessionEntity>>

    @Query("SELECT SUM(cardsStudied) FROM study_sessions")
    fun getTotalCardsStudied(): Flow<Int?>

    @Query("SELECT SUM(durationSeconds) FROM study_sessions")
    fun getTotalStudyTimeSeconds(): Flow<Long?>

    @Query("SELECT COUNT(*) FROM study_sessions")
    fun getTotalSessionCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: StudySessionEntity): Long

    @Delete
    suspend fun deleteSession(session: StudySessionEntity)

    @Query("DELETE FROM study_sessions WHERE id = :id")
    suspend fun deleteSessionById(id: Long)

    @Query("DELETE FROM study_sessions")
    suspend fun clearAllSessions()
}
