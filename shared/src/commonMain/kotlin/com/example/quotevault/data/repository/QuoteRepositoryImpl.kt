package com.example.quotevault.data.repository

import com.example.quotevault.data.model.Quote
import com.example.quotevault.data.local.FavoriteQuoteDao
import com.example.quotevault.data.local.FavoriteQuoteEntity
import com.example.quotevault.data.remote.QuoteApi
import com.example.quotevault.domain.repository.QuoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class QuoteRepositoryImpl(
    private val api: QuoteApi,
    private val favoriteQuoteDao: FavoriteQuoteDao,
    scope: CoroutineScope,
) : QuoteRepository {
    private var cachedQuotes: List<Quote> = emptyList()

    private val _favorites = MutableStateFlow<List<Quote>>(emptyList())
    override val favorites: StateFlow<List<Quote>> = _favorites.asStateFlow()

    init {
        scope.launch {
            favoriteQuoteDao.observeFavorites().collect { savedQuotes ->
                _favorites.value = savedQuotes.map { it.toQuote() }
            }
        }
    }

    override suspend fun getQuotes(forceRefresh: Boolean): List<Quote> {
        if (cachedQuotes.isNotEmpty() && !forceRefresh) return cachedQuotes
        return api.fetchQuotes().also { cachedQuotes = it }
    }

    override suspend fun toggleFavorite(quote: Quote) {
        if (favoriteQuoteDao.findById(quote.id) != null) {
            favoriteQuoteDao.remove(quote.id)
        } else {
            favoriteQuoteDao.save(quote.toFavoriteEntity())
        }
    }

    private fun FavoriteQuoteEntity.toQuote() = Quote(id, quote, author)

    private fun Quote.toFavoriteEntity() = FavoriteQuoteEntity(id, quote, author)
}
