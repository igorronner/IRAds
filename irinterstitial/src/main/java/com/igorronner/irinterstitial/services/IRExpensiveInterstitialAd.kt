package com.igorronner.irinterstitial.services

import android.content.Context
import android.nfc.Tag
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

    val expensiveInterstitialAd = getInstance(adsInstance.activity.applicationContext)

    init {
        if (expensiveInterstitialAd.adUnitId.isNullOrBlank()) {
            expensiveInterstitialAd.adUnitId = ConfigUtil.EXPENSIVE_INTERSTITIAL_ID
        }
    }

    companion object : SingletonHolder<InterstitialAd, Context>(::InterstitialAd){
        const val tag = "Expensive"
    }

    override fun load(force: Boolean, adListener: AdListener) {
        if (expensiveInterstitialAd.isLoaded && !IRAds.isPremium(adsInstance.activity)) {
            expensiveInterstitialAd.show()
            val event = "Mostrou IRExpensiveInterstitialAd"
            AnalyticsService(adsInstance.activity).logEvent(event)
            Log.d(tag, event)
        } else if (!force)
            adListener.onAdFailedToLoad(0)

        expensiveInterstitialAd.adListener = object : AdListener() {

            override fun onAdFailedToLoad(p0: Int) {
                val event = "Falhou IRExpensiveInterstitialAd"
                AnalyticsService(adsInstance.activity).logEvent(event)
                Log.d(tag, event)

                if (force && !adsInstance.isStopped) {
                    adListener.onAdFailedToLoad(p0)
                }
            }

            override fun onAdClosed() {
                val event = "Fechou IRExpensiveInterstitialAd"
                AnalyticsService(adsInstance.activity).logEvent(event)
                Log.d(tag, event)
                adListener.onAdClosed()
                adsInstance.onStop()
            }

            override fun onAdLoaded() {
                adListener.onAdLoaded()
                if (force && !adsInstance.isStopped && !IRAds.isPremium(adsInstance.activity)) {
                    val event = "Mostrou IRExpensiveInterstitialAd"
                    AnalyticsService(adsInstance.activity).logEvent(event)
                    Log.d(tag, event)
                    expensiveInterstitialAd.show()
                }
            }
        }
    }

    override fun requestNewInterstitial() {
        if (expensiveInterstitialAd.adUnitId.isNullOrBlank())
            return

        val adRequest = AdRequest.Builder()
                .build()
        expensiveInterstitialAd.loadAd(adRequest)
        val event = "requestNewInterstitial IRExpensiveInterstitialAd"
        AnalyticsService(adsInstance.activity).logEvent(event)
        Log.d(tag, event)
    }

}

