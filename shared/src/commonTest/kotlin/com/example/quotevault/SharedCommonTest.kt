package com.example.quotevault

import com.example.quotevault.data.local.MemoryLocalStore
import com.example.quotevault.data.remote.QuoteRemote
import com.example.quotevault.data.repository.QuoteRepositoryImpl
import com.example.quotevault.data.repository.SettingsRepositoryImpl
import com.example.quotevault.domain.model.Quote
import com.example.quotevault.domain.usecase.GetQuotesUseCase
import com.example.quotevault.domain.usecase.SearchQuotesUseCase
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SharedCommonTest {

    private val quotes = FakeQuoteRepository.sampleQuotes

    @Test
    fun searchMatchesQuoteTextAndAuthorIgnoringCase() {
        assertEquals(listOf(quotes[0]), SearchQuotesUseCase()(quotes, "DALAI"))
        assertEquals(listOf(quotes[1]), SearchQuotesUseCase()(quotes, "JOURNEY"))
        assertEquals(quotes, SearchQuotesUseCase()(quotes, "  "))
    }

    @Test
    fun favoriteCanBeAddedAndRemoved() {
        val repository = FakeQuoteRepository()
        val quote = quotes[0]

        repository.toggleFavorite(quote)
        assertEquals(listOf(quote), repository.favorites.value)

        repository.toggleFavorite(quote)
        assertEquals(emptyList(), repository.favorites.value)
    }

    @Test
    fun getQuotesUseCaseReadsFromRepository() = runBlocking {
        val useCase = GetQuotesUseCase(FakeQuoteRepository())
        assertEquals(quotes, useCase())
    }

    @Test
    fun repositoryReturnsCachedQuotesWhenNetworkFails() = runBlocking {
        var shouldFail = false
        val store = MemoryLocalStore()
        val repository = QuoteRepositoryImpl(
            remote = QuoteRemote {
                if (shouldFail) error("offline")
                quotes
            },
            localStore = store,
        )

        assertEquals(quotes, repository.getQuotes())

        shouldFail = true
        assertEquals(quotes, repository.getQuotes(forceRefresh = true))
        assertEquals(
            quotes,
            QuoteRepositoryImpl(
                remote = QuoteRemote { error("offline") },
                localStore = store,
            ).getQuotes(),
        )
    }

    @Test
    fun favoritesSurviveANewRepositoryInstance() {
        val store = MemoryLocalStore()
        val first = QuoteRepositoryImpl(
            remote = QuoteRemote { quotes },
            localStore = store,
        )
        first.toggleFavorite(quotes[0])

        val second = QuoteRepositoryImpl(
            remote = QuoteRemote { quotes },
            localStore = store,
        )
        assertEquals(listOf(quotes[0]), second.favorites.value)
    }

    @Test
    fun firstLoadFailsWhenThereIsNoCache() = runBlocking {
        val repository = QuoteRepositoryImpl(
            remote = QuoteRemote { error("offline") },
            localStore = MemoryLocalStore(),
        )
        assertFailsWith<IllegalStateException> {
            repository.getQuotes()
        }
    }

    @Test
    fun darkModeSettingIsSavedLocally() {
        val settings = SettingsRepositoryImpl(MemoryLocalStore())
        assertFalse(settings.isDarkMode())
        settings.setDarkMode(true)
        assertTrue(settings.isDarkMode())
    }
}
