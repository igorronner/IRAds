
package com.igorronner.irinterstitial.services

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.igorronner.irinterstitial.BuildConfig
import com.igorronner.irinterstitial.enums.FloorEnum
import com.igorronner.irinterstitial.preferences.MainPreference

internal class ManagerAdaptiveBannerAd {
    var bannerAdMobAdUnitId: String? = ""
    var midBannerAdMobAdUnitId: String? = ""
    var expensiveBannerAdMobAdUnitId: String? = ""

    fun loadAdaptiveBanner(container: ViewGroup, activity: Activity) {
        loadAdaptiveBanner(container, activity, FloorEnum.HIGH_FLOOR)
    }

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

    private fun loadAdaptiveBanner(container: ViewGroup, activity: Activity, floorEnum: FloorEnum) {
        if (MainPreference.isPremium(activity)) {
            container.visibility = View.GONE
            return
        }

        if (container.background == null) {
            // Banner container background should not be transparent
            container.setBackgroundColor(Color.parseColor("#666666"))
        }

        var adUnitId: String? = ""

        when (floorEnum) {
            FloorEnum.HIGH_FLOOR -> {
                if (expensiveBannerAdMobAdUnitId.isIdValid()) {
                    adUnitId = expensiveBannerAdMobAdUnitId
                } else {
                    loadAdaptiveBanner(container, activity, FloorEnum.MID_FLOOR)
                    return
                }
            }

            FloorEnum.MID_FLOOR -> {
                if (midBannerAdMobAdUnitId.isIdValid()) {
                    adUnitId = midBannerAdMobAdUnitId
                } else {
                    loadAdaptiveBanner(container, activity, FloorEnum.NO_FLOOR)
                    return
                }
            }

            FloorEnum.NO_FLOOR -> {
                if (bannerAdMobAdUnitId.isIdValid()) {
                    adUnitId = bannerAdMobAdUnitId
                } else {
                    container.visibility = View.INVISIBLE
                    return
                }
            }
        }

        loadAd(container, activity, adUnitId, floorEnum)
    }

    private fun loadAd(container: ViewGroup, activity: Activity, adId: String?, floorEnum: FloorEnum) {
        if (adId.isNullOrEmpty()) {
            container.visibility = View.GONE
            return
        }

        val bannerAd = AdView(activity)
        bannerAd.adUnitId = adId
        bannerAd.adSize = getAdSize(activity)
        val adRequest = AdRequest.Builder().apply {
            if (BuildConfig.DEBUG) {
                addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
            }
        }.build()

        bannerAd.adListener = object : AdListener() {
            override fun onAdFailedToLoad(errorCode: Int) {
                Toast.makeText(activity, errorCode.toString(), Toast.LENGTH_SHORT).show()
                if (floorEnum == FloorEnum.HIGH_FLOOR && expensiveBannerAdMobAdUnitId.isIdValid()) {
                    loadAdaptiveBanner(container, activity, FloorEnum.MID_FLOOR)
                } else if (floorEnum == FloorEnum.MID_FLOOR && midBannerAdMobAdUnitId.isIdValid()) {
                    loadAdaptiveBanner(container, activity, FloorEnum.NO_FLOOR)
                } else {
                    container.visibility = View.GONE
                }
            }
        }
        bannerAd.loadAd(adRequest)
        container.addView(bannerAd)
    }

    private fun String?.isIdValid(): Boolean {
        return this != null && this.isNotEmpty()
    }
}