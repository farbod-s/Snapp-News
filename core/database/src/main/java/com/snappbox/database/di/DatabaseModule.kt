package com.snappbox.database.di

import android.content.Context
import com.snappbox.database.dao.NewsDao
import com.snappbox.database.NewsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    fun provideNewsDatabase(
        @ApplicationContext context: Context,
    ): NewsDatabase = NewsDatabase.getInstance(context)

    @Provides
    @Singleton
    fun provideNewsDao(database: NewsDatabase): NewsDao = database.newsDao()
}