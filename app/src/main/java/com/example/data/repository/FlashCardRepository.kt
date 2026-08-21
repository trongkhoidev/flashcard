package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.DeckDao
import com.example.data.local.FlashCardDao
import com.example.data.local.QuizRecordDao
import com.example.data.local.StudySessionDao
import com.example.data.local.UserProfileDao
import com.example.data.model.DeckEntity
import com.example.data.model.FlashCardEntity
import com.example.data.model.QuizRecordEntity
import com.example.data.model.StudySessionEntity
import com.example.data.model.UserProfileEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class FlashCardRepository(
    private val deckDao: DeckDao,
    private val cardDao: FlashCardDao,
    private val sessionDao: StudySessionDao,
    private val quizDao: QuizRecordDao,
    private val profileDao: UserProfileDao
) {

    constructor(database: AppDatabase) : this(
        deckDao = database.deckDao(),
        cardDao = database.flashCardDao(),
        sessionDao = database.studySessionDao(),
        quizDao = database.quizRecordDao(),
        profileDao = database.userProfileDao()
    )

    suspend fun checkAndSeedDatabase() = withContext(Dispatchers.IO) {
        val count = cardDao.getTotalCardsCount().firstOrNull() ?: 0
        if (count == 0) {
            AppDatabase.populateInitialData(deckDao, cardDao, profileDao)
        }
    }

    // ==========================================
    // 1. DECK (BỘ THẺ) QUERIES & OPERATIONS
    // ==========================================
    fun getDecksByLanguage(langCode: String): Flow<List<DeckEntity>> = deckDao.getDecksByLanguage(langCode)

    fun getAllDecks(): Flow<List<DeckEntity>> = deckDao.getAllDecks()

    fun getCustomDecks(): Flow<List<DeckEntity>> = deckDao.getCustomDecks()

    fun getDecksByLevel(level: String): Flow<List<DeckEntity>> = deckDao.getDecksByLevel(level)

    fun searchDecks(query: String): Flow<List<DeckEntity>> = deckDao.searchDecks(query)

    suspend fun getDeckById(deckId: String): DeckEntity? = withContext(Dispatchers.IO) {
        deckDao.getDeckById(deckId)
    }

    fun getDeckByIdFlow(deckId: String): Flow<DeckEntity?> = deckDao.getDeckByIdFlow(deckId)

    suspend fun insertDeck(deck: DeckEntity) = withContext(Dispatchers.IO) {
        deckDao.insertDeck(deck)
    }

    suspend fun insertDecks(decks: List<DeckEntity>) = withContext(Dispatchers.IO) {
        deckDao.insertDecks(decks)
    }

    suspend fun updateDeck(deck: DeckEntity) = withContext(Dispatchers.IO) {
        deckDao.updateDeck(deck)
    }

    suspend fun deleteDeck(deck: DeckEntity) = withContext(Dispatchers.IO) {
        // Delete cards in this deck first then delete deck
        cardDao.deleteCardsByDeckId(deck.id)
        deckDao.deleteDeck(deck)
    }

    suspend fun deleteDeckById(deckId: String) = withContext(Dispatchers.IO) {
        cardDao.deleteCardsByDeckId(deckId)
        deckDao.deleteDeckById(deckId)
    }

    // ==========================================
    // 2. FLASHCARD (THẺ TỪ VỰNG) QUERIES & OPERATIONS
    // ==========================================
    fun getCardsForDeck(deckId: String): Flow<List<FlashCardEntity>> = cardDao.getCardsForDeck(deckId)

    fun getCardsByLanguage(langCode: String): Flow<List<FlashCardEntity>> = cardDao.getCardsByLanguage(langCode)

    fun getAllCards(): Flow<List<FlashCardEntity>> = cardDao.getAllCards()

    fun getStarredCards(): Flow<List<FlashCardEntity>> = cardDao.getStarredCards()

    fun getStarredCardsByLanguage(langCode: String): Flow<List<FlashCardEntity>> = cardDao.getStarredCardsByLanguage(langCode)

    fun getMasteredCards(): Flow<List<FlashCardEntity>> = cardDao.getMasteredCards()

    fun getDueCardsByLanguage(langCode: String): Flow<List<FlashCardEntity>> = cardDao.getDueCardsByLanguage(langCode)

    fun getCardsByDifficulty(difficulty: Int): Flow<List<FlashCardEntity>> = cardDao.getCardsByDifficulty(difficulty)

    fun getRandomCardsForDeck(deckId: String, limit: Int): Flow<List<FlashCardEntity>> = cardDao.getRandomCardsForDeck(deckId, limit)

    fun getRandomCardsByLanguage(langCode: String, limit: Int): Flow<List<FlashCardEntity>> = cardDao.getRandomCardsByLanguage(langCode, limit)

    fun searchCards(query: String): Flow<List<FlashCardEntity>> = cardDao.searchCards(query)

    fun getMasteredCount(): Flow<Int> = cardDao.getMasteredCount()

    fun getStarredCount(): Flow<Int> = cardDao.getStarredCount()

    fun getTotalCardsCount(): Flow<Int> = cardDao.getTotalCardsCount()

    fun getCardsCountForDeck(deckId: String): Flow<Int> = cardDao.getCardsCountForDeck(deckId)

    suspend fun getCardById(id: Long): FlashCardEntity? = withContext(Dispatchers.IO) {
        cardDao.getCardById(id)
    }

    suspend fun insertCards(cards: List<FlashCardEntity>) = withContext(Dispatchers.IO) {
        cardDao.insertCards(cards)
    }

    suspend fun insertCard(card: FlashCardEntity): Long = withContext(Dispatchers.IO) {
        cardDao.insertCard(card)
    }

    suspend fun updateCard(card: FlashCardEntity) = withContext(Dispatchers.IO) {
        cardDao.updateCard(card)
    }

    suspend fun deleteCard(card: FlashCardEntity) = withContext(Dispatchers.IO) {
        cardDao.deleteCard(card)
    }

    suspend fun deleteCardById(id: Long) = withContext(Dispatchers.IO) {
        cardDao.deleteCardById(id)
    }

    suspend fun toggleStar(id: Long, currentStarred: Boolean) = withContext(Dispatchers.IO) {
        cardDao.toggleStar(id, !currentStarred)
    }

    suspend fun recordCardReview(id: Long, difficulty: Int) = withContext(Dispatchers.IO) {
        // difficulty: 1 = Easy (Mastered), 2 = Good, 3 = Hard (Not mastered)
        val isMastered = (difficulty == 1)
        cardDao.recordReview(
            id = id,
            mastered = isMastered,
            difficulty = difficulty,
            timestamp = System.currentTimeMillis()
        )
    }

    suspend fun resetDeckProgress(deckId: String) = withContext(Dispatchers.IO) {
        cardDao.resetDeckProgress(deckId)
    }

    // ==========================================
    // 3. STUDY SESSIONS (LỊCH SỬ ÔN TẬP)
    // ==========================================
    fun getAllStudySessions(): Flow<List<StudySessionEntity>> = sessionDao.getAllSessions()

    fun getRecentStudySessions(limit: Int = 10): Flow<List<StudySessionEntity>> = sessionDao.getRecentSessions(limit)

    fun getSessionsForDeck(deckId: String): Flow<List<StudySessionEntity>> = sessionDao.getSessionsForDeck(deckId)

    fun getTotalCardsStudiedCount(): Flow<Int?> = sessionDao.getTotalCardsStudied()

    fun getTotalStudyTimeSeconds(): Flow<Long?> = sessionDao.getTotalStudyTimeSeconds()

    suspend fun recordStudySession(session: StudySessionEntity): Long = withContext(Dispatchers.IO) {
        sessionDao.insertSession(session)
    }

    suspend fun clearStudyHistory() = withContext(Dispatchers.IO) {
        sessionDao.clearAllSessions()
    }

    // ==========================================
    // 4. QUIZ & GAME RECORDS (LỊCH SỬ THI & ĐIỂM SỐ)
    // ==========================================
    fun getAllQuizRecords(): Flow<List<QuizRecordEntity>> = quizDao.getAllRecords()

    fun getRecentQuizRecords(limit: Int = 10): Flow<List<QuizRecordEntity>> = quizDao.getRecentRecords(limit)

    fun getQuizRecordsByMode(mode: String): Flow<List<QuizRecordEntity>> = quizDao.getRecordsByMode(mode)

    fun getHighestQuizScoreForDeck(deckId: String): Flow<Int?> = quizDao.getHighestScoreForDeck(deckId)

    fun getTotalQuizPointsEarned(): Flow<Int?> = quizDao.getTotalPointsEarned()

    fun getHighestStreakRecord(): Flow<Int?> = quizDao.getHighestStreak()

    fun getTotalGamesPlayedCount(): Flow<Int> = quizDao.getTotalGamesPlayed()

    suspend fun recordQuizResult(record: QuizRecordEntity): Long = withContext(Dispatchers.IO) {
        quizDao.insertRecord(record)
    }

    suspend fun clearQuizHistory() = withContext(Dispatchers.IO) {
        quizDao.clearQuizHistory()
    }

    // ==========================================
    // 5. USER PROFILE & STREAK (HỒ SƠ & TIẾN ĐỘ)
    // ==========================================
    fun getUserProfile(): Flow<UserProfileEntity?> = profileDao.getUserProfile()

    suspend fun getUserProfileDirect(): UserProfileEntity? = withContext(Dispatchers.IO) {
        profileDao.getUserProfileDirect()
    }

    suspend fun saveUserProfile(profile: UserProfileEntity) = withContext(Dispatchers.IO) {
        profileDao.insertOrUpdateProfile(profile)
    }

    suspend fun updateUserName(name: String) = withContext(Dispatchers.IO) {
        profileDao.updateName(name)
    }

    suspend fun updateUserVipLevel(vipLevel: Int) = withContext(Dispatchers.IO) {
        profileDao.updateVipLevel(vipLevel)
    }

    suspend fun updateUserAvatar(emoji: String, bgColorHex: String) = withContext(Dispatchers.IO) {
        profileDao.updateAvatar(emoji, bgColorHex)
    }

    suspend fun updateStreak(streakDays: Int) = withContext(Dispatchers.IO) {
        profileDao.updateStreak(streakDays, System.currentTimeMillis())
    }

    suspend fun addPoints(points: Int) = withContext(Dispatchers.IO) {
        profileDao.addPoints(points)
    }

    suspend fun incrementCardsLearned(count: Int) = withContext(Dispatchers.IO) {
        profileDao.incrementCardsLearned(count)
    }
}
