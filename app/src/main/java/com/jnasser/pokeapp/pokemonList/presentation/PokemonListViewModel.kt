package com.jnasser.pokeapp.pokemonList.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(

): ViewModel() {

    private val _statusGetPokemonList = MutableLiveData<>
}