package com.hlypalo.express_kassa.ui.product

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.model.Product
import com.hlypalo.express_kassa.util.inflate
import com.hlypalo.express_kassa.util.loadUrlNoCache
import kotlinx.android.synthetic.main.fragment_products.*
import kotlinx.android.synthetic.main.item_product.view.*

class ProductFragment : Fragment(), ProductView {

    private val presenter: ProductPresenter by lazy { ProductPresenter(this, this) }
    private val adapter: Adapter by lazy { Adapter() }

    companion object {
        private const val ORDER_DELETE = 0
        private const val ORDER_EDIT = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_products, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        list_products?.layoutManager = LinearLayoutManager(context)
        list_products?.adapter = adapter

        presenter.init()
        btn_product_add?.setOnClickListener {
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.content_navigation, AddProductFragment())
                ?.addToBackStack(null)?.commit()
        }
    }

    override fun updateList() {
        adapter.notifyDataSetChanged()
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.order) {
            ORDER_DELETE -> {
                presenter.deleteProduct(item.groupId)
            }
            ORDER_EDIT -> {
                presenter.onEditProductSelected(item.groupId)
            }
        }
        return super.onContextItemSelected(item)
    }

    override fun showEditProductDialog(product: Product) {
        // TODO
    }

    inner class Adapter : RecyclerView.Adapter<Adapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(parent.inflate(R.layout.item_product))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(presenter.getProductList()[position])
        }

        override fun getItemCount(): Int = presenter.getProductList().size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnCreateContextMenuListener {
            fun bind(item: Product) = with(itemView) {
                text_product_name?.text = item.name
                text_product_price?.text = item.price.toString()
                image_product?.loadUrlNoCache(item.photo_url, R.drawable.image)
                setOnCreateContextMenuListener(this@ViewHolder)
            }

            override fun onCreateContextMenu(
                menu: ContextMenu?,
                v: View?,
                menuInfo: ContextMenu.ContextMenuInfo?
            ) {
                v?.id?.let {
                    menu?.add(this.adapterPosition, it, ORDER_DELETE, R.string.delete)
                    menu?.add(this.adapterPosition, it, ORDER_EDIT, R.string.edit)
                }
            }
        }
    }

}