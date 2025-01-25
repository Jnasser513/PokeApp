package com.jnasser.pokeapp.core.data

import com.jnasser.pokeapp.core.databaseManager.room.entity.PokemonEntity

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

fun List<Pokemon>.toPokemonEntityList() = map { it.toPokemonEntity() }
fun List<PokemonEntity>.toPokemonList() = map { it.toPokemon() }