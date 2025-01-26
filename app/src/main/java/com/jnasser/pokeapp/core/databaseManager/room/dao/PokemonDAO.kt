package com.jnasser.pokeapp.core.databaseManager.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jnasser.pokeapp.core.databaseManager.room.entity.PokemonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemonList(pokemonList: List<PokemonEntity>): List<Long>

    //Usamos reactividad para actualizar la lista de pokemons en tiempo real
    @Query("SELECT * FROM pokemon_table")
    fun getAllPokemons(): Flow<List<PokemonEntity>>

    @Query("SELECT COUNT(*) FROM pokemon_table")
    suspend fun getPokemonQuantity(): Int
}