package com.example.quotevault.data.remote

import com.example.quotevault.domain.model.Quote
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
private data class QuotesResponse(
    val quotes: List<Quote>,
    val total: Int,
    val skip: Int,
    val limit: Int,
)

fun interface QuoteRemote {
    suspend fun fetchQuotes(): List<Quote>
}

internal fun HttpClientConfig<*>.configureQuoteClient() {
    install(Logging) {
        level = LogLevel.INFO
    }
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
}

expect fun createHttpClient(): HttpClient

class QuoteApi(private val client: HttpClient) : QuoteRemote {
    override suspend fun fetchQuotes(): List<Quote> {
        return try {
            val response = client.get(QUOTES_URL)
            if (response.status != HttpStatusCode.OK) {
                error("Quote server returned ${response.status}.")
            }
            response.body<QuotesResponse>().quotes
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            throw IllegalStateException(
                error.message ?: "Unable to reach the quote server.",
                error,
            )
        }
    }

    private companion object {
        const val QUOTES_URL = "https://dummyjson.com/quotes?limit=50"
    }
}
