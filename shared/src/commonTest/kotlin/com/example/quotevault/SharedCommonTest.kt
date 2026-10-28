package com.example.quotevault

import com.example.quotevault.data.model.Quote
import com.example.quotevault.data.repository.MockQuoteRepository
import com.example.quotevault.domain.usecase.SearchQuotesUseCase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

class SharedCommonTest {

    @Test
    fun searchMatchesQuoteTextAndAuthorIgnoringCase() {
        val quotes = listOf(
            Quote(1, "Be kind whenever possible.", "Dalai Lama"),
            Quote(2, "The journey matters.", "Unknown"),
        )

        assertEquals(
            listOf(quotes[0]),
            SearchQuotesUseCase()(quotes, "DALAI"),
        )
        assertEquals(
            listOf(quotes[1]),
            SearchQuotesUseCase()(quotes, "JOURNEY"),
        )
        assertEquals(quotes, SearchQuotesUseCase()(quotes, "  "))
    }

    @Test
    fun favoriteCanBeAddedAndRemoved() = runTest {
        val repository = MockQuoteRepository()
        val quote = Quote(1, "Be kind whenever possible.", "Dalai Lama")

        repository.toggleFavorite(quote)
        assertEquals(listOf(quote), repository.favorites.value)

        repository.toggleFavorite(quote)
        assertEquals(emptyList(), repository.favorites.value)
    }
}