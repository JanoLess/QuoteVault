package com.example.quotevault.di

import com.example.quotevault.data.local.createLocalStore
import com.example.quotevault.data.remote.QuoteApi
import com.example.quotevault.data.remote.createHttpClient
import com.example.quotevault.data.repository.QuoteRepositoryImpl
import com.example.quotevault.data.repository.SettingsRepositoryImpl
import com.example.quotevault.domain.repository.SettingsRepository
import com.example.quotevault.domain.usecase.GetQuotesUseCase
import com.example.quotevault.domain.usecase.ObserveFavoritesUseCase
import com.example.quotevault.domain.usecase.SearchQuotesUseCase
import com.example.quotevault.domain.usecase.ToggleFavoriteUseCase
import com.example.quotevault.presentation.BrowseQuotesViewModel
import kotlinx.coroutines.CoroutineScope

class AppGraph(
    val viewModel: BrowseQuotesViewModel,
    val settings: SettingsRepository,
    val close: () -> Unit,
)

fun createAppGraph(scope: CoroutineScope): AppGraph {
    val client = createHttpClient()
    val localStore = createLocalStore()
    val quotes = QuoteRepositoryImpl(QuoteApi(client), localStore)
    return AppGraph(
        viewModel = BrowseQuotesViewModel(
            getQuotes = GetQuotesUseCase(quotes),
            searchQuotes = SearchQuotesUseCase(),
            toggleFavorite = ToggleFavoriteUseCase(quotes),
            observeFavorites = ObserveFavoritesUseCase(quotes),
            scope = scope,
        ),
        settings = SettingsRepositoryImpl(localStore),
        close = { client.close() },
    )
}
