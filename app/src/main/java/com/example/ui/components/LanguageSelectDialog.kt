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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.localization.AppLanguage
import com.example.localization.LocalAppStrings
import com.example.ui.theme.AppBgLight
import com.example.ui.theme.AppBorderLight
import com.example.ui.theme.AppSurfaceLight
import com.example.ui.theme.AppTextMuted
import com.example.ui.theme.AppTextPrimary
import com.example.ui.theme.AppTextSecondary
import com.example.ui.theme.VpnPrimaryBlue
import com.example.ui.theme.VpnPrimaryBlueSoft

@Composable
fun LanguageSelectDialog(
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    onDismiss: () -> Unit
) {
    val strings = LocalAppStrings.current
    var selectedLang by remember { mutableStateOf(currentLanguage) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = AppBgLight,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .shadow(20.dp, RoundedCornerShape(26.dp))
                .border(1.dp, AppBorderLight, RoundedCornerShape(26.dp))
                .testTag("language_select_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AppSurfaceLight)
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
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = VpnPrimaryBlue,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = strings.chooseLanguageTitle,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppTextPrimary
                            )
                            Text(
                                text = strings.chooseLanguageSubtitle,
                                fontSize = 12.sp,
                                color = AppTextSecondary
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = AppTextSecondary
                        )
                    }
                }

                HorizontalDivider(color = AppBorderLight)

                Column(modifier = Modifier.padding(20.dp)) {
                    // List of language options
                    val languageOptions = listOf(
                        LanguageOptionItem(
                            language = AppLanguage.SYSTEM,
                            flag = "🌐",
                            nativeName = strings.chooseLanguageSubtitle.let { "افتراضي النظام (System Default)" },
                            secondaryName = "Automatic based on device / store locale"
                        ),
                        LanguageOptionItem(
                            language = AppLanguage.ARABIC,
                            flag = "🇸🇦",
                            nativeName = "العربية",
                            secondaryName = "Arabic"
                        ),
                        LanguageOptionItem(
                            language = AppLanguage.ENGLISH,
                            flag = "🇺🇸",
                            nativeName = "English",
                            secondaryName = "English"
                        ),
                        LanguageOptionItem(
                            language = AppLanguage.FRENCH,
                            flag = "🇫🇷",
                            nativeName = "Français",
                            secondaryName = "French"
                        ),
                        LanguageOptionItem(
                            language = AppLanguage.SPANISH,
                            flag = "🇪🇸",
                            nativeName = "Español",
                            secondaryName = "Spanish"
                        )
                    )

                    languageOptions.forEach { option ->
                        val isSelected = selectedLang == option.language
                        LanguageChoiceCard(
                            option = option,
                            isSelected = isSelected,
                            onSelect = { selectedLang = option.language }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action buttons
                    Button(
                        onClick = {
                            onLanguageSelected(selectedLang)
                            onDismiss()
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VpnPrimaryBlue),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("apply_language_button")
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
                            color = AppTextSecondary
                        )
                    }
                }
            }
        }
    }
}

private data class LanguageOptionItem(
    val language: AppLanguage,
    val flag: String,
    val nativeName: String,
    val secondaryName: String
)

@Composable
private fun LanguageChoiceCard(
    option: LanguageOptionItem,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) VpnPrimaryBlueSoft else AppSurfaceLight
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) VpnPrimaryBlue else AppBorderLight,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onSelect)
            .testTag("language_option_${option.language.code}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = option.flag,
                    fontSize = 26.sp
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = option.nativeName,
                        fontSize = 15.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) VpnPrimaryBlue else AppTextPrimary
                    )
                    Text(
                        text = option.secondaryName,
                        fontSize = 12.sp,
                        color = AppTextSecondary
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
            } else {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, AppTextMuted, CircleShape)
                )
            }
        }
    }
}
