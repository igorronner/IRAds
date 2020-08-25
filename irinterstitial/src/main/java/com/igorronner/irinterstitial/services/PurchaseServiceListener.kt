package com.igorronner.irinterstitial.services

import com.android.billingclient.api.SkuDetails
import com.igorronner.irinterstitial.dto.IRPurchase

interface ProductPurchasedListener{
    fun onProductsPurchased()
}

interface ProductPurchasedListListener{
    fun onProductsPurchasedList(list: List<IRPurchase>)
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