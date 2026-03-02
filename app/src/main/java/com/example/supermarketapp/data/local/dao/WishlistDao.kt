package com.example.supermarketapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.supermarketapp.data.local.WishlistItem

@Dao
interface WishlistDao {
    @Query("SELECT * FROM wishlist_items ORDER BY createdAt DESC")
    suspend fun all(): List<WishlistItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: WishlistItem): Long

    @Query("DELETE FROM wishlist_items WHERE productId = :productId")
    suspend fun removeByProduct(productId: Long)
}


