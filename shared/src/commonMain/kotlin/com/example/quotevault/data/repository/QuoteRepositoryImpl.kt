package com.example.quotevault.data.repository

import com.example.quotevault.data.model.Quote
import com.example.quotevault.data.remote.QuoteApi
import com.example.quotevault.domain.repository.QuoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class QuoteRepositoryImpl(private val api: QuoteApi) : QuoteRepository {
    private var cachedQuotes: List<Quote> = emptyList()

    private val _favorites = MutableStateFlow<List<Quote>>(emptyList())
    override val favorites: StateFlow<List<Quote>> = _favorites.asStateFlow()

    override suspend fun getQuotes(forceRefresh: Boolean): List<Quote> {
        if (cachedQuotes.isNotEmpty() && !forceRefresh) return cachedQuotes
        return api.fetchQuotes().also { cachedQuotes = it }
    }

    override fun toggleFavorite(quote: Quote) {
        _favorites.value = if (_favorites.value.any { it.id == quote.id }) {
            _favorites.value.filterNot { it.id == quote.id }
        } else {
            _favorites.value + quote
        }
    }
}
