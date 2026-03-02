package com.example.supermarketapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.supermarketapp.ui.navigation.AppNavigation
import com.example.supermarketapp.ui.theme.SupermarketappTheme
import com.example.supermarketapp.data.seed.clearAndSeed
import com.example.supermarketapp.ui.ThemeManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen for better perceived performance
        installSplashScreen()
        
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge for better performance
        enableEdgeToEdge()
        
        setContent {
            var isDarkTheme by remember { mutableStateOf(ThemeManager.isDarkTheme) }
            
            SupermarketappTheme(
                darkTheme = isDarkTheme,
                dynamicColor = false // Disable dynamic colors to use our custom theme
            ) {
                // Seed database and then show UI
                val context = LocalContext.current
                val app = remember(context) { (context.applicationContext as MyApp) }
                var isSeedingComplete by remember { mutableStateOf(false) }
                
                LaunchedEffect(app) {
                    try {
                        clearAndSeed(context, app.db)
                        isSeedingComplete = true
                    } catch (e: Exception) {
                        // If seeding fails, still show the app but log the error
                        Log.e("MainActivity", "Failed to seed database", e)
                        isSeedingComplete = true
                    }
                }
                
                if (isSeedingComplete) {
                    AppNavigation(
                        isDarkTheme = isDarkTheme,
                        onThemeChange = { isDark ->
                            isDarkTheme = isDark
                        }
                    )
                } else {
                    // Show loading screen while seeding
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Loading products...")
                        }
                    }
                }
            }
        }
    }
    
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        // Clear caches when memory is low
        if (level >= android.content.ComponentCallbacks2.TRIM_MEMORY_MODERATE) {
            // Clear image caches and other memory-intensive resources
            System.gc()
        }
    }
}