package com.igorronner.interstitialsample.services

import com.igorronner.interstitialsample.R
import com.igorronner.interstitialsample.pojo.PurchaseDTO
import com.igorronner.interstitialsample.requests.RequestGeneratorV3
import com.igorronner.interstitialsample.requests.SubscribeV3Request
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.io.IOException

class SubscribeV3Service {


    fun subscribe(purchaseDTO: PurchaseDTO, callback: Callback<Boolean>){
        val subscribeV3Request
                = RequestGeneratorV3
                        .createService(SubscribeV3Request::class.java)

        subscribeV3Request.subscribe(purchaseDTO).enqueue(object : retrofit2.Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.handler(callback)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                callback.onSuccess(response.isSuccessful)
            }
        })


    }

    inline fun <reified T> Throwable?.handler(callback: Callback<T>){
        this?.let {
            throwable ->
            if (throwable is IOException) {
                callback.onError(R.string.sem_internet)
            } else {
                callback.onError(R.string.error)
            }
        } ?: run {
            callback.onError(R.string.error)
        }
    }

}