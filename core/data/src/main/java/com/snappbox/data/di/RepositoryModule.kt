package com.snappbox.data.di

import com.snappbox.data.repository.DefaultNewsRepository
import com.snappbox.data.repository.NewsRepository
import com.snappbox.data.utils.CacheValidator
import com.snappbox.data.utils.ConnectivityChecker
import com.snappbox.data.utils.DefaultCacheValidator
import com.snappbox.data.utils.DefaultConnectivityChecker
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindNewsRepository(repository: DefaultNewsRepository): NewsRepository

    @Singleton
    @Binds
    abstract fun bindCacheValidator(cacheValidator: DefaultCacheValidator): CacheValidator

    @Singleton
    @Binds
    abstract fun bindConnectivityChecker(connectivityChecker: DefaultConnectivityChecker): ConnectivityChecker
}