package com.example.bookworm.model

import com.example.bookworm.data.local.BookEntity
import com.example.bookworm.data.local.WordEntity


data class ProfileSummary(
    val currentXp: Int = 0,
    val level: Int = 1,
    val levelName: String = Levels.nameFor(1),
    val progress: Float = 0f,
    val xpIntoLevel: Int = 0,
    val xpNeededForLevel: Int = 100,
    val totalBooksFinished: Int = 0,
    val totalWordsAdded: Int = 0,
    val appearance: AppAppearance = AppAppearance(),
)

data class QuizQuestion(
    val word: WordEntity,
    val options: List<String>,
    val correctAnswer: String,
)

data class WordWithBook(
    val word: WordEntity,
    val book: BookEntity?,
)
