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
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.model.VpnConnectionState
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberRed
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun VpnPowerButton(
    connectionState: VpnConnectionState,
    onToggleConnect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "vpn_pulse")

    // Pulse wave 1
    val pulseScale1 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_scale_1"
    )
    val pulseAlpha1 by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_alpha_1"
    )

    // Pulse wave 2 (offset)
    val pulseScale2 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, delayMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_scale_2"
    )
    val pulseAlpha2 by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, delayMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_alpha_2"
    )

    // Continuous rotation for connecting spinner ring
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring_rotation"
    )

    // Colors according to state
    val mainColor by animateColorAsState(
        targetValue = when (connectionState) {
            VpnConnectionState.CONNECTED -> CyberEmerald
            VpnConnectionState.CONNECTING -> CyberAmber
            VpnConnectionState.DISCONNECTING -> CyberRed
            VpnConnectionState.DISCONNECTED -> CyberCyan
        },
        label = "button_color"
    )

    val buttonBackground = Brush.radialGradient(
        colors = when (connectionState) {
            VpnConnectionState.CONNECTED -> listOf(
                Color(0xFF0F3A2C),
                Color(0xFF071C17),
                CyberSurface
            )
            VpnConnectionState.CONNECTING -> listOf(
                Color(0xFF382B0E),
                Color(0xFF201807),
                CyberSurface
            )
            VpnConnectionState.DISCONNECTING -> listOf(
                Color(0xFF3A1212),
                Color(0xFF200909),
                CyberSurface
            )
            VpnConnectionState.DISCONNECTED -> listOf(
                Color(0xFF13233E),
                Color(0xFF0D1728),
                CyberSurface
            )
        }
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(240.dp)
        ) {
            // Pulse rings for active states
            if (connectionState == VpnConnectionState.CONNECTED || connectionState == VpnConnectionState.CONNECTING) {
                Canvas(
                    modifier = Modifier
                        .size(160.dp)
                        .scale(pulseScale1)
                ) {
                    drawCircle(
                        color = mainColor.copy(alpha = pulseAlpha1),
                        radius = size.minDimension / 2
                    )
                }

                Canvas(
                    modifier = Modifier
                        .size(160.dp)
                        .scale(pulseScale2)
                ) {
                    drawCircle(
                        color = mainColor.copy(alpha = pulseAlpha2),
                        radius = size.minDimension / 2
                    )
                }
            }

            // Outer decorative circular border with cyber dashes
            Canvas(modifier = Modifier.size(190.dp)) {
                drawCircle(
                    color = CyberBorder.copy(alpha = 0.6f),
                    radius = (size.minDimension / 2),
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }

            // Connecting rotating sweep
            if (connectionState == VpnConnectionState.CONNECTING) {
                Canvas(modifier = Modifier.size(190.dp)) {
                    drawArc(
                        color = CyberAmber,
                        startAngle = ringRotation,
                        sweepAngle = 90f,
                        useCenter = false,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }

            // Main Clickable Circular Button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(150.dp)
                    .testTag("vpn_power_button")
                    .shadow(
                        elevation = if (connectionState == VpnConnectionState.CONNECTED) 18.dp else 8.dp,
                        shape = CircleShape,
                        spotColor = mainColor
                    )
                    .clip(CircleShape)
                    .background(buttonBackground)
                    .border(
                        width = 3.dp,
                        color = mainColor.copy(alpha = if (connectionState == VpnConnectionState.CONNECTED) 0.9f else 0.5f),
                        shape = CircleShape
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true, color = mainColor),
                        onClick = onToggleConnect
                    )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = when (connectionState) {
                            VpnConnectionState.CONNECTED -> Icons.Default.Shield
                            VpnConnectionState.CONNECTING -> Icons.Default.VpnKey
                            else -> Icons.Default.PowerSettingsNew
                        },
                        contentDescription = "زر اتصال VPN",
                        tint = mainColor,
                        modifier = Modifier.size(54.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = when (connectionState) {
                            VpnConnectionState.CONNECTED -> "متصل"
                            VpnConnectionState.CONNECTING -> "اتصال…"
                            VpnConnectionState.DISCONNECTING -> "قطع…"
                            VpnConnectionState.DISCONNECTED -> "اتصال"
                        },
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // State subtitle
        Text(
            text = when (connectionState) {
                VpnConnectionState.CONNECTED -> "النفق مشفر ومحمي بالكامل 🔒"
                VpnConnectionState.CONNECTING -> "جاري إنشاء نفق OpenVPN الآمن…"
                VpnConnectionState.DISCONNECTING -> "جاري إنهاء الاتصال بأمان…"
                VpnConnectionState.DISCONNECTED -> "انقر فوق الزر لتأمين اتصالك فوراً"
            },
            color = when (connectionState) {
                VpnConnectionState.CONNECTED -> CyberEmerald
                VpnConnectionState.CONNECTING -> CyberAmber
                VpnConnectionState.DISCONNECTING -> CyberRed
                VpnConnectionState.DISCONNECTED -> TextSecondary
            },
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
