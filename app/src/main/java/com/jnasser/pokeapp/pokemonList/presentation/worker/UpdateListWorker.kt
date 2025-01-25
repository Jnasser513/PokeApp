package com.jnasser.pokeapp.pokemonList.presentation.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.jnasser.pokeapp.core.usecases.GetRemotePokemonListUseCase
import com.jnasser.pokeapp.core.usecases.InsertPokemonListUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope

@HiltWorker
class UpdateListWorker @AssistedInject constructor(
    private val updatePokemonListManager: UpdatePokemonListManager,
    @Assisted context: Context,
    @Assisted params: WorkerParameters
): CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = coroutineScope {
        return@coroutineScope try {
            val resultInsertPokemonList = updatePokemonListManager.fetchAndInsertPokemonList()

            if(resultInsertPokemonList) Result.success()
            else Result.failure()
        } catch (e: Exception) {
            Result.failure(workDataOf("error" to e.message))
        }
    }
}