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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.ui.theme.AppBgLight
import com.example.ui.theme.AppBorderLight
import com.example.ui.theme.AppSurfaceLight
import com.example.ui.theme.AppSurfaceVariantLight
import com.example.ui.theme.AppTextMuted
import com.example.ui.theme.AppTextPrimary
import com.example.ui.theme.AppTextSecondary
import com.example.ui.theme.VpnConnectedGreen
import com.example.ui.theme.VpnPrimaryBlue
import com.example.ui.theme.VpnPrimaryBlueSoft

@Composable
fun PrivacyPolicyDialog(
    onDismiss: () -> Unit
) {
    var isArabic by remember { mutableStateOf(true) }

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
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(VpnPrimaryBlueSoft)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Policy,
                                contentDescription = null,
                                tint = VpnPrimaryBlue,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isArabic) "سياسة الخصوصية" else "Privacy Policy",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppTextPrimary
                            )
                            Text(
                                text = if (isArabic) "متوافقة مع معايير Google Play" else "Google Play Policy Compliant",
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
                            contentDescription = "Close",
                            tint = AppTextSecondary
                        )
                    }
                }

                HorizontalDivider(color = AppBorderLight)

                // Language Selector FilterChips
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AppSurfaceLight)
                        .padding(horizontal = 18.dp, vertical = 8.dp)
                ) {
                    FilterChip(
                        selected = isArabic,
                        onClick = { isArabic = true },
                        label = { Text("العربية") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = VpnPrimaryBlueSoft,
                            selectedLabelColor = VpnPrimaryBlue,
                            containerColor = AppSurfaceVariantLight,
                            labelColor = AppTextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isArabic,
                            borderColor = if (isArabic) VpnPrimaryBlue else AppBorderLight
                        )
                    )

                    FilterChip(
                        selected = !isArabic,
                        onClick = { isArabic = false },
                        label = { Text("English") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = VpnPrimaryBlueSoft,
                            selectedLabelColor = VpnPrimaryBlue,
                            containerColor = AppSurfaceVariantLight,
                            labelColor = AppTextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = !isArabic,
                            borderColor = if (!isArabic) VpnPrimaryBlue else AppBorderLight
                        )
                    )
                }

                HorizontalDivider(color = AppBorderLight)

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    if (isArabic) {
                        ArabicPrivacyPolicyContent()
                    } else {
                        EnglishPrivacyPolicyContent()
                    }

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
                            text = if (isArabic) "فهمت وموافق" else "I Understand & Agree",
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
private fun ArabicPrivacyPolicyContent() {
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
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "سياسة صارمة لمنع السجلات (Zero-Logs Policy)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = VpnConnectedGreen
                )
                Text(
                    text = "لا نسجل، لا نراقب، ولا نشارك أي نشاط تصفح أو بيانات شخصية إطلاقاً.",
                    fontSize = 12.sp,
                    color = AppTextSecondary
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    PolicySectionItem(
        icon = Icons.Default.Shield,
        title = "1. استخدام خدمة VpnService وفق معايير Google Play",
        content = "تعتمد الوظيفة الأساسية لهذا التطبيق حصرياً على واجهة برمجة تطبيقات VpnService من نظام Android لإنشاء نفق تشفير آمن على مستوى الجهاز. الغرض الوحيد من استخدام VpnService هو:\n" +
                "• تأمين وتشفير اتصالك بالإنترنت أثناء استخدام شبكات Wi-Fi العامة أو غير الموثوقة.\n" +
                "• توجيه حزم البيانات عبر خوادم OpenVPN مشفرة لحماية عنوان IP الأصلي الخاص بك.\n" +
                "• لا نقوم بأي تلاعب في حركة المرور، ولا نقوم بحقن أي إعلانات، ولا نتدخل في أداء التطبيقات الأخرى."
    )

    PolicySectionItem(
        icon = Icons.Default.VisibilityOff,
        title = "2. البيانات التي لا يتم جمعها إطلاقاً (عدم الاحتفاظ بالسجلات)",
        content = "نحن نؤمن بحق الخصوصية المطلق، لذا تم تصميم بنية التطبيق التقنية لعدم حفظ أو تخزين:\n" +
                "• لا نسجل سجل التصفح، المواقع، أو التطبيقات التي تستخدمها.\n" +
                "• لا نخزن استعلامات DNS (حيث يتم توجيهها مشفرة عبر DNS Leak Protection).\n" +
                "• لا نسجل عنوان IP الحقيقي الخاص بك أو موقعك الجغرافي الدقيق.\n" +
                "• لا نسجل محتوى البيانات المرسلة أو المستقبلة عبر النفق المشفر.\n" +
                "• لا نطلب ولا نخزن أي معلومات شخصية (مثل الاسم أو البريد أو رقم الهاتف)."
    )

    PolicySectionItem(
        icon = Icons.Default.Lock,
        title = "3. التشفير والأمان التقني",
        content = "يتم تأمين جميع قنوات الاتصال باستخدام معايير التشفير الرائدة في الصناعة:\n" +
                "• تشفير OpenVPN AES-256-GCM عسكري المستوى مع مفاتيح تبادل TLS 1.3.\n" +
                "• ميزة Kill Switch لمنع تسرب البيانات عند انقطاع الاتصال المفاجئ.\n" +
                "• حماية تسرب DNS لحجب استعلامات مزود الخدمة المحلي."
    )

    PolicySectionItem(
        icon = Icons.Default.Security,
        title = "4. البيانات اللحظية المؤقتة (Session Metrics)",
        content = "أثناء الاتصال النشط، يحسب التطبيق سرعة التنزيل والرفع وعدد البايتات المنقولة. هذه الأرقام:\n" +
                "• تُحسب لحظياً وتُعرض على شاشة التطبيق داخل الذاكرة العشوائية (RAM) للجهاز فقط.\n" +
                "• لا تُخزن في أي قاعدة بيانات ولا يتم إرسالها أو مشاركتها مع أي خادم خارجي.\n" +
                "• يتم مسحها وتصفيرها فور إيقاف جلسة الـ VPN."
    )

    PolicySectionItem(
        icon = Icons.Default.NoAccounts,
        title = "5. عدم بيع أو مشاركة البيانات لأي طرف ثالث",
        content = "نلتزم التزاماً قاطعاً بعدم بيع أو تأجير أو مشاركة أو تداول أي بيانات خاصة بالمستخدمين مع شركات الإعلانات، أو وسطاء البيانات، أو أي جهات خارجية لأي غرض تجاري."
    )

    PolicySectionItem(
        icon = Icons.Default.GppGood,
        title = "6. حقوق المستخدم والتواصل",
        content = "نظراً لعدم جمع أي بيانات شخصية، لا توجد سجلات مرتبطة بك على خوادمنا. يمكنك دائماً التوقف عن استخدام التطبيق أو مسحه متى شئت دون أن تبقى أي آثار لبياناتك.\n" +
                "لأي استفسارات قانونية أو تقنية: support@openvpnfree.local\n" +
                "تاريخ السريان: سبتمبر 2026 - الإصدار 1.0"
    )
}

@Composable
private fun EnglishPrivacyPolicyContent() {
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
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Strict Zero-Logs Policy",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = VpnConnectedGreen
                )
                Text(
                    text = "We do not log, track, or share your browsing history or personal data.",
                    fontSize = 12.sp,
                    color = AppTextSecondary
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    PolicySectionItem(
        icon = Icons.Default.Shield,
        title = "1. Android VpnService Usage & Google Play Compliance",
        content = "The core functionality of this application relies on the Android VpnService API to create a secure device-level encrypted tunnel. In compliance with Google Play Developer Program Policies:\n" +
                "• VpnService is used strictly for encrypting your device internet traffic and providing remote gateway routing.\n" +
                "• We never use VpnService to redirect or manipulate traffic from other apps for monetization, ad fraud, or unauthorized data collection."
    )

    PolicySectionItem(
        icon = Icons.Default.VisibilityOff,
        title = "2. Information We Never Collect (No-Logs Guarantee)",
        content = "Our network architecture operates with zero retention:\n" +
                "• No Browsing History: We do not see or record the websites you visit or apps you use.\n" +
                "• No DNS Queries: Queries are resolved via private DNS.\n" +
                "• No Real IP Addresses: Your original IP address is never logged.\n" +
                "• No Traffic Content: Data passing through the tunnel is encrypted.\n" +
                "• No Personal Identifiers: No account registration, email, or name is required."
    )

    PolicySectionItem(
        icon = Icons.Default.Lock,
        title = "3. Encryption & Security Standards",
        content = "• Industry-standard OpenVPN protocol with AES-256-GCM cipher.\n" +
                "• TLS 1.3 cryptographic handshake for secure session negotiation.\n" +
                "• Built-in Kill Switch to prevent data exposure during unexpected connection drops."
    )

    PolicySectionItem(
        icon = Icons.Default.Security,
        title = "4. Real-time Session Metrics (In-Memory Only)",
        content = "Live download/upload speed counters and session duration are processed strictly in local device RAM during active connections and are never transmitted to any analytics or server infrastructure."
    )

    PolicySectionItem(
        icon = Icons.Default.NoAccounts,
        title = "5. Third-Party Data Sharing & Advertising",
        content = "We do not sell, rent, license, or disclose user data to advertisers, data brokers, or third parties."
    )

    PolicySectionItem(
        icon = Icons.Default.GppGood,
        title = "6. User Rights & Contact",
        content = "Because we collect zero personal data, there is no persistent identifiable data to delete. You retain full control to disconnect or uninstall at any time.\n" +
                "Contact: support@openvpnfree.local\n" +
                "Effective Date: September 2026 - Version 1.0"
    )
}

@Composable
private fun PolicySectionItem(
    icon: ImageVector,
    title: String,
    content: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppSurfaceLight),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .border(1.dp, AppBorderLight, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
