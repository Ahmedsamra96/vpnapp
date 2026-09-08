package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.LocalAppStrings
import com.example.model.VpnConnectionState
import com.example.ui.components.AppPermissionsDialog
import com.example.ui.components.ConnectionDetailsDialog
import com.example.ui.components.DataUsageDialog
import com.example.ui.components.LanguageSelectDialog
import com.example.ui.components.LocationsScreen
import com.example.ui.components.PrivacyPolicyDialog
import com.example.ui.components.ProminentDisclosureDialog
import com.example.ui.components.ServerCard
import com.example.ui.components.SettingsScreen
import com.example.ui.components.TrafficDashboard
import com.example.ui.components.VpnLogsDialog
import com.example.ui.components.VpnPowerButton
import com.example.ui.components.WelcomeOnboardingDialog
import com.example.ui.theme.AppBgLight
import com.example.ui.theme.AppBorderLight
import com.example.ui.theme.AppSurfaceLight
import com.example.ui.theme.AppSurfaceVariantLight
import com.example.ui.theme.AppTextPrimary
import com.example.ui.theme.AppTextSecondary
import com.example.ui.theme.VpnConnectedGreen
import com.example.ui.theme.VpnPrimaryBlue
import com.example.ui.theme.VpnPrimaryBlueSoft

@Composable
fun VpnMainScreen(
    viewModel: VpnViewModel,
    onConnectRequested: () -> Unit,
    onDisconnectRequested: () -> Unit,
    onReconnectRequested: () -> Unit = onConnectRequested,
    onRequestNotificationPermission: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val connectionState by viewModel.connectionState.collectAsState()
    val selectedServer by viewModel.selectedServer.collectAsState()
    val trafficStats by viewModel.trafficStats.collectAsState()
    val logs by viewModel.logs.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()
    val hasAcceptedConsent by viewModel.hasAcceptedConsent.collectAsState()
    val appLanguage by viewModel.appLanguage.collectAsState()
    val effectiveLanguage by viewModel.effectiveLanguage.collectAsState()
    val strings by viewModel.appStrings.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showLogsDialog by remember { mutableStateOf(false) }
    var showConnectionDetails by remember { mutableStateOf(false) }
    var showOnboardingDialog by remember { mutableStateOf(false) }
    var showPrivacyPolicyDialog by remember { mutableStateOf(false) }
    var showDataSafetyDialog by remember { mutableStateOf(false) }
    var showProminentDisclosureDialog by remember { mutableStateOf(false) }
    var showPermissionsDialog by remember { mutableStateOf(false) }
    var showLanguageSelectDialog by remember { mutableStateOf(false) }

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUserMessage()
        }
    }

    CompositionLocalProvider(
        LocalLayoutDirection provides effectiveLanguage.layoutDirection,
        LocalAppStrings provides strings
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                NavigationBar(
                    containerColor = AppSurfaceLight,
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, AppBorderLight)
                        .testTag("main_bottom_nav")
                ) {
                    NavigationBarItem(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = strings.navHome
                            )
                        },
                        label = {
                            Text(
                                text = strings.navHome,
                                fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = VpnPrimaryBlue,
                            selectedTextColor = VpnPrimaryBlue,
                            indicatorColor = VpnPrimaryBlueSoft,
                            unselectedIconColor = AppTextSecondary,
                            unselectedTextColor = AppTextSecondary
                        ),
                        modifier = Modifier.testTag("nav_item_home")
                    )

                    NavigationBarItem(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Public,
                                contentDescription = strings.navServers
                            )
                        },
                        label = {
                            Text(
                                text = strings.navServers,
                                fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = VpnPrimaryBlue,
                            selectedTextColor = VpnPrimaryBlue,
                            indicatorColor = VpnPrimaryBlueSoft,
                            unselectedIconColor = AppTextSecondary,
                            unselectedTextColor = AppTextSecondary
                        ),
                        modifier = Modifier.testTag("nav_item_locations")
                    )

                    NavigationBarItem(
                        selected = selectedTabIndex == 2,
                        onClick = { selectedTabIndex = 2 },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = strings.navSettings
                            )
                        },
                        label = {
                            Text(
                                text = strings.navSettings,
                                fontWeight = if (selectedTabIndex == 2) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = VpnPrimaryBlue,
                            selectedTextColor = VpnPrimaryBlue,
                            indicatorColor = VpnPrimaryBlueSoft,
                            unselectedIconColor = AppTextSecondary,
                            unselectedTextColor = AppTextSecondary
                        ),
                        modifier = Modifier.testTag("nav_item_settings")
                    )
                }
            },
            containerColor = AppBgLight,
            modifier = modifier.fillMaxSize()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(AppBgLight)
            ) {
                when (selectedTabIndex) {
                    0 -> HomeContent(
                        viewModel = viewModel,
                        connectionState = connectionState,
                        selectedServer = selectedServer,
                        trafficStats = trafficStats,
                        onToggleConnect = {
                            if (connectionState == VpnConnectionState.CONNECTED || connectionState == VpnConnectionState.CONNECTING) {
                                onDisconnectRequested()
                            } else if (connectionState == VpnConnectionState.DISCONNECTED) {
                                if (!hasAcceptedConsent) {
                                    showProminentDisclosureDialog = true
                                } else {
                                    onConnectRequested()
                                }
                            }
                        },
                        onSelectLocation = { selectedTabIndex = 1 },
                        onOpenDetails = { showConnectionDetails = true },
                        onOpenLogs = { showLogsDialog = true },
                        onOpenHelp = { showOnboardingDialog = true }
                    )

                    1 -> LocationsScreen(
                        viewModel = viewModel,
                        onServerSelected = { server ->
                            selectedTabIndex = 0
                            val isConnectedOrConnecting = connectionState == VpnConnectionState.CONNECTED ||
                                                          connectionState == VpnConnectionState.CONNECTING
                            if (isConnectedOrConnecting) {
                                viewModel.notifyServerChanged(server)
                                if (!hasAcceptedConsent) {
                                    showProminentDisclosureDialog = true
                                } else {
                                    onReconnectRequested()
                                }
                            }
                        }
                    )

                    2 -> SettingsScreen(
                        viewModel = viewModel,
                        onShowLogs = { showLogsDialog = true },
                        onShowAbout = { showOnboardingDialog = true },
                        onShowPrivacyPolicy = { showPrivacyPolicyDialog = true },
                        onShowDataSafety = { showDataSafetyDialog = true },
                        onShowDisclosure = { showProminentDisclosureDialog = true },
                        onShowPermissions = { showPermissionsDialog = true },
                        onOpenLanguageSelector = { showLanguageSelectDialog = true }
                    )
                }
            }
        }

        // Language Select Dialog
        if (showLanguageSelectDialog) {
            LanguageSelectDialog(
                currentLanguage = appLanguage,
                onLanguageSelected = { lang ->
                    viewModel.setLanguage(lang)
                },
                onDismiss = { showLanguageSelectDialog = false }
            )
        }

    // Connection Details Dialog
    if (showConnectionDetails) {
        ConnectionDetailsDialog(
            server = selectedServer,
            connectionState = connectionState,
            trafficStats = trafficStats,
            onDismiss = { showConnectionDetails = false }
        )
    }

    // Welcome & About Onboarding Dialog
    if (showOnboardingDialog) {
        WelcomeOnboardingDialog(
            onDismiss = { showOnboardingDialog = false },
            onViewPrivacyPolicy = { showPrivacyPolicyDialog = true }
        )
    }

    // OpenVPN Live Logs Dialog
    if (showLogsDialog) {
        VpnLogsDialog(
            logs = logs,
            onDismiss = { showLogsDialog = false }
        )
    }

    // Google Play Mandatory Prominent In-App Disclosure & Consent
    if (showProminentDisclosureDialog) {
        ProminentDisclosureDialog(
            onAccept = {
                viewModel.acceptConsent()
                showProminentDisclosureDialog = false
                onConnectRequested()
            },
            onDecline = {
                showProminentDisclosureDialog = false
            },
            onViewPrivacyPolicy = {
                showPrivacyPolicyDialog = true
            },
            onViewDataSafety = {
                showDataSafetyDialog = true
            }
        )
    }

    // Full Privacy Policy Screen / Dialog
    if (showPrivacyPolicyDialog) {
        PrivacyPolicyDialog(
            onDismiss = { showPrivacyPolicyDialog = false }
        )
    }

    // Data Safety & Usage Screen / Dialog
    if (showDataSafetyDialog) {
        DataUsageDialog(
            onDismiss = { showDataSafetyDialog = false }
        )
    }

    // App Permissions & Transparency Dialog
    if (showPermissionsDialog) {
        AppPermissionsDialog(
            onRequestNotificationPermission = {
                showPermissionsDialog = false
                onRequestNotificationPermission()
            },
            onDismiss = { showPermissionsDialog = false }
        )
    }
    }
}

@Composable
private fun HomeContent(
    viewModel: VpnViewModel,
    connectionState: VpnConnectionState,
    selectedServer: com.example.model.VpnServer,
    trafficStats: com.example.model.VpnTrafficStats,
    onToggleConnect: () -> Unit,
    onSelectLocation: () -> Unit,
    onOpenDetails: () -> Unit,
    onOpenLogs: () -> Unit,
    onOpenHelp: () -> Unit
) {
    val isConnected = connectionState == VpnConnectionState.CONNECTED
    val strings = LocalAppStrings.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(VpnPrimaryBlueSoft)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = VpnPrimaryBlue,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = strings.appTitle,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppTextPrimary
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onOpenLogs,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(AppSurfaceLight)
                        .border(1.dp, AppBorderLight, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.BugReport,
                        contentDescription = strings.connectionLogsTitle,
                        tint = AppTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = onOpenHelp,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(AppSurfaceLight)
                        .border(1.dp, AppBorderLight, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = strings.helpSupportTitle,
                        tint = AppTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Selected Server Card (Clickable to open Locations)
        ServerCard(
            server = selectedServer,
            connectionState = connectionState,
            onClickChange = onSelectLocation
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Concentric Power Button
        VpnPowerButton(
            connectionState = connectionState,
            onToggleConnect = onToggleConnect
        )

        Spacer(modifier = Modifier.height(26.dp))

        // Connected stats vs Disconnected features
        if (isConnected) {
            TrafficDashboard(
                stats = trafficStats,
                connectionState = connectionState,
                onClickDetails = onOpenDetails
            )
        } else {
            // Disconnected Feature Highlight Cards
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = AppSurfaceLight),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 3.dp, shape = RoundedCornerShape(18.dp), spotColor = Color(0x0A000000))
                    .border(1.dp, AppBorderLight, RoundedCornerShape(18.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = VpnPrimaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = strings.vpnSecurityCategory,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    FeatureBadgeRow(
                        icon = Icons.Default.CheckCircle,
                        title = strings.freeFeatureTitle,
                        subtitle = strings.freeFeatureSubtitle
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    FeatureBadgeRow(
                        icon = Icons.Default.Lock,
                        title = strings.secureFeatureTitle,
                        subtitle = strings.secureFeatureSubtitle
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    FeatureBadgeRow(
                        icon = Icons.Default.Speed,
                        title = strings.optimalServer,
                        subtitle = strings.optimalServerDesc
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun FeatureBadgeRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(AppSurfaceVariantLight)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = VpnConnectedGreen,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppTextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = AppTextSecondary
            )
        }
    }
}
