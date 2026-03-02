package com.example.supermarketapp.data

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.example.supermarketapp.data.Product

data class ShoppingList(
    val id: String,
    val name: String,
    val items: List<ShoppingItem>,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val completedAt: LocalDateTime? = null
) {
    val totalCost: Double
        get() = items.sumOf { it.totalPrice }
    
    fun getItemCount(): Int = items.size
    fun isCompleted(): Boolean = completedAt != null
}

data class ShoppingItem(
    val product: Product,
    val quantity: Int,
    val isChecked: Boolean = false
) {
    val totalPrice: Double
        get() = product.price * quantity
}

data class WishListItem(
    val product: Product,
    val addedAt: LocalDateTime = LocalDateTime.now()
)

data class PurchaseHistory(
    val id: String,
    val shoppingList: ShoppingList,
    val totalSpent: Double,
    val date: LocalDateTime
)

object DateTimeUtils {
    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    
    fun formatDateTime(dateTime: LocalDateTime): String = dateTime.format(formatter)
    fun formatDate(dateTime: LocalDateTime): String = dateTime.format(dateFormatter)
} 