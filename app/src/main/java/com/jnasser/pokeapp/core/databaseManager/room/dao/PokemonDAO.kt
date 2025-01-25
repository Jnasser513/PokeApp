package com.jnasser.pokeapp.core.databaseManager.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jnasser.pokeapp.core.databaseManager.room.entity.PokemonEntity

@Dao
interface PokemonDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemonList(pokemonList: List<PokemonEntity>): List<Long>

    @Query("SELECT * FROM pokemon_table")
    suspend fun getAllPokemons(): List<PokemonEntity>

    @Query("SELECT * FROM pokemon_table WHERE types = :type")
    suspend fun searchPokemonByType(type: String): List<PokemonEntity>
}