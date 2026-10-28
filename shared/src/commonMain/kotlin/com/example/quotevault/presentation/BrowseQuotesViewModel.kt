package com.example.quotevault.presentation

import com.example.quotevault.domain.model.Quote
import com.example.quotevault.domain.usecase.GetQuotesUseCase
import com.example.quotevault.domain.usecase.ObserveFavoritesUseCase
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

class BrowseQuotesViewModel(
    private val getQuotes: GetQuotesUseCase,
    private val searchQuotes: SearchQuotesUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    observeFavorites: ObserveFavoritesUseCase,
    private val scope: CoroutineScope,
) {
    private var allQuotes: List<Quote> = emptyList()

    private val _uiState = MutableStateFlow<QuotesUiState>(QuotesUiState.Loading)
    val uiState: StateFlow<QuotesUiState> = _uiState.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val favorites: StateFlow<List<Quote>> = observeFavorites()

    private val _events = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val events: SharedFlow<String> = _events.asSharedFlow()

    fun loadQuotes(forceRefresh: Boolean = false) {
        scope.launch {
            _uiState.value = QuotesUiState.Loading
            try {
                allQuotes = getQuotes(forceRefresh)
                showFilteredQuotes()
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
        _query.value = query
        if (allQuotes.isNotEmpty()) {
            showFilteredQuotes()
        }
    }

    fun onToggleFavorite(quote: Quote) {
        toggleFavorite(quote)
        _events.tryEmit("Favorites updated.")
    }

    fun findQuote(id: Int): Quote? =
        allQuotes.firstOrNull { it.id == id } ?: favorites.value.firstOrNull { it.id == id }

    private fun showFilteredQuotes() {
        _uiState.value = QuotesUiState.Success(searchQuotes(allQuotes, _query.value))
    }
}
