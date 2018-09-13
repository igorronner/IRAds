package com.igorronner.irinterstitial.views

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.SkuType
import com.igorronner.irinterstitial.R
import com.igorronner.irinterstitial.init.ConfigUtil
import com.igorronner.irinterstitial.preferences.MainPreference


open class PurchaseActivity : AppCompatActivity(), PurchasesUpdatedListener {


    interface ProductPurchasedListener {
        fun onProductPurchased()
    }

    var productPurchasedListener: ProductPurchasedListener? = null

    override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?) {
        handlePurchasesResult(responseCode, purchases)
    }


    private lateinit var billingClient: BillingClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        billingClient = BillingClient.newBuilder(this).setListener(this).build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(@BillingClient.BillingResponse billingResponseCode: Int) {
                Log.d("billingClient", "onBillingSetupFinished ")
                if (billingResponseCode == BillingClient.BillingResponse.OK) {
                    Log.d("billingClient", "BillingClient.BillingResponse.OK ")
                    val skuList = ArrayList<String>()
                    skuList.add(ConfigUtil.PRODUCT_SKU)
                    val params = SkuDetailsParams.newBuilder()
                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
                    billingClient.querySkuDetailsAsync(params.build()) { responseCode, skuDetailsList ->
                        Log.d("billingClient", "querySkuDetailsAsync ")
                        Log.d("billingClient", "responseCode $responseCode")
                        skuDetailsList.forEach { skuDetails: SkuDetails? ->
                            Log.d("billingClient", "skuDetailsList " + skuDetails?.description)
                            Log.d("billingClient", "skuDetailsList " + skuDetails?.title)
                            Log.d("billingClient", "skuDetailsList " + skuDetails?.price)
                            Log.d("billingClient", "skuDetailsList " + skuDetails?.sku)
                        }

                    }
                }
            }
            override fun onBillingServiceDisconnected() {
                Log.d("billingClient", "onBillingServiceDisconnected");
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })

    }

    override fun onResume() {
        super.onResume()
        Log.d("billingClient", "onResume() ")
        if (!::billingClient.isInitialized)
            return

        Log.d("billingClient", "billingClient.isInitialized ")
        val purchasesResult = billingClient.queryPurchases(SkuType.INAPP)
        val responseCode = purchasesResult.responseCode
        val purchases = purchasesResult?.purchasesList
        purchases?.forEach {
            purchase: Purchase? ->
            Log.d("billingClient", "purchase " + purchase?.sku)
        }

        handlePurchasesResult(responseCode, purchases)

    }

    fun showDialogPremium() {
        val dialog = AlertDialog.Builder(this)

        dialog.setTitle(R.string.buy_premium)
        dialog.setMessage(R.string.message_buy_premium)

        dialog.setPositiveButton(R.string.purchase) { _, which -> purchase() }

        dialog.setNegativeButton(R.string.cancel, null)
        dialog.show()
    }

    private fun handlePurchasesResult(responseCode: Int, purchases: MutableList<Purchase>?){
        if (responseCode == BillingClient.BillingResponse.OK && purchases != null) {

            Log.d("billingClient", "handlePurchasesResult BillingClient.BillingResponse.OK")
            for (purchase in purchases) {
                if(purchase.sku == ConfigUtil.PRODUCT_SKU) {
                    MainPreference.setPremium(this)
                    productPurchasedListener?.onProductPurchased()
                    Log.d("billingClient", "onProductPurchased ")
                }
            }
        } else if (responseCode == BillingClient.BillingResponse.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            Log.d("billingClient", "handlePurchasesResult USER_CANCELED")
        } else {
            // Handle any other error codes.
            Log.d("billingClient", "handlePurchasesResult $responseCode")
        }
    }

    fun purchase(){
        val flowParams = BillingFlowParams.newBuilder()
                .setSku(ConfigUtil.PRODUCT_SKU)
                .setType(BillingClient.SkuType.INAPP) // SkuType.SUB for subscription
                .build()
        billingClient.launchBillingFlow(this@PurchaseActivity, flowParams)
    }
}