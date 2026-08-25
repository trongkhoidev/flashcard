package com.example.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class TTSManager(context: Context) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isInitialized = true
                tts?.language = Locale.US
            } else {
                Log.e("TTSManager", "Init failed with status $status")
            }
        }
    }

    fun speak(text: String, languageTag: String = "en-US") {
        if (!isInitialized || tts == null) return
        try {
            val locale = when {
                languageTag.startsWith("ko", ignoreCase = true) -> Locale.KOREA
                languageTag.startsWith("ja", ignoreCase = true) -> Locale.JAPAN
                languageTag.startsWith("zh", ignoreCase = true) -> Locale.SIMPLIFIED_CHINESE
                languageTag.startsWith("fr", ignoreCase = true) -> Locale.FRANCE
                languageTag.startsWith("de", ignoreCase = true) -> Locale.GERMANY
                languageTag.startsWith("es", ignoreCase = true) -> Locale("es", "ES")
                languageTag.startsWith("vi", ignoreCase = true) -> Locale("vi", "VN")
                else -> Locale.US
            }

            tts?.language = locale
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "NTK_SPEECH_${System.currentTimeMillis()}")
        } catch (e: Exception) {
            Log.e("TTSManager", "Error speaking: ${e.message}")
        }
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
