package com.snappbox.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.snappbox.ui.providers.LocalAnimatedVisibilityScope
import com.snappbox.ui.providers.LocalSharedTransitionScope
import com.snappbox.ui.theme.SnappNewsTheme

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PreviewWrapper(content: @Composable () -> Unit) {
    SnappNewsTheme {
        SharedTransitionLayout {
            AnimatedVisibility(visible = true) {
                CompositionLocalProvider(
                    LocalSharedTransitionScope provides this@SharedTransitionLayout,
                    LocalAnimatedVisibilityScope provides this
                ) {
                    content()
                }
            }
        }
    }
}