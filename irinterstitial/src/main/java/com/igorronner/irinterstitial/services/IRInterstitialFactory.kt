package com.igorronner.irinterstitial.services

import com.igorronner.irinterstitial.enums.IRInterstitialVersionEnum
import com.igorronner.irinterstitial.init.IRAds

class IRInterstitialFactory(private val adsInstance: IRAds) {

    fun create(
            interstitialVersionEnum: IRInterstitialVersionEnum?
    ) = when (interstitialVersionEnum) {
        IRInterstitialVersionEnum.EXPENSIVE_INTERSTITIAL -> IRExpensiveInterstitialAd(adsInstance)
        else -> IRInterstitialAd(adsInstance)
    }

}