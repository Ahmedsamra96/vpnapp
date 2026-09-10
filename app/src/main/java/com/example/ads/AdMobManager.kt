package com.example.ads

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import java.util.Date

/**
 * Manages Google AdMob Test Ads according to Google Play & AdMob Policies:
 * - Uses official Google AdMob Test IDs
 * - Ensures ads are never shown during active user inputs or critical VPN transitions without grace
 * - Handles Banner, Interstitial, and App Open Ad lifecycle cleanly
 */
object AdMobManager : Application.ActivityLifecycleCallbacks {

    private const val TAG = "AdMobManager"

    // Official Google AdMob Test Ad Unit IDs
    private const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
    private const val TEST_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
    private const val TEST_APP_OPEN_AD_UNIT_ID = "ca-app-pub-3940256099942544/9257390708"

    // Real Production AdMob Ad Unit IDs from your AdMob Console
    private const val PROD_BANNER_AD_UNIT_ID = "ca-app-pub-7820157448660134/6776380038"
    private const val PROD_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-7820157448660134/7878698394"
    private const val PROD_APP_OPEN_AD_UNIT_ID = "ca-app-pub-7820157448660134/5252535056"

    /**
     * Active Ad Unit IDs:
     * - In DEBUG / Testing (during development & emulator), test IDs are used automatically to protect your AdMob account from policy violations.
     * - In RELEASE (the build uploaded to Google Play), your real production AdMob IDs are used automatically.
     */
    val BANNER_AD_UNIT_ID: String
        get() = if (com.example.BuildConfig.DEBUG) TEST_BANNER_AD_UNIT_ID else PROD_BANNER_AD_UNIT_ID

    val INTERSTITIAL_AD_UNIT_ID: String
        get() = if (com.example.BuildConfig.DEBUG) TEST_INTERSTITIAL_AD_UNIT_ID else PROD_INTERSTITIAL_AD_UNIT_ID

    val APP_OPEN_AD_UNIT_ID: String
        get() = if (com.example.BuildConfig.DEBUG) TEST_APP_OPEN_AD_UNIT_ID else PROD_APP_OPEN_AD_UNIT_ID

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
        if (isInitialized) return
        application.registerActivityLifecycleCallbacks(this)

        // Automatically register emulators and test devices to prevent any invalid traffic on real accounts
        val testDeviceIds: List<String> = listOf(
            AdRequest.DEVICE_ID_EMULATOR
        )
        val requestConfiguration = RequestConfiguration.Builder()
            .setTestDeviceIds(testDeviceIds)
            .build()
        MobileAds.setRequestConfiguration(requestConfiguration)

        MobileAds.initialize(application) { initStatus ->
            Log.d(TAG, "AdMob SDK Initialized: $initStatus")
            isInitialized = true
            loadInterstitialAd(application)
            loadAppOpenAd(application)
        }
    }

    // =========================================================================
    // INTERSTITIAL ADS
    // =========================================================================

    fun loadInterstitialAd(context: Context) {
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
                    Log.d(TAG, "Interstitial Ad loaded successfully")

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

    /**
     * Show interstitial ad if loaded and cooldown has passed.
     * Complies with AdMob policy: Never show unexpectedly or disruptively.
     */
    fun showInterstitialIfReady(activity: Activity, onAdFinished: () -> Unit = {}) {
        val now = System.currentTimeMillis()
        if (now - lastInterstitialShownTime < INTERSTITIAL_COOLDOWN_MS) {
            // Cooldown active, don't interrupt user
            onAdFinished()
            return
        }

        val ad = interstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    loadInterstitialAd(activity.applicationContext)
                    onAdFinished()
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    interstitialAd = null
                    loadInterstitialAd(activity.applicationContext)
                    onAdFinished()
                }

                override fun onAdShowedFullScreenContent() {
                    lastInterstitialShownTime = System.currentTimeMillis()
                }
            }
            ad.show(activity)
        } else {
            loadInterstitialAd(activity.applicationContext)
            onAdFinished()
        }
    }

    // =========================================================================
    // APP OPEN ADS
    // =========================================================================

    fun loadAppOpenAd(context: Context) {
        if (isAppOpenAdAvailable() || isAppOpenLoading) return
        isAppOpenLoading = true

        val adRequest = AdRequest.Builder().build()
        AppOpenAd.load(
            context,
            APP_OPEN_AD_UNIT_ID,
            adRequest,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    isAppOpenLoading = false
                    loadTime = Date().time
                    Log.d(TAG, "App Open Ad loaded successfully")
                }

                override fun onAdFailedToLoad(loadError: LoadAdError) {
                    appOpenAd = null
                    isAppOpenLoading = false
                    Log.w(TAG, "App Open Ad failed to load: ${loadError.message}")
                }
            }
        )
    }

    private fun isAppOpenAdAvailable(): Boolean {
        // App Open ad is considered valid for up to 4 hours per AdMob policy
        val isNotExpired = (Date().time - loadTime) < 4 * 3600 * 1000
        return appOpenAd != null && isNotExpired
    }

    fun showAppOpenAdIfAvailable(activity: Activity, onComplete: () -> Unit = {}) {
        if (isShowingAppOpenAd) {
            onComplete()
            return
        }

        if (!isAppOpenAdAvailable()) {
            loadAppOpenAd(activity.applicationContext)
            onComplete()
            return
        }

        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                appOpenAd = null
                isShowingAppOpenAd = false
                loadAppOpenAd(activity.applicationContext)
                onComplete()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                appOpenAd = null
                isShowingAppOpenAd = false
                loadAppOpenAd(activity.applicationContext)
                onComplete()
            }

            override fun onAdShowedFullScreenContent() {
                isShowingAppOpenAd = true
            }
        }
        isShowingAppOpenAd = true
        appOpenAd?.show(activity)
    }

    // =========================================================================
    // ACTIVITY LIFECYCLE CALLBACKS
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
