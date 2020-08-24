package com.igorronner.irinterstitial.services

import android.content.Context
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.igorronner.irinterstitial.init.ConfigUtil
import com.igorronner.irinterstitial.init.IRAds
import com.igorronner.irinterstitial.utils.Logger
import com.igorronner.irinterstitial.utils.SingletonHolder

class IRMidFloorInterstitialAd(
        val adsInstance: IRAds
) : IRInterstitial{

    val midFloorInterstitialAd = getInstance(adsInstance.activity.applicationContext)

    init {
        if (midFloorInterstitialAd.adUnitId.isNullOrBlank()) {
            midFloorInterstitialAd.adUnitId = ConfigUtil.MID_INTERSTITIAL_ID
        }
    }

    companion object : SingletonHolder<InterstitialAd, Context>(::InterstitialAd){
        const val tag = "IRAds"
    }

    override fun load(force: Boolean, adListener: IRInterstitialListener) {
        if (midFloorInterstitialAd.isLoaded && !IRAds.isPremium(adsInstance.activity)) {
            midFloorInterstitialAd.show()
            val event = "Mostrou_IRMidFloorInterstitialAd"
            AnalyticsService(adsInstance.activity).logEvent(event)
            Logger.log(tag, event)
        } else if (!force)
            adListener.onNotLoaded()

        midFloorInterstitialAd.adListener = object : AdListener() {

            override fun onAdFailedToLoad(p0: Int) {
                val event = "Falhou_IRMidFloorInterstitialAd"
                AnalyticsService(adsInstance.activity).logEvent(event)
                Logger.logError(tag, event)

                if (force && !adsInstance.isStopped) {
                    adListener.onFailed()
                }
            }

            override fun onAdClosed() {
                val event = "Fechou_IRMidFloorInterstitialAd"
                AnalyticsService(adsInstance.activity).logEvent(event)
                Logger.log(tag, event)
                adListener.onAdClosed()
                adsInstance.onStop()
            }

            override fun onAdLoaded() {
                if (force && !adsInstance.isStopped && !IRAds.isPremium(adsInstance.activity)) {
                    val event = "Mostrou_IRMidFloorInterstitialAd"
                    AnalyticsService(adsInstance.activity).logEvent(event)
                    Logger.log(tag, event)
                    midFloorInterstitialAd.show()
                }
            }
        }
    }
    override fun load(force: Boolean, adListener: AdListener) {
        if (midFloorInterstitialAd.isLoaded && !IRAds.isPremium(adsInstance.activity)) {
            midFloorInterstitialAd.show()
            val event = "Mostrou_IRMidFloorInterstitialAd"
            AnalyticsService(adsInstance.activity).logEvent(event)
            Logger.log(tag, event)
        } else if (!force)
            adListener.onAdFailedToLoad(0)

        midFloorInterstitialAd.adListener = object : AdListener() {

            override fun onAdFailedToLoad(p0: Int) {
                val event = "Falhou_IRMidFloorInterstitialAd"
                AnalyticsService(adsInstance.activity).logEvent(event)
                Logger.logError(tag, event)

                if (force && !adsInstance.isStopped) {
                    adListener.onAdFailedToLoad(p0)
                }
            }

            override fun onAdClosed() {
                val event = "Fechou_IRMidFloorInterstitialAd"
                AnalyticsService(adsInstance.activity).logEvent(event)
                Logger.log(tag, event)
                adListener.onAdClosed()
                adsInstance.onStop()
            }

            override fun onAdLoaded() {
                adListener.onAdLoaded()
                if (force && !adsInstance.isStopped && !IRAds.isPremium(adsInstance.activity)) {
                    val event = "Mostrou_IRMidFloorInterstitialAd"
                    AnalyticsService(adsInstance.activity).logEvent(event)
                    Logger.log(tag, event)
                    midFloorInterstitialAd.show()
                }
            }
        }
    }

    override fun requestNewInterstitial() {
        if (midFloorInterstitialAd.adUnitId.isNullOrBlank())
            return

        val adRequest = AdRequest.Builder()
                .build()
        midFloorInterstitialAd.loadAd(adRequest)
        val event = "requestNew_IRMidFloorInterstitialAd"
        AnalyticsService(adsInstance.activity).logEvent(event)
        Logger.log(tag, event)
    }

}

