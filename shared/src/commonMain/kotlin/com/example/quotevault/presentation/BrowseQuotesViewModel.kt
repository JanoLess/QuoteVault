package com.example.quotevault.presentation

import com.example.quotevault.data.model.Quote
import com.example.quotevault.domain.usecase.GetQuotesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed interface QuotesUiState {
    data object Loading : QuotesUiState
    data class Success(val quotes: List<Quote>) : QuotesUiState
    data class Error(val message: String) : QuotesUiState
}

class BrowseQuotesViewModel(
    private val getQuotes: GetQuotesUseCase,
) {
    private val _uiState = MutableStateFlow<QuotesUiState>(QuotesUiState.Loading)
    val uiState: StateFlow<QuotesUiState> = _uiState.asStateFlow()

    suspend fun loadQuotes() {
        _uiState.value = QuotesUiState.Loading

        try {
            _uiState.value = QuotesUiState.Success(getQuotes())
        } catch (error: Exception) {
            _uiState.value = QuotesUiState.Error(
                error.message ?: "Unable to load quotes.",
            )
        }
    }
}
