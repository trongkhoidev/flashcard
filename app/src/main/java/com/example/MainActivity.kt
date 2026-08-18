package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AppLanguage
import com.example.data.model.DeckEntity
import com.example.ui.dialogs.CreateCardDialog
import com.example.ui.dialogs.CreateDeckDialog
import com.example.ui.dialogs.UserProfileDialog
import com.example.ui.home.HomeScreen
import com.example.ui.match.WordMatchScreen
import com.example.ui.quiz.QuizScreen
import com.example.ui.study.FlashcardStudyScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.ScreenState
import com.example.ui.welcome.WelcomeScreen

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NTKFlashCardApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun NTKFlashCardApp(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val selectedLanguage by viewModel.selectedLanguage.collectAsStateWithLifecycle()
    val decks by viewModel.decksForCurrentLanguage.collectAsStateWithLifecycle()
    val allDecks by viewModel.allDecks.collectAsStateWithLifecycle()
    val starredCards by viewModel.starredCardsList.collectAsStateWithLifecycle()
    val streakDays by viewModel.streakDays.collectAsStateWithLifecycle()
    val masteredCount by viewModel.masteredCount.collectAsStateWithLifecycle()
    val totalCount by viewModel.totalCardsCount.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()

    var showProfileDialog by remember { mutableStateOf(false) }
    var showCreateDeckDialog by remember { mutableStateOf(false) }
    var targetDeckForCardCreation by remember { mutableStateOf<DeckEntity?>(null) }

    // Android System Back Button Handler
    BackHandler(enabled = currentScreen !is ScreenState.Welcome) {
        when (currentScreen) {
            is ScreenState.Home -> viewModel.navigateTo(ScreenState.Welcome)
            else -> viewModel.navigateTo(ScreenState.Home)
        }
    }

    Crossfade(targetState = currentScreen, label = "screen_transition") { screen ->
        when (screen) {
            is ScreenState.Welcome -> {
                WelcomeScreen(
                    onStartLearning = {
                        viewModel.navigateTo(ScreenState.Home)
                    },
                    onLoginClick = {
                        showProfileDialog = true
                    },
                    onSelectLanguage = { code ->
                        val lang = AppLanguage.fromCode(code)
                        viewModel.selectLanguage(lang)
                        viewModel.navigateTo(ScreenState.Home)
                    }
                )
            }

            is ScreenState.Home -> {
                HomeScreen(
                    selectedLanguage = selectedLanguage,
                    onSelectLanguage = { viewModel.selectLanguage(it) },
                    decks = decks,
                    allDecksList = allDecks,
                    starredCards = starredCards,
                    streakDays = streakDays,
                    masteredWordsCount = masteredCount,
                    totalWordsCount = totalCount,
                    userName = userName,
                    onStudyDeck = { deck -> viewModel.startStudyDeck(deck) },
                    onQuizDeck = { deck -> viewModel.startQuizDeck(deck) },
                    onMatchDeck = { deck -> viewModel.startMatchDeck(deck) },
                    onAddCardToDeck = { deck -> targetDeckForCardCreation = deck },
                    onCreateNewDeck = { showCreateDeckDialog = true },
                    onOpenProfile = { showProfileDialog = true },
                    onOpenStarred = { viewModel.openStarredCards() },
                    onSpeak = { text, langTag -> viewModel.speak(text, langTag) },
                    onCreateDeckDirect = { title, subtitle, langCode, level ->
                        viewModel.createNewDeck(
                            DeckEntity(
                                id = "deck_${System.currentTimeMillis()}",
                                languageCode = langCode,
                                title = title,
                                subtitle = subtitle,
                                iconEmoji = "📚",
                                level = level,
                                colorHex = "#6366F1",
                                cardCount = 0
                            )
                        )
                    },
                    onImportCardsDirect = { deckId, cards ->
                        viewModel.importCards(cards)
                    },
                    onStudyByLang = { langCode ->
                        viewModel.startStudyByLanguage(langCode)
                    }
                )
            }

            is ScreenState.Study -> {
                FlashcardStudyScreen(
                    deckTitle = screen.deck.title,
                    languageTag = selectedLanguage.ttsLanguageTag,
                    cards = screen.cards,
                    onBack = { viewModel.navigateTo(ScreenState.Home) },
                    onSpeak = { text, tag -> viewModel.speak(text, tag) },
                    onToggleStar = { id, starred -> viewModel.toggleStar(id, starred) },
                    onRecordReview = { id, diff -> viewModel.recordReview(id, diff) },
                    onStartQuiz = { viewModel.startQuizDeck(screen.deck) }
                )
            }

            is ScreenState.Quiz -> {
                QuizScreen(
                    deckTitle = screen.deck.title,
                    languageTag = selectedLanguage.ttsLanguageTag,
                    cards = screen.cards,
                    onBack = { viewModel.navigateTo(ScreenState.Home) },
                    onSpeak = { text, tag -> viewModel.speak(text, tag) },
                    onFinishQuiz = { score, total ->
                        // Could record quiz achievement here
                    }
                )
            }

            is ScreenState.Match -> {
                WordMatchScreen(
                    deckTitle = screen.deck.title,
                    cards = screen.cards,
                    onBack = { viewModel.navigateTo(ScreenState.Home) }
                )
            }

            is ScreenState.Starred -> {
                FlashcardStudyScreen(
                    deckTitle = "Từ vựng đã lưu (Starred)",
                    languageTag = selectedLanguage.ttsLanguageTag,
                    cards = screen.cards,
                    onBack = { viewModel.navigateTo(ScreenState.Home) },
                    onSpeak = { text, tag -> viewModel.speak(text, tag) },
                    onToggleStar = { id, starred -> viewModel.toggleStar(id, starred) },
                    onRecordReview = { id, diff -> viewModel.recordReview(id, diff) },
                    onStartQuiz = {
                        val mockDeck = DeckEntity(
                            id = "starred",
                            languageCode = selectedLanguage.code,
                            title = "Từ vựng đã lưu",
                            subtitle = "Thực hành từ yêu thích",
                            iconEmoji = "⭐",
                            level = "Ôn tập",
                            colorHex = "#F59E0B"
                        )
                        viewModel.startQuizDeck(mockDeck)
                    }
                )
            }
        }
    }

    // PROFILE DIALOG
    if (showProfileDialog) {
        UserProfileDialog(
            userName = userName,
            streakDays = streakDays,
            masteredWordsCount = masteredCount,
            totalWordsCount = totalCount,
            onDismiss = { showProfileDialog = false },
            onUpdateName = { viewModel.updateUserName(it) }
        )
    }

    // CREATE DECK DIALOG
    if (showCreateDeckDialog) {
        CreateDeckDialog(
            currentLanguageCode = selectedLanguage.code,
            onDismiss = { showCreateDeckDialog = false },
            onSave = { newDeck ->
                viewModel.createNewDeck(newDeck)
                showCreateDeckDialog = false
            }
        )
    }

    // CREATE CARD DIALOG
    targetDeckForCardCreation?.let { deck ->
        CreateCardDialog(
            deckId = deck.id,
            languageCode = deck.languageCode,
            onDismiss = { targetDeckForCardCreation = null },
            onSave = { newCard ->
                viewModel.createNewCard(newCard)
                targetDeckForCardCreation = null
            }
        )
    }
}
