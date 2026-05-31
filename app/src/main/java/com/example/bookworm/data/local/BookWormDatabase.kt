package com.example.bookworm.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [BookEntity::class, WordEntity::class, UserStatsEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class BookWormDatabase : RoomDatabase() {
    abstract fun dao(): BookWormDao

    companion object {
        @Volatile private var instance: BookWormDatabase? = null

        fun getInstance(context: Context): BookWormDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                BookWormDatabase::class.java,
                "bookworm.db",
            ).fallbackToDestructiveMigration().build().also { instance = it }
        }
    }
}
