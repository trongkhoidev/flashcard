package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.model.DeckEntity
import com.example.data.model.FlashCardEntity
import com.example.data.model.StudySessionEntity
import com.example.data.model.UserLanguageEntity
import com.example.data.repository.FlashCardRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class MasteryProgressTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: FlashCardRepository

    @Before
    fun setUp() {
        runBlocking {
            val context = ApplicationProvider.getApplicationContext<Context>()
            database = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "ntk_flashcard_db"
            )
                .createFromAsset("databases/flashcard_database_expanded.db")
                .allowMainThreadQueries()
                .build()
            repository = FlashCardRepository(database)

            // Dọn dữ liệu demo để test độc lập
            database.clearAllTables()

            // Ngôn ngữ đang học + 2 deck
            database.userLanguageDao().insertLanguages(
                listOf(
                    UserLanguageEntity(languageCode = "en", displayName = "Tiếng Anh", flagEmoji = "🇬🇧", isCurrentActive = true),
                    UserLanguageEntity(languageCode = "ja", displayName = "Tiếng Nhật", flagEmoji = "🇯🇵", isCurrentActive = false)
                )
            )
            val deckDao = database.deckDao()
            deckDao.insertDeck(testDeck("deck_a", "en", cardCount = 3))
            deckDao.insertDeck(testDeck("deck_b", "ja", cardCount = 1))
            insertCard("deck_a", "en", "apple", isMastered = false)
            insertCard("deck_a", "en", "banana", isMastered = false)
            insertCard("deck_a", "en", "cat", isMastered = false)
            insertCard("deck_b", "ja", "ねこ", isMastered = false)
        }
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun testDeck(id: String, lang: String, cardCount: Int) = DeckEntity(
        id = id, languageCode = lang, title = "Deck $id", subtitle = "",
        iconEmoji = "📘", level = "Cơ bản", colorHex = "#000000",
        cardCount = cardCount, isCustom = false
    )

    private suspend fun insertCard(deckId: String, lang: String, word: String, isMastered: Boolean): Long {
        return database.flashCardDao().insertCard(
            FlashCardEntity(
                id = 0, deckId = deckId, languageCode = lang,
                frontWord = word, phonetic = "", partOfSpeech = "n",
                frontExample = "", backMeaning = word, backExampleTranslation = "",
                memoryTip = "", difficulty = 0, isStarred = false,
                isMastered = isMastered, reviewCount = 0, lastReviewedTimestamp = 0L,
                srsInterval = 1, srsEaseFactor = 2.5f, srsRepetitions = 0, nextReviewTimestamp = 0L
            )
        )
    }

    private suspend fun cardsOf(deckId: String) =
        database.flashCardDao().getCardsForDeck(deckId).first()

    @Test
    fun `studying flashcard never changes mastered state`() = runBlocking {
        val apple = cardsOf("deck_a").first { it.frontWord == "apple" }
        val banana = cardsOf("deck_a").first { it.frontWord == "banana" }

        // Học flashcard với difficulty "Easy" (1) — luật cũ sẽ đánh dấu thuộc
        repository.recordCardReview(apple.id, difficulty = 1)
        // Học với difficulty khác
        repository.recordCardReview(banana.id, difficulty = 2)

        val after = cardsOf("deck_a").associateBy { it.id }
        assertFalse(after[apple.id]!!.isMastered)
        assertFalse(after[banana.id]!!.isMastered)
        assertEquals(1, after[apple.id]!!.reviewCount)
        assertEquals(1, after[banana.id]!!.reviewCount)
    }

    @Test
    fun `quiz correct answers master cards and wrong answers demote them`() = runBlocking {
        val (apple, banana, cat) = cardsOf("deck_a").sortedBy { it.frontWord }

        repository.setCardsMasteredState(
            correctIds = listOf(apple.id, banana.id),
            wrongIds = listOf(cat.id),
            langCode = "en"
        )

        val after = cardsOf("deck_a").associateBy { it.id }
        assertTrue(after[apple.id]!!.isMastered)
        assertTrue(after[banana.id]!!.isMastered)
        assertFalse(after[cat.id]!!.isMastered)
        assertEquals(2, database.userLanguageDao().getLanguageDirect("en")!!.masteredCardsCount)
    }

    @Test
    fun `mastery updates are idempotent - no double counting`() = runBlocking {
        val (apple, _, _) = cardsOf("deck_a").sortedBy { it.frontWord }

        repeat(3) {
            repository.setCardsMasteredState(correctIds = listOf(apple.id), wrongIds = emptyList(), langCode = "en")
        }

        assertEquals(1, database.userLanguageDao().getLanguageDirect("en")!!.masteredCardsCount)
        assertEquals(1, cardsOf("deck_a").count { it.isMastered })
    }

    @Test
    fun `demoting a mastered card decrements the per-language count`() = runBlocking {
        val (apple, _, _) = cardsOf("deck_a").sortedBy { it.frontWord }

        repository.setCardsMasteredState(correctIds = listOf(apple.id), wrongIds = emptyList(), langCode = "en")
        assertEquals(1, database.userLanguageDao().getLanguageDirect("en")!!.masteredCardsCount)

        repository.setCardsMasteredState(correctIds = emptyList(), wrongIds = listOf(apple.id), langCode = "en")
        assertFalse(cardsOf("deck_a").first { it.id == apple.id }.isMastered)
        assertEquals(0, database.userLanguageDao().getLanguageDirect("en")!!.masteredCardsCount)
    }

    @Test
    fun `last study session returns the most recent one`() = runBlocking {
        val now = System.currentTimeMillis()
        repository.recordStudySession(session("deck_a", now - 5_000))
        repository.recordStudySession(session("deck_b", now))
        repository.recordStudySession(session("deck_a", now - 60_000))

        val last = repository.getLastStudySession().first()
        assertNotNull(last)
        assertEquals("deck_b", last!!.deckId)
    }

    @Test
    fun `decks with stats aggregates mastered and total correctly`() = runBlocking {
        val apple = cardsOf("deck_a").first { it.frontWord == "apple" }
        val banana = cardsOf("deck_a").first { it.frontWord == "banana" }
        repository.setCardsMasteredState(correctIds = listOf(apple.id, banana.id), wrongIds = emptyList(), langCode = "en")

        val stats = database.flashCardDao().getAllDecksWithStats().first().associateBy { it.deck.id }

        val a = stats["deck_a"]!!
        assertEquals(3, a.totalCards)
        assertEquals(2, a.masteredCards)
        assertEquals(1, a.learningCards)
        assertEquals(2f / 3f, a.progressPercent, 0.001f)

        val b = stats["deck_b"]!!
        assertEquals(1, b.totalCards)
        assertEquals(0, b.masteredCards)
        assertEquals(0f, b.progressPercent, 0.001f)
    }

    private fun session(deckId: String, timestamp: Long) = StudySessionEntity(
        deckId = deckId, deckTitle = "T $deckId", languageCode = if (deckId == "deck_b") "ja" else "en",
        cardsStudied = 3, masteredCount = 1, durationSeconds = 60, timestamp = timestamp
    )
}
