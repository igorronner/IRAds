package com.igorronner.irinterstitial.services

import com.google.android.gms.ads.AdListener

interface IRInterstitial {
    fun load(adListener: AdListener)
    fun requestNewInterstitial()

}