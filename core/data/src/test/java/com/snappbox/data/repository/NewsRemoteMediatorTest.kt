package com.snappbox.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.snappbox.data.utils.CacheValidator
import com.snappbox.data.utils.ConnectivityChecker
import com.snappbox.database.NewsDatabase
import com.snappbox.database.datasource.PreferencesDataSource
import com.snappbox.network.NewsApiService
import com.snappbox.network.exception.ApiException
import com.snappbox.network.model.ApiError
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import okio.IOException
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException

@OptIn(ExperimentalPagingApi::class)
class NewsRemoteMediatorTest {

    private val pagingConfig = PagingConfig(pageSize = 20, enablePlaceholders = false)

    private lateinit var api: NewsApiService
    private lateinit var db: NewsDatabase
    private lateinit var settings: PreferencesDataSource
    private lateinit var cacheValidator: CacheValidator
    private lateinit var connectivityChecker: ConnectivityChecker
    private lateinit var remoteMediator: NewsRemoteMediator

    @Before
    fun setup() {
        api = mockk()
        db = mockk(relaxed = true)
        settings = mockk(relaxed = true)
        cacheValidator = mockk()
        connectivityChecker = mockk()
        remoteMediator = NewsRemoteMediator(api, db, settings, cacheValidator, connectivityChecker)
    }

    @Test
    fun testInitialize_WithValidCacheAndNoConnection() = runBlocking {
        // Given
        coEvery { cacheValidator.isValid() } returns true
        every { connectivityChecker.isConnected() } returns false

        // When
        val result = remoteMediator.initialize()

        // Then
        assert(result == RemoteMediator.InitializeAction.SKIP_INITIAL_REFRESH)
    }

    @Test
    fun testInitialize_NeedRefresh() = runBlocking {
        // Given
        coEvery { cacheValidator.isValid() } returns false
        every { connectivityChecker.isConnected() } returns true

        // When
        val result = remoteMediator.initialize()

        // Then
        assert(result == RemoteMediator.InitializeAction.LAUNCH_INITIAL_REFRESH)
    }

    @Test
    fun testLoad_ErrorOnIOException() = runBlocking {
        // Given
        coEvery { api.getNews(any(), any()) } throws IOException("Network error")

        // When
        val result = remoteMediator.load(
            LoadType.REFRESH,
            PagingState(emptyList(), null, pagingConfig, 0)
        )

        // Then
        assert(result is RemoteMediator.MediatorResult.Error)
        assert((result as RemoteMediator.MediatorResult.Error).throwable is IOException)
    }

    @Test
    fun testLoad_ErrorOnHttpException() = runBlocking {
        // Given
        coEvery { api.getNews(any(), any()) } throws HttpException(mockk(relaxed = true))

        // When
        val result = remoteMediator.load(
            LoadType.REFRESH,
            PagingState(emptyList(), null, pagingConfig, 0)
        )

        // Then
        assert(result is RemoteMediator.MediatorResult.Error)
        assert((result as RemoteMediator.MediatorResult.Error).throwable is HttpException)
    }

    @Test
    fun testLoad_ErrorOnApiException() = runBlocking {
        // Given
        coEvery { api.getNews(any(), any()) } throws ApiException(
            ApiError(
                status = "error",
                code = "400",
                message = "API error"
            )
        )

        // When
        val result = remoteMediator.load(
            LoadType.REFRESH,
            PagingState(emptyList(), null, pagingConfig, 0)
        )

        // Then
        assert(result is RemoteMediator.MediatorResult.Error)
        assert((result as RemoteMediator.MediatorResult.Error).throwable is ApiException)
    }
}