package com.hlypalo.express_kassa.ui.main

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.hlypalo.express_kassa.data.model.CartDto
import com.hlypalo.express_kassa.data.model.CartProduct
import com.hlypalo.express_kassa.data.model.Product
import com.hlypalo.express_kassa.data.repository.ProductRepository
import com.hlypalo.express_kassa.util.calculateTotal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class MainPresenter(
    private val view: MainView,
    private val lifecycleOwner: LifecycleOwner
) : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO
    private val repo: ProductRepository = ProductRepository()
    private val list: MutableList<Product> = mutableListOf()
    private val filteredList: MutableList<Product> = mutableListOf()
    private val cartLiveData = repo.getProductsFromCartLiveData()

    fun init() {
        fetchProductList()
        cartLiveData.observe(lifecycleOwner) {
            view.updateCart()
            val total: Float = cartLiveData.value.calculateTotal()
            view.updateTotal(total)
        }
    }

    private fun fetchProductList() {
        repo.fetchProductList {
            onResponse = func@{
                it ?: return@func
                list.clear()
                list.addAll(it)
                updateFilteredList()
            }
            onError = {

            }
        }
    }

    fun updateFilteredList() = launch {
        filteredList.clear()
        if (view.getFilter() == null) {
            filteredList.addAll(list)
            return@launch
        }
        filteredList.addAll(
            list.filter { p -> p.name.contains(view.getFilter()!!) }
        )
        withContext(Dispatchers.Main) {
            view.updateProductList()
        }
    }

    fun getProductList() : List<Product> {
        return filteredList
    }

    fun addProductToCart(data: Product) = launch {
        repo.addProductToCart(CartProduct(0, data.name, data.price))
    }

    fun getProductListForCart(): List<CartDto> {
        return cartLiveData.value ?: listOf()
    }

}