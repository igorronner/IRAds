package com.igorronner.interstitialsample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.igorronner.irinterstitial.init.IRAds

class FragmentSampleActivity : AppCompatActivity() {

    private lateinit var adsInstance: IRAds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_sample)
        adsInstance = IRAds.newInstance(this)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.frameLayout, Sample1Fragment(), null)
                    .commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_fragment_sample, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_item_one -> adsInstance.showInterstitialBeforeFragment(Sample1Fragment(), R.id.frameLayout, this)
            R.id.menu_item_two -> adsInstance.showInterstitialBeforeFragment(Sample2Fragment(), R.id.frameLayout, this)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        adsInstance.showInterstitial()
    }

}