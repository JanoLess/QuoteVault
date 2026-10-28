package com.example.quotevault.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteQuoteDao {
    @Query("SELECT * FROM favorite_quotes ORDER BY id")
    fun observeFavorites(): Flow<List<FavoriteQuoteEntity>>

    @Query("SELECT * FROM favorite_quotes WHERE id = :id LIMIT 1")
    suspend fun findById(id: Int): FavoriteQuoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(quote: FavoriteQuoteEntity)

    @Query("DELETE FROM favorite_quotes WHERE id = :id")
    suspend fun remove(id: Int)
}
