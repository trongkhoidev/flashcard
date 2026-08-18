package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.DefaultVocabData
import com.example.data.local.FlashCardDao
import com.example.data.model.DeckEntity
import com.example.data.model.FlashCardEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class FlashCardRepository(private val dao: FlashCardDao) {

    suspend fun checkAndSeedDatabase(context: android.content.Context) = withContext(Dispatchers.IO) {
        val count = dao.getTotalCardsCount().firstOrNull() ?: 0
        if (count == 0) {
            AppDatabase.populateInitialData(context, dao)
        }
    }

    fun getDecksByLanguage(langCode: String): Flow<List<DeckEntity>> = dao.getDecksByLanguage(langCode)

    fun getAllDecks(): Flow<List<DeckEntity>> = dao.getAllDecks()

    suspend fun getDeckById(deckId: String): DeckEntity? = withContext(Dispatchers.IO) {
        dao.getDeckById(deckId)
    }

    fun getCardsForDeck(deckId: String): Flow<List<FlashCardEntity>> = dao.getCardsForDeck(deckId)

    fun getCardsByLanguage(langCode: String): Flow<List<FlashCardEntity>> = dao.getCardsByLanguage(langCode)

    fun getStarredCards(): Flow<List<FlashCardEntity>> = dao.getStarredCards()

    fun getMasteredCards(): Flow<List<FlashCardEntity>> = dao.getMasteredCards()

    fun getDueCardsByLanguage(langCode: String): Flow<List<FlashCardEntity>> = dao.getDueCardsByLanguage(langCode)

    fun getMasteredCount(): Flow<Int> = dao.getMasteredCount()

    fun getTotalCardsCount(): Flow<Int> = dao.getTotalCardsCount()

    suspend fun insertCards(cards: List<FlashCardEntity>) = withContext(Dispatchers.IO) {
        dao.insertCards(cards)
    }

    suspend fun insertCard(card: FlashCardEntity): Long = withContext(Dispatchers.IO) {
        dao.insertCard(card)
    }

    suspend fun insertDeck(deck: DeckEntity) = withContext(Dispatchers.IO) {
        dao.insertDeck(deck)
    }

    suspend fun toggleStar(id: Long, currentStarred: Boolean) = withContext(Dispatchers.IO) {
        dao.toggleStar(id, !currentStarred)
    }

    suspend fun recordCardReview(id: Long, difficulty: Int) = withContext(Dispatchers.IO) {
        // difficulty: 1 = Easy (Mastered), 2 = Good, 3 = Hard (Not mastered)
        val isMastered = (difficulty == 1)
        dao.recordReview(
            id = id,
            mastered = isMastered,
            difficulty = difficulty,
            timestamp = System.currentTimeMillis()
        )
    }

    suspend fun deleteCard(card: FlashCardEntity) = withContext(Dispatchers.IO) {
        dao.deleteCard(card)
    }

    suspend fun deleteDeck(deck: DeckEntity) = withContext(Dispatchers.IO) {
        dao.deleteDeck(deck)
    }
}
