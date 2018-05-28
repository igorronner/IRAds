package com.igorronner.interstitialsample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.igorronner.irinterstitial.init.IRAds
import com.igorronner.irinterstitial.views.PurchaseActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        IRAds.openSplashScreen(this)


    }

    override fun onBackPressed() {
        super.onBackPressed()
//        IRAds.showInterstitalOnFinish(this)
    }

}
