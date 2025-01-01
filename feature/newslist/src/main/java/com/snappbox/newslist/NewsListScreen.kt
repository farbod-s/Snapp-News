package com.snappbox.newslist

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.snappbox.data.model.NewsDto
import com.snappbox.ui.components.LazyPagingVerticalGrid
import com.snappbox.ui.components.previewLazyPagingItems
import com.snappbox.ui.theme.SnappNewsTheme

@Composable
fun NewsListScreen(
    modifier: Modifier = Modifier,
    viewModel: NewsListViewModel = hiltViewModel(),
    onItemClicked: (NewsDto) -> Unit
) {
    viewModel.refreshNewsState.collectAsStateWithLifecycle()

    val items = viewModel.newsFlow.collectAsLazyPagingItems()
    val lastNewsCount by viewModel.lastNewsCount.collectAsStateWithLifecycle()

    val listState = rememberLazyGridState()

    // Track current first item
    val currentFirstItem by remember {
        derivedStateOf {
            items.itemCount.takeIf { it > 0 }?.let { items[0]?.id.orEmpty() }.orEmpty()
        }
    }

    // Track previous first item
    var previousFirstItem by remember { mutableStateOf("") }

    // Track whether notification was clicked
    var shouldScrollToTop by remember { mutableStateOf(false) }

    // Runs whenever current first item changes or should scroll to top
    LaunchedEffect(currentFirstItem, shouldScrollToTop) {
        if (shouldScrollToTop && previousFirstItem != currentFirstItem) {
            // Scroll to the top if the first item has changed since notification click
            listState.animateScrollToItem(0)
            // Reset the scroll flag after handling the click
            shouldScrollToTop = false
        }
        previousFirstItem = currentFirstItem
    }

    NewsListRootView(
        modifier = modifier,
        listState = listState,
        items = items,
        newArticlesCount = lastNewsCount,
        onNotificationClicked = {
            viewModel.showLastNews()
            shouldScrollToTop = true
        },
        onItemClicked = onItemClicked
    )
}

@Composable
fun NewsListRootView(
    listState: LazyGridState = rememberLazyGridState(),
    modifier: Modifier = Modifier,
    items: LazyPagingItems<NewsDto>,
    newArticlesCount: Int,
    onNotificationClicked: () -> Unit,
    onItemClicked: (NewsDto) -> Unit
) {
    Scaffold { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyPagingVerticalGrid(
                modifier = Modifier.fillMaxSize(),
                state = listState,
                columns = GridCells.Fixed(1),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                items = items,
                key = { index ->
                    items[index]?.id.orEmpty()
                },
                itemContent = { article ->
                    article?.let {
                        NewsItem(article = it) { onItemClicked(it) }
                    }
                }
            )
            if (newArticlesCount > 0) {
                NewsNotification(
                    count = newArticlesCount,
                    onClick = { onNotificationClicked() }
                )
            }
        }
    }
}

@Preview("Light Theme Preview")
@Composable
private fun NewsListRootViewLightThemePreview() {
    SnappNewsTheme {
        NewsListRootView(
            items = previewLazyPagingItems(emptyList()),
            newArticlesCount = 3,
            onNotificationClicked = {}
        ) {}
    }
}

@Preview("Dark Theme Preview", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun NewsListRootViewDarkThemePreview() {
    SnappNewsTheme {
        NewsListRootView(
            items = previewLazyPagingItems(emptyList()),
            newArticlesCount = 3,
            onNotificationClicked = {}
        ) {}
    }
}