package com.jnasser.pokeapp.pokemonList.presentation.backgroundServices

import com.jnasser.pokeapp.core.data.ApiResponse
import com.jnasser.pokeapp.core.data.RoomResponse
import com.jnasser.pokeapp.core.databaseManager.sharedPreference.PreferenceManager
import com.jnasser.pokeapp.core.di.IoDispatcher
import com.jnasser.pokeapp.core.requestManager.ApiConstants
import com.jnasser.pokeapp.core.usecases.GetRemotePokemonListUseCase
import com.jnasser.pokeapp.core.usecases.InsertPokemonListUseCase
import com.jnasser.pokeapp.pokemonList.data.PokemonResponse
import com.jnasser.pokeapp.pokemonList.data.UpdatePokemonListStatus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpdatePokemonListManager @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val getRemotePokemonListUseCase: GetRemotePokemonListUseCase,
    private val insertPokemonListUseCase: InsertPokemonListUseCase,
    private val preference: PreferenceManager
) {

    suspend fun fetchAndInsertPokemonList(): UpdatePokemonListStatus {
        val offset = preference.lastPokemonInserted
        return when(val response = getRemotePokemonListUseCase.invoke(offset, ApiConstants.PAGE_QUANTITY)) {
            is ApiResponse.EmptyList -> UpdatePokemonListStatus.Stop // Finalizamos el worker ya que no existen mas pokemon que insertar
            is ApiResponse.Error -> UpdatePokemonListStatus.Error
            is ApiResponse.Success -> {
                response.data?.results?.let { pokemonList ->
                    insertPokemonList(pokemonList)
                    UpdatePokemonListStatus.Continue(response.data.count)
                } ?: UpdatePokemonListStatus.Continue(response.data?.count ?: 0)
            }
        }
    }

    private suspend fun insertPokemonList(pokemonList: List<PokemonResponse>): Boolean {
        return withContext(ioDispatcher) {
            when(val response = insertPokemonListUseCase.invoke(pokemonList)) {
                is RoomResponse.EmptyList -> true
                is RoomResponse.Error -> false // Aqui podriamos notificar de error al insertar algun pokemon, usando response sabemos la cantidad de errores
                is RoomResponse.Success -> {
                    preference.lastPokemonInserted += ApiConstants.PAGE_QUANTITY
                    true
                }
            }
        }
    }

}