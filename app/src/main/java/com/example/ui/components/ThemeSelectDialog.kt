package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.model.AppThemeMode
import com.example.ui.theme.VpnPrimaryBlue
import com.example.ui.theme.VpnPrimaryBlueSoft

@Composable
fun ThemeSelectDialog(
    currentTheme: AppThemeMode,
    onThemeSelected: (AppThemeMode) -> Unit,
    onDismiss: () -> Unit
) {
    val strings = LocalAppStrings.current
    var selectedMode by remember { mutableStateOf(currentTheme) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .shadow(20.dp, RoundedCornerShape(26.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(26.dp))
                .testTag("theme_select_dialog")
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(VpnPrimaryBlueSoft)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                tint = VpnPrimaryBlue,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = strings.chooseThemeTitle,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = strings.chooseThemeSubtitle,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = strings.closeBtn,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline)

                Column(modifier = Modifier.padding(20.dp)) {
                    // Option 1: Follow System
                    ThemeOptionCard(
                        icon = Icons.Default.BrightnessAuto,
                        title = strings.appearanceSystem,
                        subtitle = strings.systemDefaultSubtitle,
                        isSelected = selectedMode == AppThemeMode.SYSTEM,
                        onSelect = { selectedMode = AppThemeMode.SYSTEM },
                        testTag = "theme_option_system"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Option 2: Light Mode
                    ThemeOptionCard(
                        icon = Icons.Default.LightMode,
                        title = strings.appearanceLight,
                        subtitle = "Classic bright clean style",
                        isSelected = selectedMode == AppThemeMode.LIGHT,
                        onSelect = { selectedMode = AppThemeMode.LIGHT },
                        testTag = "theme_option_light"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Option 3: Dark Mode
                    ThemeOptionCard(
                        icon = Icons.Default.DarkMode,
                        title = strings.appearanceDark,
                        subtitle = "Dark cyber canvas, easy on eyes",
                        isSelected = selectedMode == AppThemeMode.DARK,
                        onSelect = { selectedMode = AppThemeMode.DARK },
                        testTag = "theme_option_dark"
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Action buttons
                    Button(
                        onClick = {
                            onThemeSelected(selectedMode)
                            onDismiss()
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VpnPrimaryBlue),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("apply_theme_button")
                    ) {
                        Text(
                            text = strings.applyLanguageBtn,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                    ) {
                        Text(
                            text = strings.cancelBtn,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemeOptionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    testTag: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) VpnPrimaryBlueSoft else MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) VpnPrimaryBlue else MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onSelect)
            .testTag(testTag)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) VpnPrimaryBlue else MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) VpnPrimaryBlue else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (isSelected) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(VpnPrimaryBlue)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
