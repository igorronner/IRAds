package com.igorronner.irinterstitial.services

import android.app.Activity
import android.content.Intent
import android.support.annotation.IdRes
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentTransaction
import com.google.android.gms.ads.AdListener
import com.igorronner.irinterstitial.enums.IRInterstitialVersionEnum
import com.igorronner.irinterstitial.enums.IRInterstitialVersionEnum.*
import com.igorronner.irinterstitial.init.ConfigUtil
import com.igorronner.irinterstitial.init.IRAds
import com.igorronner.irinterstitial.preferences.MainPreference


open class IRInterstitialService {

    private var irInterstitial:IRInterstitial
    var adsInstance:IRAds

    constructor(adsInstance: IRAds){
        this.adsInstance = adsInstance
        irInterstitial = IRInterstitialFactory(adsInstance).create(EXPENSIVE_INTERSTITIAL)
        irInterstitial.requestNewInterstitial()
    }

    constructor(adsInstance: IRAds,
                irInterstitialVersionEnum: IRInterstitialVersionEnum = EXPENSIVE_INTERSTITIAL){
        this.adsInstance = adsInstance
        irInterstitial = IRInterstitialFactory(adsInstance).create(irInterstitialVersionEnum)
        irInterstitial.requestNewInterstitial()

    }

    fun showInterstitial(
            finish: Boolean,
            force: Boolean
    ){
        if (MainPreference.isPremium(adsInstance.activity.applicationContext)){
            finish(adsInstance.activity, finish)
            return
        }

        when {
            !ConfigUtil.EXPENSIVE_INTERSTITIAL_ID.isNullOrBlank() -> showExpensiveInterstitial(finish, force)
            !ConfigUtil.INTERSTITIAL_ID.isNullOrBlank() -> showDefaultInterstitial(finish, force)
            else -> finish(adsInstance.activity, finish)
        }
    }

    fun showInterstitialBeforeIntent(
            intent: Intent,
            finishAll: Boolean,
            force: Boolean
    ) {
        val activity = adsInstance.activity
        if (MainPreference.isPremium(activity)){
            finishWithIntent(finishAll, intent)
            return
        }

        when {
            !ConfigUtil.EXPENSIVE_INTERSTITIAL_ID.isNullOrBlank() -> showExpensiveInterstitialBeforeIntent(intent, finishAll, force)
            !ConfigUtil.INTERSTITIAL_ID.isNullOrBlank() -> showDefaultInterstitialBeforeIntent(intent, finishAll, force)
            else -> finishWithIntent(finishAll, intent)
        }
    }

    fun showInterstitialBeforeFragment(
            fragment: Fragment,
            @IdRes containerViewId:Int,
            fragmentActivity: FragmentActivity,
            force: Boolean
    ) {
        val activity = adsInstance.activity
        if (MainPreference.isPremium(activity)){
            replaceFragment(fragment,  containerViewId, fragmentActivity)
            return
        }

        when {
            !ConfigUtil.EXPENSIVE_INTERSTITIAL_ID.isNullOrBlank() -> showExpensiveInterstitialBeforeFragment(fragment, containerViewId, fragmentActivity, force)
            !ConfigUtil.INTERSTITIAL_ID.isNullOrBlank() -> showDefaultInterstitialBeforeFragment(fragment, containerViewId, fragmentActivity, force)
            else -> replaceFragment(fragment,  containerViewId, fragmentActivity)
        }
    }

    private fun showDefaultInterstitialBeforeFragment(
            fragment: Fragment,
            containerViewId: Int,
            fragmentActivity: FragmentActivity,
            force: Boolean
    ) {
        val activity = adsInstance.activity
        if (MainPreference.isPremium(activity)){
            replaceFragment(fragment,  containerViewId, fragmentActivity)
            return
        }

        if (irInterstitial !is  IRInterstitialAd)
            irInterstitial = requestNewInterstitial(INTERSTITIAL_AD)

        irInterstitial.load(force, object : AdListener() {
            override fun onAdFailedToLoad(p0: Int) {
                replaceFragment(fragment,  containerViewId, fragmentActivity)

            }
            override fun onAdClosed() {
                replaceFragment(fragment,  containerViewId, fragmentActivity)
                requestNewInterstitial(EXPENSIVE_INTERSTITIAL)
            }
        })
    }


    private fun showExpensiveInterstitialBeforeFragment(
            fragment: Fragment,
            containerViewId: Int,
            fragmentActivity: FragmentActivity,
            force: Boolean
    ) {
        val activity = adsInstance.activity
        if (MainPreference.isPremium(activity)){
            replaceFragment(fragment,  containerViewId, fragmentActivity)
            return
        }

        if (irInterstitial !is  IRInterstitialAd)
            irInterstitial = requestNewInterstitial(EXPENSIVE_INTERSTITIAL)
        irInterstitial.load(force, object : AdListener() {
            override fun onAdFailedToLoad(p0: Int) {
                showDefaultInterstitialBeforeFragment(fragment, containerViewId, fragmentActivity, force)
            }
            override fun onAdClosed() {
                replaceFragment(fragment,  containerViewId, fragmentActivity)
                requestNewInterstitial(EXPENSIVE_INTERSTITIAL)
            }
        })
    }


    fun showDefaultInterstitialBeforeIntent(
            intent: Intent,
            finishAll: Boolean,
            force: Boolean
    ) {
        val activity = adsInstance.activity
        if (MainPreference.isPremium(activity)){
            finishWithIntent(finishAll, intent)
            return
        }

        if (irInterstitial !is  IRInterstitialAd)
            irInterstitial = requestNewInterstitial(INTERSTITIAL_AD)

        irInterstitial.load(force, object : AdListener() {

            override fun onAdFailedToLoad(p0: Int) {
                finishWithIntent(finishAll, intent)
            }

            override fun onAdClosed() {
                finishWithIntent(finishAll, intent)
                requestNewInterstitial(EXPENSIVE_INTERSTITIAL)
            }
        })
    }

    fun showExpensiveInterstitialBeforeIntent(
            intent: Intent,
            finishAll: Boolean,
            force: Boolean
    ) {
        val activity = adsInstance.activity
        if (MainPreference.isPremium(activity)){
            finishWithIntent(finishAll, intent)
            return
        }

        if (irInterstitial !is  IRInterstitialAd)
            irInterstitial = requestNewInterstitial(EXPENSIVE_INTERSTITIAL)

        irInterstitial.load(force, object : AdListener() {

            override fun onAdFailedToLoad(p0: Int) {
                showDefaultInterstitialBeforeIntent(intent, finishAll, force)
            }

            override fun onAdClosed() {
                finishWithIntent(finishAll, intent)
                requestNewInterstitial(EXPENSIVE_INTERSTITIAL)
            }
        })
    }

    fun showDefaultInterstitial(
            finish: Boolean,
            force: Boolean
    ) {
        if (MainPreference.isPremium(adsInstance.activity.applicationContext)){
            finish(adsInstance.activity, finish)
            return
        }

        if (irInterstitial !is  IRInterstitialAd)
            irInterstitial = requestNewInterstitial(INTERSTITIAL_AD)

        irInterstitial.load(force, object : AdListener() {

            override fun onAdFailedToLoad(p0: Int) {
                finish(adsInstance.activity, finish)
            }

            override fun onAdClosed() {
                finish(adsInstance.activity, finish)
                requestNewInterstitial(EXPENSIVE_INTERSTITIAL)
            }
        })
    }

    fun showExpensiveInterstitial(
            finish: Boolean,
            force: Boolean
    ){
        if (MainPreference.isPremium(adsInstance.activity.applicationContext)){
            finish(adsInstance.activity, true)
            return
        }

        if (irInterstitial !is IRExpensiveInterstitialAd)
            irInterstitial  = requestNewInterstitial(EXPENSIVE_INTERSTITIAL)

        irInterstitial.load(force, object : AdListener() {

            override fun onAdFailedToLoad(p0: Int) {
                showDefaultInterstitial(finish, force)
            }

            override fun onAdClosed() {
                finish(adsInstance.activity, finish)
                requestNewInterstitial(EXPENSIVE_INTERSTITIAL)
            }
        })

    }

    fun showDefaultInterstitial(){
        showDefaultInterstitial(finish = true, force = false)
    }

    fun showDefaultInterstitial(finish: Boolean){
        showDefaultInterstitial(finish, false)
    }

    fun showInterstitial(){
        showInterstitial(finish = false, force = false)
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

    fun finish(activity: Activity, finish: Boolean){
        if (finish)
            activity.finish()

    }

    fun forceShowInterstitialBeforeIntent(intent: Intent, finish: Boolean){
        showInterstitialBeforeIntent(intent, finish, true)
    }

    fun forceShowInterstitialBeforeIntent(intent: Intent){
        showInterstitialBeforeIntent(intent, finishAll = true, force = true)
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

    fun replaceFragment(fragment: Fragment, @IdRes  containerViewId:Int, fragmentActivity: FragmentActivity){
        val fragmentTransaction = fragmentActivity.supportFragmentManager
                .beginTransaction()
        fragmentTransaction.replace(containerViewId, fragment)
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        fragmentTransaction.commitAllowingStateLoss()
    }

    private fun requestNewInterstitial(enum: IRInterstitialVersionEnum):IRInterstitial {
        val irInterstitial = IRInterstitialFactory(adsInstance).create(enum)
        irInterstitial.requestNewInterstitial()
        return irInterstitial
    }


}