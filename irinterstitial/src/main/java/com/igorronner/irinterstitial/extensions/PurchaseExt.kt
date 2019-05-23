package com.igorronner.irinterstitial.extensions

import com.android.billingclient.api.Purchase
import com.igorronner.irinterstitial.dto.IRPurchase

fun List<Purchase>.toIRPurchaseList():List<IRPurchase> {
    val list = arrayListOf<IRPurchase>()
    forEach {purchase ->
        val irPurchase = IRPurchase(
                purchase.purchaseToken,
                purchase.orderId,
                purchase.sku
        )

        list.add(irPurchase)
    }

    return list
}