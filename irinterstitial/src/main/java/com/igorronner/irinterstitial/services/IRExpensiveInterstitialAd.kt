package com.igorronner.irinterstitial.services

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.igorronner.irinterstitial.init.ConfigUtil
import com.igorronner.irinterstitial.init.IRAds
import com.igorronner.irinterstitial.utils.SingletonHolder

class IRExpensiveInterstitialAd(
        val adsInstance: IRAds
) : IRInterstitial{

    private var adUnitId:String

    init {
        adUnitId = ConfigUtil.EXPENSIVE_INTERSTITIAL_ID
    }

    constructor(adsInstance: IRAds,
                adUnitId:String) : this(adsInstance){
        this.adUnitId = adUnitId
    }

    val expensiveInterstitialAd = getInstance(adsInstance.activity.applicationContext)

    companion object : SingletonHolder<InterstitialAd, Context>(::InterstitialAd)

    init {
        if (expensiveInterstitialAd.adUnitId.isNullOrBlank()) {
            expensiveInterstitialAd.adUnitId = adUnitId
        }
    }

    override fun load(force: Boolean, adListener: AdListener) {
        if (expensiveInterstitialAd.isLoaded && !IRAds.isPremium(adsInstance.activity)) {
            expensiveInterstitialAd.show()
            AnalyticsService(adsInstance.activity).logEvent("SHOWN_AD_VERSION 3")
            Log.d(IRExpensiveInterstitialAd::class.java.simpleName, "SHOW EXPENSIVE")
        } else if (!force)
            adListener.onAdFailedToLoad(0)

        expensiveInterstitialAd.adListener = object : AdListener() {

            override fun onAdFailedToLoad(p0: Int) {
                if (force && !adsInstance.isStopped) {
                    adListener.onAdFailedToLoad(p0)
                }
            }

            override fun onAdClosed() {
                adListener.onAdClosed()
                adsInstance.onStop()
                requestNewInterstitial()
            }

            override fun onAdLoaded() {
                adListener.onAdLoaded()
                if (force && !adsInstance.isStopped && !IRAds.isPremium(adsInstance.activity)) {
                    expensiveInterstitialAd.show()
                }
            }
        }
    }

    override fun requestNewInterstitial() {

        Log.d(IRExpensiveInterstitialAd::class.java.simpleName, "REQUEST NEW EXPENSIVE")
        val adRequest = AdRequest.Builder()
                .build()
        expensiveInterstitialAd.loadAd(adRequest)
    }
}

