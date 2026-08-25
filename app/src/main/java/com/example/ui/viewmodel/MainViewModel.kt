package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.TTSManager
import com.example.data.local.AppDatabase
import com.example.data.model.AppLanguage
import com.example.data.model.DeckEntity
import com.example.data.model.FlashCardEntity
import com.example.data.repository.FlashCardRepository
import com.example.data.model.UserProfileEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class ScreenState {
    object Welcome : ScreenState()
    object Login : ScreenState()
    object Register : ScreenState()
    object Onboarding : ScreenState()
    object Home : ScreenState()
    data class DeckDetail(val deck: DeckEntity, val cards: List<FlashCardEntity>) : ScreenState()
    data class Study(val deck: DeckEntity, val cards: List<FlashCardEntity>) : ScreenState()
    data class Quiz(val deck: DeckEntity, val cards: List<FlashCardEntity>) : ScreenState()
    data class Match(val deck: DeckEntity, val cards: List<FlashCardEntity>) : ScreenState()
    data class Starred(val cards: List<FlashCardEntity>) : ScreenState()
    data class OnboardingTrialStudy(val language: AppLanguage, val cards: List<FlashCardEntity>) : ScreenState()
    data class OnboardingTrialQuiz(val language: AppLanguage, val cards: List<FlashCardEntity>) : ScreenState()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database: AppDatabase = AppDatabase.getDatabase(application)
    private val repository: FlashCardRepository = FlashCardRepository(database)
    private val ttsManager: TTSManager = TTSManager(application)

    private val _currentScreen = MutableStateFlow<ScreenState>(ScreenState.Welcome)
    val currentScreen: StateFlow<ScreenState> = _currentScreen.asStateFlow()

    private val _selectedLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val selectedLanguage: StateFlow<AppLanguage> = _selectedLanguage.asStateFlow()

    private val _learningLanguages = MutableStateFlow<List<AppLanguage>>(listOf(AppLanguage.ENGLISH))
    val learningLanguages: StateFlow<List<AppLanguage>> = _learningLanguages.asStateFlow()

    val learningLanguagesFromDb: StateFlow<List<com.example.data.model.UserLanguageEntity>> = repository.getAllLearningLanguages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dueCardsForCurrentLanguage: StateFlow<List<FlashCardEntity>> = _selectedLanguage
        .flatMapLatest { lang -> repository.getDueCardsForLanguage(lang.code) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dueCountForCurrentLanguage: StateFlow<Int> = _selectedLanguage
        .flatMapLatest { lang -> repository.getDueCountForLanguage(lang.code) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val starterCardsForCurrentLanguage: StateFlow<List<FlashCardEntity>> = _selectedLanguage
        .flatMapLatest { lang -> repository.getStarterCardsForLanguage(lang.code) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val masteredCountForCurrentLanguage: StateFlow<Int> = _selectedLanguage
        .flatMapLatest { lang -> repository.getMasteredCountByLanguage(lang.code) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val userProfile: StateFlow<UserProfileEntity?> = repository.getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val userName: StateFlow<String> = userProfile
        .map { it?.userName ?: "Tuấn Nguyễn" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Tuấn Nguyễn")

    val userVipLevel: StateFlow<Int> = userProfile
        .map { it?.vipLevel ?: 3 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 3)

    val streakDays: StateFlow<Int> = userProfile
        .map { it?.streakDays ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val userTotalPoints: StateFlow<Int> = userProfile
        .map { it?.totalPoints ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val studySchedule: StateFlow<com.example.data.model.StudyScheduleEntity?> = repository.getStudySchedule()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val smartNotificationEngine = com.example.notification.SmartNotificationEngine(application)

    private val _notificationPreview = MutableStateFlow<com.example.notification.NotificationPreviewEvent?>(null)
    val notificationPreview: StateFlow<com.example.notification.NotificationPreviewEvent?> = _notificationPreview.asStateFlow()

    private var _pendingTrialLanguage: AppLanguage? = null

    fun dismissNotificationPreview() {
        _notificationPreview.value = null
    }

    suspend fun authenticateUser(username: String, passwordHash: String): Boolean {
        val user = repository.authenticateUser(username, com.example.data.local.PasswordHasher.sha256(passwordHash))
        if (user != null) {
            _pendingTrialLanguage = null
            repository.updateUserName(user.username)
            applyActiveLanguageFromDb()
            return true
        }
        return false
    }

    suspend fun registerUser(username: String, passwordHash: String): Boolean {
        return repository.registerUser(username, com.example.data.local.PasswordHasher.sha256(passwordHash))
    }

    suspend fun logoutUser() {
        repository.logoutUser()
    }

    fun logoutAndReturnToWelcome() {
        viewModelScope.launch { repository.logoutUser() }
        _selectedLanguage.value = AppLanguage.ENGLISH
        _learningLanguages.value = listOf(AppLanguage.ENGLISH)
        _currentScreen.value = ScreenState.Welcome
    }

    val decksForCurrentLanguage: StateFlow<List<DeckEntity>> = _selectedLanguage
        .flatMapLatest { lang -> repository.getDecksByLanguage(lang.code) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDecks: StateFlow<List<DeckEntity>> = repository.getAllDecks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val starredCardsList: StateFlow<List<FlashCardEntity>> = repository.getStarredCards()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCardsList: StateFlow<List<FlashCardEntity>> = repository.getAllCards()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val masteredCount: StateFlow<Int> = repository.getMasteredCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalCardsCount: StateFlow<Int> = repository.getTotalCardsCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        viewModelScope.launch {
            repository.checkAndSeedDatabase()
            val activeUser = repository.getActiveLoggedInUserDirect()
            if (activeUser != null) {
                applyActiveLanguageFromDb()
                _currentScreen.value = ScreenState.Home
            }
            com.example.widget.VocabularyStreakWidgetProvider.updateAllWidgets(getApplication(), streakDays.value)

            // Khởi tạo lịch học AlarmManager thông minh
            com.example.notification.StudyAlarmScheduler.scheduleStudyAlarm(
                getApplication(),
                com.example.data.model.StudySchedule()
            )
        }
    }

    /**
     * Khôi phục "thứ đang học": đọc danh sách ngôn ngữ theo học từ DB,
     * chọn ngôn ngữ có cờ isCurrentActive làm ngôn ngữ hiện tại.
     */
    private suspend fun applyActiveLanguageFromDb() {
        val langs = learningLanguagesFromDb.first()
        if (langs.isEmpty()) return
        val active = langs.firstOrNull { it.isCurrentActive } ?: langs.first()
        _selectedLanguage.value = AppLanguage.fromCode(active.languageCode)
        _learningLanguages.value = langs.map { AppLanguage.fromCode(it.languageCode) }
    }

    fun navigateTo(screen: ScreenState) {
        _currentScreen.value = screen
    }

    fun setInitialLearningLanguage(language: AppLanguage) {
        _selectedLanguage.value = language
        _learningLanguages.value = listOf(language)
        viewModelScope.launch {
            repository.addLearningLanguage(language)
            repository.switchActiveLanguage(language.code)
        }
    }

    fun selectLanguage(language: AppLanguage) {
        _selectedLanguage.value = language
        if (!_learningLanguages.value.contains(language)) {
            _learningLanguages.value = _learningLanguages.value + language
        }
        viewModelScope.launch {
            repository.switchActiveLanguage(language.code)
        }
    }

    fun addLearningLanguage(language: AppLanguage) {
        if (!_learningLanguages.value.contains(language)) {
            _learningLanguages.value = _learningLanguages.value + language
        }
        _selectedLanguage.value = language
        viewModelScope.launch {
            repository.addLearningLanguage(language)
            repository.switchActiveLanguage(language.code)
        }
    }

    fun updateStudySchedule(reminderHour: Int, reminderMinute: Int = 0) {
        viewModelScope.launch {
            val existing = repository.getStudyScheduleDirect()
            if (existing == null) {
                repository.saveStudySchedule(
                    com.example.data.model.StudyScheduleEntity(
                        isEnabled = true,
                        reminderHour = reminderHour,
                        reminderMinute = reminderMinute
                    )
                )
            } else {
                repository.updateReminderTime(reminderHour, reminderMinute)
            }
            val schedule = com.example.data.model.StudySchedule(
                isEnabled = true,
                reminderHour = reminderHour,
                reminderMinute = reminderMinute
            )
            com.example.notification.StudyAlarmScheduler.scheduleStudyAlarm(
                getApplication(),
                schedule
            )
        }
    }

    fun updateUserName(name: String) {
        viewModelScope.launch {
            repository.updateUserName(name)
        }
    }

    fun updateUserVipLevel(level: Int) {
        viewModelScope.launch {
            repository.updateUserVipLevel(level)
        }
    }

    fun speak(text: String, languageTag: String = "en-US") {
        ttsManager.speak(text, languageTag)
    }

    fun toggleStar(cardId: Long, currentStarred: Boolean) {
        viewModelScope.launch {
            repository.toggleStar(cardId, currentStarred)
        }
    }

    fun recordReview(cardId: Long, difficulty: Int) {
        viewModelScope.launch {
            repository.recordCardReview(cardId, difficulty)
            com.example.widget.VocabularyStreakWidgetProvider.updateAllWidgets(getApplication(), streakDays.value)
        }
    }

    fun completeStudySession(deckId: String, deckTitle: String, langCode: String, cardsStudied: Int, masteredCount: Int, durationSecs: Int) {
        viewModelScope.launch {
            repository.recordStudySession(
                com.example.data.model.StudySessionEntity(
                    deckId = deckId,
                    deckTitle = deckTitle,
                    languageCode = langCode,
                    cardsStudied = cardsStudied,
                    masteredCount = masteredCount,
                    durationSeconds = durationSecs,
                    timestamp = System.currentTimeMillis()
                )
            )
            repository.updateDailyStreakIfNeeded()
        }
    }

    fun markCardMastered(cardId: Long, langCode: String) {
        viewModelScope.launch {
            repository.markCardMastered(cardId, langCode)
            com.example.widget.VocabularyStreakWidgetProvider.updateAllWidgets(getApplication(), streakDays.value)
        }
    }

    fun markCardUnmastered(cardId: Long) {
        viewModelScope.launch {
            repository.markCardUnmastered(cardId)
        }
    }

    /**
     * Xử lý hoàn tất bài Quiz:
     * 1. Đánh dấu các từ trả lời SAI là "Chưa thuộc" (isMastered = false)
     * 2. Tự động tạo/cập nhật 1 bộ thẻ "Từ chưa thuộc" cho bộ từ vựng đó để người dùng học lại
     * 3. Lưu lịch sử Quiz và phiên học
     */
    fun processQuizResult(
        deck: DeckEntity,
        score: Int,
        total: Int,
        wrongCards: List<FlashCardEntity>,
        durationSecs: Int = 90
    ) {
        viewModelScope.launch {
            // 1. Lưu phiên học
            repository.recordStudySession(
                com.example.data.model.StudySessionEntity(
                    deckId = deck.id,
                    deckTitle = deck.title,
                    languageCode = deck.languageCode,
                    cardsStudied = total,
                    masteredCount = score,
                    durationSeconds = durationSecs,
                    timestamp = System.currentTimeMillis()
                )
            )

            // 2. Lưu kết quả Quiz vào lịch sử
            val accuracy = if (total > 0) (score.toFloat() / total.toFloat()) * 100f else 0f
            repository.recordQuizResult(
                com.example.data.model.QuizRecordEntity(
                    deckId = deck.id,
                    deckTitle = deck.title,
                    mode = "QUIZ",
                    score = score,
                    totalQuestions = total,
                    pointsEarned = score * 100,
                    maxStreak = score,
                    accuracyPercent = accuracy,
                    timeSpentSeconds = durationSecs,
                    timestamp = System.currentTimeMillis()
                )
            )

            // 3. Đánh dấu các từ SAI là chưa thuộc trong database
            wrongCards.forEach { card ->
                repository.markCardUnmastered(card.id)
            }

            // 4. Nếu có từ sai, tự động tạo / cập nhật bộ thẻ "Từ chưa thuộc" trong CSDL
            if (wrongCards.isNotEmpty()) {
                val unmasteredDeckId = "unmastered_${deck.languageCode}_${deck.id}"
                val unmasteredDeckTitle = "⚠️ Cần ôn: ${deck.title}"
                val unmasteredDeck = DeckEntity(
                    id = unmasteredDeckId,
                    languageCode = deck.languageCode,
                    title = unmasteredDeckTitle,
                    subtitle = "Bộ ôn tập gồm ${wrongCards.size} từ làm sai trong bài kiểm tra",
                    iconEmoji = "⚠️",
                    level = "Chưa thuộc",
                    colorHex = "#EF4444",
                    cardCount = wrongCards.size,
                    isCustom = true
                )
                repository.insertDeck(unmasteredDeck)

                // Làm mới danh sách thẻ của bộ chưa thuộc
                repository.deleteCardsByDeckId(unmasteredDeckId)
                val newCardsForUnmasteredDeck = wrongCards.map { card ->
                    card.copy(
                        id = 0L,
                        deckId = unmasteredDeckId,
                        isMastered = false,
                        difficulty = 3,
                        srsRepetitions = 0,
                        srsInterval = 1,
                        nextReviewTimestamp = System.currentTimeMillis()
                    )
                }
                repository.insertCards(newCardsForUnmasteredDeck)
            }

            // 5. Cập nhật điểm, streak theo ngày & Widget
            repository.addPoints(score * 100)
            repository.updateDailyStreakIfNeeded()
            com.example.widget.VocabularyStreakWidgetProvider.updateAllWidgets(getApplication(), streakDays.value)
        }
    }

    fun startStudyUnmasteredDeck(deck: DeckEntity, wrongCards: List<FlashCardEntity>) {
        val unmasteredDeck = DeckEntity(
            id = "unmastered_${deck.languageCode}_${deck.id}",
            languageCode = deck.languageCode,
            title = "⚠️ Cần ôn: ${deck.title}",
            subtitle = "Bộ ôn tập gồm ${wrongCards.size} từ chưa thuộc",
            iconEmoji = "⚠️",
            level = "Chưa thuộc",
            colorHex = "#EF4444",
            cardCount = wrongCards.size,
            isCustom = true
        )
        _currentScreen.value = ScreenState.Study(unmasteredDeck, wrongCards)
    }

    fun triggerSmartNotificationTest() {
        viewModelScope.launch {
            smartNotificationEngine.evaluateAndSendSmartNotification(
                isForcedTest = true,
                onPreviewGenerated = { event ->
                    _notificationPreview.value = event
                }
            )
        }
    }

    fun triggerAchievementTest(streak: Int) {
        smartNotificationEngine.checkAndNotifyStreakMilestone(
            newStreak = streak,
            onPreviewGenerated = { event ->
                _notificationPreview.value = event
            }
        )
    }

    fun snoozeStudyReminderToday() {
        com.example.notification.NotificationHelper.setSnoozedToday(getApplication())
        _notificationPreview.value = null
    }

    fun createNewDeck(deck: DeckEntity) {
        viewModelScope.launch {
            repository.insertDeck(deck)
        }
    }

    fun createNewDeckWithCards(deck: DeckEntity, selectedCards: List<FlashCardEntity>) {
        viewModelScope.launch {
            val deckWithCount = deck.copy(cardCount = selectedCards.size)
            repository.insertDeck(deckWithCount)
            
            val newCards = selectedCards.map { card ->
                FlashCardEntity(
                    deckId = deck.id,
                    languageCode = deck.languageCode,
                    frontWord = card.frontWord,
                    phonetic = card.phonetic,
                    partOfSpeech = card.partOfSpeech,
                    frontExample = card.frontExample,
                    backMeaning = card.backMeaning,
                    backExampleTranslation = card.backExampleTranslation,
                    memoryTip = card.memoryTip,
                    difficulty = card.difficulty,
                    isStarred = card.isStarred,
                    isMastered = card.isMastered,
                    reviewCount = card.reviewCount,
                    lastReviewedTimestamp = card.lastReviewedTimestamp
                )
            }
            repository.insertCards(newCards)
        }
    }

    fun createNewCard(card: FlashCardEntity) {
        viewModelScope.launch {
            repository.insertCard(card)
        }
    }

    fun importCards(cards: List<FlashCardEntity>) {
        viewModelScope.launch {
            repository.insertCards(cards)
        }
    }

    fun startStudyByLanguage(langCode: String) {
        viewModelScope.launch {
            val list = repository.getDecksByLanguage(langCode).first()
            val targetDeck = list.firstOrNull()
            if (targetDeck != null) {
                startStudyDeck(targetDeck)
            }
        }
    }

    fun openDeckDetail(deck: DeckEntity) {
        viewModelScope.launch {
            val cards = repository.getCardsForDeck(deck.id).first()
            _currentScreen.value = ScreenState.DeckDetail(deck, cards)
        }
    }

    fun startStudyDeck(deck: DeckEntity) {
        viewModelScope.launch {
            val cards = repository.getCardsForDeck(deck.id).first()
            _currentScreen.value = ScreenState.Study(deck, cards)
        }
    }

    fun startQuizDeck(deck: DeckEntity) {
        viewModelScope.launch {
            val cards = repository.getCardsForDeck(deck.id).first()
            _currentScreen.value = ScreenState.Quiz(deck, cards)
        }
    }

    fun startMatchDeck(deck: DeckEntity) {
        viewModelScope.launch {
            val cards = repository.getCardsForDeck(deck.id).first()
            _currentScreen.value = ScreenState.Match(deck, cards)
        }
    }

    fun openStarredCards() {
        viewModelScope.launch {
            val cards = repository.getStarredCards().first()
            _currentScreen.value = ScreenState.Starred(cards)
        }
    }

    fun startStudySavedCards(cards: List<FlashCardEntity>, title: String = "Từ vựng đã lưu", langCode: String = "en") {
        val customDeck = DeckEntity(
            id = "starred_study_${System.currentTimeMillis()}",
            languageCode = langCode,
            title = title,
            subtitle = "Tiếp tục học ${cards.size} từ đã lưu",
            iconEmoji = "⭐",
            level = "Đã lưu",
            colorHex = "#0284C7",
            cardCount = cards.size
        )
        _currentScreen.value = ScreenState.Study(customDeck, cards)
    }

    fun startQuizSavedCards(cards: List<FlashCardEntity>, title: String = "Trắc nghiệm từ đã lưu", langCode: String = "en") {
        val customDeck = DeckEntity(
            id = "starred_quiz_${System.currentTimeMillis()}",
            languageCode = langCode,
            title = title,
            subtitle = "Kiểm tra ${cards.size} từ đã lưu",
            iconEmoji = "⚡",
            level = "Đã lưu",
            colorHex = "#0284C7",
            cardCount = cards.size
        )
        _currentScreen.value = ScreenState.Quiz(customDeck, cards)
    }

    fun startMatchSavedCards(cards: List<FlashCardEntity>, title: String = "Ghép thẻ từ đã lưu", langCode: String = "en") {
        val customDeck = DeckEntity(
            id = "starred_match_${System.currentTimeMillis()}",
            languageCode = langCode,
            title = title,
            subtitle = "Ghép cặp ${cards.size} từ đã lưu",
            iconEmoji = "🧩",
            level = "Đã lưu",
            colorHex = "#0284C7",
            cardCount = cards.size
        )
        _currentScreen.value = ScreenState.Match(customDeck, cards)
    }

    fun startOnboardingTrial(language: AppLanguage, reminderHour: Int) {
        _pendingTrialLanguage = language
        _selectedLanguage.value = language
        _learningLanguages.value = listOf(language)
        updateStudySchedule(reminderHour)
        val trialCards = com.example.data.local.StarterVocabData.getStarterCardsForLanguage(language)
        _currentScreen.value = ScreenState.OnboardingTrialStudy(language, trialCards)
    }

    fun startOnboardingTrialQuiz(language: AppLanguage, cards: List<FlashCardEntity>) {
        _currentScreen.value = ScreenState.OnboardingTrialQuiz(language, cards)
    }

    fun finishOnboardingTrialAndGoToAuth() {
        _currentScreen.value = ScreenState.Register
    }

    /**
     * Gọi sau khi đăng ký tài khoản thành công.
     * - Đến từ luồng Onboarding Trial: lưu ngôn ngữ đang học, tạo bộ Starter + thẻ,
     *   và set streak = 1 (vừa học thử hôm nay).
     * - Đăng ký trực tiếp: đảm bảo có ngôn ngữ mặc định đang theo học.
     */
    fun onRegisterSuccess(username: String) {
        viewModelScope.launch {
            val trialLang = _pendingTrialLanguage
            _pendingTrialLanguage = null
            if (trialLang != null) {
                _selectedLanguage.value = trialLang
                _learningLanguages.value = listOf(trialLang)
                repository.addLearningLanguage(trialLang)
                repository.switchActiveLanguage(trialLang.code)
                val starterCards = com.example.data.local.StarterVocabData.getStarterCardsForLanguage(trialLang)
                repository.insertDeck(
                    DeckEntity(
                        id = "${trialLang.code}_starter",
                        languageCode = trialLang.code,
                        title = "${trialLang.displayName} Khởi động",
                        subtitle = "Bộ từ vựng khởi đầu từ màn hình chào mừng",
                        iconEmoji = "🚀",
                        level = "Mới bắt đầu",
                        colorHex = "#10B981",
                        cardCount = starterCards.size,
                        isCustom = false
                    )
                )
                starterCards.forEach { card ->
                    repository.insertCard(card.copy(id = 0L))
                }
                repository.updateStreak(1)
            } else {
                repository.addLearningLanguage(_selectedLanguage.value)
                repository.switchActiveLanguage(_selectedLanguage.value.code)
            }
        }
        _currentScreen.value = ScreenState.Home
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
    }
}
