package com.igorronner.irinterstitial.services

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.support.v4.app.ActivityCompat
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.igorronner.irinterstitial.init.ConfigUtil
import com.igorronner.irinterstitial.preferences.MainPreference

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

        mInterstitialAd?.let {mInterstitialAd ->

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

        showInterstitalBeforeFragment {  }
    }


    fun showInterstitalBeforeFragment(callback: () -> Unit){
        if (MainPreference.isPremium(activity)){
            callback()
            return
        }

        mInterstitialAd?.let {mInterstitialAd ->
            mInterstitialAd.show()

            mInterstitialAd.adListener = object : AdListener() {


                override fun onAdFailedToLoad(p0: Int) {
                    callback()
                }
                override fun onAdClosed() {
                    callback()
                    requestNewInterstitial()
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    mInterstitialAd.show()
                    requestNewInterstitial()
                }
            }
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