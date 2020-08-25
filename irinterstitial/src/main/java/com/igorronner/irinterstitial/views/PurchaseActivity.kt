package com.igorronner.irinterstitial.views

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.SkuType
import com.igorronner.irinterstitial.R
import com.igorronner.irinterstitial.init.ConfigUtil
import com.igorronner.irinterstitial.preferences.MainPreference
import com.igorronner.irinterstitial.services.ProductPurchasedListener
import com.igorronner.irinterstitial.services.ProductsListListener
import com.igorronner.irinterstitial.utils.Logger

open class PurchaseActivity : AppCompatActivity(), PurchasesUpdatedListener, ProductPurchasedListener, ProductsListListener {

    private lateinit var billingClient: BillingClient

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        handlePurchasesResult(billingResult.responseCode, purchases)
    }

    override fun onProductsPurchased() = Unit

    override fun onProductList(list: List<SkuDetails>?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        billingClient = BillingClient.newBuilder(this)
                .setListener(this)
                .enablePendingPurchases()
                .build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                val billingResponseCode = billingResult.responseCode

                Logger.log("billingClient", "onBillingSetupFinished ")
                if (billingResponseCode == BillingClient.BillingResponseCode.OK) {
                    Logger.logInfo("billingClient", "BillingClient.BillingResponse.OK ")
                    val skuList = ArrayList<String>()
                    skuList.add(ConfigUtil.PRODUCT_SKU)

                    val params = SkuDetailsParams.newBuilder()
                            .setSkusList(skuList)
                            .setType(SkuType.INAPP)

                    billingClient.querySkuDetailsAsync(params.build()) { result, skuDetailsList ->
                        val responseCode = result.responseCode
                        Logger.log("billingClient", "querySkuDetailsAsync ")
                        Logger.log("billingClient", "responseCode $responseCode")
                        skuDetailsList?.forEach { skuDetails: SkuDetails? ->
                            Logger.log("billingClient", "skuDetailsList ${skuDetails?.description}")
                            Logger.log("billingClient", "skuDetailsList ${skuDetails?.title}")
                            Logger.log("billingClient", "skuDetailsList ${skuDetails?.price}")
                            Logger.log("billingClient", "skuDetailsList ${skuDetails?.sku}")
                        }
                    }
                }
            }

            override fun onBillingServiceDisconnected() {
                Logger.logWarning("billingClient", "onBillingServiceDisconnected")
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

    override fun onResume() {
        super.onResume()
        Logger.log("billingClient", "onResume() ")
        if (!::billingClient.isInitialized)
            return

        Logger.log("billingClient", "billingClient.isInitialized ")
        val purchasesResult = billingClient.queryPurchases(SkuType.INAPP)
        val responseCode = purchasesResult.responseCode
        val purchases = purchasesResult?.purchasesList
        purchases?.forEach {
            purchase: Purchase? ->
            Logger.log("billingClient", "purchase ${purchase?.sku}")
        }

        handlePurchasesResult(responseCode, purchases)
    }

    protected fun showDialogPremium(skuDetails: SkuDetails) {
        val dialog = AlertDialog.Builder(this)
                .setTitle(R.string.buy_premium)
                .setMessage(R.string.message_buy_premium)
                .setPositiveButton(R.string.purchase) { _, _ -> purchase(skuDetails) }
                .setNegativeButton(R.string.cancel, null)

        if (!isFinishing) {
            dialog.show()
        }
    }

    private fun handlePurchasesResult(responseCode: Int, purchases: MutableList<Purchase>?){
        if (responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            Logger.log("billingClient", "handlePurchasesResult BillingClient.BillingResponse.OK")
            for (purchase in purchases) {
                if(purchase.sku == ConfigUtil.PRODUCT_SKU) {
                    MainPreference.setPremium(this)
                    onProductsPurchased()
                    Logger.log("billingClient", "onProductPurchased ")
                }
            }
        } else if (responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            Logger.logInfo("billingClient", "handlePurchasesResult USER_CANCELED")
        } else {
            // Handle any other error codes.
            Logger.logError("billingClient", "handlePurchasesResult $responseCode")
        }
    }

    protected fun purchase(skuDetails: SkuDetails) {
        val flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build()
        billingClient.launchBillingFlow(this@PurchaseActivity, flowParams)
    }

}