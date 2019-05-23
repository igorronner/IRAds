package com.igorronner.interstitialsample.utils

import com.igorronner.irinterstitial.dto.IRSkuDetails

fun List<IRSkuDetails>.findBySku(sku:String): IRSkuDetails? {
    return this.firstOrNull { it.sku == sku }
}