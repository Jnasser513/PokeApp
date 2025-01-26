package com.jnasser.pokeapp.core.domain

import androidx.lifecycle.LiveData
import com.jnasser.pokeapp.core.data.pokemon.Pokemon
import com.jnasser.pokeapp.core.data.RoomResponse
import kotlinx.coroutines.flow.Flow

//Creamos una abstraccion en caso de ser necesario un cambio en la libreria de almacenamiento local
interface LocalPokemonDataSource {

    suspend fun insertPokemonList(pokemonList: List<Pokemon>): RoomResponse<List<Long>>
    suspend fun getPokemonList(): Flow<RoomResponse<List<Pokemon>>>
    suspend fun getPokemonQuantity(): RoomResponse<Int>

}