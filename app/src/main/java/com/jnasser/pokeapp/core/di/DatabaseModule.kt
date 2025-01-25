package com.jnasser.pokeapp.core.di

import android.content.Context
import androidx.work.impl.model.Preference
import com.jnasser.pokeapp.core.databaseManager.room.PokemonDB
import com.jnasser.pokeapp.core.databaseManager.room.dao.PokemonDAO
import com.jnasser.pokeapp.core.databaseManager.room.datasource.PokemonRoomDataSource
import com.jnasser.pokeapp.core.databaseManager.sharedPreference.PreferenceManager
import com.jnasser.pokeapp.core.domain.LocalPokemonDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providePokemonDatabase(@ApplicationContext context: Context) =
        PokemonDB.getDatabase(context)

    @Provides
    @Singleton
    fun providePokemonDao(database: PokemonDB) = database.pokemonDao()

    @Singleton
    @Provides
    fun provideSharedPreference(@ApplicationContext context: Context) =
        context.getSharedPreferences(PreferenceManager.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)


}