package com.igorronner.irinterstitial.services

import com.igorronner.irinterstitial.dto.IRPurchase
import com.igorronner.irinterstitial.dto.IRSkuDetails

interface ProductPurchasedListener{
    fun onProductsPurchased()
}

interface ProductPurchasedListListener{
    fun onProductsPurchasedList(list: List<IRPurchase>)
}

interface ProductsListListener{
    fun onProductList(list: List<IRSkuDetails>)
}

interface PurchaseErrorListener{
    fun onError(responseCode: Int)
}

interface PurchaseCanceledListener{
    fun onCanceled()
}