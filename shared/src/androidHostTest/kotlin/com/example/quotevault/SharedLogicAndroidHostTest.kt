package com.example.quotevault

import com.example.quotevault.domain.model.Quote
import com.example.quotevault.domain.usecase.SearchQuotesUseCase
import kotlin.test.Test
import kotlin.test.assertEquals

class SharedLogicAndroidHostTest {

    @Test
    fun searchFiltersByAuthor() {
        val quotes = listOf(
            Quote(1, "Be kind whenever possible.", "Dalai Lama"),
            Quote(2, "The journey matters.", "Unknown"),
        )
        assertEquals(listOf(quotes[0]), SearchQuotesUseCase()(quotes, "lama"))
    }
}
