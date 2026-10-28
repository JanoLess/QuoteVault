package com.example.quotevault.data.local

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import java.io.File

@Composable
actual fun rememberQuoteDatabase(): QuoteDatabase = remember {
    val directory = File(System.getProperty("user.home"), ".quotevault")
    check(directory.isDirectory || directory.mkdirs()) {
        "Unable to create the QuoteVault database directory."
    }
    Room.databaseBuilder<QuoteDatabase>(name = File(directory, "quotevault.db").absolutePath)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
