package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.FlashCardEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO xử lý toàn bộ truy vấn & thao tác dữ liệu cho Thẻ từ vựng (FlashCards)
 */
@Dao
interface FlashCardDao {

    // --- Query Thẻ theo Bộ / Ngôn ngữ ---
    @Query("SELECT * FROM flashcards WHERE deckId = :deckId ORDER BY id ASC")
    fun getCardsForDeck(deckId: String): Flow<List<FlashCardEntity>>

    @Query("SELECT * FROM flashcards WHERE languageCode = :langCode ORDER BY id ASC")
    fun getCardsByLanguage(langCode: String): Flow<List<FlashCardEntity>>

    @Query("SELECT * FROM flashcards ORDER BY id ASC")
    fun getAllCards(): Flow<List<FlashCardEntity>>

    @Query("SELECT * FROM flashcards WHERE id = :id LIMIT 1")
    fun getCardByIdFlow(id: Long): Flow<FlashCardEntity?>

    @Query("SELECT * FROM flashcards WHERE id = :id LIMIT 1")
    suspend fun getCardById(id: Long): FlashCardEntity?

    // --- Query Trạng thái Học tập & Đánh dấu sao ---
    @Query("SELECT * FROM flashcards WHERE isStarred = 1 ORDER BY lastReviewedTimestamp DESC, id DESC")
    fun getStarredCards(): Flow<List<FlashCardEntity>>

    @Query("SELECT * FROM flashcards WHERE languageCode = :langCode AND isStarred = 1")
    fun getStarredCardsByLanguage(langCode: String): Flow<List<FlashCardEntity>>

    @Query("SELECT * FROM flashcards WHERE isMastered = 1 ORDER BY lastReviewedTimestamp DESC")
    fun getMasteredCards(): Flow<List<FlashCardEntity>>

    @Query("SELECT * FROM flashcards WHERE deckId = :deckId AND isMastered = 1")
    fun getMasteredCardsForDeck(deckId: String): Flow<List<FlashCardEntity>>

    @Query("SELECT * FROM flashcards WHERE languageCode = :langCode AND isMastered = 0 ORDER BY lastReviewedTimestamp ASC, id ASC")
    fun getDueCardsByLanguage(langCode: String): Flow<List<FlashCardEntity>>

    @Query("SELECT * FROM flashcards WHERE deckId = :deckId AND isMastered = 0")
    fun getLearningCardsForDeck(deckId: String): Flow<List<FlashCardEntity>>

    @Query("SELECT * FROM flashcards WHERE difficulty = :difficulty")
    fun getCardsByDifficulty(difficulty: Int): Flow<List<FlashCardEntity>>

    // --- Query Luyện tập Ngẫu nhiên (Quiz / Match Game) ---
    @Query("SELECT * FROM flashcards WHERE deckId = :deckId ORDER BY RANDOM() LIMIT :limit")
    fun getRandomCardsForDeck(deckId: String, limit: Int): Flow<List<FlashCardEntity>>

    @Query("SELECT * FROM flashcards WHERE languageCode = :langCode ORDER BY RANDOM() LIMIT :limit")
    fun getRandomCardsByLanguage(langCode: String, limit: Int): Flow<List<FlashCardEntity>>

    @Query("SELECT * FROM flashcards WHERE isStarred = 1 ORDER BY RANDOM() LIMIT :limit")
    fun getRandomStarredCards(limit: Int): Flow<List<FlashCardEntity>>

    // --- Tìm kiếm & Tra cứu Từ vựng ---
    @Query("SELECT * FROM flashcards WHERE frontWord LIKE '%' || :query || '%' OR backMeaning LIKE '%' || :query || '%' OR frontExample LIKE '%' || :query || '%'")
    fun searchCards(query: String): Flow<List<FlashCardEntity>>

    // --- Đếm Thống kê (Counts) ---
    @Query("SELECT COUNT(*) FROM flashcards WHERE isMastered = 1")
    fun getMasteredCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM flashcards WHERE deckId = :deckId AND isMastered = 1")
    fun getMasteredCountForDeck(deckId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM flashcards WHERE isStarred = 1")
    fun getStarredCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM flashcards")
    fun getTotalCardsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM flashcards WHERE deckId = :deckId")
    fun getCardsCountForDeck(deckId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM flashcards WHERE languageCode = :langCode")
    fun getCardsCountByLanguage(langCode: String): Flow<Int>

    // --- Thao tác Thêm / Sửa / Xóa ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: FlashCardEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(cards: List<FlashCardEntity>)

    @Update
    suspend fun updateCard(card: FlashCardEntity)

    @Delete
    suspend fun deleteCard(card: FlashCardEntity)

    @Query("DELETE FROM flashcards WHERE id = :id")
    suspend fun deleteCardById(id: Long)

    @Query("DELETE FROM flashcards WHERE deckId = :deckId")
    suspend fun deleteCardsByDeckId(deckId: String)

    // --- Cập nhật Đánh dấu sao & Tiến độ Ôn tập ---
    @Query("UPDATE flashcards SET isStarred = :starred WHERE id = :id")
    suspend fun toggleStar(id: Long, starred: Boolean)

    @Query("UPDATE flashcards SET isMastered = :mastered, difficulty = :difficulty, reviewCount = reviewCount + 1, lastReviewedTimestamp = :timestamp WHERE id = :id")
    suspend fun recordReview(id: Long, mastered: Boolean, difficulty: Int, timestamp: Long)

    @Query("UPDATE flashcards SET isMastered = 0, reviewCount = 0, difficulty = 0 WHERE deckId = :deckId")
    suspend fun resetDeckProgress(deckId: String)
}
