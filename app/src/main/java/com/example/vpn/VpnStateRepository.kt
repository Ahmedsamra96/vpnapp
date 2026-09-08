package com.example.vpn

import com.example.data.DefaultServers
import com.example.model.LogLevel
import com.example.model.VpnAppSettings
import com.example.model.VpnConnectionState
import com.example.model.VpnLogEntry
import com.example.model.VpnServer
import com.example.model.VpnTrafficStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object VpnStateRepository {

    private val defaultServer = DefaultServers.getDefaultServers().first()

    private val _connectionState = MutableStateFlow(VpnConnectionState.DISCONNECTED)
    val connectionState: StateFlow<VpnConnectionState> = _connectionState.asStateFlow()

    private val _selectedServer = MutableStateFlow<VpnServer>(defaultServer)
    val selectedServer: StateFlow<VpnServer> = _selectedServer.asStateFlow()

    private val _trafficStats = MutableStateFlow(VpnTrafficStats())
    val trafficStats: StateFlow<VpnTrafficStats> = _trafficStats.asStateFlow()

    private val _logs = MutableStateFlow<List<VpnLogEntry>>(emptyList())
    val logs: StateFlow<List<VpnLogEntry>> = _logs.asStateFlow()

    private val _settings = MutableStateFlow(VpnAppSettings())
    val settings: StateFlow<VpnAppSettings> = _settings.asStateFlow()

    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    fun setSelectedServer(server: VpnServer) {
        _selectedServer.value = server
        addLog("Server selected: ${server.country} (${server.ip})", LogLevel.INFO)
    }

    fun setConnectionState(state: VpnConnectionState) {
        _connectionState.value = state
        when (state) {
            VpnConnectionState.CONNECTING -> {
                addLog("Connecting to encrypted OpenVPN tunnel...", LogLevel.INFO)
            }
            VpnConnectionState.CONNECTED -> {
                addLog("Connection secured successfully via OpenVPN protocol", LogLevel.SUCCESS)
            }
            VpnConnectionState.DISCONNECTING -> {
                addLog("Terminating tunnel session...", LogLevel.WARNING)
            }
            VpnConnectionState.DISCONNECTED -> {
                addLog("VPN disconnected", LogLevel.INFO)
                _trafficStats.value = VpnTrafficStats()
            }
        }
    }

    fun updateTrafficStats(stats: VpnTrafficStats) {
        _trafficStats.value = stats
    }

    fun updateSettings(newSettings: VpnAppSettings) {
        _settings.value = newSettings
        addLog("Security settings updated", LogLevel.INFO)
    }

    fun addLog(message: String, level: LogLevel = LogLevel.INFO) {
        val entry = VpnLogEntry(
            timestamp = timeFormat.format(Date()),
            message = message,
            level = level
        )
        val current = _logs.value.toMutableList()
        if (current.size > 150) {
            current.removeAt(0)
        }
        current.add(entry)
        _logs.value = current
    }

    fun clearLogs() {
        _logs.value = emptyList()
    }
}
