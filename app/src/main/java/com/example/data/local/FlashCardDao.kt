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
 * Bao gồm thuật toán Ghi nhớ ngắt quãng (Spaced Repetition System - SRS),
 * Hệ thống lọc theo Ngôn ngữ đang học, Thống kê mục tiêu ngày & Ôn tập.
 */
@Dao
interface FlashCardDao {

    // ========================================================
    // 1. QUERY THẺ THEO BỘ / NGÔN NGỮ
    // ========================================================
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

    // ========================================================
    // 2. QUERY THEO TRẠNG THÁI HỌC & SPACED REPETITION (SRS)
    // ========================================================
    @Query("SELECT * FROM flashcards WHERE isStarred = 1 ORDER BY lastReviewedTimestamp DESC, id DESC")
    fun getStarredCards(): Flow<List<FlashCardEntity>>

    @Query("SELECT * FROM flashcards WHERE languageCode = :langCode AND isStarred = 1")
    fun getStarredCardsByLanguage(langCode: String): Flow<List<FlashCardEntity>>

    @Query("SELECT * FROM flashcards WHERE isMastered = 1 ORDER BY lastReviewedTimestamp DESC")
    fun getMasteredCards(): Flow<List<FlashCardEntity>>

    @Query("SELECT * FROM flashcards WHERE languageCode = :langCode AND isMastered = 1")
    fun getMasteredCardsByLanguage(langCode: String): Flow<List<FlashCardEntity>>

    @Query("SELECT * FROM flashcards WHERE deckId = :deckId AND isMastered = 1")
    fun getMasteredCardsForDeck(deckId: String): Flow<List<FlashCardEntity>>

    @Query("SELECT * FROM flashcards WHERE deckId = :deckId AND isMastered = 0")
    fun getLearningCardsForDeck(deckId: String): Flow<List<FlashCardEntity>>

    // Lấy thẻ đến hạn ôn tập (Spaced Repetition Due Cards) theo Ngôn ngữ
    @Query("""
        SELECT * FROM flashcards 
        WHERE languageCode = :langCode 
          AND (nextReviewTimestamp <= :currentTimestamp OR isMastered = 0)
        ORDER BY nextReviewTimestamp ASC, id ASC 
        LIMIT :limit
    """)
    fun getDueCardsForLanguage(
        langCode: String,
        currentTimestamp: Long = System.currentTimeMillis(),
        limit: Int = 50
    ): Flow<List<FlashCardEntity>>

    // Lấy tất cả thẻ đến hạn ôn tập trên toàn bộ hệ thống
    @Query("""
        SELECT * FROM flashcards 
        WHERE nextReviewTimestamp <= :currentTimestamp OR isMastered = 0
        ORDER BY nextReviewTimestamp ASC, id ASC 
        LIMIT :limit
    """)
    fun getAllDueCards(
        currentTimestamp: Long = System.currentTimeMillis(),
        limit: Int = 50
    ): Flow<List<FlashCardEntity>>

    // Lấy thẻ đến hạn ôn tập theo Bộ thẻ cụ thể
    @Query("""
        SELECT * FROM flashcards 
        WHERE deckId = :deckId 
          AND (nextReviewTimestamp <= :currentTimestamp OR isMastered = 0)
        ORDER BY nextReviewTimestamp ASC, id ASC
    """)
    fun getDueCardsForDeck(
        deckId: String,
        currentTimestamp: Long = System.currentTimeMillis()
    ): Flow<List<FlashCardEntity>>

    // Lấy danh sách từ mới khởi đầu (chưa học lần nào)
    @Query("SELECT * FROM flashcards WHERE languageCode = :langCode AND reviewCount = 0 ORDER BY id ASC LIMIT :limit")
    fun getStarterCardsForLanguage(langCode: String, limit: Int = 20): Flow<List<FlashCardEntity>>

    @Query("SELECT * FROM flashcards WHERE difficulty = :difficulty")
    fun getCardsByDifficulty(difficulty: Int): Flow<List<FlashCardEntity>>

    // ========================================================
    // 3. QUERY LUYỆN TẬP NGẪU NHIÊN (QUIZ & MATCH GAME)
    // ========================================================
    @Query("SELECT * FROM flashcards WHERE deckId = :deckId ORDER BY RANDOM() LIMIT :limit")
    fun getRandomCardsForDeck(deckId: String, limit: Int): Flow<List<FlashCardEntity>>

    @Query("SELECT * FROM flashcards WHERE languageCode = :langCode ORDER BY RANDOM() LIMIT :limit")
    fun getRandomCardsByLanguage(langCode: String, limit: Int): Flow<List<FlashCardEntity>>

    // Toàn bộ thẻ của 1 ngôn ngữ, thứ tự ổn định (dùng cho Widget hiển thị tuần tự)
    @Query("SELECT * FROM flashcards WHERE languageCode = :langCode ORDER BY nextReviewTimestamp ASC, id ASC")
    fun getAllCardsByLanguage(langCode: String): Flow<List<FlashCardEntity>>

    @Query("SELECT * FROM flashcards WHERE isStarred = 1 ORDER BY RANDOM() LIMIT :limit")
    fun getRandomStarredCards(limit: Int): Flow<List<FlashCardEntity>>

    // ========================================================
    // 4. TÌM KIẾM & TRA CỨU TỪ VỰNG
    // ========================================================
    @Query("""
        SELECT * FROM flashcards 
        WHERE frontWord LIKE '%' || :query || '%' 
           OR backMeaning LIKE '%' || :query || '%' 
           OR frontExample LIKE '%' || :query || '%'
    """)
    fun searchCards(query: String): Flow<List<FlashCardEntity>>

    // ========================================================
    // 5. ĐẾM & THỐNG KÊ TIẾN ĐỘ (COUNTS & STATS)
    // ========================================================
    @Query("SELECT COUNT(*) FROM flashcards WHERE isMastered = 1")
    fun getMasteredCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM flashcards WHERE languageCode = :langCode AND isMastered = 1")
    fun getMasteredCountByLanguage(langCode: String): Flow<Int>

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

    @Query("""
        SELECT COUNT(*) FROM flashcards 
        WHERE languageCode = :langCode 
          AND (nextReviewTimestamp <= :currentTimestamp OR isMastered = 0)
    """)
    fun getDueCountForLanguage(langCode: String, currentTimestamp: Long = System.currentTimeMillis()): Flow<Int>

    @Query("""
        SELECT COUNT(*) FROM flashcards 
        WHERE nextReviewTimestamp <= :currentTimestamp OR isMastered = 0
    """)
    fun getTotalDueCount(currentTimestamp: Long = System.currentTimeMillis()): Flow<Int>

    @Query("""
        SELECT COUNT(*) FROM flashcards 
        WHERE deckId = :deckId 
          AND (nextReviewTimestamp <= :currentTimestamp OR isMastered = 0)
    """)
    fun getDueCountForDeck(deckId: String, currentTimestamp: Long = System.currentTimeMillis()): Flow<Int>

    // Đếm số lượng từ đã học hôm nay (cho Thẻ mục tiêu ngày)
    @Query("SELECT COUNT(*) FROM flashcards WHERE lastReviewedTimestamp >= :startOfDayTimestamp")
    fun getCardsStudiedTodayCount(startOfDayTimestamp: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM flashcards WHERE languageCode = :langCode AND lastReviewedTimestamp >= :startOfDayTimestamp")
    fun getCardsStudiedTodayByLanguageCount(langCode: String, startOfDayTimestamp: Long): Flow<Int>

    // Tiến trình thật của TẤT CẢ deck theo user: tổng thẻ & số thẻ đã thuộc (quiz đúng mới thuộc)
    @Query("""
        SELECT d.*,
               COUNT(f.id) AS totalCards,
               COALESCE(SUM(CASE WHEN f.isMastered = 1 THEN 1 ELSE 0 END), 0) AS masteredCards
        FROM decks d
        LEFT JOIN flashcards f ON f.deckId = d.id
        WHERE d.isCustom = 0 OR (d.isCustom = 1 AND d.userId = :userId)
        GROUP BY d.id
        ORDER BY d.id
    """)
    fun getAllDecksWithStatsForUser(userId: Long): Flow<List<com.example.data.model.DeckWithStats>>

    @Query("""
        SELECT d.*,
               COUNT(f.id) AS totalCards,
               COALESCE(SUM(CASE WHEN f.isMastered = 1 THEN 1 ELSE 0 END), 0) AS masteredCards
        FROM decks d
        LEFT JOIN flashcards f ON f.deckId = d.id
        GROUP BY d.id
        ORDER BY d.id
    """)
    fun getAllDecksWithStats(): Flow<List<com.example.data.model.DeckWithStats>>

    // ========================================================
    // 6. THAO TÁC THÊM / SỬA / XÓA
    // ========================================================
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

    // ========================================================
    // 7. CẬP NHẬT TRẠNG THÁI ÔN TẬP & SRS (SPACED REPETITION)
    // ========================================================
    @Query("UPDATE flashcards SET isStarred = :starred WHERE id = :id")
    suspend fun toggleStar(id: Long, starred: Boolean)

    @Query("UPDATE flashcards SET isStarred = 0")
    suspend fun clearAllStarredFlags()

    @Query("UPDATE flashcards SET isStarred = 1 WHERE id IN (:cardIds)")
    suspend fun setStarredForCardIds(cardIds: List<Long>)

    @Query("UPDATE flashcards SET isMastered = 0")
    suspend fun clearAllMasteredFlags()

    @Query("UPDATE flashcards SET isMastered = 1 WHERE id IN (:cardIds)")
    suspend fun setMasteredForCardIds(cardIds: List<Long>)

    @Query("""
        UPDATE flashcards 
        SET isMastered = :mastered, 
            difficulty = :difficulty, 
            reviewCount = reviewCount + 1, 
            lastReviewedTimestamp = :timestamp 
        WHERE id = :id
    """)
    suspend fun recordReview(id: Long, mastered: Boolean, difficulty: Int, timestamp: Long)

    // Ghi nhận lượt học flashcard: KHÔNG thay đổi isMastered.
    // Trạng thái "Đã thuộc" chỉ được quyết định bởi kết quả trả lời ĐÚNG trong Quiz.
    @Query("""
        UPDATE flashcards
        SET difficulty = :difficulty,
            reviewCount = reviewCount + 1,
            lastReviewedTimestamp = :timestamp
        WHERE id = :id
    """)
    suspend fun updateReviewProgress(id: Long, difficulty: Int, timestamp: Long)

    // Cập nhật thông số Spaced Repetition (SRS)
    @Query("""
        UPDATE flashcards 
        SET isMastered = :isMastered,
            difficulty = :difficulty,
            reviewCount = reviewCount + 1,
            lastReviewedTimestamp = :timestamp,
            nextReviewTimestamp = :nextReviewTimestamp,
            srsInterval = :srsInterval,
            srsEaseFactor = :srsEaseFactor,
            srsRepetitions = :srsRepetitions
        WHERE id = :id
    """)
    suspend fun updateSrsReview(
        id: Long,
        isMastered: Boolean,
        difficulty: Int,
        timestamp: Long,
        nextReviewTimestamp: Long,
        srsInterval: Int,
        srsEaseFactor: Float,
        srsRepetitions: Int
    )

    @Query("UPDATE flashcards SET isMastered = 0, reviewCount = 0, difficulty = 0, nextReviewTimestamp = 0, srsRepetitions = 0 WHERE deckId = :deckId")
    suspend fun resetDeckProgress(deckId: String)
}
