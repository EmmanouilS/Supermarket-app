package com.example.supermarketapp.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "shopping_list_items",
    indices = [Index("listId"), Index("productId")]
)
data class ShoppingListItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val listId: Long,
    val productId: Long,
    val quantity: Double,
    val unitPriceSnapshot: Double,
    val offerSnapshot: Boolean
)


