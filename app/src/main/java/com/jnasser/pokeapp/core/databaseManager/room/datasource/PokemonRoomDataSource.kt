package com.jnasser.pokeapp.core.databaseManager.room.datasource

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.jnasser.pokeapp.core.data.pokemon.Pokemon
import com.jnasser.pokeapp.core.data.RoomResponse
import com.jnasser.pokeapp.core.data.pokemon.toPokemon
import com.jnasser.pokeapp.core.data.pokemon.toPokemonEntityList
import com.jnasser.pokeapp.core.data.pokemon.toPokemonList
import com.jnasser.pokeapp.core.databaseManager.room.dao.PokemonDAO
import com.jnasser.pokeapp.core.di.IoDispatcher
import com.jnasser.pokeapp.core.domain.LocalPokemonDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import okio.IOException

class PokemonRoomDataSource(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
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

    override suspend fun getPokemonList(): Flow<RoomResponse<List<Pokemon>>> = flow {
        try {
            // Observamos los cambios en la base de datos utilizando Flow y Room
            pokemonDAO.getAllPokemons()
                .map { pokemonEntities ->
                    // Convertimos PokemonEntity a Pokemon
                    pokemonEntities.map { it.toPokemon() }
                }
                .collect { pokemonList ->
                    // Emitimos la respuesta de la consulta según el estado
                    if (pokemonList.isNotEmpty()) {
                        emit(RoomResponse.Success(pokemonList))
                    } else {
                        emit(RoomResponse.EmptyList(pokemonList))
                    }
                }
        } catch (e: Exception) {
            // En caso de error, emitimos el error en el flujo
            emit(RoomResponse.Error<List<Pokemon>>(e))
        }
    }



    override suspend fun getPokemonQuantity(): RoomResponse<Int> {
        return try {
            val pokemonQuantity = pokemonDAO.getPokemonQuantity()
            RoomResponse.Success(pokemonQuantity)
        } catch (e: IOException) {
            RoomResponse.Error(e)
        }
    }
}