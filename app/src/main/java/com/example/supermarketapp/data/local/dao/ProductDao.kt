package com.example.supermarketapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.supermarketapp.data.local.Product

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY name_en")
    suspend fun all(): List<Product>

    @Query(
        """
        SELECT * FROM products
        WHERE (:categoryId IS NULL OR categoryId = :categoryId)
          AND (:q IS NULL OR name_en LIKE :q OR name_el LIKE :q)
          AND (:offerOnly == 0 OR onOffer = 1)
        ORDER BY name_en
        """
    )
    suspend fun search(categoryId: Long?, q: String?, offerOnly: Int): List<Product>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(products: List<Product>)

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun byId(id: Long): Product?

    @Query("DELETE FROM products")
    suspend fun clearAll()
    
    @Query("UPDATE products SET stockQuantity = :newStock WHERE id = :productId")
    suspend fun updateStock(productId: Long, newStock: Int)
    
    @Query("UPDATE products SET stockQuantity = :newStock, inStock = :isAvailable WHERE id = :productId")
    suspend fun updateStockAndAvailability(productId: Long, newStock: Int, isAvailable: Boolean)
    
    @Query("SELECT stockQuantity FROM products WHERE id = :productId")
    suspend fun getStockQuantity(productId: Long): Int?
    
    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductById(productId: Long): Product?
}


