package com.example.quotevault.domain.usecase

import com.example.quotevault.data.model.Quote
import com.example.quotevault.domain.repository.QuoteRepository

class GetQuotesUseCase(
    private val repository: QuoteRepository,
) {
    suspend operator fun invoke(forceRefresh: Boolean = false): List<Quote> =
        repository.getQuotes(forceRefresh)
}
