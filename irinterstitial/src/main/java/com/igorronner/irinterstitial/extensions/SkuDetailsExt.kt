package com.igorronner.irinterstitial.extensions

import com.android.billingclient.api.SkuDetails
import com.igorronner.irinterstitial.dto.IRSkuDetails

fun List<SkuDetails>.toIRSkuDetailsList():List<IRSkuDetails>{
    val list = arrayListOf<IRSkuDetails>()
    forEach {skuDetails ->
        val irSkuDetails = IRSkuDetails(skuDetails.price, skuDetails.sku)
        list.add(irSkuDetails)
    }

    return list
}