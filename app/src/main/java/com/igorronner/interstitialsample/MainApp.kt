package com.igorronner.interstitialsample

import android.app.Application
import com.igorronner.irinterstitial.init.IRAdsInit

class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()

        IRAdsInit.start()
                .setAppId("ca-app-pub-3940256099942544~3347511713")
                .setInterstitialId("ca-app-pub-3940256099942544/1033173712")
                //With Error
                //.setInterstitialId("ca-app-pub")
                .setExpensiveInterstitialId("ca-app-pub-3940256099942544/1033173712")
                //With error
                .setExpensiveInterstitialId("ca-app-pub")
                .setLogo(R.mipmap.ic_launcher)
                .setExpensiveNativeAdId("ca-app-pub-3940256099942544/2247696110")
                .setNativeAdId("ca-app-pub-3940256099942544/2247696110")
                .setAppPrefix("lib_")
                .enablePurchace("premium")
                .setTester(BuildConfig.DEBUG)
//                .enableCheckMobills(true)
                .build(this)
    }

}