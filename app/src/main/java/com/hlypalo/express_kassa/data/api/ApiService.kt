package com.hlypalo.express_kassa.data.api

import com.hlypalo.express_kassa.data.model.AuthenticationDto
import com.hlypalo.express_kassa.data.model.LoginResponse
import com.hlypalo.express_kassa.data.model.Product
import com.hlypalo.express_kassa.data.model.Result
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @GET("/search/anime")
    fun getAnime(@Query("q") q: String): Call<Response<Result>>

    @GET("/product")
    fun getProducts(): Call<Response<List<Product>>>

    @POST("/login")
    fun login(@Body dto: AuthenticationDto): Call<Response<LoginResponse>>

    @POST("/register")
    fun register(@Body dto: AuthenticationDto): Call<Response<*>>

    companion object {
        var apiService: ApiService? = null

        fun getInstance(): ApiService {
            if (apiService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://api.jikan.moe/v3/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                apiService = retrofit.create(ApiService::class.java)
            }
            return apiService!!
        }
    }

}