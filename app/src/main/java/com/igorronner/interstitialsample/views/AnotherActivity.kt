package com.igorronner.interstitialsample.views

import android.os.Bundle
import com.android.billingclient.api.SkuDetails
import com.igorronner.interstitialsample.R
import com.igorronner.irinterstitial.init.ConfigUtil
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
                true)

    }

    override fun onBackPressed() {
        super.onBackPressed()
        adsInstance.showInterstitial()
    }

    override fun onProductList(list: List<SkuDetails>?) {
        if (!list.isNullOrEmpty()) {
            list.find {
                it.sku ==  ConfigUtil.PRODUCT_SKU
            }?.let { skuDetails ->
                purchase.setOnClickListener {
                    showDialogPremium(skuDetails)
                }
            }
        }
    }

    override fun onProductsPurchased() {
        //TODO Refresh View
    }
}