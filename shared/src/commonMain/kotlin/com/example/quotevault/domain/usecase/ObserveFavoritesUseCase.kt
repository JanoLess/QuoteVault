package com.example.quotevault.domain.usecase

import com.example.quotevault.domain.model.Quote
import com.example.quotevault.domain.repository.QuoteRepository
import kotlinx.coroutines.flow.StateFlow

class ObserveFavoritesUseCase(
    private val repository: QuoteRepository,
) {
    operator fun invoke(): StateFlow<List<Quote>> = repository.favorites
}
