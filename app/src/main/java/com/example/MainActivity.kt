package com.example

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.ui.VpnMainScreen
import com.example.ui.VpnViewModel
import com.example.ui.theme.MyApplicationTheme
import com.example.vpn.OpenVpnAndroidService

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<VpnViewModel>()

    // Launcher for Android OS VPN preparation dialog
    private val vpnPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            startVpnConnection()
        } else {
            Toast.makeText(this, "يلزم منح إذن الـ VPN للاتصال بالخادم", Toast.LENGTH_SHORT).show()
        }
    }

    // Launcher for Android 13+ Notification permission
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        // Notification permission granted or denied, proceed with VPN
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Check notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            MyApplicationTheme {
                VpnMainScreen(
                    viewModel = viewModel,
                    onConnectRequested = {
                        requestVpnConnection()
                    },
                    onDisconnectRequested = {
                        OpenVpnAndroidService.stopVpn(this)
                    }
                )
            }
        }
    }

    private fun requestVpnConnection() {
        val prepareIntent = VpnService.prepare(this)
        if (prepareIntent != null) {
            vpnPermissionLauncher.launch(prepareIntent)
        } else {
            startVpnConnection()
        }
    }

    private fun startVpnConnection() {
        OpenVpnAndroidService.startVpn(this)
    }
}
