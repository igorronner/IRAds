package com.igorronner.interstitialsample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.igorronner.irinterstitial.init.IRAds

class AnotherActivity : AppCompatActivity() {


    private lateinit var adsInstance: IRAds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_another)
        adsInstance = IRAds.newInstance(this)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        adsInstance.showInterstitial()
    }
}
