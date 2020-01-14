package com.igorronner.interstitialsample.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import com.igorronner.interstitialsample.R
import com.igorronner.interstitialsample.pojo.ProductSku
import com.igorronner.interstitialsample.utils.inflate

class ProductSkuAdapter(private var productSkuList:MutableList<ProductSku>, private var onItemClick: ((ProductSku) -> Unit)? = null) : androidx.recyclerview.widget.RecyclerView.Adapter<ProductSkuHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductSkuHolder {
        val inflatedView = parent.inflate(R.layout.product_sku_item, false)
        return ProductSkuHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return productSkuList.size
    }

    override fun onBindViewHolder(holder: ProductSkuHolder, position: Int) {
        val productSku = productSkuList[position]
        holder.bindProductSku(productSku, onItemClick)
    }
}