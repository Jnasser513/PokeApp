package com.jnasser.pokeapp.core.databaseManager.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jnasser.pokeapp.core.databaseManager.room.dao.PokemonDAO
import com.jnasser.pokeapp.core.databaseManager.room.entity.PokemonEntity

@Database(
    version = 1,
    entities = [PokemonEntity::class]
)
abstract class PokemonDB: RoomDatabase() {

    abstract fun pokemonDao(): PokemonDAO

    companion object {
        @Volatile
        private var INSTANCE: PokemonDB? = null
        fun getDatabase(context: Context) = INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context,
                PokemonDB::class.java,
                "pokemon_db"
            ).fallbackToDestructiveMigration().build()

            INSTANCE = instance
            instance
        }
    }
}