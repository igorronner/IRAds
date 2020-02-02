package com.igorronner.irinterstitial.services

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.annotation.IdRes
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import android.util.Log
import com.igorronner.irinterstitial.BuildConfig
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
        irInterstitial = requestNewInterstitial(EXPENSIVE_INTERSTITIAL)
        irInterstitial.requestNewInterstitial()
    }

    constructor(adsInstance: IRAds,
                irInterstitialVersionEnum: IRInterstitialVersionEnum = EXPENSIVE_INTERSTITIAL){
        this.adsInstance = adsInstance
        irInterstitial = requestNewInterstitial(irInterstitialVersionEnum)

    }

    fun showInterstitial(
            finish: Boolean,
            force: Boolean
    ){

        log("showInterstitial()")
        if (MainPreference.isPremium(adsInstance.activity.applicationContext)){
            finish(adsInstance.activity, finish)
            return
        }

        when {
            ConfigUtil.EXPENSIVE_INTERSTITIAL_ID.isIdValid() -> showExpensiveInterstitial(finish, force)
            ConfigUtil.MID_INTERSTITIAL_ID.isIdValid() -> showMidFloorInterstitial(finish, force)
            ConfigUtil.INTERSTITIAL_ID.isIdValid() -> showDefaultInterstitial(finish, force)
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
            ConfigUtil.EXPENSIVE_INTERSTITIAL_ID.isIdValid() -> showExpensiveInterstitialBeforeIntent(intent, finishAll, force)
            ConfigUtil.MID_INTERSTITIAL_ID.isIdValid() -> showMidFloorInterstitialBeforeIntent(intent, finishAll, force)
            ConfigUtil.INTERSTITIAL_ID.isIdValid() -> showDefaultInterstitialBeforeIntent(intent, finishAll, force)
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
            ConfigUtil.EXPENSIVE_INTERSTITIAL_ID.isIdValid() -> showExpensiveInterstitialBeforeFragment(fragment, containerViewId, fragmentActivity, force)
            ConfigUtil.MID_INTERSTITIAL_ID.isIdValid() -> showMidFloorInterstitialBeforeFragment(fragment, containerViewId, fragmentActivity, force)
            ConfigUtil.INTERSTITIAL_ID.isIdValid() -> showDefaultInterstitialBeforeFragment(fragment, containerViewId, fragmentActivity, force)
            else -> replaceFragment(fragment,  containerViewId, fragmentActivity)
        }
    }

    private fun showDefaultInterstitialBeforeFragment(
            fragment: Fragment,
            containerViewId: Int,
            fragmentActivity: FragmentActivity,
            force: Boolean
    ) {
        log("showDefaultInterstitialBeforeFragment()")
        val activity = adsInstance.activity
        if (MainPreference.isPremium(activity)){
            replaceFragment(fragment,  containerViewId, fragmentActivity)
            return
        }

        if (irInterstitial !is  IRInterstitialAd)
            irInterstitial = requestNewInterstitial(INTERSTITIAL_AD)

        irInterstitial.load(force, object : IRInterstitialListener {
            override fun onAdClosed() {
                log("showDefaultInterstitialBeforeFragment() onAdClosed")
                replaceFragment(fragment,  containerViewId, fragmentActivity)
                irInterstitial = requestNewInterstitial(EXPENSIVE_INTERSTITIAL)
            }

            override fun onFailed() {
                log("showDefaultInterstitialBeforeFragment() onFailed")
                replaceFragment(fragment,  containerViewId, fragmentActivity)
                irInterstitial = requestNewInterstitial(EXPENSIVE_INTERSTITIAL)
            }

            override fun onNotLoaded() {
                log("showDefaultInterstitialBeforeFragment() onNotLoaded")
                replaceFragment(fragment,  containerViewId, fragmentActivity)
            }
        })
    }


    private fun showExpensiveInterstitialBeforeFragment(
            fragment: Fragment,
            containerViewId: Int,
            fragmentActivity: FragmentActivity,
            force: Boolean
    ) {
        log("showExpensiveInterstitialBeforeFragment()")
        val activity = adsInstance.activity
        if (MainPreference.isPremium(activity)){
            replaceFragment(fragment,  containerViewId, fragmentActivity)
            return
        }

        if (irInterstitial !is  IRExpensiveInterstitialAd)
            irInterstitial = requestNewInterstitial(EXPENSIVE_INTERSTITIAL)

        irInterstitial.load(force, object : IRInterstitialListener {
            override fun onAdClosed() {
                log("showExpensiveInterstitialBeforeFragment() onAdClosed()")
                replaceFragment(fragment,  containerViewId, fragmentActivity)
                irInterstitial = requestNewInterstitial(EXPENSIVE_INTERSTITIAL)
            }

            override fun onFailed() {
                log("showExpensiveInterstitialBeforeFragment() onFailed()")
                showMidFloorInterstitialBeforeFragment(fragment, containerViewId, fragmentActivity, force)
            }

            override fun onNotLoaded() {
                log("showExpensiveInterstitialBeforeFragment() onNotLoaded()")
                showMidFloorInterstitialBeforeFragment(fragment, containerViewId, fragmentActivity, force)
            }
        })
    }

    private fun showMidFloorInterstitialBeforeFragment(
            fragment: Fragment,
            containerViewId: Int,
            fragmentActivity: FragmentActivity,
            force: Boolean
    ) {
        log("showMidFloorInterstitialBeforeFragment()")
        val activity = adsInstance.activity
        if (MainPreference.isPremium(activity)){
            replaceFragment(fragment,  containerViewId, fragmentActivity)
            return
        }

        if (irInterstitial !is  IRMidFloorInterstitialAd)
            irInterstitial = requestNewInterstitial(MID_FLOOR_INTERSTITIAL)

        irInterstitial.load(force, object : IRInterstitialListener {
            override fun onAdClosed() {
                log("showMidFloorInterstitialBeforeFragment() onAdClosed()")
                replaceFragment(fragment,  containerViewId, fragmentActivity)
                irInterstitial = requestNewInterstitial(EXPENSIVE_INTERSTITIAL)
            }

            override fun onFailed() {
                log("showMidFloorInterstitialBeforeFragment() onFailed()")
                replaceFragment(fragment,  containerViewId, fragmentActivity)
                showDefaultInterstitialBeforeFragment(fragment, containerViewId,fragmentActivity, force)
            }

            override fun onNotLoaded() {
                log("showMidFloorInterstitialBeforeFragment() onNotLoaded()")
                showDefaultInterstitialBeforeFragment(fragment, containerViewId,fragmentActivity, force)
            }
        })
    }


    fun showDefaultInterstitialBeforeIntent(
            intent: Intent,
            finishAll: Boolean,
            force: Boolean
    ) {
        log("showDefaultInterstitialBeforeIntent")
        val activity = adsInstance.activity
        if (MainPreference.isPremium(activity)){
            finishWithIntent(finishAll, intent)
            return
        }

        if (irInterstitial !is  IRInterstitialAd)
            irInterstitial = requestNewInterstitial(INTERSTITIAL_AD)

        irInterstitial.load(force, object : IRInterstitialListener {
            override fun onAdClosed() {
                log("showDefaultInterstitialBeforeIntent onAdClosed()")
                finishWithIntent(finishAll, intent)
                irInterstitial = requestNewInterstitial(EXPENSIVE_INTERSTITIAL)
            }

            override fun onFailed() {
                log("showDefaultInterstitialBeforeIntent onFailed()")
                finishWithIntent(finishAll, intent)
                irInterstitial = requestNewInterstitial(EXPENSIVE_INTERSTITIAL)
            }

            override fun onNotLoaded() {
                log("showDefaultInterstitialBeforeIntent onNotLoaded()")
                finishWithIntent(finishAll, intent)
            }
        })
    }

    fun showExpensiveInterstitialBeforeIntent(
            intent: Intent,
            finishAll: Boolean,
            force: Boolean
    ) {
        log("showExpensiveInterstitialBeforeIntent()")
        val activity = adsInstance.activity
        if (MainPreference.isPremium(activity)){
            finishWithIntent(finishAll, intent)
            return
        }

        if (irInterstitial !is  IRExpensiveInterstitialAd)
            irInterstitial = requestNewInterstitial(EXPENSIVE_INTERSTITIAL)

        irInterstitial.load(force, object : IRInterstitialListener {
            override fun onAdClosed() {
                log("showExpensiveInterstitialBeforeIntent onAdClosed()")
                finishWithIntent(finishAll, intent)
                irInterstitial = requestNewInterstitial(EXPENSIVE_INTERSTITIAL)
            }

            override fun onFailed() {
                log("showExpensiveInterstitialBeforeIntent onFailed()")
                showMidFloorInterstitialBeforeIntent(intent, finishAll, force)
            }

            override fun onNotLoaded() {
                log("showExpensiveInterstitialBeforeIntent onNotLoaded()")
                showMidFloorInterstitialBeforeIntent(intent, finishAll, force)
            }
        })
    }

    fun showMidFloorInterstitialBeforeIntent(
            intent: Intent,
            finishAll: Boolean,
            force: Boolean
    ) {
        log("showMidFloorInterstitialBeforeIntent()")
        val activity = adsInstance.activity
        if (MainPreference.isPremium(activity)){
            finishWithIntent(finishAll, intent)
            return
        }

        if (irInterstitial !is IRMidFloorInterstitialAd)
            irInterstitial = requestNewInterstitial(MID_FLOOR_INTERSTITIAL)

        irInterstitial.load(force, object : IRInterstitialListener {
            override fun onAdClosed() {
                log("showMidFloorInterstitialBeforeIntent() onAdClosed()")
                finishWithIntent(finishAll, intent)
                irInterstitial = requestNewInterstitial(EXPENSIVE_INTERSTITIAL)
            }

            override fun onFailed() {
                log("showMidFloorInterstitialBeforeIntent() onFailed()")
                showDefaultInterstitialBeforeIntent(intent, finishAll, force)
            }

            override fun onNotLoaded() {
                log("showMidFloorInterstitialBeforeIntent() onNotLoaded()")
                showDefaultInterstitialBeforeIntent(intent, finishAll, force)
            }
        })
    }

    fun showDefaultInterstitial(
            finish: Boolean,
            force: Boolean
    ) {
        log("showDefaultInterstitial()")
        if (MainPreference.isPremium(adsInstance.activity.applicationContext)){
            finish(adsInstance.activity, finish)
            return
        }

        if (irInterstitial !is  IRInterstitialAd)
            irInterstitial = requestNewInterstitial(INTERSTITIAL_AD)

        irInterstitial.load(force, object : IRInterstitialListener {
            override fun onAdClosed() {
                log("showDefaultInterstitial() onAdClosed()")
                finish(adsInstance.activity, finish)
                irInterstitial = requestNewInterstitial(EXPENSIVE_INTERSTITIAL)
            }

            override fun onFailed() {
                log("showDefaultInterstitial() onFailed()")
                finish(adsInstance.activity, finish)
                irInterstitial = requestNewInterstitial(INTERSTITIAL_AD)
            }

            override fun onNotLoaded() {
                log("showDefaultInterstitial() onNotLoaded()")
                finish(adsInstance.activity, finish)
            }
        })
    }

    fun showExpensiveInterstitial(
            finish: Boolean,
            force: Boolean
    ){

        log("showExpensiveInterstitial()")
        if (MainPreference.isPremium(adsInstance.activity.applicationContext)){
            finish(adsInstance.activity, true)
            return
        }

        if (irInterstitial !is IRExpensiveInterstitialAd)
            irInterstitial  = requestNewInterstitial(EXPENSIVE_INTERSTITIAL)

        irInterstitial.load(force, object : IRInterstitialListener {
            override fun onAdClosed() {
                log("showExpensiveInterstitial() onAdClosed")
                finish(adsInstance.activity, finish)
                irInterstitial = requestNewInterstitial(EXPENSIVE_INTERSTITIAL)
            }

            override fun onFailed() {
                log("showExpensiveInterstitial() onFailed")
                showMidFloorInterstitial(finish, force)
            }

            override fun onNotLoaded() {
                log("showExpensiveInterstitial() onNotLoaded")
                showMidFloorInterstitial(finish, force)
            }
        })
    }

    fun showMidFloorInterstitial(
            finish: Boolean,
            force: Boolean
    ){
        log("showMidFloorInterstitial()")
        if (MainPreference.isPremium(adsInstance.activity.applicationContext)){
            finish(adsInstance.activity, true)
            return
        }

        if (irInterstitial !is IRMidFloorInterstitialAd)
            irInterstitial = requestNewInterstitial(MID_FLOOR_INTERSTITIAL)

        irInterstitial.load(force, object : IRInterstitialListener{
            override fun onAdClosed() {
                log("showMidFloorInterstitial() onAdClosed()")
                finish(adsInstance.activity, finish)
                irInterstitial = requestNewInterstitial(EXPENSIVE_INTERSTITIAL)
            }

            override fun onFailed() {
                log("showMidFloorInterstitial() onFailed()")
                showDefaultInterstitial(finish, force)
            }

            override fun onNotLoaded() {
                log("showMidFloorInterstitial() onNotLoaded()")
                showDefaultInterstitial(finish, force)
            }
        })
    }

    fun showOnlyExpensiveInterstitial(
            finish: Boolean,
            force: Boolean
    ){
        log("showOnlyExpensiveInterstitial()")
        if (MainPreference.isPremium(adsInstance.activity.applicationContext)){
            finish(adsInstance.activity, true)
            return
        }

        if (irInterstitial !is IRExpensiveInterstitialAd)
            irInterstitial  = requestNewInterstitial(EXPENSIVE_INTERSTITIAL)

        irInterstitial.load(force, object : IRInterstitialListener {
            override fun onAdClosed() {
                log("showOnlyExpensiveInterstitial() onAdClosed()")
                finish(adsInstance.activity, finish)
                irInterstitial = requestNewInterstitial(EXPENSIVE_INTERSTITIAL)
            }

            override fun onFailed() {
                log("showOnlyExpensiveInterstitial() onFailed()")
                finish(adsInstance.activity, finish)
            }

            override fun onNotLoaded() {
                log("showOnlyExpensiveInterstitial() onNotLoaded()")
                finish(adsInstance.activity, finish)
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

    fun forceShowExpensiveInterstitial(finish: Boolean){
        showOnlyExpensiveInterstitial(finish, true)
    }

    fun forceShowInterstitial(){
        showInterstitial(finish = true, force = true)
    }

    fun finish(activity: Activity, finish: Boolean){
        log("finish()")
        if (finish)
            activity.finish()
    }

    fun forceShowInterstitialBeforeIntent(intent: Intent, finish: Boolean){
        showInterstitialBeforeIntent(intent, finish, true)
    }

    fun forceShowExpensiveInterstitialBeforeIntent(intent: Intent, finish: Boolean){
        showInterstitialBeforeIntent(intent, finish, true)
    }

    fun forceShowInterstitialBeforeIntent(intent: Intent){
        showInterstitialBeforeIntent(intent, finishAll = true, force = true)
    }

    fun showInterstitialBeforeIntent(intent: Intent, finishAll: Boolean){
        showInterstitialBeforeIntent(intent, finishAll, false)
    }

    fun showExpensiveInterstitialBeforeIntent(intent: Intent, finishAll: Boolean){
        showExpensiveInterstitialBeforeIntent(intent, finishAll, false)
    }

    fun showInterstitialBeforeIntent(intent: Intent) {
        showInterstitialBeforeIntent(intent, finishAll = false, force = false)
    }

    fun finishWithIntent(finishAll: Boolean, intent: Intent){
        log("finishWithIntent()")
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

    fun replaceFragment(
            fragment: Fragment,
            @IdRes  containerViewId:Int,
            fragmentActivity: FragmentActivity

    ){
        log("replaceFragment()")
        val fragmentTransaction = fragmentActivity.supportFragmentManager
                .beginTransaction()
        fragmentTransaction.replace(containerViewId, fragment)
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        fragmentTransaction.commitAllowingStateLoss()
    }

    private fun requestNewInterstitial(enum: IRInterstitialVersionEnum) : IRInterstitial {
        log("requestNewInterstitial()")
        val irInterstitial: IRInterstitial = when (enum) {
            EXPENSIVE_INTERSTITIAL -> {
                log("requestNewInterstitial() EXPENSIVE_INTERSTITIAL")
                when {
                    ConfigUtil.EXPENSIVE_INTERSTITIAL_ID.isIdValid() -> IRExpensiveInterstitialAd(adsInstance)
                    ConfigUtil.MID_INTERSTITIAL_ID.isIdValid() -> IRMidFloorInterstitialAd(adsInstance)
                    else -> IRInterstitialAd(adsInstance)
                }
            }
            MID_FLOOR_INTERSTITIAL -> {
                log("requestNewInterstitial() MID_FLOOR_INTERSTITIAL")
                when {
                    ConfigUtil.MID_INTERSTITIAL_ID.isIdValid() -> IRMidFloorInterstitialAd(adsInstance)
                    else -> IRInterstitialAd(adsInstance)
                }
            }
            else -> {
                log("requestNewInterstitial() NO_FLOOR")
                when {
                    (ConfigUtil.INTERSTITIAL_ID.isIdValid()) -> IRInterstitialAd(adsInstance)
                    else -> throw Exception("Preencher pelo menos um id de Interstitial **setInterstitialId()")
                }
            }
        }

        irInterstitial.requestNewInterstitial()
        return irInterstitial
    }

    private fun String?.isIdValid() = this?.isNotEmpty() ?: false

    private fun log(message: String){
        if (BuildConfig.DEBUG) Log.d("IRInterstitialService", message)
    }

}