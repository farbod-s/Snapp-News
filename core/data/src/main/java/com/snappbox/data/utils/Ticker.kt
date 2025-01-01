package com.snappbox.data.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Duration

interface Ticker {

    fun flowWith(period: Duration, subscriptionCount: StateFlow<Int>): Flow<Unit>
}