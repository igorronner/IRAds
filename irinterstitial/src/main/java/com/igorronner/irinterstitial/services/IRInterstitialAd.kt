package com.igorronner.irinterstitial.services

import android.os.Build
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.igorronner.irinterstitial.dto.RemoteConfigDTO
import com.igorronner.irinterstitial.init.ConfigUtil
import com.igorronner.irinterstitial.init.IRAds

class IRInterstitialAd(val adsInstance: IRAds, remoteConfigDTO: RemoteConfigDTO) : IRInterstitial{

    var mInterstitialAd: InterstitialAd = InterstitialAd(adsInstance.activity)

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
//                val show=   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
//                    !adsInstance.isFinishing && !adsInstance.isDestroyed
//                else
//                    !adsInstance.isFinishing
//
//
//                if (show) {
//                    AnalyticsService(adsInstance).logEvent("SHOWN_AD_VERSION 1")
//                    mInterstitialAd.show()
//                }
            }
        }
    }

    override fun requestNewInterstitial() {
        val adRequest = AdRequest.Builder()
                .build()
        mInterstitialAd.loadAd(adRequest)
    }
}