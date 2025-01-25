package com.jnasser.pokeapp.core.data.pokemon

import com.jnasser.pokeapp.core.data.RoomResponse
import com.jnasser.pokeapp.core.domain.LocalPokemonDataSource
import com.jnasser.pokeapp.pokemonList.data.PokemonResponse
import javax.inject.Inject

class PokemonRepository @Inject constructor(
    private val localPokemonDataSource: LocalPokemonDataSource
) {

    suspend fun insertPokemons(pokemonList: List<PokemonResponse>): RoomResponse<List<Long>> {
        val mappedPokemonList = pokemonList.toPokemonList2()
        return localPokemonDataSource.insertPokemonList(mappedPokemonList)
    }

    suspend fun getAllPokemons() = localPokemonDataSource.getPokemonList()

    suspend fun searchPokemonByType(type: String) = localPokemonDataSource.searchPokemonByType(type)
}