package com.jnasser.pokeapp.core.databaseManager.room.datasource

import com.jnasser.pokeapp.core.data.Pokemon
import com.jnasser.pokeapp.core.data.RoomResponse
import com.jnasser.pokeapp.core.data.toPokemonEntityList
import com.jnasser.pokeapp.core.data.toPokemonList
import com.jnasser.pokeapp.core.databaseManager.room.dao.PokemonDAO
import com.jnasser.pokeapp.core.databaseManager.room.entity.PokemonEntity
import com.jnasser.pokeapp.core.domain.LocalPokemonDataSource
import okio.IOException
import javax.inject.Inject

class PokemonRoomDataSource @Inject constructor(
    private val pokemonDAO: PokemonDAO
): LocalPokemonDataSource {

    override suspend fun insertPokemonList(pokemonList: List<Pokemon>): RoomResponse<List<Long>> {
        return try {
            val data = pokemonDAO.insertPokemonList(pokemonList.toPokemonEntityList())
            //Validamos si algun elemento no se logro insertar en la base local
            val errorList = data.filter { it == -1L }
            if(errorList.isNotEmpty()) RoomResponse.Error(Exception("Fallo al insertar elementos"))
            else RoomResponse.Success(data.filter { it != -1L })
        } catch (e: IOException) {
            RoomResponse.Error(e)
        }
    }

    override suspend fun getPokemonList(): RoomResponse<List<Pokemon>> {
        return try {
            val pokemons = pokemonDAO.getAllPokemons().toPokemonList()
            if(pokemons.isNotEmpty()) RoomResponse.Success(pokemons)
            else RoomResponse.EmptyList(pokemons)
        } catch (e: IOException) {
            RoomResponse.Error(e)
        }
    }

    override suspend fun searchPokemonByType(type: String): RoomResponse<List<Pokemon>> {
        return try {
            val pokemons = pokemonDAO.searchPokemonByType(type).toPokemonList()
            if(pokemons.isNotEmpty()) RoomResponse.Success(pokemons)
            else RoomResponse.EmptyList(pokemons)
        } catch (e: IOException) {
            RoomResponse.Error(e)
        }
    }
}