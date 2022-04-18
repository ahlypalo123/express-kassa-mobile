package com.hlypalo.express_kassa.ui.product

import com.hlypalo.express_kassa.data.model.CartProduct
import com.hlypalo.express_kassa.data.model.Product
import com.hlypalo.express_kassa.data.repository.ProductRepository
import com.hlypalo.express_kassa.util.showError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class ProductPresenter(private val view: ProductView) : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private val list: MutableList<Product> = mutableListOf()
    private val filteredList: MutableList<Product> = mutableListOf()
    private val repo: ProductRepository = ProductRepository()

    fun init() {
        repo.fetchProductList {
            onResponse = func@{
                view.manageEmpty(it.isNullOrEmpty())
                it ?: return@func
                list.clear()
                list.addAll(it)
                updateFilteredList(null)
            }
            onError = {
                view.showError(it)
            }
        }
    }

    fun getProductList() : List<Product> {
        return filteredList
    }

    fun deleteProduct(position: Int) {
        val product = list[position]
        repo.deleteProduct(product.id) {
            onResponse = {
                list.remove(product)
                view.updateList()
            }
            onError = {
                view.showError(it)
            }
        }
    }

    fun updateFilteredList(filter: String?) = launch {
        filteredList.clear()
        if (filter.isNullOrBlank()) {
            filteredList.addAll(list)
        } else {
            filteredList.addAll(
                list.filter { p -> p.name.contains(filter) }
            )
        }
        withContext(Dispatchers.Main) {
            view.updateList()
        }
    }

    fun addProductToCart(data: Product) = launch {
        repo.addProductToCart(CartProduct(0, data.name, data.price))
    }

}