package com.hlypalo.express_kassa.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hlypalo.express_kassa.App
import com.hlypalo.express_kassa.data.api.ApiService
import com.hlypalo.express_kassa.data.model.CartDto
import com.hlypalo.express_kassa.data.model.CartProduct
import com.hlypalo.express_kassa.data.model.Product
import com.hlypalo.express_kassa.util.CallBackKt
import com.hlypalo.express_kassa.util.enqueue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.HttpException
import java.io.File
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody


class ProductRepository : CoroutineScope {

    private val api: ApiService by lazy { ApiService.getInstance() }
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

    fun fetchProductList(callback: CallBackKt<List<Product>>.() -> Unit) {
        api.getProducts().enqueue(callback)
    }

    suspend fun addProduct(data: Product) {
        api.addProductAsync(data).await()
    }

    suspend fun uploadPhoto(image: File) : String? {
        val requestFile = image.asRequestBody("multipart/form-data".toMediaType())
        val body = MultipartBody.Part.createFormData("file", image.name, requestFile)
        val response = api.uploadPhotoAsync(body).await()
        if (response.isSuccessful) {
            return response.body() as String
        }
        return null
    }

    suspend fun addProductToCart(data: CartProduct) {
        App.db.cartDao().add(data)
    }

    fun deleteProduct(id: Long, callback: CallBackKt<Unit>.() -> Unit) {
        api.deleteProduct(id).enqueue(callback)
    }

    suspend fun getProductsFromCartGrouped() : List<CartDto> {
        return App.db.cartDao().getAllGrouped()
    }

    suspend fun deleteProductsFromCart() {
        App.db.cartDao().deleteAll()
    }

    fun getProductsFromCartLiveData() : LiveData<List<CartDto>> {
        return App.db.cartDao().getAllGroupedLiveData()
    }

    suspend fun getProductsFromCart() : List<CartProduct> {
        return App.db.cartDao().getAll()
    }

    suspend fun deleteProductFromCartByName(name: String) {
        App.db.cartDao().deleteByName(name)
    }
}