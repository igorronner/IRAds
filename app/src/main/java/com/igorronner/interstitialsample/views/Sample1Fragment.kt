package com.igorronner.interstitialsample.views

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.igorronner.interstitialsample.R
import com.igorronner.irinterstitial.init.IRAds
import kotlinx.android.synthetic.main.fragment_sample.*

class Sample1Fragment : Fragment() {

    private lateinit var adsInstance: IRAds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adsInstance = IRAds.newInstance(activity)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_sample, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adsInstance.loadNativeAd(true, adSmallUnified as UnifiedNativeAdView)
        adsInstance.loadNativeAd(true, adMediumUnified as UnifiedNativeAdView)
        adsInstance.loadNativeAd(true, adLargeUnified as UnifiedNativeAdView)
    }

}