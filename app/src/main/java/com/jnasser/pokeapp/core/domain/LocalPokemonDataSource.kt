package com.jnasser.pokeapp.core.domain

import com.jnasser.pokeapp.core.data.pokemon.Pokemon
import com.jnasser.pokeapp.core.data.RoomResponse

//Creamos una abstraccion en caso de ser necesario un cambio en la libreria de almacenamiento local
interface LocalPokemonDataSource {

    suspend fun insertPokemonList(pokemonList: List<Pokemon>): RoomResponse<List<Long>>
    suspend fun getPokemonList(): RoomResponse<List<Pokemon>>
    suspend fun searchPokemonByType(type: String): RoomResponse<List<Pokemon>>

}