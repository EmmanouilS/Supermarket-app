package com.example.supermarketapp.data.seed

import android.content.Context
import com.example.supermarketapp.data.local.AppDatabase
import com.example.supermarketapp.data.local.Category
import com.example.supermarketapp.data.local.Product
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class SeedData(
    val version: Int,
    val categories: List<Category>,
    val products: List<Product>
)

suspend fun seedIfEmpty(context: Context, db: AppDatabase) {
    // Always seed/update the database with the latest data
    val jsonStr = context.assets.open("products.json").bufferedReader().use { it.readText() }
    val seed = Json { ignoreUnknownKeys = true }.decodeFromString<SeedData>(jsonStr)
    db.categoryDao().upsertAll(seed.categories)
    db.productDao().upsertAll(seed.products)
}

suspend fun clearAndSeed(context: Context, db: AppDatabase) {
    // Clear all existing data and seed fresh
    db.productDao().clearAll()
    db.categoryDao().clearAll()
    seedIfEmpty(context, db)
}


