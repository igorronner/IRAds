package com.igorronner.irinterstitial.services

import android.app.Activity
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.igorronner.irinterstitial.init.ConfigUtil
import com.igorronner.irinterstitial.init.IRAds
import com.igorronner.irinterstitial.preferences.MainPreference
import com.igorronner.irinterstitial.utils.AbstractRewardedVideoAdListener

class IRRewardedVideoAd(
        private val activity: Activity
) : IRRewardedVideo {

    private val rewardedVideoAd: RewardedVideoAd = MobileAds.getRewardedVideoAdInstance(activity)

    override fun load(force: Boolean, adListener: RewardedVideoAdListener) {
        if (IRAds.isPremium(activity)) {
            return
        }

        rewardedVideoAd.rewardedVideoAdListener = object : AbstractRewardedVideoAdListener(activity) {
            override fun onRewardedVideoAdClosed() {
                super.onRewardedVideoAdClosed()
                adListener.onRewardedVideoAdClosed()
                requestRewardedVideoAd()
                rewardedVideoAd.rewardedVideoAdListener = null
            }

            override fun onRewardedVideoAdLeftApplication() {
                super.onRewardedVideoAdLeftApplication()
                adListener.onRewardedVideoAdLeftApplication()
            }

            override fun onRewardedVideoAdLoaded() {
                super.onRewardedVideoAdLoaded()
                adListener.onRewardedVideoAdLoaded()

                if (force && rewardedVideoAd.rewardedVideoAdListener != null) rewardedVideoAd.show()
            }

            override fun onRewardedVideoAdOpened() {
                super.onRewardedVideoAdOpened()
                adListener.onRewardedVideoAdOpened()
            }

            override fun onRewardedVideoCompleted() {
                super.onRewardedVideoCompleted()
                adListener.onRewardedVideoCompleted()
            }

            override fun onRewarded(reward: RewardItem?) {
                super.onRewarded(reward)
                adListener.onRewarded(reward)

                if (reward != null) MainPreference.setDaysPremium(activity, reward.amount)
            }

            override fun onRewardedVideoStarted() {
                super.onRewardedVideoStarted()
                adListener.onRewardedVideoStarted()
            }

            override fun onRewardedVideoAdFailedToLoad(p0: Int) {
                super.onRewardedVideoAdFailedToLoad(p0)
                adListener.onRewardedVideoAdFailedToLoad(p0)
            }
        }

        if (rewardedVideoAd.isLoaded) {
            rewardedVideoAd.show()
        } else {
            requestRewardedVideoAd()
        }
    }

    override fun requestRewardedVideoAd() {
        if (ConfigUtil.REWARDED_VIDEO_ID.isNullOrBlank()) {
            return
        }

        val adRequest = AdRequest.Builder()
                .build()

        rewardedVideoAd.loadAd(ConfigUtil.REWARDED_VIDEO_ID, adRequest)
        sendEvent("requestRewardedVideoAd")
    }

    private fun sendEvent(event: String) {
        AnalyticsService(activity).logEvent(event)
        Log.d(this::class.java.simpleName, event)
    }

}