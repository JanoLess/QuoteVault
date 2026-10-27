package com.example.quotevault.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.quotevault.data.model.Quote
import com.example.quotevault.presentation.QuotesUiState

@Composable
fun BrowseQuotesScreen(
    state: QuotesUiState,
    query: String,
    favoriteIds: Set<Int>,
    message: String,
    onSearch: (String) -> Unit,
    onRefresh: () -> Unit,
    onQuoteClick: (Int) -> Unit,
    onToggleFavorite: (Quote) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
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
        if (message.isNotEmpty()) {
            Text(message, color = MaterialTheme.colorScheme.primary)
        }

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
                onQuoteClick = onQuoteClick,
                onToggleFavorite = onToggleFavorite,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun LoadingQuotes() {
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
private fun QuoteList(
    quotes: List<Quote>,
    favoriteIds: Set<Int>,
    onQuoteClick: (Int) -> Unit,
    onToggleFavorite: (Quote) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (quotes.isEmpty()) {
        Text("No quotes found.", modifier = modifier.padding(top = 20.dp))
        return
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(quotes, key = { it.id }) { quote ->
            QuoteCard(
                quote = quote,
                isFavorite = quote.id in favoriteIds,
                onClick = { onQuoteClick(quote.id) },
                onToggleFavorite = { onToggleFavorite(quote) },
            )
        }
    }
}

@Composable
private fun QuoteCard(
    quote: Quote,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
                Text(
                    text = "\"${quote.quote}\"",
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "- ${quote.author}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onToggleFavorite) {
                    Text(if (isFavorite) "Remove favorite" else "Save favorite")
                }
            }
        }
    }
}

@Composable
fun FavoritesQuotesScreen(
    favorites: List<Quote>,
    favoriteIds: Set<Int>,
    onQuoteClick: (Int) -> Unit,
    onToggleFavorite: (Quote) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Favorites", style = MaterialTheme.typography.headlineMedium)
        if (favorites.isEmpty()) {
            Text("No saved quotes yet.", modifier = Modifier.padding(top = 12.dp))
        } else {
            QuoteList(
                quotes = favorites,
                favoriteIds = favoriteIds,
                onQuoteClick = onQuoteClick,
                onToggleFavorite = onToggleFavorite,
                modifier = Modifier.weight(1f).padding(top = 8.dp),
            )
        }
    }
}

@Composable
fun DetailQuoteScreen(
    quote: Quote?,
    isFavorite: Boolean,
    onToggleFavorite: (Quote) -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (quote == null) {
            Text("Quote not found.")
        } else {
            Text("\"${quote.quote}\"", style = MaterialTheme.typography.headlineSmall)
            Text("- ${quote.author}", style = MaterialTheme.typography.bodyLarge)
            Button(onClick = { onToggleFavorite(quote) }) {
                Text(if (isFavorite) "Remove from favorites" else "Save to favorites")
            }
        }
        androidx.compose.material3.OutlinedButton(onClick = onBack) { Text("Back") }
    }
}
