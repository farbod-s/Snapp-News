package com.snappbox.data.utils

import com.snappbox.database.dao.NewsDao
import com.snappbox.database.datasource.PreferencesDataSource
import javax.inject.Inject

class DefaultCacheValidator @Inject constructor(
    private val settings: PreferencesDataSource,
    private val newsDao: NewsDao,
) : CacheValidator {

    companion object {
        private const val CACHE_VALIDITY_TIME = 5 * 60 * 1000L // 5 minutes
    }

    override suspend fun isValid(): Boolean {
        val currentTime = System.currentTimeMillis()
        val lastRefreshTime = settings.getLastFetchTimestamp()
        val isCacheOldEnough = (currentTime - lastRefreshTime) > CACHE_VALIDITY_TIME
        val isLocalStorageEmpty = newsDao.getLatestPublishedAt().isNullOrEmpty()

        return !isCacheOldEnough && !isLocalStorageEmpty
    }
}