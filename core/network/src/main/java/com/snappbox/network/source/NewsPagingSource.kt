package com.snappbox.network.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.snappbox.network.NewsApiService
import com.snappbox.network.exception.ApiException
import com.snappbox.network.model.Article
import retrofit2.HttpException
import java.io.IOException

class NewsPagingSource(
    private val api: NewsApiService,
    private val latestPublishedAt: String
) : PagingSource<String, Article>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Article> {
        return try {
            val articles = api.getNews(
                pageSize = params.loadSize,
                from = params.key ?: latestPublishedAt,
            ).getOrThrow().articles

            // End of pagination if no new data or all articles match the `from` timestamp
            val endOfPagination =
                articles.isEmpty() || articles.all {
                    it.publishedAt <= (params.key ?: latestPublishedAt)
                }

            LoadResult.Page(
                data = articles,
                prevKey = null, // no previous pages for refreshing new data
                nextKey = if (endOfPagination) null else articles.maxOf { it.publishedAt }
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        } catch (e: ApiException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<String, Article>): String? {
        return null // Only loads new data, no refresh key needed.
    }
}