package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DeckEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO xử lý toàn bộ truy vấn & thao tác dữ liệu cho các Bộ thẻ (Decks)
 */
@Dao
interface DeckDao {

    @Query("SELECT * FROM decks WHERE languageCode = :langCode AND (isCustom = 0 OR (isCustom = 1 AND userId = :userId)) ORDER BY title ASC")
    fun getDecksByLanguageForUser(langCode: String, userId: Long): Flow<List<DeckEntity>>

    @Query("SELECT * FROM decks WHERE languageCode = :langCode ORDER BY title ASC")
    fun getDecksByLanguage(langCode: String): Flow<List<DeckEntity>>

    @Query("SELECT * FROM decks WHERE isCustom = 0 OR (isCustom = 1 AND userId = :userId) ORDER BY languageCode, title ASC")
    fun getAllDecksForUser(userId: Long): Flow<List<DeckEntity>>

    @Query("SELECT * FROM decks ORDER BY languageCode, title ASC")
    fun getAllDecks(): Flow<List<DeckEntity>>

    @Query("SELECT * FROM decks WHERE id = :deckId LIMIT 1")
    fun getDeckByIdFlow(deckId: String): Flow<DeckEntity?>

    @Query("SELECT * FROM decks WHERE id = :deckId LIMIT 1")
    suspend fun getDeckById(deckId: String): DeckEntity?

    @Query("SELECT * FROM decks WHERE isCustom = 1 AND userId = :userId ORDER BY title ASC")
    fun getCustomDecksForUser(userId: Long): Flow<List<DeckEntity>>

    @Query("SELECT * FROM decks WHERE isCustom = 1 ORDER BY title ASC")
    fun getCustomDecks(): Flow<List<DeckEntity>>

    @Query("SELECT * FROM decks WHERE level = :level AND (isCustom = 0 OR (isCustom = 1 AND userId = :userId)) ORDER BY title ASC")
    fun getDecksByLevelForUser(level: String, userId: Long): Flow<List<DeckEntity>>

    @Query("SELECT * FROM decks WHERE level = :level ORDER BY title ASC")
    fun getDecksByLevel(level: String): Flow<List<DeckEntity>>

    @Query("SELECT * FROM decks WHERE (title LIKE '%' || :query || '%' OR subtitle LIKE '%' || :query || '%') AND (isCustom = 0 OR (isCustom = 1 AND userId = :userId))")
    fun searchDecksForUser(query: String, userId: Long): Flow<List<DeckEntity>>

    @Query("SELECT * FROM decks WHERE title LIKE '%' || :query || '%' OR subtitle LIKE '%' || :query || '%'")
    fun searchDecks(query: String): Flow<List<DeckEntity>>

    @Query("SELECT COUNT(*) FROM decks")
    fun getTotalDecksCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM decks WHERE languageCode = :langCode")
    fun getDecksCountByLanguage(langCode: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeck(deck: DeckEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDecks(decks: List<DeckEntity>)

    @Update
    suspend fun updateDeck(deck: DeckEntity)

    @Query("UPDATE decks SET cardCount = :count WHERE id = :deckId")
    suspend fun updateCardCount(deckId: String, count: Int)

    @Delete
    suspend fun deleteDeck(deck: DeckEntity)

    @Query("DELETE FROM decks WHERE id = :deckId")
    suspend fun deleteDeckById(deckId: String)
}
