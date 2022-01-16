package com.hlypalo.express_kassa.data.repository

import androidx.lifecycle.LiveData
import com.hlypalo.express_kassa.App
import com.hlypalo.express_kassa.data.api.ApiService
import com.hlypalo.express_kassa.data.model.CartDto
import com.hlypalo.express_kassa.data.model.CartProduct
import com.hlypalo.express_kassa.data.model.Product
import retrofit2.Call
import retrofit2.Response

class ProductRepository {

    suspend fun getProductList() : List<Product> {
        return App.db.productDao().getAll()
    }

    suspend fun addProduct(data: Product) {
        App.db.productDao().add(data)
    }

    suspend fun updateProduct(data: Product) {
        App.db.productDao().update(data)
    }

    suspend fun addProductToCart(data: CartProduct) {
        App.db.cartDao().add(data)
    }

    suspend fun deleteProduct(data: Product) {
        App.db.productDao().delete(data)
    }

    fun getProductsFromCart() : LiveData<List<CartDto>> {
        return App.db.cartDao().getAllWithCountLiveData()
    }

}