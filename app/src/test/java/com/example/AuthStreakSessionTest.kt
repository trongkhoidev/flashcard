package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.model.AppLanguage
import com.example.data.model.DeckEntity
import com.example.data.model.FlashCardEntity
import com.example.data.model.UserProfileEntity
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

        val profile = repository.getUserProfileDirect()
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
        repository.saveUserProfile(
            UserProfileEntity(
                id = 1,
                userName = "tester",
                streakDays = 3,
                lastActiveTimestamp = now - 3_600_000L
            )
        )
        repository.updateDailyStreakIfNeeded(now)
        assertEquals(3, repository.getUserProfileDirect()!!.streakDays)
    }

    @Test
    fun `streak increments by one when studying next day`() = runBlocking {
        val now = System.currentTimeMillis()
        repository.saveUserProfile(
            UserProfileEntity(
                id = 1,
                userName = "tester",
                streakDays = 3,
                lastActiveTimestamp = dayMillis(now) - 86_400_000L + 3_600_000L
            )
        )
        repository.updateDailyStreakIfNeeded(now)
        assertEquals(4, repository.getUserProfileDirect()!!.streakDays)
    }

    @Test
    fun `streak resets to one after missing two or more days`() = runBlocking {
        val now = System.currentTimeMillis()
        repository.saveUserProfile(
            UserProfileEntity(
                id = 1,
                userName = "tester",
                streakDays = 9,
                lastActiveTimestamp = dayMillis(now) - 3 * 86_400_000L + 3_600_000L
            )
        )
        repository.updateDailyStreakIfNeeded(now)
        assertEquals(1, repository.getUserProfileDirect()!!.streakDays)
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

    @Test
    fun `saved cards are isolated per user and cleared on logout`() = runBlocking {
        // 1. User A đăng ký và lưu thẻ số 1 và 2
        repository.registerUser("user_a", "pw_a")
        val userA = repository.getActiveLoggedInUserDirect()
        assertNotNull(userA)

        repository.toggleStar(1L, currentStarred = false)
        repository.toggleStar(2L, currentStarred = false)

        val starredCardsUserA = repository.getStarredCards().firstOrNull().orEmpty()
        assertEquals(2, starredCardsUserA.size)
        assertEquals(setOf(1L, 2L), starredCardsUserA.map { it.id }.toSet())

        // 2. User A logout -> danh sách từ đã lưu hiển thị phải trống (0 thẻ)
        repository.logoutUser()
        val starredCardsAfterLogout = repository.getStarredCards().firstOrNull().orEmpty()
        assertEquals(0, starredCardsAfterLogout.size)

        // 3. User B đăng ký mới -> không nhìn thấy thẻ của User A, lưu thẻ số 3
        repository.registerUser("user_b", "pw_b")
        val starredCardsUserBInitial = repository.getStarredCards().firstOrNull().orEmpty()
        assertEquals(0, starredCardsUserBInitial.size)

        repository.toggleStar(3L, currentStarred = false)
        val starredCardsUserB = repository.getStarredCards().firstOrNull().orEmpty()
        assertEquals(1, starredCardsUserB.size)
        assertEquals(3L, starredCardsUserB[0].id)

        // 4. Logout User B và đăng nhập lại User A -> Thẻ 1 và 2 của User A được phục hồi nguyên vẹn
        repository.logoutUser()
        val loggedInUserA = repository.authenticateUser("user_a", "pw_a")
        assertNotNull(loggedInUserA)
        val restoredCardsUserA = repository.getStarredCards().firstOrNull().orEmpty()
        assertEquals(2, restoredCardsUserA.size)
        assertEquals(setOf(1L, 2L), restoredCardsUserA.map { it.id }.toSet())
    }

    @Test
    fun `mastered cards and daily goals are isolated per user and cleared on logout`() = runBlocking {
        // 1. User A đăng ký và học thuộc 2 từ (id 1 và 2)
        repository.registerUser("user_goal_a", "pw_goal_a")
        val userA = repository.getActiveLoggedInUserDirect()
        assertNotNull(userA)

        repository.markCardMastered(1L, "en")
        repository.markCardMastered(2L, "en")

        val masteredCardsUserA = repository.getMasteredCount().firstOrNull() ?: 0
        assertEquals(2, masteredCardsUserA)

        // 2. User A logout -> tiến trình từ đã thuộc hiển thị về 0
        repository.logoutUser()
        val masteredAfterLogout = repository.getMasteredCount().firstOrNull() ?: 0
        assertEquals(0, masteredAfterLogout)

        // 3. User B đăng ký mới -> ban đầu có 0 từ đã thuộc (Mục tiêu hôm nay 0/20)
        repository.registerUser("user_goal_b", "pw_goal_b")
        val masteredUserBInitial = repository.getMasteredCount().firstOrNull() ?: 0
        assertEquals(0, masteredUserBInitial)

        // User B học thuộc 1 từ (id 3)
        repository.markCardMastered(3L, "en")
        val masteredUserB = repository.getMasteredCount().firstOrNull() ?: 0
        assertEquals(1, masteredUserB)

        // 4. Logout User B và đăng nhập lại User A -> Khôi phục chính xác 2 từ đã thuộc của User A
        repository.logoutUser()
        val loggedInUserA = repository.authenticateUser("user_goal_a", "pw_goal_a")
        assertNotNull(loggedInUserA)
        val restoredMasteredUserA = repository.getMasteredCount().firstOrNull() ?: 0
        assertEquals(2, restoredMasteredUserA)
    }

    @Test
    fun `direct registration creates starter deck and new user profile ready for welcome hero card`() = runBlocking {
        // Đăng ký trực tiếp trước khi làm step
        val username = "tuan_direct"
        val regSuccess = repository.registerUser(username, "strong_password_123")
        assertEquals(true, regSuccess)

        val profile = repository.getUserProfileDirect()
        assertNotNull(profile)
        assertEquals(username, profile!!.userName)
        assertEquals(0, profile.streakDays)
        assertEquals(0, profile.totalCardsLearned)

        // Sau khi đăng ký, tiến hành onboarding chọn ngôn ngữ (ví dụ Tiếng Nhật)
        val selectedLang = AppLanguage.JAPANESE
        repository.addLearningLanguage(selectedLang)
        repository.switchActiveLanguage(selectedLang.code)
        val starterDeckId = "${selectedLang.code}_starter"
        val starterCards = com.example.data.local.StarterVocabData.getStarterCardsForLanguage(selectedLang)
        repository.insertDeck(
            DeckEntity(
                id = starterDeckId,
                languageCode = selectedLang.code,
                title = "${selectedLang.displayName} Khởi động",
                subtitle = "Bộ từ vựng khởi đầu cho người mới bắt đầu",
                iconEmoji = "🚀",
                level = "Mới bắt đầu",
                colorHex = "#10B981",
                cardCount = starterCards.size,
                isCustom = false
            )
        )
        starterCards.forEach { card ->
            repository.insertCard(card.copy(id = 0L))
        }
        repository.updateStreak(1)

        val activeDeck = repository.getDeckById(starterDeckId)
        assertNotNull(activeDeck)
        assertEquals("Tiếng Nhật Khởi động", activeDeck!!.title)

        val deckCards = repository.getCardsForDeck(starterDeckId).firstOrNull() ?: emptyList()
        assertEquals(starterCards.size, deckCards.size)

        // Kiểm tra điều kiện isFirstTimeUser
        val updatedProfile = repository.getUserProfileDirect()
        assertNotNull(updatedProfile)
        assertEquals(1, updatedProfile!!.streakDays)
        val masteredCount = repository.getMasteredCount().firstOrNull() ?: 0
        assertEquals(0, masteredCount)
        val isFirstTimeUser = masteredCount == 0 && updatedProfile.streakDays <= 1
        assertEquals(true, isFirstTimeUser)
    }
}
