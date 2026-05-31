package com.example.bookworm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookworm.data.local.BookWithWords
import com.example.bookworm.data.repository.BookWormRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class ArchiveUiState(val books: List<BookWithWords> = emptyList())

class ArchiveViewModel(repository: BookWormRepository) : ViewModel() {
    val uiState: StateFlow<ArchiveUiState> = repository.finishedBooksWithWords
        .map { ArchiveUiState(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ArchiveUiState())
}
