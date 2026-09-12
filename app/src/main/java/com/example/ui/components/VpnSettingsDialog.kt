package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.localization.LocalAppStrings
import com.example.model.VpnAppSettings
import com.example.ui.theme.ThemeColors
import com.example.ui.theme.VpnConnectedGreen
import com.example.ui.theme.VpnPrimaryBlue

@Composable
fun VpnSettingsDialog(
    settings: VpnAppSettings,
    onToggleKillSwitch: (Boolean) -> Unit,
    onToggleDnsLeak: (Boolean) -> Unit,
    onSetProtocol: (String) -> Unit,
    onImportCustomConfig: (name: String, config: String) -> Unit,
    onDismiss: () -> Unit
) {
    val strings = LocalAppStrings.current
    var showImportField by remember { mutableStateOf(false) }
    var configName by remember { mutableStateOf("") }
    var configText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, androidx.compose.material3.MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = VpnPrimaryBlue,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strings.securityProtocolSettingsTitle,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = strings.closeBtn,
                            tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Kill Switch Setting Card
                SettingToggleRow(
                    icon = Icons.Default.Security,
                    title = strings.killSwitchTitle,
                    subtitle = strings.killSwitchSubtitle,
                    checked = settings.killSwitchEnabled,
                    onCheckedChange = onToggleKillSwitch
                )

                Spacer(modifier = Modifier.height(12.dp))

                // DNS Leak Protection Setting Card
                SettingToggleRow(
                    icon = Icons.Default.Dns,
                    title = strings.dnsLeakTitle,
                    subtitle = strings.dnsLeakSubtitle,
                    checked = settings.dnsLeakProtection,
                    onCheckedChange = onToggleDnsLeak
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Protocol Selector
                Text(
                    text = strings.protocolTitle,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FilterChip(
                        selected = settings.preferredProtocol == "UDP",
                        onClick = { onSetProtocol("UDP") },
                        label = { Text(strings.udpFastLabel) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = VpnPrimaryBlue.copy(alpha = 0.15f),
                            selectedLabelColor = VpnPrimaryBlue,
                            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = settings.preferredProtocol == "UDP",
                            borderColor = if (settings.preferredProtocol == "UDP") VpnPrimaryBlue else androidx.compose.material3.MaterialTheme.colorScheme.outline
                        )
                    )

                    FilterChip(
                        selected = settings.preferredProtocol == "TCP",
                        onClick = { onSetProtocol("TCP") },
                        label = { Text(strings.tcpStableLabel) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = VpnPrimaryBlue.copy(alpha = 0.15f),
                            selectedLabelColor = VpnPrimaryBlue,
                            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = settings.preferredProtocol == "TCP",
                            borderColor = if (settings.preferredProtocol == "TCP") VpnPrimaryBlue else androidx.compose.material3.MaterialTheme.colorScheme.outline
                        )
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Custom OpenVPN Import Button
                if (!showImportField) {
                    Button(
                        onClick = { showImportField = true },
                        colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant),
                        border = androidx.compose.foundation.BorderStroke(1.dp, androidx.compose.material3.MaterialTheme.colorScheme.outline),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("open_import_config_button")
                    ) {
                        Icon(Icons.Default.FileUpload, contentDescription = null, tint = VpnPrimaryBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            strings.customConfigImportTitle,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, androidx.compose.material3.MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = strings.customConfigTitle,
                            color = VpnPrimaryBlue,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = configName,
                            onValueChange = { configName = it },
                            placeholder = {
                                Text(
                                    strings.customConfigNamePlaceholder,
                                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    fontSize = 12.sp
                                )
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                                focusedBorderColor = VpnPrimaryBlue,
                                unfocusedBorderColor = androidx.compose.material3.MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = configText,
                            onValueChange = { configText = it },
                            placeholder = {
                                Text(
                                    strings.customConfigContentPlaceholder,
                                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    fontSize = 12.sp
                                )
                            },
                            minLines = 3,
                            maxLines = 5,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                                focusedBorderColor = VpnPrimaryBlue,
                                unfocusedBorderColor = androidx.compose.material3.MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { showImportField = false },
                                colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color.Transparent)
                            ) {
                                Text(
                                    strings.cancelBtn,
                                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (configText.isNotBlank()) {
                                        onImportCustomConfig(configName, configText)
                                        showImportField = false
                                        onDismiss()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = VpnPrimaryBlue),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(strings.saveAndUseBtn, color = androidx.compose.ui.graphics.Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 100% Free Guarantee Banner
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(VpnConnectedGreen.copy(alpha = 0.1f))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.GppGood,
                        contentDescription = null,
                        tint = VpnConnectedGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = strings.freeGuaranteeBadge,
                        color = VpnConnectedGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingToggleRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, androidx.compose.material3.MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = VpnPrimaryBlue,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = androidx.compose.ui.graphics.Color.White,
                checkedTrackColor = VpnPrimaryBlue,
                uncheckedThumbColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                uncheckedTrackColor = androidx.compose.material3.MaterialTheme.colorScheme.outline
            )
        )
    }
}
