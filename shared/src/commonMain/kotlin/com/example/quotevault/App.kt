package com.example.quotevault

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.quotevault.data.repository.MockQuoteRepository
import com.example.quotevault.domain.usecase.GetQuotesUseCase
import com.example.quotevault.presentation.BrowseQuotesViewModel
import com.example.quotevault.presentation.screens.BrowseQuotesScreen

@Composable
fun App() {
    val viewModel = remember {
        BrowseQuotesViewModel(GetQuotesUseCase(MockQuoteRepository()))
    }
    var selectedQuoteAuthor by remember { mutableStateOf<String?>(null) }

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            BrowseQuotesScreen(
                viewModel = viewModel,
                onQuoteClick = { quote -> selectedQuoteAuthor = quote.author },
            )

            selectedQuoteAuthor?.let { author ->
                Text(
                    text = "Selected quote by $author",
                    modifier = Modifier.padding(top = 12.dp),
                )
            }
        }
    }
}
