package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.AppLanguage
import com.example.data.model.DeckEntity
import com.example.data.model.FlashCardEntity
import com.example.data.model.QuizRecordEntity
import com.example.data.model.StudyScheduleEntity
import com.example.data.model.StudySessionEntity
import com.example.data.model.UserAccountEntity
import com.example.data.model.UserLanguageEntity
import com.example.data.model.UserProfileEntity
import com.example.data.model.UserSavedCardEntity
import com.example.data.model.UserMasteredCardEntity
import com.example.data.local.UserMasteredCardDao

@Database(
    entities = [
        DeckEntity::class,
        FlashCardEntity::class,
        StudySessionEntity::class,
        QuizRecordEntity::class,
        UserProfileEntity::class,
        UserAccountEntity::class,
        StudyScheduleEntity::class,
        UserLanguageEntity::class,
        UserSavedCardEntity::class,
        UserMasteredCardEntity::class
    ],
    version = 9,
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
    abstract fun userSavedCardDao(): UserSavedCardDao
    abstract fun userMasteredCardDao(): UserMasteredCardDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ntk_flashcard_db"
                )
                .createFromAsset("databases/flashcard_database_expanded.db")
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun populateInitialData(
            deckDao: DeckDao,
            flashCardDao: FlashCardDao,
            userProfileDao: UserProfileDao,
            userLanguageDao: UserLanguageDao? = null
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

            // Khởi tạo ngôn ngữ mặc định (Tiếng Anh) cho tài khoản mặc định (userId = 1L)
            userLanguageDao?.insertLanguages(
                listOf(
                    UserLanguageEntity(
                        userId = 1L,
                        languageCode = AppLanguage.ENGLISH.code,
                        displayName = AppLanguage.ENGLISH.displayName,
                        flagEmoji = AppLanguage.ENGLISH.flagEmoji,
                        isCurrentActive = true,
                        dailyGoalCards = 20,
                        masteredCardsCount = 15,
                        totalWordsEnrolled = 50,
                        streakDays = 7,
                        level = "Cơ bản"
                    )
                )
            )
        }
    }
}
