package com.igorronner.irinterstitial.services

import android.content.Context
import com.igorronner.irinterstitial.init.IRAds
import com.igorronner.irinterstitial.preferences.MainPreference
import com.igorronner.irinterstitial.utils.AbstractRewardedVideoAdListener

class IRRewardedVideoAdService(val context: Context) {

    private val rewardedVideo: IRRewardedVideo = IRRewardedVideoAd(context)

    @JvmOverloads
    fun showRewardedVideo(force: Boolean = true, listener: OnRewardedVideoListener) {
        if (IRAds.isPremium(context)) {
            return
        }

        rewardedVideo.load(force, object : AbstractRewardedVideoAdListener(context) {
            override fun onRewardedVideoAdOpened() {
                listener.onRewardedVideoAdOpened()
            }

            override fun onRewardedVideoAdLoaded() {
                listener.onRewardedVideoAdLoaded()

            }

            override fun onRewardedVideoAdClosed() {
                listener.onRewardedVideoAdClosed(MainPreference.hasPremiumDays(context))
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