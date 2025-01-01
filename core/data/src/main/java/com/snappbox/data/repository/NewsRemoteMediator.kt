package com.snappbox.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.snappbox.data.model.toMainEntity
import com.snappbox.data.utils.CacheValidator
import com.snappbox.data.utils.ConnectivityChecker
import com.snappbox.database.NewsDatabase
import com.snappbox.database.datasource.PreferencesDataSource
import com.snappbox.database.model.NewsEntity
import com.snappbox.network.NewsApiService
import com.snappbox.network.exception.ApiException
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class NewsRemoteMediator @Inject constructor(
    private val api: NewsApiService,
    private val db: NewsDatabase,
    private val settings: PreferencesDataSource,
    private val cacheValidator: CacheValidator,
    private val connectivityChecker: ConnectivityChecker
) : RemoteMediator<Int, NewsEntity>() {

    override suspend fun initialize(): InitializeAction {
        return if (cacheValidator.isValid() || !connectivityChecker.isConnected()) {
            // Cached data is up-to-date, so there is no need to re-fetch from network.
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            // Need to refresh cached data from network; returning LAUNCH_INITIAL_REFRESH here
            // will also block RemoteMediator's APPEND and PREPEND from running until REFRESH
            // succeeds.
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, NewsEntity>
    ): MediatorResult {
        return try {
            // The network load method takes an optional [String] parameter. For every page
            // after the first, we pass the [String] token returned from the previous page to
            // let it continue from where it left off.
            val loadKey = when (loadType) {
                // For REFRESH, pass `null` to load the first page.
                LoadType.REFRESH -> null
                // In this example, we never need to prepend, since REFRESH will always load the
                // first page in the list. Immediately return, reporting end of pagination.
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                // We must explicitly check if the last item is `null` when appending,
                // since passing `null` to networkService is only valid for initial load.
                // If lastItem is `null` it means no items were loaded after the initial
                // REFRESH and there are no more items to load.
                LoadType.APPEND -> {
                    state.lastItemOrNull()?.publishedAt ?: return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                }
            }

            val articles = api.getNews(
                pageSize = state.config.pageSize,
                to = loadKey
            ).getOrThrow().articles.filter { article ->
                (loadKey?.let { key ->
                    article.publishedAt < key
                } ?: true) && !article.title.lowercase().contains("removed")
            }.map { it.toMainEntity() }

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    settings.setLastFetchTimestamp(System.currentTimeMillis())
                    db.newsDao().clearAll()
                    db.newsDao().clearLastNews()
                }
                db.newsDao().insertAll(articles)
            }

            MediatorResult.Success(endOfPaginationReached = articles.isEmpty())
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        } catch (e: ApiException) {
            MediatorResult.Error(e)
        }
    }
}