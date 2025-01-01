package com.snappbox.data.utils

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.TimeMark
import kotlin.time.TimeSource

class SynchronizedTicker @Inject constructor(
    private val timeSource: TimeSource
) : Ticker {

    override fun flowWith(period: Duration, subscriptionCount: StateFlow<Int>): Flow<Unit> =
        flow {
            var nextEmissionTimeMark: TimeMark? = null
            flow {
                nextEmissionTimeMark?.let { delay(-it.elapsedNow()) }
                while (true) {
                    nextEmissionTimeMark?.let {
                        emit(Unit)
                    }
                    nextEmissionTimeMark = timeSource.markNow() + period
                    delay(period)
                }
            }
                .flowWhileShared(subscriptionCount, SharingStarted.WhileSubscribed(5000))
                .collect(this)
        }
}