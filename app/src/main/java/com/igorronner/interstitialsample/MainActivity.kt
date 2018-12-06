package com.igorronner.interstitialsample

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.igorronner.irinterstitial.init.IRAds
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var adsInstance:IRAds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adsInstance = IRAds.newInstance(this)
        adsInstance.openSplashScreen()

        button1.setOnClickListener {
            adsInstance.showInterstitialBeforeIntent(Intent(this, AnotherActivity::class.java))
        }

        button2.setOnClickListener {
            adsInstance.showInterstitialBeforeIntent(Intent(this, FragmentSampleActivity::class.java))
        }

        button3.setOnClickListener {
            adsInstance.showInterstitial(false)
        }

        adsInstance.loadNativeAd(true)
    }

    override fun onStop() {
        super.onStop()
        adsInstance.onStop()
    }

    override fun onResume() {
        super.onResume()
        adsInstance.onResume()
    }

    override fun onBackPressed() {
        adsInstance.showInterstitialOnFinish()
    }

}