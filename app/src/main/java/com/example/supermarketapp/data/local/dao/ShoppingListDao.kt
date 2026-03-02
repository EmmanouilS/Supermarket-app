package com.example.supermarketapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.supermarketapp.data.local.ShoppingList

@Dao
interface ShoppingListDao {
    @Query("SELECT * FROM shopping_lists WHERE status = :status ORDER BY createdAt DESC")
    suspend fun byStatus(status: String): List<ShoppingList>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: ShoppingList): Long

    @Update
    suspend fun update(list: ShoppingList)
}


