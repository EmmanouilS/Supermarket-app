package com.example.supermarketapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.supermarketapp.data.local.Category

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY name_en")
    suspend fun all(): List<Category>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(categories: List<Category>)

    @Query("DELETE FROM categories")
    suspend fun clearAll()
}


