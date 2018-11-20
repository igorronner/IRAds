package com.igorronner.irinterstitial.services

import android.content.Context
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.doubleclick.PublisherAdRequest
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd
import com.igorronner.irinterstitial.dto.RemoteConfigDTO
import com.igorronner.irinterstitial.enums.IRInterstitialVersionEnum
import com.igorronner.irinterstitial.init.ConfigUtil
import com.igorronner.irinterstitial.init.IRAds
import com.igorronner.irinterstitial.utils.SingletonHolder


class IRPublisherInterstitial(val adsInstance: IRAds, val remoteConfigDTO: RemoteConfigDTO) : IRInterstitial {

    val mPublisherInterstitialAd = getInstance(adsInstance.activity.applicationContext)

    companion object : SingletonHolder<PublisherInterstitialAd, Context>(::PublisherInterstitialAd)

    init {
        if (mPublisherInterstitialAd.adUnitId.isNullOrBlank()) {
            if(!remoteConfigDTO.publisherInterstitialId.isNullOrBlank())
                mPublisherInterstitialAd.adUnitId = remoteConfigDTO.publisherInterstitialId
            else
                mPublisherInterstitialAd.adUnitId = ConfigUtil.PUBLISHER_INTERSTITIAL_ID
        }
    }

    override fun load(force:Boolean, adListener: AdListener) {
        if (mPublisherInterstitialAd.isLoaded) {
            mPublisherInterstitialAd.show()
            AnalyticsService(adsInstance.activity).logEvent("SHOWN_AD_VERSION 2")

        } else if (!force)
            adListener.onAdFailedToLoad(0)

        mPublisherInterstitialAd.adListener = object : AdListener() {

            override fun onAdFailedToLoad(code: Int) {
                if (force && !adsInstance.isStopped)
                    adListener.onAdFailedToLoad(code)
            }

            override fun onAdClosed() {
                requestNewInterstitial()
                adListener.onAdClosed()
            }

            override fun onAdLoaded() {
                adListener.onAdLoaded()
                if (force && !adsInstance.isStopped)
                    mPublisherInterstitialAd.show()

            }
        }
    }

    override fun requestNewInterstitial() {
        mPublisherInterstitialAd.loadAd(PublisherAdRequest.Builder().build())
    }
}