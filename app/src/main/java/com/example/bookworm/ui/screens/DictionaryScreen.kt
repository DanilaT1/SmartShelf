package com.example.bookworm.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bookworm.data.local.WordEntity
import com.example.bookworm.model.QuizQuestion
import com.example.bookworm.model.WordWithBook
import com.example.bookworm.ui.components.LevelUpDialog
import com.example.bookworm.ui.components.WordDialog
import com.example.bookworm.viewmodel.DictionaryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictionaryScreen(viewModel: DictionaryViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var expanded by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<WordEntity?>(null) }
    var deleteTarget by remember { mutableStateOf<WordEntity?>(null) }
    val selectedBook = state.books.firstOrNull { it.id == state.selectedBookId }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Dictionary", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = viewModel::updateSearch,
            label = { Text("Search words or explanations") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(
                value = selectedBook?.let { "${it.title} • ${it.author}" } ?: "All books",
                onValueChange = {},
                readOnly = true,
                label = { Text("Filter by book") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(text = { Text("All books") }, onClick = { viewModel.selectBook(null); expanded = false })
                state.books.forEach { book ->
                    DropdownMenuItem(text = { Text("${book.title} • ${book.author}") }, onClick = { viewModel.selectBook(book.id); expanded = false })
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button({ editing = WordEntity(word = "", explanation = "", bookId = state.selectedBookId) }) { Text("Add word") }
            OutlinedButton({ viewModel.startTraining() }, enabled = state.words.size >= 4) { Text("Train") }
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.words, key = { it.word.id }) { item -> WordCard(item, onClick = { editing = item.word }, onDelete = { deleteTarget = item.word }) }
        }
    }

    editing?.let { word ->
        WordDialog(
            title = if (word.id == 0L) "Add word" else "Edit word",
            books = state.books,
            defaultBookId = word.bookId ?: state.selectedBookId,
            existingWord = word.takeIf { it.id != 0L },
            onDismiss = { editing = null },
            onSave = { w, e, ex, b -> viewModel.addOrUpdateWord(word.takeIf { it.id != 0L }, w, e, ex, b) },
        )
    }
    deleteTarget?.let { target ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("Delete ${target.word}?") },
            text = { Text("This cannot be undone.") },
            confirmButton = { Button({ viewModel.deleteWord(target); deleteTarget = null }) { Text("Delete") } },
            dismissButton = { OutlinedButton({ deleteTarget = null }) { Text("Cancel") } },
        )
    }
    state.trainingQuestion?.let { question -> TrainingDialog(question, state.trainingScore, viewModel::answer, viewModel::stopTraining) }
    state.levelUpMessage?.let { LevelUpDialog(it, viewModel::dismissLevelDialog) }
}

@Composable
private fun WordCard(item: WordWithBook, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(item.word.word, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                OutlinedButton(onDelete) { Text("Delete") }
            }
            Text(item.word.explanation)
            if (item.word.example.isNotBlank()) Text("Example: ${item.word.example}", style = MaterialTheme.typography.bodySmall)
            Text(item.book?.title ?: "No book", style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun TrainingDialog(question: QuizQuestion, score: Int, onAnswer: (String) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Train: ${question.word.word}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Session score: $score")
                question.options.forEach { option ->
                    OutlinedButton(onClick = { onAnswer(option) }, modifier = Modifier.fillMaxWidth()) { Text(option) }
                }
            }
        },
        confirmButton = { Button(onDismiss) { Text("End") } },
    )
}
