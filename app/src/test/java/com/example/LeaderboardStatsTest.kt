package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.model.QuizRecordEntity
import com.example.data.repository.FlashCardRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Calendar

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class LeaderboardStatsTest {

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
        runBlocking { database.clearAllTables() }
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun record(points: Int, timestamp: Long) = QuizRecordEntity(
        deckId = "deck_a", deckTitle = "T", mode = "QUIZ",
        score = 1, totalQuestions = 1, pointsEarned = points, maxStreak = 1,
        accuracyPercent = 100f, timeSpentSeconds = 30, timestamp = timestamp
    )

    private fun startOfWeek(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
        val dow = cal.get(Calendar.DAY_OF_WEEK)
        cal.add(Calendar.DAY_OF_YEAR, if (dow == Calendar.SUNDAY) -6 else -(dow - 2))
        return cal.timeInMillis
    }

    private fun startOfMonth(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    @Test
    fun `points since filters by period boundary`() = runBlocking {
        val weekStart = startOfWeek()
        val monthStart = startOfMonth()

        // Trước CẢ hai mốc -> bị loại khỏi cả tuần lẫn tháng
        val longAgo = minOf(weekStart, monthStart) - 86_400_000L * 10
        database.quizRecordDao().insertRecord(record(100, longAgo))

        // Sau cả hai mốc -> tính vào cả Tuần và Tháng
        database.quizRecordDao().insertRecord(record(200, maxOf(weekStart, monthStart) + 1_000))

        // Ngay sau đầu tuần -> luôn tính vào Tuần; chỉ tính vào Tháng nếu đầu tuần >= đầu tháng
        database.quizRecordDao().insertRecord(record(150, weekStart + 1_000))

        assertEquals(350, repository.getPointsEarnedSince(weekStart).first())
        val expectedMonthly = if (weekStart >= monthStart) 350 else 200
        assertEquals(expectedMonthly, repository.getPointsEarnedSince(monthStart).first())
    }

    @Test
    fun `points since is zero when no records in period`() = runBlocking {
        assertEquals(0, repository.getPointsEarnedSince(System.currentTimeMillis()).first())
    }
}
