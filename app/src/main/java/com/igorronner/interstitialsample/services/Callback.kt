package com.igorronner.interstitialsample.services

import android.support.annotation.StringRes

interface Callback<T> {

    fun onSuccess(result:T?)
    fun onError(@StringRes errorStringRes:Int?)

}