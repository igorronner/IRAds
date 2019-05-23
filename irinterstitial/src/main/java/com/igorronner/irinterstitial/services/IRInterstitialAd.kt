package com.igorronner.irinterstitial.services

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.igorronner.irinterstitial.init.ConfigUtil
import com.igorronner.irinterstitial.init.IRAds
import com.igorronner.irinterstitial.utils.SingletonHolder

class IRInterstitialAd(val adsInstance: IRAds) : IRInterstitial{

    val mInterstitialAd = getInstance(adsInstance.activity.applicationContext)

    companion object : SingletonHolder<InterstitialAd, Context>(::InterstitialAd)

    init {
        if (mInterstitialAd.adUnitId.isNullOrBlank()) {
            mInterstitialAd.adUnitId = ConfigUtil.INTERSTITIAL_ID
        }
    }

    override fun load(force:Boolean, adListener: AdListener) {

        if (mInterstitialAd.isLoaded && !IRAds.isPremium(adsInstance.activity)) {
            mInterstitialAd.show()
            AnalyticsService(adsInstance.activity).logEvent("SHOWN_AD_VERSION 1")
            Log.d(IRInterstitialAd::class.java.simpleName, "SHOW IRInterstitial")
        } else if (!force)
            adListener.onAdFailedToLoad(0)

        mInterstitialAd.adListener = object : AdListener() {

            override fun onAdFailedToLoad(p0: Int) {
                if (force && !adsInstance.isStopped)
                    adListener.onAdFailedToLoad(p0)
            }

            override fun onAdClosed() {
                adListener.onAdClosed()
                adsInstance.onStop()
                requestNewInterstitial()
            }

            override fun onAdLoaded() {
                adListener.onAdLoaded()
                if (force && !adsInstance.isStopped && !IRAds.isPremium(adsInstance.activity)) {
                    mInterstitialAd.show()
                    Log.d(IRInterstitialAd::class.java.simpleName, "SHOW IRInterstitial")
                }
            }
        }
    }

    override fun requestNewInterstitial() {
        val adRequest = AdRequest.Builder()
                .build()
        mInterstitialAd.loadAd(adRequest)
        Log.d(IRInterstitialAd::class.java.simpleName, "SHOW IRInterstitial")
    }
}