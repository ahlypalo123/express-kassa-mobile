package com.hlypalo.express_kassa.ui.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.model.CartProduct
import com.hlypalo.express_kassa.data.model.Product
import com.hlypalo.express_kassa.data.repository.ProductRepository
import com.hlypalo.express_kassa.ui.base.NavigationFragment
import kotlinx.android.synthetic.main.fragment_bar_code.*
import kotlinx.android.synthetic.main.fragment_bar_code.toolbar
import kotlinx.android.synthetic.main.fragment_products.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BarCodeFragment : Fragment() {

    private val repo: ProductRepository by lazy { ProductRepository() }
    private val list: MutableList<Product> by lazy { mutableListOf() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bar_code, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val activity = activity as AppCompatActivity?
        activity?.setSupportActionBar(toolbar)
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity?.supportActionBar?.setDisplayShowTitleEnabled(false)
        activity?.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_hamburger)
        setHasOptionsMenu(true)

        list.clear()
        repo.fetchProductList {
            onResponse = func@{
                it ?: return@func
                list.addAll(it)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                (parentFragment as? NavigationFragment)?.openDrawer()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        scanner_view?.setResultHandler {
            CoroutineScope(Dispatchers.IO).launch {
                list.firstOrNull { p -> p.barCode == it.contents }?.let { p ->
                    repo.addProductToCart(CartProduct(0, p.name, p.price))
                }
            }
        }
        scanner_view?.startCamera()
    }
}