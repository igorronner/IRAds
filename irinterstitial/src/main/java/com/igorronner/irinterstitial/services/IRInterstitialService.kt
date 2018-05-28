package com.igorronner.irinterstitial.services

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.support.v4.app.ActivityCompat
import android.util.Log
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.igorronner.irinterstitial.init.ConfigUtil
import com.igorronner.irinterstitial.preferences.MainPreference

open class IRInterstitialService(val activity: Activity) : AdListener() {

    val tag = "IRInterstitialService"

    private val mInterstitialAd:InterstitialAd

    interface Callback{
        fun handle()
    }

    val callback:Callback? = null

    init {

        Log.d(tag, "init{}")
        mInterstitialAd = InterstitialAd(activity)
        mInterstitialAd.adUnitId = ConfigUtil.INTERSTITIAL_ID
        mInterstitialAd.adListener = (object : AdListener() {

            override fun onAdFailedToLoad(p0: Int) {
                callback?.handle()
                Log.d(tag, "onAdFailedToLoad")
            }
            override fun onAdClosed() {
                Log.d(tag, "onAdClosed")
                callback?.handle()
                requestNewInterstitial()
            }
        })

        requestNewInterstitial()
        Log.d(tag, "" + mInterstitialAd.adUnitId)

    }

    private fun requestNewInterstitial() {

        Log.d(tag, "requestNewInterstitial")
        Log.d(tag, "isLoading " + mInterstitialAd.isLoading)
        Log.d(tag, "isLoaded " + mInterstitialAd.isLoaded)
        if (!mInterstitialAd.isLoading && !mInterstitialAd.isLoaded) {
            // Create an ad request. If you're running this on a physical device, check your logcat
            // to learn how to enable test ads for it. Look for a line like this one:
            // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
            val adRequest = AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build()

            mInterstitialAd.loadAd(adRequest)

            Log.d(tag, "loadAd")
        }


    }

    fun showInterstitial(finishAll: Boolean){
        showInterstitial(null, finishAll)
    }
    fun showInterstitial(){
        showInterstitial(null, false)
    }
    fun showInterstitial(titleDialog: String?, finishAll: Boolean) {
        if (MainPreference.isPremium(activity)){
            finish(activity, finishAll)
            return
        }

        val dialog= ProgressDialog(activity)
        titleDialog?.let {
            dialog.setMessage(titleDialog)
            dialog.setCancelable(false)
            dialog.isIndeterminate=true
            dialog.show()
        }

        mInterstitialAd.show()

        mInterstitialAd.adListener = object : AdListener() {

            override fun onAdFailedToLoad(p0: Int) {
                titleDialog?.let {
                    if (dialog.isShowing)
                        dialog.hide()
                }
                finish(activity, finishAll)
            }

            override fun onAdClosed() {
                finish(activity, finishAll)
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                titleDialog?.let {
                    if (dialog.isShowing)
                        dialog.hide()
                }

                mInterstitialAd.show()
            }
        }

    }

    fun finish(activity: Activity, finishAll: Boolean){
        if (finishAll)
            ActivityCompat.finishAffinity(activity)
        else
            activity.finish()

    }
    fun showInterstitialBeforeIntent(activity: Activity, intent: Intent, finishAll: Boolean, titleDialog:String) {

        if (MainPreference.isPremium(activity)){
            finishWithIntent(activity, finishAll, intent)
            return
        }
        val dialog= ProgressDialog(activity)
        dialog.setMessage(titleDialog)
        dialog.setCancelable(false)
        dialog.isIndeterminate=true
        dialog.show()

        mInterstitialAd?.let {mInterstitialAd ->

            mInterstitialAd.show()

            mInterstitialAd.adListener = object : AdListener() {


                override fun onAdFailedToLoad(p0: Int) {
                    if (dialog.isShowing)
                        dialog.hide()
                    finishWithIntent(activity, finishAll, intent)
                }
                override fun onAdClosed() {
                    finishWithIntent(activity, finishAll, intent)
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    if (dialog.isShowing)
                        dialog.hide()
                    mInterstitialAd.show()
                    requestNewInterstitial()
                }
            }
        }

    }


    fun showInterstitialBeforeFragment(callback: Callback){
        if (MainPreference.isPremium(activity)){
            callback.handle()
            Log.d(tag, "premium")
            return
        }

        if (mInterstitialAd.isLoaded) {
            Log.d(tag, "loaded")
            mInterstitialAd.show()
        } else {
            Log.d(tag, "not loaded")
            callback.handle()
            requestNewInterstitial()
        }
    }

    fun finishWithIntent(activity: Activity, finishAll: Boolean, intent: Intent){
        if (finishAll)
            ActivityCompat.finishAffinity(activity)

        activity.startActivity(intent)

    }

    fun showInterstitialBeforeIntent(activity: Activity, intent: Intent, titleDialog:String) {
       showInterstitialBeforeIntent(activity, intent, false, titleDialog)
    }



}