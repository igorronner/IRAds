package com.igorronner.irinterstitial.services

import android.app.Activity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.igorronner.irinterstitial.init.ConfigUtil
import com.igorronner.irinterstitial.views.SplashActivity

open class IRInterstitialService(val activity: Activity) {


    var mInterstitialAd: InterstitialAd? = null


    init {
        mInterstitialAd = InterstitialAd(activity)
        mInterstitialAd!!.adUnitId = ConfigUtil.INTERSTITIAL_ID
        requestNewInterstitial()
    }

    private fun requestNewInterstitial() {
        val adRequest = AdRequest.Builder()
                .build()
        mInterstitialAd?.loadAd(adRequest)
    }

    fun showInterstitial() {
        mInterstitialAd?.let {mInterstitialAd ->

            mInterstitialAd.show()

            mInterstitialAd.adListener = object : AdListener() {
                override fun onAdClosed() {
                    activity.finish()
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    requestNewInterstitial()
                    if(activity is SplashActivity)
                        mInterstitialAd.show()
                }
            }
        }
    }
}