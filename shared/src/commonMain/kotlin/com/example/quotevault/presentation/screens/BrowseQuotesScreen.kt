package com.example.quotevault.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.quotevault.data.model.Quote
import com.example.quotevault.presentation.BrowseQuotesViewModel
import com.example.quotevault.presentation.QuotesUiState
import kotlinx.coroutines.launch

@Composable
fun BrowseQuotesScreen(
    viewModel: BrowseQuotesViewModel,
    onQuoteClick: (Quote) -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadQuotes()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Browse Quotes",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 12.dp),
        )

        when (val currentState = state) {
            QuotesUiState.Loading -> LoadingContent()
            is QuotesUiState.Success -> QuoteList(
                quotes = currentState.quotes,
                onQuoteClick = onQuoteClick,
            )
            is QuotesUiState.Error -> ErrorContent(
                message = currentState.message,
                onRetry = { scope.launch { viewModel.loadQuotes() } },
            )
        }
    }
}

@Composable
private fun LoadingContent() {
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
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Error: $message")
        Button(onClick = onRetry, modifier = Modifier.padding(top = 12.dp)) {
            Text("Try again")
        }
    }
}

@Composable
private fun QuoteList(
    quotes: List<Quote>,
    onQuoteClick: (Quote) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(quotes, key = { quote -> quote.id }) { quote ->
            QuoteCard(quote = quote, onClick = { onQuoteClick(quote) })
        }
    }
}

@Composable
private fun QuoteCard(
    quote: Quote,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "\"${quote.quote}\"",
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "- ${quote.author}",
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}
