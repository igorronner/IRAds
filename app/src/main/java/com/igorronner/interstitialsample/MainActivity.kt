package com.igorronner.interstitialsample

import android.os.Bundle
import com.igorronner.irinterstitial.views.PurchaseActivity

class MainActivity : PurchaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        IRAds.openSplashScreen(this)


    }


}
