package com.hlypalo.express_kassa.ui.product

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.model.Product
import com.hlypalo.express_kassa.data.repository.ProductRepository
import com.hlypalo.express_kassa.ui.view.ProductAdapter
import com.hlypalo.express_kassa.util.inflate
import com.hlypalo.express_kassa.util.showError
import kotlinx.android.synthetic.main.fragment_products.*
import kotlinx.android.synthetic.main.item_product.view.*

class ProductFragment : Fragment() {

    private val repo: ProductRepository by lazy { ProductRepository() }
    private val list: MutableList<Product> by lazy { mutableListOf() }
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

        repo.fetchProductList {
            onResponse = func@{
                it ?: return@func
                list.clear()
                list.addAll(it)
                updateList()
            }
        }
        btn_product_add?.setOnClickListener {
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.content_navigation, AddProductFragment())
                ?.addToBackStack(null)?.commit()
        }
    }

    private fun updateList() {
        adapter.notifyDataSetChanged()
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.order) {
            ORDER_DELETE -> {
                deleteProduct(item.groupId)
            }
            ORDER_EDIT -> {
                showEditProductDialog(list[item.groupId])
            }
        }
        return super.onContextItemSelected(item)
    }

    private fun deleteProduct(position: Int) {
        val product = list[position]
        repo.deleteProduct(product.id) {
            onResponse = {
                list.remove(product)
                updateList()
            }
            onError = {
                activity?.showError(it)
            }
        }
    }

    private fun showEditProductDialog(product: Product) {
        // TODO
    }

    inner class Adapter : RecyclerView.Adapter<Adapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(parent.inflate(R.layout.item_product))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(list[position])
        }

        override fun getItemCount(): Int = list.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnCreateContextMenuListener {
            fun bind(item: Product) = with(itemView) {
                ProductAdapter.ViewHolder(itemView).bind(item)
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