package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DefaultServers
import com.example.data.VpnGateRepository
import com.example.model.LogLevel
import com.example.model.VpnAppSettings
import com.example.model.VpnConnectionState
import com.example.model.VpnLogEntry
import com.example.model.VpnServer
import com.example.model.VpnTrafficStats
import com.example.vpn.VpnStateRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ServerFilterCategory {
    ALL,
    FASTEST,
    FAVORITES
}

class VpnViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VpnGateRepository(application)

    val connectionState: StateFlow<VpnConnectionState> = VpnStateRepository.connectionState
    val selectedServer: StateFlow<VpnServer> = VpnStateRepository.selectedServer
    val trafficStats: StateFlow<VpnTrafficStats> = VpnStateRepository.trafficStats
    val logs: StateFlow<List<VpnLogEntry>> = VpnStateRepository.logs
    val settings: StateFlow<VpnAppSettings> = VpnStateRepository.settings

    private val _servers = MutableStateFlow<List<VpnServer>>(DefaultServers.getDefaultServers())
    val rawServers: StateFlow<List<VpnServer>> = _servers.asStateFlow()

    private val _isLoadingServers = MutableStateFlow(false)
    val isLoadingServers: StateFlow<Boolean> = _isLoadingServers.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(ServerFilterCategory.ALL)
    val selectedCategory: StateFlow<ServerFilterCategory> = _selectedCategory.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // Filtered server list based on category and query
    val filteredServers: StateFlow<List<VpnServer>> = combine(
        _servers,
        _searchQuery,
        _selectedCategory
    ) { list, query, category ->
        var result = list

        if (category == ServerFilterCategory.FAVORITES) {
            result = result.filter { it.isFavorite }
        } else if (category == ServerFilterCategory.FASTEST) {
            result = result.sortedBy { it.pingMs }
        }

        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            result = result.filter {
                it.country.lowercase().contains(q) ||
                it.countryAr.lowercase().contains(q) ||
                it.city.lowercase().contains(q) ||
                it.ip.contains(q)
            }
        }
        result
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DefaultServers.getDefaultServers())

    init {
        // Automatically fetch live VPNGate servers on startup in the background
        refreshServers(silent = true)
    }

    fun selectServer(server: VpnServer) {
        val actualServer = if (server.isOptimal) {
            // Pick lowest ping server from the list
            val nonOptimal = _servers.value.filter { !it.isOptimal }
            nonOptimal.minByOrNull { it.pingMs } ?: server
        } else {
            server
        }
        VpnStateRepository.setSelectedServer(actualServer)
    }

    fun refreshServers(silent: Boolean = false) {
        viewModelScope.launch {
            if (!silent) _isLoadingServers.value = true
            try {
                VpnStateRepository.addLog("جاري فحص وتحديث قائمة خوادم OpenVPN المجانية...", LogLevel.INFO)
                val liveServers = repository.fetchServers()
                if (liveServers.isNotEmpty()) {
                    _servers.value = liveServers
                    if (!silent) {
                        _userMessage.value = "تم تحديث ${liveServers.size} خادم بنجاح!"
                        VpnStateRepository.addLog("تم جلب ${liveServers.size} خادم OpenVPN مجاني نشط", LogLevel.SUCCESS)
                    }
                }
            } catch (e: Exception) {
                if (!silent) {
                    _userMessage.value = "تعذر تحديث الخوادم مباشرة، تم استخدام الخوادم المدمجة السريعة"
                }
            } finally {
                _isLoadingServers.value = false
            }
        }
    }

    fun toggleFavorite(server: VpnServer) {
        val newFav = repository.toggleFavorite(server.id)
        _servers.value = _servers.value.map {
            if (it.id == server.id) it.copy(isFavorite = newFav) else it
        }
        if (selectedServer.value.id == server.id) {
            VpnStateRepository.setSelectedServer(selectedServer.value.copy(isFavorite = newFav))
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategory(category: ServerFilterCategory) {
        _selectedCategory.value = category
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun toggleKillSwitch(enabled: Boolean) {
        VpnStateRepository.updateSettings(settings.value.copy(killSwitchEnabled = enabled))
    }

    fun toggleDnsLeak(enabled: Boolean) {
        VpnStateRepository.updateSettings(settings.value.copy(dnsLeakProtection = enabled))
    }

    fun setProtocol(protocol: String) {
        VpnStateRepository.updateSettings(settings.value.copy(preferredProtocol = protocol))
    }

    fun importCustomOvpnConfig(name: String, configContent: String) {
        if (configContent.isBlank()) return
        val customServer = VpnServer(
            id = "custom_${System.currentTimeMillis()}",
            country = name.ifBlank { "خادم مخصص" },
            countryAr = name.ifBlank { "خادم مخصص" },
            countryCode = "CUSTOM",
            city = "OpenVPN Config",
            ip = "خادم مخصص",
            pingMs = 25,
            speedMbps = 100.0,
            protocol = if (configContent.contains("proto tcp", ignoreCase = true)) "TCP" else "UDP",
            port = 1194,
            ovpnConfigBase64 = android.util.Base64.encodeToString(configContent.toByteArray(), android.util.Base64.NO_WRAP),
            flagEmoji = "⚙️"
        )
        val current = _servers.value.toMutableList()
        current.add(1, customServer)
        _servers.value = current
        selectServer(customServer)
        _userMessage.value = "تمت إضافة وتفعيل ملف التكوين المخصص بنجاح"
        VpnStateRepository.addLog("تم استيراد ملف تكوين OpenVPN بنجاح ($name)", LogLevel.SUCCESS)
    }
}
