package com.example.quotevault.domain.repository

import com.example.quotevault.data.model.Quote

interface QuoteRepository {
    suspend fun getQuotes(): List<Quote>
}
