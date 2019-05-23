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


open class IRInterstitialService {

    private var irInterstitial:IRInterstitial
    var adsInstance:IRAds

    constructor(adsInstance: IRAds,
                remoteConfigDTO: RemoteConfigDTO? = null){
        this.adsInstance = adsInstance
        irInterstitial = IRInterstitialFactory(adsInstance, remoteConfigDTO).create(IRInterstitialVersionEnum.values().first())
        irInterstitial.requestNewInterstitial()
    }

    constructor(adsInstance: IRAds,
                remoteConfigDTO: RemoteConfigDTO? = null,
                irInterstitialVersionEnum: IRInterstitialVersionEnum = IRInterstitialVersionEnum.INTERSTITIAL_AD){
        this.adsInstance = adsInstance
        irInterstitial = IRInterstitialFactory(adsInstance, remoteConfigDTO).create(irInterstitialVersionEnum)
        irInterstitial.requestNewInterstitial()

    }

    fun showInterstitial(){
        showInterstitial(finish = true, force = false)
    }

    fun showInterstitial(finish: Boolean){
        showInterstitial(finish, false)
    }

    fun forceShowInterstitial(finish: Boolean){
        showInterstitial(finish, true)
    }

    fun forceShowInterstitial(){
        showInterstitial(finish = true, force = true)
    }

    fun showInterstitial(finish: Boolean, force: Boolean) {
        if (MainPreference.isPremium(adsInstance.activity.applicationContext)){
            finish(adsInstance.activity, finish)
            return
        }

        irInterstitial.load(force, object : AdListener() {

            override fun onAdFailedToLoad(p0: Int) {
                finish(adsInstance.activity, finish)
            }

            override fun onAdClosed() {
                finish(adsInstance.activity, finish)
            }
        })
    }

    fun forceShowExpensiveInterstitial(){
        if (MainPreference.isPremium(adsInstance.activity.applicationContext)){
            finish(adsInstance.activity, true)
            return
        }

        irInterstitial.load(true, object : AdListener() {
            override fun onAdFailedToLoad(p0: Int) {
                forceShowInterstitial()
            }

            override fun onAdClosed() {
                finish(adsInstance.activity, true)
            }
        })
    }

    fun finish(activity: Activity, finish: Boolean){
        if (finish)
            activity.finish()

    }

    fun forceShowInterstitialBeforeIntent(intent: Intent, finish: Boolean){
        showInterstitialBeforeIntent(intent, finish, true)
    }

    fun forceShowInterstitialBeforeIntent(intent: Intent){
        showInterstitialBeforeIntent(intent,true, true)
    }


    fun showInterstitialBeforeIntent(intent: Intent, finishAll: Boolean, force: Boolean) {
        val activity = adsInstance.activity
        if (MainPreference.isPremium(activity)){
            finishWithIntent(finishAll, intent)
            return
        }

        irInterstitial.load(force, object : AdListener() {
            override fun onAdFailedToLoad(p0: Int) {
                finishWithIntent(finishAll, intent)
            }
            override fun onAdClosed() {
                finishWithIntent(finishAll, intent)
            }
        })


    }

    fun showInterstitialBeforeIntent(intent: Intent, finishAll: Boolean){
        showInterstitialBeforeIntent(intent, finishAll, false)
    }

    fun showInterstitialBeforeIntent(intent: Intent) {
        showInterstitialBeforeIntent(intent, false, false)
    }


    fun finishWithIntent(finishAll: Boolean, intent: Intent){
        if (finishAll)
            ActivityCompat.finishAffinity(adsInstance.activity)

       adsInstance.activity.startActivity(intent)

    }

    fun forceShowInterstitialBeforeFragment(fragment: Fragment, @IdRes  containerViewId:Int, fragmentActivity: FragmentActivity){
        showInterstitialBeforeFragment(fragment, containerViewId, fragmentActivity, true)
    }

    fun showInterstitialBeforeFragment(fragment: Fragment, @IdRes  containerViewId:Int, fragmentActivity: FragmentActivity) {
        showInterstitialBeforeFragment(fragment, containerViewId, fragmentActivity, false)
    }

    fun showInterstitialBeforeFragment(fragment: Fragment, @IdRes  containerViewId:Int, fragmentActivity: FragmentActivity, force: Boolean) {
        val activity = adsInstance.activity
        if (MainPreference.isPremium(activity)){
            replaceFragment(fragment,  containerViewId, fragmentActivity)
            return
        }

        irInterstitial.load(force, object : AdListener() {
            override fun onAdFailedToLoad(p0: Int) {
                replaceFragment(fragment,  containerViewId, fragmentActivity)

            }
            override fun onAdClosed() {
                replaceFragment(fragment,  containerViewId, fragmentActivity)
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