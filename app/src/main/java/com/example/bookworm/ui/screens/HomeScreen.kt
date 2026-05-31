package com.example.bookworm.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bookworm.ui.components.AddBookDialog
import com.example.bookworm.ui.components.FinishBookDialog
import com.example.bookworm.ui.components.LevelUpDialog
import com.example.bookworm.ui.components.WordDialog
import com.example.bookworm.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showBookDialog by remember { mutableStateOf(false) }
    var showWordDialog by remember { mutableStateOf(false) }
    var showFinishDialog by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("BookWorm", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Currently reading", style = MaterialTheme.typography.titleMedium)
                val active = state.activeBook
                if (active == null) {
                    Text("No active book yet. Start or select a book to begin collecting words.")
                } else {
                    Text(active.title, style = MaterialTheme.typography.headlineSmall)
                    Text("by ${active.author}")
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button({ showBookDialog = true }) { Text("New book") }
                    Button({ showWordDialog = true }, enabled = active != null || state.finishedBooks.isNotEmpty()) { Text("Add new word") }
                }
                OutlinedButton({ showFinishDialog = true }, enabled = active != null) { Text("Finish book") }
            }
        }

        if (state.readingBooks.isNotEmpty()) {
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                OutlinedTextField(
                    value = state.activeBook?.title ?: "Choose active book",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Active book") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(text = { Text("Clear active book") }, onClick = { viewModel.setActiveBook(null); expanded = false })
                    state.readingBooks.forEach { book ->
                        DropdownMenuItem(text = { Text("${book.title} • ${book.author}") }, onClick = { viewModel.setActiveBook(book.id); expanded = false })
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text("XP: +5 word • +50 finished book • +5 training streak", modifier = Modifier.align(Alignment.CenterHorizontally))
    }

    if (showBookDialog) AddBookDialog(onDismiss = { showBookDialog = false }, onSave = viewModel::addBook)
    if (showWordDialog) {
        WordDialog(
            title = "Add new word",
            books = (state.readingBooks + state.finishedBooks).distinctBy { it.id },
            defaultBookId = state.activeBook?.id,
            onDismiss = { showWordDialog = false },
            onSave = viewModel::addWord,
        )
    }
    if (showFinishDialog) FinishBookDialog(onDismiss = { showFinishDialog = false }, onFinish = viewModel::finishBook)
    state.levelUpMessage?.let { LevelUpDialog(it, viewModel::dismissLevelDialog) }
}
