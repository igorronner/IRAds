package com.igorronner.irinterstitial.services

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.igorronner.irinterstitial.init.ConfigUtil


class AnalyticsService(context: Context) {

    private val mFirebaseAnalytics: FirebaseAnalytics  = FirebaseAnalytics.getInstance(context)

    fun logEvent(event: String){
        mFirebaseAnalytics.logEvent(ConfigUtil.APP_PREFIX + event, Bundle())
    }
}