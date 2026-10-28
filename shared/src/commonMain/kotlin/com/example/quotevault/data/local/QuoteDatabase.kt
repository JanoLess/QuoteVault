package com.example.quotevault.data.local

import androidx.compose.runtime.Composable
import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(
    entities = [FavoriteQuoteEntity::class],
    version = 1,
    exportSchema = true,
)
@ConstructedBy(QuoteDatabaseConstructor::class)
abstract class QuoteDatabase : RoomDatabase() {
    abstract fun favoriteQuoteDao(): FavoriteQuoteDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object QuoteDatabaseConstructor : RoomDatabaseConstructor<QuoteDatabase>

@Composable
expect fun rememberQuoteDatabase(): QuoteDatabase
