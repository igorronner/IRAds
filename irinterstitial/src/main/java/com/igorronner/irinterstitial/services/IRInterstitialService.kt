package com.igorronner.irinterstitial.services

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.support.annotation.IdRes
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentTransaction
import com.google.android.gms.ads.AdListener
import com.igorronner.irinterstitial.dto.RemoteConfigDTO
import com.igorronner.irinterstitial.enums.IRInterstitialVersionEnum
import com.igorronner.irinterstitial.init.IRAds
import com.igorronner.irinterstitial.preferences.MainPreference


open class IRInterstitialService {

    lateinit var adsInstance: IRAds
    lateinit var remoteConfigDTO: RemoteConfigDTO

    private val irInterstitial = IRInterstitialFactory(adsInstance, remoteConfigDTO)
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
        if (MainPreference.isPremium(adsInstance.activity)){
            finish(adsInstance.activity, finishAll)
            return
        }

        val dialog= ProgressDialog(adsInstance.activity)
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
                finish(adsInstance.activity, finishAll)
            }

            override fun onAdClosed() {
                finish(adsInstance.activity, finishAll)
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

    fun showInterstitialBeforeIntent(intent: Intent, finishAll: Boolean, titleDialog:String?) {
        val activity = adsInstance.activity
        if (MainPreference.isPremium(activity)){
            finishWithIntent(finishAll, intent)
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

                finishWithIntent(finishAll, intent)
            }
            override fun onAdClosed() {
                finishWithIntent(finishAll, intent)
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

    fun showInterstitialBeforeIntent(intent: Intent, titleDialog:String) {
        showInterstitialBeforeIntent(intent, false, titleDialog)
    }

    fun showInterstitialBeforeIntent(intent: Intent) {
        showInterstitialBeforeIntent(intent, false, null)
    }

    fun finishWithIntent(finishAll: Boolean, intent: Intent){
        if (finishAll)
            ActivityCompat.finishAffinity(adsInstance.activity)

       adsInstance.activity.startActivity(intent)

    }


    fun showInterstitialBeforeFragment(fragment: Fragment, @IdRes  containerViewId:Int, fragmentActivity: FragmentActivity, titleDialog:String?) {
        val activity = adsInstance.activity
        if (MainPreference.isPremium(activity)){
            replaceFragment(fragment,  containerViewId, fragmentActivity)
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

                replaceFragment(fragment,  containerViewId, fragmentActivity)

            }
            override fun onAdClosed() {
                titleDialog?.let {
                    if (dialog.isShowing)
                        dialog.hide()
                }
                replaceFragment(fragment,  containerViewId, fragmentActivity)
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

    fun showInterstitialBeforeFragment(fragment: Fragment, @IdRes  containerViewId:Int, fragmentActivity: FragmentActivity){
        showInterstitialBeforeFragment(fragment,  containerViewId, fragmentActivity, null)
    }

    fun replaceFragment(fragment: Fragment, @IdRes  containerViewId:Int, fragmentActivity: FragmentActivity){
        val fragmentTransaction = fragmentActivity.supportFragmentManager
                .beginTransaction()
        fragmentTransaction.replace(containerViewId, fragment)
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        fragmentTransaction.commitAllowingStateLoss()
    }

}