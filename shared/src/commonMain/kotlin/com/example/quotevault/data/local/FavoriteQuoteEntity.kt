package com.example.quotevault.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_quotes")
data class FavoriteQuoteEntity(
    @PrimaryKey val id: Int,
    val quote: String,
    val author: String,
)
