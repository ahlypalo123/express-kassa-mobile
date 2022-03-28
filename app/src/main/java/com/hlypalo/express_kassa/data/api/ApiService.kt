package com.hlypalo.express_kassa.data.api

import com.google.gson.GsonBuilder
import com.hlypalo.express_kassa.data.model.*
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit


interface ApiService {

    @GET("/")
    fun healthCheck() : Call<String>

    @POST("/auth/login")
    fun login(@Body req: AuthenticationRequest) : Call<String>

    @POST("/auth/register")
    fun register(@Body req: AuthenticationRequest) : Call<Unit>

    @GET("/product")
    fun getProducts() : Call<List<Product>>

    @POST("/product")
    fun addProductAsync(@Body data: Product) : Deferred<Response<Unit>>

    @DELETE("/product/{id}")
    fun deleteProduct(@Path("id") id: Long) : Call<Unit>

    @Multipart
    @POST("/photo")
    fun uploadPhotoAsync(@Part image: MultipartBody.Part?) : Deferred<Response<String>>

    @GET("/shift")
    fun getCurrentShift() : Call<ShiftDetails>

    @POST("/shift")
    @Headers("action: OPEN_SHIFT")
    fun openShift(@Body req: ShiftRequest) : Call<ShiftDetails>

    @POST("/shift")
    @Headers("action: CLOSE_SHIFT")
    fun closeShift() : Call<Unit>

    @POST("/check")
    fun saveCheckAsync(@Body data: Check) : Deferred<Response<Unit>>

    @GET("/check")
    fun getCheckHistoryAsync() : Deferred<Response<List<Check>>>

    @POST("/auth/forgot-password")
    fun forgotPassword(@Body req: AuthenticationRequest) : Call<Unit>

    @POST("/auth/validate")
    fun validate(@Body req: ValidateRequest) : Call<Unit>

    @PUT("/merchant/update-password")
    fun updatePassword(@Body req: AuthenticationRequest) : Call<Unit>

    companion object {
        var apiService: ApiService? = null

        const val BASE_URL = "https://60ea-95-174-109-197.ngrok.io/"

        fun getInstance(): ApiService {
            if (apiService == null) {
                val client = OkHttpClient.Builder()
                    .addInterceptor(
                        HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BODY
                        }
                    )
                    .addInterceptor(AuthInterceptor())
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build()
                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addCallAdapterFactory(CoroutineCallAdapterFactory())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(
                        GsonBuilder()
                            .setLenient()
                            .create()
                    ))
                    .build()
                apiService = retrofit.create(ApiService::class.java)
            }
            return apiService!!
        }
    }

}