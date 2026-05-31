package com.example.bookworm.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookworm.data.local.BookEntity
import com.example.bookworm.data.local.WordEntity
import com.example.bookworm.data.repository.BookWormRepository
import com.example.bookworm.model.Levels
import com.example.bookworm.model.QuizQuestion
import com.example.bookworm.model.WordWithBook
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DictionaryUiState(
    val words: List<WordWithBook> = emptyList(),
    val books: List<BookEntity> = emptyList(),
    val searchQuery: String = "",
    val selectedBookId: Long? = null,
    val trainingQuestion: QuizQuestion? = null,
    val trainingScore: Int = 0,
    val trainingCorrectInRound: Int = 0,
    val levelUpMessage: String? = null,
)

class DictionaryViewModel(
    private val repository: BookWormRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val search = savedStateHandle.getStateFlow(KEY_SEARCH, "")
    private val selectedBookId = savedStateHandle.getStateFlow<Long?>(KEY_BOOK_ID, null)
    private val levelMessage = savedStateHandle.getStateFlow<String?>(KEY_LEVEL_MESSAGE, null)
    private val trainingQuestion = MutableStateFlow<QuizQuestion?>(null)
    private val trainingScore = MutableStateFlow(0)
    private val trainingCorrectInRound = MutableStateFlow(0)

    private val library = combine(repository.words, repository.readingBooks, repository.finishedBooks) { words, reading, finished ->
        LibraryState(words, (reading + finished).distinctBy { it.id }.sortedBy { it.title.lowercase() })
    }
    private val filters = combine(search, selectedBookId) { query, bookId -> FilterState(query, bookId) }
    private val training = combine(trainingQuestion, trainingScore, trainingCorrectInRound) { question, score, round ->
        TrainingState(question, score, round)
    }

    val uiState: StateFlow<DictionaryUiState> = combine(library, filters, training, levelMessage) { library, filters, training, message ->
        val bookMap = library.books.associateBy { it.id }
        val filtered = library.words.filter { word ->
            (filters.query.isBlank() || word.word.contains(filters.query, ignoreCase = true) || word.explanation.contains(filters.query, ignoreCase = true)) &&
                (filters.bookId == null || word.bookId == filters.bookId)
        }.map { WordWithBook(it, it.bookId?.let(bookMap::get)) }
        DictionaryUiState(
            words = filtered,
            books = library.books,
            searchQuery = filters.query,
            selectedBookId = filters.bookId,
            trainingQuestion = training.question,
            trainingScore = training.score,
            trainingCorrectInRound = training.correctInRound,
            levelUpMessage = message,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DictionaryUiState())

    fun updateSearch(query: String) { savedStateHandle[KEY_SEARCH] = query }
    fun selectBook(bookId: Long?) { savedStateHandle[KEY_BOOK_ID] = bookId }

    fun addOrUpdateWord(existing: WordEntity?, word: String, explanation: String, example: String, bookId: Long?) = viewModelScope.launch {
        if (word.isBlank() || explanation.isBlank()) return@launch
        val result = repository.addOrUpdateWord(
            id = existing?.id ?: 0,
            word = word,
            explanation = explanation,
            example = example,
            bookId = bookId,
            createdAt = existing?.createdAt ?: System.currentTimeMillis(),
        )
        if (result.leveledUp) savedStateHandle[KEY_LEVEL_MESSAGE] = "Congratulations! You reached ${Levels.nameFor(result.stats.level)}."
    }

    fun deleteWord(word: WordEntity) = viewModelScope.launch { repository.deleteWord(word) }

    fun startTraining() {
        val allWords = uiState.value.words.map { it.word }
        trainingScore.value = 0
        trainingCorrectInRound.value = 0
        trainingQuestion.value = buildQuestion(allWords)
    }

    fun answer(option: String) = viewModelScope.launch {
        val question = trainingQuestion.value ?: return@launch
        val allWords = uiState.value.words.map { it.word }
        if (option == question.correctAnswer) {
            val score = trainingScore.value + 1
            val round = trainingCorrectInRound.value + 1
            trainingScore.value = score
            trainingCorrectInRound.value = round
            if (round >= 3) {
                trainingCorrectInRound.value = 0
                val result = repository.addTrainingReward()
                if (result.leveledUp) savedStateHandle[KEY_LEVEL_MESSAGE] = "Congratulations! You reached ${Levels.nameFor(result.stats.level)}."
            }
        }
        trainingQuestion.value = buildQuestion(allWords)
    }

    fun stopTraining() { trainingQuestion.value = null }
    fun dismissLevelDialog() { savedStateHandle[KEY_LEVEL_MESSAGE] = null }

    private fun buildQuestion(words: List<WordEntity>): QuizQuestion? {
        if (words.size < 4) return null
        val correct = words.random()
        val distractors = words.filterNot { it.id == correct.id }.shuffled().take(3).map { it.explanation }
        return QuizQuestion(correct, (distractors + correct.explanation).shuffled(), correct.explanation)
    }

    companion object {
        private const val KEY_SEARCH = "dictionary_search"
        private const val KEY_BOOK_ID = "dictionary_book_id"
        private const val KEY_LEVEL_MESSAGE = "dictionary_level_message"
    }
}

private data class LibraryState(val words: List<WordEntity>, val books: List<BookEntity>)
private data class FilterState(val query: String, val bookId: Long?)
private data class TrainingState(val question: QuizQuestion?, val score: Int, val correctInRound: Int)

