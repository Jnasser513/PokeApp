package com.jnasser.pokeapp.core.data.pokemon

import com.jnasser.pokeapp.core.databaseManager.room.entity.PokemonEntity
import com.jnasser.pokeapp.pokemonList.data.PokemonResponse

fun PokemonEntity.toPokemon() = Pokemon(
    name = name,
    url = url
)

fun Pokemon.toPokemonEntity() = PokemonEntity(
    name = name,
    url = url
)

fun PokemonResponse.toPokemon() = Pokemon(
    name = name,
    url = url
)

fun List<Pokemon>.toPokemonEntityList() = map { it.toPokemonEntity() }
fun List<PokemonEntity>.toPokemonList() = map { it.toPokemon() }
fun List<PokemonResponse>.toPokemonList2() = map { it.toPokemon() }