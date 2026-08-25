package com.example

import com.example.data.local.LearningPath
import com.example.data.model.DeckEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LearningPathTest {

    private fun deck(id: String, lang: String = "en", level: String) = DeckEntity(
        id = id, languageCode = lang, title = id, subtitle = "",
        iconEmoji = "📘", level = level, colorHex = "#000000",
        cardCount = 10, isCustom = false
    )

    private val decks = listOf(
        deck("deck_en_travel_intermediate", level = "Trung cấp"),
        deck("deck_en_daily_basic", level = "Cơ bản"),
        deck("my_custom_deck", level = "Cơ bản"),
        deck("deck_en_daily_advanced", level = "Nâng cao"),
        deck("deck_ja_daily_basic", level = "Cơ bản", lang = "ja"),
        deck("deck_en_travel_basic", level = "Cơ bản"),
        deck("deck_en_daily_intermediate", level = "Trung cấp"),
        deck("deck_en_travel_advanced", level = "Nâng cao")
    )

    @Test
    fun `path follows level-major ordering and excludes non-path decks`() {
        val path = LearningPath.buildPath(decks, "en")

        assertEquals(
            listOf(
                "deck_en_daily_basic",
                "deck_en_travel_basic",
                "deck_en_daily_intermediate",
                "deck_en_travel_intermediate",
                "deck_en_daily_advanced",
                "deck_en_travel_advanced"
            ),
            path.map { it.id }
        )
    }

    @Test
    fun `path is isolated per language`() {
        val jaPath = LearningPath.buildPath(decks, "ja")
        assertEquals(listOf("deck_ja_daily_basic"), jaPath.map { it.id })
    }

    @Test
    fun `nextDeckAfter skips completed decks`() {
        val path = LearningPath.buildPath(decks, "en")
        val mastered = mapOf(
            "deck_en_daily_basic" to 10,
            "deck_en_travel_basic" to 10,
            "deck_en_daily_intermediate" to 4
        )
        val totals = path.associate { it.id to 10 }

        val next = LearningPath.nextDeckAfter(
            current = path.first { it.id == "deck_en_daily_basic" },
            path = path,
            masteredCountByDeckId = mastered,
            totalCardsByDeckId = totals
        )

        assertEquals("deck_en_daily_intermediate", next?.id)
    }

    @Test
    fun `returns null when entire path completed`() {
        val path = LearningPath.buildPath(decks, "en")
        val allMastered = path.associate { it.id to 10 }
        val totals = path.associate { it.id to 10 }

        val next = LearningPath.nextDeckAfter(path.last(), path, allMastered, totals)
        assertNull(next)
    }

    @Test
    fun `completion requires at least one card and full mastery`() {
        assertTrue(LearningPath.isCompleted(masteredCount = 10, totalCards = 10))
        assertFalse(LearningPath.isCompleted(masteredCount = 9, totalCards = 10))
        assertFalse(LearningPath.isCompleted(masteredCount = 0, totalCards = 0))
        assertFalse(LearningPath.isCompleted(null, null))
    }

    @Test
    fun `deck with unknown level text is excluded from path`() {
        // ID chuẩn nhưng level text không nhận diện được -> KHÔNG tham gia tiến trình
        // (tránh nội dung bonus/tự tạo làm loạn path chính)
        val weird = listOf(
            deck("deck_en_special_bonus", level = "Khó"),
            deck("deck_en_daily_basic", level = "Cơ bản")
        )
        val path = LearningPath.buildPath(weird, "en")
        assertEquals(listOf("deck_en_daily_basic"), path.map { it.id })
        assertFalse(LearningPath.isPathDeck(deck("deck_en_special_bonus", level = "Khó")))
    }
}
