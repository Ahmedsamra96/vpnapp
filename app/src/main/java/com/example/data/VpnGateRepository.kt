package com.example.data

import android.content.Context
import android.util.Log
import com.example.model.VpnServer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Dns
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

class VpnGateRepository(private val context: Context) {

    private val vpnGateFallbackIps = listOf(
        "130.158.75.42",
        "130.158.75.38",
        "130.158.75.39",
        "130.158.75.40",
        "130.158.75.44",
        "130.158.75.48"
    )

    private val resilientDns = object : Dns {
        override fun lookup(hostname: String): List<InetAddress> {
            try {
                val systemResult = Dns.SYSTEM.lookup(hostname)
                if (systemResult.isNotEmpty()) return systemResult
            } catch (_: Exception) {
                // System DNS failed (common in Android emulators or restricted DNS)
            }

            if (hostname.contains("vpngate", ignoreCase = true)) {
                val fallbackList = vpnGateFallbackIps.mapNotNull { ipStr ->
                    try {
                        InetAddress.getByAddress(hostname, InetAddress.getByName(ipStr).address)
                    } catch (_: Exception) {
                        null
                    }
                }
                if (fallbackList.isNotEmpty()) {
                    return fallbackList
                }
            }

            throw UnknownHostException("Unable to resolve host: $hostname")
        }
    }

    private val client = OkHttpClient.Builder()
        .dns(resilientDns)
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    private val prefs = context.getSharedPreferences("vpn_prefs", Context.MODE_PRIVATE)

    suspend fun fetchServers(): List<VpnServer> = withContext(Dispatchers.IO) {
        val defaultList = DefaultServers.getDefaultServers()
        val favorites = getFavoriteServerIds()

        // Try primary HTTPS URL then fallback HTTP URL
        val urlsToTry = listOf(
            "https://www.vpngate.net/api/iphone/",
            "http://www.vpngate.net/api/iphone/"
        )

        for (url in urlsToTry) {
            try {
                val request = Request.Builder()
                    .url(url)
                    .header("User-Agent", "OpenVPNFree-Android/1.0")
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val body = response.body?.string()
                        if (!body.isNullOrBlank() && body.contains("*vpn_servers")) {
                            // Cache valid response for offline use
                            prefs.edit().putString("cached_vpngate_csv", body).apply()
                            val parsedServers = parseVpnGateCsv(body)
                            if (parsedServers.isNotEmpty()) {
                                return@withContext assembleServersList(parsedServers, defaultList, favorites)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.i("VpnGateRepo", "Attempt fetching from $url note: ${e.message}")
            }
        }

        // If network fetch failed, check local persistent cache first
        val cachedCsv = prefs.getString("cached_vpngate_csv", null)
        if (!cachedCsv.isNullOrBlank()) {
            try {
                val cachedServers = parseVpnGateCsv(cachedCsv)
                if (cachedServers.isNotEmpty()) {
                    Log.i("VpnGateRepo", "Using ${cachedServers.size} cached servers from local storage")
                    return@withContext assembleServersList(cachedServers, defaultList, favorites)
                }
            } catch (_: Exception) {
                // Ignore cache parsing error
            }
        }

        // Fallback gracefully to high-performance bundled servers
        Log.i("VpnGateRepo", "Using bundled high-speed servers")
        defaultList.map { it.copy(isFavorite = favorites.contains(it.id)) }
    }

    private fun assembleServersList(
        liveServers: List<VpnServer>,
        defaultList: List<VpnServer>,
        favorites: Set<String>
    ): List<VpnServer> {
        val optimalServer = defaultList.first()
        val uniqueCountryServers = mutableListOf(optimalServer)
        val seenCountryCodes = mutableSetOf<String>()

        // 1) Add the best live server for each country from VPNGate
        for (server in liveServers) {
            val code = server.countryCode.uppercase()
            if (code != "AUTO" && seenCountryCodes.add(code)) {
                uniqueCountryServers.add(server)
            }
        }

        // 2) Fill in any missing default countries (e.g., CA, AU, GB) if not already present
        for (defaultServer in defaultList) {
            val code = defaultServer.countryCode.uppercase()
            if (code != "AUTO" && seenCountryCodes.add(code)) {
                uniqueCountryServers.add(defaultServer)
            }
        }

        return uniqueCountryServers.map { it.copy(isFavorite = favorites.contains(it.id)) }
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

                    if (ip.isNotBlank() && countryShort.isNotBlank() && ovpnBase64.isNotBlank()) {
                        val flag = DefaultServers.getCountryFlag(countryShort)
                        val countryAr = DefaultServers.getArabicCountryName(countryShort, countryLong)

                        servers.add(
                            VpnServer(
                                id = "vpngate_${countryShort.lowercase()}_${ip.replace('.', '_')}",
                                country = countryLong,
                                countryAr = countryAr,
                                countryCode = countryShort.uppercase(),
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

        // Distinct by country: pick the single BEST server (highest score and lowest ping) per country
        return servers
            .groupBy { it.countryCode }
            .mapNotNull { (_, countryServers) ->
                countryServers.maxByOrNull { it.score }
            }
            .sortedByDescending { it.score }
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
