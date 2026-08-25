package com.example

import com.example.data.model.FlashCardEntity
import com.example.widget.VocabularyStreakWidgetProvider
import org.junit.Assert.assertEquals
import org.junit.Test

class WidgetCardPickerTest {

    private fun card(id: Long, word: String, nextReview: Long, mastered: Boolean = false) =
        FlashCardEntity(
            id = id, deckId = "deck_a", languageCode = "en",
            frontWord = word, phonetic = "", partOfSpeech = "",
            frontExample = "", backMeaning = word, backExampleTranslation = "",
            memoryTip = "", difficulty = 0, isStarred = false,
            isMastered = mastered, reviewCount = 0, lastReviewedTimestamp = 0L,
            srsInterval = 1, srsEaseFactor = 2.5f, srsRepetitions = 0,
            nextReviewTimestamp = nextReview
        )

    @Test
    fun `prefer due cards over general list`() {
        val due = listOf(card(1, "due1", 100), card(2, "due2", 200))
        val all = listOf(card(3, "other", 0), card(9, "mastered", 0, mastered = true))

        val picked = VocabularyStreakWidgetProvider.pickDisplayCards(due, all)

        assertEquals(listOf("due1", "due2"), picked.map { it.frontWord })
    }

    @Test
    fun `fallback to all cards of language when nothing due`() {
        val all = listOf(card(3, "a", 0), card(4, "b", 10))
        val picked = VocabularyStreakWidgetProvider.pickDisplayCards(emptyList(), all)
        assertEquals(all, picked)
    }

    @Test
    fun `clampIndex wraps around and handles empty list`() {
        assertEquals(0, VocabularyStreakWidgetProvider.clampIndex(0, 3))
        assertEquals(1, VocabularyStreakWidgetProvider.clampIndex(1, 3))
        assertEquals(0, VocabularyStreakWidgetProvider.clampIndex(3, 3))
        assertEquals(2, VocabularyStreakWidgetProvider.clampIndex(-1, 3))
        assertEquals(0, VocabularyStreakWidgetProvider.clampIndex(99, 0))
    }

    @Test
    fun `advance sequence cycles through due cards in order`() {
        val due = listOf(
            card(1, "w1", 100),
            card(2, "w2", 200),
            card(3, "w3", 300)
        )
        val size = due.size
        val words = (0 until size * 2).map { i ->
            due[VocabularyStreakWidgetProvider.clampIndex(i, size)].frontWord
        }
        assertEquals(listOf("w1", "w2", "w3", "w1", "w2", "w3"), words)
    }
}
