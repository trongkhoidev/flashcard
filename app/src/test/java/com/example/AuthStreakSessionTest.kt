package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.repository.FlashCardRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AuthStreakSessionTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: FlashCardRepository

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
        repository = FlashCardRepository(database)
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun dayMillis(now: Long): Long {
        val cal = java.util.Calendar.getInstance()
        cal.timeInMillis = now
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    @Test
    fun `register new user resets profile to clean state`() = runBlocking {
        val success = repository.registerUser("hocvien_moi", "hashed_pw")
        assertEquals(true, success)

        val profile = database.userProfileDao().getUserProfileDirect()
        assertNotNull(profile)
        assertEquals("hocvien_moi", profile!!.userName)
        assertEquals(0, profile.streakDays)
        assertEquals(0, profile.maxStreakDays)
        assertEquals(0, profile.totalPoints)
        assertEquals(0, profile.totalCardsLearned)
        assertEquals(1, profile.vipLevel)

        val account = repository.getActiveLoggedInUserDirect()
        assertNotNull(account)
        assertEquals("hocvien_moi", account!!.username)
    }

    @Test
    fun `register fails when username already exists`() = runBlocking {
        val first = repository.registerUser("trung_lap", "x")
        assertEquals(true, first)
        val second = repository.registerUser("trung_lap", "y")
        assertEquals(false, second)
    }

    @Test
    fun `streak unchanged when studying again in same day`() = runBlocking {
        val now = System.currentTimeMillis()
        database.userProfileDao().updateStreak(3, now - 3_600_000L)
        repository.updateDailyStreakIfNeeded(now)
        assertEquals(3, database.userProfileDao().getUserProfileDirect()!!.streakDays)
    }

    @Test
    fun `streak increments by one when studying next day`() = runBlocking {
        val now = System.currentTimeMillis()
        database.userProfileDao().updateStreak(3, dayMillis(now) - 86_400_000L + 3_600_000L)
        repository.updateDailyStreakIfNeeded(now)
        assertEquals(4, database.userProfileDao().getUserProfileDirect()!!.streakDays)
    }

    @Test
    fun `streak resets to one after missing two or more days`() = runBlocking {
        val now = System.currentTimeMillis()
        database.userProfileDao().updateStreak(9, dayMillis(now) - 3 * 86_400_000L + 3_600_000L)
        repository.updateDailyStreakIfNeeded(now)
        assertEquals(1, database.userProfileDao().getUserProfileDirect()!!.streakDays)
    }

    @Test
    fun `logged in session is restored then cleared on logout`() = runBlocking {
        repository.registerUser("session_user", "pw")
        assertNotNull(repository.getActiveLoggedInUserDirect())

        repository.logoutUser()
        assertNull(repository.getActiveLoggedInUserDirect())
    }
}
