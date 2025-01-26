package com.jnasser.pokeapp.pokemonList.presentation.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.getSystemService
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.jnasser.pokeapp.MainActivity
import com.jnasser.pokeapp.R
import com.jnasser.pokeapp.core.data.RoomResponse
import com.jnasser.pokeapp.core.data.utils.NotificationUtils
import com.jnasser.pokeapp.core.usecases.GetPokemonQuantityUseCase
import com.jnasser.pokeapp.pokemonList.data.UpdatePokemonListStatus
import com.jnasser.pokeapp.pokemonList.presentation.backgroundServices.UpdatePokemonListManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@HiltWorker
class UpdatePokemonListWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val updatePokemonListManager: UpdatePokemonListManager,
    private val getPokemonQuantityUseCase: GetPokemonQuantityUseCase
) : CoroutineWorker(context, params) {

    private var workerScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // job para timer de 30 segundos
    private var timerJob: Job? = null

    private val notificationManager by lazy {
        applicationContext.getSystemService<NotificationManager>()!!
    }

    private val baseNotification by lazy {
        NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_pokeball)
            .setContentTitle(context.getString(R.string.app_title))
    }

    private var maxPokemonQuantity: Int = 0

    override suspend fun doWork(): Result = coroutineScope {
        return@coroutineScope try {
            val result = start()
            if(result) Result.success()
            else Result.failure()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private suspend fun start(): Boolean {
        createNotificationChannel()

        val activityIntent = Intent(applicationContext, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        val pendingIntent = TaskStackBuilder.create(applicationContext).run {
            addNextIntentWithParentStack(activityIntent)
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
        }
        val notification =
            baseNotification.setContentText("Actualizando...")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOngoing(true)
                .build()

        notificationManager.notify(NotificationUtils.UPDATE_POKEMON_NOTIFICATION_ID, notification)

        return startTimer()
    }

    private suspend fun stop() {
        timerJob?.cancel()
        timerJob = null

        //Reiniciamos el scope para evitar fugas de memoria y cancelamos cualquier actividad que ocurre en el scope
        workerScope.cancel()
        workerScope = CoroutineScope(SupervisorJob())
    }

    private suspend fun startTimer(): Boolean {
        var isFinished: Boolean = false
        if (timerJob == null) {
            timerJob = workerScope.launch {
                while (true) {
                    val resultInsertPokemonList =
                        updatePokemonListManager.fetchAndInsertPokemonList()

                    when (resultInsertPokemonList) {
                        is UpdatePokemonListStatus.Continue -> {
                            maxPokemonQuantity = resultInsertPokemonList.count
                            updateNotification(true)
                        }

                        UpdatePokemonListStatus.Error -> {
                            isFinished = true
                            updateNotification(false)
                        }

                        UpdatePokemonListStatus.Stop -> {
                            isFinished = true
                            stop()
                        }
                    }

                    delay(DELAY_TIMER)
                }
            }
        }

        return isFinished
    }

    private suspend fun updateNotification(status: Boolean) {
        val pokemonQuantityResponse = getPokemonQuantityUseCase.invoke()
        var pokemonQuantityInserted = ""

        if (pokemonQuantityResponse is RoomResponse.Success) pokemonQuantityInserted =
            pokemonQuantityResponse.data.toString()
        else pokemonQuantityInserted = "No se logro obtener los pokemon insertados"

        val notification = baseNotification.setContentText(
                if (status) "Descargados: $pokemonQuantityInserted / $maxPokemonQuantity"
                else "Fallo la descarga de pokemons, verifica la coneccion a internet"
            ).setProgress(maxPokemonQuantity, pokemonQuantityInserted.toIntOrNull() ?: 0, !status)
            .build()

        notificationManager.notify(NotificationUtils.UPDATE_POKEMON_NOTIFICATION_ID, notification)
    }

    private suspend fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                applicationContext.getString(R.string.update_pokemon_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "update_pokemon"

        private const val DELAY_TIMER = 30000L
    }
}