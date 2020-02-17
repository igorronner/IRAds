package com.igorronner.irinterstitial.init

import android.app.Activity
import android.content.Context
import android.view.ViewGroup
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.igorronner.irinterstitial.R
import com.igorronner.irinterstitial.services.ManagerNativeAd

object IRBanner {

    lateinit var managerNativeAd: ManagerNativeAd

    fun initialize(context: Context) {
        managerNativeAd = ManagerNativeAd(context)
                .setAdmobAdUnitId(ConfigUtil.NATIVE_AD_ID)
                .setExpensiveAdmobAdUnitId(ConfigUtil.EXPENSIVE_NATIVE_AD_ID)
                .setMidAdmobAdUnitId(ConfigUtil.MID_NATIVE_AD_ID)
                .setBannerAdmobAdUnitId(ConfigUtil.BANNER_AD_ID)
    }

    fun initialize(managerNativeAd: ManagerNativeAd) {
        this.managerNativeAd = managerNativeAd
    }

    @JvmOverloads fun loadNativeAd(
            activity: Activity,
            showProgress: Boolean = false
    ) {
        loadNativeAd(
                showProgress = showProgress,
                unifiedNativeAdView = activity.findViewById(R.id.adViewNative) as UnifiedNativeAdView
        )
    }

    @JvmOverloads fun loadNativeAd(
            unifiedNativeAdView: UnifiedNativeAdView,
            cardView: ViewGroup? = null,
            showProgress: Boolean = false
    ) {
        managerNativeAd
                .setShowProgress(showProgress)
                .loadNativeAd(cardView, unifiedNativeAdView)
    }

    @JvmOverloads fun loadExpensiveNativeAd(
            unifiedNativeAdView: UnifiedNativeAdView,
            cardView: ViewGroup? = null,
            showProgress: Boolean = false
    ) {
        managerNativeAd
                .setShowProgress(showProgress)
                .loadExpensiveNativeAd(cardView, unifiedNativeAdView)
    }

    @JvmOverloads fun loadExpensiveNativeAd(
            activity: Activity,
            showProgress: Boolean = false
    ) {
        loadExpensiveNativeAd(
                showProgress = showProgress,
                unifiedNativeAdView = activity.findViewById(R.id.adViewNative) as UnifiedNativeAdView
        )
    }







}