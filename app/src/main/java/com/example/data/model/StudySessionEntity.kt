package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_sessions")
data class StudySessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deckId: String,
    val deckTitle: String,
    val languageCode: String,
    val cardsStudied: Int,
    val masteredCount: Int,
    val durationSeconds: Int,
    val timestamp: Long = System.currentTimeMillis()
)
