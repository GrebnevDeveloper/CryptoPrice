package com.grebnev.cryptoprice.data.network

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val apiKey: String,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val requestWithHeaders =
            originalRequest
                .newBuilder()
                .header("Content-type", "application/json; charset=UTF-8")
                .header("authorization", "Apikey $apiKey")
                .build()

        return chain.proceed(requestWithHeaders)
    }
}