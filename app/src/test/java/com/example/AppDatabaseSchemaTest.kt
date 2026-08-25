package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.model.DeckEntity
import com.example.data.model.FlashCardEntity
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AppDatabaseSchemaTest {

    private lateinit var database: AppDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "ntk_flashcard_db"
        )
            .createFromAsset("databases/flashcard_database_expanded.db")
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `prepackaged database passes room schema validation`() = runBlocking {
        val decks = database.deckDao().getAllDecks().firstOrNull().orEmpty()
        val cards = database.flashCardDao().getTotalCardsCount().firstOrNull()
        assertEquals(12, decks.size)
        assertEquals(120, cards)
    }

    @Test
    fun `foreign key cascade delete removes cards of deleted deck`() = runBlocking {
        val deckDao = database.deckDao()
        val cardDao = database.flashCardDao()
        val deck = DeckEntity(
            id = "test_fk_deck",
            languageCode = "en",
            title = "FK Test",
            subtitle = "test",
            iconEmoji = "🧪",
            level = "Cơ bản",
            colorHex = "#000000",
            cardCount = 1,
            isCustom = true
        )
        deckDao.insertDeck(deck)
        cardDao.insertCard(
            FlashCardEntity(
                deckId = "test_fk_deck",
                languageCode = "en",
                frontWord = "hello",
                phonetic = "/həˈloʊ/",
                partOfSpeech = "int",
                frontExample = "Hello!",
                backMeaning = "xin chào",
                backExampleTranslation = "Xin chào!"
            )
        )

        deckDao.deleteDeck(deck)

        val remaining = cardDao.getCardsForDeck("test_fk_deck").firstOrNull().orEmpty()
        assertTrue(remaining.isEmpty())
    }
}
