package com.igorronner.interstitialsample

import android.app.Application
import com.igorronner.irinterstitial.init.IRAds

class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()

        IRAds.startInit(this)
                .setInterstitialId("PLACE_YOUR_ADD_UNIT_ID")
                .setLogo(R.mipmap.ic_launcher)
                .setNativeAdId("PLACE_YOUR_NATIVE_AD_ID")
                .setPublisherInterstitialId("/6499/example/interstitial")
                .build()

    }


}