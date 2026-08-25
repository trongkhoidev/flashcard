package com.example.data.repository

import com.example.data.local.StudyScheduleDao
import com.example.data.model.StudyScheduleEntity
import com.example.data.local.UserAccountDao
import com.example.data.model.UserAccountEntity
import com.example.data.local.AppDatabase
import com.example.data.local.DeckDao
import com.example.data.local.FlashCardDao
import com.example.data.local.QuizRecordDao
import com.example.data.local.StudySessionDao
import com.example.data.local.UserLanguageDao
import com.example.data.local.UserProfileDao
import com.example.data.model.AppLanguage
import com.example.data.model.DeckEntity
import com.example.data.model.FlashCardEntity
import com.example.data.model.QuizRecordEntity
import com.example.data.model.StudySessionEntity
import com.example.data.model.UserLanguageEntity
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
    private val profileDao: UserProfileDao,
    private val languageDao: UserLanguageDao,
    private val accountDao: UserAccountDao,
    private val scheduleDao: StudyScheduleDao
) {
    private companion object {
        const val MILLIS_PER_DAY = 86_400_000L
    }


    constructor(database: AppDatabase) : this(
        deckDao = database.deckDao(),
        cardDao = database.flashCardDao(),
        sessionDao = database.studySessionDao(),
        quizDao = database.quizRecordDao(),
        profileDao = database.userProfileDao(),
        languageDao = database.userLanguageDao(),
        accountDao = database.userAccountDao(),
        scheduleDao = database.studyScheduleDao()
    )

    suspend fun checkAndSeedDatabase() = withContext(Dispatchers.IO) {
        val count = cardDao.getTotalCardsCount().firstOrNull() ?: 0
        if (count == 0) {
            AppDatabase.populateInitialData(deckDao, cardDao, profileDao, languageDao)
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
        cardDao.deleteCardsByDeckId(deck.id)
        deckDao.deleteDeck(deck)
    }

    suspend fun deleteDeckById(deckId: String) = withContext(Dispatchers.IO) {
        cardDao.deleteCardsByDeckId(deckId)
        deckDao.deleteDeckById(deckId)
    }

    // ==========================================
    // 2. FLASHCARD & SPACED REPETITION (SRS) OPERATIONS
    // ==========================================
    fun getCardsForDeck(deckId: String): Flow<List<FlashCardEntity>> = cardDao.getCardsForDeck(deckId)

    fun getCardsByLanguage(langCode: String): Flow<List<FlashCardEntity>> = cardDao.getCardsByLanguage(langCode)

    fun getAllCards(): Flow<List<FlashCardEntity>> = cardDao.getAllCards()

    fun getStarredCards(): Flow<List<FlashCardEntity>> = cardDao.getStarredCards()

    fun getStarredCardsByLanguage(langCode: String): Flow<List<FlashCardEntity>> = cardDao.getStarredCardsByLanguage(langCode)

    fun getMasteredCards(): Flow<List<FlashCardEntity>> = cardDao.getMasteredCards()

    fun getMasteredCardsByLanguage(langCode: String): Flow<List<FlashCardEntity>> = cardDao.getMasteredCardsByLanguage(langCode)

    fun getDueCardsForLanguage(langCode: String, limit: Int = 50): Flow<List<FlashCardEntity>> = 
        cardDao.getDueCardsForLanguage(langCode = langCode, limit = limit)

    fun getAllDueCards(limit: Int = 50): Flow<List<FlashCardEntity>> = 
        cardDao.getAllDueCards(limit = limit)

    fun getStarterCardsForLanguage(langCode: String, limit: Int = 20): Flow<List<FlashCardEntity>> =
        cardDao.getStarterCardsForLanguage(langCode, limit)

    fun getCardsByDifficulty(difficulty: Int): Flow<List<FlashCardEntity>> = cardDao.getCardsByDifficulty(difficulty)

    fun getRandomCardsForDeck(deckId: String, limit: Int): Flow<List<FlashCardEntity>> = cardDao.getRandomCardsForDeck(deckId, limit)

    fun getRandomCardsByLanguage(langCode: String, limit: Int): Flow<List<FlashCardEntity>> = cardDao.getRandomCardsByLanguage(langCode, limit)

    fun searchCards(query: String): Flow<List<FlashCardEntity>> = cardDao.searchCards(query)

    fun getMasteredCount(): Flow<Int> = cardDao.getMasteredCount()

    fun getMasteredCountByLanguage(langCode: String): Flow<Int> = cardDao.getMasteredCountByLanguage(langCode)

    fun getStarredCount(): Flow<Int> = cardDao.getStarredCount()

    fun getTotalCardsCount(): Flow<Int> = cardDao.getTotalCardsCount()

    fun getCardsCountForDeck(deckId: String): Flow<Int> = cardDao.getCardsCountForDeck(deckId)

    fun getCardsCountByLanguage(langCode: String): Flow<Int> = cardDao.getCardsCountByLanguage(langCode)

    fun getDueCountForLanguage(langCode: String): Flow<Int> = cardDao.getDueCountForLanguage(langCode)

    fun getTotalDueCount(): Flow<Int> = cardDao.getTotalDueCount()

    fun getCardsStudiedTodayCount(startOfDayTimestamp: Long): Flow<Int> = 
        cardDao.getCardsStudiedTodayCount(startOfDayTimestamp)

    fun getCardsStudiedTodayByLanguageCount(langCode: String, startOfDayTimestamp: Long): Flow<Int> =
        cardDao.getCardsStudiedTodayByLanguageCount(langCode, startOfDayTimestamp)

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

    suspend fun deleteCardsByDeckId(deckId: String) = withContext(Dispatchers.IO) {
        cardDao.deleteCardsByDeckId(deckId)
    }

    suspend fun toggleStar(id: Long, currentStarred: Boolean) = withContext(Dispatchers.IO) {
        cardDao.toggleStar(id, !currentStarred)
    }

    /**
     * Đánh dấu từ vựng ĐÃ THUỘC (Hoàn thành) khi người dùng trả lời đúng trong Quiz
     */
    suspend fun markCardMastered(id: Long, langCode: String) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        cardDao.recordReview(
            id = id,
            mastered = true,
            difficulty = 1,
            timestamp = now
        )
        languageDao.incrementMasteredCount(langCode, increment = 1, timestamp = now)
    }

    /**
     * Đánh dấu từ vựng CHƯA THUỘC khi người dùng trả lời sai trong Quiz
     */
    suspend fun markCardUnmastered(id: Long) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        cardDao.recordReview(
            id = id,
            mastered = false,
            difficulty = 3,
            timestamp = now
        )
    }

    suspend fun recordCardReview(id: Long, difficulty: Int) = withContext(Dispatchers.IO) {
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
    // 3. MULTI-LANGUAGE LEARNING (NGÔN NGỮ HỌC)
    // ==========================================
    fun getAllLearningLanguages(): Flow<List<UserLanguageEntity>> = languageDao.getAllLearningLanguages()

    fun getActiveLearningLanguage(): Flow<UserLanguageEntity?> = languageDao.getActiveLearningLanguage()

    fun getEnrolledLanguageCodes(): Flow<List<String>> = languageDao.getEnrolledLanguageCodes()

    fun isLanguageEnrolled(code: String): Flow<Boolean> = languageDao.isLanguageEnrolled(code)

    suspend fun addLearningLanguage(language: AppLanguage) = withContext(Dispatchers.IO) {
        val existing = languageDao.getLanguageDirect(language.code)
        if (existing == null) {
            languageDao.insertLanguage(
                UserLanguageEntity(
                    languageCode = language.code,
                    displayName = language.displayName,
                    flagEmoji = language.flagEmoji,
                    isCurrentActive = false,
                    dailyGoalCards = 20,
                    masteredCardsCount = 0,
                    totalWordsEnrolled = 50,
                    level = "Mới bắt đầu"
                )
            )
        }
    }

    suspend fun switchActiveLanguage(languageCode: String) = withContext(Dispatchers.IO) {
        languageDao.switchActiveLanguage(languageCode)
    }

    suspend fun updateLanguageDailyGoal(languageCode: String, goal: Int) = withContext(Dispatchers.IO) {
        languageDao.updateDailyGoal(languageCode, goal)
    }

    suspend fun deleteLearningLanguage(languageCode: String) = withContext(Dispatchers.IO) {
        languageDao.deleteLanguageByCode(languageCode)
    }

    // ==========================================
    // 4. STUDY SESSIONS (LỊCH SỬ ÔN TẬP)
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
    // 5. QUIZ & GAME RECORDS (LỊCH SỬ THI & ĐIỂM SỐ)
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
    // 6. USER PROFILE & STREAK (HỒ SƠ & TIẾN ĐỘ)
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

    /**
     * Cập nhật chuỗi ngày học theo ngày thật:
     * - Học trong cùng ngày -> giữ nguyên (tránh cộng dồn nhiều lần/ngày)
     * - Học liên tục sang hôm sau -> streak + 1
     * - Bỏ học >= 2 ngày -> reset về 1
     */
    suspend fun updateDailyStreakIfNeeded(now: Long = System.currentTimeMillis()) = withContext(Dispatchers.IO) {
        val profile = profileDao.getUserProfileDirect() ?: return@withContext
        val dayDiff = ((startOfDayMillis(now) - startOfDayMillis(profile.lastActiveTimestamp)) / MILLIS_PER_DAY).toInt()
        val newStreak = when {
            dayDiff <= 0 -> return@withContext
            dayDiff == 1 -> profile.streakDays + 1
            else -> 1
        }
        profileDao.updateStreak(newStreak, now)
    }

    private fun startOfDayMillis(millis: Long): Long {
        val cal = java.util.Calendar.getInstance()
        cal.timeInMillis = millis
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    suspend fun addPoints(points: Int) = withContext(Dispatchers.IO) {
        profileDao.addPoints(points)
    }

    suspend fun incrementCardsLearned(count: Int) = withContext(Dispatchers.IO) {
        profileDao.incrementCardsLearned(count)
    }

    // ==========================================
    // 7. USER ACCOUNTS & AUTHENTICATION
    // ==========================================
    fun getActiveLoggedInUser(): Flow<UserAccountEntity?> = accountDao.getActiveLoggedInUser()

    suspend fun getActiveLoggedInUserDirect(): UserAccountEntity? = withContext(Dispatchers.IO) {
        accountDao.getActiveLoggedInUserDirect()
    }

    suspend fun authenticateUser(username: String, passwordHash: String): UserAccountEntity? = withContext(Dispatchers.IO) {
        val user = accountDao.authenticate(username, passwordHash)
        if (user != null) {
            accountDao.logoutAllUsers()
            accountDao.setLoggedIn(user.id, System.currentTimeMillis())
        }
        user
    }

    /**
     * Đăng nhập theo cách cũ (so khớp trực tiếp chuỗi lưu trong DB).
     * Chỉ dùng làm fallback cho tài khoản được tạo từ bản chưa hash mật khẩu.
     */
    suspend fun authenticateLegacy(username: String, rawPassword: String): UserAccountEntity? = withContext(Dispatchers.IO) {
        accountDao.authenticate(username, rawPassword)
    }

    suspend fun updateAccountPassword(username: String, newPasswordHash: String) = withContext(Dispatchers.IO) {
        accountDao.updatePassword(username, newPasswordHash)
    }

    suspend fun registerUser(username: String, passwordHash: String): Boolean = withContext(Dispatchers.IO) {
        val exists = accountDao.isUsernameExists(username) > 0
        if (exists) {
            false
        } else {
            accountDao.logoutAllUsers()
            val newAccount = UserAccountEntity(
                username = username,
                passwordHash = passwordHash,
                createdAt = System.currentTimeMillis(),
                lastLoginAt = System.currentTimeMillis(),
                isLoggedIn = true
            )
            accountDao.registerUser(newAccount)
            profileDao.insertOrUpdateProfile(
                UserProfileEntity(
                    id = 1,
                    userName = username,
                    avatarEmoji = "🦉",
                    avatarBgColorHex = "#EEF2FF",
                    vipLevel = 1,
                    streakDays = 0,
                    maxStreakDays = 0,
                    totalPoints = 0,
                    totalCardsLearned = 0,
                    lastActiveTimestamp = System.currentTimeMillis()
                )
            )
            true
        }
    }

    suspend fun logoutUser() = withContext(Dispatchers.IO) {
        accountDao.logoutAllUsers()
    }

    // ==========================================
    // 8. STUDY SCHEDULE & REMINDERS (LỊCH HỌC & NHẮC NHỞ)
    // ==========================================
    fun getStudySchedule(): Flow<StudyScheduleEntity?> = scheduleDao.getSchedule()

    suspend fun getStudyScheduleDirect(): StudyScheduleEntity? = withContext(Dispatchers.IO) {
        scheduleDao.getScheduleDirect()
    }

    suspend fun saveStudySchedule(schedule: StudyScheduleEntity) = withContext(Dispatchers.IO) {
        scheduleDao.saveSchedule(schedule)
    }

    suspend fun updateReminderTime(hour: Int, minute: Int) = withContext(Dispatchers.IO) {
        scheduleDao.updateReminderTime(hour, minute)
    }

    suspend fun setReminderEnabled(enabled: Boolean) = withContext(Dispatchers.IO) {
        scheduleDao.setReminderEnabled(enabled)
    }
}
