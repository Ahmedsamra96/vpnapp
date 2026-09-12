package com.example.ads

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.example.BuildConfig
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import java.util.Date

/**
 * Manages Google AdMob Ads for Ruvon VPN:
 * - In DEBUG mode: All ads (Banner, Interstitial, AppOpen) are completely DISABLED.
 *   No ads will load, no banners will show, providing a 100% clean debug experience.
 * - In RELEASE mode: Real production AdMob Ad Unit IDs are used exclusively.
 */
object AdMobManager : Application.ActivityLifecycleCallbacks {

    private const val TAG = "AdMobManager"

    /**
     * Set to false so that Debug APK has NO ads at all.
     * Only Release builds will have ads enabled.
     */
    val areAdsEnabled: Boolean
        get() = !BuildConfig.DEBUG

    // Real Production AdMob Ad Unit IDs from your AdMob Console
    const val PROD_BANNER_AD_UNIT_ID = "ca-app-pub-7820157448660134/6776380038"
    const val PROD_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-7820157448660134/7878698394"
    const val PROD_APP_OPEN_AD_UNIT_ID = "ca-app-pub-7820157448660134/5252535056"

    /**
     * Banner Ad Unit ID: Real production ID in Release mode, empty in Debug.
     */
    val BANNER_AD_UNIT_ID: String
        get() = if (areAdsEnabled) PROD_BANNER_AD_UNIT_ID else ""

    val INTERSTITIAL_AD_UNIT_ID: String
        get() = if (areAdsEnabled) PROD_INTERSTITIAL_AD_UNIT_ID else ""

    val APP_OPEN_AD_UNIT_ID: String
        get() = if (areAdsEnabled) PROD_APP_OPEN_AD_UNIT_ID else ""

    private var isInitialized = false
    private var currentActivity: Activity? = null

    // Interstitial Ad state
    private var interstitialAd: InterstitialAd? = null
    private var isInterstitialLoading = false
    private var lastInterstitialShownTime: Long = 0

    // Minimum cooldown between interstitials to respect Google AdMob policy (at least 45 seconds)
    private const val INTERSTITIAL_COOLDOWN_MS = 45_000L

    // App Open Ad state
    private var appOpenAd: AppOpenAd? = null
    private var isAppOpenLoading = false
    private var loadTime: Long = 0
    private var isShowingAppOpenAd = false

    fun initialize(application: Application) {
        if (!areAdsEnabled) {
            Log.d(TAG, "Debug build: Ads are completely disabled.")
            return
        }
        if (isInitialized) return
        application.registerActivityLifecycleCallbacks(this)

        MobileAds.initialize(application) { initStatus ->
            Log.d(TAG, "AdMob SDK Initialized in Release: $initStatus")
            isInitialized = true
            loadInterstitialAd(application)
            loadAppOpenAd(application)
        }
    }

    // =========================================================================
    // INTERSTITIAL ADS
    // =========================================================================

    fun loadInterstitialAd(context: Context) {
        if (!areAdsEnabled) return
        if (interstitialAd != null || isInterstitialLoading) return
        isInterstitialLoading = true

        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            INTERSTITIAL_AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isInterstitialLoading = false
                    Log.d(TAG, "Interstitial Ad loaded with unit: $INTERSTITIAL_AD_UNIT_ID")

                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            interstitialAd = null
                            loadInterstitialAd(context)
                        }

                        override fun onAdFailedToShowFullScreenContent(error: AdError) {
                            interstitialAd = null
                            loadInterstitialAd(context)
                        }

                        override fun onAdShowedFullScreenContent() {
                            lastInterstitialShownTime = System.currentTimeMillis()
                        }
                    }
                }

                override fun onAdFailedToLoad(loadError: LoadAdError) {
                    interstitialAd = null
                    isInterstitialLoading = false
                    Log.w(TAG, "Interstitial failed to load: ${loadError.message}")
                }
            }
        )
    }

    fun showInterstitialIfReady(activity: Activity, onAdFinished: () -> Unit = {}) {
        if (!areAdsEnabled) {
            onAdFinished()
            return
        }

        val now = System.currentTimeMillis()
        if (now - lastInterstitialShownTime < INTERSTITIAL_COOLDOWN_MS) {
            onAdFinished()
            return
        }

        val ad = interstitialAd
        if (ad != null) {
            val originalCallback = ad.fullScreenContentCallback
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    originalCallback?.onAdDismissedFullScreenContent()
                    onAdFinished()
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    originalCallback?.onAdFailedToShowFullScreenContent(error)
                    onAdFinished()
                }

                override fun onAdShowedFullScreenContent() {
                    originalCallback?.onAdShowedFullScreenContent()
                }
            }
            ad.show(activity)
        } else {
            loadInterstitialAd(activity)
            onAdFinished()
        }
    }

    // =========================================================================
    // APP OPEN ADS
    // =========================================================================

    fun loadAppOpenAd(context: Context) {
        if (!areAdsEnabled) return
        if (isAppOpenLoading || isAppOpenAdAvailable()) return
        isAppOpenLoading = true

        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            context,
            APP_OPEN_AD_UNIT_ID,
            request,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    isAppOpenLoading = false
                    loadTime = Date().time
                    Log.d(TAG, "App Open Ad loaded with unit: $APP_OPEN_AD_UNIT_ID")

                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            appOpenAd = null
                            isShowingAppOpenAd = false
                            loadAppOpenAd(context)
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            appOpenAd = null
                            isShowingAppOpenAd = false
                            loadAppOpenAd(context)
                        }

                        override fun onAdShowedFullScreenContent() {
                            isShowingAppOpenAd = true
                        }
                    }
                }

                override fun onAdFailedToLoad(loadError: LoadAdError) {
                    isAppOpenLoading = false
                    appOpenAd = null
                    Log.w(TAG, "App Open Ad failed to load: ${loadError.message}")
                }
            }
        )
    }

    private fun isAppOpenAdAvailable(): Boolean {
        if (!areAdsEnabled) return false
        val wasLoadedRecently = (Date().time - loadTime) < 4 * 3600 * 1000 // 4 hours validity
        return appOpenAd != null && wasLoadedRecently
    }

    fun showAppOpenAdIfAvailable(activity: Activity, onAdFinished: () -> Unit = {}) {
        if (!areAdsEnabled) {
            onAdFinished()
            return
        }

        if (isShowingAppOpenAd) {
            onAdFinished()
            return
        }

        if (!isAppOpenAdAvailable()) {
            loadAppOpenAd(activity)
            onAdFinished()
            return
        }

        appOpenAd?.let { ad ->
            val originalCallback = ad.fullScreenContentCallback
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    originalCallback?.onAdDismissedFullScreenContent()
                    onAdFinished()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    originalCallback?.onAdFailedToShowFullScreenContent(adError)
                    onAdFinished()
                }

                override fun onAdShowedFullScreenContent() {
                    originalCallback?.onAdShowedFullScreenContent()
                }
            }
            ad.show(activity)
        } ?: onAdFinished()
    }

    // =========================================================================
    // Activity Lifecycle Callbacks
    // =========================================================================

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {
        if (currentActivity == activity) {
            currentActivity = null
        }
    }
}
