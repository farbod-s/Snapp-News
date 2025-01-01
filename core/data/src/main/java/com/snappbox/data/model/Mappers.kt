package com.snappbox.data.model

import com.snappbox.database.model.LastNewsEntity
import com.snappbox.database.model.NewsEntity
import com.snappbox.network.model.Article

internal fun Article.toMainEntity() = NewsEntity(
    title = title,
    description = description,
    url = url,
    urlToImage = urlToImage,
    publishedAt = publishedAt,
    source = source.name
)

internal fun Article.toTempEntity() = LastNewsEntity(
    title = title,
    description = description,
    url = url,
    urlToImage = urlToImage,
    publishedAt = publishedAt,
    source = source.name
)

fun NewsEntity.toDto() = NewsDto(
    id = id,
    title = title,
    description = description,
    url = url,
    urlToImage = urlToImage,
    publishedAt = publishedAt,
    source = source
)