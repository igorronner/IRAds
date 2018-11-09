package com.igorronner.irinterstitial.services

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.support.v4.app.ActivityCompat
import com.google.android.gms.ads.AdListener
import com.igorronner.irinterstitial.dto.RemoteConfigDTO
import com.igorronner.irinterstitial.enums.IRInterstitialVersionEnum
import com.igorronner.irinterstitial.preferences.MainPreference


open class IRInterstitialService(val activity: Activity, private val remoteConfigDTO: RemoteConfigDTO) {

    val irInterstitial = IRInterstitialFactory(activity, remoteConfigDTO)
            .create(IRInterstitialVersionEnum.values().first { it.version == remoteConfigDTO.adVersion })

    init {
        irInterstitial.requestNewInterstitial()
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
        irInterstitial.load(object : AdListener() {

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
            }
          })


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

        irInterstitial.load(object : AdListener() {
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
            }
        })


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