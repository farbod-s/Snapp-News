package com.snappbox.network.source

import com.snappbox.network.NewsApiService
import javax.inject.Inject

class NewsPagingSourceFactory @Inject constructor(
    private val api: NewsApiService
) {

    fun create(publishedAt: String): NewsPagingSource {
        return NewsPagingSource(api, publishedAt)
    }
}