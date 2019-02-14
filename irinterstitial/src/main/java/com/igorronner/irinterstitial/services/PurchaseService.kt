package com.igorronner.irinterstitial.services

import android.app.Activity
import android.support.v7.app.AlertDialog
import android.util.Log
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.SkuType
import com.igorronner.irinterstitial.R
import com.igorronner.irinterstitial.init.ConfigUtil
import com.igorronner.irinterstitial.init.IRAds
import com.igorronner.irinterstitial.preferences.MainPreference


class PurchaseService(var activity: Activity) : PurchasesUpdatedListener {

    init {
        startBilling()
    }

    var purchaseErrorListener: PurchaseErrorListener? = null
    var productPurchasedListener: ProductPurchasedListener? = null
    var productsListListener:ProductsListListener? = null
    var purchaseCanceledListener:PurchaseCanceledListener? = null

    override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?) {
        handlePurchasesResult(responseCode, purchases)
    }

    private lateinit var billingClient: BillingClient

    fun startBilling() {
        billingClient = BillingClient.newBuilder(activity).setListener(this).build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(@BillingClient.BillingResponse billingResponseCode: Int) {
                Log.d("billingClient", "onBillingSetupFinished ")
                productsListListener?.let {
                    productsListListener ->
                    if (billingResponseCode == BillingClient.BillingResponse.OK) {
                        Log.d("billingClient", "BillingClient.BillingResponse.OK ")
                        val skuList = ArrayList<String>()
                        skuList.add(ConfigUtil.PRODUCT_SKU)
                        val params = SkuDetailsParams.newBuilder()
                        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)

                        billingClient.querySkuDetailsAsync(params.build()) { responseCode, skuDetailsList ->
                            Log.d("billingClient", "querySkuDetailsAsync ")
                            Log.d("billingClient", "responseCode $responseCode")
                            productsListListener.onProductList(skuDetailsList)
                            skuDetailsList.forEach { skuDetails: SkuDetails? ->
                                Log.d("billingClient", "skuDetailsList " + skuDetails?.description)
                                Log.d("billingClient", "skuDetailsList " + skuDetails?.title)
                                Log.d("billingClient", "skuDetailsList " + skuDetails?.price)
                                Log.d("billingClient", "skuDetailsList " + skuDetails?.sku)

                            }

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

    fun onResume() {
        Log.d("billingClient", "onResume() ")
        if (!::billingClient.isInitialized || IRAds.isPremium(activity))
            return

        Log.d("billingClient", "billingClient.isInitialized ")
        val purchasesResult = billingClient.queryPurchases(SkuType.INAPP)
        val responseCode = purchasesResult.responseCode
        val purchases = purchasesResult.purchasesList
        purchases?.forEach {
            purchase: Purchase? ->
            Log.d("billingClient", "purchase " + purchase?.sku)
        }

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
        if ((responseCode == BillingClient.BillingResponse.OK || responseCode == BillingClient.BillingResponse.ITEM_ALREADY_OWNED) && purchases!=null) {

            Log.d("billingClient", "handlePurchasesResult BillingClient.BillingResponse.OK")
            purchases.forEach {
                purchase->
                if(purchase.sku == ConfigUtil.PRODUCT_SKU) {
                    MainPreference.setPremium(activity)
                    productPurchasedListener?.onProductsPurchased()
                    Log.d("billingClient", "onProductPurchased ")
                }

            }
        } else if (responseCode == BillingClient.BillingResponse.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            purchaseCanceledListener?.onCanceled()
        } else {
            // Handle any other error codes.
            purchaseErrorListener?.onError(responseCode)
            Log.d("billingClient", "handlePurchasesResult $responseCode")
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