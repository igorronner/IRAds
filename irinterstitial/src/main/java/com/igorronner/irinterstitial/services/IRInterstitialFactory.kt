package com.igorronner.irinterstitial.services

import com.igorronner.irinterstitial.dto.RemoteConfigDTO
import com.igorronner.irinterstitial.enums.IRInterstitialVersionEnum
import com.igorronner.irinterstitial.init.IRAds

class IRInterstitialFactory(private val adsInstance: IRAds, private val remoteConfigDTO: RemoteConfigDTO) {


    fun create(interstitialVersionEnum: IRInterstitialVersionEnum):IRInterstitial{
        return if (interstitialVersionEnum == IRInterstitialVersionEnum.PUBLISHER_INTERSTITIAL)
            IRPublisherInterstitial(adsInstance, remoteConfigDTO)
        else
            IRInterstitialAd(adsInstance, remoteConfigDTO)
    }
}