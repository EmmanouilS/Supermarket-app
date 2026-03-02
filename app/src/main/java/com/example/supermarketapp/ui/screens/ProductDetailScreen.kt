package com.example.supermarketapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.supermarketapp.data.Product
import com.example.supermarketapp.ui.LocalizationManager
import com.example.supermarketapp.ui.rememberLocalizedStrings
import com.example.supermarketapp.util.drawableIdOrNull

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    product: Product,
    isInWishList: Boolean = false,
    onAddToCart: (Int) -> Unit = {},
    onAddToWishList: () -> Unit = {},
    onBackClick: () -> Unit = {},
    showSnackbar: (String) -> Unit = {}
) {
    val strings = rememberLocalizedStrings()
    var quantity by remember { mutableStateOf(1) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(LocalizationManager.getLocalizedProductName(product)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onAddToWishList) {
                        Icon(
                            imageVector = if (isInWishList) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = strings.addToWishList,
                            tint = if (isInWishList) Color.Red else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Quantity Selector
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(strings.quantity, style = MaterialTheme.typography.bodyMedium)
                        Button(
                            onClick = { if (quantity > 1) quantity-- },
                            enabled = quantity > 1
                        ) {
                            Text("-")
                        }
                        Text(
                            text = quantity.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Button(
                            onClick = { quantity++ },
                            enabled = quantity < product.stockQuantity
                        ) {
                            Text("+")
                        }
                    }
                    
                    // Add to Cart Button
                    Button(
                        onClick = {
                            onAddToCart(quantity)
                            showSnackbar("${product.name} x$quantity added to shopping list")
                        },
                        enabled = product.isAvailable && product.stockQuantity > 0 && quantity <= product.stockQuantity
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text(strings.addToCart)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Product Image - Prefer local drawable over network URL
            run {
                val context = LocalContext.current
                val imageResId = remember(product.imageUrl) { 
                    context.drawableIdOrNull(product.imageUrl) 
                }
                val model = imageResId ?: product.imageUrl
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(model)
                        .crossfade(false) // Disable crossfade for better performance
                        .size(400, 400) // Optimized size for detail view
                        .memoryCachePolicy(CachePolicy.ENABLED) // Enable memory cache
                        .diskCachePolicy(CachePolicy.ENABLED) // Enable disk cache
                        .build(),
                    contentDescription = LocalizationManager.getLocalizedProductName(product),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
            }
            
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Product Name
                Text(
                    text = LocalizationManager.getLocalizedProductName(product),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Price Section
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "€${String.format("%.2f", product.price)}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    product.discount?.let { discount ->
                        Text(
                            text = "€${String.format("%.2f", discount.originalPrice)}",
                            style = MaterialTheme.typography.bodyLarge,
                            textDecoration = TextDecoration.LineThrough,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.Red)
                        ) {
                            Text(
                                text = "-${discount.percentage}%",
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                
                Text(
                    text = product.pricePerUnit,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Description
                Text(
                    text = strings.description,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = LocalizationManager.getLocalizedProductDescription(product),
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Ingredients
                if (LocalizationManager.getLocalizedProductIngredients(product).isNotEmpty()) {
                    Text(
                        text = strings.ingredients,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = LocalizationManager.getLocalizedProductIngredients(product),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Nutritional Information
                if (product.nutritionalInfo.calories > 0) {
                    Text(
                        text = strings.nutritionalInfo,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Card {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            NutritionRow(strings.calories, "${product.nutritionalInfo.calories} kcal")
                            NutritionRow(strings.protein, "${product.nutritionalInfo.protein}g")
                            NutritionRow(strings.carbohydrates, "${product.nutritionalInfo.carbohydrates}g")
                            NutritionRow(strings.fat, "${product.nutritionalInfo.fat}g")
                            NutritionRow(strings.fiber, "${product.nutritionalInfo.fiber}g")
                            NutritionRow(strings.sugar, "${product.nutritionalInfo.sugar}g")
                            NutritionRow(strings.sodium, "${product.nutritionalInfo.sodium}mg")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Availability
                Text(
                    text = strings.availability,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val isInStock = product.isAvailable && product.stockQuantity > 0
                    Icon(
                        imageVector = if (isInStock) Icons.Default.CheckCircle else Icons.Default.Close,
                        contentDescription = null,
                        tint = if (isInStock) Color.Green else Color.Red
                    )
                    Text(
                        text = if (isInStock) strings.inStock else strings.outOfStock,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isInStock) Color.Green else Color.Red
                    )
                }
                
                if (product.isAvailable && product.stockQuantity > 0) {
                    Text(
                        text = "${product.stockQuantity} ${strings.inStock.lowercase()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Discount Details
                product.discount?.let { discount ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = strings.discount,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${strings.originalPrice}: €${String.format("%.2f", discount.originalPrice)}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "${strings.validUntil}: ${discount.validUntil}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NutritionRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
} 