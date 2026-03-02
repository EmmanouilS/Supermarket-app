package com.example.supermarketapp.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "wishlist_items", indices = [Index("productId")])
data class WishlistItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Long,
    val createdAt: Long
)


