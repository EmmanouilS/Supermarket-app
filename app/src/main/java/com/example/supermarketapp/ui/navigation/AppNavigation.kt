package com.example.supermarketapp.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

import com.example.supermarketapp.data.Product
import com.example.supermarketapp.data.mappers.toUiProduct
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.supermarketapp.data.ShoppingItem
import com.example.supermarketapp.data.WishListItem
import com.example.supermarketapp.data.PurchaseHistory
import com.example.supermarketapp.ui.LocalizationManager
import com.example.supermarketapp.ui.rememberLocalizedStrings
import com.example.supermarketapp.ui.ThemeManager
import com.example.supermarketapp.ui.screens.*
import kotlinx.coroutines.launch

sealed class Screen(val route: String, val icon: ImageVector) {
    object Catalog : Screen("catalogue", Icons.Default.Home)
    object ShoppingList : Screen("shopping_list", Icons.Default.ShoppingCart)
    object WishList : Screen("wish_list", Icons.Default.Favorite)
    object History : Screen("history", Icons.AutoMirrored.Filled.List)
    object Settings : Screen("settings", Icons.Default.Settings)
    object ProductDetail : Screen("product_detail/{productId}", Icons.Default.Info)
    object OrderDetail : Screen("order_detail/{orderId}", Icons.Default.Info)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    isDarkTheme: Boolean = false,
    onThemeChange: (Boolean) -> Unit = {}
) {
    val navController = rememberNavController()
    val strings = rememberLocalizedStrings()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    
    // State management - Memoized to prevent unnecessary recompositions
    var shoppingItems by remember { mutableStateOf<List<ShoppingItem>>(emptyList()) }
    var wishListItems by remember { mutableStateOf<List<WishListItem>>(emptyList()) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var purchaseHistory by remember { mutableStateOf<List<PurchaseHistory>>(emptyList()) }
    
    // Catalog filter/search state - Memoized to prevent unnecessary recompositions
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<com.example.supermarketapp.data.ProductCategory?>(null) }
    var showOffersOnly by remember { mutableStateOf(false) }
    var catalogResetKey by remember { mutableStateOf(0) }
    var orderCompleted by remember { mutableStateOf(false) }
    var selectedOrder by remember { mutableStateOf<PurchaseHistory?>(null) }
    var showStockExceededNotification by remember { mutableStateOf(false) }
    
    // Memoize screens list to prevent recreation
    val screens = remember {
        listOf(
            Screen.Catalog,
            Screen.ShoppingList,
            Screen.WishList,
            Screen.History,
            Screen.Settings
        )
    }
    
    // Memoize shopping items map for O(1) lookup
    val shoppingItemsMap = remember(shoppingItems) {
        shoppingItems.associate { it.product.id to it.quantity }
    }
    
    // Memoize wish list products set for O(1) lookup
    val wishListProductsSet = remember(wishListItems) {
        wishListItems.map { it.product.id }.toSet()
    }
    
    // Show stock exceeded notification
    LaunchedEffect(showStockExceededNotification) {
        if (showStockExceededNotification) {
            snackbarHostState.showSnackbar(
                message = "Cannot add more items - stock limit exceeded!",
                duration = SnackbarDuration.Short
            )
            showStockExceededNotification = false
        }
    }
    
    // Calculate total cost for shopping list badge
    val totalCost = remember(shoppingItems) {
        shoppingItems.sumOf { it.product.price * it.quantity }
    }
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                screens.forEach { screen ->
                    val title = when (screen) {
                        Screen.Catalog -> strings.catalog
                        Screen.ShoppingList -> strings.shoppingList
                        Screen.WishList -> strings.wishList
                        Screen.History -> strings.history
                        Screen.Settings -> strings.settings
                        else -> ""
                    }
                    NavigationBarItem(
                        icon = { 
                            if (screen is Screen.ShoppingList && totalCost > 0) {
                                BadgedBox(
                                    badge = {
                                        Badge {
                                            Text(
                                                text = "€${String.format("%.2f", totalCost)}",
                                                fontSize = 8.sp
                                            )
                                        }
                                    }
                                ) {
                                    Icon(screen.icon, contentDescription = title)
                                }
                            } else {
                                Icon(screen.icon, contentDescription = title)
                            }
                        },
                        label = { Text(title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            if (screen is Screen.Catalog) {
                                searchQuery = ""
                                selectedCategory = null
                                showOffersOnly = false
                                catalogResetKey++ // increment to force recomposition
                                navController.popBackStack(Screen.Catalog.route, inclusive = false)
                                if (navController.currentDestination?.route != Screen.Catalog.route) {
                                    navController.navigate(Screen.Catalog.route) {
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            } else {
                                // Reset order completed state when navigating to shopping list
                                if (screen is Screen.ShoppingList) {
                                    orderCompleted = false
                                }
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Catalog.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Catalog.route) {
                // Use key() to force recomposition when catalogResetKey changes
                key(catalogResetKey) {
                    CatalogScreen(
                        onProductClick = { product ->
                            selectedProduct = product
                            navController.navigate("product_detail/${product.id}")
                        },
                        onAddToCart = { product ->
                            val existingItem = shoppingItems.find { it.product.id == product.id }
                            val currentQuantity = existingItem?.quantity ?: 0
                            
                            // Check if adding one more item would exceed stock
                            if (currentQuantity < product.stockQuantity) {
                                if (existingItem != null) {
                                    shoppingItems = shoppingItems.map { item ->
                                        if (item.product.id == product.id) {
                                            item.copy(quantity = item.quantity + 1)
                                        } else item
                                    }
                                } else {
                                    shoppingItems = shoppingItems + ShoppingItem(product, 1)
                                }
                            } else {
                                // Show notification when stock limit is exceeded
                                showStockExceededNotification = true
                            }
                        },
                        onRemoveFromCart = { product ->
                            val existingItem = shoppingItems.find { it.product.id == product.id }
                            if (existingItem != null) {
                                if (existingItem.quantity > 1) {
                                    shoppingItems = shoppingItems.map { item ->
                                        if (item.product.id == product.id) {
                                            item.copy(quantity = item.quantity - 1)
                                        } else item
                                    }
                                } else {
                                    shoppingItems = shoppingItems.filter { it.product.id != product.id }
                                }
                            }
                        },
                        onAddToWishList = { product ->
                            val isInWishList = wishListItems.any { it.product.id == product.id }
                            if (!isInWishList) {
                                wishListItems = wishListItems + WishListItem(product)
                            }
                        },
                        wishListProducts = wishListProductsSet,
                        showSnackbar = { message -> coroutineScope.launch { snackbarHostState.showSnackbar(message, duration = SnackbarDuration.Short) } },
                        shoppingItems = shoppingItems,
                        searchQuery = searchQuery,
                        onSearchQueryChange = { searchQuery = it },
                        selectedCategory = selectedCategory,
                        onSelectedCategoryChange = { selectedCategory = it },
                        showOffersOnly = showOffersOnly,
                        onShowOffersOnlyChange = { showOffersOnly = it },
                        resetKey = catalogResetKey
                    )
                }
            }
            composable(Screen.ShoppingList.route) {
                ShoppingListScreen(
                    shoppingItems = shoppingItems,
                    onUpdateQuantity = { item, newQuantity ->
                        shoppingItems = shoppingItems.map { 
                            if (it.product.id == item.product.id) item.copy(quantity = newQuantity) else it 
                        }
                    },
                    onRemoveItem = { item ->
                        shoppingItems = shoppingItems.filter { it.product.id != item.product.id }
                    },
                    onOrderConfirmed = {
                        println("=== ORDER CONFIRMATION TRIGGERED ===")
                        println("Shopping items count: ${shoppingItems.size}")
                        if (shoppingItems.isNotEmpty()) {
                            // Capture shopping items before they're cleared
                            val itemsToProcess = shoppingItems.toList()
                            println("Captured ${itemsToProcess.size} items for stock update")
                            
                            // Update stock quantities in database
                            coroutineScope.launch {
                                try {
                                    val db = (context.applicationContext as com.example.supermarketapp.MyApp).db
                                    
                                    println("=== STOCK UPDATE DEBUG ===")
                                    println("Processing ${itemsToProcess.size} items")
                                    
                                    // Update stock for each item in the order
                                    for (shoppingItem in itemsToProcess) {
                                        println("Processing: ${shoppingItem.product.name} (ID: ${shoppingItem.product.id})")
                                        
                                        val currentProduct = db.productDao().byId(shoppingItem.product.id.toLong())
                                        if (currentProduct != null) {
                                            val oldStock = currentProduct.stockQuantity
                                            val newStock = maxOf(0, currentProduct.stockQuantity - shoppingItem.quantity)
                                            val isAvailable = newStock > 0
                                            
                                            println("Before update - Product: ${shoppingItem.product.name}, Old Stock: $oldStock, New Stock: $newStock, Available: $isAvailable")
                                            
                                            // Update the stock
                                            val updateResult = db.productDao().updateStockAndAvailability(shoppingItem.product.id.toLong(), newStock, isAvailable)
                                            println("Update result: $updateResult")
                                            
                                            // Wait a moment and verify the update worked by reading back from database
                                            kotlinx.coroutines.delay(100)
                                            val updatedProduct = db.productDao().byId(shoppingItem.product.id.toLong())
                                            println("After update - Product: ${shoppingItem.product.name}, DB Stock: ${updatedProduct?.stockQuantity}, Available: ${updatedProduct?.inStock}")
                                        } else {
                                            println("ERROR: Product not found in database: ${shoppingItem.product.name} (ID: ${shoppingItem.product.id})")
                                        }
                                    }
                                    println("=== END STOCK UPDATE DEBUG ===")
                                } catch (e: Exception) {
                                    println("Error updating stock: ${e.message}")
                                    e.printStackTrace()
                                }
                            }
                            
                            // Add to history
                            val newList = com.example.supermarketapp.data.ShoppingList(
                                id = System.currentTimeMillis().toString(),
                                name = "Order ${purchaseHistory.size + 1}",
                                items = itemsToProcess
                            )
                            val newHistory = com.example.supermarketapp.data.PurchaseHistory(
                                id = newList.id,
                                shoppingList = newList,
                                totalSpent = newList.totalCost,
                                date = java.time.LocalDateTime.now()
                            )
                            purchaseHistory = listOf(newHistory) + purchaseHistory
                        }
                        shoppingItems = emptyList()
                        orderCompleted = true
                        // Force catalog refresh to show updated stock values
                        catalogResetKey++
                    },
                    orderCompleted = orderCompleted,
                    onOrderCompletedReset = {
                        orderCompleted = false
                    },
                    onNavigateToCatalog = {
                        // Reset catalog state and navigate to catalog
                        searchQuery = ""
                        selectedCategory = null
                        showOffersOnly = false
                        catalogResetKey++
                        navController.navigate(Screen.Catalog.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable(Screen.WishList.route) {
                WishListScreen(
                    wishListItems = wishListItems,
                    shoppingItems = shoppingItems,
                    onAddToCart = { wishListItem ->
                        val existingItem = shoppingItems.find { it.product.id == wishListItem.product.id }
                        val currentQuantity = existingItem?.quantity ?: 0
                        
                        // Check if adding one more item would exceed stock
                        if (currentQuantity < wishListItem.product.stockQuantity) {
                            if (existingItem != null) {
                                shoppingItems = shoppingItems.map { item ->
                                    if (item.product.id == wishListItem.product.id) {
                                        item.copy(quantity = item.quantity + 1)
                                    } else item
                                }
                            } else {
                                shoppingItems = shoppingItems + ShoppingItem(wishListItem.product, 1)
                            }
                        } else {
                            // Show notification when stock limit is exceeded
                            showStockExceededNotification = true
                        }
                    },
                    onRemoveFromWishList = { wishListItem ->
                        wishListItems = wishListItems.filter { it != wishListItem }
                    },
                    onProductClick = { wishListItem ->
                        selectedProduct = wishListItem.product
                        navController.navigate("product_detail/${wishListItem.product.id}")
                    }
                )
            }
            composable(Screen.History.route) {
                HistoryScreen(
                    purchaseHistory = purchaseHistory,
                    onRepeatOrder = { shoppingList ->
                        // Add all items from the order to shopping list
                        shoppingItems = shoppingList.items
                        // Navigate to shopping list tab
                        navController.navigate(Screen.ShoppingList.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onOrderClick = { order ->
                        selectedOrder = order
                        navController.navigate("order_detail/${order.id}")
                    }
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onLanguageChange = { language ->
                        // Force recomposition by updating a dummy state
                        // The LocalizationManager will handle the actual language change
                        LocalizationManager.setLanguage(language)
                    },
                    onThemeChange = { isDark ->
                        ThemeManager.setTheme(isDark)
                        onThemeChange(isDark)
                    }
                )
            }
            composable("product_detail/{productId}") { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId")
                var product by remember { mutableStateOf<Product?>(null) }
                val context = androidx.compose.ui.platform.LocalContext.current
                val app = remember(context) { (context.applicationContext as com.example.supermarketapp.MyApp) }
                LaunchedEffect(productId, app) {
                    if (productId != null) {
                        product = app.db.productDao().byId(productId.toLong())?.toUiProduct()
                    }
                }
                product?.let {
                    ProductDetailScreen(
                        product = it,
                        isInWishList = wishListItems.any { wishItem -> wishItem.product.id == it.id },
                        onAddToCart = { quantity ->
                            val existingItem = shoppingItems.find { item -> item.product.id == it.id }
                            val currentQuantity = existingItem?.quantity ?: 0
                            val totalQuantity = currentQuantity + quantity
                            
                            // Check if adding the requested quantity would exceed stock
                            if (totalQuantity <= it.stockQuantity) {
                                if (existingItem != null) {
                                    shoppingItems = shoppingItems.map { item ->
                                        if (item.product.id == it.id) {
                                            item.copy(quantity = item.quantity + quantity)
                                        } else item
                                    }
                                } else {
                                    shoppingItems = shoppingItems + ShoppingItem(it, quantity)
                                }
                            } else {
                                // Show notification when stock limit is exceeded
                                showStockExceededNotification = true
                            }
                        },
                        onAddToWishList = {
                            val isInWishList = wishListItems.any { wishItem -> wishItem.product.id == it.id }
                            if (isInWishList) {
                                wishListItems = wishListItems.filter { wishItem -> wishItem.product.id != it.id }
                            } else {
                                wishListItems = wishListItems + WishListItem(it)
                            }
                        },
                        onBackClick = {
                            navController.popBackStack()
                        }
                    )
                }
            }
            composable("order_detail/{orderId}") { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId")
                val order = selectedOrder ?: purchaseHistory.find { it.id == orderId }
                order?.let {
                    OrderDetailScreen(
                        purchaseHistory = it,
                        onBackClick = {
                            navController.popBackStack()
                        },
                        onRepeatOrder = { shoppingList ->
                            // Add all items from the order to shopping list
                            shoppingItems = shoppingList.items
                            // Navigate to shopping list tab
                            navController.navigate(Screen.ShoppingList.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    }
} 