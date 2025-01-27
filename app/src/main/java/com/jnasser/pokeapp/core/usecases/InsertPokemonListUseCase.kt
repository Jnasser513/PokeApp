package com.jnasser.pokeapp.core.usecases

import com.jnasser.pokeapp.core.data.pokemon.PokemonRepository
import com.jnasser.pokeapp.pokemonList.data.PokemonResponse
import javax.inject.Inject

class InsertPokemonListUseCase @Inject constructor(
    private val repository: PokemonRepository
) {

    suspend fun invoke(pokemonList: List<PokemonResponse>) = repository.insertPokemons(pokemonList)
}