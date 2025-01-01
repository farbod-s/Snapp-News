package com.snappbox.network.model

data class ApiError(
    val status: String,
    val code: String,
    val message: String
)