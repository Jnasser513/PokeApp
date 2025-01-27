package com.jnasser.pokeapp.core.requestManager.datasources

import com.jnasser.pokeapp.core.data.ApiResponse
import com.jnasser.pokeapp.core.domain.RemotePokemonDataSource
import com.jnasser.pokeapp.core.requestManager.APIServices
import com.jnasser.pokeapp.pokemonList.data.PokemonListResponse
import okio.IOException

class PokemonRetrofitDataSource(
    private val service: APIServices
): RemotePokemonDataSource {

    override suspend fun getPokemonList(offset: Int, limit: Int): ApiResponse<PokemonListResponse> {
        return try {
            val call = service.getPokemonList(offset, limit)
            val response = call.body()
            // Validamos si la lista viene vacia para no insertar datos en base local
            if(!response?.results.isNullOrEmpty()) ApiResponse.Success(response)
            else ApiResponse.EmptyList(response)
        } catch (e: IOException) {
            ApiResponse.Error(e)
        }
    }
}