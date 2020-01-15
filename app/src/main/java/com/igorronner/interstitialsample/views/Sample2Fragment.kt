package com.igorronner.interstitialsample.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.ads.AdSize
import com.igorronner.interstitialsample.R
import com.igorronner.irinterstitial.init.IRAds
import kotlinx.android.synthetic.main.fragment_sample2.*

class Sample2Fragment : androidx.fragment.app.Fragment() {

    private lateinit var adsInstance: IRAds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adsInstance = IRAds.newInstance(activity)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_sample2, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        radioGrupo.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbSmall -> loadNativeOrBanner(AdSize.BANNER)
                R.id.rbMedium -> loadNativeOrBanner(AdSize.MEDIUM_RECTANGLE)
                R.id.rbLarge -> loadNativeOrBanner(AdSize.LARGE_BANNER)
            }
        }

        rbSmall.performClick()
    }

    private fun loadNativeOrBanner(size: AdSize, progress: Boolean = true) {
        adsInstance.loadNativeOrBannerAd(
                view?.findViewById(R.id.adViewContainer), view?.findViewById(R.id.adViewNative), size, progress)
    }

}