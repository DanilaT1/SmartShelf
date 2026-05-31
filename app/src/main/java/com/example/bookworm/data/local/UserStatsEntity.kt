package com.example.bookworm.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey val id: Int = SINGLETON_ID,
    val currentXp: Int = 0,
    val level: Int = 1,
) {
    companion object {
        const val SINGLETON_ID = 1
    }
}
