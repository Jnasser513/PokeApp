package com.jnasser.pokeapp.pokemonList.presentation.backgroundServices

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.work.ListenableWorker.Result
import androidx.work.workDataOf
import com.jnasser.pokeapp.MainActivity
import com.jnasser.pokeapp.R
import com.jnasser.pokeapp.core.data.utils.NotificationUtils
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

@AndroidEntryPoint
class UpdatePokemonListService: Service() {

    @Inject lateinit var updatePokemonListManager: UpdatePokemonListManager

    private var serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // job para timer de 30 segundos
    private var timerJob: Job? = null

    private val notificationManager by lazy {
        getSystemService<NotificationManager>()
    }

    private val baseNotification by lazy {
        NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("Descargando lista de pokemons")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }

        // Si el sistema mata el servicio, este se reiniciara automaticamente
        return START_STICKY
    }

    private fun start() {
        if (!isServiceActive) {
            isServiceActive = true

            // Crear el canal de notificación solo si el nivel de API es >= 26
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel()
            }

            val notification = baseNotification
                .setContentText("Actualizando...")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Esto solo se usa en < API 26
                .setOngoing(true)
                .build()

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
        if(timerJob == null) {
            timerJob = serviceScope.launch {
                while(true) {
                    val resultInsertPokemonList = updatePokemonListManager.fetchAndInsertPokemonList()

                    when(resultInsertPokemonList) {
                        UpdatePokemonListStatus.Continue -> {}
                        UpdatePokemonListStatus.Error -> {
                            // Aqui podriamos informar de error en la insercion de datos
                        }
                        UpdatePokemonListStatus.Stop -> stop()
                    }

                    delay(DELAY_TIMER)
                }
            }
        }
    }

    private fun updateNotification() {
        val notification = baseNotification
            .build()
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.update_pokemon_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager?.createNotificationChannel(channel)
        }
    }

    companion object {
        var isServiceActive = false
        private const val CHANNEL_ID = "update_pokemon"

        private const val ACTION_START = "ACTION_START"
        private const val ACTION_STOP = "ACTION_STOP"

        private const val DELAY_TIMER = 30000L

        fun createStartIntent(context: Context): Intent {
            Log.d("STARTSERVICEDONE 2", isServiceActive.toString())
            return Intent(context, UpdatePokemonListService::class.java).apply {
                action = ACTION_START
            }
        }

        fun createStopIntent(context: Context): Intent {
            return Intent(context, UpdatePokemonListService::class.java).apply {
                action = ACTION_STOP
            }
        }
    }
}