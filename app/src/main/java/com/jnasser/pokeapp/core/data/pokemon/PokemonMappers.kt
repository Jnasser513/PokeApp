package com.jnasser.pokeapp.core.data.pokemon

import com.jnasser.pokeapp.core.databaseManager.room.entity.PokemonEntity
import com.jnasser.pokeapp.pokemonList.data.PokemonResponse

fun PokemonEntity.toPokemon() = Pokemon(
    name = name,
    url = url,
    type = types
)

fun Pokemon.toPokemonEntity() = PokemonEntity(
    name = name,
    url = url,
    types = type
)

fun PokemonResponse.toPokemon() = Pokemon(
    name = name,
    url = url,
    type = types.map { it.name }
)

fun List<Pokemon>.toPokemonEntityList() = map { it.toPokemonEntity() }
fun List<PokemonEntity>.toPokemonList() = map { it.toPokemon() }
fun List<PokemonResponse>.toPokemonList() = map { it.toPokemon() }