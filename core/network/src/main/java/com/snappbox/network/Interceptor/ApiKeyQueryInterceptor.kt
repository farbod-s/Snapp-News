package com.snappbox.network.Interceptor

import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyQueryInterceptor(private val apiKey: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url

        // Add the API key as a query parameter
        val urlWithApiKey = originalUrl.newBuilder()
            .addQueryParameter("apiKey", apiKey)
            .build()

        val requestWithApiKey = originalRequest.newBuilder()
            .url(urlWithApiKey)
            .build()

        return chain.proceed(requestWithApiKey)
    }
}