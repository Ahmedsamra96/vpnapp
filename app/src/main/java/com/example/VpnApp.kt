package com.example

import android.app.Application
import android.os.Build
import com.tim.openvpn.init.initializeOpenVpnLibrary

class VpnApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (Build.FINGERPRINT != "robolectric") {
            try {
                initializeOpenVpnLibrary()
            } catch (e: Throwable) {
                android.util.Log.w("VpnApp", "OpenVPN library initialization note: ${e.message}")
            }
        }
    }
}

