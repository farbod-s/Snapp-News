package com.snappbox.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.snappbox.ui.R

@Composable
fun <T : Any> LazyPagingVerticalGrid(
    modifier: Modifier,
    items: LazyPagingItems<T>,
    key: ((index: Int) -> Any)? = null,
    columns: GridCells,
    state: LazyGridState = rememberLazyGridState(),
    horizontalArrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(0.dp),
    verticalArrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(0.dp),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    itemContent: @Composable (T?) -> Unit,
    loadingContent: @Composable () -> Unit = { DefaultLoadingView() },
    errorContent: @Composable (String, () -> Unit) -> Unit = { message, retry ->
        DefaultErrorView(message, retry)
    },
    loadingItemContent: @Composable () -> Unit = { DefaultLoadingItem() },
    errorItemContent: @Composable (String, () -> Unit) -> Unit = { message, retry ->
        DefaultErrorItem(message, retry)
    },
    emptyContent: @Composable () -> Unit = { DefaultEmptyView() },
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (items.loadState.refresh) {
            is LoadState.Loading -> {
                loadingContent()
            }

            is LoadState.Error -> {
                val e = items.loadState.refresh as LoadState.Error
                errorContent(
                    e.error.message ?: LocalContext.current.getString(R.string.error_unknown)
                ) {
                    items.refresh()
                }
            }

            else -> {
                if (items.itemCount == 0) {
                    emptyContent()
                } else {
                    LazyVerticalGrid(
                        columns = columns,
                        state = state,
                        horizontalArrangement = horizontalArrangement,
                        verticalArrangement = verticalArrangement,
                        contentPadding = contentPadding,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(count = items.itemCount, key = key) { index ->
                            items[index]?.let { item ->
                                itemContent(item)
                            }
                        }

                        when (items.loadState.append) {
                            is LoadState.Loading -> {
                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    loadingItemContent()
                                }
                            }

                            is LoadState.Error -> {
                                val e = items.loadState.append as LoadState.Error
                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    errorItemContent(
                                        e.error.message
                                            ?: LocalContext.current.getString(R.string.error_unknown)
                                    ) {
                                        items.retry()
                                    }
                                }
                            }

                            else -> {}
                        }
                    }
                }
            }
        }
    }
}