package com.hlypalo.express_kassa.ui.main

import androidx.lifecycle.LifecycleOwner
import com.hlypalo.express_kassa.data.model.CartDto
import com.hlypalo.express_kassa.data.model.CartProduct
import com.hlypalo.express_kassa.data.model.Product
import com.hlypalo.express_kassa.data.repository.ProductRepository
import com.hlypalo.express_kassa.util.calculateTotal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class CartPresenter(
    private val view: CartView,
    private val lifecycleOwner: LifecycleOwner
) : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO
    private val repo: ProductRepository = ProductRepository()
    private val cartLiveData = repo.getProductsFromCartLiveData()

    fun init() {
        cartLiveData.observe(lifecycleOwner) {
            view.updateCart()
            val total: Float = cartLiveData.value.calculateTotal()
            view.updateTotal(total)
        }
    }

    fun getProductListForCart(): List<CartDto> {
        return cartLiveData.value ?: listOf()
    }

    fun deleteFromCart(pos: Int) = launch {
        repo.deleteProductFromCartByName(getProductListForCart()[pos].name)
    }

}