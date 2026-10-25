package com.example.quotevault.data.repository

import com.example.quotevault.data.model.Quote
import com.example.quotevault.domain.repository.QuoteRepository

class MockQuoteRepository : QuoteRepository {
    override suspend fun getQuotes(): List<Quote> = listOf(
        Quote(1, "The future depends on what you do today.", "Mahatma Gandhi"),
        Quote(2, "Believe you can and you are halfway there.", "Theodore Roosevelt"),
        Quote(3, "Success is the sum of small efforts repeated daily.", "Robert Collier"),
        Quote(4, "Start where you are. Use what you have. Do what you can.", "Arthur Ashe"),
    )
}
