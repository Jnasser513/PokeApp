package com.jnasser.pokeapp.pokemonList.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jnasser.pokeapp.core.data.pokemon.Pokemon
import com.jnasser.pokeapp.core.data.pokemon.PokemonRepository
import com.jnasser.pokeapp.core.di.IoDispatcher
import com.jnasser.pokeapp.core.presentation.PresentationUtils
import com.jnasser.pokeapp.core.presentation.UIStatus
import com.jnasser.pokeapp.core.usecases.InsertPokemonListUseCase
import com.jnasser.pokeapp.pokemonList.data.PokemonResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val insertPokemonListUseCase: InsertPokemonListUseCase
): ViewModel() {

    private val _statusInsertPokemonList = MutableLiveData<UIStatus<List<Long>>>()
    val statusInsertPokemonList: LiveData<UIStatus<List<Long>>> get() = _statusInsertPokemonList

    fun insertPokemonList(pokemonList: List<PokemonResponse>) {
        _statusInsertPokemonList.value = UIStatus.Loading("Cargando...")
        viewModelScope.launch(ioDispatcher) {
            _statusInsertPokemonList.postValue(
                PresentationUtils.mapRoomResponseToUIStatus(insertPokemonListUseCase.invoke(pokemonList))
            )
        }
    }
}