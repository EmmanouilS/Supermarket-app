package com.example.supermarketapp.data.mappers

import com.example.supermarketapp.data.Discount
import com.example.supermarketapp.data.NutritionalInfo
import com.example.supermarketapp.data.Product as UiProduct
import com.example.supermarketapp.data.local.Product as DbProduct

private fun computeDiscount(current: Double, before: Double?): Discount? {
    if (before == null || before <= 0.0 || current >= before) return null
    val percent = (((before - current) / before) * 100).toInt()
    return Discount(percentage = percent, originalPrice = before, validUntil = "")
}

fun DbProduct.toUiProduct(): UiProduct {
    val category = when (this.categoryId) {
        1L -> com.example.supermarketapp.data.ProductCategory.FRESH_FOOD
        2L -> com.example.supermarketapp.data.ProductCategory.DAIRY
        3L -> com.example.supermarketapp.data.ProductCategory.FROZEN
        4L -> com.example.supermarketapp.data.ProductCategory.CLEANING
        5L -> com.example.supermarketapp.data.ProductCategory.BEVERAGES
        6L -> com.example.supermarketapp.data.ProductCategory.SNACKS
        7L -> com.example.supermarketapp.data.ProductCategory.BAKERY
        8L -> com.example.supermarketapp.data.ProductCategory.CANNED
        9L -> com.example.supermarketapp.data.ProductCategory.SPICES
        10L -> com.example.supermarketapp.data.ProductCategory.PERSONAL_CARE
        else -> com.example.supermarketapp.data.ProductCategory.FRESH_FOOD
    }
    
    return UiProduct(
        id = this.id.toString(),
        name = this.name_en,
        nameGreek = this.name_el,
        description = this.desc_en,
        descriptionGreek = this.desc_el,
        category = category,
        price = this.unitPrice,
        pricePerUnit = this.unit,
        imageUrl = this.imageName ?: "",
        isAvailable = this.inStock,
        ingredients = this.ingredients_en,
        ingredientsGreek = this.ingredients_el,
        nutritionalInfo = NutritionalInfo(),
        discount = computeDiscount(this.unitPrice, this.priceBeforeOffer),
        stockQuantity = this.stockQuantity
    )
}


