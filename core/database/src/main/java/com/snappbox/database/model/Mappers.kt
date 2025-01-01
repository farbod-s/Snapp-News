package com.snappbox.database.model

internal fun LastNewsEntity.toEntity() = NewsEntity(
    title = title,
    description = description,
    url = url,
    urlToImage = urlToImage,
    publishedAt = publishedAt,
    source = source
)