package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Wifi
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.ui.theme.AppBgLight
import com.example.ui.theme.AppBorderLight
import com.example.ui.theme.AppSurfaceLight
import com.example.ui.theme.AppSurfaceVariantLight
import com.example.ui.theme.AppTextPrimary
import com.example.ui.theme.AppTextSecondary
import com.example.ui.theme.VpnAmber
import com.example.ui.theme.VpnConnectedGreen
import com.example.ui.theme.VpnConnectedGreenSoft
import com.example.ui.theme.VpnPrimaryBlue
import com.example.ui.theme.VpnPrimaryBlueSoft

@Composable
fun AppPermissionsDialog(
    onRequestNotificationPermission: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    val hasNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = AppBgLight,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .shadow(20.dp, RoundedCornerShape(24.dp))
                .border(1.dp, AppBorderLight, RoundedCornerShape(24.dp))
                .testTag("app_permissions_dialog")
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
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = VpnPrimaryBlue,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "أذونات التطبيق والشفافية",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppTextPrimary
                            )
                            Text(
                                text = "وفق معايير الحد الأدنى من الأذونات",
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
                    Text(
                        text = "يطلب التطبيق فقط الأذونات الضرورية تقنياً لتشغيل نفق الـ VPN بأمان وفق سياسات Google Play.",
                        fontSize = 12.5.sp,
                        color = AppTextSecondary,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Permission 1: VPN Service
                    PermissionCard(
                        icon = Icons.Default.VpnKey,
                        name = "إذن خدمة الـ VPN (VpnService)",
                        status = "إذن نظام معتمد",
                        isGranted = true,
                        description = "يتم طلبه تلقائياً عبر نافذة حماية نظام Android عند أول اتصال لإنشاء النفق المشفر بأمان.",
                        actionButton = null
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Permission 2: Notifications
                    PermissionCard(
                        icon = Icons.Default.Notifications,
                        name = "إذن الإشعارات (Foreground Service)",
                        status = if (hasNotificationPermission) "ممنوح ومفعل" else "مطلوب لتنبيهات الاتصال",
                        isGranted = hasNotificationPermission,
                        description = "مطلوب وفق سياسة Google Play لإبقاء إشعار دائم في شريط الحالة أثناء عمل الـ VPN لتمكينك من متابعة سرعة النفق وإيقافه فوراً بنقرة واحدة.",
                        actionButton = if (!hasNotificationPermission) {
                            {
                                Button(
                                    onClick = {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                            onRequestNotificationPermission()
                                        } else {
                                            openAppSettings(context)
                                        }
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = VpnPrimaryBlue),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Text("تفعيل الإذن", fontSize = 12.sp, color = Color.White)
                                }
                            }
                        } else null
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Permission 3: Network Access
                    PermissionCard(
                        icon = Icons.Default.Wifi,
                        name = "الوصول للإنترنت والشبكة (Internet)",
                        status = "ممنوح تلقائياً",
                        isGranted = true,
                        description = "إذن قياسي لتنزيل قائمة الخوادم العامة وإرسال واستقبال حزم بيانات الإنترنت.",
                        actionButton = null
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Open App Settings Button
                    OutlinedButton(
                        onClick = { openAppSettings(context) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = AppTextPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "فتح إعدادات التطبيق في النظام",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = AppTextPrimary
                        )
                    }
                }
            }
        }
    }
}

private fun openAppSettings(context: Context) {
    try {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {}
}

@Composable
private fun PermissionCard(
    icon: ImageVector,
    name: String,
    status: String,
    isGranted: Boolean,
    description: String,
    actionButton: (@Composable () -> Unit)?
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppSurfaceLight),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, AppBorderLight, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (isGranted) VpnConnectedGreenSoft else VpnAmber.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isGranted) VpnConnectedGreen else VpnAmber,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = name,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTextPrimary
                        )
                        Text(
                            text = status,
                            fontSize = 11.5.sp,
                            color = if (isGranted) VpnConnectedGreen else VpnAmber
                        )
                    }
                }

                if (isGranted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Granted",
                        tint = VpnConnectedGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                fontSize = 11.5.sp,
                color = AppTextSecondary,
                lineHeight = 16.sp
            )

            if (actionButton != null) {
                Spacer(modifier = Modifier.height(10.dp))
                actionButton()
            }
        }
    }
}
