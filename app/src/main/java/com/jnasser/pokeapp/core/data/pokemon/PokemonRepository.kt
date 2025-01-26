package com.jnasser.pokeapp.core.data.pokemon

import com.jnasser.pokeapp.core.data.RoomResponse
import com.jnasser.pokeapp.core.domain.LocalPokemonDataSource
import com.jnasser.pokeapp.core.domain.RemotePokemonDataSource
import com.jnasser.pokeapp.pokemonList.data.PokemonResponse
import javax.inject.Inject

class PokemonRepository @Inject constructor(
    private val localPokemonDataSource: LocalPokemonDataSource,
    private val remotePokemonDataSource: RemotePokemonDataSource
) {

    suspend fun insertPokemons(pokemonList: List<PokemonResponse>) =
        localPokemonDataSource.insertPokemonList(pokemonList.toPokemonList2())

    suspend fun getAllPokemons() = localPokemonDataSource.getPokemonList()

    suspend fun searchPokemonByType(type: String) = localPokemonDataSource.searchPokemonByType(type)

    suspend fun getRemotePokemonList(offset: Int, limit: Int) =
        remotePokemonDataSource.getPokemonList(offset, limit)

}