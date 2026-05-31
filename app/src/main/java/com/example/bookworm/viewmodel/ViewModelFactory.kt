package com.example.bookworm.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.bookworm.data.repository.BookWormRepository

class BookWormViewModelFactory(
    private val repository: BookWormRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val savedStateHandle: SavedStateHandle = extras.createSavedStateHandle()
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(repository, savedStateHandle)
            modelClass.isAssignableFrom(DictionaryViewModel::class.java) -> DictionaryViewModel(repository, savedStateHandle)
            modelClass.isAssignableFrom(ArchiveViewModel::class.java) -> ArchiveViewModel(repository)
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> ProfileViewModel(repository)
            else -> error("Unknown ViewModel class: ${modelClass.name}")
        } as T
    }
}
