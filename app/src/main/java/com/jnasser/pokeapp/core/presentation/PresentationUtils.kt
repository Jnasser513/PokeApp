package com.jnasser.pokeapp.core.presentation

import com.jnasser.pokeapp.core.data.RoomResponse


object PresentationUtils {

    /*fun <T> mapResponseToUIStatus(response: ApiResponse<T>, onSuccess: ((T?) -> Unit)? = null): UIStatus<T> {
        return when (response) {
            is ApiResponse.Error -> UIStatus.Error(response.exception)
            is ApiResponse.ErrorWithMessage -> UIStatus.ErrorWithMessage(response.message)
            is ApiResponse.Success -> {
                onSuccess?.invoke(response.data)
                UIStatus.Success(response.data)
            }
            is ApiResponse.EmptyList -> UIStatus.EmptyList(response.data)
            is ApiResponse.LogOut -> UIStatus.LogOut(response.message)
        }
    }*/

    fun <T> mapRoomResponseToUIStatus(response: RoomResponse<T>, onSuccess: ((T?) -> Unit)? = null): UIStatus<T> {
        return when (response) {
            is RoomResponse.Error -> UIStatus.Error(response.exception)
            is RoomResponse.Success -> {
                onSuccess?.invoke(response.data)
                UIStatus.Success(response.data)
            }
            is RoomResponse.EmptyList -> UIStatus.EmptyList(response.data)
        }
    }

}