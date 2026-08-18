package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DeckEntity
import com.example.data.model.FlashCardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FlashCardDao {

    // Decks
    @Query("SELECT * FROM decks WHERE languageCode = :langCode")
    fun getDecksByLanguage(langCode: String): Flow<List<DeckEntity>>

    @Query("SELECT * FROM decks")
    fun getAllDecks(): Flow<List<DeckEntity>>

    @Query("SELECT * FROM decks WHERE id = :deckId LIMIT 1")
    suspend fun getDeckById(deckId: String): DeckEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeck(deck: DeckEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDecks(decks: List<DeckEntity>)

    @Delete
    suspend fun deleteDeck(deck: DeckEntity)

    // FlashCards
    @Query("SELECT * FROM flashcards WHERE deckId = :deckId")
    fun getCardsForDeck(deckId: String): Flow<List<FlashCardEntity>>

    @Query("SELECT * FROM flashcards WHERE languageCode = :langCode")
    fun getCardsByLanguage(langCode: String): Flow<List<FlashCardEntity>>

    @Query("SELECT * FROM flashcards WHERE isStarred = 1")
    fun getStarredCards(): Flow<List<FlashCardEntity>>

    @Query("SELECT * FROM flashcards WHERE isMastered = 1")
    fun getMasteredCards(): Flow<List<FlashCardEntity>>

    @Query("SELECT * FROM flashcards WHERE languageCode = :langCode AND isMastered = 0")
    fun getDueCardsByLanguage(langCode: String): Flow<List<FlashCardEntity>>

    @Query("SELECT * FROM flashcards")
    fun getAllCards(): Flow<List<FlashCardEntity>>

    @Query("SELECT COUNT(*) FROM flashcards WHERE isMastered = 1")
    fun getMasteredCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM flashcards")
    fun getTotalCardsCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: FlashCardEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(cards: List<FlashCardEntity>)

    @Update
    suspend fun updateCard(card: FlashCardEntity)

    @Delete
    suspend fun deleteCard(card: FlashCardEntity)

    @Query("UPDATE flashcards SET isStarred = :starred WHERE id = :id")
    suspend fun toggleStar(id: Long, starred: Boolean)

    @Query("UPDATE flashcards SET isMastered = :mastered, difficulty = :difficulty, reviewCount = reviewCount + 1, lastReviewedTimestamp = :timestamp WHERE id = :id")
    suspend fun recordReview(id: Long, mastered: Boolean, difficulty: Int, timestamp: Long)
}
