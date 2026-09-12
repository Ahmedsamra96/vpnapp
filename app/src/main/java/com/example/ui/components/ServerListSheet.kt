package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppLanguage
import com.example.localization.LocalAppLanguage
import com.example.localization.LocalAppStrings
import com.example.model.VpnServer
import com.example.ui.ServerFilterCategory
import com.example.ui.theme.ThemeColors
import com.example.ui.theme.VpnAmber
import com.example.ui.theme.VpnConnectedGreen
import com.example.ui.theme.VpnDisconnectedRed
import com.example.ui.theme.VpnPrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerListSheet(
    sheetState: SheetState,
    servers: List<VpnServer>,
    selectedServer: VpnServer,
    searchQuery: String,
    selectedCategory: ServerFilterCategory,
    isLoading: Boolean,
    onSearchChange: (String) -> Unit,
    onCategoryChange: (ServerFilterCategory) -> Unit,
    onSelectServer: (VpnServer) -> Unit,
    onToggleFavorite: (VpnServer) -> Unit,
    onRefreshServers: () -> Unit,
    onDismiss: () -> Unit
) {
    val strings = LocalAppStrings.current
    val isArabic = LocalAppLanguage.current == AppLanguage.ARABIC
    val infiniteTransition = rememberInfiniteTransition(label = "refresh_spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing)
        ),
        label = "spin"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(44.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(androidx.compose.material3.MaterialTheme.colorScheme.outline)
            )
        },
        modifier = Modifier.fillMaxHeight(0.92f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
        ) {
            // Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = strings.serversTitle,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = strings.serversSubtitle,
                        color = VpnConnectedGreen,
                        fontSize = 12.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onRefreshServers,
                        enabled = !isLoading,
                        modifier = Modifier.testTag("refresh_servers_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = strings.refreshServers,
                            tint = VpnPrimaryBlue,
                            modifier = Modifier.rotate(if (isLoading) rotation else 0f)
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = strings.closeBtn,
                            tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = {
                    Text(
                        strings.searchPlaceholder,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = strings.clearButton,
                                tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant,
                    focusedBorderColor = VpnPrimaryBlue,
                    unfocusedBorderColor = androidx.compose.material3.MaterialTheme.colorScheme.outline,
                    focusedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("server_search_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Filter Tabs
            TabRow(
                selectedTabIndex = selectedCategory.ordinal,
                containerColor = Color.Transparent,
                contentColor = VpnPrimaryBlue,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedCategory.ordinal]),
                        color = VpnPrimaryBlue
                    )
                },
                divider = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(androidx.compose.material3.MaterialTheme.colorScheme.outline)
                    )
                }
            ) {
                Tab(
                    selected = selectedCategory == ServerFilterCategory.ALL,
                    onClick = { onCategoryChange(ServerFilterCategory.ALL) },
                    text = {
                        Text(
                            text = "${strings.filterAll} (${servers.size})",
                            fontSize = 13.sp,
                            fontWeight = if (selectedCategory == ServerFilterCategory.ALL) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedCategory == ServerFilterCategory.FASTEST,
                    onClick = { onCategoryChange(ServerFilterCategory.FASTEST) },
                    text = {
                        Text(
                            text = "${strings.filterFast} ⚡",
                            fontSize = 13.sp,
                            fontWeight = if (selectedCategory == ServerFilterCategory.FASTEST) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedCategory == ServerFilterCategory.FAVORITES,
                    onClick = { onCategoryChange(ServerFilterCategory.FAVORITES) },
                    text = {
                        Text(
                            text = strings.filterFavorites,
                            fontSize = 13.sp,
                            fontWeight = if (selectedCategory == ServerFilterCategory.FAVORITES) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Server Items List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = 16.dp)
            ) {
                if (servers.isEmpty()) {
                    item {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp)
                        ) {
                            Text(
                                text = if (selectedCategory == ServerFilterCategory.FAVORITES) strings.noFavoritesFound else strings.noServersFound,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    items(servers, key = { it.id }) { server ->
                        ServerListItem(
                            server = server,
                            isArabic = isArabic,
                            strings = strings,
                            isSelected = selectedServer.id == server.id,
                            onSelect = {
                                onSelectServer(server)
                                onDismiss()
                            },
                            onToggleFavorite = { onToggleFavorite(server) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ServerListItem(
    server: VpnServer,
    isArabic: Boolean,
    strings: com.example.localization.AppStrings,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val pingColor = when {
        server.pingMs < 40 -> VpnConnectedGreen
        server.pingMs < 90 -> VpnAmber
        else -> VpnDisconnectedRed
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isSelected) {
                    androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant
                } else {
                    androidx.compose.material3.MaterialTheme.colorScheme.surface
                }
            )
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) VpnPrimaryBlue else androidx.compose.material3.MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onSelect)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // Flag Icon
            Text(
                text = server.flagEmoji,
                fontSize = 26.sp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = server.getDisplayName(isArabic, strings.optimalServer),
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (server.isOptimal) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(VpnPrimaryBlue.copy(alpha = 0.15f))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                strings.autoServerBadge,
                                color = VpnPrimaryBlue,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${server.city} • ${server.ip}",
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }
        }

        // Stats and Actions
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ping
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(pingColor.copy(alpha = 0.12f))
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(pingColor)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${server.pingMs} ms",
                    color = pingColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Favorite star button
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (server.isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                    contentDescription = strings.favoriteServerAction,
                    tint = if (server.isFavorite) VpnAmber else androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }

            if (isSelected) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = strings.selectedServerAction,
                    tint = VpnPrimaryBlue,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
