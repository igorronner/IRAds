package com.igorronner.irinterstitial.dto

data class RemoteConfigDTO(var showSplash:Boolean = false,
                           var finishWithInterstitial:Boolean = false,
                           var adVersion:Long = 1,
                           var interstitialId:String? = null,
                           var publisherInterstitialId:String? =null)



