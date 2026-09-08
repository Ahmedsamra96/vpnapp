package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.localization.LocalAppStrings
import com.example.model.VpnConnectionState
import com.example.model.VpnServer
import com.example.model.VpnTrafficStats
import com.example.ui.theme.AppBorderLight
import com.example.ui.theme.AppSurfaceLight
import com.example.ui.theme.AppSurfaceVariantLight
import com.example.ui.theme.AppTextMuted
import com.example.ui.theme.AppTextPrimary
import com.example.ui.theme.AppTextSecondary
import com.example.ui.theme.VpnConnectedGreen
import com.example.ui.theme.VpnConnectedGreenSoft
import com.example.ui.theme.VpnPrimaryBlue
import com.example.ui.theme.VpnPrimaryBlueSoft

@Composable
fun ConnectionDetailsDialog(
    server: VpnServer,
    connectionState: VpnConnectionState,
    trafficStats: VpnTrafficStats,
    onDismiss: () -> Unit
) {
    val strings = LocalAppStrings.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = AppSurfaceLight),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(24.dp))
                .testTag("connection_details_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Top Header with Close
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = strings.connectionDetailsTitle,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTextPrimary
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(AppSurfaceVariantLight)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = AppTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Server status card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, AppBorderLight, RoundedCornerShape(16.dp))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(AppSurfaceLight)
                                    .border(1.dp, AppBorderLight, CircleShape)
                            ) {
                                Text(text = server.flagEmoji, fontSize = 22.sp)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = server.country,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppTextPrimary
                                )
                                Text(
                                    text = server.city,
                                    fontSize = 13.sp,
                                    color = AppTextSecondary
                                )
                            }
                        }

                        // Connected badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(VpnConnectedGreenSoft)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(VpnConnectedGreen)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = strings.statusConnected,
                                    color = VpnConnectedGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Key metrics rows
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF8FAFC))
                        .border(1.dp, AppBorderLight, RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    DetailRow(label = strings.ipAddress, value = server.ip)
                    HorizontalDivider(color = AppBorderLight)
                    DetailRow(label = strings.protocolTitle, value = "OpenVPN ${server.protocol}")
                    HorizontalDivider(color = AppBorderLight)
                    DetailRow(label = strings.duration, value = trafficStats.formattedDuration())
                    HorizontalDivider(color = AppBorderLight)
                    DetailRow(label = "${strings.download} / ${strings.upload}", value = "${trafficStats.formattedDownloaded()} ↓ / ${trafficStats.formattedUploaded()} ↑")
                    HorizontalDivider(color = AppBorderLight)
                    DetailRow(label = strings.ping, value = "${server.pingMs} ms")
                    HorizontalDivider(color = AppBorderLight)
                    DetailRow(label = strings.privacySecurityTitle, value = "AES-256-GCM")
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Security card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = VpnPrimaryBlueSoft),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(VpnPrimaryBlue)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "A safer, more private internet",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = VpnPrimaryBlue
                            )
                            Text(
                                text = "No registration. Free access. Just connect and browse securely.",
                                fontSize = 12.sp,
                                color = AppTextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Made for a more open internet 💙",
                    fontSize = 12.sp,
                    color = AppTextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = AppTextSecondary
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppTextPrimary
        )
    }
}
