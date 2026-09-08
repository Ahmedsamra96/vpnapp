package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.VpnConnectionState
import com.example.ui.components.ServerCard
import com.example.ui.components.ServerListSheet
import com.example.ui.components.TrafficDashboard
import com.example.ui.components.VpnLogsDialog
import com.example.ui.components.VpnPowerButton
import com.example.ui.components.VpnSettingsDialog
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VpnMainScreen(
    viewModel: VpnViewModel,
    onConnectRequested: () -> Unit,
    onDisconnectRequested: () -> Unit,
    modifier: Modifier = Modifier
) {
    val connectionState by viewModel.connectionState.collectAsState()
    val selectedServer by viewModel.selectedServer.collectAsState()
    val trafficStats by viewModel.trafficStats.collectAsState()
    val logs by viewModel.logs.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val filteredServers by viewModel.filteredServers.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val isLoadingServers by viewModel.isLoadingServers.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showServerSheet by remember { mutableStateOf(false) }
    var showLogsDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = CyberBackground,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF060A14),
                            CyberBackground,
                            Color(0xFF070B16)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header Bar
                TopAppBarContent(
                    onOpenLogs = { showLogsDialog = true },
                    onOpenSettings = { showSettingsDialog = true }
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Selected Server Card
                ServerCard(
                    server = selectedServer,
                    connectionState = connectionState,
                    onClickChange = { showServerSheet = true }
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Center One-Click Power Button
                VpnPowerButton(
                    connectionState = connectionState,
                    onToggleConnect = {
                        if (connectionState == VpnConnectionState.CONNECTED || connectionState == VpnConnectionState.CONNECTING) {
                            onDisconnectRequested()
                        } else if (connectionState == VpnConnectionState.DISCONNECTED) {
                            onConnectRequested()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Live Traffic & Duration Dashboard
                TrafficDashboard(
                    stats = trafficStats,
                    connectionState = connectionState,
                    server = selectedServer
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Security Features Strip
                SecurityFeaturesRow(
                    isKillSwitchOn = settings.killSwitchEnabled,
                    isDnsProtected = settings.dnsLeakProtection
                )

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Server Selection Modal Bottom Sheet
    if (showServerSheet) {
        ServerListSheet(
            sheetState = sheetState,
            servers = filteredServers,
            selectedServer = selectedServer,
            searchQuery = searchQuery,
            selectedCategory = selectedCategory,
            isLoading = isLoadingServers,
            onSearchChange = { viewModel.setSearchQuery(it) },
            onCategoryChange = { viewModel.setCategory(it) },
            onSelectServer = { server ->
                viewModel.selectServer(server)
            },
            onToggleFavorite = { server ->
                viewModel.toggleFavorite(server)
            },
            onRefreshServers = {
                viewModel.refreshServers(silent = false)
            },
            onDismiss = { showServerSheet = false }
        )
    }

    // OpenVPN Live Logs Dialog
    if (showLogsDialog) {
        VpnLogsDialog(
            logs = logs,
            onDismiss = { showLogsDialog = false }
        )
    }

    // Settings Dialog
    if (showSettingsDialog) {
        VpnSettingsDialog(
            settings = settings,
            onToggleKillSwitch = { viewModel.toggleKillSwitch(it) },
            onToggleDnsLeak = { viewModel.toggleDnsLeak(it) },
            onSetProtocol = { viewModel.setProtocol(it) },
            onImportCustomConfig = { name, config ->
                viewModel.importCustomOvpnConfig(name, config)
            },
            onDismiss = { showSettingsDialog = false }
        )
    }
}

@Composable
private fun TopAppBarContent(
    onOpenLogs: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        // App Title and Icon
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(CyberSurface)
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = R.drawable.img_vpn_shield),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = "OpenVPN Free",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(CyberEmerald)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "100% مجاني بدون قيود",
                        color = CyberEmerald,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Action Icons
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onOpenLogs,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(CyberSurface)
                    .testTag("open_logs_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Terminal,
                    contentDescription = "سجل الاتصال",
                    tint = CyberCyan,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(CyberSurface)
                    .testTag("open_settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "الإعدادات",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun SecurityFeaturesRow(
    isKillSwitchOn: Boolean,
    isDnsProtected: Boolean
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        SecurityPill(
            icon = Icons.Default.Security,
            label = "Kill Switch",
            isActive = isKillSwitchOn,
            modifier = Modifier.weight(1f)
        )
        SecurityPill(
            icon = Icons.Default.Dns,
            label = "حماية DNS",
            isActive = isDnsProtected,
            modifier = Modifier.weight(1f)
        )
        SecurityPill(
            icon = Icons.Default.AllInclusive,
            label = "بيانات غير محدودة",
            isActive = true,
            modifier = Modifier.weight(1.2f)
        )
    }
}

@Composable
private fun SecurityPill(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(CyberSurface)
            .padding(horizontal = 8.dp, vertical = 7.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isActive) CyberEmerald else CyberBorder,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            color = if (isActive) TextPrimary else TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
