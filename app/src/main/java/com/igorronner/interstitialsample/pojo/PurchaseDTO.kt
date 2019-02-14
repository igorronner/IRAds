package com.igorronner.interstitialsample.pojo

import com.google.gson.annotations.SerializedName

data class PurchaseDTO(
        @SerializedName("purchaseToken")
        val purchaseToken: String,
        @SerializedName("orderId")
        val orderId: String,
        @SerializedName("sku")
        val sku: String,
        @SerializedName("subscriptionDate")
        val subscriptionDate: Long,
        @SerializedName("price")
        var price: Double,
        @SerializedName("currency")
        val currency: String)

