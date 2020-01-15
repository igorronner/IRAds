package com.igorronner.irinterstitial.services

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.igorronner.irinterstitial.preferences.MainPreference

class ManagerAdaptiveBannerAd {

    var adMobAdUnitId: String = ""
    var expensiveAdMobAdUnitId: String = ""
    var bannerAdMobAdUnitId: String = ""

    private fun getAdSize(activity: Activity): AdSize? {
        return getAdSize(activity, activity.windowManager)
    }

    private fun getAdSize(context: Context, windowManager: WindowManager): AdSize? {
        val display = windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        val widthPixels = outMetrics.widthPixels.toFloat()
        val density = outMetrics.density
        val adWidth = (widthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth)
    }


    fun loadAdaptiveBanner(container: ViewGroup, activity: Activity){
        if (MainPreference.isPremium(activity)) {
            container.visibility = View.GONE
            return
        }
        val bannerAd = AdView(activity)
        bannerAd.adUnitId = bannerAdMobAdUnitId
        bannerAd.adSize = getAdSize(activity)
        val adRequest = AdRequest
                .Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build()
        bannerAd.loadAd(adRequest)
        container.addView(bannerAd)
    }












}