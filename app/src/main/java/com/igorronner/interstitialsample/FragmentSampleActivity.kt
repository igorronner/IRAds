package com.igorronner.interstitialsample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.igorronner.irinterstitial.init.IRAds
import kotlinx.android.synthetic.main.activity_fragment_sample.*

class FragmentSampleActivity : AppCompatActivity() {


    private lateinit var adsInstance: IRAds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_sample)

        adsInstance = IRAds.newInstance(this)

        fragment1.setOnClickListener {
            adsInstance.showInterstitialBeforeFragment(Sample1Fragment(), R.id.frameLayout, this)
        }

        fragment2.setOnClickListener {
            adsInstance.showInterstitialBeforeFragment(Sample2Fragment(), R.id.frameLayout, this, getString(R.string.loading))
        }

    }

    override fun onStop() {
        super.onStop()
        adsInstance.onStop()
    }

    override fun onResume() {
        super.onResume()
        adsInstance.onResume()
    }

}
