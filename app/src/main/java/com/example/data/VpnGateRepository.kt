package com.example.data

import android.content.Context
import android.util.Log
import com.example.model.VpnServer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class VpnGateRepository(private val context: Context) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val prefs = context.getSharedPreferences("vpn_prefs", Context.MODE_PRIVATE)

    suspend fun fetchServers(): List<VpnServer> = withContext(Dispatchers.IO) {
        val defaultList = DefaultServers.getDefaultServers()
        val favorites = getFavoriteServerIds()

        try {
            // VPNGate public CSV API endpoint
            val request = Request.Builder()
                .url("https://www.vpngate.net/api/iphone/")
                .header("User-Agent", "OpenVPNFree-Android/1.0")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.w("VpnGateRepo", "Failed HTTP response: ${response.code}, using defaults")
                    return@withContext defaultList.map { it.copy(isFavorite = favorites.contains(it.id)) }
                }

                val body = response.body?.string()
                if (body.isNullOrBlank()) {
                    return@withContext defaultList.map { it.copy(isFavorite = favorites.contains(it.id)) }
                }

                val parsedServers = parseVpnGateCsv(body)
                if (parsedServers.isEmpty()) {
                    return@withContext defaultList.map { it.copy(isFavorite = favorites.contains(it.id)) }
                }

                // Include the Optimal Server at top
                val optimalServer = defaultList.first()
                val combined = mutableListOf(optimalServer)
                combined.addAll(parsedServers)

                combined.map { it.copy(isFavorite = favorites.contains(it.id)) }
            }
        } catch (e: Exception) {
            Log.e("VpnGateRepo", "Error fetching from VPNGate: ${e.message}, using bundled servers")
            defaultList.map { it.copy(isFavorite = favorites.contains(it.id)) }
        }
    }

    private fun parseVpnGateCsv(csvContent: String): List<VpnServer> {
        val servers = mutableListOf<VpnServer>()
        val lines = csvContent.lines()

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty() || trimmed.startsWith("*") || trimmed.startsWith("#")) {
                continue
            }

            val cols = trimmed.split(",")
            if (cols.size >= 15) {
                try {
                    val hostName = cols[0]
                    val ip = cols[1]
                    val score = cols[2].toLongOrNull() ?: 0L
                    val pingMs = cols[3].toIntOrNull() ?: 60
                    val speedBytes = cols[4].toLongOrNull() ?: 0L
                    val speedMbps = speedBytes / (1024.0 * 1024.0) * 8.0 // convert to Mbps
                    val countryLong = cols[5]
                    val countryShort = cols[6]
                    val numSessions = cols[7].toIntOrNull() ?: 0
                    val ovpnBase64 = cols[14]

                    if (ip.isNotBlank() && countryShort.isNotBlank()) {
                        val flag = DefaultServers.getCountryFlag(countryShort)
                        val countryAr = DefaultServers.getArabicCountryName(countryShort, countryLong)

                        servers.add(
                            VpnServer(
                                id = "vpngate_${ip.replace('.', '_')}",
                                country = countryLong,
                                countryAr = countryAr,
                                countryCode = countryShort,
                                city = hostName.take(15),
                                ip = ip,
                                pingMs = pingMs,
                                speedMbps = if (speedMbps > 0) speedMbps else 45.0,
                                protocol = "UDP",
                                port = 1194,
                                ovpnConfigBase64 = ovpnBase64,
                                sessionsCount = numSessions,
                                score = score,
                                flagEmoji = flag
                            )
                        )
                    }
                } catch (e: Exception) {
                    // Ignore line parsing error and continue
                }
            }
        }

        // Return sorted by score descending, max 30 servers to keep UI snappy
        return servers.sortedByDescending { it.score }.take(30)
    }

    fun toggleFavorite(serverId: String): Boolean {
        val favorites = getFavoriteServerIds().toMutableSet()
        val isFav = if (favorites.contains(serverId)) {
            favorites.remove(serverId)
            false
        } else {
            favorites.add(serverId)
            true
        }
        prefs.edit().putStringSet("favorites", favorites).apply()
        return isFav
    }

    private fun getFavoriteServerIds(): Set<String> {
        return prefs.getStringSet("favorites", emptySet()) ?: emptySet()
    }
}
