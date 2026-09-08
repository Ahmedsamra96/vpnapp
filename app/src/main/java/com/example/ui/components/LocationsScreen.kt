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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.NetworkCell
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.model.VpnServer
import com.example.ui.VpnViewModel
import com.example.ui.theme.AppBgLight
import com.example.ui.theme.AppBorderLight
import com.example.ui.theme.AppSurfaceLight
import com.example.ui.theme.AppSurfaceVariantLight
import com.example.ui.theme.AppTextMuted
import com.example.ui.theme.AppTextPrimary
import com.example.ui.theme.AppTextSecondary
import com.example.ui.theme.VpnAmber
import com.example.ui.theme.VpnConnectedGreen
import com.example.ui.theme.VpnPrimaryBlue

@Composable
fun LocationsScreen(
    viewModel: VpnViewModel,
    onServerSelected: (VpnServer) -> Unit,
    modifier: Modifier = Modifier
) {
    val servers by viewModel.filteredServers.collectAsState()
    val selectedServer by viewModel.selectedServer.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isLoading by viewModel.isLoadingServers.collectAsState()
    val strings = LocalAppStrings.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBgLight)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Header Title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    text = strings.serversTitle,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppTextPrimary
                )
                Text(
                    text = strings.serverCountFormat.format(servers.size),
                    fontSize = 13.sp,
                    color = AppTextSecondary
                )
            }

            IconButton(
                onClick = { viewModel.refreshServers() },
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(AppSurfaceLight)
                    .border(1.dp, AppBorderLight, CircleShape)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = VpnPrimaryBlue
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = strings.refreshServers,
                        tint = AppTextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = {
                Text(
                    text = strings.searchPlaceholder,
                    color = AppTextMuted,
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = AppTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search",
                            tint = AppTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = AppSurfaceLight,
                unfocusedContainerColor = AppSurfaceLight,
                focusedBorderColor = VpnPrimaryBlue,
                unfocusedBorderColor = AppBorderLight
            ),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_servers_input")
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Servers List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(servers, key = { it.id }) { server ->
                val isSelected = selectedServer.id == server.id

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp), spotColor = Color(0x0A000000))
                        .clip(RoundedCornerShape(16.dp))
                        .background(AppSurfaceLight)
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) VpnPrimaryBlue else AppBorderLight,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable {
                            viewModel.selectServer(server)
                            onServerSelected(server)
                        }
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    // Flag and Names
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(AppSurfaceVariantLight)
                        ) {
                            Text(
                                text = server.flagEmoji,
                                fontSize = 22.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = server.country,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AppTextPrimary
                                )
                                if (isSelected) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = VpnPrimaryBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Text(
                                text = if (server.isOptimal) strings.optimalServer else "${server.city} (${server.ip})",
                                fontSize = 12.sp,
                                color = AppTextSecondary
                            )
                        }
                    }

                    // Ping and Signal Bar
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${server.pingMs} ms",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = AppTextSecondary
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Icon(
                            imageVector = Icons.Default.NetworkCell,
                            contentDescription = "Signal",
                            tint = when {
                                server.pingMs < 45 -> VpnConnectedGreen
                                server.pingMs < 90 -> VpnAmber
                                else -> AppTextMuted
                            },
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        IconButton(
                            onClick = { viewModel.toggleFavorite(server) },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = if (server.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                contentDescription = "Favorite",
                                tint = if (server.isFavorite) VpnAmber else AppTextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = null,
                            tint = AppTextMuted,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
