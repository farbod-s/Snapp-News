package com.snappbox.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.map
import com.snappbox.data.model.NewsDto
import com.snappbox.data.model.toDto
import com.snappbox.data.model.toTempEntity
import com.snappbox.database.dao.NewsDao
import com.snappbox.network.NewsApiService
import com.snappbox.network.model.Article
import com.snappbox.network.source.NewsPagingSourceFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@OptIn(ExperimentalPagingApi::class)
class DefaultNewsRepository @Inject constructor(
    private val remoteMediator: NewsRemoteMediator,
    private val pagingConfig: PagingConfig,
    private val newsDao: NewsDao,
    private val api: NewsApiService,
    private val pagingSourceFactory: NewsPagingSourceFactory
) : NewsRepository {

    override fun getNewsStream(): Flow<PagingData<NewsDto>> {
        return Pager(
            config = pagingConfig,
            remoteMediator = remoteMediator,
            pagingSourceFactory = { newsDao.pagingSource() }
        ).flow.map { it.map { entity -> entity.toDto() } }
    }

    override fun getLastNewsCount(): Flow<Int> {
        return newsDao.getLastNewsCount()
    }

    override suspend fun getArticleById(id: String): NewsDto? {
        return newsDao.getArticleById(id)?.toDto()
    }

    override suspend fun migrateLastNews() {
        newsDao.migrateLastNews()
    }

    override suspend fun refreshNewsWithPagingSource() {
        var latestPublishedAt =
            newsDao.getLastNewsLatestPublishedAt() ?: newsDao.getLatestPublishedAt() ?: return
        val pagingSource = pagingSourceFactory.create(latestPublishedAt)

        // Load the data in pages
        var loadResult: PagingSource.LoadResult<String, Article>
        do {
            loadResult = pagingSource.load(
                PagingSource.LoadParams.Refresh(
                    latestPublishedAt,
                    pagingConfig.pageSize,
                    false
                )
            )
            if (loadResult is PagingSource.LoadResult.Page) {
                val articlesToSave = loadResult.data.filter { it.publishedAt > latestPublishedAt }
                if (articlesToSave.isNotEmpty()) {
                    // Save new articles to the database
                    newsDao.insertLastNews(articlesToSave.map { it.toTempEntity() })
                    // Update the latest published date to the most recent article
                    latestPublishedAt = loadResult.nextKey.orEmpty()
                }
            }
        } while (loadResult is PagingSource.LoadResult.Page && !loadResult.nextKey.isNullOrEmpty())
    }

    @Deprecated(
        message = "Use refreshNewsWithPagingSource() instead",
        replaceWith = ReplaceWith("refreshNewsWithPagingSource()")
    )
    override suspend fun refreshNews() {
        var latestPublishedAt =
            newsDao.getLastNewsLatestPublishedAt() ?: newsDao.getLatestPublishedAt() ?: return

        try {
            withTimeout(60_000) {
                while (true) {
                    val articlesToSave =
                        api.getNews(
                            from = latestPublishedAt,
                            pageSize = pagingConfig.pageSize
                        ).getOrThrow().articles.filter { it.publishedAt > latestPublishedAt }

                    if (articlesToSave.isEmpty()) {
                        break
                    }

                    latestPublishedAt = articlesToSave.maxOf { it.publishedAt }
                    newsDao.insertLastNews(articlesToSave.map { it.toTempEntity() })
                    delay(500)
                }
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            return
        }
    }
}