package com.example.vpn

import android.content.Context
import android.net.TrafficStats
import android.util.Base64
import android.util.Log
import com.example.data.DefaultServers
import com.example.model.LogLevel
import com.example.model.VpnConnectionState
import com.example.model.VpnTrafficStats
import com.tim.basevpn.vpn.api.VpnClientApi
import com.tim.basevpn.vpn.api.VpnClientApiFactory
import com.tim.basevpn.vpn.api.VpnClientApiHandle
import com.tim.basevpn.vpn.api.VpnConfig
import com.tim.basevpn.vpn.api.VpnState
import com.tim.openvpn.OpenVpnProtocol
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull

object OpenVpnAndroidService {

    private const val TAG = "OpenVpnService"
    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private val mutex = Mutex()
    private var vpnHandle: VpnClientApiHandle? = null
    private var vpnClient: VpnClientApi? = null
    private var stateObserverJob: Job? = null
    private var statsJob: Job? = null
    private var connectionTimeoutJob: Job? = null
    private var connectionStartTime: Long = 0L

    @Volatile
    private var currentEngineStatus: VpnState.Status = VpnState.Status.DISCONNECTED
    @Volatile
    private var activeServerId: String? = null
    @Volatile
    private var isConnectingInProgress: Boolean = false

    fun init(context: Context) {
        getOrCreateClient(context)
    }

    @Synchronized
    private fun getOrCreateClient(context: Context): VpnClientApi {
        vpnClient?.let { return it }
        val handle = VpnClientApiFactory.createAppScoped(context.applicationContext, OpenVpnProtocol.descriptor)
        vpnHandle = handle
        val client = handle.api
        vpnClient = client

        // Observe VPN State events from the OpenVPN native engine
        stateObserverJob?.cancel()
        stateObserverJob = scope.launch {
            client.observeState().collect { state ->
                currentEngineStatus = state.status
                Log.d(TAG, "VPN Engine State: ${state.status}, blocked=${state.startBlockedReason}")
                when (state.status) {
                    VpnState.Status.CONNECTED -> {
                        connectionTimeoutJob?.cancel()
                        connectionTimeoutJob = null
                        isConnectingInProgress = false
                        connectionStartTime = System.currentTimeMillis()
                        VpnStateRepository.setConnectionState(VpnConnectionState.CONNECTED)
                        val server = VpnStateRepository.selectedServer.value
                        activeServerId = server.id
                        VpnStateRepository.addLog("[OpenVPN 3] Connected successfully! Encrypted tunnel active via ${server.country} (${server.ip})", LogLevel.SUCCESS)
                        startTrafficMonitoring()
                    }
                    VpnState.Status.CONNECTING, VpnState.Status.CONNECTING_IPC -> {
                        VpnStateRepository.setConnectionState(VpnConnectionState.CONNECTING)
                    }
                    VpnState.Status.DISCONNECTING -> {
                        isConnectingInProgress = false
                        VpnStateRepository.setConnectionState(VpnConnectionState.DISCONNECTING)
                        stopTrafficMonitoring()
                    }
                    VpnState.Status.DISCONNECTED -> {
                        if (connectionTimeoutJob == null || !connectionTimeoutJob!!.isActive) {
                            isConnectingInProgress = false
                            activeServerId = null
                            VpnStateRepository.setConnectionState(VpnConnectionState.DISCONNECTED)
                            stopTrafficMonitoring()
                        }
                    }
                }
            }
        }
        return client
    }

    fun startVpn(context: Context) {
        val selectedServer = VpnStateRepository.selectedServer.value
        connectInternal(context, selectedServer, isFailover = false)
    }

    private fun connectInternal(context: Context, targetServer: com.example.model.VpnServer, isFailover: Boolean) {
        val client = getOrCreateClient(context)

        scope.launch {
            mutex.withLock {
                val isEngineBusy = currentEngineStatus == VpnState.Status.CONNECTING ||
                                  currentEngineStatus == VpnState.Status.CONNECTING_IPC ||
                                  currentEngineStatus == VpnState.Status.CONNECTED

                if (!isFailover && (isConnectingInProgress || (isEngineBusy && activeServerId == targetServer.id))) {
                    Log.d(TAG, "VPN connection already in progress or connected for server: ${targetServer.id}")
                    VpnStateRepository.addLog("[OpenVPN] Connection already in progress...", LogLevel.INFO)
                    return@withLock
                }

                if (isEngineBusy) {
                    VpnStateRepository.addLog("[OpenVPN] Initializing tunnel for ${targetServer.country}...", LogLevel.INFO)
                    try {
                        client.disconnect()
                        withTimeoutOrNull(2000) {
                            while (currentEngineStatus != VpnState.Status.DISCONNECTED) {
                                delay(100)
                            }
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Notice during prior disconnect: ${e.message}")
                    }
                }

                isConnectingInProgress = true
                activeServerId = targetServer.id
                VpnStateRepository.setConnectionState(VpnConnectionState.CONNECTING)

                try {
                    val rawConfig = getResolvedOvpnConfig(targetServer)
                    if (rawConfig.isBlank()) {
                        isConnectingInProgress = false
                        VpnStateRepository.addLog("[OpenVPN] Failed: Unable to find valid configuration for this server", LogLevel.ERROR)
                        VpnStateRepository.setConnectionState(VpnConnectionState.DISCONNECTED)
                        return@withLock
                    }

                    val tunedConfig = sanitizeAndTuneOvpnConfig(rawConfig)

                    if (isFailover) {
                        VpnStateRepository.addLog("[OpenVPN 3] Connecting to fallback server: ${targetServer.country} (${targetServer.ip})...", LogLevel.INFO)
                    } else {
                        VpnStateRepository.addLog("[OpenVPN 3] Starting connection to: ${targetServer.country} (${targetServer.ip})...", LogLevel.INFO)
                    }
                    VpnStateRepository.addLog("[OpenVPN 3] TLS handshake and cipher verification in progress...", LogLevel.INFO)

                    // Pass tuned OpenVPN config to native OpenVPN 3 core
                    client.connect(VpnConfig(tunedConfig))

                    // Start connection timeout watchdog (12 seconds)
                    startTimeoutWatchdog(context, targetServer)

                } catch (e: Exception) {
                    val msg = e.message.orEmpty()
                    if (msg.contains("already connecting or connected", ignoreCase = true) || msg.contains("409")) {
                        Log.w(TAG, "Engine reported already connecting: $msg")
                        VpnStateRepository.addLog("[OpenVPN] Data tunnel is already initializing...", LogLevel.INFO)
                    } else {
                        isConnectingInProgress = false
                        Log.e(TAG, "Error connecting to VPN: ${e.message}", e)
                        VpnStateRepository.addLog("[OpenVPN] Error connecting: ${e.message}", LogLevel.ERROR)
                        VpnStateRepository.setConnectionState(VpnConnectionState.DISCONNECTED)
                    }
                }
            }
        }
    }

    private fun startTimeoutWatchdog(context: Context, attemptedServer: com.example.model.VpnServer) {
        connectionTimeoutJob?.cancel()
        connectionTimeoutJob = scope.launch {
            delay(12000) // 12 seconds timeout threshold
            if (isConnectingInProgress && currentEngineStatus != VpnState.Status.CONNECTED) {
                Log.w(TAG, "Server connection timeout (12s) reached for server: ${attemptedServer.id} (${attemptedServer.ip})")
                VpnStateRepository.addLog("[OpenVPN] Server (${attemptedServer.country}) timed out after 12 seconds...", LogLevel.WARNING)

                val fallbackServer = getFallbackServer(attemptedServer.id)
                if (fallbackServer != null && fallbackServer.id != attemptedServer.id) {
                    VpnStateRepository.addLog("[OpenVPN] Auto-switching to high-speed alternative (${fallbackServer.country})...", LogLevel.INFO)
                    VpnStateRepository.setSelectedServer(fallbackServer)
                    try {
                        vpnClient?.disconnect()
                    } catch (_: Exception) {}
                    delay(500)
                    connectInternal(context, fallbackServer, isFailover = true)
                } else {
                    VpnStateRepository.addLog("[OpenVPN] Could not connect. Please choose another server or tap to retry.", LogLevel.ERROR)
                    try {
                        vpnClient?.disconnect()
                    } catch (_: Exception) {}
                    isConnectingInProgress = false
                    activeServerId = null
                    VpnStateRepository.setConnectionState(VpnConnectionState.DISCONNECTED)
                }
            }
        }
    }

    private fun getFallbackServer(currentId: String?): com.example.model.VpnServer? {
        val servers = DefaultServers.getDefaultServers()
        return servers.firstOrNull { it.id != currentId && it.ovpnConfigBase64.isNotBlank() }
    }

    private fun sanitizeAndTuneOvpnConfig(rawConfig: String): String {
        val lines = rawConfig.replace("\r\n", "\n").lines()
        val cleanedLines = mutableListOf<String>()
        var hasConnectTimeout = false
        var hasConnectRetryMax = false
        var hasResolvRetry = false

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.startsWith("resolv-retry", ignoreCase = true)) {
                cleanedLines.add("resolv-retry 3")
                hasResolvRetry = true
            } else if (trimmed.startsWith("connect-timeout", ignoreCase = true)) {
                cleanedLines.add("connect-timeout 8")
                hasConnectTimeout = true
            } else if (trimmed.startsWith("connect-retry-max", ignoreCase = true)) {
                cleanedLines.add("connect-retry-max 2")
                hasConnectRetryMax = true
            } else {
                cleanedLines.add(line)
            }
        }

        if (!hasResolvRetry) cleanedLines.add(0, "resolv-retry 3")
        if (!hasConnectTimeout) cleanedLines.add(0, "connect-timeout 8")
        if (!hasConnectRetryMax) cleanedLines.add(0, "connect-retry-max 2")
        cleanedLines.add(0, "hand-window 10")
        cleanedLines.add(0, "ping 5")
        cleanedLines.add(0, "ping-restart 15")

        return cleanedLines.joinToString("\n")
    }

    fun stopVpn(context: Context) {
        connectionTimeoutJob?.cancel()
        connectionTimeoutJob = null
        isConnectingInProgress = false
        activeServerId = null

        val client = getOrCreateClient(context)
        scope.launch {
            mutex.withLock {
                VpnStateRepository.setConnectionState(VpnConnectionState.DISCONNECTING)
                VpnStateRepository.addLog("[OpenVPN] Disconnecting tunnel safely...", LogLevel.INFO)

                try {
                    client.disconnect()
                    withTimeoutOrNull(2000) {
                        while (currentEngineStatus != VpnState.Status.DISCONNECTED) {
                            delay(100)
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error disconnecting VPN: ${e.message}", e)
                } finally {
                    isConnectingInProgress = false
                    activeServerId = null
                    VpnStateRepository.setConnectionState(VpnConnectionState.DISCONNECTED)
                    VpnStateRepository.addLog("[OpenVPN] Tunnel disconnected.", LogLevel.INFO)
                    stopTrafficMonitoring()
                }
            }
        }
    }

    private fun getResolvedOvpnConfig(server: com.example.model.VpnServer): String {
        if (server.ovpnConfigBase64.isNotBlank()) {
            try {
                val decoded = Base64.decode(server.ovpnConfigBase64, Base64.DEFAULT)
                val config = String(decoded, Charsets.UTF_8).replace("\r\n", "\n").trim()
                if (config.isNotBlank()) return config
            } catch (e: Exception) {
                Log.e(TAG, "Failed to decode base64 ovpn config: ${e.message}")
            }
        }
        // Fallback to any default server with config
        for (defaultServer in DefaultServers.getDefaultServers()) {
            if (defaultServer.ovpnConfigBase64.isNotBlank()) {
                try {
                    val decoded = Base64.decode(defaultServer.ovpnConfigBase64, Base64.DEFAULT)
                    val config = String(decoded, Charsets.UTF_8).replace("\r\n", "\n").trim()
                    if (config.isNotBlank()) return config
                } catch (_: Exception) {}
            }
        }
        return ""
    }

    private fun startTrafficMonitoring() {
        statsJob?.cancel()
        var initialRx = TrafficStats.getTotalRxBytes()
        var initialTx = TrafficStats.getTotalTxBytes()
        if (initialRx == TrafficStats.UNSUPPORTED.toLong()) initialRx = 0L
        if (initialTx == TrafficStats.UNSUPPORTED.toLong()) initialTx = 0L

        var lastRx = initialRx
        var lastTx = initialTx
        var totalDown = 0L
        var totalUp = 0L

        statsJob = scope.launch {
            while (isActive) {
                delay(1000)
                val curRx = TrafficStats.getTotalRxBytes()
                val curTx = TrafficStats.getTotalTxBytes()

                val rxDiff = if (curRx > lastRx && lastRx > 0) curRx - lastRx else 0L
                val txDiff = if (curTx > lastTx && lastTx > 0) curTx - lastTx else 0L

                lastRx = curRx
                lastTx = curTx

                totalDown += rxDiff
                totalUp += txDiff

                val duration = if (connectionStartTime > 0) {
                    (System.currentTimeMillis() - connectionStartTime) / 1000
                } else 0L

                VpnStateRepository.updateTrafficStats(
                    VpnTrafficStats(
                        downloadSpeedBps = rxDiff,
                        uploadSpeedBps = txDiff,
                        totalBytesDown = totalDown,
                        totalBytesUp = totalUp,
                        durationSeconds = duration,
                        connectedSince = connectionStartTime
                    )
                )
            }
        }
    }

    private fun stopTrafficMonitoring() {
        statsJob?.cancel()
        statsJob = null
        VpnStateRepository.updateTrafficStats(
            VpnTrafficStats(
                downloadSpeedBps = 0L,
                uploadSpeedBps = 0L,
                totalBytesDown = 0L,
                totalBytesUp = 0L,
                durationSeconds = 0L,
                connectedSince = 0L
            )
        )
    }
}
