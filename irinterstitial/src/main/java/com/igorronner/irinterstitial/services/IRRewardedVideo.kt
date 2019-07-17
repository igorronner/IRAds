package com.igorronner.irinterstitial.services

import com.google.android.gms.ads.reward.RewardedVideoAdListener

interface IRRewardedVideo {
    fun load(force: Boolean, adListener: RewardedVideoAdListener)
    fun requestRewardedVideoAd()
}