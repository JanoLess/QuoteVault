package com.example.quotevault.domain.repository

import com.example.quotevault.data.model.Quote
import kotlinx.coroutines.flow.StateFlow

interface QuoteRepository {
    val favorites: StateFlow<List<Quote>>
    suspend fun getQuotes(forceRefresh: Boolean = false): List<Quote>
    suspend fun toggleFavorite(quote: Quote)
}
