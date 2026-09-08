package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.LocalAppStrings
import com.example.model.VpnConnectionState
import com.example.ui.theme.AppBorderLight
import com.example.ui.theme.AppTextMuted
import com.example.ui.theme.AppTextPrimary
import com.example.ui.theme.AppTextSecondary
import com.example.ui.theme.VpnConnectedGreen
import com.example.ui.theme.VpnDisconnectedRed
import com.example.ui.theme.VpnPrimaryBlue
import com.example.ui.theme.VpnPrimaryBlueDark
import com.example.ui.theme.VpnPrimaryBlueLight
import com.example.ui.theme.VpnPrimaryBlueSoft

@Composable
fun VpnPowerButton(
    connectionState: VpnConnectionState,
    onToggleConnect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isConnected = connectionState == VpnConnectionState.CONNECTED
    val isConnecting = connectionState == VpnConnectionState.CONNECTING

    val infiniteTransition = rememberInfiniteTransition(label = "power_pulse")

    // Pulse animation when connected or connecting
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_scale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_alpha"
    )

    // Spinner rotation while connecting
    val spinRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin_rotation"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(220.dp)
        ) {
            // Pulse wave for connected state
            if (isConnected || isConnecting) {
                Canvas(
                    modifier = Modifier
                        .size(175.dp)
                        .scale(pulseScale)
                ) {
                    drawCircle(
                        color = (if (isConnected) VpnPrimaryBlue else VpnPrimaryBlueLight).copy(alpha = pulseAlpha),
                        radius = size.minDimension / 2
                    )
                }
            }

            // Outer Concentric Decorative Ring
            Canvas(modifier = Modifier.size(195.dp)) {
                drawCircle(
                    color = if (isConnected) Color(0xFFDBEAFE) else Color(0xFFEDF2F7),
                    radius = size.minDimension / 2
                )
            }

            // Middle Concentric Ring
            Canvas(modifier = Modifier.size(165.dp)) {
                drawCircle(
                    color = if (isConnected) Color(0xFFBFDBFE) else Color(0xFFF8FAFC),
                    radius = size.minDimension / 2
                )
            }

            // Connecting progress arc
            if (isConnecting) {
                Canvas(modifier = Modifier.size(165.dp)) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(
                                VpnPrimaryBlue.copy(alpha = 0.1f),
                                VpnPrimaryBlue
                            )
                        ),
                        startAngle = spinRotation,
                        sweepAngle = 120f,
                        useCenter = false,
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }

            // Main Core Power Button
            val buttonBrush = if (isConnected) {
                Brush.verticalGradient(
                    colors = listOf(
                        VpnPrimaryBlueLight,
                        VpnPrimaryBlue,
                        VpnPrimaryBlueDark
                    )
                )
            } else if (isConnecting) {
                Brush.verticalGradient(
                    colors = listOf(
                        VpnPrimaryBlueLight,
                        VpnPrimaryBlue
                    )
                )
            } else {
                Brush.verticalGradient(
                    colors = listOf(
                        Color.White,
                        Color(0xFFF8FAFC)
                    )
                )
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(136.dp)
                    .testTag("vpn_power_button")
                    .shadow(
                        elevation = if (isConnected) 16.dp else 6.dp,
                        shape = CircleShape,
                        spotColor = if (isConnected) VpnPrimaryBlue else Color(0x22000000)
                    )
                    .clip(CircleShape)
                    .background(buttonBrush)
                    .border(
                        width = if (isConnected) 0.dp else 1.5.dp,
                        color = if (isConnected) Color.Transparent else AppBorderLight,
                        shape = CircleShape
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true, color = if (isConnected) Color.White else VpnPrimaryBlue),
                        onClick = onToggleConnect
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.PowerSettingsNew,
                    contentDescription = "VPN Power Button",
                    tint = if (isConnected || isConnecting) Color.White else Color(0xFF475569),
                    modifier = Modifier.size(54.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        val strings = LocalAppStrings.current

        // Status Title
        Text(
            text = when (connectionState) {
                VpnConnectionState.CONNECTED -> strings.statusConnected
                VpnConnectionState.CONNECTING -> strings.statusConnecting
                VpnConnectionState.DISCONNECTING -> strings.tapToCancel
                VpnConnectionState.DISCONNECTED -> strings.statusDisconnected
            },
            color = when (connectionState) {
                VpnConnectionState.CONNECTED -> VpnConnectedGreen
                VpnConnectionState.CONNECTING -> VpnPrimaryBlue
                VpnConnectionState.DISCONNECTING -> VpnDisconnectedRed
                VpnConnectionState.DISCONNECTED -> AppTextPrimary
            },
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.testTag("connection_state_label")
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Status Subtitle
        Text(
            text = when (connectionState) {
                VpnConnectionState.CONNECTED -> strings.connectionProtected
                VpnConnectionState.CONNECTING -> strings.statusConnecting
                VpnConnectionState.DISCONNECTING -> strings.tapToCancel
                VpnConnectionState.DISCONNECTED -> strings.notProtected
            },
            color = AppTextSecondary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        )
    }
}
