package com.igorronner.irinterstitial.services

import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails

interface ProductPurchasedListener{
    fun onProductsPurchased()
}

interface ProductPurchasedListListener{
    fun onProductsPurchasedList(list: List<Purchase>)
}

interface ProductsListListener{
    fun onProductList(list: List<SkuDetails>?)
}

interface PurchaseErrorListener{
    fun onError(responseCode: Int)
}

interface PurchaseCanceledListener{
    fun onCanceled()
}