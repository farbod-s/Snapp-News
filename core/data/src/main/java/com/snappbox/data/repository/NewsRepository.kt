package com.snappbox.data.repository

import androidx.paging.PagingData
import com.snappbox.data.model.NewsDto
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    fun getNewsStream(): Flow<PagingData<NewsDto>>

    fun getLastNewsCount(): Flow<Int>

    suspend fun getArticleById(id: String): NewsDto?

    suspend fun migrateLastNews()

    suspend fun refreshNewsWithPagingSource()

    @Deprecated(
        message = "Use refreshNewsWithPagingSource() instead",
        replaceWith = ReplaceWith("refreshNewsWithPagingSource()")
    )
    suspend fun refreshNews()
}