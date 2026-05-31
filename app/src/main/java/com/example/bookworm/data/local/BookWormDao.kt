package com.example.bookworm.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BookWormDao {
    @Query("SELECT * FROM books WHERE id = :id LIMIT 1")
    fun observeBook(id: Long): Flow<BookEntity?>

    @Query("SELECT * FROM books WHERE isFinished = 0 ORDER BY id DESC")
    fun observeReadingBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE isFinished = 1 ORDER BY finishDate DESC, id DESC")
    fun observeFinishedBooks(): Flow<List<BookEntity>>

    @Transaction
    @Query("SELECT * FROM books WHERE isFinished = 1 ORDER BY finishDate DESC, id DESC")
    fun observeFinishedBooksWithWords(): Flow<List<BookWithWords>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBook(book: BookEntity): Long

    @Update
    suspend fun updateBook(book: BookEntity)

    @Query("SELECT * FROM words ORDER BY createdAt DESC")
    fun observeWords(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE id = :id LIMIT 1")
    fun observeWord(id: Long): Flow<WordEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertWord(word: WordEntity): Long

    @Delete
    suspend fun deleteWord(word: WordEntity)

    @Query("SELECT * FROM user_stats WHERE id = :id LIMIT 1")
    fun observeStats(id: Int = UserStatsEntity.SINGLETON_ID): Flow<UserStatsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStats(stats: UserStatsEntity)

    @Query("SELECT COUNT(*) FROM books WHERE isFinished = 1")
    fun observeFinishedBookCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM words")
    fun observeWordCount(): Flow<Int>
}
