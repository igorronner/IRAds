package com.igorronner.interstitialsample.views

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.igorronner.interstitialsample.R
import com.igorronner.interstitialsample.adapters.ProductSkuAdapter
import com.igorronner.interstitialsample.pojo.ProductSku
import com.igorronner.irinterstitial.services.ProductPurchasedListListener
import com.igorronner.irinterstitial.services.ProductsListListener
import com.igorronner.irinterstitial.services.SubscribeService
import kotlinx.android.synthetic.main.activity_subscribes.*

class SubscribesActivity : AppCompatActivity(), ProductsListListener, ProductPurchasedListListener {

    private lateinit var subscribeService: SubscribeService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscribes)

        subscribeService = SubscribeService(this)
        subscribeService.productsListListener = this
        subscribeService.productPurchasedListListener = this

    }

    override fun onResume() {
        super.onResume()
        subscribeService.onResume()
    }

    override fun onProductList(list: MutableList<SkuDetails>) {
        progressBar.visibility = View.GONE
        var productSkuList = mutableListOf<ProductSku>()
        list.forEach { productSkuList.add(ProductSku(it.title, it.description, it.price, it.sku)) }
        val layoutManager =LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        layoutManager.scrollToPosition(0)
        recyclerView.layoutManager = layoutManager
        val donateAdapter = ProductSkuAdapter(productSkuList){
            subscribeService.purchase(it.sku)
        }
        recyclerView.adapter = donateAdapter
    }

    override fun onProductsPurchasedList(list: MutableList<Purchase>) {

    }
}
