package com.example.supermarketapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ThemeState {
    var isDarkTheme by mutableStateOf(false)
        private set
    
    fun setTheme(isDark: Boolean) {
        isDarkTheme = isDark
    }
}

object ThemeManager {
    private val themeState = ThemeState()
    var isDarkTheme: Boolean
        get() = themeState.isDarkTheme
        set(value) = themeState.setTheme(value)
    
    fun setTheme(isDark: Boolean) {
        println("ThemeManager: Setting theme to ${if (isDark) "DARK" else "LIGHT"}")
        themeState.setTheme(isDark)
    }
    
    @Composable
    fun getCurrentTheme(): Boolean {
        return isDarkTheme
    }
}
