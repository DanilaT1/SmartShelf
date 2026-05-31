package com.example.bookworm.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bookworm.data.local.BookWithWords
import com.example.bookworm.viewmodel.ArchiveViewModel
import java.text.DateFormat
import java.util.Date

@Composable
fun ArchiveScreen(viewModel: ArchiveViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val expanded = remember { mutableStateMapOf<Long, Boolean>() }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Archive", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        if (state.books.isEmpty()) Text("Finished books will appear here.")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.books, key = { it.book.id }) { book ->
                ArchiveBookCard(book, expanded[book.book.id] == true) {
                    expanded[book.book.id] = !(expanded[book.book.id] ?: false)
                }
            }
        }
    }
}

@Composable
private fun ArchiveBookCard(bookWithWords: BookWithWords, expanded: Boolean, onToggle: () -> Unit) {
    val book = bookWithWords.book
    Card(Modifier.fillMaxWidth().clickable(onClick = onToggle)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(Modifier.weight(1f)) {
                    Text(book.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text("by ${book.author}")
                }
                Text("★ ${book.rating}/5")
            }
            Text("Finished ${book.finishDate?.let { DateFormat.getDateInstance().format(Date(it)) } ?: "Unknown"}")
            AnimatedVisibility(expanded) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    HorizontalDivider()
                    Text("Notes", fontWeight = FontWeight.SemiBold)
                    Text(book.notes.ifBlank { "No notes." })
                    Text("Words", fontWeight = FontWeight.SemiBold)
                    if (bookWithWords.words.isEmpty()) Text("No words saved for this book.")
                    bookWithWords.words.forEach { word ->
                        Column {
                            Text(word.word, fontWeight = FontWeight.SemiBold)
                            Text(word.explanation, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
