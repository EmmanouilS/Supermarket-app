package com.example.supermarketapp.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "products",
    indices = [Index("categoryId"), Index("name_en"), Index("name_el"), Index("onOffer")]
)
data class Product(
    @PrimaryKey val id: Long,
    val sku: String,
    val categoryId: Long,
    val name_en: String,
    val name_el: String,
    val desc_en: String,
    val desc_el: String,
    val ingredients_en: String,
    val ingredients_el: String,
    val nutrition: String,
    val unit: String,
    val unitPrice: Double,
    val imageName: String?,
    val inStock: Boolean,
    val onOffer: Boolean,
    val priceBeforeOffer: Double?,
    val stockQuantity: Int = 0
)


