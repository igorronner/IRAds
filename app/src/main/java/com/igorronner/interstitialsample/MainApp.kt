package com.igorronner.interstitialsample

import android.app.Application
import com.igorronner.irinterstitial.init.IRInterstitial

class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()

        IRInterstitial.startInit("PLACE_YOUR_GOOGLE_CLIENTE_ID", "PLACE_YOUR_ADD_UNIT_ID")
                .setLogo(R.mipmap.ic_launcher_round)
                .build()

    }


}