package com.igorronner.interstitialsample.requests

import com.igorronner.interstitialsample.pojo.PurchaseDTO
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface SubscribeV3Request {

    @POST("apiv3/Subscription/SubscribeAndroidTest")
    fun subscribe(@Body purchaseDTO:PurchaseDTO):Call<ResponseBody>


}