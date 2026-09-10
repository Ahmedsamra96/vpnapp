package com.example.model

enum class AppThemeMode(val code: String) {
    SYSTEM("system"),
    LIGHT("light"),
    DARK("dark");

    companion object {
        fun fromCode(code: String): AppThemeMode {
            return entries.find { it.code.equals(code, ignoreCase = true) } ?: SYSTEM
        }
    }
}
