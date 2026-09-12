package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Modern Light & Clean Palette (Matches Design Reference)
val VpnPrimaryBlue = Color(0xFF2563EB)
val VpnPrimaryBlueDark = Color(0xFF1D4ED8)
val VpnPrimaryBlueLight = Color(0xFF3B82F6)
val VpnPrimaryBlueSoft = Color(0xFFEFF6FF)

val VpnConnectedGreen = Color(0xFF16A34A)
val VpnConnectedGreenSoft = Color(0xFFDCFCE7)

val VpnDisconnectedRed = Color(0xFFEF4444)
val VpnAmber = Color(0xFFF59E0B)

// Background & Surface
val AppBgLight = Color(0xFFF8FAFC)
val AppSurfaceLight = Color(0xFFFFFFFF)
val AppSurfaceVariantLight = Color(0xFFF1F5F9)
val AppBorderLight = Color(0xFFE2E8F0)
val AppBorderSubtle = Color(0xFFF1F5F9)

// Text Colors (Light)
val AppTextPrimary = Color(0xFF0F172A)
val AppTextSecondary = Color(0xFF64748B)
val AppTextMuted = Color(0xFF94A3B8)

// Dark Theme Variants
val AppBgDark = Color(0xFF0B1120)
val AppSurfaceDark = Color(0xFF131D33)
val AppSurfaceVariantDark = Color(0xFF1E293B)
val AppBorderDark = Color(0xFF22324E)
val AppTextPrimaryDark = Color(0xFFF8FAFC)
val AppTextSecondaryDark = Color(0xFF94A3B8)
val AppTextMutedDark = Color(0xFF64748B)

// Helpers to get adaptive colors within Composables
object ThemeColors {
    val surface: Color
        @Composable get() = androidx.compose.material3.MaterialTheme.colorScheme.surface

    val surfaceVariant: Color
        @Composable get() = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant

    val background: Color
        @Composable get() = androidx.compose.material3.MaterialTheme.colorScheme.background

    val textPrimary: Color
        @Composable get() = androidx.compose.material3.MaterialTheme.colorScheme.onSurface

    val textSecondary: Color
        @Composable get() = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant

    val outline: Color
        @Composable get() = androidx.compose.material3.MaterialTheme.colorScheme.outline

    val isDark: Boolean
        @Composable get() = androidx.compose.material3.MaterialTheme.colorScheme.background == AppBgDark

    val primarySoft: Color
        @Composable get() = if (isDark) Color(0xFF1E293B) else VpnPrimaryBlueSoft

    val primarySoftBorder: Color
        @Composable get() = if (isDark) Color(0xFF1E3A8A) else Color(0xFFBFDBFE)

    val cardBg: Color
        @Composable get() = if (isDark) AppSurfaceDark else Color(0xFFFFFFFF)

    val innerCardBg: Color
        @Composable get() = if (isDark) AppSurfaceVariantDark else Color(0xFFF8FAFC)
}

// Backward-compatible aliases for existing components
val CyberCyan = VpnPrimaryBlue
val CyberCyanGlow = Color(0x332563EB)
val CyberEmerald = VpnConnectedGreen
val CyberEmeraldGlow = Color(0x3316A34A)
val CyberAmber = VpnAmber
val CyberAmberGlow = Color(0x33F59E0B)
val CyberRed = VpnDisconnectedRed
val CyberBackground = AppBgLight
val CyberSurface = AppSurfaceLight
val CyberSurfaceVariant = AppSurfaceVariantLight
val CyberBorder = AppBorderLight
val TextPrimary = AppTextPrimary
val TextSecondary = AppTextSecondary
val TextMuted = AppTextMuted
val CardGradientStart = AppSurfaceLight
val CardGradientEnd = AppSurfaceLight
val ConnectedGradientStart = AppSurfaceLight
val ConnectedGradientEnd = AppSurfaceLight
