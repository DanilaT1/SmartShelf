package com.example.bookworm.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class BookWithWords(
    @Embedded val book: BookEntity,
    @Relation(parentColumn = "id", entityColumn = "bookId") val words: List<WordEntity>,
)
