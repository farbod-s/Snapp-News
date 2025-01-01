package com.snappbox.data.model

data class NewsDto(
    val id: String,
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String,
    val source: String,
)