package com.igorronner.irinterstitial.services

import android.app.Activity
import com.igorronner.irinterstitial.init.IRAds
import com.igorronner.irinterstitial.preferences.MainPreference
import com.igorronner.irinterstitial.utils.AbstractRewardedVideoAdListener

class IRRewardedVideoAdService(val activity: Activity) {

    private val rewardedVideo: IRRewardedVideo = IRRewardedVideoAd(activity)

    @JvmOverloads
    fun showRewardedVideo(force: Boolean = true, listener: OnRewardedVideoListener) {
        if (IRAds.isPremium(activity)) {
            return
        }

        rewardedVideo.load(force, object : AbstractRewardedVideoAdListener(activity) {
            override fun onRewardedVideoAdClosed() {
                listener.onRewardedVideoAdClosed(MainPreference.hasPremiumDays(activity))
            }

            override fun onRewardedVideoAdFailedToLoad(p0: Int) {
                listener.onRewardedVideoAdFailedToLoad()
            }
        })
    }

    interface OnRewardedVideoListener {
        fun onRewardedVideoAdClosed(earned: Boolean)
        fun onRewardedVideoAdFailedToLoad()
    }

}