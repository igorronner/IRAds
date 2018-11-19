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

        if (mInterstitialAd.isLoaded) {
            mInterstitialAd.show()
            AnalyticsService(adsInstance.activity).logEvent("SHOWN_AD_VERSION 1")

        } else
            adListener.onAdFailedToLoad(0)

        mInterstitialAd.adListener = object : AdListener() {

            override fun onAdFailedToLoad(p0: Int) {
            }

            override fun onAdClosed() {
               adListener.onAdClosed()
            }

            override fun onAdLoaded() {
                adListener.onAdLoaded()
            }
        }
    }

    override fun requestNewInterstitial() {
        val adRequest = AdRequest.Builder()
                .build()
        mInterstitialAd.loadAd(adRequest)
    }
}