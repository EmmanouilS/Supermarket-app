package com.example.supermarketapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey val id: Long,
    val key: String,
    val name_en: String,
    val name_el: String
)


