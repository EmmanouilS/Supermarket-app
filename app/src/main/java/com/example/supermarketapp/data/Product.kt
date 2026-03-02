package com.example.supermarketapp.data

import androidx.compose.ui.graphics.Color

data class Product(
    val id: String,
    val name: String,
    val nameGreek: String,
    val description: String,
    val descriptionGreek: String,
    val category: ProductCategory,
    val price: Double,
    val pricePerUnit: String, // e.g., "€/kg", "€/piece"
    val imageUrl: String,
    val isAvailable: Boolean = true,
    val ingredients: String = "",
    val ingredientsGreek: String = "",
    val nutritionalInfo: NutritionalInfo = NutritionalInfo(),
    val discount: Discount? = null,
    val stockQuantity: Int = 0
)

data class NutritionalInfo(
    val calories: Int = 0,
    val protein: Double = 0.0,
    val carbohydrates: Double = 0.0,
    val fat: Double = 0.0,
    val fiber: Double = 0.0,
    val sugar: Double = 0.0,
    val sodium: Double = 0.0
)

data class Discount(
    val percentage: Int,
    val originalPrice: Double,
    val validUntil: String
)

enum class ProductCategory(
    val displayName: String,
    val displayNameGreek: String,
    val color: Color
) {
    FRESH_FOOD("Fresh Food", "Τρόφιμα", Color(0xFF4CAF50)),
    DAIRY("Dairy", "Γαλακτοκομικά", Color(0xFF2196F3)),
    FROZEN("Frozen", "Κατεψυγμένα", Color(0xFF00BCD4)),
    CLEANING("Cleaning", "Καθαριστικά", Color(0xFFFF9800)),
    BEVERAGES("Beverages", "Αναψυκτικά", Color(0xFF9C27B0)),
    SNACKS("Snacks", "Σνακ", Color(0xFFFF5722)),
    BAKERY("Bakery", "Αρτοποιία", Color(0xFF795548)),
    CANNED("Canned", "Κονσέρβες", Color(0xFF607D8B)),
    SPICES("Spices", "Μπαχαρικά", Color(0xFF8BC34A)),
    PERSONAL_CARE("Personal Care", "Προσωπική Φροντίδα", Color(0xFFE91E63))
} 