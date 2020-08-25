package com.igorronner.irinterstitial.services

import android.app.Activity
import com.android.billingclient.api.*
import com.igorronner.irinterstitial.extensions.toIRPurchaseList
import com.igorronner.irinterstitial.init.IRAds
import com.igorronner.irinterstitial.utils.Logger


class SubscribeService(private var activity: Activity) : PurchasesUpdatedListener {

    val skuList = ArrayList<String>()
    init {
        skuList.add("assinatura_anual_br_20172")
        skuList.add("nova_assinatura_mensal2")
        skuList.add("premium_desconto_50")
        skuList.add("premium_desconto_30")
        skuList.add("nova_assinatura_anual")
        skuList.add("premium_trial_anual2")
        startBilling()
    }


    var purchaseErrorListener: PurchaseErrorListener? = null
    var productPurchasedListListener: ProductPurchasedListListener? = null
    var productsListListener:ProductsListListener? = null
    var purchaseCanceledListener:PurchaseCanceledListener? = null

    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {
        handlePurchasesResult(result.responseCode, purchases)
    }

    private lateinit var billingClient: BillingClient

    private fun startBilling() {
        billingClient = BillingClient.newBuilder(activity)
                .setListener(this)
                .enablePendingPurchases()
                .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                val billingResponseCode = billingResult.responseCode

                if (billingResponseCode == BillingClient.BillingResponseCode.OK) {
                    val params = SkuDetailsParams.newBuilder()
                    params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS)
                    productsListListener?.let {
                        productsListListener ->
                        billingClient.querySkuDetailsAsync(params.build()) { _, skuDetailsList ->
                            productsListListener.onProductList(skuDetailsList)
                        }
                    }
                }
            }

            override fun onBillingServiceDisconnected() {
                Logger.logWarning("billingClient", "onBillingServiceDisconnected");
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })

    }

    fun onResume() {
        if (!::billingClient.isInitialized || IRAds.isPremium(activity))
            return

        val purchasesResult = billingClient.queryPurchases(BillingClient.SkuType.SUBS)
        val responseCode = purchasesResult.responseCode
        val purchases = purchasesResult.purchasesList
        handlePurchasesResult(responseCode, purchases)
    }


    private fun handlePurchasesResult(responseCode: Int, purchases: MutableList<Purchase>?){
        Logger.log("billingClient", "responseCode $responseCode")

        when (responseCode){
            BillingClient.BillingResponseCode.OK -> {
                purchases?.let {
                    productPurchasedListListener?.onProductsPurchasedList(it.toIRPurchaseList())
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                purchaseCanceledListener?.onCanceled()

            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                Logger.logWarning("billingClient", "handlePurchasesResult ITEM_ALREADY_OWNED")
            }
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> {
                Logger.logError("billingClient", "handlePurchasesResult SERVICE_DISCONNECTED")
            }
            BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE -> {
                Logger.logError("billingClient", "handlePurchasesResult SERVICE_UNAVAILABLE")
            }
            else -> {
                Logger.log("billingClient", "handlePurchasesResult $responseCode")
                purchaseErrorListener?.onError(responseCode)
            }

        }
    }

    fun purchase(skuDetails: SkuDetails) {
        val flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails) // Type is already in SkuDetails
                .build()
        billingClient.launchBillingFlow(activity, flowParams)
    }



}