package com.snappbox.data.utils

import kotlinx.coroutines.flow.Flow

interface ConnectivityChecker {
    fun isConnected(): Boolean

    fun connectionFlow(): Flow<Boolean>
}