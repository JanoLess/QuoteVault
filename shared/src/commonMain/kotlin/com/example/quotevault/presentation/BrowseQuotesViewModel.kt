package com.example.quotevault.presentation

import com.example.quotevault.data.model.Quote
import com.example.quotevault.domain.repository.QuoteRepository
import com.example.quotevault.domain.usecase.GetQuotesUseCase
import com.example.quotevault.domain.usecase.SearchQuotesUseCase
import com.example.quotevault.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface QuotesUiState {
    data object Loading : QuotesUiState
    data class Success(val quotes: List<Quote>) : QuotesUiState
    data class Error(val message: String) : QuotesUiState
}

class BrowseQuotesViewModel(
    private val getQuotes: GetQuotesUseCase,
    private val searchQuotes: SearchQuotesUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    repository: QuoteRepository,
    private val scope: CoroutineScope,
) {
    private var allQuotes: List<Quote> = emptyList()
    private var searchQuery: String = ""

    private val _uiState = MutableStateFlow<QuotesUiState>(QuotesUiState.Loading)
    val uiState: StateFlow<QuotesUiState> = _uiState.asStateFlow()
    val favorites: StateFlow<List<Quote>> = repository.favorites

    private val _events = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val events: SharedFlow<String> = _events.asSharedFlow()

    fun loadQuotes(forceRefresh: Boolean = false) {
        scope.launch {
            _uiState.value = QuotesUiState.Loading
            try {
                allQuotes = getQuotes(forceRefresh)
                _uiState.value = QuotesUiState.Success(searchQuotes(allQuotes, searchQuery))
                if (forceRefresh) _events.tryEmit("Quotes refreshed.")
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.value = QuotesUiState.Error(
                    error.message ?: "Unable to load quotes.",
                )
            }
        }
    }

    fun onSearch(query: String) {
        searchQuery = query
        if (allQuotes.isNotEmpty()) {
            _uiState.value = QuotesUiState.Success(searchQuotes(allQuotes, query))
        }
    }

    fun onToggleFavorite(quote: Quote) {
        scope.launch {
            try {
                toggleFavorite(quote)
                _events.emit("Favorites updated.")
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _events.emit(error.message ?: "Unable to update favorites.")
            }
        }
    }

    fun findQuote(id: Int): Quote? =
        allQuotes.firstOrNull { it.id == id } ?: favorites.value.firstOrNull { it.id == id }
}
