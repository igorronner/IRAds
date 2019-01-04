package com.igorronner.interstitialsample.views

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.android.billingclient.api.SkuDetails
import com.igorronner.interstitialsample.R
import com.igorronner.irinterstitial.init.IRAds
import com.igorronner.irinterstitial.services.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ProductsListListener, ProductPurchasedListener, PurchaseCanceledListener, PurchaseErrorListener {

    private lateinit var adsInstance:IRAds
    private lateinit var purchaseService: PurchaseService;

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

        purchaseService = PurchaseService(this)
        purchaseService.productsListListener = this
        purchaseService.productPurchasedListener = this
        purchaseService.purchaseErrorListener = this
        purchaseService.purchaseCanceledListener = this
        if (!IRAds.isPremium(this)){
            progressBar.visibility = View.VISIBLE
            contentLayout.visibility = View.GONE
            purchase.visibility = View.VISIBLE
        }
        adsInstance.loadNativeAd(true)
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        purchaseService.onResume()
    }

    override fun onBackPressed() {
        adsInstance.showInterstitialOnFinish()
    }

    override fun onProductsPurchased() {
        refreshScreen()
    }

    override fun onProductList(list: MutableList<SkuDetails>) {
        progressBar.visibility = View.GONE
        contentLayout.visibility = View.VISIBLE
        if (list.isNotEmpty()) {
            val skuDetails = list.first()
            purchase.text = skuDetails.price
            purchase.setOnClickListener {
                purchaseService.purchase()
            }
        }
    }

    override fun onError(responseCode: Int) {

    }

    override fun onCanceled() {
    }

    private fun refreshScreen(){
        purchase.visibility = View.GONE
        adsInstance.loadNativeAd(true)
    }
}