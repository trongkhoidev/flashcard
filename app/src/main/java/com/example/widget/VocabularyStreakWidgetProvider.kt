package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.local.AppDatabase
import com.example.data.model.FlashCardEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.isActive
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

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            appWidgetIds.forEach { id ->
                remove(keyCardIndex(id))
                remove(keyFlipped(id))
            }
        }.apply()
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        when (intent.action) {
            ACTION_REFRESH_WORD -> {
                val requestedId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID
                )
                if (requestedId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                    // Bấm "Từ khác": chuyển sang thẻ kế tiếp của đúng widget đó
                    advanceAndRender(context, appWidgetManager, requestedId)
                } else {
                    // Auto-rotate / cập nhật từ app: đổi từ cho mọi widget
                    currentWidgetIds(context, appWidgetManager).forEach { id ->
                        advanceAndRender(context, appWidgetManager, id)
                    }
                }
            }
            ACTION_TOGGLE_FLIP -> {
                val appWidgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID
                )
                if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    val flipped = !prefs.getBoolean(keyFlipped(appWidgetId), false)
                    prefs.edit().putBoolean(keyFlipped(appWidgetId), flipped).apply()
                    updateAppWidget(context, appWidgetManager, appWidgetId)
                }
            }
        }
    }

    private fun advanceAndRender(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val next = clampIndex(prefs.getInt(keyCardIndex(appWidgetId), 0) + 1, Int.MAX_VALUE)
        prefs.edit()
            .putInt(keyCardIndex(appWidgetId), next)
            .putBoolean(keyFlipped(appWidgetId), false)
            .apply()
        updateAppWidget(context, appWidgetManager, appWidgetId)
    }

    companion object {
        const val ACTION_REFRESH_WORD = "com.example.widget.ACTION_REFRESH_WORD"
        const val ACTION_TOGGLE_FLIP = "com.example.widget.ACTION_TOGGLE_FLIP"
        private const val PREFS_NAME = "app_widget_prefs"

        private var autoScrollJob: Job? = null
        private val widgetScope = CoroutineScope(Dispatchers.Main)

        // ---- Keys theo từng widget instance ----
        private fun keyCardIndex(appWidgetId: Int) = "card_index_$appWidgetId"
        private fun keyFlipped(appWidgetId: Int) = "flipped_$appWidgetId"

        /**
         * Chọn danh sách thẻ hiển thị trên widget — dữ liệu THẬT của ngôn ngữ đang học:
         * 1. Thẻ đến hạn / chưa thuộc (đã sắp nextReviewTimestamp ASC từ DAO)
         * 2. Fallback: toàn bộ thẻ ngôn ngữ đó
         */
        fun pickDisplayCards(dueOrUnmastered: List<FlashCardEntity>, allOfLanguage: List<FlashCardEntity>): List<FlashCardEntity> {
            return dueOrUnmastered.ifEmpty { allOfLanguage }
        }

        fun clampIndex(index: Int, size: Int): Int =
            if (size <= 0) 0 else ((index % size) + size) % size

        private fun currentWidgetIds(context: Context, manager: AppWidgetManager): IntArray =
            manager.getAppWidgetIds(ComponentName(context, VocabularyStreakWidgetProvider::class.java))

        /**
         * Tự động đổi từ mỗi 8s — ĐÃ TỐI ƯU: chỉ chạy khi còn widget VÀ màn hình đang bật.
         */
        fun startAutoScroll(context: Context) {
            if (autoScrollJob?.isActive == true) return
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            autoScrollJob = widgetScope.launch {
                while (isActive) {
                    delay(8000L)
                    val manager = AppWidgetManager.getInstance(context)
                    val hasWidgets = currentWidgetIds(context, manager).isNotEmpty()
                    if (!hasWidgets || !powerManager.isInteractive) continue
                    context.sendBroadcast(
                        Intent(context, VocabularyStreakWidgetProvider::class.java).apply {
                            action = ACTION_REFRESH_WORD
                        }
                    )
                }
            }
        }

        fun stopAutoScroll() {
            autoScrollJob?.cancel()
            autoScrollJob = null
        }

        fun updateAllWidgets(context: Context, streakDays: Int? = null) {
            if (streakDays != null) {
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .edit().putInt("app_streak_days", streakDays).apply()
            }
            context.sendBroadcast(
                Intent(context, VocabularyStreakWidgetProvider::class.java).apply {
                    action = ACTION_REFRESH_WORD
                }
            )
        }

        private fun openAppPendingIntent(context: Context, requestCode: Int): PendingIntent {
            val mainIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            return PendingIntent.getActivity(
                context,
                requestCode,
                mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        private fun broadcastPendingIntent(
            context: Context,
            action: String,
            appWidgetId: Int
        ): PendingIntent {
            val intent = Intent(context, VocabularyStreakWidgetProvider::class.java).apply {
                this.action = action
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            return PendingIntent.getBroadcast(
                context,
                appWidgetId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        private fun languageLabel(languageCode: String): String = when (languageCode) {
            "en" -> "🇬🇧 Anh"
            "ja" -> "🇯🇵 Nhật"
            "fr" -> "🇫🇷 Pháp"
            "zh" -> "🇨🇳 Trung"
            "ko" -> "🇰🇷 Hàn"
            "vi" -> "🇻🇳 Việt"
            "es" -> "🇪🇸 Tây Ban Nha"
            "de" -> "🇩🇪 Đức"
            "it" -> "🇮🇹 Ý"
            "pt" -> "🇵🇹 Bồ Đào Nha"
            else -> "🌐 Từ vựng"
        }

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_vocabulary_streak)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    render(context, appWidgetManager, appWidgetId, views)
                } catch (e: Exception) {
                    e.printStackTrace()
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            }
        }

        private suspend fun render(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            views: RemoteViews
        ) {
            val db = AppDatabase.getDatabase(context)
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

            // ===== DỮ LIỆU THẬT =====
            val activeLanguage = db.userLanguageDao().getActiveLearningLanguageDirect()
            val langCode = activeLanguage?.languageCode ?: "en"
            val langLabel = activeLanguage?.let { "${languageLabel(it.languageCode)}" } ?: "🌐 Từ vựng"
            val now = System.currentTimeMillis()

            val streak = db.userProfileDao().getUserProfileDirect()?.streakDays ?: 0
            val dueCount = db.flashCardDao().getDueCountForLanguage(langCode, now)
                .firstOrNull() ?: 0

            val dueOrUnmastered = db.flashCardDao()
                .getDueCardsForLanguage(langCode, now)
                .firstOrNull()
                .orEmpty()
            val allOfLang = db.flashCardDao()
                .getAllCardsByLanguage(langCode)
                .firstOrNull()
                .orEmpty()

            val displayCards = pickDisplayCards(dueOrUnmastered, allOfLang)
            val fallbackCard = FlashCardEntity(
                deckId = "", languageCode = langCode,
                frontWord = "Học ngay nhé!", phonetic = "",
                partOfSpeech = "", frontExample = "💡 Mở app để thêm thẻ mới.",
                backMeaning = "Chưa có từ nào", backExampleTranslation = ""
            )
            val card = displayCards.getOrNull(clampIndex(prefs.getInt(keyCardIndex(appWidgetId), 0), displayCards.size))
                ?: displayCards.firstOrNull()
                ?: fallbackCard

            val deckLabel = db.deckDao().getDeckById(card.deckId)?.title

            // ===== RENDER =====
            val posLabel = card.partOfSpeech.ifBlank { "" }
            views.setTextViewText(R.id.tv_widget_header, "📖 Từ đang học • $langLabel")
            views.setTextViewText(R.id.tv_widget_streak, "🔥 $streak ngày")

            // Mặt trước
            views.setTextViewText(R.id.tv_widget_front_word, card.frontWord)
            views.setTextViewText(R.id.tv_widget_pos, posLabel)
            views.setTextViewText(R.id.tv_widget_phonetic, card.phonetic)
            views.setTextViewText(
                R.id.tv_widget_example,
                card.frontExample.ifBlank { "💡 Từ vựng đang học" }.let { if (it.startsWith("💡")) it else "💡 $it" }
            )

            // Mặt sau
            views.setTextViewText(R.id.tv_widget_back_hint_word, "$langLabel ${card.frontWord} • ${posLabel}".trim())
            views.setTextViewText(R.id.tv_widget_meaning, card.backMeaning)
            views.setTextViewText(
                R.id.tv_widget_back_translation,
                card.backExampleTranslation.ifBlank { "" }.let { if (it.isBlank()) "" else "💡 $it" }
            )

            // Footer: cần ôn + deck hiện tại
            val footerParts = mutableListOf<String>()
            footerParts.add("⏰ Cần ôn: $dueCount từ")
            deckLabel?.let { footerParts.add(it) }
            if (card.isMastered) footerParts.add("✅ Đã thuộc") else footerParts.add("📌 Chưa thuộc")
            views.setTextViewText(R.id.tv_widget_deck_name, footerParts.joinToString(" • "))

            // ===== PHÂN VÙNG CHẠM =====
            // Thẻ (ViewFlipper) = lật mặt
            views.setOnClickPendingIntent(
                R.id.widget_flipper,
                broadcastPendingIntent(context, ACTION_TOGGLE_FLIP, appWidgetId)
            )
            // Header + badge streak + footer = mở app
            val openApp = openAppPendingIntent(context, appWidgetId)
            views.setOnClickPendingIntent(R.id.layout_widget_header, openApp)
            views.setOnClickPendingIntent(R.id.layout_widget_footer, openApp)
            // Nút "Từ khác" = thẻ kế tiếp
            views.setOnClickPendingIntent(
                R.id.btn_widget_refresh,
                broadcastPendingIntent(context, ACTION_REFRESH_WORD, appWidgetId)
            )

            // Trạng thái lật hiện tại
            val flipped = prefs.getBoolean(keyFlipped(appWidgetId), false)
            views.setInt(R.id.widget_flipper, "setDisplayedChild", if (flipped) 1 else 0)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
