package com.example.bookworm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookworm.data.repository.BookWormRepository
import com.example.bookworm.model.Levels
import com.example.bookworm.model.ProfileSummary
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class ProfileViewModel(repository: BookWormRepository) : ViewModel() {
    val uiState: StateFlow<ProfileSummary> = combine(
        repository.stats,
        repository.finishedBookCount,
        repository.wordCount,
    ) { stats, books, words ->
        val info = Levels.infoFor(stats.currentXp)
        val span = info.nextThreshold - info.threshold
        val into = stats.currentXp - info.threshold
        ProfileSummary(
            currentXp = stats.currentXp,
            level = stats.level,
            levelName = info.name,
            progress = (into.toFloat() / span.toFloat()).coerceIn(0f, 1f),
            xpIntoLevel = into,
            xpNeededForLevel = span,
            totalBooksFinished = books,
            totalWordsAdded = words,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProfileSummary())
}
