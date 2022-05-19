package com.hlypalo.express_kassa.data.repository

import com.hlypalo.express_kassa.data.api.ApiService
import com.hlypalo.express_kassa.data.model.Product
import com.hlypalo.express_kassa.util.CallBackKt
import com.hlypalo.express_kassa.util.enqueue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import kotlin.coroutines.CoroutineContext


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

    fun deleteProduct(id: Long, callback: CallBackKt<Unit>.() -> Unit) {
        api.deleteProduct(id).enqueue(callback)
    }
}