package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.model.AppThemeMode

private val LightColorScheme = lightColorScheme(
    primary = VpnPrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = VpnPrimaryBlueSoft,
    onPrimaryContainer = VpnPrimaryBlueDark,
    secondary = VpnConnectedGreen,
    onSecondary = Color.White,
    secondaryContainer = VpnConnectedGreenSoft,
    onSecondaryContainer = Color(0xFF065F46),
    tertiary = VpnAmber,
    background = AppBgLight,
    onBackground = AppTextPrimary,
    surface = AppSurfaceLight,
    onSurface = AppTextPrimary,
    surfaceVariant = AppSurfaceVariantLight,
    onSurfaceVariant = AppTextSecondary,
    outline = AppBorderLight,
    error = VpnDisconnectedRed
)

private val DarkColorScheme = darkColorScheme(
    primary = VpnPrimaryBlueLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1E3A8A),
    onPrimaryContainer = Color(0xFFDBEAFE),
    secondary = VpnConnectedGreen,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF064E3B),
    onSecondaryContainer = Color(0xFFA7F3D0),
    tertiary = VpnAmber,
    background = AppBgDark,
    onBackground = AppTextPrimaryDark,
    surface = AppSurfaceDark,
    onSurface = AppTextPrimaryDark,
    surfaceVariant = AppSurfaceVariantDark,
    onSurfaceVariant = AppTextSecondaryDark,
    outline = AppBorderDark,
    error = VpnDisconnectedRed
)

@Composable
fun MyApplicationTheme(
    themeMode: AppThemeMode = AppThemeMode.DARK,
    systemInDark: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        AppThemeMode.SYSTEM -> systemInDark
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
