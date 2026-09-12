package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.LocalAppStrings
import com.example.model.VpnConnectionState
import com.example.model.VpnTrafficStats
import com.example.ui.theme.ThemeColors
import com.example.ui.theme.VpnPrimaryBlue

@Composable
fun TrafficDashboard(
    stats: VpnTrafficStats,
    connectionState: VpnConnectionState,
    onClickDetails: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isConnected = connectionState == VpnConnectionState.CONNECTED

    if (!isConnected) return

    val strings = LocalAppStrings.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("traffic_dashboard")
    ) {
        // Row of 2 cards: Downloaded & Uploaded
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Downloaded Card
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .weight(1f)
                    .shadow(elevation = 3.dp, shape = RoundedCornerShape(18.dp), spotColor = Color(0x10000000))
                    .border(1.dp, androidx.compose.material3.MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(ThemeColors.primarySoft)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = strings.download,
                            tint = VpnPrimaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = stats.formattedDownloaded(),
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = strings.download,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Uploaded Card
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .weight(1f)
                    .shadow(elevation = 3.dp, shape = RoundedCornerShape(18.dp), spotColor = Color(0x10000000))
                    .border(1.dp, androidx.compose.material3.MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(ThemeColors.primarySoft)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = strings.upload,
                            tint = VpnPrimaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = stats.formattedUploaded(),
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = strings.upload,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Connection Duration Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 3.dp, shape = RoundedCornerShape(18.dp), spotColor = Color(0x10000000))
                .border(1.dp, androidx.compose.material3.MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
                .then(
                    if (onClickDetails != null) Modifier.clickable(onClick = onClickDetails) else Modifier
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = strings.duration,
                        tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = stats.formattedDuration(),
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = strings.duration,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }

                if (onClickDetails != null) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = strings.connectionDetailsTitle,
                        color = VpnPrimaryBlue,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
