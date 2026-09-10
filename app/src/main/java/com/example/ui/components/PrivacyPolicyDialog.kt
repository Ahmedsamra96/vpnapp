package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NoAccounts
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.localization.LocalAppStrings
import com.example.ui.theme.AppBgLight
import com.example.ui.theme.AppBorderLight
import com.example.ui.theme.AppSurfaceLight
import com.example.ui.theme.AppTextPrimary
import com.example.ui.theme.AppTextSecondary
import com.example.ui.theme.VpnConnectedGreen
import com.example.ui.theme.VpnPrimaryBlue
import com.example.ui.theme.VpnPrimaryBlueSoft

@Composable
fun PrivacyPolicyDialog(
    onDismiss: () -> Unit
) {
    val strings = LocalAppStrings.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = AppBgLight,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxSize(0.92f)
                .shadow(20.dp, RoundedCornerShape(24.dp))
                .border(1.dp, AppBorderLight, RoundedCornerShape(24.dp))
                .testTag("privacy_policy_dialog")
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Top App Bar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AppSurfaceLight)
                        .padding(horizontal = 18.dp, vertical = 14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AppBrandLogo(
                            size = 40.dp,
                            showContainer = true,
                            showGlow = false
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = strings.privacyPolicyTitle,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppTextPrimary
                            )
                            Text(
                                text = strings.privacyPolicySubtitle,
                                fontSize = 12.sp,
                                color = AppTextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_privacy_policy")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = strings.closeBtn,
                            tint = AppTextSecondary
                        )
                    }
                }

                HorizontalDivider(color = AppBorderLight)

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    // Verified Badge
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = VpnConnectedGreen.copy(alpha = 0.08f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, VpnConnectedGreen.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.GppGood,
                                contentDescription = null,
                                tint = VpnConnectedGreen,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = strings.privacyVerifiedBadgeTitle,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VpnConnectedGreen
                                )
                                Text(
                                    text = strings.privacyVerifiedBadgeSubtitle,
                                    fontSize = 11.5.sp,
                                    color = AppTextSecondary,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    PolicySectionCard(
                        icon = Icons.Default.Shield,
                        title = strings.privacySection1Title,
                        content = strings.privacySection1Content
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    PolicySectionCard(
                        icon = Icons.Default.VisibilityOff,
                        title = strings.privacySection2Title,
                        content = strings.privacySection2Content
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    PolicySectionCard(
                        icon = Icons.Default.Lock,
                        title = strings.privacySection3Title,
                        content = strings.privacySection3Content
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    PolicySectionCard(
                        icon = Icons.Default.Security,
                        title = strings.privacySection4Title,
                        content = strings.privacySection4Content
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    PolicySectionCard(
                        icon = Icons.Default.NoAccounts,
                        title = strings.privacySection5Title,
                        content = strings.privacySection5Content
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    PolicySectionCard(
                        icon = Icons.Default.Policy,
                        title = strings.privacySection6Title,
                        content = strings.privacySection6Content
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Bottom Action
                Surface(
                    color = AppSurfaceLight,
                    tonalElevation = 4.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, AppBorderLight)
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VpnPrimaryBlue),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("accept_privacy_policy_button")
                    ) {
                        Text(
                            text = strings.privacyAcceptButton,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PolicySectionCard(
    icon: ImageVector,
    title: String,
    content: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppSurfaceLight),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, AppBorderLight, RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(VpnPrimaryBlueSoft)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = VpnPrimaryBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppTextPrimary,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = content,
                fontSize = 12.5.sp,
                color = AppTextSecondary,
                lineHeight = 19.sp
            )
        }
    }
}
