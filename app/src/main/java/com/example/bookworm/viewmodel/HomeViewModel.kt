package com.example.bookworm.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookworm.data.local.BookEntity
import com.example.bookworm.data.repository.BookWormRepository
import com.example.bookworm.model.Levels
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val activeBook: BookEntity? = null,
    val readingBooks: List<BookEntity> = emptyList(),
    val finishedBooks: List<BookEntity> = emptyList(),
    val levelUpMessage: String? = null,
)

class HomeViewModel(
    private val repository: BookWormRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val uiState: StateFlow<HomeUiState> = combine(
        repository.activeBook,
        repository.readingBooks,
        repository.finishedBooks,
        savedStateHandle.getStateFlow<String?>(KEY_LEVEL_MESSAGE, null),
    ) { active, reading, finished, message ->
        HomeUiState(
            activeBook = active,
            readingBooks = reading,
            finishedBooks = finished,
            levelUpMessage = message,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun addBook(title: String, author: String) = viewModelScope.launch {
        if (title.isNotBlank() && author.isNotBlank()) repository.addBook(title, author)
    }

    fun setActiveBook(bookId: Long?) = viewModelScope.launch { repository.setActiveBook(bookId) }

    fun addWord(word: String, explanation: String, example: String, bookId: Long?) = viewModelScope.launch {
        if (word.isBlank() || explanation.isBlank()) return@launch
        val result = repository.addOrUpdateWord(word = word, explanation = explanation, example = example, bookId = bookId)
        if (result.leveledUp) savedStateHandle[KEY_LEVEL_MESSAGE] = "Congratulations! You reached ${Levels.nameFor(result.stats.level)}."
    }

    fun finishBook(notes: String, rating: Int) = viewModelScope.launch {
        val result = repository.finishActiveBook(notes, rating)
        if (result.leveledUp) savedStateHandle[KEY_LEVEL_MESSAGE] = "Congratulations! You reached ${Levels.nameFor(result.stats.level)}."
    }

    fun dismissLevelDialog() {
        savedStateHandle[KEY_LEVEL_MESSAGE] = null
    }

    companion object {
        private const val KEY_LEVEL_MESSAGE = "home_level_message"
    }
}
