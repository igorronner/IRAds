package com.igorronner.irinterstitial.services

import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.doubleclick.PublisherAdRequest
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd
import com.igorronner.irinterstitial.dto.RemoteConfigDTO
import com.igorronner.irinterstitial.enums.IRInterstitialVersionEnum
import com.igorronner.irinterstitial.init.ConfigUtil
import com.igorronner.irinterstitial.init.IRAds
import com.igorronner.irinterstitial.init.IRAdsInit

class IRPublisherInterstitial(val adsInstance: IRAds, val remoteConfigDTO: RemoteConfigDTO) : IRInterstitial {

    var mPublisherInterstitialAd: PublisherInterstitialAd = PublisherInterstitialAd(adsInstance.activity)

    init {
        remoteConfigDTO.publisherInterstitialId?.let {
            mPublisherInterstitialAd.adUnitId = it
        } ?: kotlin.run {
            mPublisherInterstitialAd.adUnitId = ConfigUtil.PUBLISHER_INTERSTITIAL_ID
        }
    }

    override fun load(adListener: AdListener) {
        mPublisherInterstitialAd.show()
        mPublisherInterstitialAd.adListener = object : AdListener() {

            override fun onAdFailedToLoad(code: Int) {
                if (code == PublisherAdRequest.ERROR_CODE_NO_FILL){
                    IRInterstitialFactory(adsInstance, remoteConfigDTO)
                            .create(IRInterstitialVersionEnum.INTERSTITIAL_AD)
                            .load(adListener)
                    return
                }

                adListener.onAdFailedToLoad(code)
            }

            override fun onAdClosed() {
                adListener.onAdClosed()
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                adListener.onAdLoaded()

                if (!adsInstance.isStopped) {
                    AnalyticsService(adsInstance.activity).logEvent("SHOWN_AD_VERSION 2")
                    mPublisherInterstitialAd.show()
                }
            }
        }
    }

    override fun requestNewInterstitial() {
        mPublisherInterstitialAd.loadAd(PublisherAdRequest.Builder().build())
    }
}