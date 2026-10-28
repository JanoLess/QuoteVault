package com.example.quotevault

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.example.quotevault.data.local.FavoriteQuoteEntity
import com.example.quotevault.data.local.QuoteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class FavoriteQuoteDaoTest {
    @Test
    fun savedFavoritesCanBeObservedAndRemoved() = runTest {
        val database = Room.inMemoryDatabaseBuilder<QuoteDatabase>()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()

        try {
            val dao = database.favoriteQuoteDao()
            val favorite = FavoriteQuoteEntity(
                id = 7,
                quote = "A persistent favorite.",
                author = "QuoteVault",
            )

            dao.save(favorite)
            assertEquals(listOf(favorite), dao.observeFavorites().first())

            dao.remove(favorite.id)
            assertEquals(emptyList(), dao.observeFavorites().first())
        } finally {
            database.close()
        }
    }
}
