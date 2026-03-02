package com.example.supermarketapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.supermarketapp.data.Product
import com.example.supermarketapp.data.ProductCategory
import com.example.supermarketapp.data.mappers.toUiProduct
import com.example.supermarketapp.ui.LocalizationManager
import com.example.supermarketapp.ui.rememberLocalizedStrings
import com.example.supermarketapp.ui.components.ProductCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    onProductClick: (Product) -> Unit = {},
    onAddToCart: (Product) -> Unit = {},
    onRemoveFromCart: (Product) -> Unit = {},
    onAddToWishList: (Product) -> Unit = {},
    wishListProducts: Set<String> = emptySet(),
    showSnackbar: (String) -> Unit = {},
    shoppingItems: List<com.example.supermarketapp.data.ShoppingItem> = emptyList(),
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    selectedCategory: com.example.supermarketapp.data.ProductCategory? = null,
    onSelectedCategoryChange: (com.example.supermarketapp.data.ProductCategory?) -> Unit = {},
    showOffersOnly: Boolean = false,
    onShowOffersOnlyChange: (Boolean) -> Unit = {},
    resetKey: Int = 0
) {
    val strings = rememberLocalizedStrings()
    
    // Simple shopping items map - no memoization
    val shoppingItemsMap = shoppingItems.associate { it.product.id to it.quantity }
    
    // Simplified product loading - no complex memoization
    val context = androidx.compose.ui.platform.LocalContext.current
    var products by remember { mutableStateOf<List<com.example.supermarketapp.data.Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    
    LaunchedEffect(searchQuery, selectedCategory, showOffersOnly, strings.language) {
        val db = (context.applicationContext as com.example.supermarketapp.MyApp).db
        val all = db.productDao().all().map { it.toUiProduct() }
        val filtered = all.filter { product ->
            val matchesSearch = searchQuery.isEmpty() ||
                LocalizationManager.getLocalizedProductName(product).contains(searchQuery, ignoreCase = true) ||
                LocalizationManager.getLocalizedProductDescription(product).contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedCategory == null || product.category == selectedCategory
            val matchesOffers = !showOffersOnly || product.discount != null
            matchesSearch && matchesCategory && matchesOffers
        }
        products = filtered.sortedBy { product ->
            if (searchQuery.isEmpty()) {
                product.name
            } else {
                val name = LocalizationManager.getLocalizedProductName(product)
                if (name.startsWith(searchQuery, ignoreCase = true)) "0" else "1"
            }
        }
        isLoading = false
    }
        
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Search Bar
            SearchBar(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                onSearch = onSearchQueryChange,
                active = false,
                onActiveChange = { },
                placeholder = { Text(strings.search) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = strings.search) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) { }
            
            // Category Filter
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // All Categories option
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { onSelectedCategoryChange(null) },
                        label = { Text(strings.allCategories) }
                    )
                }
                
                // Individual categories
                items(ProductCategory.values()) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { onSelectedCategoryChange(category) },
                        label = { 
                            Text(LocalizationManager.getLocalizedCategoryName(category))
                        }
                    )
                }
            }
            
            // Offers Filter
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = showOffersOnly,
                    onCheckedChange = onShowOffersOnlyChange
                )
                Text(
                    text = strings.offers,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            // Results count - Only show when filters are applied
            if (searchQuery.isNotEmpty() || selectedCategory != null || showOffersOnly) {
                Text(
                    text = "${products.size} ${strings.noResults.lowercase()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            // Products Grid - Optimized with proper key and stable callbacks
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(products) { product ->
                        val cartQuantity = shoppingItemsMap[product.id] ?: 0
                        ProductCard(
                            product = product,
                            isInWishList = wishListProducts.contains(product.id),
                            onAddToCart = { onAddToCart(product) },
                            onRemoveFromCart = { onRemoveFromCart(product) },
                            onAddToWishList = { onAddToWishList(product) },
                            onProductClick = { onProductClick(product) },
                            cartQuantity = cartQuantity
                        )
                    }
                }
            }
        }
    }