package com.example.quotevault.domain.usecase

import com.example.quotevault.data.model.Quote
import com.example.quotevault.domain.repository.QuoteRepository

class ToggleFavoriteUseCase(private val repository: QuoteRepository) {
    suspend operator fun invoke(quote: Quote) = repository.toggleFavorite(quote)
}
