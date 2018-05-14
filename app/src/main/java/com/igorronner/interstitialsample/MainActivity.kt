package com.igorronner.interstitialsample

import android.os.Bundle
import android.support.annotation.IntegerRes
import android.support.v7.app.AppCompatActivity
import com.igorronner.irinterstitial.init.IRInterstitial

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val test:IntArray = IntArray(3)
        test.sortedArray()
        IRInterstitial.openSplashScren(this)

    }
}
