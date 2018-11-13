package com.igorronner.irinterstitial.services

import android.content.Context
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.igorronner.irinterstitial.init.ConfigUtil
import com.igorronner.irinterstitial.init.IRAds
import com.igorronner.irinterstitial.utils.SingletonHolder

class IRInterstitialAd(val adsInstance: IRAds) : IRInterstitial{

    val mInterstitialAd = IRInterstitialAd.getInstance(adsInstance.activity.applicationContext)

    companion object : SingletonHolder<InterstitialAd, Context>(::InterstitialAd)


    init {
        if (mInterstitialAd.adUnitId.isNullOrBlank()) {
            mInterstitialAd.adUnitId = ConfigUtil.INTERSTITIAL_ID
        }
    }

    override fun load(adListener: AdListener) {

        if (mInterstitialAd.isLoaded && !adsInstance.isStopped)
            mInterstitialAd.show()

        mInterstitialAd.adListener = object : AdListener() {

            override fun onAdFailedToLoad(p0: Int) {
               adListener.onAdFailedToLoad(p0)
            }

            override fun onAdClosed() {
               adListener.onAdClosed()
            }

            override fun onAdLoaded() {
                adListener.onAdLoaded()
                if (!adsInstance.isStopped) {
                    AnalyticsService(adsInstance.activity).logEvent("SHOWN_AD_VERSION 1")
                    mInterstitialAd.show()
                }
            }
        }
    }

    override fun requestNewInterstitial() {
        val adRequest = AdRequest.Builder()
                .build()
        mInterstitialAd.loadAd(adRequest)
    }
}