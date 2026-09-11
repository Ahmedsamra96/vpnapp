package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.localization.LocalAppStrings

/**
 * Onboarding Tour & Welcome Screen in Dark Theme:
 * - Dedicated Dark Background (#070E24 -> #030712)
 * - Centered Shield emblem with cyber cyan & blue glow
 * - App Title & Tagline in high-contrast crisp text
 * - 3 Feature Cards (Free access, No registration, Secure connection) in Dark Surface style
 * - Prominent "Continue ->" button
 * - 3 Page Indicator dots
 */
@Composable
fun OnboardingTourScreen(
    onContinue: () -> Unit,
    onViewPrivacyPolicy: () -> Unit = {}
) {
    val strings = LocalAppStrings.current

    // Dark Mode Theme Palette
    val bgGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF070E24),
            Color(0xFF030712),
            Color(0xFF02040A)
        )
    )
    val cardBg = Color(0xFF131D33)
    val textPrimary = Color(0xFFF8FAFC)
    val textSecondary = Color(0xFF94A3B8)
    val accentBlue = Color(0xFF2563EB)
    val accentCyan = Color(0xFF00E5FF)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
            .testTag("onboarding_tour_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 36.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Center Content Block
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Shield Emblem with Dark Container & Cyan/Blue Glow
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(116.dp)
                        .shadow(
                            elevation = 24.dp,
                            shape = RoundedCornerShape(34.dp),
                            spotColor = accentCyan.copy(alpha = 0.40f),
                            ambientColor = accentBlue.copy(alpha = 0.25f)
                        )
                        .clip(RoundedCornerShape(34.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF0F1E3D), Color(0xFF081226))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_ruvon_app),
                        contentDescription = "Shield Emblem",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // App Title
                Text(
                    text = strings.appTitle,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textPrimary,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Subtitle
                Text(
                    text = strings.onboardingSubtitle,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    textAlign = TextAlign.Center,
                    color = textSecondary,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Feature List (Cards in dark mode)
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Feature 1: Free access
                    DarkTourFeatureRow(
                        icon = Icons.Default.Bolt,
                        iconTint = Color(0xFF60A5FA),
                        iconBg = Color(0xFF1E3A8A).copy(alpha = 0.4f),
                        title = strings.freeFeatureTitle,
                        subtitle = strings.freeFeatureSubtitle,
                        cardBg = cardBg,
                        textColor = textPrimary,
                        subTextColor = textSecondary
                    )

                    // Feature 2: No registration
                    DarkTourFeatureRow(
                        icon = Icons.Default.Person,
                        iconTint = Color(0xFF38BDF8),
                        iconBg = Color(0xFF0369A1).copy(alpha = 0.35f),
                        title = strings.noRegFeatureTitle,
                        subtitle = strings.noRegFeatureSubtitle,
                        cardBg = cardBg,
                        textColor = textPrimary,
                        subTextColor = textSecondary
                    )

                    // Feature 3: Secure connection
                    DarkTourFeatureRow(
                        icon = Icons.Default.Lock,
                        iconTint = Color(0xFF60A5FA),
                        iconBg = Color(0xFF1E3A8A).copy(alpha = 0.4f),
                        title = strings.secureFeatureTitle,
                        subtitle = strings.secureFeatureSubtitle,
                        cardBg = cardBg,
                        textColor = textPrimary,
                        subTextColor = textSecondary
                    )
                }
            }

            // Bottom Section: Continue Button + 3 Pagination Dots
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Vibrant Blue Continue Button
                Button(
                    onClick = onContinue,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("tour_continue_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = strings.onboardingContinueBtn,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 3 Pagination Indicator Dots (Active on first dot)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(0xFF38BDF8), CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(Color(0xFF334155), CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(Color(0xFF334155), CircleShape)
                    )
                }
            }
        }
    }
}

@Composable
private fun DarkTourFeatureRow(
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    title: String,
    subtitle: String,
    cardBg: Color,
    textColor: Color,
    subTextColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(42.dp)
                .background(iconBg, CircleShape)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = subTextColor
            )
        }
    }
}
