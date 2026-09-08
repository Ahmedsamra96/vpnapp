package com.example.model

import java.util.Locale

enum class VpnConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    DISCONNECTING
}

data class VpnServer(
    val id: String,
    val country: String,
    val countryAr: String,
    val countryCode: String,
    val city: String,
    val ip: String,
    val pingMs: Int,
    val speedMbps: Double,
    val protocol: String = "UDP",
    val port: Int = 1194,
    val ovpnConfigBase64: String = "",
    val sessionsCount: Int = 0,
    val score: Long = 0,
    val flagEmoji: String = "🌐",
    val isFavorite: Boolean = false,
    val isOptimal: Boolean = false
)

data class VpnTrafficStats(
    val downloadSpeedBps: Long = 0L,
    val uploadSpeedBps: Long = 0L,
    val totalBytesDown: Long = 0L,
    val totalBytesUp: Long = 0L,
    val durationSeconds: Long = 0L,
    val connectedSince: Long = 0L
) {
    fun formatDuration(): String {
        val hours = durationSeconds / 3600
        val minutes = (durationSeconds % 3600) / 60
        val seconds = durationSeconds % 60
        return if (hours > 0) {
            String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.US, "%02d:%02d", minutes, seconds)
        }
    }

    fun formatSpeed(bytesPerSec: Long): String {
        return when {
            bytesPerSec >= 1024 * 1024 -> String.format(Locale.US, "%.1f MB/s", bytesPerSec / (1024.0 * 1024.0))
            bytesPerSec >= 1024 -> String.format(Locale.US, "%.0f KB/s", bytesPerSec / 1024.0)
            else -> "$bytesPerSec B/s"
        }
    }

    fun formatTotalBytes(totalBytes: Long): String {
        return when {
            totalBytes >= 1024 * 1024 * 1024 -> String.format(Locale.US, "%.2f GB", totalBytes / (1024.0 * 1024.0 * 1024.0))
            totalBytes >= 1024 * 1024 -> String.format(Locale.US, "%.1f MB", totalBytes / (1024.0 * 1024.0))
            totalBytes >= 1024 -> String.format(Locale.US, "%.0f KB", totalBytes / 1024.0)
            else -> "$totalBytes B"
        }
    }
}

data class VpnLogEntry(
    val timestamp: String,
    val message: String,
    val level: LogLevel = LogLevel.INFO
)

enum class LogLevel {
    INFO,
    SUCCESS,
    WARNING,
    ERROR
}

data class VpnAppSettings(
    val killSwitchEnabled: Boolean = true,
    val dnsLeakProtection: Boolean = true,
    val preferredProtocol: String = "UDP",
    val autoConnectOnLaunch: Boolean = false,
    val customDns: String = "1.1.1.1"
)
