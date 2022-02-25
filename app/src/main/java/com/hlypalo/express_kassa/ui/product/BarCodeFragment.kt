package com.hlypalo.express_kassa.ui.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.hlypalo.express_kassa.R
import com.hlypalo.express_kassa.data.model.CartProduct
import com.hlypalo.express_kassa.data.model.Product
import com.hlypalo.express_kassa.data.repository.ProductRepository
import kotlinx.android.synthetic.main.fragment_bar_code.*
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
        list.clear()
        repo.fetchProductList {
            onResponse = func@{
                it ?: return@func
                list.addAll(it)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        scanner_view?.setResultHandler {
            CoroutineScope(Dispatchers.IO).launch {
                list.firstOrNull { p -> p.barCode == it.contents }?.let { p ->
                    repo.addProductToCart(CartProduct(0, p.name, p.price))
                }
            }
            Toast.makeText(context, it.contents, Toast.LENGTH_SHORT).show()
        }
        scanner_view?.startCamera()
    }
}