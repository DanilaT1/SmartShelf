package com.example.bookworm.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val author: String,
    val finishDate: Long? = null,
    val notes: String = "",
    val rating: Int = 0,
    val isFinished: Boolean = false,
)
