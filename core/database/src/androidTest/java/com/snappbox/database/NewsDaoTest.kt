package com.snappbox.database

import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.snappbox.database.dao.NewsDao
import com.snappbox.database.model.LastNewsEntity
import com.snappbox.database.model.NewsEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NewsDaoTest {

    private lateinit var database: NewsDatabase
    private lateinit var newsDao: NewsDao

    @Before
    fun setUp() {
        // Create an in-memory database for testing
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            NewsDatabase::class.java
        ).allowMainThreadQueries().build()

        newsDao = database.newsDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAll_and_getArticles_returnsInsertedArticles() = runBlocking {
        // Given
        val articles = listOf(
            NewsEntity(
                id = "1",
                title = "Test Article 1",
                description = "Description 1",
                url = "http://example.com/1",
                urlToImage = "http://example.com/image1.jpg",
                publishedAt = "2023-01-01T00:00:00Z",
                source = "Source A"
            ),
            NewsEntity(
                id = "2",
                title = "Test Article 2",
                description = "Description 2",
                url = "http://example.com/2",
                urlToImage = "http://example.com/image2.jpg",
                publishedAt = "2023-01-02T00:00:00Z",
                source = "Source B"
            )
        )

        // When
        newsDao.insertAll(articles)

        // Then
        val result = newsDao.pagingSource().load(PagingSource.LoadParams.Refresh(null, 10, false))
        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(2, page.data.size)
        assertEquals("Test Article 2", page.data[0].title)
        assertEquals("Test Article 1", page.data[1].title)
    }

    @Test
    fun clearAll_removesAllArticles() = runBlocking {
        // Given
        val articles = listOf(
            NewsEntity(
                id = "1",
                title = "Test Article 1",
                description = null,
                url = "http://example.com/1",
                urlToImage = null,
                publishedAt = "2023-01-01T00:00:00Z",
                source = "Source A"
            )
        )
        newsDao.insertAll(articles)

        // When
        newsDao.clearAll()

        // Then
        val result = newsDao.pagingSource().load(PagingSource.LoadParams.Refresh(null, 10, false))
        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertTrue(page.data.isEmpty())
    }

    @Test
    fun getArticleById_returnsCorrectArticle() = runBlocking {
        // Given
        val article = NewsEntity(
            id = "1",
            title = "Test Article",
            description = "Description",
            url = "http://example.com",
            urlToImage = null,
            publishedAt = "2023-01-01T00:00:00Z",
            source = "Source A"
        )
        newsDao.insertAll(listOf(article))

        // When
        val result = newsDao.getArticleById("1")

        // Then
        assertNotNull(result)
        assertEquals(article.title, result?.title)
    }

    @Test
    fun getLatestPublishedAt_returnsLatestPublishedAt() = runBlocking {
        // Given
        val articles = listOf(
            NewsEntity(
                id = "1",
                title = "Test Article 1",
                description = "Description 1",
                url = "http://example.com/1",
                urlToImage = "http://example.com/image1.jpg",
                publishedAt = "2023-01-01T00:00:00Z",
                source = "Source A"
            ),
            NewsEntity(
                id = "2",
                title = "Test Article 2",
                description = "Description 2",
                url = "http://example.com/2",
                urlToImage = "http://example.com/image2.jpg",
                publishedAt = "2023-01-02T00:00:00Z",
                source = "Source B"
            )
        )
        newsDao.insertAll(articles)

        // When
        val result = newsDao.getLatestPublishedAt()

        // Then
        assertEquals("2023-01-02T00:00:00Z", result)
    }

    @Test
    fun getLastNewsCount_returnsCorrectCount() = runBlocking {
        // Given
        val lastNews = listOf(
            LastNewsEntity(
                id = "1",
                title = "Test Article 1",
                description = "Description 1",
                url = "http://example.com/1",
                urlToImage = "http://example.com/image1.jpg",
                publishedAt = "2023-01-01T00:00:00Z",
                source = "Source A"
            ),
            LastNewsEntity(
                id = "2",
                title = "Test Article 2",
                description = "Description 2",
                url = "http://example.com/2",
                urlToImage = "http://example.com/image2.jpg",
                publishedAt = "2023-01-02T00:00:00Z",
                source = "Source B"
            )
        )
        newsDao.insertLastNews(lastNews)

        // When
        val countFlow = newsDao.getLastNewsCount()
        val count = countFlow.first()

        // Then
        assertEquals(2, count)
    }
}