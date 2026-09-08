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
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
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
import com.example.ui.theme.AppBgLight
import com.example.ui.theme.AppBorderLight
import com.example.ui.theme.AppSurfaceLight
import com.example.ui.theme.AppSurfaceVariantLight
import com.example.ui.theme.AppTextMuted
import com.example.ui.theme.AppTextPrimary
import com.example.ui.theme.AppTextSecondary
import com.example.ui.theme.VpnAmber
import com.example.ui.theme.VpnConnectedGreen
import com.example.ui.theme.VpnConnectedGreenSoft
import com.example.ui.theme.VpnDisconnectedRed
import com.example.ui.theme.VpnPrimaryBlue
import com.example.ui.theme.VpnPrimaryBlueSoft

@Composable
fun DataUsageDialog(
    onDismiss: () -> Unit
) {
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
                .testTag("data_usage_dialog")
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AppSurfaceLight)
                        .padding(horizontal = 18.dp, vertical = 14.dp)
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
                                imageVector = Icons.Default.DataUsage,
                                contentDescription = null,
                                tint = VpnPrimaryBlue,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "أمان واستخدام البيانات",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppTextPrimary
                            )
                            Text(
                                text = "Google Play Data Safety Standards",
                                fontSize = 12.sp,
                                color = AppTextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_data_usage")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
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
                    // Google Play Data Safety Summary Grid
                    Text(
                        text = "ملخص إقرار أمان البيانات (Google Play Data Safety)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        DataSafetySummaryCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Block,
                            title = "جمع البيانات",
                            value = "لا يوجد (0%)",
                            color = VpnConnectedGreen,
                            bgColor = VpnConnectedGreenSoft
                        )
                        DataSafetySummaryCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Share,
                            title = "مشاركة البيانات",
                            value = "لا تشارك (0%)",
                            color = VpnConnectedGreen,
                            bgColor = VpnConnectedGreenSoft
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        DataSafetySummaryCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Lock,
                            title = "التشفير أثناء النقل",
                            value = "مشفّر بالكامل",
                            color = VpnPrimaryBlue,
                            bgColor = VpnPrimaryBlueSoft
                        )
                        DataSafetySummaryCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.DeleteSweep,
                            title = "حذف البيانات",
                            value = "لا توجد بيانات مخزنة",
                            color = VpnAmber,
                            bgColor = VpnAmber.copy(alpha = 0.12f)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Transparent Category Breakdown
                    Text(
                        text = "تفصيل فئات البيانات وفق تصنيفات Google Play",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = AppSurfaceLight),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, AppBorderLight, RoundedCornerShape(16.dp))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            DataCategoryRow(
                                title = "الموقع الجغرافي الدقيق (Precise Location)",
                                status = "غير مستخدم إطلاقاً",
                                collected = false
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = AppBorderLight)

                            DataCategoryRow(
                                title = "المعلومات الشخصية (الاسم، البريد، الهاتف)",
                                status = "غير مطلوبة ولا يتم جمعها",
                                collected = false
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = AppBorderLight)

                            DataCategoryRow(
                                title = "سجل التصفح وسجل الويب (Web Browsing)",
                                status = "صفر سجلات (Zero Logs)",
                                collected = false
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = AppBorderLight)

                            DataCategoryRow(
                                title = "معرفات الجهاز (Device ID / Advertising ID)",
                                status = "لا يتم قراءتها أو استخدامها للتتبع",
                                collected = false
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = AppBorderLight)

                            DataCategoryRow(
                                title = "حركة البيانات اللحظية (Live Throughput)",
                                status = "في ذاكرة RAM فقط أثناء الاتصال",
                                collected = false,
                                isInfo = true
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Bandwidth & Traffic Overhead Guide
                    Text(
                        text = "إرشادات استهلاك البيانات والإنترنت",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = AppSurfaceLight),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, AppBorderLight, RoundedCornerShape(16.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(VpnPrimaryBlueSoft)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NetworkCheck,
                                        contentDescription = null,
                                        tint = VpnPrimaryBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "كيف يؤثر الـ VPN على باقة الإنترنت؟",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppTextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "• تشفير OpenVPN يضيف ترويسة أمان صغيرة (حوالي 4-7% حجم إضافي) لكل حزمة بيانات مشفرة، وهو المعيار العالمي لضمان الأمان.\n" +
                                        "• لا يقوم التطبيق بتنزيل أي ملفات في الخلفية بدون علمك.\n" +
                                        "• يمكنك مراقبة إجمالي البايتات المرسلة والمستقبلة بدقة مباشرة من لوحة التحكم الرئيسية.",
                                fontSize = 12.sp,
                                color = AppTextSecondary,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Bottom button
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
                    ) {
                        Text(
                            text = "إغلاق",
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
private fun DataSafetySummaryCard(
    icon: ImageVector,
    title: String,
    value: String,
    color: Color,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AppSurfaceLight),
        modifier = modifier.border(1.dp, AppBorderLight, RoundedCornerShape(14.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(bgColor)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 11.5.sp,
                color = AppTextSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = AppTextPrimary
            )
        }
    }
}

@Composable
private fun DataCategoryRow(
    title: String,
    status: String,
    collected: Boolean,
    isInfo: Boolean = false
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppTextPrimary
            )
            Text(
                text = status,
                fontSize = 11.5.sp,
                color = if (collected) VpnDisconnectedRed else if (isInfo) VpnPrimaryBlue else VpnConnectedGreen
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(
                    if (collected) VpnDisconnectedRed.copy(alpha = 0.1f)
                    else if (isInfo) VpnPrimaryBlueSoft
                    else VpnConnectedGreenSoft
                )
        ) {
            Icon(
                imageVector = if (collected) Icons.Default.Close else if (isInfo) Icons.Default.Info else Icons.Default.Check,
                contentDescription = null,
                tint = if (collected) VpnDisconnectedRed else if (isInfo) VpnPrimaryBlue else VpnConnectedGreen,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
