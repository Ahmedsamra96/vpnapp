package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.localization.LocalAppStrings
import com.example.ui.theme.VpnPrimaryBlue
import com.example.ui.theme.VpnPrimaryBlueDark
import kotlinx.coroutines.delay

/**
 * Modern, High-End Animated Splash Screen.
 * Features:
 * - Fluid entrance with scale, bounce and alpha animation
 * - Radial pulsing energy aura behind the central shield
 * - Elegant typography & slogan fade-in
 * - Animated sleek 3-dot loading indicator
 * - Polished transition callback when ready
 */
@Composable
fun AppSplashScreen(
    onSplashFinished: () -> Unit
) {
    val strings = LocalAppStrings.current
    val isDark = isSystemInDarkTheme()

    val scale = remember { Animatable(0.72f) }
    val alpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }

    // Pulsing shield aura animation
    val infiniteTransition = rememberInfiniteTransition(label = "splash_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    LaunchedEffect(Unit) {
        // Run staggered animations
        scale.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(750, easing = FastOutSlowInEasing)
        )
        alpha.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(500, easing = LinearEasing)
        )
        textAlpha.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(600, easing = FastOutSlowInEasing)
        )
        // Keep on screen briefly for smooth branding feel
        delay(1400)
        onSplashFinished()
    }

    val bgGradient = if (isDark) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF070E24),
                Color(0xFF030712),
                Color(0xFF02040A)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFFFFFF),
                Color(0xFFF4F8FD),
                Color(0xFFEAF2FB)
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
            .testTag("app_splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Decorative background subtle light orb
        Box(
            modifier = Modifier
                .size(320.dp)
                .scale(pulseScale)
                .alpha(pulseAlpha)
                .blur(48.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF00E5FF).copy(alpha = 0.6f),
                            Color(0xFF2563EB).copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            // Main Animated Shield Icon container
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(130.dp)
                    .scale(scale.value)
                    .alpha(alpha.value)
            ) {
                // Outer glow shadow
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .shadow(
                            elevation = 28.dp,
                            shape = RoundedCornerShape(36.dp),
                            spotColor = Color(0xFF00E5FF).copy(alpha = 0.45f),
                            ambientColor = Color(0xFF2563EB).copy(alpha = 0.25f)
                        )
                        .clip(RoundedCornerShape(36.dp))
                        .background(
                            Brush.linearGradient(
                                colors = if (isDark) {
                                    listOf(Color(0xFF0F1E3D), Color(0xFF081226))
                                } else {
                                    listOf(Color(0xFFFFFFFF), Color(0xFFF0F7FF))
                                }
                            )
                        )
                        .padding(18.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_ruvon_app),
                        contentDescription = "Ruvon VPN Icon",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // App Brand Name with smooth fade-in
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(textAlpha.value)
            ) {
                Text(
                    text = strings.appTitle,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp,
                    color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = strings.freeAndFastBadge,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.2.sp,
                    color = if (isDark) Color(0xFF38BDF8) else VpnPrimaryBlue
                )
            }
        }

        // Bottom Animated Dots / Status
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 54.dp)
                .alpha(textAlpha.value)
        ) {
            AnimatedLoadingDots()

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Fast • Simple • Secure",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8),
                letterSpacing = 1.2.sp
            )
        }
    }
}

@Composable
private fun AnimatedLoadingDots() {
    val infiniteTransition = rememberInfiniteTransition(label = "dots_transition")
    
    val dot1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )
    val dot2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )
    val dot3Alpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .alpha(dot1Alpha)
                .background(VpnPrimaryBlue, CircleShape)
        )
        Box(
            modifier = Modifier
                .size(7.dp)
                .alpha(dot2Alpha)
                .background(VpnPrimaryBlue, CircleShape)
        )
        Box(
            modifier = Modifier
                .size(7.dp)
                .alpha(dot3Alpha)
                .background(VpnPrimaryBlue, CircleShape)
        )
    }
}
