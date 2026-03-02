package com.example.supermarketapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.supermarketapp.data.WishListItem
import com.example.supermarketapp.data.ShoppingItem
import com.example.supermarketapp.ui.LocalizationManager
import com.example.supermarketapp.ui.rememberLocalizedStrings
import com.example.supermarketapp.util.drawableIdOrNull

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishListScreen(
    wishListItems: List<WishListItem>,
    shoppingItems: List<ShoppingItem> = emptyList(),
    onAddToCart: (WishListItem) -> Unit = { },
    onRemoveFromWishList: (WishListItem) -> Unit = { },
    onProductClick: (WishListItem) -> Unit = { }
) {
    val strings = rememberLocalizedStrings()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.wishList) }
            )
        }
    ) { paddingValues ->
        if (wishListItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = strings.emptyWishList,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(paddingValues)
            ) {
                items(
                    items = wishListItems,
                    key = { it.product.id } // Stable keys for better performance
                ) { wishListItem ->
                    WishListItemCard(
                        wishListItem = wishListItem,
                        shoppingItems = shoppingItems,
                        onAddToCart = onAddToCart,
                        onRemoveFromWishList = onRemoveFromWishList,
                        onProductClick = onProductClick
                    )
                }
            }
        }
    }
}

@Composable
fun WishListItemCard(
    wishListItem: WishListItem,
    shoppingItems: List<ShoppingItem>,
    onAddToCart: (WishListItem) -> Unit,
    onRemoveFromWishList: (WishListItem) -> Unit,
    onProductClick: (WishListItem) -> Unit
) {
    val strings = rememberLocalizedStrings()
    val product = wishListItem.product
    
    // Memoize expensive localization calls
    val productName = remember(product) {
        LocalizationManager.getLocalizedProductName(product)
    }
    
    val productDescription = remember(product) {
        LocalizationManager.getLocalizedProductDescription(product)
    }
    
    // Memoize price formatting
    val formattedPrice = remember(product.price) {
        "€${String.format("%.2f", product.price)}"
    }
    
    val formattedOriginalPrice = remember(product.discount?.originalPrice) {
        product.discount?.originalPrice?.let { "€${String.format("%.2f", it)}" }
    }
    
    val discountPercentage = remember(product.discount?.percentage) {
        product.discount?.percentage?.let { "-${it}%" }
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onProductClick(wishListItem) },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Image - Enhanced with better styling and error handling
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                val context = LocalContext.current
                val imageResId = remember(product.imageUrl) { 
                    context.drawableIdOrNull(product.imageUrl) 
                }
                val model = imageResId ?: product.imageUrl
                
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(model)
                        .crossfade(false) // Disable crossfade for better performance
                        .size(120, 120) // Optimized size for thumbnails
                        .memoryCachePolicy(CachePolicy.ENABLED) // Enable memory cache
                        .diskCachePolicy(CachePolicy.ENABLED) // Enable disk cache
                        .allowHardware(true) // Use hardware acceleration
                        .allowRgb565(true) // Use RGB565 for better performance
                        .build(),
                    contentDescription = productName,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Fallback icon if image fails to load
                if (product.imageUrl.isEmpty() || product.imageUrl == "placeholder") {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Product image",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Product Info - Memoized to prevent unnecessary recompositions
            val productInfo = remember(productName, productDescription, formattedPrice, formattedOriginalPrice, discountPercentage, product.pricePerUnit) {
                @Composable {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = productName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = productDescription,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        // Price Section
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = formattedPrice,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            
                            formattedOriginalPrice?.let { originalPrice ->
                                Text(
                                    text = originalPrice,
                                    style = MaterialTheme.typography.bodySmall,
                                    textDecoration = TextDecoration.LineThrough,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                
                                discountPercentage?.let { discount ->
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.error)
                                    ) {
                                        Text(
                                            text = discount,
                                            color = MaterialTheme.colorScheme.onError,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                        
                        Text(
                            text = product.pricePerUnit,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            productInfo()
            
            // Action Buttons - Memoized to prevent unnecessary recompositions
            val cartQuantity = remember(shoppingItems, product.id) {
                shoppingItems.find { it.product.id == product.id }?.quantity ?: 0
            }
            
            val actionButtons = remember(product.isAvailable, cartQuantity) {
                @Composable {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Heart icon to remove from wish list
                        IconButton(
                            onClick = { onRemoveFromWishList(wishListItem) }
                        ) {
                            Icon(
                                Icons.Default.Favorite,
                                contentDescription = "Remove from wish list",
                                tint = Color(0xFFE91E63) // Soft pink-red color
                            )
                        }
                        
                        // Add to cart button with quantity indicator
                        Box {
                            IconButton(
                                onClick = { onAddToCart(wishListItem) },
                                enabled = product.isAvailable && product.stockQuantity > 0 && cartQuantity < product.stockQuantity
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = strings.addToCart,
                                    tint = if (product.isAvailable && product.stockQuantity > 0 && cartQuantity < product.stockQuantity) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            
                            // Quantity badge on top of plus icon
                            if (cartQuantity > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = androidx.compose.foundation.shape.CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = cartQuantity.toString(),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
            actionButtons()
        }
    }
} 