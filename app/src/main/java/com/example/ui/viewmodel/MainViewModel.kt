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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class ScreenState {
    object Welcome : ScreenState()
    object Home : ScreenState()
    data class DeckDetail(val deck: DeckEntity, val cards: List<FlashCardEntity>) : ScreenState()
    data class Study(val deck: DeckEntity, val cards: List<FlashCardEntity>) : ScreenState()
    data class Quiz(val deck: DeckEntity, val cards: List<FlashCardEntity>) : ScreenState()
    data class Match(val deck: DeckEntity, val cards: List<FlashCardEntity>) : ScreenState()
    data class Starred(val cards: List<FlashCardEntity>) : ScreenState()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database: AppDatabase = AppDatabase.getDatabase(application, viewModelScope)
    private val repository: FlashCardRepository = FlashCardRepository(database.flashCardDao())
    private val ttsManager: TTSManager = TTSManager(application)

    private val _currentScreen = MutableStateFlow<ScreenState>(ScreenState.Welcome)
    val currentScreen: StateFlow<ScreenState> = _currentScreen.asStateFlow()

    private val _selectedLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val selectedLanguage: StateFlow<AppLanguage> = _selectedLanguage.asStateFlow()

    private val _userName = MutableStateFlow("Bạn Học")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _streakDays = MutableStateFlow(7)
    val streakDays: StateFlow<Int> = _streakDays.asStateFlow()

    val decksForCurrentLanguage: StateFlow<List<DeckEntity>> = _selectedLanguage
        .flatMapLatest { lang -> repository.getDecksByLanguage(lang.code) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDecks: StateFlow<List<DeckEntity>> = repository.getAllDecks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val starredCardsList: StateFlow<List<FlashCardEntity>> = repository.getStarredCards()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val masteredCount: StateFlow<Int> = repository.getMasteredCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalCardsCount: StateFlow<Int> = repository.getTotalCardsCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        viewModelScope.launch {
            repository.checkAndSeedDatabase()
        }
    }

    fun navigateTo(screen: ScreenState) {
        _currentScreen.value = screen
    }

    fun selectLanguage(language: AppLanguage) {
        _selectedLanguage.value = language
    }

    fun updateUserName(name: String) {
        _userName.value = name
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
        }
    }

    fun createNewDeck(deck: DeckEntity) {
        viewModelScope.launch {
            repository.insertDeck(deck)
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
            val decks = repository.getDecksByLanguage(langCode)
            decks.collect { list ->
                val targetDeck = list.firstOrNull()
                if (targetDeck != null) {
                    startStudyDeck(targetDeck)
                }
            }
        }
    }

    fun openDeckDetail(deck: DeckEntity) {
        viewModelScope.launch {
            repository.getCardsForDeck(deck.id).collect { cards ->
                _currentScreen.value = ScreenState.DeckDetail(deck, cards)
            }
        }
    }

    fun startStudyDeck(deck: DeckEntity) {
        viewModelScope.launch {
            repository.getCardsForDeck(deck.id).collect { cards ->
                _currentScreen.value = ScreenState.Study(deck, cards)
            }
        }
    }

    fun startQuizDeck(deck: DeckEntity) {
        viewModelScope.launch {
            repository.getCardsForDeck(deck.id).collect { cards ->
                _currentScreen.value = ScreenState.Quiz(deck, cards)
            }
        }
    }

    fun startMatchDeck(deck: DeckEntity) {
        viewModelScope.launch {
            repository.getCardsForDeck(deck.id).collect { cards ->
                _currentScreen.value = ScreenState.Match(deck, cards)
            }
        }
    }

    fun openStarredCards() {
        viewModelScope.launch {
            repository.getStarredCards().collect { cards ->
                _currentScreen.value = ScreenState.Starred(cards)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
    }
}
