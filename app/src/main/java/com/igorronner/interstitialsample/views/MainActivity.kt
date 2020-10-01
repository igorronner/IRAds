package com.igorronner.interstitialsample.views

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.SkuDetails
import com.igorronner.interstitialsample.R
import com.igorronner.irinterstitial.init.ConfigUtil
import com.igorronner.irinterstitial.init.IRAds
import com.igorronner.irinterstitial.init.IRBanner
import com.igorronner.irinterstitial.services.*
import com.igorronner.irinterstitial.utils.Logger
import kotlinx.android.synthetic.main.activity_main.*

class  MainActivity : AppCompatActivity(),
        ProductsListListener, ProductPurchasedListener, PurchaseCanceledListener, PurchaseErrorListener {

    private lateinit var adsInstance:IRAds
    private lateinit var purchaseService: PurchaseService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adsInstance = IRAds.newInstance(this)
        purchaseService = PurchaseService(this).apply {
            productsListListener = this@MainActivity
        }
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

        button4.setOnClickListener {
            IRBanner.loadNativeAd(this, showProgress = true)
        }

        button5.setOnClickListener {
            adsInstance.openDialogRewardedVideo(this)
        }

        button6.setOnClickListener {
            adsInstance.openDialogRewardedThenIntent(this, Intent(this, AnotherActivity::class.java))
        }

        IRBanner.loadNativeAd(this, showProgress = true)
        adsInstance.loadAdaptiveBanner(banner_ad, this)
    }

    override fun onResume() {
        super.onResume()
        purchaseService.onResume()
    }

    override fun onBackPressed() {
        adsInstance.showInterstitial()
    }

    override fun onProductsPurchased() {
        refreshScreen()
    }

    override fun onProductList(list: List<SkuDetails>?) {
        progressBar.visibility = View.GONE
        contentLayout.visibility = View.VISIBLE
        if (!list.isNullOrEmpty()) {
            // TODO: maybe use list.find { it.sku == ConfigUtil.PRODUCT_SKU }?.let { }
            val skuDetails = list.first().also {
                if (it.sku != ConfigUtil.PRODUCT_SKU) {
                    Logger.logWarning(w = "Listed product SKU (${it.sku}) != ${ConfigUtil.PRODUCT_SKU}")
                }
            }
            purchase.text = skuDetails.price
            purchase.setOnClickListener {
                purchaseService.purchase(skuDetails)
            }
        }
    }

    override fun onError(responseCode: Int) {}

    override fun onCanceled() {}

    private fun refreshScreen(){
        purchase.visibility = View.GONE
        IRBanner.loadNativeAd(this, showProgress = true)
    }
}