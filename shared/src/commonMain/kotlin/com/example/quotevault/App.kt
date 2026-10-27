package com.example.quotevault

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.quotevault.data.remote.QuoteApi
import com.example.quotevault.data.remote.createHttpClient
import com.example.quotevault.data.repository.QuoteRepositoryImpl
import com.example.quotevault.domain.usecase.GetQuotesUseCase
import com.example.quotevault.domain.usecase.SearchQuotesUseCase
import com.example.quotevault.domain.usecase.ToggleFavoriteUseCase
import com.example.quotevault.presentation.BrowseQuotesViewModel
import com.example.quotevault.presentation.screens.BrowseQuotesScreen
import com.example.quotevault.presentation.screens.DetailQuoteScreen
import com.example.quotevault.presentation.screens.FavoritesQuotesScreen

private sealed interface Destination {
    data object Browse : Destination
    data object Favorites : Destination
    data object Settings : Destination
    data class Detail(val quoteId: Int) : Destination
}

@Composable
fun App() {
    val scope = rememberCoroutineScope()
    val client = remember { createHttpClient() }
    DisposableEffect(client) {
        onDispose { client.close() }
    }

    val repository = remember(client) { QuoteRepositoryImpl(QuoteApi(client)) }
    val viewModel = remember(scope, repository) {
        BrowseQuotesViewModel(
            getQuotes = GetQuotesUseCase(repository),
            searchQuotes = SearchQuotesUseCase(),
            toggleFavorite = ToggleFavoriteUseCase(repository),
            repository = repository,
            scope = scope,
        )
    }

    val uiState by viewModel.uiState.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val favoriteIds = remember(favorites) { favorites.mapTo(mutableSetOf()) { it.id } }
    var destination by remember { mutableStateOf<Destination>(Destination.Browse) }
    var previousDestination by remember { mutableStateOf<Destination>(Destination.Browse) }
    var query by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    LaunchedEffect(viewModel) {
        viewModel.loadQuotes()
        viewModel.events.collect { message = it }
    }

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1f)) {
                    when (val current = destination) {
                        Destination.Browse -> BrowseQuotesScreen(
                            state = uiState,
                            query = query,
                            favoriteIds = favoriteIds,
                            message = message,
                            onSearch = {
                                query = it
                                viewModel.onSearch(it)
                            },
                            onRefresh = { viewModel.loadQuotes(forceRefresh = true) },
                            onQuoteClick = { id ->
                                previousDestination = destination
                                destination = Destination.Detail(id)
                            },
                            onToggleFavorite = viewModel::onToggleFavorite,
                        )

                        Destination.Favorites -> FavoritesQuotesScreen(
                            favorites = favorites,
                            favoriteIds = favoriteIds,
                            onQuoteClick = { id ->
                                previousDestination = destination
                                destination = Destination.Detail(id)
                            },
                            onToggleFavorite = viewModel::onToggleFavorite,
                        )

                        Destination.Settings -> Column(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                        ) {
                            Text("Settings", style = MaterialTheme.typography.headlineMedium)
                            Text("Settings are not available yet.", modifier = Modifier.padding(top = 8.dp))
                        }

                        is Destination.Detail -> DetailQuoteScreen(
                            quote = viewModel.findQuote(current.quoteId),
                            isFavorite = current.quoteId in favoriteIds,
                            onToggleFavorite = viewModel::onToggleFavorite,
                            onBack = { destination = previousDestination },
                        )
                    }
                }

                if (destination !is Destination.Detail) {
                    AppNavigationBar(
                        destination = destination,
                        onSelect = { destination = it },
                    )
                }
            }
        }
    }
}

@Composable
private fun AppNavigationBar(
    destination: Destination,
    onSelect: (Destination) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        NavigationButton("Browse", destination is Destination.Browse) {
            onSelect(Destination.Browse)
        }
        NavigationButton("Favorites", destination is Destination.Favorites) {
            onSelect(Destination.Favorites)
        }
        NavigationButton("Settings", destination is Destination.Settings) {
            onSelect(Destination.Settings)
        }
    }
}

@Composable
private fun NavigationButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    if (selected) {
        Button(onClick = onClick) { Text(label) }
    } else {
        OutlinedButton(onClick = onClick) { Text(label) }
    }
}
