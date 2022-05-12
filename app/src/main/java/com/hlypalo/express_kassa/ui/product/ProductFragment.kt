package com.hlypalo.express_kassa.ui.product

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hlypalo.express_kassa.App
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.model.ErrorBody
import com.hlypalo.express_kassa.data.model.Product
import com.hlypalo.express_kassa.ui.main.FreeSaleFragment
import com.hlypalo.express_kassa.ui.main.NavigationFragment
import com.hlypalo.express_kassa.ui.main.MainFragment
import com.hlypalo.express_kassa.util.*
import kotlinx.android.synthetic.main.fragment_navigation.*
import kotlinx.android.synthetic.main.fragment_products.*
import kotlinx.android.synthetic.main.item_product.view.*

class ProductFragment : Fragment(), ProductView {

    private val presenter = ProductListPresenter(this)
    private val adapter = Adapter(presenter.getProductList())

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
        setupActionBar()

        btn_product_add?.visibility = if (parentFragment is MainFragment) {
            View.GONE
        } else {
            View.VISIBLE
        }

        presenter.init()
        list_products?.layoutManager = LinearLayoutManager(context)
        list_products?.adapter = adapter

        btn_product_add?.setOnClickListener {
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.container, AddProductFragment())
                ?.addToBackStack(null)?.commit()
        }
    }

    override fun onResume() {
        super.onResume()
        setupActionBar()
    }

    private fun setupActionBar() {
        val activity = activity as AppCompatActivity?
        activity?.setSupportActionBar(toolbar)
        activity?.supportActionBar?.setDisplayShowTitleEnabled(false)
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity?.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_hamburger)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_products, menu)
        menu.findItem(R.id.interface_free_sale).isVisible = parentFragment is MainFragment
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
            R.id.interface_free_sale -> {
                App.prefEditor.putBoolean(PREF_INTERFACE_TYPE_FREE_SALE, true).commit()
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

    override fun manageEmpty(b: Boolean) {
        text_empty?.visibility = if (b) View.VISIBLE else View.INVISIBLE
    }

    override fun showProgress() {
        progress_bar?.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progress_bar?.visibility = View.INVISIBLE
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
                if (item.photoUrl.isNullOrBlank()) {
                    val tv = TextView(context)
                    tv.text = item.name[0].toString()
                    tv.setTextColor(ContextCompat.getColor(context, R.color.white))
                    tv.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent))
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24F)
                    tv.setPadding(16, 0, 16, 0)
                    tv.measure(
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                    )
                    tv.layout(0, 0, tv.measuredWidth, tv.measuredHeight)
                    val bmp = getBitmapFromView(tv)
                    image_product?.setImageBitmap(bmp)
                } else {
                    image_product?.loadImageUrl(
                        item.photoUrl,
                        R.drawable.image
                    )
                }
                if (parentFragment is MainFragment) {
                    setOnClickListener {
                        presenter.addProductToCheck(item)
                    }
                } else {
                    setOnCreateContextMenuListener(this@ViewHolder)
                }
            }

            private fun getBitmapFromView(view: View): Bitmap? {
                val bitmap =
                    Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                view.draw(canvas)
                return bitmap
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