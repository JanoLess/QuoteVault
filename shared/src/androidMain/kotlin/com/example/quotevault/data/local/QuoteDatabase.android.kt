package com.example.quotevault.data.local

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers

@Composable
actual fun rememberQuoteDatabase(): QuoteDatabase {
    val context = LocalContext.current.applicationContext
    return remember(context) {
        Room.databaseBuilder<QuoteDatabase>(context, "quotevault.db")
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}
