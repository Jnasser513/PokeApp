package com.jnasser.pokeapp.core.data

sealed interface RoomResponse<T> {
    data class Success<T>(val data: T): RoomResponse<T>
    data class Error<T>(val exception: Exception): RoomResponse<T>
    data class EmptyList<T>(val data: T): RoomResponse<T>
}
