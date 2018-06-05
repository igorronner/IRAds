package com.igorronner.irinterstitial.services

import android.app.Activity
import android.support.v7.app.AlertDialog
import android.util.Log
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.SkuType
import com.igorronner.irinterstitial.R
import com.igorronner.irinterstitial.init.ConfigUtil
import com.igorronner.irinterstitial.preferences.MainPreference


class PurchaseService(var activity: Activity) : PurchasesUpdatedListener {


    interface ProductPurchasedListener {
        fun onProductPurchased()
    }

    var productPurchasedListener: ProductPurchasedListener? = null

    override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?) {
        handlePurchasesResult(responseCode, purchases)
    }


    private lateinit var billingClient: BillingClient

    fun onCreate() {
        billingClient = BillingClient.newBuilder(activity).setListener(this).build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(@BillingClient.BillingResponse billingResponseCode: Int) {
                if (billingResponseCode == BillingClient.BillingResponse.OK) {
                    val skuList = ArrayList<String>()
                    skuList.add(ConfigUtil.PRODUCT_SKU)
                    val params = SkuDetailsParams.newBuilder()
                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
                    billingClient.querySkuDetailsAsync(params.build(), { _, _ -> // Process the result.
                                                                        })
                }
            }
            override fun onBillingServiceDisconnected() {
                Log.d("billingClient", "onBillingServiceDisconnected");
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })

    }

    fun onResume() {
        if (!::billingClient.isInitialized)
            return

        val purchasesResult = billingClient.queryPurchases(SkuType.INAPP)
        val responseCode = purchasesResult.responseCode
        val purchases = purchasesResult.purchasesList
        handlePurchasesResult(responseCode, purchases)

    }

    fun showDialogPremium() {
        val dialog = AlertDialog.Builder(activity)

        dialog.setTitle(R.string.buy_premium)
        dialog.setMessage(R.string.message_buy_premium)

        dialog.setPositiveButton(R.string.purchase) { _, _ -> purchase() }

        dialog.setNegativeButton(R.string.cancel, null)
        dialog.show()
    }

    private fun handlePurchasesResult(responseCode: Int, purchases: MutableList<Purchase>?){
        if (responseCode == BillingClient.BillingResponse.OK && purchases != null) {
            for (purchase in purchases) {
                if(purchase.sku == ConfigUtil.PRODUCT_SKU) {
                    MainPreference.setPremium(activity)
                    productPurchasedListener?.onProductPurchased()
                }
            }
        } else if (responseCode == BillingClient.BillingResponse.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
        } else {
            // Handle any other error codes.
        }
    }

    fun purchase(){
        val flowParams = BillingFlowParams.newBuilder()
                .setSku(ConfigUtil.PRODUCT_SKU)
                .setType(BillingClient.SkuType.INAPP) // SkuType.SUB for subscription
                .build()
        billingClient.launchBillingFlow(activity, flowParams)
    }
}