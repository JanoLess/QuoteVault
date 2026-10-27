package com.example.quotevault.domain.usecase

import com.example.quotevault.data.model.Quote

class SearchQuotesUseCase {
    operator fun invoke(quotes: List<Quote>, query: String): List<Quote> {
        if (query.isBlank()) return quotes
        return quotes.filter {
            it.quote.contains(query, ignoreCase = true) ||
                it.author.contains(query, ignoreCase = true)
        }
    }
}
