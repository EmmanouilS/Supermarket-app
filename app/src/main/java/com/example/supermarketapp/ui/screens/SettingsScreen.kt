package com.example.supermarketapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.supermarketapp.ui.Language
import com.example.supermarketapp.ui.LocalizationManager
import com.example.supermarketapp.ui.rememberLocalizedStrings
import com.example.supermarketapp.ui.ThemeManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onLanguageChange: (Language) -> Unit = { },
    onThemeChange: (Boolean) -> Unit = { }
) {
    val strings = rememberLocalizedStrings()
    val isDarkMode = ThemeManager.getCurrentTheme()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.settings) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Language Setting
            Card {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = strings.languageLabel,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = LocalizationManager.currentLanguage == Language.ENGLISH,
                            onClick = { 
                                LocalizationManager.setLanguage(Language.ENGLISH)
                                onLanguageChange(Language.ENGLISH)
                            },
                            label = { Text("English") }
                        )
                        
                        FilterChip(
                            selected = LocalizationManager.currentLanguage == Language.GREEK,
                            onClick = { 
                                LocalizationManager.setLanguage(Language.GREEK)
                                onLanguageChange(Language.GREEK)
                            },
                            label = { Text("Ελληνικά") }
                        )
                    }
                }
            }
            
            // Theme Setting
            Card {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = strings.darkMode,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = !isDarkMode,
                            onClick = { 
                                onThemeChange(false)
                            },
                            label = { Text("Light") }
                        )
                        
                        FilterChip(
                            selected = isDarkMode,
                            onClick = { 
                                onThemeChange(true)
                            },
                            label = { Text("Dark") }
                        )
                    }
                }
            }
            
            // About Section
            Card {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = strings.about,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = strings.appVersion,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Text(
                        text = strings.appDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Features List
            Card {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = strings.features,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    FeatureItem(strings.featureCatalog)
                    FeatureItem(strings.featureSearch)
                    FeatureItem(strings.featureShoppingList)
                    FeatureItem(strings.featureWishList)
                    FeatureItem(strings.featureHistory)
                    FeatureItem(strings.featureOffers)
                    FeatureItem(strings.featureMultiLanguage)
                    FeatureItem(strings.featureNutrition)
                }
            }
        }
    }
}

@Composable
private fun FeatureItem(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
} 