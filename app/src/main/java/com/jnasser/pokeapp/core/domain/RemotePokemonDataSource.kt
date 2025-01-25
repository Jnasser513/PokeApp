package com.jnasser.pokeapp.core.domain

import com.jnasser.pokeapp.core.data.ApiResponse
import com.jnasser.pokeapp.pokemonList.data.PokemonListResponse

interface RemotePokemonDataSource {

    suspend fun getPokemonList(offset: Int, limit: Int): ApiResponse<PokemonListResponse>
}