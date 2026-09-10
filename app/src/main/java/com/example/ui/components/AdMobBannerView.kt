package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ads.AdMobManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

/**
 * Large, seamless AdMob Banner placed firmly at the bottom of the screen.
 * - No top "ADVERTISEMENT" text or labels (clean aesthetic requested by user)
 * - Uses large/adaptive height (up to 60-90dp) ensuring prominent visibility
 * - Placed across all main screens at the bottom
 */
@Composable
fun AdMobBannerView(
    modifier: Modifier = Modifier,
    adUnitId: String = AdMobManager.BANNER_AD_UNIT_ID
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .testTag("admob_banner_container")
    ) {
        AndroidView(
            factory = { context ->
                AdView(context).apply {
                    // Use large banner (320x100) or standard full width banner
                    setAdSize(AdSize.LARGE_BANNER)
                    this.adUnitId = adUnitId
                    loadAd(AdRequest.Builder().build())
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        )
    }
}
