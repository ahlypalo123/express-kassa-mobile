package com.hlypalo.express_kassa.ui.view

import android.graphics.BitmapFactory
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.model.Product
import com.hlypalo.express_kassa.util.inflate
import com.hlypalo.express_kassa.util.loadUrlNoCache
import kotlinx.android.synthetic.main.item_product.view.*

class ProductAdapter(
    private val items: List<Product>,
    private val listener: ((Product) -> Unit)? = null
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_product))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], listener)
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: Product, listener: ((Product) -> Unit)? = null) = with(itemView) {
            text_product_name?.text = item.name
            text_product_price?.text = item.price.toString()
            image_product?.loadUrlNoCache(item.photoUrl, R.drawable.image)
            itemView.setOnClickListener {
                listener?.invoke(item)
            }
        }
    }
}