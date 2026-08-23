package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AppLanguage
import com.example.data.model.DeckEntity
import com.example.notification.NotificationHelper
import com.example.ui.detail.DeckDetailScreen
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
import com.example.ui.welcome.OnboardingStepsScreen
import com.example.ui.welcome.WelcomeScreen
import com.example.ui.welcome.RegisterScreen
import com.example.ui.welcome.LoginScreen
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleNotificationIntent(intent)
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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNotificationIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        // Khi người dùng đang ở trong app, tự động xóa notification nhắc học ngoài hệ thống
        NotificationHelper.clearAllNotifications(this)
    }

    private fun handleNotificationIntent(intent: Intent?) {
        if (intent == null) return
        NotificationHelper.clearAllNotifications(this)
        val navTarget = intent.getStringExtra("EXTRA_NAV_TARGET")
        if (navTarget == "HOME_STUDY") {
            viewModel.navigateTo(ScreenState.Home)
            viewModel.dismissNotificationPreview()
        }
    }
}

@Composable
fun NTKFlashCardApp(viewModel: MainViewModel) {
    val context = LocalContext.current
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val selectedLanguage by viewModel.selectedLanguage.collectAsStateWithLifecycle()
    val learningLanguages by viewModel.learningLanguages.collectAsStateWithLifecycle()
    val decks by viewModel.decksForCurrentLanguage.collectAsStateWithLifecycle()
    val allDecks by viewModel.allDecks.collectAsStateWithLifecycle()
    val starredCards by viewModel.starredCardsList.collectAsStateWithLifecycle()
    val allCards by viewModel.allCardsList.collectAsStateWithLifecycle()
    val streakDays by viewModel.streakDays.collectAsStateWithLifecycle()
    val masteredCount by viewModel.masteredCount.collectAsStateWithLifecycle()
    val totalCount by viewModel.totalCardsCount.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val userVipLevel by viewModel.userVipLevel.collectAsStateWithLifecycle()
    val notificationPreview by viewModel.notificationPreview.collectAsStateWithLifecycle()

    var showProfileDialog by remember { mutableStateOf(false) }
    var showCreateDeckDialog by remember { mutableStateOf(false) }
    var targetDeckForCardCreation by remember { mutableStateOf<DeckEntity?>(null) }

    // On-demand notification permission launcher for in-app test actions
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    // Auto-dismiss in-app notification banner after 6 seconds
    LaunchedEffect(notificationPreview) {
        if (notificationPreview != null) {
            delay(6000)
            viewModel.dismissNotificationPreview()
        }
    }

    // Android System Back Button Handler
    BackHandler(enabled = currentScreen !is ScreenState.Welcome) {
        when (currentScreen) {
            is ScreenState.Login -> viewModel.navigateTo(ScreenState.Welcome)
            is ScreenState.Register -> viewModel.navigateTo(ScreenState.Welcome)
            is ScreenState.Onboarding -> viewModel.navigateTo(ScreenState.Welcome)
            is ScreenState.Home -> viewModel.navigateTo(ScreenState.Welcome)
            else -> viewModel.navigateTo(ScreenState.Home)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Crossfade(targetState = currentScreen, label = "screen_transition") { screen ->
            when (screen) {
                is ScreenState.Welcome -> {
                    WelcomeScreen(
                        onStartLearning = {
                            viewModel.navigateTo(ScreenState.Onboarding)
                        },
                        onLoginClick = {
                            viewModel.navigateTo(ScreenState.Login)
                        },
                        onSelectLanguage = { code ->
                            val lang = AppLanguage.fromCode(code)
                            viewModel.selectLanguage(lang)
                            viewModel.navigateTo(ScreenState.Onboarding)
                        }
                    )
                }

                is ScreenState.Login -> {
                    LoginScreen(
                        onLoginSuccess = { username ->
                            viewModel.updateUserName(username)
                            viewModel.navigateTo(ScreenState.Home)
                        },
                        onBackToWelcome = {
                            viewModel.navigateTo(ScreenState.Welcome)
                        },
                        onNavigateToRegister = {
                            viewModel.navigateTo(ScreenState.Register)
                        }
                    )
                }

                is ScreenState.Register -> {
                    RegisterScreen(
                        onRegisterSuccess = { username ->
                            viewModel.updateUserName(username)
                            viewModel.navigateTo(ScreenState.Home)
                        },
                        onBackToWelcome = {
                            viewModel.navigateTo(ScreenState.Welcome)
                        },
                        onNavigateToLogin = {
                            viewModel.navigateTo(ScreenState.Login)
                        }
                    )
                }

                is ScreenState.Onboarding -> {
                    OnboardingStepsScreen(
                        onCompleteOnboarding = { chosenLang, reminderHour ->
                            viewModel.selectLanguage(chosenLang)
                            viewModel.updateStudySchedule(reminderHour)
                            viewModel.navigateTo(ScreenState.Home)
                        },
                        onBackToWelcome = {
                            viewModel.navigateTo(ScreenState.Welcome)
                        }
                    )
                }

                is ScreenState.Home -> {
                    HomeScreen(
                        selectedLanguage = selectedLanguage,
                        learningLanguages = learningLanguages,
                        onSelectLanguage = { viewModel.selectLanguage(it) },
                        onAddLearningLanguage = { viewModel.addLearningLanguage(it) },
                        decks = decks,
                        allDecksList = allDecks,
                        starredCards = starredCards,
                        allCardsList = allCards,
                        streakDays = streakDays,
                        masteredWordsCount = masteredCount,
                        totalWordsCount = totalCount,
                        userName = userName,
                        userVipLevel = userVipLevel,
                        onSelectVipLevel = { viewModel.updateUserVipLevel(it) },
                        onOpenDeckDetail = { deck -> viewModel.openDeckDetail(deck) },
                        onStudyDeck = { deck -> viewModel.startStudyDeck(deck) },
                        onQuizDeck = { deck -> viewModel.startQuizDeck(deck) },
                        onMatchDeck = { deck -> viewModel.startMatchDeck(deck) },
                        onAddCardToDeck = { deck -> targetDeckForCardCreation = deck },
                        onCreateNewDeck = { showCreateDeckDialog = true },
                        onOpenProfile = { showProfileDialog = true },
                        onOpenStarred = { viewModel.openStarredCards() },
                        onSpeak = { text, tag -> viewModel.speak(text, tag) },
                        onToggleStar = { id, starred -> viewModel.toggleStar(id, starred) },
                        onStudyByLang = { langCode ->
                            viewModel.startStudyByLanguage(langCode)
                        },
                        onTestSmartNotification = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                val hasPermission = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) == PackageManager.PERMISSION_GRANTED
                                if (!hasPermission) {
                                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                            }
                            viewModel.triggerSmartNotificationTest()
                        },
                        onTestMilestoneNotification = { streak ->
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                val hasPermission = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) == PackageManager.PERMISSION_GRANTED
                                if (!hasPermission) {
                                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                            }
                            viewModel.triggerAchievementTest(streak)
                        }
                    )
                }

                is ScreenState.DeckDetail -> {
                    DeckDetailScreen(
                        deck = screen.deck,
                        cards = screen.cards,
                        onBack = { viewModel.navigateTo(ScreenState.Home) },
                        onStartStudy = { viewModel.startStudyDeck(screen.deck) },
                        onStartQuiz = { viewModel.startQuizDeck(screen.deck) },
                        onStartMatch = { viewModel.startMatchDeck(screen.deck) },
                        onSpeak = { text, tag -> viewModel.speak(text, tag) },
                        onToggleStar = { id, starred -> viewModel.toggleStar(id, starred) }
                    )
                }

                is ScreenState.Study -> {
                    FlashcardStudyScreen(
                        deckTitle = screen.deck.title,
                        languageTag = screen.deck.languageCode,
                        cards = screen.cards,
                        userVipLevel = userVipLevel,
                        onBack = { viewModel.navigateTo(ScreenState.Home) },
                        onSpeak = { text, tag -> viewModel.speak(text, tag) },
                        onToggleStar = { id, starred -> viewModel.toggleStar(id, starred) },
                        onRecordReview = { id, diff -> viewModel.recordReview(id, diff) },
                        onStartQuiz = { viewModel.startQuizDeck(screen.deck) },
                        onSessionFinished = { count, mastered ->
                            viewModel.completeStudySession(
                                deckId = screen.deck.id,
                                deckTitle = screen.deck.title,
                                langCode = screen.deck.languageCode,
                                cardsStudied = count,
                                masteredCount = mastered,
                                durationSecs = 120
                            )
                        }
                    )
                }

                is ScreenState.Quiz -> {
                    QuizScreen(
                        deckTitle = screen.deck.title,
                        languageTag = screen.deck.languageCode,
                        cards = screen.cards,
                        onBack = { viewModel.navigateTo(ScreenState.Home) },
                        onSpeak = { text, tag -> viewModel.speak(text, tag) },
                        onAnswerCorrect = { correctCard ->
                            viewModel.markCardMastered(correctCard.id, screen.deck.languageCode)
                        },
                        onAnswerWrong = { wrongCard ->
                            viewModel.markCardUnmastered(wrongCard.id)
                        },
                        onFinishQuiz = { score, total, wrongCards ->
                            viewModel.processQuizResult(
                                deck = screen.deck,
                                score = score,
                                total = total,
                                wrongCards = wrongCards,
                                durationSecs = 90
                            )
                        },
                        onStudyWrongCards = { wrongCards ->
                            viewModel.startStudyUnmasteredDeck(screen.deck, wrongCards)
                        },
                        onStudyNext = { viewModel.startStudyDeck(screen.deck) }
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
                        deckTitle = "Từ vựng đã lưu ⭐",
                        languageTag = selectedLanguage.code,
                        cards = screen.cards,
                        userVipLevel = userVipLevel,
                        onBack = { viewModel.navigateTo(ScreenState.Home) },
                        onSpeak = { text, tag -> viewModel.speak(text, tag) },
                        onToggleStar = { id, starred -> viewModel.toggleStar(id, starred) },
                        onRecordReview = { id, diff -> viewModel.recordReview(id, diff) },
                        onStartQuiz = {
                            val mockDeck = DeckEntity(
                                id = "starred",
                                languageCode = selectedLanguage.code,
                                title = "Từ vựng đã lưu ⭐",
                                subtitle = "Các từ bạn đã đánh dấu để ôn tập riêng",
                                iconEmoji = "⭐",
                                level = "Cơ bản",
                                colorHex = "#F59E0B",
                                cardCount = starredCards.size
                            )
                            viewModel.startQuizDeck(mockDeck)
                        },
                        onSessionFinished = { count, mastered ->
                            viewModel.completeStudySession(
                                deckId = "starred",
                                deckTitle = "Từ vựng đã lưu",
                                langCode = selectedLanguage.code,
                                cardsStudied = count,
                                masteredCount = mastered,
                                durationSecs = 120
                            )
                        }
                    )
                }
            }
        }

        // FLOATING IN-APP HEADS-UP NOTIFICATION BANNER (Matching Design)
        AnimatedVisibility(
            visible = notificationPreview != null,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            notificationPreview?.let { preview ->
                var isExpanded by remember { mutableStateOf(true) }

                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = if (preview.isAchievement) Color(0xFF1E1B4B) else Color(0xFFF4F5FD),
                    shadowElevation = 14.dp,
                    border = BorderStroke(
                        1.dp,
                        if (preview.isAchievement) Color(0xFFF59E0B) else Color(0xFFE2E4F4)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        // Top row: App Icon + Header Info + Mascot + Expand/Close
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Left App Icon (Squircle)
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF5856D6)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_app_notification_logo),
                                    contentDescription = "NTK FlashCard",
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            // App Name + Timestamp + Title & Message
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "NTK FlashCard",
                                        color = if (preview.isAchievement) Color(0xFFC7D2FE) else Color(0xFF5856D6),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = " • now 🔔",
                                        color = if (preview.isAchievement) Color(0xFF94A3B8) else Color(0xFF6B7280),
                                        fontSize = 11.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = preview.title,
                                    color = if (preview.isAchievement) Color.White else Color(0xFF1F2440),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = preview.message,
                                    color = if (preview.isAchievement) Color(0xFFCBD5E1) else Color(0xFF4B5270),
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    maxLines = if (isExpanded) 3 else 1
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Cute Mascot Penguin Image
                            Image(
                                painter = painterResource(id = R.drawable.img_mascot_penguin_1787286222548),
                                contentDescription = "Mascot",
                                modifier = Modifier
                                    .size(if (isExpanded) 56.dp else 46.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            // Expand / Collapse Chevron Button
                            IconButton(
                                onClick = { isExpanded = !isExpanded },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = if (isExpanded) "Thu gọn" else "Mở rộng",
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // EXPANDED DETAILS: Streak Progress Bar (7 checkmarks + gift) & Action Buttons
                        if (isExpanded) {
                            Spacer(modifier = Modifier.height(12.dp))

                            // 1. Streak Tracker Bar
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, Color(0xFFE5E7FA)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "🔥",
                                        fontSize = 20.sp
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Column {
                                        Text(
                                            text = "Streak hiện tại",
                                            color = Color(0xFF64748B),
                                            fontSize = 10.sp
                                        )
                                        Text(
                                            text = "$streakDays ngày",
                                            color = Color(0xFF5856D6),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.weight(1f))

                                    // 7 Checkmarks + 1 Gift Box
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        for (day in 1..7) {
                                            Image(
                                                painter = painterResource(id = R.drawable.ic_check_circle_purple),
                                                contentDescription = "Ngày $day",
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(4.dp))

                                        Image(
                                            painter = painterResource(id = R.drawable.ic_gift_box_purple),
                                            contentDescription = "Phần thưởng",
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // 2. Action Buttons: [▶ Học ngay] & [⏰ Để sau]
                            if (!preview.isAchievement) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // Button 1: Học ngay
                                    Button(
                                        onClick = {
                                            viewModel.dismissNotificationPreview()
                                            NotificationHelper.clearAllNotifications(context)
                                            if (decks.isNotEmpty()) {
                                                viewModel.startStudyDeck(decks.first())
                                            } else {
                                                viewModel.navigateTo(ScreenState.Home)
                                            }
                                        },
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF5856D6)
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(42.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp)
                                    ) {
                                        Text(
                                            text = "▶  Học ngay",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }

                                    // Button 2: Để sau
                                    OutlinedButton(
                                        onClick = {
                                            viewModel.snoozeStudyReminderToday()
                                            NotificationHelper.clearAllNotifications(context)
                                            android.widget.Toast.makeText(
                                                context,
                                                "⏰ Đã hoãn nhắc nhở hôm nay. Sẽ không nhắc lại trong ngày!",
                                                android.widget.Toast.LENGTH_SHORT
                                            ).show()
                                        },
                                        shape = RoundedCornerShape(14.dp),
                                        border = BorderStroke(1.5.dp, Color(0xFFDCDFF2)),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = Color.White,
                                            contentColor = Color(0xFF4338CA)
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(42.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp)
                                    ) {
                                        Text(
                                            text = "⏰  Để sau",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF4338CA)
                                        )
                                    }
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.dismissNotificationPreview() },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFF59E0B)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(42.dp)
                                ) {
                                    Text(
                                        text = "🎉 Tuyệt vời! Cảm ơn",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // PROFILE DIALOG
    if (showProfileDialog) {
        UserProfileDialog(
            userName = userName,
            userVipLevel = userVipLevel,
            streakDays = streakDays,
            masteredWordsCount = masteredCount,
            totalWordsCount = totalCount,
            onDismiss = { showProfileDialog = false },
            onUpdateName = { viewModel.updateUserName(it) },
            onSelectVipLevel = { viewModel.updateUserVipLevel(it) }
        )
    }

    // CREATE DECK DIALOG
    if (showCreateDeckDialog) {
        CreateDeckDialog(
            currentLanguageCode = selectedLanguage.code,
            allCards = allCards,
            onDismiss = { showCreateDeckDialog = false },
            onSave = { newDeck, selectedCards ->
                viewModel.createNewDeckWithCards(newDeck, selectedCards)
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
