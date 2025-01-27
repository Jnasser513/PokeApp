@file:OptIn(ExperimentalCoroutinesApi::class)

package com.jnasser.pokeapp.core.databaseManager.room.datasource

import com.jnasser.pokeapp.core.data.RoomResponse
import com.jnasser.pokeapp.core.data.pokemon.toPokemon
import com.jnasser.pokeapp.core.data.pokemon.toPokemonList
import com.jnasser.pokeapp.core.databaseManager.room.dao.PokemonDAO
import com.jnasser.pokeapp.core.databaseManager.room.entity.PokemonEntity
import com.jnasser.pokeapp.core.di.IoDispatcher
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okio.IOException
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PokemonRoomDataSourceTest {

    @MockK
    @IoDispatcher
    private lateinit var ioDispatcher: CoroutineDispatcher

    @MockK
    private lateinit var pokemonDAO: PokemonDAO

    @InjectMockKs
    private lateinit var pokemonRoomDataSource: PokemonRoomDataSource

    @Before
    fun setUp() {
        // Inicializamos todos los mocks creados arriba
        MockKAnnotations.init(this)
    }

    @Test
    fun `insertPokemonList should return RoomResponse Success when list were inserted in room db`() = runTest {
        // Given
        val fakeList = listOf(
            PokemonEntity(name = "Pikachu", url = ""),
            PokemonEntity(name = "Bulbasaur", url = ""),
            PokemonEntity(name = "Ivysaur", url = "")
        )
        // Cada vez que se insert en la lista devolvera una lista vacia, es decir que los pokemon se insertaron correctamente
        coEvery { pokemonDAO.insertPokemonList(fakeList) } returns emptyList()

        // When
        val result = pokemonRoomDataSource.insertPokemonList(fakeList.toPokemonList())

        // Then
        assertTrue(result is RoomResponse.Success)
        assertTrue((result as RoomResponse.Success).data.isEmpty())
        // Verificamos que la inserción en la base de datos fue llamada correctamente y solo 1 vez
        coVerify(exactly = 1) { pokemonDAO.insertPokemonList(fakeList) }
    }

    @Test
    fun `insertPokemonList should return RoomResponse Error when at least one insertion failed`() = runTest {
        // Given
        val fakeList = listOf(
            PokemonEntity(name = "Pikachu", url = ""),
            PokemonEntity(name = "Bulbasaur", url = ""),
            PokemonEntity(name = "Ivysaur", url = "")
        )
        // Cada vez que se insert en la lista devolvera un -1 en la lista, es decir que 1 dato fallo al insertarse en la base local
        coEvery { pokemonDAO.insertPokemonList(fakeList) } returns listOf(-1L)

        // When
        val result = pokemonRoomDataSource.insertPokemonList(fakeList.toPokemonList())

        // Then
        assertTrue(result is RoomResponse.Error)
    }

    @Test
    fun `insertPokemonList should return RoomResponse Error when catch an error`() = runTest {
        // Given
        val fakeList = listOf(
            PokemonEntity(name = "Pikachu", url = ""),
            PokemonEntity(name = "Bulbasaur", url = ""),
            PokemonEntity(name = "Ivysaur", url = "")
        )
        // Simulamos que el metodo para insertar el listado de pokemon lanza una excepcion
        coEvery { pokemonDAO.insertPokemonList(fakeList) } throws IOException()

        // When
        val result = pokemonRoomDataSource.insertPokemonList(fakeList.toPokemonList())

        // Then
        assertTrue(result is RoomResponse.Error)
    }

    @Test
    fun `getPokemonList should return RoomResponse Success with the list of pokemons from database`() = runTest {
        // Given
        val storedList = listOf(
            PokemonEntity(name = "Pikachu", url = ""),
            PokemonEntity(name = "Bulbasaur", url = ""),
            PokemonEntity(name = "Ivysaur", url = "")
        )

        // Simulamos que la base de datos devuelve una lista de Pokémon
        coEvery { pokemonDAO.getAllPokemons() } returns flow { emit(storedList) }

        // When
        val result = pokemonRoomDataSource.getPokemonList().toList() // recolectamos los valores emitidos por el Flow

        // Then
        assertTrue(result.isNotEmpty())  // Verificamos que el flujo emitió datos
        assertTrue(result[0] is RoomResponse.Success)  // Aseguramos que el resultado es de tipo Success
        assertTrue((result[0] as RoomResponse.Success).data == storedList.map { it.toPokemon() })  // Verificamos que los datos son correctos

        // Verificamos que la recuperación de la lista desde la base de datos fue llamada correctamente
        coVerify { pokemonDAO.getAllPokemons() }
    }

    @Test
    fun `getPokemonList should return EmptyList when no pokemons are stored in the database`() = runTest {
        // Given
        val emptyList: List<PokemonEntity> = emptyList()

        // Simulamos que la base de datos devuelve una lista vacía
        coEvery { pokemonDAO.getAllPokemons() } returns flow { emit(emptyList) }

        // When
        val result = pokemonRoomDataSource.getPokemonList().toList()  // recolectamos los valores emitidos por el Flow

        // Then
        assertTrue(result.isNotEmpty())  // Verificamos que el flujo emitió algo
        assertTrue(result[0] is RoomResponse.EmptyList)  // Aseguramos que el resultado es de tipo EmptyList
        assertTrue((result[0] as RoomResponse.EmptyList).data.isEmpty())  // Verificamos que la lista esté vacía

        // Verificamos que la recuperación de la lista desde la base de datos fue llamada correctamente
        coVerify { pokemonDAO.getAllPokemons() }
    }


    @Test
    fun `getPokemonList should return Error when there is an exception during retrieval`() = runTest {
        // Given
        val exception = IOException("Database error")

        // Simulamos que la base de datos lanza una excepción
        coEvery { pokemonDAO.getAllPokemons() } throws exception

        // When
        val result = pokemonRoomDataSource.getPokemonList().toList()  // recolectamos los valores emitidos por el Flow

        // Then
        assertTrue(result.isNotEmpty())  // Verificamos que el flujo emitió algo
        assertTrue(result[0] is RoomResponse.Error)  // Aseguramos que el resultado es de tipo Error
        assertTrue((result[0] as RoomResponse.Error).exception == exception)  // Verificamos que la excepción lanzada es la correcta

        // Verificamos que la recuperación de la lista desde la base de datos fue llamada correctamente
        coVerify { pokemonDAO.getAllPokemons() }
    }


}