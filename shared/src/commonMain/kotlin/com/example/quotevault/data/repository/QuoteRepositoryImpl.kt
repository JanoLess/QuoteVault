package com.example.quotevault.data.repository

import com.example.quotevault.data.local.LocalStore
import com.example.quotevault.data.remote.QuoteRemote
import com.example.quotevault.domain.model.Quote
import com.example.quotevault.domain.repository.QuoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class QuoteRepositoryImpl(
    private val remote: QuoteRemote,
    private val localStore: LocalStore,
) : QuoteRepository {
    private val json = Json { ignoreUnknownKeys = true }
    private var cachedQuotes: List<Quote> = loadQuotes()

    private val _favorites = MutableStateFlow(loadFavorites())
    override val favorites: StateFlow<List<Quote>> = _favorites.asStateFlow()

    override suspend fun getQuotes(forceRefresh: Boolean): List<Quote> {
        if (cachedQuotes.isNotEmpty() && !forceRefresh) return cachedQuotes
        return try {
            remote.fetchQuotes().also { quotes ->
                cachedQuotes = quotes
                localStore.putString(KEY_QUOTES, json.encodeToString(quotes))
            }
        } catch (error: Exception) {
            if (cachedQuotes.isNotEmpty()) cachedQuotes else throw error
        }
    }

    override fun toggleFavorite(quote: Quote) {
        _favorites.value = if (_favorites.value.any { it.id == quote.id }) {
            _favorites.value.filterNot { it.id == quote.id }
        } else {
            _favorites.value + quote
        }
        localStore.putString(KEY_FAVORITES, json.encodeToString(_favorites.value))
    }

    private fun loadQuotes(): List<Quote> = decodeList(localStore.getString(KEY_QUOTES))

    private fun loadFavorites(): List<Quote> = decodeList(localStore.getString(KEY_FAVORITES))

    private fun decodeList(raw: String?): List<Quote> {
        if (raw.isNullOrBlank()) return emptyList()
        return try {
            json.decodeFromString(raw)
        } catch (_: Exception) {
            emptyList()
        }
    }

    private companion object {
        const val KEY_QUOTES = "cached_quotes"
        const val KEY_FAVORITES = "favorites"
    }
}
