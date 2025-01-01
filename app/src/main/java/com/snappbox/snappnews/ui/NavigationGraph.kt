package com.snappbox.snappnews.ui

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.snappbox.newsdetail.NewsDetailScreen
import com.snappbox.newslist.NewsListScreen
import com.snappbox.ui.providers.composableWithAnimatedContentScope

fun NavGraphBuilder.navigationGraph(navController: NavController) {
    composableWithAnimatedContentScope(route = Screen.NewsList.route) {
        NewsListScreen { article ->
            navController.navigate(Screen.NewsDetail.createRoute(articleId = article.id))
        }
    }
    composableWithAnimatedContentScope(
        route = Screen.NewsDetail.route,
        arguments = listOf(
            navArgument(name = "articleId") {
                type = NavType.StringType
                defaultValue = ""
            }
        )
    ) { backStackEntry ->
        val articleId = backStackEntry.arguments?.getString("articleId")
            ?: return@composableWithAnimatedContentScope
        NewsDetailScreen(articleId)
    }
}