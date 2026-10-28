package com.example.quotevault.data.repository

import com.example.quotevault.data.model.Quote
import com.example.quotevault.domain.repository.QuoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockQuoteRepository : QuoteRepository {
    private val _favorites = MutableStateFlow<List<Quote>>(emptyList())
    override val favorites: StateFlow<List<Quote>> = _favorites.asStateFlow()

    override suspend fun getQuotes(forceRefresh: Boolean): List<Quote> = listOf(
        Quote(1, "The future depends on what you do today.", "Mahatma Gandhi"),
        Quote(2, "Believe you can and you are halfway there.", "Theodore Roosevelt"),
        Quote(3, "Success is the sum of small efforts repeated daily.", "Robert Collier"),
        Quote(4, "Start where you are. Use what you have. Do what you can.", "Arthur Ashe"),
    )

    override suspend fun toggleFavorite(quote: Quote) {
        _favorites.value = if (_favorites.value.any { it.id == quote.id }) {
            _favorites.value.filterNot { it.id == quote.id }
        } else {
            _favorites.value + quote
        }
    }
}
