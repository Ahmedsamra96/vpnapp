package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DefaultServers
import com.example.data.VpnGateRepository
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import com.example.localization.getAppStrings
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
    private val prefs = application.getSharedPreferences("vpn_prefs", Context.MODE_PRIVATE)

    private val _hasAcceptedConsent = MutableStateFlow(
        prefs.getBoolean("has_accepted_vpn_consent", false)
    )
    val hasAcceptedConsent: StateFlow<Boolean> = _hasAcceptedConsent.asStateFlow()

    fun acceptConsent() {
        prefs.edit().putBoolean("has_accepted_vpn_consent", true).apply()
        _hasAcceptedConsent.value = true
        VpnStateRepository.addLog("VPN Service Disclosure and Privacy Policy accepted", LogLevel.INFO)
    }

    // Language preference management
    private val _appLanguage = MutableStateFlow(
        AppLanguage.fromCode(prefs.getString("app_language", AppLanguage.SYSTEM.code) ?: AppLanguage.SYSTEM.code)
    )
    val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

    private val _effectiveLanguage = MutableStateFlow(
        AppLanguage.resolveEffectiveLanguage(_appLanguage.value)
    )
    val effectiveLanguage: StateFlow<AppLanguage> = _effectiveLanguage.asStateFlow()

    private val _appStrings = MutableStateFlow(getAppStrings(_appLanguage.value))
    val appStrings: StateFlow<AppStrings> = _appStrings.asStateFlow()

    fun setLanguage(language: AppLanguage) {
        prefs.edit().putString("app_language", language.code).apply()
        _appLanguage.value = language
        val resolved = AppLanguage.resolveEffectiveLanguage(language)
        _effectiveLanguage.value = resolved
        _appStrings.value = getAppStrings(language)
        VpnStateRepository.addLog("App language changed to: ${resolved.englishName}", LogLevel.INFO)
    }

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

    fun notifyServerChanged(server: VpnServer) {
        val strings = _appStrings.value
        val isArabic = _effectiveLanguage.value == AppLanguage.ARABIC
        val targetName = server.getDisplayName(isArabic, strings.optimalServer)
        _userMessage.value = strings.formatReconnecting(targetName)
        VpnStateRepository.addLog("Reconnecting to server: $targetName (${server.ip})", LogLevel.INFO)
    }

    fun refreshServers(silent: Boolean = false) {
        viewModelScope.launch {
            if (!silent) _isLoadingServers.value = true
            try {
                VpnStateRepository.addLog("Scanning and refreshing OpenVPN servers...", LogLevel.INFO)
                val liveServers = repository.fetchServers()
                if (liveServers.isNotEmpty()) {
                    _servers.value = liveServers
                    if (!silent) {
                        _userMessage.value = _appStrings.value.formatServersRefreshed(liveServers.size)
                        VpnStateRepository.addLog("Retrieved ${liveServers.size} active OpenVPN servers", LogLevel.SUCCESS)
                    }
                }
            } catch (e: Exception) {
                if (!silent) {
                    _userMessage.value = _appStrings.value.serversRefreshFailed
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
        val strings = _appStrings.value
        val isArabic = _effectiveLanguage.value == AppLanguage.ARABIC
        val defaultName = if (isArabic) "خادم مخصص" else "Custom Server"
        val customServer = VpnServer(
            id = "custom_${System.currentTimeMillis()}",
            country = name.ifBlank { defaultName },
            countryAr = name.ifBlank { "خادم مخصص" },
            countryCode = "CUSTOM",
            city = "OpenVPN Config",
            ip = "Custom",
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
        _userMessage.value = strings.customConfigImported
        VpnStateRepository.addLog("Imported custom OpenVPN config successfully ($name)", LogLevel.SUCCESS)
    }
}
