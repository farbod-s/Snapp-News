package com.snappbox.network.model

data class NewsApiResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<Article>
)