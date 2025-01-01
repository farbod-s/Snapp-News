package com.snappbox.newslist

import app.cash.turbine.test
import app.cash.turbine.turbineScope
import com.snappbox.data.repository.NewsRepository
import com.snappbox.data.utils.ConnectivityChecker
import com.snappbox.data.utils.SynchronizedTicker
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.testTimeSource
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalCoroutinesApi::class)
class NewsListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private val repository: NewsRepository = mockk(relaxed = true)
    private val connectivityChecker: ConnectivityChecker = mockk()

    private lateinit var viewModel: NewsListViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `refreshNewsState triggers repository refresh when connected`() = testScope.runTest {
        turbineScope {
            coEvery { repository.refreshNewsWithPagingSource() } just runs

            every { connectivityChecker.connectionFlow() } returns MutableStateFlow(true)

            viewModel = NewsListViewModel(
                repository = repository,
                connectivityChecker = connectivityChecker,
                ticker = SynchronizedTicker(testScope.testTimeSource)
            )

            viewModel.refreshNewsState.test {
                advanceTimeBy(2.minutes.inWholeMilliseconds)
                awaitItem()

                // Advance time briefly to ensure flow stabilizes
                advanceTimeBy(500)

                coVerify(exactly = 1) { repository.refreshNewsWithPagingSource() }
                cancelAndConsumeRemainingEvents()
            }
        }
    }

    @Test
    fun `refreshNewsState does not trigger repository refresh when disconnected`() =
        testScope.runTest {
            turbineScope {
                coEvery { repository.refreshNewsWithPagingSource() } just runs

                every { connectivityChecker.connectionFlow() } returns MutableStateFlow(false)

                viewModel = NewsListViewModel(
                    repository = repository,
                    connectivityChecker = connectivityChecker,
                    ticker = SynchronizedTicker(testScope.testTimeSource)
                )

                viewModel.refreshNewsState.test {
                    advanceTimeBy(2.minutes.inWholeMilliseconds)
                    awaitItem()

                    // Advance time briefly to ensure flow stabilizes
                    advanceTimeBy(500)

                    coVerify(exactly = 0) { repository.refreshNewsWithPagingSource() }
                    cancelAndConsumeRemainingEvents()
                }
            }
        }
}