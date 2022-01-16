package com.hlypalo.express_kassa.ui.main

import androidx.lifecycle.LifecycleOwner
import com.hlypalo.express_kassa.data.model.CartDto
import com.hlypalo.express_kassa.data.model.CartProduct
import com.hlypalo.express_kassa.data.model.Product
import com.hlypalo.express_kassa.data.repository.ProductRepository
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
    private val cartLiveData = repo.getProductsFromCart()

    fun init() {
        fetchProductList()
        cartLiveData.observe(lifecycleOwner) {
            view.updateCart()
        }
    }

    private fun fetchProductList() = launch {
        list.clear()
        list.addAll(repo.getProductList())
        withContext(Dispatchers.Main) {
            update()
        }
    }

    fun update() = launch {
        filteredList.clear()
        val filter = view.getFilter() ?: return@launch
        filteredList.addAll(
            list.filter { p -> p.name.contains(filter) }
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