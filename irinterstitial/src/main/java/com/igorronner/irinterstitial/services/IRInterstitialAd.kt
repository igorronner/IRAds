package com.igorronner.irinterstitial.services

import android.content.Context
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.igorronner.irinterstitial.init.ConfigUtil
import com.igorronner.irinterstitial.init.IRAds
import com.igorronner.irinterstitial.utils.Logger
import com.igorronner.irinterstitial.utils.SingletonHolder

class IRInterstitialAd(val adsInstance: IRAds) : IRInterstitial{

    val mInterstitialAd = getInstance(adsInstance.activity.applicationContext)

    companion object : SingletonHolder<InterstitialAd, Context>(::InterstitialAd){
        const val tag = "DefaultInterstitial"
    }

    init {
        if (mInterstitialAd.adUnitId.isNullOrBlank()) {
            mInterstitialAd.adUnitId = ConfigUtil.INTERSTITIAL_ID
        }
    }

    override fun load(force: Boolean, adListener: IRInterstitialListener) {
        if (mInterstitialAd.isLoaded && !IRAds.isPremium(adsInstance.activity)) {
            mInterstitialAd.show()
            val event = "Mostrou DefaultInterstitial"
            AnalyticsService(adsInstance.activity).logEvent(event)
            Logger.log(tag, event)
        } else if (!force)
            adListener.onNotLoaded()

        mInterstitialAd.adListener = object : AdListener() {

            override fun onAdFailedToLoad(p0: Int) {
                if (force && !adsInstance.isStopped)
                    adListener.onFailed()
                val event = "Falhou DefaultInterstitial"
                AnalyticsService(adsInstance.activity).logEvent(event)
                Logger.logWarning(tag, event)
            }

            override fun onAdClosed() {
                adListener.onAdClosed()
                adsInstance.onStop()
                val event = "Fechou DefaultInterstitial"
                AnalyticsService(adsInstance.activity).logEvent(event)
                Logger.log(tag, event)
            }

            override fun onAdLoaded() {
                if (force && !adsInstance.isStopped && !IRAds.isPremium(adsInstance.activity)) {
                    mInterstitialAd.show()
                    val event = "Mostrou DefaultInterstitial"
                    AnalyticsService(adsInstance.activity).logEvent(event)
                    Logger.log(tag, event)
                }
            }
        }
    }


    override fun load(force:Boolean, adListener: AdListener) {

        if (mInterstitialAd.isLoaded && !IRAds.isPremium(adsInstance.activity)) {
            mInterstitialAd.show()
            val event = "Mostrou DefaultInterstitial"
            AnalyticsService(adsInstance.activity).logEvent(event)
            Logger.log(tag, event)
        } else if (!force)
            adListener.onAdFailedToLoad(0)

        mInterstitialAd.adListener = object : AdListener() {

            override fun onAdFailedToLoad(p0: Int) {
                if (force && !adsInstance.isStopped)
                    adListener.onAdFailedToLoad(p0)
                val event = "Falhou DefaultInterstitial"
                AnalyticsService(adsInstance.activity).logEvent(event)
                Logger.logWarning(tag, event)
            }

            override fun onAdClosed() {
                adListener.onAdClosed()
                adsInstance.onStop()
                val event = "Fechou DefaultInterstitial"
                AnalyticsService(adsInstance.activity).logEvent(event)
                Logger.log(tag, event)
            }

            override fun onAdLoaded() {
                adListener.onAdLoaded()
                if (force && !adsInstance.isStopped && !IRAds.isPremium(adsInstance.activity)) {
                    mInterstitialAd.show()
                    val event = "Mostrou DefaultInterstitial"
                    AnalyticsService(adsInstance.activity).logEvent(event)
                    Logger.log(tag, event)
                }
            }
        }
    }

    override fun requestNewInterstitial() {
        if (mInterstitialAd.adUnitId.isNullOrBlank())
            return
        val adRequest = AdRequest.Builder()
                .build()
        mInterstitialAd.loadAd(adRequest)
        val event = "requestNewInterstitial DefaultInterstitial"
        AnalyticsService(adsInstance.activity).logEvent(event)
        Logger.log(tag, event)
    }
}