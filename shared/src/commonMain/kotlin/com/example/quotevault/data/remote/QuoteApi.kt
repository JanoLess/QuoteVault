package com.example.quotevault.data.remote

import com.example.quotevault.data.model.Quote
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
private data class QuotesResponse(
    val quotes: List<Quote>,
    val total: Int,
    val skip: Int,
    val limit: Int,
)

internal fun HttpClientConfig<*>.configureQuoteClient() {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
}

expect fun createHttpClient(): HttpClient

class QuoteApi(private val client: HttpClient) {
    suspend fun fetchQuotes(): List<Quote> {
        val response = client.get("https://dummyjson.com/quotes?limit=0")
        check(response.status == HttpStatusCode.OK) {
            "Unable to load quotes: ${response.status}"
        }
        return response.body<QuotesResponse>().quotes
    }
}
