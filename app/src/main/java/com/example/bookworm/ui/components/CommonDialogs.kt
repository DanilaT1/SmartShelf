package com.example.bookworm.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bookworm.data.local.BookEntity
import com.example.bookworm.data.local.WordEntity

@Composable
fun AddBookDialog(onDismiss: () -> Unit, onSave: (String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Start a new book") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(title, { title = it }, label = { Text("Title") }, singleLine = true)
                OutlinedTextField(author, { author = it }, label = { Text("Author") }, singleLine = true)
            }
        },
        confirmButton = { Button({ onSave(title, author); onDismiss() }, enabled = title.isNotBlank() && author.isNotBlank()) { Text("Save") } },
        dismissButton = { OutlinedButton(onDismiss) { Text("Cancel") } },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordDialog(
    title: String,
    books: List<BookEntity>,
    defaultBookId: Long?,
    existingWord: WordEntity? = null,
    onDismiss: () -> Unit,
    onSave: (word: String, explanation: String, example: String, bookId: Long?) -> Unit,
) {
    var word by remember(existingWord) { mutableStateOf(existingWord?.word.orEmpty()) }
    var explanation by remember(existingWord) { mutableStateOf(existingWord?.explanation.orEmpty()) }
    var example by remember(existingWord) { mutableStateOf(existingWord?.example.orEmpty()) }
    var selectedBookId by remember(existingWord, defaultBookId) { mutableStateOf(existingWord?.bookId ?: defaultBookId) }
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = books.firstOrNull { it.id == selectedBookId }?.let { "${it.title} • ${it.author}" } ?: "No book"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(word, { word = it }, label = { Text("Word") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(explanation, { explanation = it }, label = { Text("Explanation") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(example, { example = it }, label = { Text("Example (optional)") }, modifier = Modifier.fillMaxWidth())
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(
                        value = selectedLabel,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Book") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(text = { Text("No book") }, onClick = { selectedBookId = null; expanded = false })
                        books.forEach { book ->
                            DropdownMenuItem(text = { Text("${book.title} • ${book.author}") }, onClick = { selectedBookId = book.id; expanded = false })
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button({ onSave(word, explanation, example, selectedBookId); onDismiss() }, enabled = word.isNotBlank() && explanation.isNotBlank()) { Text("Save") }
        },
        dismissButton = { OutlinedButton(onDismiss) { Text("Cancel") } },
    )
}

@Composable
fun FinishBookDialog(onDismiss: () -> Unit, onFinish: (String, Int) -> Unit) {
    var notes by remember { mutableStateOf("") }
    var rating by remember { mutableFloatStateOf(5f) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Finish book") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(notes, { notes = it }, label = { Text("Notes") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                Text("Rating: ${rating.toInt()} / 5")
                Slider(value = rating, onValueChange = { rating = it }, valueRange = 0f..5f, steps = 4)
            }
        },
        confirmButton = { Button({ onFinish(notes, rating.toInt()); onDismiss() }) { Text("Finish") } },
        dismissButton = { OutlinedButton(onDismiss) { Text("Cancel") } },
    )
}

@Composable
fun LevelUpDialog(message: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Level up!") },
        text = { Text(message) },
        confirmButton = { Button(onDismiss) { Text("Awesome") } },
    )
}
