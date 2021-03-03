package com.igorronner.interstitialsample

import android.app.Application
import com.igorronner.irinterstitial.init.IRAdsInit
import com.igorronner.irinterstitial.init.IRBanner

@Suppress("unused")
class MainApp : Application() {


    override fun onCreate() {
        super.onCreate()

        IRAdsInit.start()
                .setAppId("ca-app-pub-3940256099942544~3347511713")
                .setLogo(R.mipmap.ic_launcher)
                .setInterstitialId("ca-app-pub-3940256099942544/1033173712")
                .setTwoFloorsInterstitial(
                    "ca-app-pub-3940256099942544/1033173712",
                    "ca-app-pub-3940256099942544/1033173712"
                )
                .setNativeAdId("ca-app-pub-3940256099942544/2247696110")
                .setTwoFloorsNativeAd(
                    "ca-app-pub-3940256099942544/2247696110",
                        "ca-app-pub-3940256099942544/2247696110"
                )
                .setBannerAdId("ca-app-pub-3940256099942544/6300978111")
                .setTwoFloorsBannerId( // TODO
                    "malformed-ad-id-to-force-fallback",
                    "ca-app-pub-3940256099942544/6300978111"
                )
                .setRewardedVideoId("ca-app-pub-3940256099942544/5224354917")
                .setAppPrefix("lib_")
                .enablePurchace("premium")
//                .enableSubscription("premium_sub")
                .setTester(BuildConfig.DEBUG)
//                .setAdEnabled(false)
                .enableCheckMobills(false)
                .build(this)

        IRBanner.initialize(this)
    }



}