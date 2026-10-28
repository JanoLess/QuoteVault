package com.example.quotevault.presentation

import com.example.quotevault.domain.model.Quote

sealed interface QuotesUiState {
    data object Loading : QuotesUiState
    data class Success(val quotes: List<Quote>) : QuotesUiState
    data class Error(val message: String) : QuotesUiState
}
