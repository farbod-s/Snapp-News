package com.snappbox.data.utils

import com.snappbox.database.dao.NewsDao
import com.snappbox.database.datasource.PreferencesDataSource
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

class CacheValidatorTest {

    private lateinit var preferencesDataSource: PreferencesDataSource
    private lateinit var newsDao: NewsDao
    private lateinit var cacheValidator: DefaultCacheValidator

    @Before
    fun setUp() {
        preferencesDataSource = mockk()
        newsDao = mockk()
        cacheValidator = DefaultCacheValidator(preferencesDataSource, newsDao)
    }

    @Test
    fun `isValid returns true when cache is valid and local storage is not empty`() = runBlocking {
        // Given
        val currentTime = System.currentTimeMillis()
        coEvery { preferencesDataSource.getLastFetchTimestamp() } returns currentTime
        coEvery { newsDao.getLatestPublishedAt() } returns "2024-11-11T00:00:00Z"

        // When
        val result = cacheValidator.isValid()

        // Then
        assertTrue(result)
    }

    @Test
    fun `isValid returns false when cache is too old`() = runBlocking {
        // Given
        val currentTime = System.currentTimeMillis()
        coEvery { preferencesDataSource.getLastFetchTimestamp() } returns currentTime - (6 * 60 * 1000)
        coEvery { newsDao.getLatestPublishedAt() } returns "2024-11-11T00:00:00Z"

        // When
        val result = cacheValidator.isValid()

        // Then
        assertFalse(result)
    }

    @Test
    fun `isValid returns false when local storage is empty`() = runBlocking {
        // Given
        val currentTime = System.currentTimeMillis()
        coEvery { preferencesDataSource.getLastFetchTimestamp() } returns currentTime
        coEvery { newsDao.getLatestPublishedAt() } returns null

        // When
        val result = cacheValidator.isValid()

        // Then
        assertFalse(result)
    }

    @Test
    fun `isValid returns false when both conditions are invalid`() = runBlocking {
        // Given
        val currentTime = System.currentTimeMillis()
        coEvery { preferencesDataSource.getLastFetchTimestamp() } returns currentTime - (6 * 60 * 1000)
        coEvery { newsDao.getLatestPublishedAt() } returns null

        // When
        val result = cacheValidator.isValid()

        // Then
        assertFalse(result)
    }
}