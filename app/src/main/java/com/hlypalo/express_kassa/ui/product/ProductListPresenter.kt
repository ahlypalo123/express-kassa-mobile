package com.hlypalo.express_kassa.ui.product

import com.hlypalo.express_kassa.data.model.Product
import com.hlypalo.express_kassa.data.repository.ProductRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class ProductListPresenter(private val view: ProductView) : BaseProductPresenter(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private val list: MutableList<Product> = mutableListOf()
    private val filteredList: MutableList<Product> = mutableListOf()
    private val repo: ProductRepository by lazy { ProductRepository() }

    fun init() {
        view.showProgress()
        repo.fetchProductList {
            onResponse = func@{
                view.hideProgress()
                view.manageEmpty(it.isNullOrEmpty())
                it ?: return@func
                list.clear()
                list.addAll(it)
                updateFilteredList(null)
            }
            onError = {
                view.hideProgress()
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
                list.removeAt(position)
                updateFilteredList(null)
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

}