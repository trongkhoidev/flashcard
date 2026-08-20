package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.local.AppDatabase
import com.example.data.model.FlashCardEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class VocabularyStreakWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
        startAutoScroll(context.applicationContext)
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        startAutoScroll(context.applicationContext)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        stopAutoScroll()
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_REFRESH_WORD) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, VocabularyStreakWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }
    }

    companion object {
        const val ACTION_REFRESH_WORD = "com.example.widget.ACTION_REFRESH_WORD"
        private var autoScrollJob: kotlinx.coroutines.Job? = null
        private val widgetScope = CoroutineScope(Dispatchers.Main)

        fun startAutoScroll(context: Context) {
            if (autoScrollJob != null && autoScrollJob?.isActive == true) return
            autoScrollJob = widgetScope.launch {
                while (true) {
                    kotlinx.coroutines.delay(8000L) // Tự động chuyển từ mới sau mỗi 8 giây
                    val intent = Intent(context, VocabularyStreakWidgetProvider::class.java).apply {
                        action = ACTION_REFRESH_WORD
                    }
                    context.sendBroadcast(intent)
                }
            }
        }

        fun stopAutoScroll() {
            autoScrollJob?.cancel()
            autoScrollJob = null
        }

        fun updateAllWidgets(context: Context, streakDays: Int? = null) {
            if (streakDays != null) {
                val prefs = context.getSharedPreferences("app_widget_prefs", Context.MODE_PRIVATE)
                prefs.edit().putInt("app_streak_days", streakDays).apply()
            }
            val intent = Intent(context, VocabularyStreakWidgetProvider::class.java).apply {
                action = ACTION_REFRESH_WORD
            }
            context.sendBroadcast(intent)
        }

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_vocabulary_streak)

            // PendingIntent to launch MainActivity on tapping widget body
            val mainIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val mainPendingIntent = PendingIntent.getActivity(
                context,
                0,
                mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, mainPendingIntent)

            // PendingIntent for Refresh button click
            val refreshIntent = Intent(context, VocabularyStreakWidgetProvider::class.java).apply {
                action = ACTION_REFRESH_WORD
            }
            val refreshPendingIntent = PendingIntent.getBroadcast(
                context,
                appWidgetId,
                refreshIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.btn_widget_refresh, refreshPendingIntent)

            // Read streak count from prefs
            val prefs = context.getSharedPreferences("app_widget_prefs", Context.MODE_PRIVATE)
            val streakCount = prefs.getInt("app_streak_days", 7)
            views.setTextViewText(R.id.tv_widget_streak, "🔥 $streakCount ngày")

            // Query Room DB asynchronously for random unmastered card
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.getDatabase(context)
                    val allCards = db.flashCardDao().getAllCards().firstOrNull() ?: emptyList()
                    val unmasteredCards = allCards.filter { !it.isMastered }
                    val targetCards = if (unmasteredCards.isNotEmpty()) unmasteredCards else allCards

                    val randomCard = targetCards.randomOrNull() ?: FlashCardEntity(
                        deckId = "en_n5",
                        languageCode = "en",
                        frontWord = "Resilient",
                        phonetic = "/rɪˈzɪl.jənt/",
                        partOfSpeech = "adj",
                        frontExample = "He is resilient in facing challenges.",
                        backMeaning = "Kiên cường, phục hồi nhanh",
                        backExampleTranslation = "Anh ấy kiên cường đối mặt với thử thách.",
                        isMastered = false
                    )

                    val posLabel = if (randomCard.partOfSpeech.isNotBlank()) randomCard.partOfSpeech else "từ"
                    val exampleText = if (randomCard.frontExample.isNotBlank()) "💡 ${randomCard.frontExample}" else "💡 Từ vựng đang học"
                    val langDeckName = when (randomCard.languageCode) {
                        "en" -> "🇬🇧 Anh"
                        "ja" -> "🇯🇵 Nhật"
                        "fr" -> "🇫🇷 Pháp"
                        "zh" -> "🇨🇳 Trung"
                        "ko" -> "🇰🇷 Hàn"
                        else -> "🌐 Từ vựng"
                    }

                    views.setTextViewText(R.id.tv_widget_front_word, randomCard.frontWord)
                    views.setTextViewText(R.id.tv_widget_pos, posLabel)
                    views.setTextViewText(R.id.tv_widget_phonetic, randomCard.phonetic)
                    views.setTextViewText(R.id.tv_widget_meaning, randomCard.backMeaning)
                    views.setTextViewText(R.id.tv_widget_example, exampleText)
                    views.setTextViewText(R.id.tv_widget_deck_name, "$langDeckName • Chưa thuộc")

                    appWidgetManager.updateAppWidget(appWidgetId, views)
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Fallback update
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            }
        }
    }
}
