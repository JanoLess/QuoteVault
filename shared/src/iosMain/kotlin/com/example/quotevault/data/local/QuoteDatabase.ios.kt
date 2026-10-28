package com.example.quotevault.data.local

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import platform.Foundation.NSHomeDirectory

@Composable
actual fun rememberQuoteDatabase(): QuoteDatabase = remember {
    Room.databaseBuilder<QuoteDatabase>(
        name = "${NSHomeDirectory()}/Documents/quotevault.db",
    )
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
