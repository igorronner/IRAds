package com.igorronner.irinterstitial.services

import android.app.Activity
import android.util.Log
import com.android.billingclient.api.*


class SubscribeService(private var activity: Activity) : PurchasesUpdatedListener {

    val skuList = ArrayList<String>()
    init {
        skuList.add("assinatura_anual_br_2017")
        skuList.add("nova_assinatura_mensal")
        skuList.add("premium_desconto_50")
        skuList.add("premium_desconto_30")
        skuList.add("nova_assinatura_anual")
        skuList.add("premium_trial_anual")
        startBilling()
    }


    var purchaseErrorListener: PurchaseErrorListener? = null
    var productPurchasedListListener: ProductPurchasedListListener? = null
    var productsListListener:ProductsListListener? = null
    var purchaseCanceledListener:PurchaseCanceledListener? = null

    override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?) {
        handlePurchasesResult(responseCode, purchases)
    }

    private lateinit var billingClient: BillingClient

    private fun startBilling() {
        billingClient = BillingClient.newBuilder(activity).setListener(this).build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(@BillingClient.BillingResponse billingResponseCode: Int) {
                if (billingResponseCode == BillingClient.BillingResponse.OK) {
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
                Log.d("billingClient", "onBillingServiceDisconnected");
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })

    }

    fun onResume() {
        if (!::billingClient.isInitialized)
            return

        val purchasesResult = billingClient.queryPurchases(BillingClient.SkuType.SUBS)
        val responseCode = purchasesResult.responseCode
        val purchases = purchasesResult.purchasesList
        handlePurchasesResult(responseCode, purchases)
    }


    private fun handlePurchasesResult(responseCode: Int, purchases: MutableList<Purchase>?){
        Log.d("billingClient", "responseCode $responseCode")

        when (responseCode){
            BillingClient.BillingResponse.OK -> {
                purchases?.let {
                    productPurchasedListListener?.onProductsPurchasedList(it)
                }
            }
            BillingClient.BillingResponse.USER_CANCELED -> {
                purchaseCanceledListener?.onCanceled()

            }
            BillingClient.BillingResponse.ITEM_ALREADY_OWNED -> {
                Log.d("billingClient", "handlePurchasesResult ITEM_ALREADY_OWNED")
            }
            BillingClient.BillingResponse.SERVICE_DISCONNECTED -> {
                Log.d("billingClient", "handlePurchasesResult SERVICE_DISCONNECTED")
            }
            BillingClient.BillingResponse.SERVICE_UNAVAILABLE -> {
                Log.d("billingClient", "handlePurchasesResult SERVICE_UNAVAILABLE")
            }
            else -> {
                Log.d("billingClient", "handlePurchasesResult $responseCode")
                purchaseErrorListener?.onError(responseCode)
            }

        }
    }

    fun purchase(sku:String){

        val flowParams = BillingFlowParams.newBuilder()
                .setSku(sku)
                .setType(BillingClient.SkuType.SUBS)
                .build()
        billingClient.launchBillingFlow(activity, flowParams)
    }



}