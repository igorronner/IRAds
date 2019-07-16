package com.igorronner.irinterstitial.services

import com.google.android.gms.ads.AdListener

interface IRInterstitial {
    fun load(force: Boolean, adListener: AdListener)
    fun requestNewInterstitial()
}