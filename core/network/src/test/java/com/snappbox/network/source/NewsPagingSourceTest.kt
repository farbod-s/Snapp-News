package com.snappbox.network.source

import androidx.paging.PagingSource
import com.snappbox.network.NewsApiService
import com.snappbox.network.exception.ApiException
import com.snappbox.network.model.ApiError
import com.snappbox.network.model.Article
import com.snappbox.network.model.NewsApiResponse
import com.snappbox.network.model.Source
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.IOException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class NewsPagingSourceTest {

    private val api: NewsApiService = mockk()
    private val latestPublishedAt = "2024-11-11T00:00:00Z"
    private val pagingSource = NewsPagingSource(api, latestPublishedAt)

    @Test
    fun `load returns LoadResult Page when API call is successful`() = runBlocking {
        // Given
        val articles = listOf(
            Article(
                publishedAt = "2024-11-11T01:00:00Z",
                source = Source(id = null, name = ""),
                author = null,
                title = "",
                description = "",
                url = "",
                urlToImage = null,
                content = ""
            ),
            Article(
                publishedAt = "2024-11-11T02:00:00Z",
                source = Source(id = null, name = ""),
                author = null,
                title = "",
                description = "",
                url = "",
                urlToImage = null,
                content = ""
            )
        )
        coEvery { api.getNews(pageSize = 2, from = latestPublishedAt) } returns Result.success(
            NewsApiResponse(articles = articles, status = "ok", totalResults = 2)
        )

        // when
        val result = pagingSource.load(PagingSource.LoadParams.Refresh(latestPublishedAt, 2, false))

        // Then
        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(articles, page.data)
        assertEquals(null, page.prevKey)
        assertEquals("2024-11-11T02:00:00Z", page.nextKey)
    }

    @Test
    fun `load returns LoadResult Error when IOException is thrown`() = runBlocking {
        // Given
        coEvery {
            api.getNews(
                pageSize = 2,
                from = latestPublishedAt
            )
        } throws IOException("Network error")

        // When
        val result = pagingSource.load(PagingSource.LoadParams.Refresh(latestPublishedAt, 2, false))

        // Then
        assertTrue(result is PagingSource.LoadResult.Error)
        val error = result as PagingSource.LoadResult.Error
        assertTrue(error.throwable is IOException)
        assertEquals("Network error", error.throwable.message)
    }

    @Test
    fun `load returns LoadResult Error when HttpException is thrown`() = runBlocking {
        // Given
        val errorMessage = "Server error"
        val errorResponse = Response.error<NewsApiResponse>(
            500,
            "{\"error\":\"$errorMessage\"}".toResponseBody("application/json".toMediaType())
        )
        val httpException = HttpException(errorResponse)
        coEvery { api.getNews(pageSize = 2, from = latestPublishedAt) } throws httpException

        // When
        val result = pagingSource.load(PagingSource.LoadParams.Refresh(latestPublishedAt, 2, false))

        // Then
        assertTrue(result is PagingSource.LoadResult.Error)
        val error = result as PagingSource.LoadResult.Error
        assertTrue(error.throwable is HttpException)
        assertTrue((error.throwable as HttpException).code() == 500)
    }

    @Test
    fun `load returns LoadResult Error when ApiException is thrown`() = runBlocking {
        // Given
        val apiException =
            ApiException(ApiError(message = "API error", status = "error", code = "400"))
        coEvery { api.getNews(pageSize = 2, from = latestPublishedAt) } throws apiException

        // When
        val result = pagingSource.load(PagingSource.LoadParams.Refresh(latestPublishedAt, 2, false))

        // Then
        assertTrue(result is PagingSource.LoadResult.Error)
        val error = result as PagingSource.LoadResult.Error
        assertTrue(error.throwable is ApiException)
        assertEquals("API error", (error.throwable as ApiException).message)
    }

    @Test
    fun `load returns LoadResult Page with endOfPagination when articles are old`() = runBlocking {
        // Given
        val articles = listOf(
            Article(
                publishedAt = "2024-10-30T23:00:00Z",
                source = Source(id = null, name = ""),
                author = null,
                title = "",
                description = "",
                url = "",
                urlToImage = null,
                content = ""
            ),
            Article(
                publishedAt = "2024-10-30T22:00:00Z",
                source = Source(id = null, name = ""),
                author = null,
                title = "",
                description = "",
                url = "",
                urlToImage = null,
                content = ""
            )
        )
        coEvery { api.getNews(pageSize = 2, from = latestPublishedAt) } returns Result.success(
            NewsApiResponse(articles = articles, status = "ok", totalResults = 2)
        )

        // When
        val result = pagingSource.load(PagingSource.LoadParams.Refresh(latestPublishedAt, 2, false))

        // Then
        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(articles, page.data)
        assertEquals(null, page.nextKey)
    }
}