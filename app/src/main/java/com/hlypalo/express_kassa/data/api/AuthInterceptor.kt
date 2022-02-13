package com.hlypalo.express_kassa.data.api

import android.util.Log
import com.hlypalo.express_kassa.App
import com.hlypalo.express_kassa.util.PREF_TOKEN
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = App.sharedPrefs.getString(PREF_TOKEN, "") ?: ""
        Log.i(this.javaClass.name, "token is $token")
        return chain.proceed(chain.request()
            .newBuilder()
            .addHeader(
                "AUTHORIZATION",
                token
            ).build())
    }
}