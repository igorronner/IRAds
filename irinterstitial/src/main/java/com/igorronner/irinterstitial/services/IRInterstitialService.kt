package com.igorronner.irinterstitial.services

import android.app.Activity
import android.content.Intent
import android.support.v4.app.ActivityCompat
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.igorronner.irinterstitial.init.ConfigUtil

open class IRInterstitialService(val activity: Activity) {


    var mInterstitialAd: InterstitialAd? = null


    init {
        mInterstitialAd = InterstitialAd(activity)
        mInterstitialAd!!.adUnitId = ConfigUtil.INTERSTITIAL_ID
        requestNewInterstitial()
    }

    private fun requestNewInterstitial() {
        val adRequest = AdRequest.Builder()
                .build()
        mInterstitialAd?.loadAd(adRequest)
    }

    fun showInterstitial() {
        mInterstitialAd?.let {mInterstitialAd ->

            mInterstitialAd.show()

            mInterstitialAd.adListener = object : AdListener() {
                override fun onAdClosed() {
                    activity.finish()
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    mInterstitialAd.show()
                }
            }
        }
    }

    fun showInterstitialBeforeIntent(activity: Activity, intent: Intent, finishAll: Boolean) {
        mInterstitialAd?.let {mInterstitialAd ->

            mInterstitialAd.show()

            mInterstitialAd.adListener = object : AdListener() {
                override fun onAdClosed() {
                    if (finishAll)
                        ActivityCompat.finishAffinity(activity)
                    activity.startActivity(intent)
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    mInterstitialAd.show()
                }
            }
        }
    }

    fun showInterstitialBeforeIntent(activity: Activity, intent: Intent) {
       showInterstitialBeforeIntent(activity, intent, false)
    }

}