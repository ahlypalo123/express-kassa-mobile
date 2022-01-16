package com.hlypalo.express_kassa.ui.product

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.hlypalo.express_kassa.App
import com.hlypalo.express_kassa.data.model.Product
import com.hlypalo.express_kassa.data.repository.ProductRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ProductPresenter(
    private val view: ProductView,
    private val lifecycleOwner: LifecycleOwner
) : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO
    private val list: LiveData<List<Product>> by lazy { App.db.productDao().getAllLiveData() }
    private val repo: ProductRepository by lazy { ProductRepository() }

    fun init() {
        list.observe(lifecycleOwner) {
            view.updateList()
        }
    }

    fun getProductList() : List<Product> {
        return list.value ?: mutableListOf()
    }

    fun addProduct(product: Product) = launch {
        if (product.id == 0L) {
            repo.addProduct(product)
        } else {
            repo.updateProduct(product)
        }
    }

    fun onEditProductSelected(ind: Int) {
        view.showEditProductDialog(getProductList()[ind])
    }

    fun deleteProduct(ind: Int) = launch {
        repo.deleteProduct(getProductList()[ind])
    }
}