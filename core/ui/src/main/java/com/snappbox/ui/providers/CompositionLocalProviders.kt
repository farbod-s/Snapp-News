package com.snappbox.ui.providers

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScope =
    compositionLocalOf<SharedTransitionScope> { error("No SharedTransitionScope found") }
val LocalAnimatedVisibilityScope =
    compositionLocalOf<AnimatedVisibilityScope> { error("No AnimatedVisibilityScope found") }

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedElementScopeProvider(content: @Composable () -> Unit) {
    SharedTransitionLayout {
        CompositionLocalProvider(LocalSharedTransitionScope provides this) {
            content()
        }
    }
}

fun NavGraphBuilder.composableWithAnimatedContentScope(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable(route = route, arguments = arguments) {
        CompositionLocalProvider(LocalAnimatedVisibilityScope provides this@composable) {
            content(it)
        }
    }
}