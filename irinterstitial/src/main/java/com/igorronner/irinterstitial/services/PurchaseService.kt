package com.igorronner.irinterstitial.services

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.SkuType
import com.igorronner.irinterstitial.R
import com.igorronner.irinterstitial.init.ConfigUtil
import com.igorronner.irinterstitial.init.IRAds
import com.igorronner.irinterstitial.preferences.MainPreference
import com.igorronner.irinterstitial.utils.Logger


class PurchaseService(var activity: Activity) : PurchasesUpdatedListener {

    init {
        startBilling()
    }

    var purchaseErrorListener: PurchaseErrorListener? = null
    var productPurchasedListener: ProductPurchasedListener? = null
    var productsListListener: ProductsListListener? = null
    var purchaseCanceledListener: PurchaseCanceledListener? = null

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        handlePurchasesResult(billingResult.responseCode, purchases)
    }

    private lateinit var billingClient: BillingClient

    private fun startBilling() {
        billingClient = BillingClient.newBuilder(activity)
                .setListener(this)
                .enablePendingPurchases()
                .build()

        Logger.log("billingClient", "Starting connection...")
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                val billingResponseCode = billingResult.responseCode

                Logger.log("billingClient", "onBillingSetupFinished ")
                Logger.log("billingClient", "billingResponseCode $billingResponseCode ")
                // TODO: handle other response codes

                productsListListener?.let { productsListListener ->
                    if (billingResponseCode == BillingClient.BillingResponseCode.OK) {
                        Logger.logInfo("billingClient", "BillingClient.BillingResponse.OK ")

                        val purchaseParams = SkuDetailsParams.newBuilder().apply {
                            setSkusList(listOf(ConfigUtil.PRODUCT_SKU))
                            setType(SkuType.INAPP)
                        }

                        billingClient.querySkuDetailsAsync(purchaseParams.build()) { result, skuDetailsList ->
                            Logger.log("billingClient", "querySkuDetailsAsync ")
                            Logger.log("billingClient", "responseCode ${result.responseCode}")

                            productsListListener.onProductList(skuDetailsList)

                            skuDetailsList?.forEach { skuDetails: SkuDetails? ->
                                Logger.log("billingClient", "skuDetailsList ${skuDetails?.description}")
                                Logger.log("billingClient", "skuDetailsList ${skuDetails?.title}")
                                Logger.log("billingClient", "skuDetailsList ${skuDetails?.price}")
                                Logger.log("billingClient", "skuDetailsList ${skuDetails?.sku}")
                            }
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
        Logger.log("billingClient", "onResume() ")
        if (!::billingClient.isInitialized || IRAds.isPremium(activity))
            return

        Logger.logInfo("billingClient", "billingClient.isInitialized ")
        val result = billingClient.queryPurchases(SkuType.INAPP)

        if (supportsSubscriptions()) {
            val subscriptionsResult = billingClient.queryPurchases(SkuType.SUBS)
            if (subscriptionsResult.responseCode == BillingClient.BillingResponseCode.OK) {
                result.purchasesList.addAll(subscriptionsResult.purchasesList)
            } else {
                Logger.logError(e = "Got an error response trying to query subscriptions")
            }
        } else {
            Logger.logWarning(w = "Subscriptions are not supported in this device")
        }

        val responseCode = result.responseCode
        val purchases = result.purchasesList
        purchases?.forEach { purchase: Purchase? ->
            Logger.log("billingClient", "purchase ${purchase?.sku}")
        }

        handlePurchasesResult(responseCode, purchases)

    }

    fun showDialogPremium(skuDetails: SkuDetails) {
        val dialog = AlertDialog.Builder(activity)

        dialog.setTitle(R.string.buy_premium)
        dialog.setMessage(R.string.message_buy_premium)

        dialog.setPositiveButton(R.string.purchase) { _, _ -> purchase(skuDetails) }

        dialog.setNegativeButton(R.string.cancel, null)
        dialog.show()
    }

    private fun handlePurchasesResult(responseCode: Int, purchases: MutableList<Purchase>?) {
        fun onError() {
            // Handle any other error codes.
            purchaseErrorListener?.onError(responseCode)
            Logger.logError("billingClient", "error handlePurchasesResult $responseCode")
            Logger.logError("billingClient", "purchases: ${purchases?.joinToString { it.sku }}")
        }

        if (purchases == null) {
            onError()
        } else when (responseCode) {
            BillingClient.BillingResponseCode.OK,
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                Logger.log("billingClient", "handlePurchasesResult BillingClient.BillingResponse.OK (or OWNED)")
                purchases.forEach { purchase ->
                    if (purchase.sku == ConfigUtil.PRODUCT_SKU || purchase.sku == ConfigUtil.SUBSCRIPTION_SKU) {
                        MainPreference.setPremium(activity)
                        productPurchasedListener?.onProductsPurchased()
                        Logger.log("billingClient", "onProductPurchased ")
                    }
                }
            }

            BillingClient.BillingResponseCode.USER_CANCELED -> {
                // Handle an error caused by a user cancelling the purchase flow.
                purchaseCanceledListener?.onCanceled()
            }

            else -> {
                onError()
            }
        }
    }

    private fun supportsSubscriptions(): Boolean {
        val responseCode = billingClient.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS).responseCode
        return responseCode == BillingClient.BillingResponseCode.OK
    }

    fun purchase(skuDetails: SkuDetails) {
        Logger.log("billingClient", "launching purchase flow for sku '${skuDetails.sku}'")
        val flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails) // Type is already in SkuDetails
                .build()
        billingClient.launchBillingFlow(activity, flowParams)
    }

}
