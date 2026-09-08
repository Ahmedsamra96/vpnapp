package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R

/**
 * High-fidelity 3D Globe-Shield App Brand Logo.
 * Matches the official app icon: glossy blue security shield with a glowing Earth globe
 * and an elegant dynamic white ribbon swoosh wrapped around it.
 */
@Composable
fun AppBrandLogo(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    showContainer: Boolean = false,
    showGlow: Boolean = false,
    contentDescription: String? = "FastVPN Logo"
) {
    val infiniteTransition = rememberInfiniteTransition(label = "logo_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        if (showGlow) {
            Box(
                modifier = Modifier
                    .fillMaxSize(0.9f)
                    .clip(RoundedCornerShape(size * 0.28f))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF38BDF8).copy(alpha = glowAlpha),
                                Color(0xFF0066FF).copy(alpha = glowAlpha * 0.4f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        if (showContainer) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .shadow(
                        elevation = (size.value * 0.08f).dp,
                        shape = RoundedCornerShape(size * 0.25f),
                        spotColor = Color(0xFF0066FF).copy(alpha = 0.3f)
                    )
                    .clip(RoundedCornerShape(size * 0.25f))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFF8FCFF),
                                Color(0xFFE0F2FE)
                            )
                        )
                    )
                    .padding(size * 0.08f)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_app_shield_logo),
                    contentDescription = contentDescription,
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else {
            Image(
                painter = painterResource(id = R.drawable.ic_app_shield_logo),
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
