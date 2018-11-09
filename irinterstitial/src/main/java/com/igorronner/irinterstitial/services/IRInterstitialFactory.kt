package com.igorronner.irinterstitial.services

import android.app.Activity
import com.igorronner.irinterstitial.dto.RemoteConfigDTO
import com.igorronner.irinterstitial.enums.IRInterstitialVersionEnum

class IRInterstitialFactory(private val activity: Activity, private val remoteConfigDTO: RemoteConfigDTO) {

    fun create(interstitialVersionEnum: IRInterstitialVersionEnum):IRInterstitial{
        return if (interstitialVersionEnum == IRInterstitialVersionEnum.PUBLISHER_INTERSTITIAL)
            IRPublisherInterstitial(activity, remoteConfigDTO)
        else
            IRInterstitialAd(activity, remoteConfigDTO)
    }
}