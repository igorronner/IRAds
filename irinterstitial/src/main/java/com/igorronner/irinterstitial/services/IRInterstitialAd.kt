package com.igorronner.irinterstitial.services

import android.app.Activity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.igorronner.irinterstitial.dto.RemoteConfigDTO
import com.igorronner.irinterstitial.init.ConfigUtil

class IRInterstitialAd(val activity: Activity,  remoteConfigDTO: RemoteConfigDTO) : IRInterstitial{

    var mInterstitialAd: InterstitialAd = InterstitialAd(activity)

    init {
        mInterstitialAd.adUnitId = ConfigUtil.INTERSTITIAL_ID
        requestNewInterstitial()
    }

    override fun load(adListener: AdListener) {
        val adRequest = AdRequest.Builder()
                .build()
        mInterstitialAd.loadAd(adRequest)
        mInterstitialAd.adListener = object : AdListener() {

            override fun onAdFailedToLoad(p0: Int) {
               adListener.onAdFailedToLoad(p0)
            }

            override fun onAdClosed() {
               adListener.onAdClosed()
            }

            override fun onAdLoaded() {
                super.onAdLoaded()

                mInterstitialAd.show()

                AnalyticsService(activity).logEvent("SHOWN_AD_VERSION 1")
            }
        }
    }

    override fun requestNewInterstitial() {
        val adRequest = AdRequest.Builder()
                .build()
        mInterstitialAd.loadAd(adRequest)
    }
}