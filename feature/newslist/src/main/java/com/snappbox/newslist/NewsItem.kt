package com.snappbox.newslist

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.memory.MemoryCache
import coil.request.ImageRequest
import com.snappbox.data.model.NewsDto
import com.snappbox.ui.components.PreviewWrapper
import com.snappbox.ui.providers.LocalAnimatedVisibilityScope
import com.snappbox.ui.providers.LocalSharedTransitionScope
import java.util.UUID

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun NewsItem(article: NewsDto, onClick: () -> Unit) {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedContentScope = LocalAnimatedVisibilityScope.current

    with(sharedTransitionScope) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable { onClick() },
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Box {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(article.urlToImage)
                            .memoryCacheKey(MemoryCache.Key(article.id))
                            .size(300)
                            .build(),
                        contentDescription = article.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(MaterialTheme.colorScheme.inverseOnSurface)
                            .sharedElement(
                                state = rememberSharedContentState(key = "image/${article.id}"),
                                animatedVisibilityScope = animatedContentScope
                            )
                    )
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.BottomStart)
                    ) {
                        Text(
                            text = article.source,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.75f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(8.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = article.description.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Preview("Light Theme Preview")
@Composable
private fun NewsItemLightThemePreview() {
    PreviewWrapper {
        NewsItem(
            article = NewsDto(
                id = UUID.randomUUID().toString(),
                title = "A Nuclear Iran Has Never Been More Likely",
                description = "Threatened by Israel, the Iranian regime could pursue a bomb to try to salvage its national security.",
                url = "https://www.theatlantic.com/international/archive/2024/10/iran-nuclear-weapons-israel-khamenei/680437/",
                urlToImage = "https://cdn.theatlantic.com/thumbor/DZQqqTxw6zIfbogpgULy4BMnaY8=/0x102:4792x2598/1200x625/media/img/mt/2024/10/HR_16224268/original.jpg",
                publishedAt = "2024-10-30T15:05:00Z",
                source = "The Atlantic"
            )
        ) {}
    }
}

@Preview("Dark Theme Preview", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun NewsItemDarkThemePreview() {
    PreviewWrapper {
        NewsItem(
            article = NewsDto(
                id = UUID.randomUUID().toString(),
                title = "A Nuclear Iran Has Never Been More Likely",
                description = "Threatened by Israel, the Iranian regime could pursue a bomb to try to salvage its national security.",
                url = "https://www.theatlantic.com/international/archive/2024/10/iran-nuclear-weapons-israel-khamenei/680437/",
                urlToImage = "https://cdn.theatlantic.com/thumbor/DZQqqTxw6zIfbogpgULy4BMnaY8=/0x102:4792x2598/1200x625/media/img/mt/2024/10/HR_16224268/original.jpg",
                publishedAt = "2024-10-30T15:05:00Z",
                source = "The Atlantic"
            )
        ) {}
    }
}