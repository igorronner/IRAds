package com.igorronner.irinterstitial.services

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics



class AnalyticsService(val context: Context) {

    private val mFirebaseAnalytics: FirebaseAnalytics  = FirebaseAnalytics.getInstance(context)

    fun logEvent(event: String){
        mFirebaseAnalytics.logEvent(event, Bundle())
    }
}