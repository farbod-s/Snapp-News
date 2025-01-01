package com.snappbox.data.di

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingConfig
import androidx.paging.RemoteMediator
import com.snappbox.data.repository.NewsRemoteMediator
import com.snappbox.data.utils.CacheValidator
import com.snappbox.data.utils.ConnectivityChecker
import com.snappbox.database.NewsDatabase
import com.snappbox.database.datasource.PreferencesDataSource
import com.snappbox.database.model.NewsEntity
import com.snappbox.network.NewsApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@OptIn(ExperimentalPagingApi::class)
@Module
@InstallIn(SingletonComponent::class)
object PagingModule {

    @Provides
    @Singleton
    fun providePagingConfig() = PagingConfig(
        pageSize = 20,
        enablePlaceholders = false
    )

    @Provides
    @Singleton
    fun provideNewsRemoteMediator(
        api: NewsApiService,
        db: NewsDatabase,
        settings: PreferencesDataSource,
        cacheValidator: CacheValidator,
        connectivityChecker: ConnectivityChecker
    ): RemoteMediator<Int, NewsEntity> = NewsRemoteMediator(
        api = api,
        db = db,
        settings = settings,
        cacheValidator = cacheValidator,
        connectivityChecker = connectivityChecker
    )
}