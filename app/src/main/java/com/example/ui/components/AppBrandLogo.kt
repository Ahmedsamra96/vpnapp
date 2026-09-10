package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R

/**
 * Adaptive Ruvon VPN Brand Logo.
 * Uses the Dark Mode Shield Icon with glowing neon lightning bolt when in Dark Theme,
 * and the clean Crystal Sapphire Shield with vibrant blue-cyan lightning bolt in Light Theme.
 */
@Composable
fun AppBrandLogo(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    showContainer: Boolean = false,
    showGlow: Boolean = false,
    contentDescription: String? = "Ruvon VPN Logo"
) {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val logoDrawableId = R.drawable.ic_ruvon_transparent

    val infiniteTransition = rememberInfiniteTransition(label = "logo_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = if (isDark) 0.35f else 0.20f,
        targetValue = if (isDark) 0.75f else 0.50f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    val glowColors = if (isDark) {
        listOf(
            Color(0xFF00F0FF).copy(alpha = glowAlpha),
            Color(0xFF0055FF).copy(alpha = glowAlpha * 0.35f),
            Color.Transparent
        )
    } else {
        listOf(
            Color(0xFF38BDF8).copy(alpha = glowAlpha),
            Color(0xFF0284C7).copy(alpha = glowAlpha * 0.25f),
            Color.Transparent
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        if (showGlow) {
            Box(
                modifier = Modifier
                    .fillMaxSize(1.1f)
                    .clip(RoundedCornerShape(size * 0.32f))
                    .background(Brush.radialGradient(colors = glowColors))
            )
        }

        if (showContainer) {
            val containerBg = if (isDark) {
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF0B1626),
                        Color(0xFF060D18),
                        Color(0xFF03070E)
                    )
                )
            } else {
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF),
                        Color(0xFFF0F7FF),
                        Color(0xFFE0F2FE)
                    )
                )
            }

            val containerBorder = if (isDark) {
                Brush.linearGradient(
                    listOf(
                        Color(0xFF00E5FF).copy(alpha = 0.6f),
                        Color(0xFF0055FF).copy(alpha = 0.3f)
                    )
                )
            } else {
                Brush.linearGradient(
                    listOf(
                        Color(0xFFBAE6FD),
                        Color(0xFF7DD3FC).copy(alpha = 0.5f)
                    )
                )
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .shadow(
                        elevation = (size.value * 0.08f).dp,
                        shape = RoundedCornerShape(size * 0.26f),
                        spotColor = if (isDark) Color(0xFF00F0FF).copy(alpha = 0.30f) else Color(0xFF0072FF).copy(alpha = 0.18f),
                        ambientColor = if (isDark) Color(0xFF0055FF).copy(alpha = 0.20f) else Color(0xFF00C6FF).copy(alpha = 0.10f)
                    )
                    .clip(RoundedCornerShape(size * 0.26f))
                    .background(containerBg)
                    .border(
                        width = 1.dp,
                        brush = containerBorder,
                        shape = RoundedCornerShape(size * 0.26f)
                    )
                    .padding(size * 0.07f)
            ) {
                Image(
                    painter = painterResource(id = logoDrawableId),
                    contentDescription = contentDescription,
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else {
            Image(
                painter = painterResource(id = logoDrawableId),
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
