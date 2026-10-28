package com.example.quotevault

import com.example.quotevault.domain.model.Quote
import com.example.quotevault.domain.repository.QuoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeQuoteRepository : QuoteRepository {
    private val _favorites = MutableStateFlow<List<Quote>>(emptyList())
    override val favorites: StateFlow<List<Quote>> = _favorites.asStateFlow()

    override suspend fun getQuotes(forceRefresh: Boolean): List<Quote> = sampleQuotes

    override fun toggleFavorite(quote: Quote) {
        _favorites.value = if (_favorites.value.any { it.id == quote.id }) {
            _favorites.value.filterNot { it.id == quote.id }
        } else {
            _favorites.value + quote
        }
    }

    companion object {
        val sampleQuotes = listOf(
            Quote(1, "Be kind whenever possible.", "Dalai Lama"),
            Quote(2, "The journey matters.", "Unknown"),
        )
    }
}
