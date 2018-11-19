package com.igorronner.irinterstitial.services

import android.app.Activity
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


open class IRInterstitialService(val adsInstance: IRAds,
                                 private val remoteConfigDTO: RemoteConfigDTO) {



    private val irInterstitial = IRInterstitialFactory(adsInstance, remoteConfigDTO)
            .create(IRInterstitialVersionEnum.values().first { it.version == remoteConfigDTO.adVersion })

    init {
        irInterstitial.requestNewInterstitial()
    }

    fun showInterstitial(){
        showInterstitial(false)
    }


    fun showInterstitial(finishAll: Boolean) {
        if (MainPreference.isPremium(adsInstance.activity)){
            finish(adsInstance.activity, finishAll)
            return
        }

        irInterstitial.load(object : AdListener() {

            override fun onAdFailedToLoad(p0: Int) {
                finish(adsInstance.activity, finishAll)
            }

            override fun onAdClosed() {
                finish(adsInstance.activity, finishAll)
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
            }
          })


    }

    fun finish(activity: Activity, finishAll: Boolean){
        if (finishAll)
            ActivityCompat.finishAffinity(activity)
        else
            activity.finish()

    }

    fun showInterstitialBeforeIntent(intent: Intent, finishAll: Boolean) {
        val activity = adsInstance.activity
        if (MainPreference.isPremium(activity)){
            finishWithIntent(finishAll, intent)
            return
        }

        irInterstitial.load(object : AdListener() {
            override fun onAdFailedToLoad(p0: Int) {
                finishWithIntent(finishAll, intent)
            }
            override fun onAdClosed() {
                finishWithIntent(finishAll, intent)
                irInterstitial.requestNewInterstitial()
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                irInterstitial.requestNewInterstitial()

            }
        })


    }

    fun showInterstitialBeforeIntent(intent: Intent) {
        showInterstitialBeforeIntent(intent, false)
    }


    fun finishWithIntent(finishAll: Boolean, intent: Intent){
        if (finishAll)
            ActivityCompat.finishAffinity(adsInstance.activity)

       adsInstance.activity.startActivity(intent)

    }


    fun showInterstitialBeforeFragment(fragment: Fragment, @IdRes  containerViewId:Int, fragmentActivity: FragmentActivity) {
        val activity = adsInstance.activity
        if (MainPreference.isPremium(activity)){
            replaceFragment(fragment,  containerViewId, fragmentActivity)
            return
        }

        irInterstitial.load(object : AdListener() {
            override fun onAdFailedToLoad(p0: Int) {
                replaceFragment(fragment,  containerViewId, fragmentActivity)

            }
            override fun onAdClosed() {
                replaceFragment(fragment,  containerViewId, fragmentActivity)
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
            }
        })


    }

    fun replaceFragment(fragment: Fragment, @IdRes  containerViewId:Int, fragmentActivity: FragmentActivity){
        val fragmentTransaction = fragmentActivity.supportFragmentManager
                .beginTransaction()
        fragmentTransaction.replace(containerViewId, fragment)
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        fragmentTransaction.commitAllowingStateLoss()
    }

}