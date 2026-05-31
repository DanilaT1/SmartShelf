package com.example.bookworm.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
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
import com.example.bookworm.model.AppAppearance
import com.example.bookworm.model.AppColorChoice
import com.example.bookworm.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(viewModel: ProfileViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showSettings by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Profile", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            IconButton(onClick = { showSettings = true }) {
                Icon(Icons.Default.Settings, contentDescription = "Appearance settings")
            }
        }
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Level ${state.level}", style = MaterialTheme.typography.headlineSmall)
                Text(state.levelName, style = MaterialTheme.typography.titleMedium)
                LinearProgressIndicator(progress = { state.progress }, modifier = Modifier.fillMaxWidth())
                Text("${state.xpIntoLevel} / ${state.xpNeededForLevel} XP to next level")
                Text("Total XP: ${state.currentXp}")
            }
        }
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Lifetime stats", style = MaterialTheme.typography.titleMedium)
                Text("Books finished: ${state.totalBooksFinished}")
                Text("Words added: ${state.totalWordsAdded}")
            }
        }
    }

    if (showSettings) {
        AppearanceSettingsDialog(
            appearance = state.appearance,
            onDismiss = { showSettings = false },
            onSave = { appearance ->
                viewModel.updateAppearance(appearance)
                showSettings = false
            },
        )
    }
}

@Composable
private fun AppearanceSettingsDialog(
    appearance: AppAppearance,
    onDismiss: () -> Unit,
    onSave: (AppAppearance) -> Unit,
) {
    var selectedBackground by remember(appearance) { mutableStateOf(appearance.backgroundColor) }
    var selectedFont by remember(appearance) { mutableStateOf(appearance.fontColor) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("App colors") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Background color", fontWeight = FontWeight.SemiBold)
                ColorOptionsRow(
                    selected = selectedBackground,
                    onSelected = { selectedBackground = it },
                    options = backgroundOptions,
                )
                Text("Font color", fontWeight = FontWeight.SemiBold)
                ColorOptionsRow(
                    selected = selectedFont,
                    onSelected = { selectedFont = it },
                    options = fontOptions,
                )
                Text(
                    "Tip: choose contrasting colors so text stays readable.",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSave(AppAppearance(selectedBackground, selectedFont)) }) { Text("Apply") }
        },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@Composable
private fun ColorOptionsRow(
    selected: AppColorChoice,
    onSelected: (AppColorChoice) -> Unit,
    options: List<AppColorChoice>,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        options.forEach { option ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(selected = selected == option, onClick = { onSelected(option) })
                Text(option.label)
            }
        }
    }
}

private val backgroundOptions = listOf(
    AppColorChoice.SYSTEM,
    AppColorChoice.CREAM,
    AppColorChoice.MINT,
    AppColorChoice.LAVENDER,
    AppColorChoice.CHARCOAL,
    AppColorChoice.WHITE,
    AppColorChoice.BLACK,
)

private val fontOptions = listOf(
    AppColorChoice.SYSTEM,
    AppColorChoice.BLACK,
    AppColorChoice.WHITE,
    AppColorChoice.BLUE,
    AppColorChoice.GREEN,
    AppColorChoice.PURPLE,
    AppColorChoice.BROWN,
)
