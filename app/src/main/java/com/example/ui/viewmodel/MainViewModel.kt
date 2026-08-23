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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
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
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database: AppDatabase = AppDatabase.getDatabase(application, viewModelScope)
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

    private val _userName = MutableStateFlow("Bạn Học")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userVipLevel = MutableStateFlow(1) // Default VIP 1 for test user
    val userVipLevel: StateFlow<Int> = _userVipLevel.asStateFlow()

    private val _streakDays = MutableStateFlow(7)
    val streakDays: StateFlow<Int> = _streakDays.asStateFlow()

    private val smartNotificationEngine = com.example.notification.SmartNotificationEngine(application)

    private val _notificationPreview = MutableStateFlow<com.example.notification.NotificationPreviewEvent?>(null)
    val notificationPreview: StateFlow<com.example.notification.NotificationPreviewEvent?> = _notificationPreview.asStateFlow()

    fun dismissNotificationPreview() {
        _notificationPreview.value = null
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
            com.example.widget.VocabularyStreakWidgetProvider.updateAllWidgets(getApplication(), _streakDays.value)
            
            // Khởi tạo lịch học AlarmManager thông minh
            com.example.notification.StudyAlarmScheduler.scheduleStudyAlarm(
                getApplication(),
                com.example.data.model.StudySchedule()
            )
        }
    }

    fun navigateTo(screen: ScreenState) {
        _currentScreen.value = screen
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

    fun recordSrsReview(card: FlashCardEntity, rating: Int, isCorrect: Boolean) {
        viewModelScope.launch {
            repository.recordSrsReview(card, rating, isCorrect)
            com.example.widget.VocabularyStreakWidgetProvider.updateAllWidgets(getApplication(), _streakDays.value)
        }
    }

    fun updateStudySchedule(reminderHour: Int, reminderMinute: Int = 0) {
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

    fun updateUserName(name: String) {
        _userName.value = name
    }

    fun updateUserVipLevel(level: Int) {
        _userVipLevel.value = level
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
            com.example.widget.VocabularyStreakWidgetProvider.updateAllWidgets(getApplication(), _streakDays.value)
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
        }
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

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
    }
}
