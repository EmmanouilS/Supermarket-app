package com.example.supermarketapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.supermarketapp.data.local.dao.CategoryDao
import com.example.supermarketapp.data.local.dao.ProductDao
import com.example.supermarketapp.data.local.dao.ShoppingListDao
import com.example.supermarketapp.data.local.dao.ShoppingListItemDao
import com.example.supermarketapp.data.local.dao.WishlistDao

@Database(
    entities = [Category::class, Product::class, ShoppingList::class, ShoppingListItem::class, WishlistItem::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun categoryDao(): CategoryDao
    abstract fun shoppingListDao(): ShoppingListDao
    abstract fun shoppingListItemDao(): ShoppingListItemDao
    abstract fun wishlistDao(): WishlistDao
}
