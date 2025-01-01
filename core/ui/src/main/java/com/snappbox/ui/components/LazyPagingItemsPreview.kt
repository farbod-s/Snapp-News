package com.snappbox.ui.components

import androidx.compose.runtime.Composable
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems

@Composable
fun <T : Any> previewLazyPagingItems(data: List<T>): LazyPagingItems<T> {
    val pagingSource = object : PagingSource<Int, T>() {
        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
            return LoadResult.Page(data = data, prevKey = null, nextKey = null)
        }

        override fun getRefreshKey(state: PagingState<Int, T>): Int? = null
    }
    val pager = Pager(PagingConfig(pageSize = data.size)) { pagingSource }
    return pager.flow.collectAsLazyPagingItems()
}