package com.example.bookworm.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "words",
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [Index("bookId"), Index("word")],
)
data class WordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val word: String,
    val explanation: String,
    val example: String = "",
    val bookId: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
)
