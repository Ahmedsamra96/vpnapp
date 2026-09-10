package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppLanguage
import com.example.localization.LocalAppStrings
import com.example.ui.VpnViewModel
import com.example.ui.theme.AppBgLight
import com.example.ui.theme.AppBorderLight
import com.example.ui.theme.AppSurfaceLight
import com.example.ui.theme.AppSurfaceVariantLight
import com.example.ui.theme.AppTextMuted
import com.example.ui.theme.AppTextPrimary
import com.example.ui.theme.AppTextSecondary
import com.example.ui.theme.VpnPrimaryBlue

@Composable
fun SettingsScreen(
    viewModel: VpnViewModel,
    onShowAbout: () -> Unit,
    onShowPrivacyPolicy: () -> Unit = {},
    onShowDataSafety: () -> Unit = {},
    onShowDisclosure: () -> Unit = {},
    onShowPermissions: () -> Unit = {},
    onOpenLanguageSelector: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsState()
    val appLanguage by viewModel.appLanguage.collectAsState()
    val effectiveLanguage by viewModel.effectiveLanguage.collectAsState()
    val strings = LocalAppStrings.current

    var autoConnect by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    val themeMode by viewModel.themeMode.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Title
        Text(
            text = strings.settingsTitle,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = strings.settingsSubtitle,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        // App Brand Header Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 3.dp, shape = RoundedCornerShape(20.dp), spotColor = Color(0x0C000000))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                AppBrandLogo(
                    size = 52.dp,
                    showContainer = true,
                    showGlow = true
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = strings.appTitle,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = strings.versionFree,
                        fontSize = 12.sp,
                        color = VpnPrimaryBlue,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Card 1: VPN Security Toggles
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 3.dp, shape = RoundedCornerShape(18.dp), spotColor = Color(0x0C000000))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
        ) {
            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                // Auto-connect
                SettingToggleRow(
                    icon = Icons.Default.Bolt,
                    title = strings.autoConnectTitle,
                    subtitle = strings.autoConnectSubtitle,
                    checked = autoConnect,
                    onCheckedChange = { autoConnect = it },
                    testTag = "toggle_auto_connect"
                )

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outline)

                // Kill Switch
                SettingToggleRow(
                    icon = Icons.Default.Shield,
                    title = strings.killSwitchTitle,
                    subtitle = strings.killSwitchSubtitle,
                    checked = settings.killSwitchEnabled,
                    onCheckedChange = { viewModel.toggleKillSwitch(it) },
                    testTag = "toggle_kill_switch"
                )

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outline)

                // DNS Leak Protection
                SettingToggleRow(
                    icon = Icons.Default.Security,
                    title = strings.dnsLeakTitle,
                    subtitle = strings.dnsLeakSubtitle,
                    checked = settings.dnsLeakProtection,
                    onCheckedChange = { viewModel.toggleDnsLeak(it) },
                    testTag = "toggle_dns_leak"
                )


            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Card 2: Preferences (Language, Appearance, Protocol)
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 3.dp, shape = RoundedCornerShape(18.dp), spotColor = Color(0x0C000000))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
        ) {
            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                // Protocol
                SettingActionRow(
                    icon = Icons.Default.Shield,
                    title = strings.protocolTitle,
                    value = "OpenVPN ${settings.preferredProtocol}",
                    onClick = {
                        val next = if (settings.preferredProtocol == "UDP") "TCP" else "UDP"
                        viewModel.setProtocol(next)
                    }
                )

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outline)

                // Language
                val displayLangName = if (appLanguage == AppLanguage.SYSTEM) {
                    "${appLanguage.flag} System (${effectiveLanguage.nativeName})"
                } else {
                    "${appLanguage.flag} ${appLanguage.nativeName}"
                }

                SettingActionRow(
                    icon = Icons.Default.Language,
                    title = strings.languageTitle,
                    value = displayLangName,
                    onClick = onOpenLanguageSelector
                )

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outline)

                // App Appearance
                val themeLabel = when (themeMode) {
                    com.example.model.AppThemeMode.SYSTEM -> strings.appearanceSystem
                    com.example.model.AppThemeMode.LIGHT -> strings.appearanceLight
                    com.example.model.AppThemeMode.DARK -> strings.appearanceDark
                }

                SettingActionRow(
                    icon = Icons.Default.Palette,
                    title = strings.appearanceTitle,
                    value = themeLabel,
                    onClick = { showThemeDialog = true }
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Card 3: Advanced & About
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 3.dp, shape = RoundedCornerShape(18.dp), spotColor = Color(0x0C000000))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
        ) {
            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                // About Ruvon VPN
                SettingActionRow(
                    icon = Icons.Default.Info,
                    title = strings.aboutAppTitle,
                    value = strings.versionFree,
                    onClick = onShowAbout
                )

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outline)

                // Help & Support
                SettingActionRow(
                    icon = Icons.Default.HelpOutline,
                    title = strings.helpSupportTitle,
                    value = strings.privacyPolicyTitle,
                    onClick = onShowAbout
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Card 4: Privacy & Google Play Compliance
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 3.dp, shape = RoundedCornerShape(18.dp), spotColor = Color(0x0C000000))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
        ) {
            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                // Privacy Policy
                SettingActionRow(
                    icon = Icons.Default.Policy,
                    title = strings.privacyPolicyTitle,
                    value = strings.privacyPolicySubtitle,
                    onClick = onShowPrivacyPolicy
                )

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outline)

                // Data Safety & Security
                SettingActionRow(
                    icon = Icons.Default.DataUsage,
                    title = strings.dataSafetyTitle,
                    value = strings.dataSafetySubtitle,
                    onClick = onShowDataSafety
                )

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outline)

                // VPN Service Disclosure
                SettingActionRow(
                    icon = Icons.Default.Shield,
                    title = strings.vpnDisclosureTitle,
                    value = strings.vpnDisclosureSubtitle,
                    onClick = onShowDisclosure
                )

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outline)

                // App Permissions
                SettingActionRow(
                    icon = Icons.Default.VpnKey,
                    title = strings.appPermissionsTitle,
                    value = strings.appPermissionsSubtitle,
                    onClick = onShowPermissions
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }

    if (showThemeDialog) {
        ThemeSelectDialog(
            currentTheme = themeMode,
            onThemeSelected = { mode ->
                viewModel.setThemeMode(mode)
            },
            onDismiss = { showThemeDialog = false }
        )
    }
}

@Composable
private fun SettingToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = VpnPrimaryBlue,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = MaterialTheme.colorScheme.outline
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}

@Composable
private fun SettingActionRow(
    icon: ImageVector,
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(13.dp)
            )
        }
    }
}
