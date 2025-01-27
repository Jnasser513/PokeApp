package com.jnasser.pokeapp.pokemonList.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jnasser.pokeapp.core.data.RoomResponse
import com.jnasser.pokeapp.core.data.pokemon.Pokemon
import com.jnasser.pokeapp.core.di.IoDispatcher
import com.jnasser.pokeapp.core.presentation.PresentationUtils
import com.jnasser.pokeapp.core.presentation.UIStatus
import com.jnasser.pokeapp.core.usecases.GetAllPokemonsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val getAllPokemonsUseCase: GetAllPokemonsUseCase
): ViewModel() {

    private val _statusGetPokemonList = MutableStateFlow<UIStatus<List<Pokemon>>>(UIStatus.Loading("Cargando..."))
    val statusGetPokemonList: StateFlow<UIStatus<List<Pokemon>>> get() = _statusGetPokemonList

    private val _pokemonList = MutableStateFlow<List<Pokemon>>(emptyList())
    val pokemonList: StateFlow<List<Pokemon>> get() = _pokemonList

    fun getPokemonList() {
        _statusGetPokemonList.value = UIStatus.Loading("Cargando...")
        viewModelScope.launch(ioDispatcher) {
            getAllPokemonsUseCase.invoke()
                .catch { e: Throwable ->
                    // Manejo de errores
                    _statusGetPokemonList.value = UIStatus.Error(Exception("Error al cargar los datos."))
                }
                .collect { response ->
                    _statusGetPokemonList.value = PresentationUtils.mapRoomResponseToUIStatus(response)

                    if (response is RoomResponse.Success) {
                        _pokemonList.value = response.data
                    }
                }
        }
    }

}