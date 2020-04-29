package com.igorronner.irinterstitial.services

import android.content.Context
import android.content.Intent
import com.igorronner.irinterstitial.init.IRAds
import com.igorronner.irinterstitial.preferences.MainPreference
import com.igorronner.irinterstitial.utils.AbstractRewardedVideoAdListener

class IRRewardedVideoAdService(val context: Context) {

    private val rewardedVideo: IRRewardedVideoAd = IRRewardedVideoAd(context)

    @JvmOverloads
    fun showRewardedVideo(
            force: Boolean = true,
            thenBecomePremium: Boolean = false,
            listener: OnRewardedVideoListener) {
        if (IRAds.isPremium(context)) {
            return
        }

        rewardedVideo.load(force, thenBecomePremium, object : AbstractRewardedVideoAdListener(context) {
            override fun onRewardedVideoAdOpened() {
                listener.onRewardedVideoAdOpened()
            }

            override fun onRewardedVideoAdLoaded() {
                listener.onRewardedVideoAdLoaded()

            }

            override fun onRewardedVideoAdClosed() {
                listener.onRewardedVideoAdClosed(MainPreference.wasRewarded(context))
                MainPreference.setWasRewarded(context, false)
            }

            override fun onRewardedVideoAdFailedToLoad(p0: Int) {
                listener.onRewardedVideoAdFailedToLoad()
            }
        })
    }

    fun requestRewardedVideo() {
        if (IRAds.isPremium(context)) {
            return
        }

        rewardedVideo.requestRewardedVideoAd()
    }

    interface OnRewardedVideoListener {
        fun onRewardedVideoAdOpened()
        fun onRewardedVideoAdLoaded()
        fun onRewardedVideoAdClosed(earned: Boolean)
        fun onRewardedVideoAdFailedToLoad()
    }

}