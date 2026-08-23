package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "decks")
data class DeckEntity(
    @PrimaryKey val id: String,
    val languageCode: String,
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val level: String, // "Cơ bản", "Trung cấp", "Nâng cao"
    val colorHex: String,
    val cardCount: Int = 0,
    val isCustom: Boolean = false
)

@Entity(tableName = "flashcards")
data class FlashCardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deckId: String,
    val languageCode: String,
    val frontWord: String,
    val phonetic: String,
    val partOfSpeech: String, // n, v, adj, adv, phrase
    val frontExample: String,
    val backMeaning: String,
    val backExampleTranslation: String,
    val memoryTip: String = "",
    val difficulty: Int = 0, // 0 = New, 1 = Easy, 2 = Medium, 3 = Hard
    val isStarred: Boolean = false,
    val isMastered: Boolean = false,
    val reviewCount: Int = 0,
    val lastReviewedTimestamp: Long = 0L,
    // Spaced Repetition (SRS) & SuperMemo-2 Parameters
    val srsInterval: Int = 1, // Interval in days before next review
    val srsEaseFactor: Float = 2.5f, // Ease multiplier factor (SM-2 default 2.5)
    val srsRepetitions: Int = 0, // Consecutive successful review count
    val nextReviewTimestamp: Long = 0L // Epoch timestamp when card is due for review
)

data class DeckWithStats(
    val deck: DeckEntity,
    val totalCards: Int,
    val masteredCards: Int,
    val learningCards: Int
) {
    val progressPercent: Float
        get() = if (totalCards > 0) masteredCards.toFloat() / totalCards.toFloat() else 0f
}
