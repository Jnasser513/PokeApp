package com.jnasser.pokeapp.core.data

sealed interface ApiResponse<T> {
    data class Success<T>(val data: T? = null): ApiResponse<T>
    data class Error<T>(val exception: Exception): ApiResponse<T>
    data class EmptyList<T>(val data: T? = null): ApiResponse<T>
}
