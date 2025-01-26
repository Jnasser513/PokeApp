package com.jnasser.pokeapp.pokemonList.data

sealed interface UpdatePokemonListStatus {
    data object Continue: UpdatePokemonListStatus
    data object Error: UpdatePokemonListStatus
    data object Stop: UpdatePokemonListStatus
}