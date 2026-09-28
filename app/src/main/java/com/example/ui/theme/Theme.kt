package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val CampusDarkColorScheme = darkColorScheme(
  primary = CampusBlueLight,
  onPrimary = Color.White,
  primaryContainer = CampusBlueDark,
  onPrimaryContainer = Color(0xFFDBEAFE),
  secondary = CampusTealAccent,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFF134E4A),
  onSecondaryContainer = Color(0xFFCCFBF1),
  tertiary = CampusAmberHighlight,
  onTertiary = Color.Black,
  background = SurfaceDark,
  onBackground = TextPrimaryDark,
  surface = SurfaceDark,
  onSurface = TextPrimaryDark,
  surfaceVariant = SurfaceCardDark,
  onSurfaceVariant = TextSecondaryDark,
  outline = BorderSubtleDark
)

private val CampusLightColorScheme = lightColorScheme(
  primary = CampusBluePrimary,
  onPrimary = Color.White,
  primaryContainer = Color(0xFFDBEAFE),
  onPrimaryContainer = CampusBlueDark,
  secondary = CampusTealAccent,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFCCFBF1),
  onSecondaryContainer = Color(0xFF115E59),
  tertiary = CampusAmberHighlight,
  onTertiary = Color.Black,
  background = SurfaceLight,
  onBackground = TextPrimaryLight,
  surface = SurfaceLight,
  onSurface = TextPrimaryLight,
  surfaceVariant = SurfaceCardLight,
  onSurfaceVariant = TextSecondaryLight,
  outline = BorderSubtleLight
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Use our signature campus branding by default
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> CampusDarkColorScheme
    else -> CampusLightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
