package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.localization.LocalAppStrings
import com.example.model.LogLevel
import com.example.model.VpnLogEntry
import com.example.ui.theme.ThemeColors
import com.example.ui.theme.VpnAmber
import com.example.ui.theme.VpnConnectedGreen
import com.example.ui.theme.VpnDisconnectedRed
import com.example.ui.theme.VpnPrimaryBlue
import com.example.vpn.VpnStateRepository

@Composable
fun VpnLogsDialog(
    logs: List<VpnLogEntry>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current
    val listState = rememberLazyListState()

    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            listState.animateScrollToItem(logs.size - 1)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .height(480.dp)
                .border(1.dp, androidx.compose.material3.MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Dialog Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Terminal,
                            contentDescription = null,
                            tint = VpnPrimaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strings.logsDialogTitle,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row {
                        IconButton(onClick = {
                            val text = logs.joinToString("\n") { "[${it.timestamp}] ${it.message}" }
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("VPN Logs", text))
                            Toast.makeText(context, strings.logCopiedToClipboard, Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(
                                Icons.Default.ContentCopy,
                                contentDescription = strings.copyButton,
                                tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(onClick = { VpnStateRepository.clearLogs() }) {
                            Icon(
                                Icons.Default.DeleteOutline,
                                contentDescription = strings.clearButton,
                                tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
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
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Terminal Box
                val terminalBg = if (ThemeColors.isDark) Color(0xFF070E24) else Color(0xFF0F172A)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(terminalBg)
                        .border(1.dp, androidx.compose.material3.MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    if (logs.isEmpty()) {
                        Text(
                            text = strings.noLogsAvailable,
                            color = Color(0xFF64748B),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            state = listState,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(logs) { entry ->
                                val logColor = when (entry.level) {
                                    LogLevel.SUCCESS -> Color(0xFF4ADE80)
                                    LogLevel.WARNING -> Color(0xFFFBBF24)
                                    LogLevel.ERROR -> Color(0xFFF87171)
                                    LogLevel.INFO -> Color(0xFF38BDF8)
                                }

                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "[${entry.timestamp}] ",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = entry.message,
                                        color = logColor,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
