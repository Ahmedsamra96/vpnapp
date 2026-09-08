package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.localization.LocalAppStrings
import com.example.ui.theme.AppSurfaceLight
import com.example.ui.theme.AppSurfaceVariantLight
import com.example.ui.theme.AppTextPrimary
import com.example.ui.theme.AppTextSecondary
import com.example.ui.theme.VpnConnectedGreen
import com.example.ui.theme.VpnPrimaryBlue
import com.example.ui.theme.VpnPrimaryBlueSoft

@Composable
fun WelcomeOnboardingDialog(
    onDismiss: () -> Unit,
    onViewPrivacyPolicy: () -> Unit = {}
) {
    val strings = LocalAppStrings.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = AppSurfaceLight),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .shadow(elevation = 18.dp, shape = RoundedCornerShape(26.dp))
                .testTag("welcome_onboarding_dialog")
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(26.dp)
            ) {
                // Shield Hero Brand Icon
                AppBrandLogo(
                    size = 90.dp,
                    showContainer = true,
                    showGlow = true
                )

                Spacer(modifier = Modifier.height(18.dp))

                // App Title
                Text(
                    text = strings.onboardingTitle,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppTextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Subtitle
                Text(
                    text = strings.onboardingSubtitle,
                    fontSize = 14.sp,
                    color = AppTextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Feature 1: Free access
                OnboardingFeatureRow(
                    icon = Icons.Default.Bolt,
                    iconColor = VpnPrimaryBlue,
                    title = strings.freeFeatureTitle,
                    subtitle = strings.freeFeatureSubtitle
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Feature 2: No registration
                OnboardingFeatureRow(
                    icon = Icons.Default.CheckCircle,
                    iconColor = VpnConnectedGreen,
                    title = strings.noRegFeatureTitle,
                    subtitle = strings.noRegFeatureSubtitle
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Feature 3: Secure connection
                OnboardingFeatureRow(
                    icon = Icons.Default.Lock,
                    iconColor = VpnPrimaryBlue,
                    title = strings.secureFeatureTitle,
                    subtitle = strings.secureFeatureSubtitle
                )

                Spacer(modifier = Modifier.height(22.dp))

                // Privacy Policy Link
                Text(
                    text = strings.onboardingDisclaimer,
                    fontSize = 12.sp,
                    color = AppTextSecondary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = strings.viewPrivacyPolicyLink,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = VpnPrimaryBlue,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .clickable(onClick = onViewPrivacyPolicy)
                        .padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Continue button
                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VpnPrimaryBlue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("onboarding_continue_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
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
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingFeatureRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(AppSurfaceVariantLight)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = AppTextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = AppTextSecondary
            )
        }
    }
}
