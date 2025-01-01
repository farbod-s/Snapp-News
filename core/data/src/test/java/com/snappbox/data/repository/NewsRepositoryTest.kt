package com.snappbox.data.repository

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import com.snappbox.database.dao.NewsDao
import com.snappbox.network.NewsApiService
import com.snappbox.network.model.Article
import com.snappbox.network.model.Source
import com.snappbox.network.source.NewsPagingSource
import com.snappbox.network.source.NewsPagingSourceFactory
import io.mockk.coEvery
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class NewsRepositoryTest {

    private lateinit var repository: DefaultNewsRepository
    private lateinit var mockNewsDao: NewsDao
    private lateinit var mockApi: NewsApiService
    private lateinit var mockPagingConfig: PagingConfig
    private lateinit var mockPagingSourceFactory: NewsPagingSourceFactory

    @Before
    fun setUp() {
        mockNewsDao = mockk(relaxed = true)
        mockApi = mockk(relaxed = true)
        mockPagingConfig = PagingConfig(pageSize = 20)
        mockPagingSourceFactory = mockk(relaxed = true)

        repository = DefaultNewsRepository(
            remoteMediator = mockk(relaxed = true),
            pagingConfig = mockPagingConfig,
            newsDao = mockNewsDao,
            api = mockApi,
            pagingSourceFactory = mockPagingSourceFactory
        )
    }

    @Test
    fun `refreshNewsWithPagingSource - loads pages and saves articles`() = runTest {
        // Given
        val initialPublishedAt = "2024-01-01T00:00:00Z"
        val articlesPage1 = listOf(
            Article(
                source = Source(id = null, name = "Source1"),
                author = "Author1",
                title = "Title1",
                description = "Description1",
                url = "http://example.com/1",
                urlToImage = "http://example.com/image1.jpg",
                publishedAt = "2024-01-01T01:00:00Z",
                content = "Content1"
            )
        )
        val articlesPage2 = listOf(
            Article(
                source = Source(id = null, name = "Source2"),
                author = "Author2",
                title = "Title2",
                description = "Description2",
                url = "http://example.com/2",
                urlToImage = "http://example.com/image2.jpg",
                publishedAt = "2024-01-01T02:00:00Z",
                content = "Content2"
            )
        )

        // Mock latest publication date fetching
        coEvery { mockNewsDao.getLastNewsLatestPublishedAt() } returns initialPublishedAt
        coEvery { mockNewsDao.getLatestPublishedAt() } returns null

        // Mock the paging source to return paged articles in sequence
        val mockPagingSource = mockk<NewsPagingSource>(relaxed = true)
        coEvery { mockPagingSource.load(any()) } returnsMany listOf(
            PagingSource.LoadResult.Page(
                data = articlesPage1,
                prevKey = null,
                nextKey = articlesPage1.last().publishedAt
            ),
            PagingSource.LoadResult.Page(
                data = articlesPage2,
                prevKey = articlesPage1.last().publishedAt,
                nextKey = null
            )
        )

        // Mock paging source factory to create the paging source
        every { mockPagingSourceFactory.create(initialPublishedAt) } returns mockPagingSource

        // When
        repository.refreshNewsWithPagingSource()

        // Then
        coVerifyOrder {
            // Verify that articles with the correct properties were saved
            mockNewsDao.insertLastNews(match {
                it.size == articlesPage1.size && it.first().title == articlesPage1.first().title
            })
            mockNewsDao.insertLastNews(match {
                it.size == articlesPage2.size && it.first().title == articlesPage2.first().title
            })
        }
    }
}