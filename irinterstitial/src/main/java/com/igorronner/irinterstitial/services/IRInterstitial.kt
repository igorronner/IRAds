package com.igorronner.irinterstitial.services

import com.google.android.gms.ads.AdListener

interface IRInterstitial {


    @Deprecated(level = DeprecationLevel.ERROR,
            message = "use fun load(force: Boolean, adListener: IRInterstitialListener)")
    fun load(force: Boolean, adListener: AdListener)


    fun load(force: Boolean, adListener: IRInterstitialListener)
    fun requestNewInterstitial()
}

interface IRInterstitialListener {
    fun onAdClosed()
    fun onFailed()
    fun onNotLoaded()
}