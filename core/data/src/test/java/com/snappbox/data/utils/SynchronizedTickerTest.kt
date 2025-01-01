package com.snappbox.data.utils

import app.cash.turbine.turbineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.testTimeSource
import org.junit.Test
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalCoroutinesApi::class)
class SynchronizedTickerTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private val subscriptionCount = MutableStateFlow(0)
    private val synchronizedTicker = SynchronizedTicker(testScope.testTimeSource)

    @Test
    fun `emits ticks every 2 minutes when active`() = testScope.runTest {
        turbineScope {
            val tickerFlow = synchronizedTicker.flowWith(
                period = 2.minutes,
                subscriptionCount = subscriptionCount
            ).testIn(this)

            subscriptionCount.value = 1

            advanceTimeBy(2.minutes.inWholeMilliseconds)
            tickerFlow.awaitItem()

            advanceTimeBy(2.minutes.inWholeMilliseconds)
            tickerFlow.awaitItem()

            tickerFlow.cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `does not emit ticks when inactive`() = testScope.runTest {
        turbineScope {
            val tickerFlow = synchronizedTicker.flowWith(
                period = 2.minutes,
                subscriptionCount = subscriptionCount
            ).testIn(this)

            subscriptionCount.value = 0
            advanceTimeBy(2.minutes.inWholeMilliseconds)
            tickerFlow.expectNoEvents()

            tickerFlow.cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `resumes ticks on resubscription`() = testScope.runTest {
        turbineScope {
            val tickerFlow = synchronizedTicker.flowWith(
                period = 2.minutes,
                subscriptionCount = subscriptionCount
            ).testIn(this)

            // Start, emit one tick
            subscriptionCount.value = 1
            advanceTimeBy(2.minutes.inWholeMilliseconds)
            tickerFlow.awaitItem()

            // Stop, no ticks
            subscriptionCount.value = 0
            advanceTimeBy(2.minutes.inWholeMilliseconds)
            tickerFlow.expectNoEvents()

            // Restart, emit one tick again
            subscriptionCount.value = 1
            (2.minutes.inWholeMilliseconds)
            tickerFlow.awaitItem()

            tickerFlow.cancelAndConsumeRemainingEvents()
        }
    }
}