package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AppLanguage
import com.example.data.model.DeckEntity
import com.example.data.model.FlashCardEntity
import com.example.data.model.QuizRecordEntity
import com.example.data.model.StudyScheduleEntity
import com.example.data.model.StudySessionEntity
import com.example.data.model.UserAccountEntity
import com.example.data.model.UserLanguageEntity
import com.example.data.model.UserProfileEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        DeckEntity::class,
        FlashCardEntity::class,
        StudySessionEntity::class,
        QuizRecordEntity::class,
        UserProfileEntity::class,
        UserAccountEntity::class,
        StudyScheduleEntity::class,
        UserLanguageEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun deckDao(): DeckDao
    abstract fun flashCardDao(): FlashCardDao
    abstract fun studySessionDao(): StudySessionDao
    abstract fun quizRecordDao(): QuizRecordDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun userAccountDao(): UserAccountDao
    abstract fun studyScheduleDao(): StudyScheduleDao
    abstract fun userLanguageDao(): UserLanguageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ntk_flashcard_db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        populateInitialData(
                            deckDao = database.deckDao(),
                            flashCardDao = database.flashCardDao(),
                            userProfileDao = database.userProfileDao(),
                            userLanguageDao = database.userLanguageDao(),
                            studyScheduleDao = database.studyScheduleDao()
                        )
                    }
                }
            }
        }

        suspend fun populateInitialData(
            deckDao: DeckDao,
            flashCardDao: FlashCardDao,
            userProfileDao: UserProfileDao,
            userLanguageDao: UserLanguageDao? = null,
            studyScheduleDao: StudyScheduleDao? = null
        ) {
            deckDao.insertDecks(DefaultVocabData.getDefaultDecks())
            flashCardDao.insertCards(DefaultVocabData.getDefaultFlashCards())
            userProfileDao.insertOrUpdateProfile(
                UserProfileEntity(
                    id = 1,
                    userName = "Bạn Học",
                    avatarEmoji = "🦉",
                    avatarBgColorHex = "#EEF2FF",
                    vipLevel = 1,
                    streakDays = 7,
                    maxStreakDays = 7,
                    totalPoints = 1500,
                    totalCardsLearned = 45,
                    lastActiveTimestamp = System.currentTimeMillis()
                )
            )

            // Khởi tạo các ngôn ngữ học mặc định (Tiếng Anh, Tiếng Nhật, Tiếng Hàn, Tiếng Việt)
            userLanguageDao?.insertLanguages(
                listOf(
                    UserLanguageEntity(
                        languageCode = AppLanguage.ENGLISH.code,
                        displayName = AppLanguage.ENGLISH.displayName,
                        flagEmoji = AppLanguage.ENGLISH.flagEmoji,
                        isCurrentActive = true,
                        dailyGoalCards = 20,
                        masteredCardsCount = 15,
                        totalWordsEnrolled = 50,
                        streakDays = 7,
                        level = "Cơ bản"
                    ),
                    UserLanguageEntity(
                        languageCode = AppLanguage.JAPANESE.code,
                        displayName = AppLanguage.JAPANESE.displayName,
                        flagEmoji = AppLanguage.JAPANESE.flagEmoji,
                        isCurrentActive = false,
                        dailyGoalCards = 15,
                        masteredCardsCount = 10,
                        totalWordsEnrolled = 50,
                        streakDays = 3,
                        level = "N5 Sơ cấp"
                    ),
                    UserLanguageEntity(
                        languageCode = AppLanguage.KOREAN.code,
                        displayName = AppLanguage.KOREAN.displayName,
                        flagEmoji = AppLanguage.KOREAN.flagEmoji,
                        isCurrentActive = false,
                        dailyGoalCards = 15,
                        masteredCardsCount = 5,
                        totalWordsEnrolled = 50,
                        streakDays = 2,
                        level = "Sơ cấp 1"
                    ),
                    UserLanguageEntity(
                        languageCode = AppLanguage.VIETNAMESE.code,
                        displayName = AppLanguage.VIETNAMESE.displayName,
                        flagEmoji = AppLanguage.VIETNAMESE.flagEmoji,
                        isCurrentActive = false,
                        dailyGoalCards = 20,
                        masteredCardsCount = 20,
                        totalWordsEnrolled = 50,
                        streakDays = 5,
                        level = "Giao tiếp"
                    ),
                    UserLanguageEntity(
                        languageCode = AppLanguage.SPANISH.code,
                        displayName = AppLanguage.SPANISH.displayName,
                        flagEmoji = AppLanguage.SPANISH.flagEmoji,
                        isCurrentActive = false,
                        dailyGoalCards = 15,
                        masteredCardsCount = 0,
                        totalWordsEnrolled = 40,
                        streakDays = 0,
                        level = "Cơ bản"
                    ),
                    UserLanguageEntity(
                        languageCode = AppLanguage.GERMAN.code,
                        displayName = AppLanguage.GERMAN.displayName,
                        flagEmoji = AppLanguage.GERMAN.flagEmoji,
                        isCurrentActive = false,
                        dailyGoalCards = 15,
                        masteredCardsCount = 0,
                        totalWordsEnrolled = 40,
                        streakDays = 0,
                        level = "Cơ bản"
                    ),
                    UserLanguageEntity(
                        languageCode = AppLanguage.ITALIAN.code,
                        displayName = AppLanguage.ITALIAN.displayName,
                        flagEmoji = AppLanguage.ITALIAN.flagEmoji,
                        isCurrentActive = false,
                        dailyGoalCards = 15,
                        masteredCardsCount = 0,
                        totalWordsEnrolled = 40,
                        streakDays = 0,
                        level = "Cơ bản"
                    ),
                    UserLanguageEntity(
                        languageCode = AppLanguage.PORTUGUESE.code,
                        displayName = AppLanguage.PORTUGUESE.displayName,
                        flagEmoji = AppLanguage.PORTUGUESE.flagEmoji,
                        isCurrentActive = false,
                        dailyGoalCards = 15,
                        masteredCardsCount = 0,
                        totalWordsEnrolled = 40,
                        streakDays = 0,
                        level = "Cơ bản"
                    )
                )
            )

            // Seed lịch học mặc định (id = 1, nhắc lúc 19:00)
            studyScheduleDao?.saveSchedule(
                StudyScheduleEntity(
                    id = 1,
                    isEnabled = true,
                    reminderHour = 19,
                    reminderMinute = 0,
                    remindStreak = true,
                    remindDueWords = true,
                    minWordsThreshold = 1,
                    targetLanguageCode = "en",
                    updatedTimestamp = System.currentTimeMillis()
                )
            )
        }
    }
}
