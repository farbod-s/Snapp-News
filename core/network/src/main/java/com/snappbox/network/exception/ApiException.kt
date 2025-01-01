package com.snappbox.network.exception

import com.snappbox.network.model.ApiError

class ApiException(private val error: ApiError) : Exception(error.message)
