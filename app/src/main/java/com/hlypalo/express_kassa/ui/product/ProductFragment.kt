package com.hlypalo.express_kassa.ui.product

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.api.ApiService
import com.hlypalo.express_kassa.data.model.ErrorBody
import com.hlypalo.express_kassa.data.model.Product
import com.hlypalo.express_kassa.ui.base.NavigationFragment
import com.hlypalo.express_kassa.ui.main.MainFragment
import com.hlypalo.express_kassa.util.inflate
import com.hlypalo.express_kassa.util.loadImageUrl
import com.hlypalo.express_kassa.util.showError
import kotlinx.android.synthetic.main.fragment_navigation.*
import kotlinx.android.synthetic.main.fragment_products.*
import kotlinx.android.synthetic.main.item_product.view.*

class ProductFragment : Fragment(), ProductView {

    private val presenter: ProductPresenter by lazy { ProductPresenter(this) }
    private val adapter: Adapter by lazy { Adapter(presenter.getProductList()) }

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
        val activity = activity as AppCompatActivity?
        activity?.setSupportActionBar(toolbar)
        activity?.supportActionBar?.setDisplayShowTitleEnabled(false)
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity?.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_hamburger)
        setHasOptionsMenu(true)

        list_products?.layoutManager = LinearLayoutManager(context)
        list_products?.adapter = adapter

        btn_product_add?.visibility = if (parentFragment is MainFragment) {
            View.GONE
        } else {
            View.VISIBLE
        }

        presenter.init()

        btn_product_add?.setOnClickListener {
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.content_navigation, AddProductFragment())
                ?.addToBackStack(null)?.commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)

        val searchBar = menu.findItem(R.id.search_bar).actionView as? SearchView

        searchBar?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(filter: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(filter: String?): Boolean {
                presenter.updateFilteredList(filter)
                return true
            }
        })
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val fragment = activity?.supportFragmentManager
                    ?.findFragmentByTag(NavigationFragment::class.java.simpleName)
                (fragment as? NavigationFragment)?.openDrawer()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
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
                // showEditProductDialog(list[item.groupId])
            }
        }
        return super.onContextItemSelected(item)
    }

    private fun showEditProductDialog(product: Product) {
        // TODO
    }

    override fun showError(err: ErrorBody?) {
        activity?.showError(err)
    }

    inner class Adapter(private val items: List<Product>) : RecyclerView.Adapter<Adapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(parent.inflate(R.layout.item_product))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int = items.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnCreateContextMenuListener {
            fun bind(item: Product) = with(itemView) {
                text_product_name?.text = item.name
                text_product_price?.text = item.price.toString()
                if (!item.photoUrl.isNullOrBlank()) {
                    image_product?.loadImageUrl(
                        ApiService.BASE_URL + item.photoUrl,
                        R.drawable.image
                    )
                }
                if (parentFragment is MainFragment) {
                    setOnClickListener {
                        presenter.addProductToCart(item)
                    }
                } else {
                    setOnCreateContextMenuListener(this@ViewHolder)
                }
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