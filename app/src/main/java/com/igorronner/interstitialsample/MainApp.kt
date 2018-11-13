package com.igorronner.interstitialsample

import android.app.Application
import com.igorronner.irinterstitial.init.IRAdsInit

class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()


        IRAdsInit.start()
                    .setInterstitialId("PLACE_YOUR_ADD_UNIT_ID")
                    .setLogo(R.mipmap.ic_launcher)
                    .setNativeAdId("PLACE_YOUR_NATIVE_AD_ID")
                    .setAppPrefix("lib_")
                    .setPublisherInterstitialId("/6499/example/interstitial")
                    .build()

    }


}