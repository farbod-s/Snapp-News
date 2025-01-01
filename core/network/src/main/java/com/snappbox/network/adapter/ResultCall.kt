package com.snappbox.network.adapter

import com.google.gson.Gson
import com.snappbox.network.exception.ApiException
import com.snappbox.network.model.ApiError
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResultCall<T>(private val delegate: Call<T>) : Call<Result<T>> {

    override fun enqueue(callback: Callback<Result<T>>) {
        delegate.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        callback.onResponse(this@ResultCall, Response.success(Result.success(body)))
                    } else {
                        callback.onResponse(
                            this@ResultCall,
                            Response.success(Result.failure(Throwable("Response body is null")))
                        )
                    }
                } else {
                    val error = parseError(response)
                    callback.onResponse(this@ResultCall, Response.success(Result.failure(error)))
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                callback.onResponse(this@ResultCall, Response.success(Result.failure(t)))
            }
        })
    }

    override fun execute(): Response<Result<T>> {
        throw UnsupportedOperationException("ResultCall doesn't support execute")
    }

    override fun clone(): Call<Result<T>> = ResultCall(delegate.clone())

    override fun isExecuted(): Boolean = delegate.isExecuted

    override fun cancel(): Unit = delegate.cancel()

    override fun isCanceled(): Boolean = delegate.isCanceled

    override fun request(): Request = delegate.request()

    override fun timeout(): Timeout = delegate.timeout()

    private fun parseError(response: Response<T>): Throwable {
        return try {
            val errorBody = response.errorBody()?.string()
            val apiError = Gson().fromJson(errorBody, ApiError::class.java)
            ApiException(apiError)
        } catch (e: Exception) {
            Throwable("parse error failed")
        }
    }
}