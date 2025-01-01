package com.snappbox.network

import com.snappbox.network.model.NewsApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("v2/everything")
    suspend fun getNews(
        @Query("q") query: String = "Tehran",
        @Query("pageSize") pageSize: Int,
        @Query("page") page: Int = 1,
        @Query("from") from: String? = null,
        @Query("to") to: String? = null,
        @Query("sortBy") sortBy: String = "publishedAt",
    ): Result<NewsApiResponse>
}