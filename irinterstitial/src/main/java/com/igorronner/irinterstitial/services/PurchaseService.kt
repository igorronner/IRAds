package com.igorronner.irinterstitial.services

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.SkuType
import com.igorronner.irinterstitial.R
import com.igorronner.irinterstitial.extensions.toIRSkuDetailsList
import com.igorronner.irinterstitial.init.ConfigUtil
import com.igorronner.irinterstitial.init.IRAds
import com.igorronner.irinterstitial.preferences.MainPreference
import com.igorronner.irinterstitial.utils.Logger


class PurchaseService(var activity: Activity) : PurchasesUpdatedListener {

    init {
        startBilling()
        Logger
    }

    var purchaseErrorListener: PurchaseErrorListener? = null
    var productPurchasedListener: ProductPurchasedListener? = null
    var productsListListener: ProductsListListener? = null
    var purchaseCanceledListener: PurchaseCanceledListener? = null

    override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?) {
        handlePurchasesResult(responseCode, purchases)
    }

    private lateinit var billingClient: BillingClient

    private fun startBilling() {
        billingClient = BillingClient.newBuilder(activity).setListener(this).build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(@BillingClient.BillingResponse billingResponseCode: Int) {
                Logger.log("billingClient", "onBillingSetupFinished ")
                productsListListener?.let { productsListListener ->
                    if (billingResponseCode == BillingClient.BillingResponse.OK) {
                        Logger.logInfo("billingClient", "BillingClient.BillingResponse.OK ")
                        val skuList = ArrayList<String>()
                        skuList.add(ConfigUtil.PRODUCT_SKU)
                        val params = SkuDetailsParams.newBuilder()
                        params.setSkusList(skuList).setType(SkuType.INAPP)

                        billingClient.querySkuDetailsAsync(params.build(), object : SkuDetailsResponseListener {
                            override fun onSkuDetailsResponse(responseCode: Int, skuDetailsList: MutableList<SkuDetails>?) {
                                Logger.log("billingClient", "querySkuDetailsAsync ")
                                Logger.log("billingClient", "responseCode $responseCode")
                                skuDetailsList?.toIRSkuDetailsList()?.let { productsListListener.onProductList(it) }
                                skuDetailsList?.forEach { skuDetails: SkuDetails? ->
                                    Logger.log("billingClient", "skuDetailsList ${skuDetails?.description}")
                                    Logger.log("billingClient", "skuDetailsList ${skuDetails?.title}")
                                    Logger.log("billingClient", "skuDetailsList ${skuDetails?.price}")
                                    Logger.log("billingClient", "skuDetailsList ${skuDetails?.sku}")

                                }
                            }
                        })
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
        val purchasesResult = billingClient.queryPurchases(SkuType.INAPP)
        val responseCode = purchasesResult.responseCode
        val purchases = purchasesResult.purchasesList
        purchases?.forEach { purchase: Purchase? ->
            Logger.log("billingClient", "purchase ${purchase?.sku}")
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

    private fun handlePurchasesResult(responseCode: Int, purchases: MutableList<Purchase>?) {
        if ((responseCode == BillingClient.BillingResponse.OK || responseCode == BillingClient.BillingResponse.ITEM_ALREADY_OWNED) && purchases != null) {

            Logger.log("billingClient", "handlePurchasesResult BillingClient.BillingResponse.OK")
            purchases.forEach { purchase ->
                if (purchase.sku == ConfigUtil.PRODUCT_SKU) {
                    MainPreference.setPremium(activity)
                    productPurchasedListener?.onProductsPurchased()
                    Logger.log("billingClient", "onProductPurchased ")
                }

            }
        } else if (responseCode == BillingClient.BillingResponse.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            purchaseCanceledListener?.onCanceled()
        } else {
            // Handle any other error codes.
            purchaseErrorListener?.onError(responseCode)
            Logger.log("billingClient", "handlePurchasesResult $responseCode")
        }
    }

    fun purchase() {
        val flowParams = BillingFlowParams.newBuilder()
                .setSku(ConfigUtil.PRODUCT_SKU)
                .setType(SkuType.INAPP) // SkuType.SUB for subscription
                .build()
        billingClient.launchBillingFlow(activity, flowParams)
    }

}
