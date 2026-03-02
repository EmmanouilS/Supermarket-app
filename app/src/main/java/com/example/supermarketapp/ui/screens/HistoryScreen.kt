package com.example.supermarketapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.supermarketapp.data.PurchaseHistory
import com.example.supermarketapp.data.ShoppingList
import com.example.supermarketapp.ui.LocalizationManager
import com.example.supermarketapp.ui.rememberLocalizedStrings
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    purchaseHistory: List<PurchaseHistory> = emptyList(),
    onRepeatOrder: (ShoppingList) -> Unit = { },
    onOrderClick: (PurchaseHistory) -> Unit = { }
) {
    val strings = rememberLocalizedStrings()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.history) }
            )
        }
    ) { paddingValues ->
        if (purchaseHistory.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "📊",
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = strings.emptyHistory,
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
                items(purchaseHistory) { history ->
                    HistoryItemCard(
                        purchaseHistory = history,
                        onRepeatOrder = onRepeatOrder,
                        onOrderClick = onOrderClick
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryItemCard(
    purchaseHistory: PurchaseHistory,
    onRepeatOrder: (ShoppingList) -> Unit,
    onOrderClick: (PurchaseHistory) -> Unit
) {
    val strings = rememberLocalizedStrings()
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = { onOrderClick(purchaseHistory) }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = purchaseHistory.shoppingList.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = purchaseHistory.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Text(
                    text = "€${String.format("%.2f", purchaseHistory.totalSpent)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "${purchaseHistory.shoppingList.getItemCount()} ${strings.quantity.lowercase()}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { onRepeatOrder(purchaseHistory.shoppingList) }
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text(strings.repeatOrder)
                }
            }
        }
    }
} 