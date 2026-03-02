package com.example.supermarketapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Badge
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.example.supermarketapp.ui.theme.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.supermarketapp.data.Product
import com.example.supermarketapp.ui.LocalizationManager
import com.example.supermarketapp.ui.rememberLocalizedStrings
import com.example.supermarketapp.util.drawableIdOrNull

@Composable
fun ProductCard(
    product: Product,
    isInWishList: Boolean = false,
    onAddToCart: () -> Unit = {},
    onRemoveFromCart: () -> Unit = {},
    onAddToWishList: () -> Unit = {},
    onProductClick: () -> Unit = {},
    cartQuantity: Int = 0,
    modifier: Modifier = Modifier
) {
    val strings = rememberLocalizedStrings()
    
    // Simple localization calls - no memoization
    val productName = LocalizationManager.getLocalizedProductName(product)
    val productDescription = LocalizationManager.getLocalizedProductDescription(product)
    val formattedPrice = "€${String.format("%.2f", product.price)}"
    val formattedOriginalPrice = product.discount?.originalPrice?.let { "€${String.format("%.2f", it)}" }
    val discountPercentage = product.discount?.percentage?.let { "-${it}%" }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
            .clickable { onProductClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxHeight()
        ) {
            // Product Image - Prefer local drawable over network URL
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(12.dp))
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
                        .size(120, 120) // Smaller size for better performance
                        .memoryCachePolicy(CachePolicy.ENABLED) // Enable memory cache
                        .diskCachePolicy(CachePolicy.ENABLED) // Enable disk cache
                        .allowHardware(true) // Use hardware acceleration
                        .allowRgb565(true) // Use RGB565 for better performance
                        .build(),
                    contentDescription = productName,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Wish List Button - Simple implementation
                IconButton(
                    onClick = onAddToWishList,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                            shape = RoundedCornerShape(50)
                        )
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isInWishList) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = strings.addToWishList,
                        tint = if (isInWishList) Color(0xFFC91A1A) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Discount Badge - Only show if discount exists
                discountPercentage?.let { discount ->
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .background(
                                color = RedAccent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = discount,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Product Name - Fixed height, single line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = productName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Product Description - Fixed height
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Text(
                    text = productDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // Push price section to bottom with less space
            Spacer(modifier = Modifier.height(8.dp))
            
            // Price Section - Simple implementation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .height(50.dp)
                        .weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Column {
                        // Current Price
                        Text(
                            text = formattedPrice,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        
                        // Original Price (if discounted)
                        formattedOriginalPrice?.let { originalPrice ->
                            Text(
                                text = originalPrice,
                                style = MaterialTheme.typography.bodySmall,
                                textDecoration = TextDecoration.LineThrough,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        
                        // Price per unit
                        Text(
                            text = product.pricePerUnit,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                
                // Quantity Controls - Plus/Minus buttons with quantity display
                if (cartQuantity > 0) {
                    // Show quantity controls when item is in cart
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Button(
                            onClick = { onRemoveFromCart() },
                            enabled = cartQuantity > 0,
                            modifier = Modifier.size(32.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = "−",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Text(
                            text = cartQuantity.toString(),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        )
                        
                        Button(
                            onClick = { onAddToCart() },
                            enabled = product.isAvailable && product.stockQuantity > 0 && cartQuantity < product.stockQuantity,
                            modifier = Modifier.size(32.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = "+",
                                color = if (product.isAvailable && product.stockQuantity > 0 && cartQuantity < product.stockQuantity) Color.White else Color.Gray,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    // Show add button when item is not in cart
                    Button(
                        onClick = { onAddToCart() },
                        enabled = product.isAvailable && product.stockQuantity > 0,
                        modifier = Modifier.size(40.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = strings.addToCart,
                            modifier = Modifier.size(20.dp),
                            tint = if (product.isAvailable && product.stockQuantity > 0) Color.White else Color.Gray
                        )
                    }
                }
            }
            
        }
    }
}