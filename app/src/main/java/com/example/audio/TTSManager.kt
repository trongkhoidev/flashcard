package com.example.audio

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.util.Log
import java.util.Locale

class TTSManager(context: Context) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var pendingSpeak: Pair<String, String>? = null
    private var lastAppliedTag: String? = null
    private var lastAppliedLocale: Locale? = null

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isInitialized = true
                Log.d("TTSManager", "TTS successfully initialized")
                tts?.setSpeechRate(0.98f)
                tts?.setPitch(1.0f)
                // Execute pending speech if requested before initialization finished
                pendingSpeak?.let { (text, tag) ->
                    pendingSpeak = null
                    speak(text, tag)
                }
            } else {
                Log.e("TTSManager", "TTS init failed with status $status")
            }
        }
    }

    /**
     * Pronounces the given text in the appropriate language.
     * Supports language codes (e.g. "ko", "ja", "vi", "zh", "fr", "de", "es", "it", "pt", "en")
     * and BCP-47 tags (e.g. "ko-KR", "ja-JP", "vi-VN", "zh-CN", "fr-FR", "de-DE", "es-ES", "it-IT", "pt-PT", "en-US").
     */
    fun speak(text: String, languageTag: String = "en-US") {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return

        if (!isInitialized || tts == null) {
            // Buffer the request to speak once initialized
            pendingSpeak = Pair(trimmed, languageTag)
            return
        }

        try {
            val resolvedTag = detectLanguageTag(trimmed, languageTag)
            if (resolvedTag != lastAppliedTag || lastAppliedLocale == null) {
                val appliedLocale = applyTtsLanguage(resolvedTag)
                lastAppliedTag = resolvedTag
                lastAppliedLocale = appliedLocale
                Log.d("TTSManager", "Applied locale '$appliedLocale' for tag '$resolvedTag'")
            }

            val utteranceId = "Peace_${System.currentTimeMillis()}"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val params = Bundle().apply {
                    putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
                    putBoolean(TextToSpeech.Engine.KEY_FEATURE_NETWORK_SYNTHESIS, true)
                }
                tts?.speak(trimmed, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
            } else {
                @Suppress("DEPRECATION")
                val params = HashMap<String, String>().apply {
                    put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
                    put(TextToSpeech.Engine.KEY_FEATURE_NETWORK_SYNTHESIS, "true")
                }
                @Suppress("DEPRECATION")
                tts?.speak(trimmed, TextToSpeech.QUEUE_FLUSH, params)
            }
        } catch (e: Exception) {
            Log.e("TTSManager", "Error speaking: ${e.message}", e)
        }
    }

    /**
     * Resolves the best possible language tag, with automatic script detection fallback
     * if the provided tag is ambiguous or default.
     */
    private fun detectLanguageTag(text: String, tag: String): String {
        val cleanTag = tag.trim().lowercase()

        // If tag is explicitly specific (not just fallback "en" or blank), check known prefixes
        if (cleanTag.isNotEmpty() && cleanTag != "en" && cleanTag != "en-us") {
            return when {
                cleanTag.startsWith("ko") -> "ko-KR"
                cleanTag.startsWith("ja") -> "ja-JP"
                cleanTag.startsWith("zh") || cleanTag.startsWith("cn") -> "zh-CN"
                cleanTag.startsWith("vi") -> "vi-VN"
                cleanTag.startsWith("fr") -> "fr-FR"
                cleanTag.startsWith("de") -> "de-DE"
                cleanTag.startsWith("es") -> "es-ES"
                cleanTag.startsWith("it") -> "it-IT"
                cleanTag.startsWith("pt") -> "pt-PT"
                cleanTag.startsWith("ru") -> "ru-RU"
                else -> cleanTag
            }
        }

        // Automatic script detection based on character sets
        for (char in text) {
            val codePoint = char.code
            // Korean Hangul syllables & Jamo
            if (codePoint in 0xAC00..0xD7AF || codePoint in 0x1100..0x11FF || codePoint in 0x3130..0x318F) {
                return "ko-KR"
            }
            // Japanese Hiragana & Katakana
            if (codePoint in 0x3040..0x309F || codePoint in 0x30A0..0x30FF) {
                return "ja-JP"
            }
        }

        // Chinese CJK Ideographs (when not identified as Japanese)
        if (text.any { it.code in 0x4E00..0x9FFF }) {
            return if (cleanTag.startsWith("ja")) "ja-JP" else "zh-CN"
        }

        // Vietnamese specific diacritics
        val vietnamesePattern = Regex("[áàảãạăắằẳẵặâấầẩẫậéèẻẽẹêếềểễệíìỉĩịóòỏõọôốồổỗộơớờởỡợúùủũụưứừửữựýỳỷỹỵđÁÀẢÃẠĂẮẰẲẴẶÂẤẦẨẪẬÉÈẺẼẸÊẾỀỂỄỆÍÌỈĨỊÓÒỎÕỌÔỐỒỔỖỘƠỚỜỞỠỢÚÙỦŨỤƯỨỪỬỮỰÝỲỶỸỴĐ]")
        if (vietnamesePattern.containsMatchIn(text)) {
            return "vi-VN"
        }

        return when {
            cleanTag.startsWith("ko") -> "ko-KR"
            cleanTag.startsWith("ja") -> "ja-JP"
            cleanTag.startsWith("zh") -> "zh-CN"
            cleanTag.startsWith("vi") -> "vi-VN"
            cleanTag.startsWith("fr") -> "fr-FR"
            cleanTag.startsWith("de") -> "de-DE"
            cleanTag.startsWith("es") -> "es-ES"
            cleanTag.startsWith("it") -> "it-IT"
            cleanTag.startsWith("pt") -> "pt-PT"
            cleanTag.startsWith("ru") -> "ru-RU"
            cleanTag.startsWith("en") -> "en-US"
            else -> "en-US"
        }
    }

    /**
     * Applies the requested language to TextToSpeech engine with fallback cascades and explicit Voice matching.
     */
    private fun applyTtsLanguage(tag: String): Locale {
        val ttsEngine = tts ?: return Locale.US

        val targetLangCode = when {
            tag.startsWith("ko", ignoreCase = true) -> "ko"
            tag.startsWith("ja", ignoreCase = true) -> "ja"
            tag.startsWith("zh", ignoreCase = true) -> "zh"
            tag.startsWith("vi", ignoreCase = true) -> "vi"
            tag.startsWith("fr", ignoreCase = true) -> "fr"
            tag.startsWith("de", ignoreCase = true) -> "de"
            tag.startsWith("es", ignoreCase = true) -> "es"
            tag.startsWith("it", ignoreCase = true) -> "it"
            tag.startsWith("pt", ignoreCase = true) -> "pt"
            tag.startsWith("ru", ignoreCase = true) -> "ru"
            else -> "en"
        }

        // Candidate locales to try in order of precision
        val candidates: List<Locale> = when (targetLangCode) {
            "ko" -> listOf(Locale.KOREAN, Locale.KOREA, Locale("ko", "KR"), Locale("ko"))
            "ja" -> listOf(Locale.JAPANESE, Locale.JAPAN, Locale("ja", "JP"), Locale("ja"))
            "zh" -> listOf(Locale.SIMPLIFIED_CHINESE, Locale.CHINESE, Locale.CHINA, Locale("zh", "CN"), Locale("zh"))
            "vi" -> listOf(Locale("vi", "VN"), Locale("vi"))
            "fr" -> listOf(Locale.FRENCH, Locale.FRANCE, Locale("fr", "FR"), Locale("fr"))
            "de" -> listOf(Locale.GERMAN, Locale.GERMANY, Locale("de", "DE"), Locale("de"))
            "es" -> listOf(Locale("es", "ES"), Locale("es", "MX"), Locale("es"))
            "it" -> listOf(Locale.ITALIAN, Locale.ITALY, Locale("it", "IT"), Locale("it"))
            "pt" -> listOf(Locale("pt", "PT"), Locale("pt", "BR"), Locale("pt"))
            "ru" -> listOf(Locale("ru", "RU"), Locale("ru"))
            else -> listOf(Locale.US, Locale.ENGLISH, Locale.UK)
        }

        // 1. Try matching explicit voice if available on Android 21+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                val availableVoices = ttsEngine.voices
                if (!availableVoices.isNullOrEmpty()) {
                    val matchingVoice = availableVoices.firstOrNull { voice ->
                        voice.locale != null && voice.locale.language.equals(targetLangCode, ignoreCase = true)
                    }
                    if (matchingVoice != null) {
                        ttsEngine.voice = matchingVoice
                        Log.d("TTSManager", "Successfully matched voice: ${matchingVoice.name} for language '$targetLangCode'")
                    }
                }
            } catch (e: Throwable) {
                Log.w("TTSManager", "Error matching TTS voice: ${e.message}")
            }
        }

        // 2. Set language on TTS engine using candidate locales
        for (locale in candidates) {
            try {
                val setResult = ttsEngine.setLanguage(locale)
                if (setResult != TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.d("TTSManager", "Set TTS language to $locale with status $setResult")
                    return locale
                }
            } catch (e: Throwable) {
                Log.w("TTSManager", "Failed to set language $locale: ${e.message}")
            }
        }

        // 3. Fallback: Set first candidate directly
        val fallback = candidates.firstOrNull() ?: Locale.US
        try {
            ttsEngine.language = fallback
        } catch (e: Throwable) {
            Log.e("TTSManager", "Error setting fallback locale: ${e.message}")
        }
        return fallback
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
        pendingSpeak = null
    }
}

