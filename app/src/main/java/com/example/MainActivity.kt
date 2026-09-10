package com.example

import android.app.Activity
import android.net.VpnService
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ads.AdMobManager
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
            Toast.makeText(this, viewModel.appStrings.value.vpnPermissionRequired, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize AdMob Test Ads according to Google Play policies
        AdMobManager.initialize(application)

        // Show App Open Ad on cold start once initialized (AdMob compliant)
        AdMobManager.showAppOpenAdIfAvailable(this)

        setContent {
            val themeMode by viewModel.themeMode.collectAsState()

            MyApplicationTheme(themeMode = themeMode) {
                VpnMainScreen(
                    viewModel = viewModel,
                    onConnectRequested = {
                        requestVpnConnection()
                    },
                    onDisconnectRequested = {
                        OpenVpnAndroidService.stopVpn(this)
                        // Show interstitial ad after user finishes disconnect (AdMob natural break)
                        AdMobManager.showInterstitialIfReady(this@MainActivity)
                    },
                    onReconnectRequested = {
                        requestVpnConnection()
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
