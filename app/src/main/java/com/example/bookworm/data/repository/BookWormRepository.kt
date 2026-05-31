package com.example.bookworm.data.repository

import com.example.bookworm.data.local.BookEntity
import com.example.bookworm.data.local.BookWithWords
import com.example.bookworm.data.local.BookWormDao
import com.example.bookworm.data.local.UserStatsEntity
import com.example.bookworm.data.local.WordEntity
import com.example.bookworm.data.preferences.ActiveBookPreferences
import com.example.bookworm.data.preferences.AppearancePreferences
import com.example.bookworm.model.AppAppearance
import com.example.bookworm.model.Levels
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class BookWormRepository(
    private val dao: BookWormDao,
    private val activeBookPreferences: ActiveBookPreferences,
    private val appearancePreferences: AppearancePreferences,
) {
    val activeBookId: Flow<Long?> = activeBookPreferences.activeBookId
    val readingBooks: Flow<List<BookEntity>> = dao.observeReadingBooks()
    val finishedBooks: Flow<List<BookEntity>> = dao.observeFinishedBooks()
    val finishedBooksWithWords: Flow<List<BookWithWords>> = dao.observeFinishedBooksWithWords()
    val words: Flow<List<WordEntity>> = dao.observeWords()
    val stats: Flow<UserStatsEntity> = dao.observeStats().map { it ?: UserStatsEntity() }
    val finishedBookCount: Flow<Int> = dao.observeFinishedBookCount()
    val wordCount: Flow<Int> = dao.observeWordCount()
    val appearance: Flow<AppAppearance> = appearancePreferences.appearance

    val activeBook: Flow<BookEntity?> = activeBookId.combine(readingBooks) { activeId, books ->
        books.firstOrNull { it.id == activeId }
    }

    suspend fun addBook(title: String, author: String, makeActive: Boolean = true): Long {
        val id = dao.upsertBook(BookEntity(title = title.trim(), author = author.trim()))
        if (makeActive) activeBookPreferences.setActiveBookId(id)
        return id
    }

    suspend fun setActiveBook(bookId: Long?) = activeBookPreferences.setActiveBookId(bookId)

    suspend fun setAppearance(appearance: AppAppearance) = appearancePreferences.setAppearance(appearance)

    suspend fun addOrUpdateWord(
        id: Long = 0,
        word: String,
        explanation: String,
        example: String,
        bookId: Long?,
        createdAt: Long = System.currentTimeMillis(),
    ): LevelUpResult {
        dao.upsertWord(
            WordEntity(
                id = id,
                word = word.trim(),
                explanation = explanation.trim(),
                example = example.trim(),
                bookId = bookId,
                createdAt = createdAt,
            ),
        )
        return if (id == 0L) addXp(XP_ADD_WORD) else LevelUpResult(false, currentStats())
    }

    suspend fun deleteWord(word: WordEntity) = dao.deleteWord(word)

    suspend fun finishActiveBook(notes: String, rating: Int): LevelUpResult {
        val active = activeBook.first() ?: return LevelUpResult(false, currentStats())
        dao.updateBook(
            active.copy(
                finishDate = System.currentTimeMillis(),
                notes = notes.trim(),
                rating = rating.coerceIn(0, 5),
                isFinished = true,
            ),
        )
        activeBookPreferences.setActiveBookId(null)
        return addXp(XP_FINISH_BOOK)
    }

    suspend fun addTrainingReward(): LevelUpResult = addXp(XP_TRAINING_ROUND)

    private suspend fun currentStats(): UserStatsEntity = dao.observeStats().first() ?: UserStatsEntity().also { dao.upsertStats(it) }

    private suspend fun addXp(amount: Int): LevelUpResult {
        val before = currentStats()
        val afterXp = before.currentXp + amount
        val afterLevel = Levels.levelForXp(afterXp)
        val after = before.copy(currentXp = afterXp, level = afterLevel)
        dao.upsertStats(after)
        return LevelUpResult(afterLevel > before.level, after)
    }

    companion object {
        const val XP_ADD_WORD = 5
        const val XP_FINISH_BOOK = 50
        const val XP_TRAINING_ROUND = 5
    }
}

data class LevelUpResult(val leveledUp: Boolean, val stats: UserStatsEntity)
