package com.example.ui.welcome

import android.Manifest
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.R
import com.example.audio.TTSManager
import com.example.data.model.AppLanguage
import com.example.widget.VocabularyStreakWidgetProvider
import kotlinx.coroutines.delay

// Brand palette matching the design - Squirtle Ocean Blue Theme
private val StepPurplePrimary = Color(0xFF0284C7)     // Vibrant Ocean Blue
private val StepPurpleLight = Color(0xFFF0F9FF)       // Light Ocean Aqua Surface
private val StepPurpleBorder = Color(0xFF0284C7)      // Ocean Blue Accent Border
private val StepPillBg = Color(0xFFE0F2FE)            // Soft Sky Pill Background
private val StepPillText = Color(0xFF0369A1)          // Deep Shell Blue Text
private val StepInactiveDot = Color(0xFFE2E8F0)
private val StepInactiveText = Color(0xFF94A3B8)
private val StepDarkText = Color(0xFF0F172A)
private val StepMutedText = Color(0xFF64748B)

data class OnboardingLanguageItem(
    val language: AppLanguage,
    val name: String,
    val nativeName: String,
    val flagEmoji: String
)

data class StudyLevelItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val badge: String,
    val iconEmoji: String
)

data class StudyTopicItem(
    val id: String,
    val title: String,
    val iconEmoji: String
)

data class TimeSlotItem(
    val id: String,
    val timeRange: String,
    val periodName: String,
    val iconEmoji: String,
    val defaultHour: Int
)

@Composable
fun OnboardingStepsScreen(
    onCompleteOnboarding: (AppLanguage, Int) -> Unit,
    onBackToWelcome: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableIntStateOf(1) } // 1 to 4
    var selectedLanguage by remember { mutableStateOf(AppLanguage.ENGLISH) }
    var selectedLevelId by remember { mutableStateOf("beginner") }
    var selectedTopics by remember { mutableStateOf(setOf("daily", "travel", "work")) }
    var selectedTimeSlots by remember { mutableStateOf(setOf("slot_morning_2", "slot_afternoon", "slot_evening_2")) }

    val languages = remember {
        listOf(
            OnboardingLanguageItem(AppLanguage.ENGLISH, "Tiếng Anh", "English", "🇬🇧"),
            OnboardingLanguageItem(AppLanguage.CHINESE, "Tiếng Trung", "中文", "🇨🇳"),
            OnboardingLanguageItem(AppLanguage.JAPANESE, "Tiếng Nhật", "日本語", "🇯🇵"),
            OnboardingLanguageItem(AppLanguage.KOREAN, "Tiếng Hàn", "한국어", "🇰🇷"),
            OnboardingLanguageItem(AppLanguage.FRENCH, "Tiếng Pháp", "Français", "🇫🇷"),
            OnboardingLanguageItem(AppLanguage.GERMAN, "Tiếng Đức", "Deutsch", "🇩🇪"),
            OnboardingLanguageItem(AppLanguage.SPANISH, "Tiếng Tây Ban Nha", "Español", "🇪🇸"),
            OnboardingLanguageItem(AppLanguage.ITALIAN, "Tiếng Ý", "Italiano", "🇮🇹"),
            OnboardingLanguageItem(AppLanguage.PORTUGUESE, "Tiếng Bồ Đào Nha", "Português", "🇵🇹")
        )
    }

    val levels = remember {
        listOf(
            StudyLevelItem("beginner", "Người mới bắt đầu", "Chưa biết gì hoặc mới làm quen bảng chữ cái", "A1 / Sơ cấp", "🌱"),
            StudyLevelItem("elementary", "Cơ bản & Giao tiếp", "Nắm từ vựng đơn giản, hội thoại ngắn hàng ngày", "A2 / Cơ bản", "💬"),
            StudyLevelItem("intermediate", "Trung cấp", "Tự tin giao tiếp, đọc hiểu tốt các chủ đề quen thuộc", "B1-B2 / Trung cấp", "⚡"),
            StudyLevelItem("advanced", "Nâng cao & Luyện thi", "Thành thạo chuyên sâu, luyện thi chứng chỉ quốc tế", "C1-C2 / Nâng cao", "🏆")
        )
    }

    val topics = remember {
        listOf(
            StudyTopicItem("daily", "Giao tiếp hàng ngày", "💬"),
            StudyTopicItem("travel", "Du lịch & Ẩm thực", "✈️"),
            StudyTopicItem("work", "Công việc & Kinh doanh", "💼"),
            StudyTopicItem("exam", "Luyện thi chứng chỉ", "🎓"),
            StudyTopicItem("culture", "Văn hóa & Đời sống", "🏯"),
            StudyTopicItem("tech", "Công nghệ & Khoa học", "💡"),
            StudyTopicItem("entertainment", "Phim ảnh & Âm nhạc", "🎬"),
            StudyTopicItem("shopping", "Mua sắm & Giải trí", "🛍️")
        )
    }

    val timeSlots = remember {
        listOf(
            TimeSlotItem("slot_morning_1", "06:00 - 08:00", "Buổi sáng", "🌅", 7),
            TimeSlotItem("slot_morning_2", "08:00 - 12:00", "Buổi sáng", "☀️", 9),
            TimeSlotItem("slot_noon", "12:00 - 14:00", "Buổi trưa", "⛅", 12),
            TimeSlotItem("slot_afternoon", "14:00 - 17:00", "Buổi chiều", "🌤️", 15),
            TimeSlotItem("slot_evening_1", "17:00 - 20:00", "Buổi tối", "🌆", 19),
            TimeSlotItem("slot_evening_2", "20:00 - 23:00", "Buổi tối", "🌙", 20),
            TimeSlotItem("slot_night", "23:00 - 06:00", "Đêm khuya", "🌘", 23)
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFC))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // TOP NAVIGATION & STEPPER (hidden or simplified on loading step)
            if (currentStep <= 4) {
                TopStepperBar(
                    currentStep = currentStep,
                    onBackClick = {
                        if (currentStep > 1) {
                            currentStep--
                        } else {
                            onBackToWelcome()
                        }
                    }
                )
            } else if (currentStep == 5 || currentStep == 6) {
                // Step 5 & 6 have their own back button inside their composables
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(StepPurpleLight, RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = StepPurplePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Đang cá nhân hóa lộ trình...",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = StepPurplePrimary
                            )
                        }
                    }
                }
            }

            if (currentStep <= 4 || currentStep == 7) {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // MAIN CONTENT PAGER BY STEP
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                AnimatedContent(
                    targetState = currentStep,
                    transitionSpec = {
                        if (targetState > initialState) {
                            (slideInHorizontally { width -> width / 3 } + fadeIn())
                                .togetherWith(slideOutHorizontally { width -> -width / 3 } + fadeOut())
                        } else {
                            (slideInHorizontally { width -> -width / 3 } + fadeIn())
                                .togetherWith(slideOutHorizontally { width -> width / 3 } + fadeOut())
                        }
                    },
                    label = "onboarding_step_content"
                ) { step ->
                    when (step) {
                        1 -> StepLanguageSelection(
                            languages = languages,
                            selectedLanguage = selectedLanguage,
                            onSelectLanguage = { selectedLanguage = it }
                        )

                        2 -> StepLevelSelection(
                            levels = levels,
                            selectedLevelId = selectedLevelId,
                            onSelectLevel = { selectedLevelId = it }
                        )

                        3 -> StepTopicSelection(
                            topics = topics,
                            selectedTopics = selectedTopics,
                            onToggleTopic = { topicId ->
                                selectedTopics = if (selectedTopics.contains(topicId)) {
                                    if (selectedTopics.size > 1) selectedTopics - topicId else selectedTopics
                                } else {
                                    selectedTopics + topicId
                                }
                            }
                        )

                        4 -> StepTimeSelection(
                            timeSlots = timeSlots,
                            selectedTimeSlots = selectedTimeSlots,
                            onToggleTimeSlot = { slotId ->
                                selectedTimeSlots = if (selectedTimeSlots.contains(slotId)) {
                                    if (selectedTimeSlots.size > 1) selectedTimeSlots - slotId else selectedTimeSlots
                                } else {
                                    selectedTimeSlots + slotId
                                }
                            }
                        )

                        5 -> StepNotificationPermission(
                            onEnableNotifications = {
                                currentStep = 6
                            },
                            onSkip = {
                                currentStep = 6
                            },
                            onBackClick = {
                                currentStep = 4
                            }
                        )

                        6 -> StepAddWidgetHomeScreen(
                            selectedLanguage = selectedLanguage,
                            onAddWidgetAndContinue = {
                                currentStep = 7
                            },
                            onSkip = {
                                currentStep = 7
                            },
                            onBackClick = {
                                currentStep = 5
                            }
                        )

                        7 -> {
                            val firstSelectedSlot = timeSlots.firstOrNull { selectedTimeSlots.contains(it.id) }
                            val primaryHour = firstSelectedSlot?.defaultHour ?: 19
                            StepMascotPreparingFlashcards(
                                selectedLanguage = selectedLanguage,
                                selectedLevel = levels.find { it.id == selectedLevelId }?.title ?: "Người mới bắt đầu",
                                topicCount = selectedTopics.size,
                                reminderHour = primaryHour,
                                onReady = {
                                    onCompleteOnboarding(selectedLanguage, primaryHour)
                                }
                            )
                        }
                    }
                }
            }

            // BOTTOM ACTION BUTTON (Only for steps 1-4)
            if (currentStep <= 4) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Button(
                        onClick = {
                            currentStep++
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(20.dp),
                                spotColor = StepPurplePrimary.copy(alpha = 0.5f)
                            )
                            .testTag("onboarding_next_button"),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StepPurplePrimary
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Tiếp tục",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TOP STEPPER COMPONENT
// -------------------------------------------------------------
@Composable
private fun TopStepperBar(
    currentStep: Int,
    onBackClick: () -> Unit
) {
    val stepTitles = listOf("Chọn ngôn ngữ", "Chọn trình độ", "Chủ đề yêu thích", "Giờ nhắc học")

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back Button
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .size(42.dp)
                .background(Color.White, CircleShape)
                .border(BorderStroke(1.dp, Color(0xFFE2E8F0)), CircleShape)
                .shadow(2.dp, CircleShape)
                .testTag("onboarding_back_button")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Quay lại",
                tint = StepDarkText,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 4-step Stepper
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (step in 1..4) {
                val isActive = step == currentStep
                val isCompleted = step < currentStep

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isActive -> StepPurplePrimary
                                    isCompleted -> StepPurplePrimary.copy(alpha = 0.85f)
                                    else -> StepInactiveDot
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        } else {
                            Text(
                                text = "$step",
                                color = if (isActive) Color.White else StepInactiveText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = stepTitles[step - 1],
                        fontSize = 9.sp,
                        color = if (isActive) StepPurplePrimary else StepInactiveText,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }

                // Connecting Line between steps
                if (step < 4) {
                    Box(
                        modifier = Modifier
                            .height(2.dp)
                            .width(12.dp)
                            .background(
                                if (step < currentStep) StepPurplePrimary else StepInactiveDot
                            )
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 1: CHỌN NGÔN NGỮ (LANGUAGE SELECTION)
// -------------------------------------------------------------
@Composable
private fun StepLanguageSelection(
    languages: List<OnboardingLanguageItem>,
    selectedLanguage: AppLanguage,
    onSelectLanguage: (AppLanguage) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Top Header Banner with Mascot
        HeaderBannerWithMascot(
            stepTag = "Bước 1/4",
            title = "Bạn muốn học\nngôn ngữ nào?",
            subtitle = "Chọn ngôn ngữ bạn muốn học để chúng tôi chuẩn bị bộ thẻ phù hợp cho bạn.",
            imageResId = R.drawable.mascot_language_header_1787288385500
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Chọn ngôn ngữ",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = StepDarkText,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // 3x3 Grid of Languages
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            languages.chunked(3).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowItems.forEach { item ->
                        val isSelected = item.language == selectedLanguage

                        LanguageCard(
                            item = item,
                            isSelected = isSelected,
                            onSelect = { onSelectLanguage(item.language) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Auxiliary Card: "Bạn không thấy ngôn ngữ mình muốn?"
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable { },
            color = Color.White,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(StepPurpleLight, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Language,
                        contentDescription = null,
                        tint = StepPurplePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Bạn không thấy ngôn ngữ mình muốn?",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = StepDarkText
                    )
                    Text(
                        text = "Chúng tôi sẽ sớm bổ sung thêm nhiều ngôn ngữ khác.",
                        fontSize = 10.sp,
                        color = StepMutedText
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = StepInactiveText,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

// -------------------------------------------------------------
// LANGUAGE CARD ITEM (3x3 Grid item)
// -------------------------------------------------------------
@Composable
private fun LanguageCard(
    item: OnboardingLanguageItem,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(0.95f)
            .shadow(
                elevation = if (isSelected) 4.dp else 1.dp,
                shape = RoundedCornerShape(18.dp),
                spotColor = if (isSelected) StepPurplePrimary.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.05f)
            )
            .clip(RoundedCornerShape(18.dp))
            .background(if (isSelected) StepPurpleLight else Color.White)
            .border(
                border = BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) StepPurpleBorder else Color(0xFFF1F5F9)
                ),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { onSelect() }
            .padding(8.dp)
    ) {
        // Selection Checkmark Badge (top right)
        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(20.dp)
                    .background(StepPurplePrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Đã chọn",
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
            }
        }

        // Center Content: Flag + Names
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Flag Circular Container
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .shadow(1.dp, CircleShape)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.flagEmoji,
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = item.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = StepDarkText,
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            Text(
                text = item.nativeName,
                fontSize = 10.sp,
                color = StepMutedText,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

// -------------------------------------------------------------
// STEP 2: CHỌN TRÌNH ĐỘ (LEVEL SELECTION)
// -------------------------------------------------------------
@Composable
private fun StepLevelSelection(
    levels: List<StudyLevelItem>,
    selectedLevelId: String,
    onSelectLevel: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        HeaderBannerWithMascot(
            stepTag = "Bước 2/4",
            title = "Trình độ hiện tại\ncủa bạn là gì?",
            subtitle = "Chúng tôi sẽ sắp xếp lượng từ vựng và bài tập phù hợp nhất với năng lực của bạn.",
            imageResId = R.drawable.mascot_language_header_1787288385500
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Chọn cấp độ của bạn",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = StepDarkText,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            levels.forEach { level ->
                val isSelected = level.id == selectedLevelId
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onSelectLevel(level.id) },
                    color = if (isSelected) StepPurpleLight else Color.White,
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) StepPurpleBorder else Color(0xFFF1F5F9)
                    ),
                    shadowElevation = if (isSelected) 4.dp else 1.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Emoji Badge
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    if (isSelected) StepPurplePrimary.copy(alpha = 0.15f) else Color(0xFFF8FAFC),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = level.iconEmoji, fontSize = 22.sp)
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = level.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StepDarkText
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .background(StepPillBg, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = level.badge,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = StepPillText
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = level.subtitle,
                                fontSize = 12.sp,
                                color = StepMutedText,
                                lineHeight = 16.sp
                            )
                        }

                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(StepPurplePrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Đã chọn",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

// -------------------------------------------------------------
// STEP 3: CHỦ ĐỀ YÊU THÍCH (TOPIC SELECTION)
// -------------------------------------------------------------
@Composable
private fun StepTopicSelection(
    topics: List<StudyTopicItem>,
    selectedTopics: Set<String>,
    onToggleTopic: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        HeaderBannerWithMascot(
            stepTag = "Bước 3/4",
            title = "Chủ đề bạn\nquan tâm nhất?",
            subtitle = "Chọn một hoặc nhiều chủ đề để chúng tôi chuẩn bị các bộ thẻ từ vựng hấp dẫn nhất.",
            imageResId = R.drawable.mascot_language_header_1787288385500
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Chọn các chủ đề yêu thích (chọn nhiều)",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = StepDarkText,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // 2-Column Grid for Topics
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            topics.chunked(2).forEach { rowTopics ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowTopics.forEach { topic ->
                        val isSelected = selectedTopics.contains(topic.id)
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { onToggleTopic(topic.id) },
                            color = if (isSelected) StepPurpleLight else Color.White,
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) StepPurpleBorder else Color(0xFFF1F5F9)
                            ),
                            shadowElevation = if (isSelected) 3.dp else 1.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = topic.iconEmoji, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = topic.title,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = StepDarkText,
                                    modifier = Modifier.weight(1f),
                                    maxLines = 2
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = StepPurplePrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

// -------------------------------------------------------------
// STEP 4: CHỌN KHUNG GIỜ RẢNH (TIME SLOT SELECTION)
// -------------------------------------------------------------
@Composable
private fun StepTimeSelection(
    timeSlots: List<TimeSlotItem>,
    selectedTimeSlots: Set<String>,
    onToggleTimeSlot: (String) -> Unit
) {
    val gridSlots = remember(timeSlots) { timeSlots.take(6) }
    val lastWideSlot = remember(timeSlots) { timeSlots.lastOrNull() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header Banner with Cute 3D Alarm Clock
        HeaderBannerWithMascot(
            stepTag = "Bước 4/4",
            title = "Bạn thường rảnh\nvào thời gian nào?",
            subtitle = "Chọn thời gian rảnh của bạn để chúng tôi nhắc nhở và giúp bạn duy trì thói quen học tập.",
            imageResId = R.drawable.mascot_alarm_clock_header_1787292846308
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Chọn các khung giờ rảnh",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = StepDarkText,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // 3 Rows x 2 Columns Grid
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            gridSlots.chunked(2).forEach { rowSlots ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowSlots.forEach { slot ->
                        val isSelected = selectedTimeSlots.contains(slot.id)
                        TimeSlotCard(
                            slot = slot,
                            isSelected = isSelected,
                            onToggle = { onToggleTimeSlot(slot.id) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Full-width last slot (23:00 - 06:00 Đêm khuya)
        if (lastWideSlot != null) {
            Spacer(modifier = Modifier.height(10.dp))
            val isWideSelected = selectedTimeSlots.contains(lastWideSlot.id)
            WideTimeSlotCard(
                slot = lastWideSlot,
                isSelected = isWideSelected,
                onToggle = { onToggleTimeSlot(lastWideSlot.id) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Info Banner: "Chúng tôi sẽ nhắc bạn học!"
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = StepPurpleLight,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, StepPurpleBorder.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(StepPurplePrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Chúng tôi sẽ nhắc bạn học!",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = StepPurplePrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Bạn có thể thay đổi thời gian này bất cứ lúc nào trong phần Cài đặt.",
                        fontSize = 11.sp,
                        color = StepMutedText,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

// -------------------------------------------------------------
// TIME SLOT CARD COMPONENT (Grid item)
// -------------------------------------------------------------
@Composable
private fun TimeSlotCard(
    slot: TimeSlotItem,
    isSelected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable { onToggle() },
        color = if (isSelected) StepPurpleLight else Color.White,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) StepPurpleBorder else Color(0xFFE2E8F0)
        ),
        shadowElevation = if (isSelected) 3.dp else 1.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Checkbox at top right
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(20.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isSelected) StepPurplePrimary else Color.Transparent)
                    .border(
                        BorderStroke(
                            1.5.dp,
                            if (isSelected) StepPurplePrimary else Color(0xFFCBD5E1)
                        ),
                        RoundedCornerShape(6.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Đã chọn",
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            // Center Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = slot.iconEmoji,
                    fontSize = 28.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = slot.timeRange,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) StepPurplePrimary else StepDarkText
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = slot.periodName,
                    fontSize = 11.sp,
                    color = StepMutedText
                )
            }
        }
    }
}

// -------------------------------------------------------------
// WIDE TIME SLOT CARD (Full Width Row)
// -------------------------------------------------------------
@Composable
private fun WideTimeSlotCard(
    slot: TimeSlotItem,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onToggle() },
        color = if (isSelected) StepPurpleLight else Color.White,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) StepPurpleBorder else Color(0xFFE2E8F0)
        ),
        shadowElevation = if (isSelected) 3.dp else 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(
                        if (isSelected) StepPurplePrimary.copy(alpha = 0.15f) else Color(0xFFF8FAFC),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = slot.iconEmoji, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = slot.timeRange,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) StepPurplePrimary else StepDarkText
                )
                Text(
                    text = slot.periodName,
                    fontSize = 11.sp,
                    color = StepMutedText
                )
            }

            // Checkbox
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isSelected) StepPurplePrimary else Color.Transparent)
                    .border(
                        BorderStroke(
                            1.5.dp,
                            if (isSelected) StepPurplePrimary else Color(0xFFCBD5E1)
                        ),
                        RoundedCornerShape(6.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Đã chọn",
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// HEADER BANNER WITH 3D MASCOT / ILLUSTRATION
// -------------------------------------------------------------
@Composable
private fun HeaderBannerWithMascot(
    stepTag: String,
    title: String,
    subtitle: String,
    imageResId: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left Column: Step Pill + Big Title + Description
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Pill tag "Bước X/4"
            Box(
                modifier = Modifier
                    .background(StepPillBg, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = stepTag,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = StepPillText
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Big Bold Title
            Text(
                text = title,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = StepDarkText,
                lineHeight = 28.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Subtitle Description
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = StepMutedText,
                lineHeight = 16.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Right Column: 3D Character / Clock Illustration
        Box(
            modifier = Modifier
                .size(115.dp)
                .clip(RoundedCornerShape(20.dp))
        ) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = "Illustration Header",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

// -------------------------------------------------------------
// STEP 5: YÊU CẦU CẤP QUYỀN THÔNG BÁO (NOTIFICATION PERMISSION)
// -------------------------------------------------------------
@Composable
private fun StepNotificationPermission(
    onEnableNotifications: () -> Unit,
    onSkip: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    // Request Android 13+ Notification Permission
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        onEnableNotifications()
    }

    val requestPermissionAction = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasPermission) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                onEnableNotifications()
            }
        } else {
            onEnableNotifications()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top Back Button
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(BorderStroke(1.dp, Color(0xFFE2E8F0)), CircleShape)
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Quay lại",
                tint = StepDarkText,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Content Area (Perfectly centered in vertical space, scrollable if content overflows on smaller devices)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with 3D Notification Bell
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Bật thông báo\nđể không bỏ lỡ!",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = StepDarkText,
                            lineHeight = 28.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Nhắc nhở học đúng giờ & duy trì chuỗi Streak mỗi ngày.",
                            fontSize = 12.sp,
                            color = StepMutedText,
                            lineHeight = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Box(
                        modifier = Modifier
                            .size(85.dp)
                            .clip(RoundedCornerShape(18.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.mascot_notification_bell_1787293540079),
                            contentDescription = "3D Notification Bell",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3 Compact Benefit Items
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NotificationBenefitCardCompact(
                        iconBgColor = Color(0xFFEDE9FE),
                        iconVector = Icons.Default.AccessTime,
                        iconTint = Color(0xFF7C3AED),
                        title = "Nhắc học đúng giờ",
                        description = "Thông báo nhắc nhở theo lịch đã chọn"
                    )

                    NotificationBenefitCardCompact(
                        iconBgColor = Color(0xFFD1FAE5),
                        iconVector = Icons.Default.LocalFireDepartment,
                        iconTint = Color(0xFF059669),
                        title = "Duy trì chuỗi Streak",
                        description = "Giữ thói quen học tập liên tục mỗi ngày"
                    )

                    NotificationBenefitCardCompact(
                        iconBgColor = StepPurpleLight,
                        iconVector = Icons.Default.Shield,
                        iconTint = StepPurplePrimary,
                        title = "An toàn & Riêng tư",
                        description = "Chỉ gửi thông báo bài học, tắt dễ dàng"
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Compact Mockup Hint Surface
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(StepPurpleLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = StepPurplePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Cho phép NTK FlashCard gửi thông báo?",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = StepDarkText
                            )
                            Text(
                                text = "Nhấn \"Bật thông báo\" rồi chọn \"Cho phép\"",
                                fontSize = 11.sp,
                                color = StepPurplePrimary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Pinned Primary Action: "Bật thông báo" (Always visible at bottom)
        Button(
            onClick = requestPermissionAction,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = StepPurplePrimary.copy(alpha = 0.4f)
                )
                .testTag("enable_notification_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = StepPurplePrimary
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Bật thông báo",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Secondary Action: "Để sau"
        TextButton(
            onClick = onSkip,
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .testTag("skip_notification_button")
        ) {
            Text(
                text = "Để sau",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = StepPurplePrimary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun NotificationBenefitCard(
    iconBgColor: Color,
    iconVector: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    title: String,
    description: String,
    trailingContent: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = StepDarkText
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = StepMutedText,
                    lineHeight = 15.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            trailingContent()
        }
    }
}

@Composable
private fun StepGuidePill(
    number: String,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(StepPurpleLight),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = StepPurplePrimary
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = StepDarkText
        )
    }
}

private data class WidgetSampleWord(
    val word: String,
    val phonetic: String,
    val meaning: String
)

private const val ACTION_WIDGET_PINNED_SUCCESS = "com.example.widget.ACTION_PINNED_SUCCESS"

// -------------------------------------------------------------
// STEP 6: THÊM WIDGET RA MÀN HÌNH CHÍNH (FLASHCARD TODAY WIDGET)
// -------------------------------------------------------------
@Composable
private fun StepAddWidgetHomeScreen(
    selectedLanguage: AppLanguage,
    onAddWidgetAndContinue: () -> Unit,
    onSkip: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val ttsManager = remember { TTSManager(context) }
    DisposableEffect(Unit) {
        onDispose {
            ttsManager.shutdown()
        }
    }

    var isWaitingForPinDialog by remember { mutableStateOf(false) }
    var hasAppBeenPausedWhileWaiting by remember { mutableStateOf(false) }

    // BroadcastReceiver to catch when the user confirms adding the widget in system dialog
    DisposableEffect(context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, intent: Intent?) {
                if (intent?.action == ACTION_WIDGET_PINNED_SUCCESS) {
                    Toast.makeText(context, "Đã thêm Widget FlashCard Today vào màn hình! 🎉", Toast.LENGTH_SHORT).show()
                    isWaitingForPinDialog = false
                    onAddWidgetAndContinue()
                }
            }
        }
        val filter = IntentFilter(ACTION_WIDGET_PINNED_SUCCESS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(receiver, filter)
        }

        onDispose {
            try {
                context.unregisterReceiver(receiver)
            } catch (_: Exception) {}
        }
    }

    // Lifecycle observer: When the user interacts with the system Pin Widget dialog
    // (the app pauses while dialog is open, and resumes once user clicks "Thêm" or "Hủy"/dismiss)
    DisposableEffect(lifecycleOwner, isWaitingForPinDialog) {
        val observer = LifecycleEventObserver { _, event ->
            if (isWaitingForPinDialog) {
                if (event == Lifecycle.Event.ON_PAUSE || event == Lifecycle.Event.ON_STOP) {
                    hasAppBeenPausedWhileWaiting = true
                } else if (event == Lifecycle.Event.ON_RESUME && hasAppBeenPausedWhileWaiting) {
                    // User has finished interacting with the system dialog (either added or cancelled)
                    isWaitingForPinDialog = false
                    hasAppBeenPausedWhileWaiting = false
                    onAddWidgetAndContinue()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Sample words for live widget preview based on language
    val sampleWords: List<WidgetSampleWord> = remember(selectedLanguage) {
        when (selectedLanguage) {
            AppLanguage.ENGLISH -> listOf(
                WidgetSampleWord("Serendipity", "/ˌser.ənˈdɪp.ə.ti/", "Sự tình cờ may mắn, duyên kỳ ngộ"),
                WidgetSampleWord("Eloquent", "/ˈel.ə.kwənt/", "Hùng hồn, diễn đạt lưu loát"),
                WidgetSampleWord("Resilience", "/rɪˈzɪl.jəns/", "Sự kiên cường, khả năng phục hồi")
            )
            AppLanguage.JAPANESE -> listOf(
                WidgetSampleWord("木漏れ日 (Komorebi)", "/こもれび/", "Ánh nắng dịu dàng xuyên qua kẽ lá"),
                WidgetSampleWord("一期一会 (Ichigo Ichie)", "/いちごいちえ/", "Nhất kỳ nhất hội, cuộc gặp một lần trong đời"),
                WidgetSampleWord("木枯らし (Kogarashi)", "/こがらし/", "Cơn gió đông đầu mùa báo hiệu mùa lạnh")
            )
            AppLanguage.KOREAN -> listOf(
                WidgetSampleWord("윤슬 (Yoonseul)", "/yun-seul/", "Ánh sáng lấp lánh phản chiếu trên mặt nước"),
                WidgetSampleWord("설레다 (Seolleda)", "/seol-le-da/", "Cảm giác xao xuyến, rung động con tim"),
                WidgetSampleWord("소확행 (Sohwakhaeng)", "/so-hwak-haeng/", "Hạnh phúc giản dị, nhỏ bé nhưng chắc chắn")
            )
            AppLanguage.VIETNAMESE -> listOf(
                WidgetSampleWord("Thành quả", "/thành quả/", "Kết quả tốt đẹp đạt được sau nỗ lực"),
                WidgetSampleWord("Tích tiểu thành đại", "/thành ngữ/", "Góp nhặt từng chút một để tạo nên điều lớn lao"),
                WidgetSampleWord("Kiên trì", "/kiên trì/", "Bền bỉ theo đuổi mục tiêu đã đề ra")
            )
            AppLanguage.CHINESE -> listOf(
                WidgetSampleWord("千里之行 (Qiānlǐ zhī xíng)", "/qiān lǐ zhī xíng/", "Hành trình ngàn dặm bắt đầu từ bước chân"),
                WidgetSampleWord("温故知新 (Wēngùzhīxīn)", "/wēn gù zhī xīn/", "Ôn cũ biết mới, học từ quá khứ"),
                WidgetSampleWord("持之以恒 (Chízhīyǐhéng)", "/chí zhī yǐ héng/", "Kiên trì bền bỉ không bỏ cuộc")
            )
            AppLanguage.GERMAN -> listOf(
                WidgetSampleWord("Fernweh", "/ˈfɛʁnˌveː/", "Nỗi khao khát được đi đến những miền đất xa xôi"),
                WidgetSampleWord("Wanderlust", "/ˈvandɐlʊst/", "Niềm đam mê khám phá và du lịch"),
                WidgetSampleWord("Gemütlichkeit", "/ɡəˈmyːtlɪçkaɪt/", "Cảm giác ấm cúng, dễ chịu và thân thiện")
            )
            AppLanguage.FRENCH -> listOf(
                WidgetSampleWord("Éphémère", "/e.fe.mɛʁ/", "Phù du, ngắn ngủi nhưng tuyệt mỹ"),
                WidgetSampleWord("Retrouvailles", "/ʁə.tʁu.vaj/", "Niềm vui sướng khi hội ngộ sau thời gian dài"),
                WidgetSampleWord("Déjà vu", "/de.ʒa.vy/", "Cảm giác quen thuộc như đã từng thấy")
            )
            AppLanguage.SPANISH -> listOf(
                WidgetSampleWord("Esperanza", "/es.peˈɾan.sa/", "Niềm hy vọng và sự lạc quan"),
                WidgetSampleWord("Sobremesa", "/so.βɾeˈme.sa/", "Khoảng thời gian trò chuyện ấm áp sau bữa ăn"),
                WidgetSampleWord("Mariposa", "/ma.ɾiˈpo.sa/", "Cánh bướm rực rỡ sắc màu")
            )
            AppLanguage.ITALIAN -> listOf(
                WidgetSampleWord("Serenità", "/se.re.niˈta/", "Sự bình yên, thanh thản trong tâm hồn"),
                WidgetSampleWord("Bel Paese", "/ˈbɛl paˈeːze/", "Đất nước xinh đẹp (Ý)"),
                WidgetSampleWord("Mosaico", "/moˈdza.i.ko/", "Nghệ thuật ghép mảnh tinh tế")
            )
            AppLanguage.PORTUGUESE -> listOf(
                WidgetSampleWord("Saudade", "/sawˈda.dʒi/", "Nỗi nhớ da diết đong đầy ký ức đẹp"),
                WidgetSampleWord("Carinho", "/kaˈɾi.ɲu/", "Sự trìu mến, yêu thương dịu dàng"),
                WidgetSampleWord("Cafuné", "/ka.fuˈnɛ/", "Hành động luôn tay vuốt tóc người yêu")
            )
        }
    }

    var currentWordIndex by remember { mutableIntStateOf(0) }
    val currentWord = sampleWords[currentWordIndex % sampleWords.size]

    val handleAddWidgetAction = {
        try {
            val appWidgetManager = context.getSystemService(AppWidgetManager::class.java)
            val myProvider = ComponentName(context, VocabularyStreakWidgetProvider::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && appWidgetManager != null && appWidgetManager.isRequestPinAppWidgetSupported) {
                val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT
                }
                val successCallback = PendingIntent.getBroadcast(
                    context,
                    1001,
                    Intent(ACTION_WIDGET_PINNED_SUCCESS).apply {
                        setPackage(context.packageName)
                    },
                    flag
                )
                isWaitingForPinDialog = true
                hasAppBeenPausedWhileWaiting = false
                appWidgetManager.requestPinAppWidget(myProvider, null, successCallback)
                Toast.makeText(context, "Chọn 'Thêm vào màn hình chính' hoặc 'Hủy' trong hộp thoại...", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Widget FlashCard Today đã sẵn sàng trong danh sách Widget!", Toast.LENGTH_SHORT).show()
                onAddWidgetAndContinue()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Đã chuẩn bị Widget Flashcard Today!", Toast.LENGTH_SHORT).show()
            onAddWidgetAndContinue()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top Back Button
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(BorderStroke(1.dp, Color(0xFFE2E8F0)), CircleShape)
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Quay lại",
                tint = StepDarkText,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Content Area (Perfectly centered in vertical space, scrollable if content overflows on smaller devices)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            // Header with 3D Widget Illustration
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .background(StepPillBg, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Tiện ích Màn hình chính",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = StepPurplePrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Thêm Widget FlashCard",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = StepDarkText,
                        lineHeight = 28.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Học từ vựng thụ động ngay trên Màn hình chính mỗi khi bật sáng máy.",
                        fontSize = 12.sp,
                        color = StepMutedText,
                        lineHeight = 16.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(18.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.widget_flashcard_preview_1787294014936),
                        contentDescription = "Widget Preview 3D",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ELEGANT & COMPACT WIDGET PREVIEW CARD
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(20.dp),
                        spotColor = StepPurplePrimary.copy(alpha = 0.2f)
                    ),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    // Widget Top Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🗂️", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "FlashCard Today",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = StepDarkText
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Streak Badge
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFFEF3C7), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🔥", fontSize = 10.sp)
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "1 ngày",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFD97706)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            // Audio TTS button
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(StepPurpleLight)
                                    .clickable {
                                        ttsManager.speak(currentWord.word, selectedLanguage.ttsLanguageTag)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Phát âm",
                                    tint = StepPurplePrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(8.dp))

                    // Widget Content (Vocabulary)
                    AnimatedContent(
                        targetState = currentWord,
                        label = "widget_word_anim"
                    ) { (word, phonetic, meaning) ->
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = word,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = StepPurplePrimary
                                )
                                Text(
                                    text = phonetic,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = StepMutedText
                                )
                            }

                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = meaning,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = StepDarkText,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Widget Action row in preview
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "👉 Chạm để mở bài học",
                            fontSize = 10.sp,
                            color = StepMutedText
                        )

                        // Quick Switch button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(StepPurpleLight)
                                .clickable { currentWordIndex++ }
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = StepPurplePrimary,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Đổi từ khác",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StepPurplePrimary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2 Quick Benefits
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WidgetQuickFeatureBadge(
                    icon = "⚡",
                    title = "Học thụ động",
                    subtitle = "Mỗi khi bật màn hình",
                    modifier = Modifier.weight(1f)
                )
                WidgetQuickFeatureBadge(
                    icon = "🔄",
                    title = "Đổi từ liên tục",
                    subtitle = "Tăng ghi nhớ x3",
                    modifier = Modifier.weight(1f)
                )
            }
        }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Pinned Action Button: "Thêm Widget ra màn hình" ALWAYS VISIBLE!
        Button(
            onClick = handleAddWidgetAction,
            enabled = !isWaitingForPinDialog,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = StepPurplePrimary.copy(alpha = 0.4f)
                )
                .testTag("add_widget_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = StepPurplePrimary,
                disabledContainerColor = StepPurplePrimary.copy(alpha = 0.7f)
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isWaitingForPinDialog) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Đang mở hộp thoại...",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Widgets,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Thêm Widget ra màn hình",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Secondary Action: "Để sau"
        TextButton(
            onClick = onSkip,
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .testTag("skip_widget_button")
        ) {
            Text(
                text = "Để sau / Tiếp tục",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = StepPurplePrimary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun NotificationBenefitCardCompact(
    iconBgColor: Color,
    iconVector: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    title: String,
    description: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = StepDarkText
                )
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = StepMutedText
                )
            }
        }
    }
}

@Composable
private fun WidgetQuickFeatureBadge(
    icon: String,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = Color.White,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = StepDarkText
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = StepMutedText
                )
            }
        }
    }
}

@Composable
private fun MockDockAppIcon(
    label: String,
    iconEmoji: String,
    bgColor: Color
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor.copy(alpha = 0.85f)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = iconEmoji, fontSize = 18.sp)
    }
}

@Composable
private fun WidgetBenefitItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    iconBg: Color,
    title: String,
    description: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = StepDarkText
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = StepMutedText,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 7: LINH VẬT ĐANG CHUẨN BỊ FLASHCARDS (MASCOT LOADING)
// -------------------------------------------------------------
@Composable
private fun StepMascotPreparingFlashcards(
    selectedLanguage: AppLanguage,
    selectedLevel: String,
    topicCount: Int,
    reminderHour: Int,
    onReady: () -> Unit
) {
    var progress by remember { mutableFloatStateOf(0f) }
    var isFinished by remember { mutableStateOf(false) }

    // Floating Mascot Animation
    val infiniteTransition = rememberInfiniteTransition(label = "mascot_float")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float_offset"
    )
    val mascotScale by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mascot_scale"
    )

    // Animated progress simulation
    LaunchedEffect(Unit) {
        // Step 1: 0% -> 30%
        delay(200)
        progress = 0.32f
        delay(700)
        // Step 2: 30% -> 65%
        progress = 0.68f
        delay(800)
        // Step 3: 65% -> 90%
        progress = 0.92f
        delay(700)
        // Step 4: 90% -> 100%
        progress = 1.0f
        delay(400)
        isFinished = true
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "progress_anim"
    )

    val currentStatusText = when {
        animatedProgress < 0.35f -> "Đang chọn lọc từ vựng ${selectedLanguage.displayName} (${selectedLanguage.nativeName})..."
        animatedProgress < 0.70f -> "Đang sắp xếp bộ thẻ cho trình độ $selectedLevel..."
        animatedProgress < 0.95f -> "Đang thiết lập $topicCount chủ đề & lịch học ${reminderHour}:00..."
        else -> "Hoàn tất! Bộ thẻ Flashcard đã sẵn sàng 🎉"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Center Mascot with Glowing Circle & Star Accents
        Box(
            modifier = Modifier
                .size(210.dp)
                .offset(y = floatOffset.dp),
            contentAlignment = Alignment.Center
        ) {
            // Background soft glow
            Box(
                modifier = Modifier
                    .size(190.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                StepPurplePrimary.copy(alpha = 0.22f),
                                StepPurpleLight,
                                Color.Transparent
                            )
                        ),
                        CircleShape
                    )
            )

            // Mascot Image
            Image(
                painter = painterResource(id = R.drawable.mascot_loading_flashcards_1787293179472),
                contentDescription = "Mascot preparing flashcards",
                modifier = Modifier
                    .size(175.dp)
                    .scale(mascotScale)
                    .clip(RoundedCornerShape(32.dp))
                    .shadow(12.dp, RoundedCornerShape(32.dp), spotColor = StepPurplePrimary.copy(alpha = 0.35f)),
                contentScale = ContentScale.Crop
            )

            // Floating Sparkle Badge
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-10).dp, y = 10.dp)
                    .background(Color(0xFFFEF08A), CircleShape)
                    .padding(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color(0xFFCA8A04),
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Main Title
        Text(
            text = if (isFinished) "Flashcard của bạn đã sẵn sàng!" else "Đang chuẩn bị thẻ Flashcard...",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = StepDarkText,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Dynamic Subtitle Status
        AnimatedContent(
            targetState = currentStatusText,
            label = "status_text_anim"
        ) { text ->
            Text(
                text = text,
                fontSize = 13.sp,
                color = if (isFinished) StepPurplePrimary else StepMutedText,
                fontWeight = if (isFinished) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Progress Bar & Percentage
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tiến độ chuẩn bị",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = StepDarkText
                    )
                    Text(
                        text = "${(animatedProgress * 100).toInt()}%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = StepPurplePrimary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Smooth Progress Indicator
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = StepPurplePrimary,
                    trackColor = StepPurpleLight
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Checklist of generated setup
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFFF8FAFC),
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PreparationCheckItem(
                    title = "Ngôn ngữ mục tiêu: ${selectedLanguage.displayName} (${selectedLanguage.nativeName})",
                    isDone = animatedProgress >= 0.3f
                )
                PreparationCheckItem(
                    title = "Trình độ & nội dung: $selectedLevel",
                    isDone = animatedProgress >= 0.6f
                )
                PreparationCheckItem(
                    title = "Tùy chỉnh $topicCount chủ đề từ vựng trọng tâm",
                    isDone = animatedProgress >= 0.85f
                )
                PreparationCheckItem(
                    title = "Lịch thông báo nhắc học lúc ${reminderHour}:00 hàng ngày",
                    isDone = animatedProgress >= 0.98f
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Button: Appears when ready
        AnimatedVisibility(
            visible = isFinished,
            enter = fadeIn() + expandVertically()
        ) {
            Button(
                onClick = onReady,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(20.dp),
                        spotColor = StepPurplePrimary.copy(alpha = 0.5f)
                    )
                    .testTag("onboarding_start_learning_button"),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = StepPurplePrimary
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Bắt đầu học ngay 🚀",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        if (!isFinished) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = StepPurplePrimary
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Vui lòng chờ trong giây lát...",
                    fontSize = 12.sp,
                    color = StepMutedText
                )
            }
        }
    }
}

@Composable
private fun PreparationCheckItem(
    title: String,
    isDone: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(if (isDone) Color(0xFF10B981) else Color(0xFFCBD5E1)),
            contentAlignment = Alignment.Center
        ) {
            if (isDone) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = if (isDone) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isDone) StepDarkText else StepMutedText
        )
    }
}


