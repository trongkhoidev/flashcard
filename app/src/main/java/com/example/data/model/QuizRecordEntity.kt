package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "quiz_records",
    indices = [Index("deckId"), Index("timestamp")]
)
data class QuizRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deckId: String,
    val deckTitle: String,
    val mode: String = "QUIZ", // "QUIZ" or "MATCH"
    val score: Int,
    val totalQuestions: Int,
    val pointsEarned: Int,
    val maxStreak: Int,
    val accuracyPercent: Float,
    val timeSpentSeconds: Int,
    val timestamp: Long = System.currentTimeMillis()
)
