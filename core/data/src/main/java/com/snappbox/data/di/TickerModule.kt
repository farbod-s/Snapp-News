package com.snappbox.data.di

import com.snappbox.data.utils.ElapsedRealTimeSource
import com.snappbox.data.utils.SynchronizedTicker
import com.snappbox.data.utils.Ticker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlin.time.TimeSource

@Module
@InstallIn(SingletonComponent::class)
object TickerModule {

    @Singleton
    @Provides
    fun provideTimeSource(): TimeSource = ElapsedRealTimeSource

    @Singleton
    @Provides
    fun provideSynchronizedTicker(timeSource: TimeSource): Ticker = SynchronizedTicker(timeSource)
}