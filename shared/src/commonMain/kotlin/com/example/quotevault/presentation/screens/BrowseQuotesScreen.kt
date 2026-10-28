package com.example.quotevault.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.quotevault.domain.model.Quote
import com.example.quotevault.presentation.QuotesUiState
import com.example.quotevault.presentation.components.QuoteCard

@Composable
fun BrowseQuotesScreen(
    state: QuotesUiState,
    query: String,
    favoriteIds: Set<Int>,
    onSearch: (String) -> Unit,
    onRefresh: () -> Unit,
    onQuoteClick: (Int) -> Unit,
    onToggleFavorite: (Quote) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("QuoteVault", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(
            value = query,
            onValueChange = onSearch,
            label = { Text("Search quotes or authors") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        Button(onClick = onRefresh) { Text("Refresh quotes") }

        when (state) {
            QuotesUiState.Loading -> LoadingQuotes()
            is QuotesUiState.Error -> Column(
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(state.message, color = MaterialTheme.colorScheme.error)
                Button(onClick = onRefresh) { Text("Try again") }
            }
            is QuotesUiState.Success -> QuoteList(
                quotes = state.quotes,
                favoriteIds = favoriteIds,
                emptyMessage = "Try searching for another quote.",
                onQuoteClick = onQuoteClick,
                onToggleFavorite = onToggleFavorite,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
internal fun LoadingQuotes() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator()
        Text("Loading quotes...", modifier = Modifier.padding(top = 12.dp))
    }
}

@Composable
internal fun QuoteList(
    quotes: List<Quote>,
    favoriteIds: Set<Int>,
    emptyMessage: String,
    onQuoteClick: (Int) -> Unit,
    onToggleFavorite: (Quote) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (quotes.isEmpty()) {
        Column(
            modifier = modifier.fillMaxWidth().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "No quotes found",
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = emptyMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        return
    }

    BoxWithConstraints(modifier = modifier) {
        val useGrid = maxWidth >= 700.dp
        val card: @Composable (Quote) -> Unit = { quote ->
            QuoteCard(
                quote = quote,
                isFavorite = quote.id in favoriteIds,
                onClick = { onQuoteClick(quote.id) },
                onToggleFavorite = { onToggleFavorite(quote) },
            )
        }
        if (useGrid) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 320.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(quotes, key = { it.id }) { quote ->
                    card(quote)
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(quotes, key = { it.id }) { quote ->
                    card(quote)
                }
            }
        }
    }
}
