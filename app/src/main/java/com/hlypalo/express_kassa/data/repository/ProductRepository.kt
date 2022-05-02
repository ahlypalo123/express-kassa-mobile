package com.hlypalo.express_kassa.data.repository

import android.content.SharedPreferences
import com.google.gson.Gson
import com.hlypalo.express_kassa.App
import com.hlypalo.express_kassa.data.api.ApiService
import com.hlypalo.express_kassa.data.model.Check
import com.hlypalo.express_kassa.data.model.CheckProduct
import com.hlypalo.express_kassa.data.model.Product
import com.hlypalo.express_kassa.util.*
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

    fun addProductToCheck(product: Product) {
        val check = getCheck()
        var cp = check?.products?.find {
            it.name == product.name
        }
        if (cp != null) {
            cp.count++
        } else {
            cp = CheckProduct(
                name = product.name,
                count = 1,
                productId = product.id,
                price = product.price
            )
            check?.products?.add(cp)
        }
        check?.total = (check?.total ?: 0F) + product.price
        updateCheck(check)
    }

    fun updateCheck(check: Check?) {
        App.prefEditor.putString(PREF_CHECK, Gson().toJson(check)).commit()
    }

    fun getCheck() : Check? {
        val check = App.sharedPrefs.getString(PREF_CHECK, "")
        if (check.isNullOrBlank()) {
            return null
        }
        return Gson().fromJson(check, Check::class.java)
    }

    private var listener: SharedPreferences.OnSharedPreferenceChangeListener? = null

    fun subscribeOnCheck(callback: (Check?) -> Unit) {
        listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == PREF_CHECK) {
                callback(getCheck())
            }
        }
        App.sharedPrefs.registerOnSharedPreferenceChangeListener(listener)
    }

    fun unsubscribeFromCheck() {
        App.sharedPrefs.unregisterOnSharedPreferenceChangeListener(listener)
    }
}