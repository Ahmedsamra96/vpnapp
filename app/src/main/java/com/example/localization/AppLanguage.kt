package com.example.localization

import android.content.res.Resources
import androidx.compose.ui.unit.LayoutDirection
import java.util.Locale

enum class AppLanguage(
    val code: String,
    val nativeName: String,
    val englishName: String,
    val flag: String,
    val layoutDirection: LayoutDirection
) {
    SYSTEM("system", "افتراضي النظام (System)", "System Default", "🌐", LayoutDirection.Ltr),
    ARABIC("ar", "العربية", "Arabic", "🇸🇦", LayoutDirection.Rtl),
    ENGLISH("en", "English", "English", "🇺🇸", LayoutDirection.Ltr),
    FRENCH("fr", "Français", "French", "🇫🇷", LayoutDirection.Ltr),
    SPANISH("es", "Español", "Spanish", "🇪🇸", LayoutDirection.Ltr),
    PERSIAN("fa", "فارسی", "Persian", "🇮🇷", LayoutDirection.Rtl),
    RUSSIAN("ru", "Русский", "Russian", "🇷🇺", LayoutDirection.Ltr),
    GERMAN("de", "Deutsch", "German", "🇩🇪", LayoutDirection.Ltr);

    companion object {
        fun fromCode(code: String): AppLanguage {
            return entries.find { it.code.equals(code, ignoreCase = true) } ?: SYSTEM
        }

        /**
         * Resolves the effective active language.
         * If the user selected SYSTEM, it inspects the device/store language:
         * Persian -> PERSIAN
         * Russian -> RUSSIAN
         * German -> GERMAN
         * French -> FRENCH
         * Spanish -> SPANISH
         * Arabic -> ARABIC
         * Default -> ENGLISH
         */
        fun resolveEffectiveLanguage(preference: AppLanguage): AppLanguage {
            if (preference != SYSTEM) return preference

            val systemLanguage = try {
                val locale = Resources.getSystem().configuration.locales.get(0) ?: Locale.getDefault()
                locale.language.lowercase()
            } catch (_: Exception) {
                Locale.getDefault().language.lowercase()
            }

            return when {
                systemLanguage.startsWith("ar") -> ARABIC
                systemLanguage.startsWith("fa") || systemLanguage.startsWith("per") -> PERSIAN
                systemLanguage.startsWith("ru") -> RUSSIAN
                systemLanguage.startsWith("de") -> GERMAN
                systemLanguage.startsWith("fr") -> FRENCH
                systemLanguage.startsWith("es") -> SPANISH
                systemLanguage.startsWith("en") -> ENGLISH
                else -> ENGLISH
            }
        }
    }
}
