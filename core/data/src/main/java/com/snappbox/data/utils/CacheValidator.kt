package com.snappbox.data.utils

interface CacheValidator {
    suspend fun isValid(): Boolean
}