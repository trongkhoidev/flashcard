package com.example.data.repository

import com.example.data.local.StudyScheduleDao
import com.example.data.local.UserSavedCardDao
import com.example.data.local.UserMasteredCardDao
import com.example.data.model.StudyScheduleEntity
import com.example.data.local.UserAccountDao
import com.example.data.model.UserAccountEntity
import com.example.data.model.UserSavedCardEntity
import com.example.data.model.UserMasteredCardEntity
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
import com.example.data.model.UserLeaderboardProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class FlashCardRepository(
    private val deckDao: DeckDao,
    private val cardDao: FlashCardDao,
    private val sessionDao: StudySessionDao,
    private val quizDao: QuizRecordDao,
    private val profileDao: UserProfileDao,
    private val languageDao: UserLanguageDao,
    private val accountDao: UserAccountDao,
    private val scheduleDao: StudyScheduleDao,
    private val userSavedCardDao: UserSavedCardDao,
    private val userMasteredCardDao: UserMasteredCardDao
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
        scheduleDao = database.studyScheduleDao(),
        userSavedCardDao = database.userSavedCardDao(),
        userMasteredCardDao = database.userMasteredCardDao()
    )

    suspend fun checkAndSeedDatabase() = withContext(Dispatchers.IO) {
        val count = cardDao.getTotalCardsCount().firstOrNull() ?: 0
        if (count == 0) {
            AppDatabase.populateInitialData(deckDao, cardDao, profileDao, languageDao)
        }
        val activeUser = accountDao.getActiveLoggedInUserDirect()
        if (activeUser != null) {
            syncUserStarredCards(activeUser.id)
            syncUserMasteredCards(activeUser.id)
        } else {
            syncUserStarredCards(null)
            syncUserMasteredCards(null)
        }
    }

    // ==========================================
    // 1. DECK (BỘ THẺ) QUERIES & OPERATIONS THUỘC TÀI KHOẢN
    // ==========================================
    fun getDecksByLanguage(langCode: String): Flow<List<DeckEntity>> = accountDao.getActiveLoggedInUser()
        .flatMapLatest { user ->
            val uid = user?.id ?: 1L
            deckDao.getDecksByLanguageForUser(langCode, uid)
        }

    fun getAllDecks(): Flow<List<DeckEntity>> = accountDao.getActiveLoggedInUser()
        .flatMapLatest { user ->
            val uid = user?.id ?: 1L
            deckDao.getAllDecksForUser(uid)
        }

    fun getCustomDecks(): Flow<List<DeckEntity>> = accountDao.getActiveLoggedInUser()
        .flatMapLatest { user ->
            val uid = user?.id ?: 1L
            deckDao.getCustomDecksForUser(uid)
        }

    fun getDecksByLevel(level: String): Flow<List<DeckEntity>> = accountDao.getActiveLoggedInUser()
        .flatMapLatest { user ->
            val uid = user?.id ?: 1L
            deckDao.getDecksByLevelForUser(level, uid)
        }

    fun searchDecks(query: String): Flow<List<DeckEntity>> = accountDao.getActiveLoggedInUser()
        .flatMapLatest { user ->
            val uid = user?.id ?: 1L
            deckDao.searchDecksForUser(query, uid)
        }

    suspend fun getDeckById(deckId: String): DeckEntity? = withContext(Dispatchers.IO) {
        deckDao.getDeckById(deckId)
    }

    fun getDeckByIdFlow(deckId: String): Flow<DeckEntity?> = deckDao.getDeckByIdFlow(deckId)

    suspend fun insertDeck(deck: DeckEntity) = withContext(Dispatchers.IO) {
        if (deck.isCustom && deck.userId == null) {
            val uid = accountDao.getActiveLoggedInUserDirect()?.id ?: 1L
            deckDao.insertDeck(deck.copy(userId = uid))
        } else {
            deckDao.insertDeck(deck)
        }
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

    suspend fun syncUserStarredCards(userId: Long?) = withContext(Dispatchers.IO) {
        cardDao.clearAllStarredFlags()
        if (userId != null && userId > 0L) {
            val savedCardIds = userSavedCardDao.getSavedCardIdsForUserDirect(userId)
            if (savedCardIds.isNotEmpty()) {
                cardDao.setStarredForCardIds(savedCardIds)
            }
        }
    }

    suspend fun syncUserMasteredCards(userId: Long?) = withContext(Dispatchers.IO) {
        cardDao.clearAllMasteredFlags()
        if (userId != null && userId > 0L) {
            val masteredCardIds = userMasteredCardDao.getMasteredCardIdsForUserDirect(userId)
            if (masteredCardIds.isNotEmpty()) {
                cardDao.setMasteredForCardIds(masteredCardIds)
            }
        }
    }

    suspend fun toggleStar(id: Long, currentStarred: Boolean) = withContext(Dispatchers.IO) {
        val activeUser = accountDao.getActiveLoggedInUserDirect()
        val userId = activeUser?.id ?: 1L
        val newStarred = !currentStarred
        if (newStarred) {
            userSavedCardDao.saveCard(UserSavedCardEntity(userId = userId, cardId = id))
        } else {
            userSavedCardDao.removeSavedCard(userId = userId, cardId = id)
        }
        cardDao.toggleStar(id, newStarred)
    }

    /**
     * Đánh dấu từ vựng ĐÃ THUỘC (Hoàn thành) khi người dùng trả lời đúng trong Quiz.
     * Chỉ tăng masteredCardsCount khi chuyển trạng thái CHƯA -> ĐÃ thuộc (tránh cộng dồn).
     */
    suspend fun markCardMastered(id: Long, langCode: String) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val card = cardDao.getCardById(id) ?: return@withContext
        val activeUser = accountDao.getActiveLoggedInUserDirect()
        val userId = activeUser?.id ?: 1L

        userMasteredCardDao.saveMasteredCard(UserMasteredCardEntity(userId = userId, cardId = id, masteredAt = now))
        if (!card.isMastered) {
            cardDao.recordReview(id = id, mastered = true, difficulty = 1, timestamp = now)
            languageDao.incrementMasteredCount(langCode, increment = 1, timestamp = now)
        } else {
            cardDao.updateReviewProgress(id = id, difficulty = 1, timestamp = now)
        }
    }

    /**
     * Đánh dấu từ vựng CHƯA THUỘC khi trả lời sai trong Quiz
     */
    suspend fun markCardUnmastered(id: Long) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val card = cardDao.getCardById(id) ?: return@withContext
        val activeUser = accountDao.getActiveLoggedInUserDirect()
        val userId = activeUser?.id ?: 1L

        userMasteredCardDao.removeMasteredCard(userId = userId, cardId = id)
        if (card.isMastered) {
            cardDao.recordReview(id = id, mastered = false, difficulty = 3, timestamp = now)
        } else {
            cardDao.updateReviewProgress(id = id, difficulty = 3, timestamp = now)
        }
    }

    /**
     * Nguồn quyết định CUỐI CÙNG trạng thái các thẻ sau một phiên Quiz:
     * - Trả lời ĐÚNG  -> isMastered = true
     * - Trả lời SAI    -> isMastered = false
     * Idempotent: chỉ tăng/giảm masteredCardsCount khi thực sự chuyển trạng thái.
     */
    suspend fun setCardsMasteredState(
        correctIds: List<Long>,
        wrongIds: List<Long>,
        langCode: String,
        timestamp: Long = System.currentTimeMillis()
    ) = withContext(Dispatchers.IO) {
        val activeUser = accountDao.getActiveLoggedInUserDirect()
        val userId = activeUser?.id ?: 1L

        var newlyMastered = 0
        correctIds.forEach { id ->
            val card = cardDao.getCardById(id) ?: return@forEach
            userMasteredCardDao.saveMasteredCard(UserMasteredCardEntity(userId = userId, cardId = id, masteredAt = timestamp))
            if (!card.isMastered) {
                cardDao.recordReview(id = id, mastered = true, difficulty = 1, timestamp = timestamp)
                newlyMastered++
            } else {
                cardDao.updateReviewProgress(id = id, difficulty = 1, timestamp = timestamp)
            }
        }
        var demoted = 0
        wrongIds.forEach { id ->
            val card = cardDao.getCardById(id) ?: return@forEach
            userMasteredCardDao.removeMasteredCard(userId = userId, cardId = id)
            if (card.isMastered) {
                cardDao.recordReview(id = id, mastered = false, difficulty = 3, timestamp = timestamp)
                demoted++
            } else {
                cardDao.updateReviewProgress(id = id, difficulty = 3, timestamp = timestamp)
            }
        }
        val netChange = newlyMastered - demoted
        if (netChange != 0) {
            languageDao.incrementMasteredCount(langCode, increment = netChange, timestamp = timestamp)
        }
    }

    /**
     * Ghi nhận lượt học flashcard trong màn Study — KHÔNG BAO GIỜ đổi isMastered.
     * "Đã thuộc" chỉ đến từ kết quả Quiz đúng.
     */
    suspend fun recordCardReview(id: Long, difficulty: Int) = withContext(Dispatchers.IO) {
        cardDao.updateReviewProgress(
            id = id,
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
    // 3. MULTI-LANGUAGE LEARNING (ĐA NGÔN NGỮ HỌC TẬP THUỘC TÀI KHOẢN)
    // ==========================================
    fun getAllLearningLanguages(): Flow<List<UserLanguageEntity>> = accountDao.getActiveLoggedInUser()
        .flatMapLatest { user ->
            val uid = user?.id ?: 1L
            languageDao.getAllLearningLanguagesForUser(uid)
        }

    fun getActiveLearningLanguage(): Flow<UserLanguageEntity?> = accountDao.getActiveLoggedInUser()
        .flatMapLatest { user ->
            val uid = user?.id ?: 1L
            languageDao.getActiveLearningLanguageForUser(uid)
        }

    fun getEnrolledLanguageCodes(): Flow<List<String>> = accountDao.getActiveLoggedInUser()
        .flatMapLatest { user ->
            val uid = user?.id ?: 1L
            languageDao.getEnrolledLanguageCodesForUser(uid)
        }

    fun isLanguageEnrolled(code: String): Flow<Boolean> = accountDao.getActiveLoggedInUser()
        .flatMapLatest { user ->
            val uid = user?.id ?: 1L
            languageDao.isLanguageEnrolledForUser(uid, code)
        }

    suspend fun addLearningLanguage(language: AppLanguage) = withContext(Dispatchers.IO) {
        val uid = accountDao.getActiveLoggedInUserDirect()?.id ?: 1L
        val existing = languageDao.getLanguageDirectForUser(uid, language.code)
        if (existing == null) {
            val currentLangs = languageDao.getAllLearningLanguagesForUser(uid).firstOrNull() ?: emptyList()
            val isFirst = currentLangs.isEmpty()
            languageDao.insertLanguage(
                UserLanguageEntity(
                    userId = uid,
                    languageCode = language.code,
                    displayName = language.displayName,
                    flagEmoji = language.flagEmoji,
                    isCurrentActive = isFirst,
                    dailyGoalCards = 20,
                    masteredCardsCount = 0,
                    totalWordsEnrolled = 50,
                    level = "Mới bắt đầu"
                )
            )
            if (isFirst) {
                languageDao.switchActiveLanguageForUser(uid, language.code)
            }
        }
    }

    suspend fun ensureInitialLanguageForUser(userId: Long) = withContext(Dispatchers.IO) {
        val currentLangs = languageDao.getAllLearningLanguagesForUser(userId).firstOrNull() ?: emptyList()
        if (currentLangs.isEmpty()) {
            languageDao.insertLanguage(
                UserLanguageEntity(
                    userId = userId,
                    languageCode = AppLanguage.ENGLISH.code,
                    displayName = AppLanguage.ENGLISH.displayName,
                    flagEmoji = AppLanguage.ENGLISH.flagEmoji,
                    isCurrentActive = true,
                    dailyGoalCards = 20,
                    masteredCardsCount = 0,
                    totalWordsEnrolled = 50,
                    level = "Cơ bản"
                )
            )
        }
    }

    suspend fun switchActiveLanguage(languageCode: String) = withContext(Dispatchers.IO) {
        val uid = accountDao.getActiveLoggedInUserDirect()?.id ?: 1L
        languageDao.switchActiveLanguageForUser(uid, languageCode)
    }

    suspend fun updateLanguageDailyGoal(languageCode: String, goal: Int) = withContext(Dispatchers.IO) {
        val uid = accountDao.getActiveLoggedInUserDirect()?.id ?: 1L
        languageDao.updateDailyGoalForUser(uid, languageCode, goal)
    }

    suspend fun deleteLearningLanguage(languageCode: String) = withContext(Dispatchers.IO) {
        val uid = accountDao.getActiveLoggedInUserDirect()?.id ?: 1L
        languageDao.deleteLanguageByCodeForUser(uid, languageCode)
    }

    // ==========================================
    // 4. STUDY SESSIONS (LỊCH SỬ ÔN TẬP THUỘC TÀI KHOẢN)
    // ==========================================
    fun getAllStudySessions(): Flow<List<StudySessionEntity>> = accountDao.getActiveLoggedInUser()
        .flatMapLatest { user ->
            val uid = user?.id ?: 1L
            sessionDao.getAllSessionsForUser(uid)
        }

    fun getRecentStudySessions(limit: Int = 10): Flow<List<StudySessionEntity>> = accountDao.getActiveLoggedInUser()
        .flatMapLatest { user ->
            val uid = user?.id ?: 1L
            sessionDao.getRecentSessionsForUser(uid, limit)
        }

    fun getLastStudySession(): Flow<StudySessionEntity?> = accountDao.getActiveLoggedInUser()
        .flatMapLatest { user ->
            val uid = user?.id ?: 1L
            sessionDao.getLastStudySessionForUser(uid)
        }

    // Tiến trình thật của từng deck (mastered chỉ tính từ Quiz trả lời đúng)
    fun getAllDecksWithStats(): Flow<List<com.example.data.model.DeckWithStats>> = accountDao.getActiveLoggedInUser()
        .flatMapLatest { user ->
            val uid = user?.id ?: 1L
            cardDao.getAllDecksWithStatsForUser(uid)
        }

    fun getSessionsForDeck(deckId: String): Flow<List<StudySessionEntity>> = sessionDao.getSessionsForDeck(deckId)

    fun getTotalCardsStudiedCount(): Flow<Int?> = sessionDao.getTotalCardsStudied()

    fun getTotalStudyTimeSeconds(): Flow<Long?> = sessionDao.getTotalStudyTimeSeconds()

    suspend fun recordStudySession(session: StudySessionEntity): Long = withContext(Dispatchers.IO) {
        val uid = accountDao.getActiveLoggedInUserDirect()?.id ?: 1L
        sessionDao.insertSession(session.copy(userId = uid))
    }

    suspend fun clearStudyHistory() = withContext(Dispatchers.IO) {
        sessionDao.clearAllSessions()
    }

    // ==========================================
    // 5. QUIZ & GAME RECORDS (LỊCH SỬ THI & ĐIỂM SỐ THUỘC TÀI KHOẢN)
    // ==========================================
    fun getAllQuizRecords(): Flow<List<QuizRecordEntity>> = accountDao.getActiveLoggedInUser()
        .flatMapLatest { user ->
            val uid = user?.id ?: 1L
            quizDao.getAllRecordsForUser(uid)
        }

    fun getRecentQuizRecords(limit: Int = 10): Flow<List<QuizRecordEntity>> = accountDao.getActiveLoggedInUser()
        .flatMapLatest { user ->
            val uid = user?.id ?: 1L
            quizDao.getRecentRecordsForUser(uid, limit)
        }

    fun getQuizRecordsByMode(mode: String): Flow<List<QuizRecordEntity>> = accountDao.getActiveLoggedInUser()
        .flatMapLatest { user ->
            val uid = user?.id ?: 1L
            quizDao.getRecordsByModeForUser(uid, mode)
        }

    fun getHighestQuizScoreForDeck(deckId: String): Flow<Int?> = accountDao.getActiveLoggedInUser()
        .flatMapLatest { user ->
            val uid = user?.id ?: 1L
            quizDao.getHighestScoreForDeckForUser(uid, deckId)
        }

    fun getTotalQuizPointsEarned(): Flow<Int?> = accountDao.getActiveLoggedInUser()
        .flatMapLatest { user ->
            val uid = user?.id ?: 1L
            quizDao.getTotalPointsEarnedForUser(uid)
        }

    // Tổng điểm kiếm được từ mốc thời gian thuộc tài khoản (BXH Tuần / Tháng)
    fun getPointsEarnedSince(since: Long): Flow<Int> = accountDao.getActiveLoggedInUser()
        .flatMapLatest { user ->
            val uid = user?.id ?: 1L
            quizDao.getPointsSinceForUser(uid, since)
        }

    fun getHighestStreakRecord(): Flow<Int?> = accountDao.getActiveLoggedInUser()
        .flatMapLatest { user ->
            val uid = user?.id ?: 1L
            quizDao.getHighestStreakForUser(uid)
        }

    fun getTotalGamesPlayedCount(): Flow<Int> = accountDao.getActiveLoggedInUser()
        .flatMapLatest { user ->
            val uid = user?.id ?: 1L
            quizDao.getTotalGamesPlayedForUser(uid)
        }

    suspend fun recordQuizResult(record: QuizRecordEntity): Long = withContext(Dispatchers.IO) {
        val uid = accountDao.getActiveLoggedInUserDirect()?.id ?: 1L
        quizDao.insertRecord(record.copy(userId = uid))
    }

    suspend fun clearQuizHistory() = withContext(Dispatchers.IO) {
        quizDao.clearQuizHistory()
    }

    // ==========================================
    // 6. USER PROFILE & STREAK (HỒ SƠ & TIẾN ĐỘ)
    // ==========================================
    fun getUserProfile(): Flow<UserProfileEntity?> = accountDao.getActiveLoggedInUser()
        .flatMapLatest { activeUser ->
            val uid = activeUser?.id ?: 1L
            profileDao.getUserProfileById(uid)
        }

    fun getOtherUserProfiles(): Flow<List<UserLeaderboardProfile>> = accountDao.getActiveLoggedInUser()
        .flatMapLatest { activeUser ->
            val uid = activeUser?.id ?: 1L
            profileDao.getOtherUserProfiles(uid)
        }
        .map { profiles ->
            val now = System.currentTimeMillis()
            val startOfWeek = startOfWeekMillis(now)
            val startOfMonth = startOfMonthMillis(now)
            profiles.map { profile ->
                val pid = profile.id.toLong()
                val weeklyFromQuiz = quizDao.getPointsSinceForUserDirect(pid, startOfWeek)
                val monthlyFromQuiz = quizDao.getPointsSinceForUserDirect(pid, startOfMonth)

                // Nếu tài khoản có làm quiz trong tuần/tháng, dùng điểm quiz thực tế.
                // Nếu chưa có quiz record nhưng totalPoints > 0, bảo toàn totalPoints không bị giảm vô lý.
                val weekly = if (weeklyFromQuiz > 0) weeklyFromQuiz else profile.totalPoints
                val monthly = if (monthlyFromQuiz > 0) monthlyFromQuiz else profile.totalPoints

                UserLeaderboardProfile(
                    id = profile.id,
                    userName = profile.userName,
                    avatarEmoji = profile.avatarEmoji,
                    avatarBgColorHex = profile.avatarBgColorHex,
                    vipLevel = profile.vipLevel,
                    streakDays = profile.streakDays,
                    maxStreakDays = profile.maxStreakDays,
                    totalPoints = profile.totalPoints,
                    weeklyPoints = weekly,
                    monthlyPoints = monthly,
                    totalCardsLearned = profile.totalCardsLearned,
                    lastActiveTimestamp = profile.lastActiveTimestamp
                )
            }
        }

    suspend fun getUserProfileDirect(): UserProfileEntity? = withContext(Dispatchers.IO) {
        val uid = accountDao.getActiveLoggedInUserDirect()?.id ?: 1L
        profileDao.getUserProfileByIdDirect(uid)
    }

    suspend fun saveUserProfile(profile: UserProfileEntity) = withContext(Dispatchers.IO) {
        profileDao.insertOrUpdateProfile(profile)
    }

    suspend fun updateUserName(name: String) = withContext(Dispatchers.IO) {
        val uid = accountDao.getActiveLoggedInUserDirect()?.id ?: 1L
        profileDao.updateNameForUser(uid, name)
    }

    suspend fun updateUserVipLevel(vipLevel: Int) = withContext(Dispatchers.IO) {
        val uid = accountDao.getActiveLoggedInUserDirect()?.id ?: 1L
        profileDao.updateVipLevelForUser(uid, vipLevel)
    }

    suspend fun updateUserAvatar(emoji: String, bgColorHex: String) = withContext(Dispatchers.IO) {
        val uid = accountDao.getActiveLoggedInUserDirect()?.id ?: 1L
        profileDao.updateAvatarForUser(uid, emoji, bgColorHex)
    }

    suspend fun updateStreak(streakDays: Int) = withContext(Dispatchers.IO) {
        val uid = accountDao.getActiveLoggedInUserDirect()?.id ?: 1L
        profileDao.updateStreakForUser(uid, streakDays, System.currentTimeMillis())
    }

    /**
     * Cập nhật chuỗi ngày học theo ngày thật:
     * - Học trong cùng ngày -> giữ nguyên (tránh cộng dồn nhiều lần/ngày)
     * - Học liên tục sang hôm sau -> streak + 1
     * - Bỏ học >= 2 ngày -> reset về 1
     */
    suspend fun updateDailyStreakIfNeeded(now: Long = System.currentTimeMillis()) = withContext(Dispatchers.IO) {
        val uid = accountDao.getActiveLoggedInUserDirect()?.id ?: 1L
        val profile = profileDao.getUserProfileByIdDirect(uid) ?: return@withContext
        val dayDiff = ((startOfDayMillis(now) - startOfDayMillis(profile.lastActiveTimestamp)) / MILLIS_PER_DAY).toInt()
        val newStreak = when {
            dayDiff <= 0 -> return@withContext
            dayDiff == 1 -> profile.streakDays + 1
            else -> 1
        }
        profileDao.updateStreakForUser(uid, newStreak, now)
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

    private fun startOfWeekMillis(now: Long = System.currentTimeMillis()): Long {
        val cal = java.util.Calendar.getInstance()
        cal.timeInMillis = now
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        val dayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK)
        cal.add(java.util.Calendar.DAY_OF_YEAR, if (dayOfWeek == java.util.Calendar.SUNDAY) -6 else -(dayOfWeek - 2))
        return cal.timeInMillis
    }

    private fun startOfMonthMillis(now: Long = System.currentTimeMillis()): Long {
        val cal = java.util.Calendar.getInstance()
        cal.timeInMillis = now
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1)
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    suspend fun getDailyQuizAttemptsForDeck(deckId: String, now: Long = System.currentTimeMillis()): Int = withContext(Dispatchers.IO) {
        val uid = accountDao.getActiveLoggedInUserDirect()?.id ?: 1L
        val startToday = startOfDayMillis(now)
        quizDao.getQuizCountForDeckSinceForUser(uid, deckId, startToday)
    }

    suspend fun addPoints(points: Int) = withContext(Dispatchers.IO) {
        val uid = accountDao.getActiveLoggedInUserDirect()?.id ?: 1L
        profileDao.addPointsForUser(uid, points)
    }

    suspend fun incrementCardsLearned(count: Int) = withContext(Dispatchers.IO) {
        val uid = accountDao.getActiveLoggedInUserDirect()?.id ?: 1L
        profileDao.incrementCardsLearnedForUser(uid, count)
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
            var profile = profileDao.getUserProfileByIdDirect(user.id)
            if (profile == null) {
                profile = UserProfileEntity(
                    id = user.id.toInt(),
                    userName = user.username,
                    avatarEmoji = "🦉",
                    avatarBgColorHex = "#EEF2FF",
                    vipLevel = 1,
                    streakDays = 0,
                    maxStreakDays = 0,
                    totalPoints = 0,
                    totalCardsLearned = 0,
                    lastActiveTimestamp = System.currentTimeMillis()
                )
                profileDao.insertOrUpdateProfile(profile)
            }
            ensureInitialLanguageForUser(user.id)
            syncUserStarredCards(user.id)
            syncUserMasteredCards(user.id)
        }
        user
    }

    /**
     * Đăng nhập theo cách cũ (so khớp trực tiếp chuỗi lưu trong DB).
     * Chỉ dùng làm fallback cho tài khoản được tạo từ bản chưa hash mật khẩu.
     */
    suspend fun authenticateLegacy(username: String, rawPassword: String): UserAccountEntity? = withContext(Dispatchers.IO) {
        val user = accountDao.authenticate(username, rawPassword)
        if (user != null) {
            accountDao.logoutAllUsers()
            accountDao.setLoggedIn(user.id, System.currentTimeMillis())
            var profile = profileDao.getUserProfileByIdDirect(user.id)
            if (profile == null) {
                profile = UserProfileEntity(
                    id = user.id.toInt(),
                    userName = user.username,
                    avatarEmoji = "🦉",
                    avatarBgColorHex = "#EEF2FF",
                    vipLevel = 1,
                    streakDays = 0,
                    maxStreakDays = 0,
                    totalPoints = 0,
                    totalCardsLearned = 0,
                    lastActiveTimestamp = System.currentTimeMillis()
                )
                profileDao.insertOrUpdateProfile(profile)
            }
            ensureInitialLanguageForUser(user.id)
            syncUserStarredCards(user.id)
            syncUserMasteredCards(user.id)
        }
        user
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
            val newId = accountDao.registerUser(newAccount)
            profileDao.insertOrUpdateProfile(
                UserProfileEntity(
                    id = newId.toInt(),
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
            ensureInitialLanguageForUser(newId)
            syncUserStarredCards(newId)
            syncUserMasteredCards(newId)
            true
        }
    }

    suspend fun logoutUser() = withContext(Dispatchers.IO) {
        accountDao.logoutAllUsers()
        syncUserStarredCards(null)
        syncUserMasteredCards(null)
    }

    // ==========================================
    // 8. STUDY SCHEDULE & REMINDERS (LỊCH HỌC & NHẮC NHỞ THEO TÀI KHOẢN)
    // ==========================================
    fun getStudySchedule(): Flow<StudyScheduleEntity?> = scheduleDao.getSchedule()

    suspend fun getStudyScheduleDirect(): StudyScheduleEntity? = withContext(Dispatchers.IO) {
        scheduleDao.getScheduleDirect()
    }

    suspend fun saveStudySchedule(schedule: StudyScheduleEntity) = withContext(Dispatchers.IO) {
        scheduleDao.saveSchedule(schedule.copy(id = 1))
    }

    suspend fun updateReminderTime(hour: Int, minute: Int) = withContext(Dispatchers.IO) {
        val existing = scheduleDao.getScheduleDirect()
        if (existing == null) {
            scheduleDao.saveSchedule(
                StudyScheduleEntity(
                    id = 1,
                    isEnabled = true,
                    reminderHour = hour,
                    reminderMinute = minute
                )
            )
        } else {
            scheduleDao.updateReminderTime(hour, minute)
        }
    }

    suspend fun setReminderEnabled(enabled: Boolean) = withContext(Dispatchers.IO) {
        val existing = scheduleDao.getScheduleDirect()
        if (existing == null) {
            scheduleDao.saveSchedule(
                StudyScheduleEntity(
                    id = 1,
                    isEnabled = enabled
                )
            )
        } else {
            scheduleDao.setReminderEnabled(enabled)
        }
    }
}
