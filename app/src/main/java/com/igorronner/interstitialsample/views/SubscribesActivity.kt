package com.igorronner.interstitialsample.views

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.igorronner.interstitialsample.R
import com.igorronner.interstitialsample.adapters.ProductSkuAdapter
import com.igorronner.interstitialsample.pojo.ProductSku
import com.igorronner.interstitialsample.pojo.PurchaseDTO
import com.igorronner.interstitialsample.services.Callback
import com.igorronner.interstitialsample.services.SubscribeV3Service
import com.igorronner.interstitialsample.utils.findBySku
import com.igorronner.irinterstitial.init.IRAds
import com.igorronner.irinterstitial.preferences.MainPreference
import com.igorronner.irinterstitial.services.ProductPurchasedListListener
import com.igorronner.irinterstitial.services.ProductsListListener
import com.igorronner.irinterstitial.services.SubscribeService
import kotlinx.android.synthetic.main.activity_subscribes.*
import java.util.ArrayList

class SubscribesActivity : AppCompatActivity(), ProductsListListener, ProductPurchasedListListener {

    private lateinit var subscribeService: SubscribeService
    var products:List<SkuDetails> = ArrayList()

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
        products = list
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
        list.forEach {
            purchase ->

            val sku = products.findBySku(purchase.sku)
            if (sku != null) {
                val purchaseDTO = PurchaseDTO(purchase.purchaseToken,
                        purchase.orderId,
                        purchase.sku,
                        purchase.purchaseTime,
                        (sku.priceAmountMicros / 1000000).toDouble(),
                        sku.priceCurrencyCode)


                SubscribeV3Service().subscribe(purchaseDTO, object : Callback<Boolean> {
                    override fun onSuccess(result: Boolean?) {
                        MainPreference.setPremium(this@SubscribesActivity)
                        Toast.makeText(this@SubscribesActivity, "Compra realizada com sucesso!", Toast.LENGTH_LONG).show()
                    }

                    override fun onError(errorStringRes: Int?) {
                        Toast.makeText(this@SubscribesActivity, "Erro ao realizar a compra", Toast.LENGTH_LONG).show()
                    }
                })
            }

        }
    }
}
