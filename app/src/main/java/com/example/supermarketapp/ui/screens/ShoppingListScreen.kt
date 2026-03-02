package com.example.supermarketapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.supermarketapp.data.ShoppingItem
import com.example.supermarketapp.ui.rememberLocalizedStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(
    shoppingItems: List<ShoppingItem>,
    onUpdateQuantity: (ShoppingItem, Int) -> Unit = { _, _ -> },
    onRemoveItem: (ShoppingItem) -> Unit = { },
    onOrderConfirmed: () -> Unit = {},
    orderCompleted: Boolean = false,
    onOrderCompletedReset: () -> Unit = {},
    onNavigateToCatalog: () -> Unit = {}
) {
    val strings = rememberLocalizedStrings()
    val totalCost = shoppingItems.sumOf { it.totalPrice }
    var showConfirmDialog by remember { mutableStateOf(false) }
    
    // Auto-reset timer for order completed state
    LaunchedEffect(orderCompleted) {
        if (orderCompleted) {
            kotlinx.coroutines.delay(10000) // 10 seconds delay
            onOrderCompletedReset()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.shoppingList) }
                // Removed actions (X icon)
            )
        },
        bottomBar = {
            if (shoppingItems.isNotEmpty() && !orderCompleted) {
                BottomAppBar {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = strings.total,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "€${String.format("%.2f", totalCost)}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Button(
                            onClick = { 
                                println("=== CONFIRM ORDER BUTTON CLICKED ===")
                                showConfirmDialog = true 
                            },
                            enabled = shoppingItems.isNotEmpty()
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null)
                            Spacer(Modifier.width(4.dp))
                            Text(strings.confirmOrder)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (orderCompleted) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Order Completed!",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Returning to shopping list in 10 seconds...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            onOrderCompletedReset()
                            onNavigateToCatalog()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(strings.continueShopping)
                    }
                }
            }
        } else if (shoppingItems.isEmpty()) {
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
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = strings.emptyCart,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(paddingValues)
            ) {
                items(
                    items = shoppingItems,
                    key = { it.product.id } // Stable keys for better performance
                ) { item ->
                    SwipeToDeleteItem(
                        item = item,
                        onUpdateQuantity = onUpdateQuantity,
                        onRemoveItem = onRemoveItem
                    )
                }
            }
        }
        if (showConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                title = { Text(strings.confirmOrder) },
                text = { Text(strings.confirmOrderMessage) },
                confirmButton = {
                    TextButton(onClick = {
                        println("=== ORDER CONFIRMATION DIALOG: YES CLICKED ===")
                        showConfirmDialog = false
                        onOrderConfirmed()
                    }) {
                        Text(strings.yes)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmDialog = false }) {
                        Text(strings.no)
                    }
                }
            )
        }
    }
}

@Composable
fun ShoppingItemCard(
    item: ShoppingItem,
    onUpdateQuantity: (ShoppingItem, Int) -> Unit
) {
    val strings = rememberLocalizedStrings()
    
    // Memoize expensive formatting operations
    val formattedPrice = remember(item.product.price) {
        "€${String.format("%.2f", item.product.price)}"
    }
    
    val formattedTotalPrice = remember(item.totalPrice) {
        "€${String.format("%.2f", item.totalPrice)}"
    }
    
    val pricePerUnitText = remember(item.product.pricePerUnit) {
        "$formattedPrice ${item.product.pricePerUnit}"
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Info - Memoized to prevent unnecessary recompositions
            val productInfo = remember(item.product.name, pricePerUnitText, formattedTotalPrice) {
                @Composable {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = item.product.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = pricePerUnitText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formattedTotalPrice,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            productInfo()
            
            // Quantity Controls - Memoized to prevent unnecessary recompositions
            val quantityControls = remember(item.quantity) {
                @Composable {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Button(
                            onClick = { 
                                if (item.quantity > 1) {
                                    onUpdateQuantity(item, item.quantity - 1)
                                }
                            },
                            enabled = item.quantity > 1
                        ) {
                            Text("-")
                        }
                        Text(
                            text = item.quantity.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Button(
                            onClick = { onUpdateQuantity(item, item.quantity + 1) }
                        ) {
                            Text("+")
                        }
                    }
                }
            }
            quantityControls()
        }
    }
}

@Composable
fun SwipeToDeleteItem(
    item: ShoppingItem,
    onUpdateQuantity: (ShoppingItem, Int) -> Unit,
    onRemoveItem: (ShoppingItem) -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    // Swipe right to delete - confirm the action
                    true
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    // Swipe left to delete - confirm the action
                    true
                }
                SwipeToDismissBoxValue.Settled -> false
            }
        },
        positionalThreshold = { totalDistance -> totalDistance * 0.8f } // Require 80% of the width to be swiped
    )
    
    // Handle the actual deletion when swipe is confirmed
    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
            // Add a small delay to ensure smooth animation
            kotlinx.coroutines.delay(100)
            onRemoveItem(item)
        }
    }
    
    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            // Red background when swiping with rounded corners
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.error,
                        shape = MaterialTheme.shapes.large
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onError,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) {
        ShoppingItemCard(
            item = item,
            onUpdateQuantity = onUpdateQuantity
        )
    }
} 