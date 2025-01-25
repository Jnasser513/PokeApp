package com.jnasser.pokeapp.core.usecases

import com.jnasser.pokeapp.core.data.pokemon.PokemonRepository
import com.jnasser.pokeapp.pokemonList.data.PokemonResponse
import javax.inject.Inject

class SearchPokemonByTypeUseCase @Inject constructor(
    private val repository: PokemonRepository
) {

    suspend fun invoke(type: String) = repository.searchPokemonByType(type)
}