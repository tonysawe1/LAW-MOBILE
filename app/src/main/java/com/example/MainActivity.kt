package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.LocalAppStrings
import com.example.ui.localization.getAppStrings
import com.example.ui.navigation.AppNavHost
import com.example.ui.theme.LawClientTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val systemInDark = isSystemInDarkTheme()
      var isDarkMode by remember { mutableStateOf(systemInDark) }
      var currentLanguage by remember { mutableStateOf(AppLanguage.ENGLISH) }

      CompositionLocalProvider(LocalAppStrings provides getAppStrings(currentLanguage)) {
        LawClientTheme(darkTheme = isDarkMode) {
          Surface(modifier = Modifier.fillMaxSize()) {
            val navController = rememberNavController()
            AppNavHost(
              navController = navController,
              currentLanguage = currentLanguage,
              isDarkMode = isDarkMode,
              onLanguageChange = { newLang -> currentLanguage = newLang },
              onThemeToggle = { darkMode -> isDarkMode = darkMode }
            )
          }
        }
      }
    }
  }
}
