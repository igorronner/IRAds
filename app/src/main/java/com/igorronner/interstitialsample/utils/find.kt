package com.igorronner.interstitialsample.utils

import com.android.billingclient.api.SkuDetails

fun List<SkuDetails>.findBySku(sku:String): SkuDetails? {
    return this.firstOrNull { it.sku == sku }
}