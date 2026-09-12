package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.localization.LocalAppStrings
import com.example.ui.theme.VpnConnectedGreen
import com.example.ui.theme.VpnPrimaryBlue
import kotlinx.coroutines.delay

/**
 * Professional, modern Splash Screen:
 * - High-definition Ruvon brand emblem with subtle pulse & scale animation
 * - Glowing radial ring effect
 * - App title and security tagline
 * - Elegant status loading indicator with "Securing Your Connection" badge
 */
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val strings = LocalAppStrings.current
    val scale = remember { Animatable(0.7f) }
    val alpha = remember { Animatable(0f) }
    val pulse = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        // Entrance animation
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
        )
    }

    LaunchedEffect(Unit) {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 500)
        )
        // Gentle pulse loop
        pulse.animateTo(
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    LaunchedEffect(Unit) {
        // Display splash for 1.8 seconds then transition smoothly
        delay(1800)
        onSplashFinished()
    }

    val bgGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF070E24),
            Color(0xFF0B1736),
            Color(0xFF030712)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // Ambient glowing circle container behind the logo
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(160.dp)
                    .scale(scale.value * pulse.value)
                    .alpha(alpha.value)
            ) {
                // Outer glow circle
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    VpnPrimaryBlue.copy(alpha = 0.35f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // App Brand Logo with rounded corners
                Image(
                    painter = painterResource(id = R.drawable.ic_ruvon_app),
                    contentDescription = "Ruvon VPN Logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(112.dp)
                        .clip(RoundedCornerShape(percent = 28))
                        .border(1.5.dp, Color(0xFF38BDF8).copy(alpha = 0.4f), RoundedCornerShape(percent = 28))
                        .shadow(16.dp, RoundedCornerShape(percent = 28), spotColor = VpnPrimaryBlue)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // App Name with high contrast and modern typography
            Text(
                text = strings.appTitle,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFF8FAFC),
                letterSpacing = 1.sp,
                modifier = Modifier.alpha(alpha.value)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Security Tagline
            Text(
                text = strings.onboardingSubtitle,
                fontSize = 14.sp,
                color = Color(0xFF94A3B8),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.alpha(alpha.value)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Minimalist status indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF131D33))
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .alpha(alpha.value)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = Color(0xFF38BDF8)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = VpnConnectedGreen,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = strings.secureFeatureTitle,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFE2E8F0)
                )
            }
        }
    }
}
