package com.jnasser.pokeapp.pokemonList.presentation.backgroundServices

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.getSystemService
import androidx.work.ListenableWorker.Result
import androidx.work.workDataOf
import com.jnasser.pokeapp.MainActivity
import com.jnasser.pokeapp.R
import com.jnasser.pokeapp.core.data.RoomResponse
import com.jnasser.pokeapp.core.data.utils.NotificationUtils
import com.jnasser.pokeapp.core.usecases.GetPokemonQuantityUseCase
import com.jnasser.pokeapp.pokemonList.data.UpdatePokemonListStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

// Se decidio usar un foreground service y no un worker,
// debido a que los worker estan diseñados para tareas recurrentes mayores a 15 minutos,
// en este caso se requiere una actualizacion de datos cada 30 segundos,
// por lo cual es mas recomendable usar un foreground service

@AndroidEntryPoint
class UpdatePokemonListService : Service() {

    @Inject
    lateinit var updatePokemonListManager: UpdatePokemonListManager

    @Inject
    lateinit var getPokemonQuantityUseCase: GetPokemonQuantityUseCase

    private var serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // job para timer de 30 segundos
    private var timerJob: Job? = null

    private val notificationManager by lazy {
        getSystemService<NotificationManager>()!!
    }

    private val baseNotification by lazy {
        NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_pokeball).setContentTitle(getString(R.string.app_title))
    }

    private var maxPokemonQuantity: Int = 0

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }

        // Si el sistema mata el servicio, este se reiniciara automaticamente
        return START_STICKY
    }

    private fun start() {
        if (!isServiceActive) {
            isServiceActive = true

            createNotificationChannel()

            val activityIntent = Intent(applicationContext, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            val pendingIntent = TaskStackBuilder.create(applicationContext).run {
                addNextIntentWithParentStack(activityIntent)
                getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
            }
            val notification =
                baseNotification.setContentText("Actualizando...").setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT).setOngoing(true).build()

            startForeground(NotificationUtils.UPDATE_POKEMON_NOTIFICATION_ID, notification)
            startTimer()
        }

    }

    private fun stop() {
        stopSelf()
        isServiceActive = false

        timerJob?.cancel()
        timerJob = null

        //Reiniciamos el scope para evitar fugas de memoria y cancelamos cualquier actividad que ocurre en el scope
        serviceScope.cancel()
        serviceScope = CoroutineScope(SupervisorJob())
    }

    private fun startTimer() {
        if (timerJob == null) {
            timerJob = serviceScope.launch {
                while (true) {
                    val resultInsertPokemonList =
                        updatePokemonListManager.fetchAndInsertPokemonList()

                    when (resultInsertPokemonList) {
                        is UpdatePokemonListStatus.Continue -> {
                            maxPokemonQuantity = resultInsertPokemonList.count
                            updateNotification(true)
                        }

                        UpdatePokemonListStatus.Error -> {
                            updateNotification(false)
                        }

                        UpdatePokemonListStatus.Stop -> stop()
                    }

                    delay(DELAY_TIMER)
                }
            }
        }
    }

    private suspend fun updateNotification(status: Boolean) {
        val pokemonQuantityResponse = getPokemonQuantityUseCase.invoke()
        var pokemonQuantityInserted = ""

        if (pokemonQuantityResponse is RoomResponse.Success) pokemonQuantityInserted =
            pokemonQuantityResponse.data.toString()
        else pokemonQuantityInserted = "No se logro obtener los pokemon insertados"

        val notification = baseNotification
            .setContentText(
                if (status) "Descargados: $pokemonQuantityInserted / $maxPokemonQuantity"
                else "Fallo la descarga de pokemons, verifica la coneccion a internet"
            )
            .setProgress(maxPokemonQuantity, pokemonQuantityInserted.toIntOrNull() ?: 0, !status)
            .build()

        notificationManager.notify(NotificationUtils.UPDATE_POKEMON_NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.update_pokemon_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        var isServiceActive = false
        private const val CHANNEL_ID = "update_pokemon"

        private const val ACTION_START = "ACTION_START"
        private const val ACTION_STOP = "ACTION_STOP"

        private const val DELAY_TIMER = 30000L

        // Intent para iniciar el service
        fun createStartIntent(context: Context): Intent {
            return Intent(context, UpdatePokemonListService::class.java).apply {
                action = ACTION_START
            }
        }

        // Intent para finalizar el service, en este caso se finaliza dentro del service
        fun createStopIntent(context: Context): Intent {
            return Intent(context, UpdatePokemonListService::class.java).apply {
                action = ACTION_STOP
            }
        }
    }
}