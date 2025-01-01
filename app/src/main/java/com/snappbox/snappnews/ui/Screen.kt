package com.snappbox.snappnews.ui

sealed class Screen(val route: String) {
    data object NewsList : Screen("news_list")
    data object NewsDetail : Screen("news_detail/{articleId}") {
        fun createRoute(articleId: String) = "news_detail/${articleId}"
    }
}