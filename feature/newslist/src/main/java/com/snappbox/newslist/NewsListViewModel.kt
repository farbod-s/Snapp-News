package com.snappbox.newslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.snappbox.data.model.NewsDto
import com.snappbox.data.repository.NewsRepository
import com.snappbox.data.utils.ConnectivityChecker
import com.snappbox.data.utils.Ticker
import com.snappbox.data.utils.mutableStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@HiltViewModel
class NewsListViewModel @Inject constructor(
    private val repository: NewsRepository,
    private val ticker: Ticker,
    private val connectivityChecker: ConnectivityChecker
) : ViewModel() {

    private val connectionState = connectivityChecker.connectionFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, initialValue = false)

    val lastNewsCount: StateFlow<Int> = repository.getLastNewsCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val refreshNewsState: StateFlow<Unit> =
        mutableStateFlow(viewModelScope, Unit) { subscriptionCount ->
            ticker.flowWith(2.toDuration(DurationUnit.MINUTES), subscriptionCount)
                .filter { connectionState.value }
                .onEach { repository.refreshNewsWithPagingSource() }
        }.asStateFlow()

    val newsFlow: Flow<PagingData<NewsDto>> = repository.getNewsStream().cachedIn(viewModelScope)

    init {
        showLastNews()
    }

    fun showLastNews() {
        viewModelScope.launch {
            repository.migrateLastNews()
        }
    }
}