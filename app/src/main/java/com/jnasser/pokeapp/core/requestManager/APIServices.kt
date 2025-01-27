package com.jnasser.pokeapp.core.requestManager

import com.jnasser.pokeapp.pokemonList.data.PokemonListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface APIServices {

    @GET(ApiConstants.POKEMON_LIST)
    suspend fun getPokemonList(
        @Query("offset") offset: Int, @Query("limit") limit: Int
    ): Response<PokemonListResponse>
}