package com.jnasser.pokeapp.core.di

import com.jnasser.pokeapp.core.databaseManager.room.dao.PokemonDAO
import com.jnasser.pokeapp.core.databaseManager.room.datasource.PokemonRoomDataSource
import com.jnasser.pokeapp.core.domain.LocalPokemonDataSource
import com.jnasser.pokeapp.core.domain.RemotePokemonDataSource
import com.jnasser.pokeapp.core.requestManager.APIServices
import com.jnasser.pokeapp.core.requestManager.datasources.PokemonRetrofitDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Provides
    @Singleton
    fun providePokemonRoomDataSource(@IoDispatcher iosDispatcher: CoroutineDispatcher, pokemonDao: PokemonDAO): LocalPokemonDataSource {
        return PokemonRoomDataSource(iosDispatcher, pokemonDao)
    }

    @Provides
    @Singleton
    fun providePokemonRetrofitDataSource(service: APIServices): RemotePokemonDataSource {
        return PokemonRetrofitDataSource(service)
    }
}