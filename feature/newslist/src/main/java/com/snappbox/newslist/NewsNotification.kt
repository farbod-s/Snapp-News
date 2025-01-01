package com.snappbox.newslist

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.snappbox.ui.theme.SnappNewsTheme

@Composable
fun NewsNotification(
    modifier: Modifier = Modifier,
    count: Int,
    onClick: () -> Unit
) {
    AnimatedVisibility(
        visible = count > 0,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
    ) {
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            ElevatedAssistChip(
                modifier = Modifier.padding(16.dp),
                label = { Text(text = "New Articles ($count)") },
                colors = AssistChipDefaults.elevatedAssistChipColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    labelColor = MaterialTheme.colorScheme.onPrimary
                ),
                onClick = { onClick() },
            )
        }
    }
}

@Preview("Light Theme Preview")
@Composable
private fun NewsNotificationLightThemePreview() {
    SnappNewsTheme {
        NewsNotification(
            count = 3
        ) {}
    }
}

@Preview("Dark Theme Preview", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun NewsNotificationDarkThemePreview() {
    SnappNewsTheme {
        NewsNotification(
            count = 3
        ) {}
    }
}