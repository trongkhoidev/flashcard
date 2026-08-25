package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.BubbleChinese
import com.example.ui.theme.BubbleEnglish
import com.example.ui.theme.BubbleFrench
import com.example.ui.theme.BubbleGerman
import com.example.ui.theme.BubbleItalian
import com.example.ui.theme.BubbleJapanese
import com.example.ui.theme.BubbleKorean
import com.example.ui.theme.BubblePortuguese
import com.example.ui.theme.BubbleSpanish
import com.example.ui.theme.BubbleVietnamese

enum class AppLanguage(
    val code: String,
    val displayName: String,
    val nativeName: String,
    val flagEmoji: String,
    val bubbleColor: Color,
    val ttsLanguageTag: String,
    val description: String
) {
    ENGLISH(
        code = "en",
        displayName = "Tiếng Anh",
        nativeName = "English",
        flagEmoji = "🇺🇸",
        bubbleColor = BubbleEnglish,
        ttsLanguageTag = "en-US",
        description = "Từ vựng giao tiếp, TOEIC, IELTS"
    ),
    KOREAN(
        code = "ko",
        displayName = "Tiếng Hàn",
        nativeName = "한국어",
        flagEmoji = "🇰🇷",
        bubbleColor = BubbleKorean,
        ttsLanguageTag = "ko-KR",
        description = "Sơ cấp, Trung cấp, TOPIK I & II"
    ),
    JAPANESE(
        code = "ja",
        displayName = "Tiếng Nhật",
        nativeName = "日本語",
        flagEmoji = "🇯🇵",
        bubbleColor = BubbleJapanese,
        ttsLanguageTag = "ja-JP",
        description = "Hiragana, Katakana, JLPT N5 - N3"
    ),
    VIETNAMESE(
        code = "vi",
        displayName = "Tiếng Việt",
        nativeName = "Tiếng Việt",
        flagEmoji = "🇻🇳",
        bubbleColor = BubbleVietnamese,
        ttsLanguageTag = "vi-VN",
        description = "Giao tiếp hàng ngày, Thành ngữ, Văn hóa"
    ),
    CHINESE(
        code = "zh",
        displayName = "Tiếng Trung",
        nativeName = "中文",
        flagEmoji = "🇨🇳",
        bubbleColor = BubbleChinese,
        ttsLanguageTag = "zh-CN",
        description = "Pinyin, Chữ Hán, HSK 1 - HSK 4"
    ),
    FRENCH(
        code = "fr",
        displayName = "Tiếng Pháp",
        nativeName = "Français",
        flagEmoji = "🇫🇷",
        bubbleColor = BubbleFrench,
        ttsLanguageTag = "fr-FR",
        description = "Giao tiếp cơ bản, Du lịch Paris, DELF A1-B1"
    ),
    SPANISH(
        code = "es",
        displayName = "Tiếng Tây Ban Nha",
        nativeName = "Español",
        flagEmoji = "🇪🇸",
        bubbleColor = BubbleSpanish,
        ttsLanguageTag = "es-ES",
        description = "Từ vựng thông dụng, DELE A1-B2"
    ),
    GERMAN(
        code = "de",
        displayName = "Tiếng Đức",
        nativeName = "Deutsch",
        flagEmoji = "🇩🇪",
        bubbleColor = BubbleGerman,
        ttsLanguageTag = "de-DE",
        description = "Giao tiếp, Ngữ pháp, Goethe A1-B1"
    ),
    ITALIAN(
        code = "it",
        displayName = "Tiếng Ý",
        nativeName = "Italiano",
        flagEmoji = "🇮🇹",
        bubbleColor = BubbleItalian,
        ttsLanguageTag = "it-IT",
        description = "Giao tiếp, Du lịch, Từ vựng đời sống"
    ),
    PORTUGUESE(
        code = "pt",
        displayName = "Tiếng Bồ Đào Nha",
        nativeName = "Português",
        flagEmoji = "🇵🇹",
        bubbleColor = BubblePortuguese,
        ttsLanguageTag = "pt-PT",
        description = "Giao tiếp cơ bản, Bồ Đào Nha & Brasil"
    );

    companion object {
        fun fromCode(code: String): AppLanguage {
            return entries.find { it.code.equals(code, ignoreCase = true) } ?: ENGLISH
        }
    }
}
