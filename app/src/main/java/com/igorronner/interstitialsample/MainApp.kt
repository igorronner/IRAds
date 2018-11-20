package com.igorronner.interstitialsample

import android.app.Application
import com.igorronner.irinterstitial.init.IRAdsInit

class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()


        IRAdsInit.start()
                    //For test
                    .setAppId("ca-app-pub-3940256099942544~3347511713")
                    //.setAppId("YOUR_ADMOB_APP_ID")

                    //For test
                    .setInterstitialId("ca-app-pub-3940256099942544/1033173712")
                    //.setInterstitialId("PLACE_YOUR_ADD_UNIT_ID")

                    .setLogo(R.mipmap.ic_launcher)
                    .setNativeAdId("PLACE_YOUR_NATIVE_AD_ID")
                    .setAppPrefix("lib_")
                    //For test
                    //.setPublisherInterstitialId("/6499/example/interstitial")
                    .setPublisherInterstitialId("PLACE_YOUR_PUBLISHER_AD_ID")
                    .setTester(BuildConfig.DEBUG)

                    .build(this)


    }


}