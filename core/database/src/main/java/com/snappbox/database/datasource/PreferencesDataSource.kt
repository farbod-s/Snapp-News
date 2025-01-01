package com.snappbox.database.datasource

interface PreferencesDataSource {
    suspend fun getLastFetchTimestamp(): Long

    suspend fun setLastFetchTimestamp(time: Long)
}