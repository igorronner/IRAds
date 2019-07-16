package com.igorronner.interstitialsample.views

import android.os.Bundle
import com.google.android.gms.ads.AdSize
import com.igorronner.interstitialsample.R
import com.igorronner.irinterstitial.init.IRAds
import com.igorronner.irinterstitial.views.PurchaseActivity
import kotlinx.android.synthetic.main.activity_another.*

class AnotherActivity : PurchaseActivity() {

    private lateinit var adsInstance: IRAds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_another)
        adsInstance = IRAds.newInstance(this)
        adsInstance.loadNativeOrBannerAd(
                findViewById(R.id.adViewContainer),
                findViewById(R.id.adViewNative),
                AdSize.LARGE_BANNER,
                true)

        purchase.setOnClickListener { showDialogPremium() }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        adsInstance.showInterstitial()
    }

    override fun onProductsPurchased() {
        //TODO Refresh View
    }
}