package com.jnasser.pokeapp.pokemonList.data

import com.google.gson.annotations.SerializedName

data class PokemonListResponse(
    @SerializedName("count")
    val count: Int,
    @SerializedName("next")
    val next: String,
    @SerializedName("results")
    val results: List<PokemonResponse>
)
