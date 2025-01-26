package com.jnasser.pokeapp.pokemonList.presentation.utils

import android.content.Context
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.jnasser.pokeapp.R
import com.jnasser.pokeapp.core.presentation.UIStatus
import com.jnasser.pokeapp.core.utils.extensions.hideView
import com.jnasser.pokeapp.core.utils.extensions.showToast
import com.jnasser.pokeapp.core.utils.extensions.showView

class UIStatusHandler(private val context: Context) {

    fun <T> handleUIStatus(progressBar: CircularProgressIndicator? = null, status: UIStatus<T>?, onSuccess: (T?) -> Unit = {}, onEmptyList: ((T?)->Unit)? = null) {
        when (status) {
            is UIStatus.Error -> {
                // Aqui podriamos validar que tipo de excepcion se dio y ejecutar alguna accion dependiendo del tipo de error
                context.showToast("Algo salió mal...")
                status.exception.printStackTrace()
                progressBar?.hideView()
            }
            is UIStatus.Loading -> {
                progressBar?.showView()
            }
            is UIStatus.Success -> {
                onSuccess(status.data)
                progressBar?.hideView()
            }
            is UIStatus.EmptyList -> {
                onEmptyList?.invoke(status.data)
                progressBar?.hideView()
            }
            else -> {
                context.showToast("Algo salió mal")
                progressBar?.hideView()
            }
        }
    }
}
