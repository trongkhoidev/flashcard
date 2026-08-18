package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.DeckEntity
import com.example.data.model.FlashCardEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [DeckEntity::class, FlashCardEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun flashCardDao(): FlashCardDao

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
                .addCallback(DatabaseCallback(context.applicationContext, scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val context: Context,
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        populateInitialData(context, database.flashCardDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(context: Context, dao: FlashCardDao) {
            val jsonVocab = JsonVocabLoader.loadVocabFromJson(context)
            if (jsonVocab != null && jsonVocab.first.isNotEmpty()) {
                dao.insertDecks(jsonVocab.first)
                dao.insertCards(jsonVocab.second)
            } else {
                dao.insertDecks(DefaultVocabData.getDefaultDecks())
                dao.insertCards(DefaultVocabData.getDefaultFlashCards())
            }
        }
    }
}
