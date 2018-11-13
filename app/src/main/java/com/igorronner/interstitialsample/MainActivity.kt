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
        adsInstance = IRAds.newInstance(this)
        adsInstance.openSplashScreen()


        setContentView(R.layout.activity_main)


        button1.setOnClickListener {
            adsInstance.showInterstitialBeforeIntent(Intent(this, AnotherActivity::class.java))
        }

        button2.setOnClickListener {
            adsInstance.showInterstitialBeforeIntent(Intent(this, FragmentSampleActivity::class.java))
        }
    }

    override fun onBackPressed() {
        adsInstance.showInterstitialOnFinish()
    }

    override fun onStop() {
        adsInstance.onStop()
        super.onStop()

    }

    override fun onResume() {
        adsInstance.onResume()
        super.onResume()
    }
}
