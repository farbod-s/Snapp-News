@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.snappbox.snappnews.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.snappbox.ui.providers.SharedElementScopeProvider
import com.snappbox.ui.theme.SnappNewsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SnappNewsTheme {
                SharedElementScopeProvider {
                    val navController = rememberNavController()
                    NavHost(navController, startDestination = Screen.NewsList.route) {
                        navigationGraph(navController)
                    }
                }
            }
        }
    }
}