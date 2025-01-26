package com.jnasser.pokeapp.pokemonList.data

sealed interface UpdatePokemonListStatus {
    data class Continue(val count: Int): UpdatePokemonListStatus
    data object Error: UpdatePokemonListStatus
    data object Stop: UpdatePokemonListStatus
}