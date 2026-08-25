package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.repository.FlashCardRepository
import kotlinx.coroutines.flow.firstOrNull
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

    @Test
    fun `chosen japanese language stays active for session restore and relogin`() = runBlocking {
        repository.registerUser("ja_user", "pw")
        repository.addLearningLanguage(com.example.data.model.AppLanguage.JAPANESE)
        repository.switchActiveLanguage(com.example.data.model.AppLanguage.JAPANESE.code)

        val langs = repository.getAllLearningLanguages().firstOrNull().orEmpty()
        val active = langs.firstOrNull { it.isCurrentActive }
        assertNotNull(active)
        assertEquals("ja", active!!.languageCode)
        assertEquals(listOf("ja"), langs.filter { it.isCurrentActive }.map { it.languageCode })
    }

    @Test
    fun `fresh install must not auto login into any demo account`() = runBlocking {
        assertNull(repository.getActiveLoggedInUserDirect())
    }

    @Test
    fun `after registration session belongs to the newly created account`() = runBlocking {
        repository.registerUser("nguoi_vua_tao", "pw")
        val active = repository.getActiveLoggedInUserDirect()
        assertNotNull(active)
        assertEquals("nguoi_vua_tao", active!!.username)
    }

    @Test
    fun `tuanzeebee can login with 123456 via sha256 path`() = runBlocking {
        val hash = com.example.data.local.PasswordHasher.sha256("123456")
        assertEquals("8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92", hash)

        val user = repository.authenticateUser("tuanzeebee", hash)
        assertNotNull("tuanzeebee/123456 phải đăng nhập được qua đường SHA-256", user)
        assertEquals("tuanzeebee", user!!.username)

        val session = repository.getActiveLoggedInUserDirect()
        assertNotNull(session)
        assertEquals("tuanzeebee", session!!.username)
    }

    @Test
    fun `session survives full app restart simulation`() = runBlocking {
        val hash = com.example.data.local.PasswordHasher.sha256("123456")
        assertNotNull(repository.authenticateUser("tuanzeebee", hash))
        database.close()

        // Mở lại "process mới": instance Room mới trên cùng file DB
        val context = ApplicationProvider.getApplicationContext<Context>()
        val reopened = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "ntk_flashcard_db"
        ).createFromAsset("databases/flashcard_database_expanded.db").allowMainThreadQueries().build()

        val restored = reopened.userAccountDao().getActiveLoggedInUserDirect()
        assertNotNull("Sau khi mở lại app phải còn phiên đăng nhập", restored)
        assertEquals("tuanzeebee", restored!!.username)
        reopened.close()
    }

    @Test
    fun `legacy plaintext account can still login and gets upgraded to hash`() = runBlocking {
        // Giả lập tài khoản tạo từ bản cũ: mật khẩu lưu plaintext trong DB
        database.userAccountDao().insertOrUpdate(
            com.example.data.model.UserAccountEntity(
                id = 0,
                username = "cu_khong_hash",
                passwordHash = "matkhau_goc",
                isLoggedIn = false
            )
        )

        val hashed = com.example.data.local.PasswordHasher.sha256("matkhau_goc")
        assertNull(repository.authenticateUser("cu_khong_hash", hashed))

        val legacy = repository.authenticateLegacy("cu_khong_hash", "matkhau_goc")
        assertNotNull("Tài khoản plaintext cũ phải login được qua fallback", legacy)

        repository.updateAccountPassword("cu_khong_hash", hashed)
        assertNotNull(repository.authenticateUser("cu_khong_hash", hashed))
        assertNull(repository.authenticateLegacy("cu_khong_hash", "matkhau_goc"))
    }
}
