package com.example.neatflixdemo.network

sealed class ResourceNotifier<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : ResourceNotifier<T>(data)
    class Error<T>(message: String, data: T? = null) : ResourceNotifier<T>(data, message)
    class Loading<T> : ResourceNotifier<T>()
}
