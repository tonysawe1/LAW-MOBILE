package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
  primary = LawNavyPrimary,
  onPrimary = LawNavyOnPrimary,
  primaryContainer = LawNavyContainer,
  onPrimaryContainer = LawOnNavyContainer,
  secondary = LawGoldSecondary,
  onSecondary = LawGoldOnSecondary,
  secondaryContainer = LawGoldContainer,
  onSecondaryContainer = LawOnGoldContainer,
  tertiary = LawSlateTertiary,
  onTertiary = LawOnSlateTertiary,
  tertiaryContainer = LawSlateContainer,
  onTertiaryContainer = LawOnSlateContainer,
  background = LawBackground,
  onBackground = LawOnBackground,
  surface = LawSurface,
  onSurface = LawOnSurface,
  surfaceVariant = LawSurfaceVariant,
  onSurfaceVariant = LawOnSurfaceVariant,
  outline = LawOutline,
  outlineVariant = LawOutlineVariant
)

private val DarkColorScheme = darkColorScheme(
  primary = LawDarkNavyPrimary,
  onPrimary = LawDarkNavyOnPrimary,
  primaryContainer = LawDarkNavyContainer,
  onPrimaryContainer = LawDarkOnNavyContainer,
  secondary = LawDarkGoldSecondary,
  onSecondary = LawDarkGoldOnSecondary,
  secondaryContainer = LawDarkGoldContainer,
  onSecondaryContainer = LawDarkOnGoldContainer,
  tertiary = LawDarkSlateTertiary,
  onTertiary = LawDarkOnSlateTertiary,
  tertiaryContainer = LawDarkSlateContainer,
  onTertiaryContainer = LawDarkOnSlateContainer,
  background = LawDarkBackground,
  onBackground = LawDarkOnBackground,
  surface = LawDarkSurface,
  onSurface = LawDarkOnSurface,
  surfaceVariant = LawDarkSurfaceVariant,
  onSurfaceVariant = LawDarkOnSurfaceVariant,
  outline = LawDarkOutline,
  outlineVariant = LawDarkOutlineVariant
)

@Composable
fun LawClientTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep firm brand identity consistent
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}

// Keep backward compatibility
@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit
) {
  LawClientTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
