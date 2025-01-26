package com.jnasser.pokeapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jnasser.pokeapp.core.presentation.PermissionHelper
import com.jnasser.pokeapp.core.utils.extensions.showToast
import com.jnasser.pokeapp.databinding.ActivityMainBinding
import com.jnasser.pokeapp.pokemonList.presentation.backgroundServices.UpdatePokemonListService
import com.jnasser.pokeapp.pokemonList.presentation.worker.UpdatePokemonListWorker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val notificationPermission = PermissionHelper(
        this@MainActivity,
        Manifest.permission.POST_NOTIFICATIONS,
        onGranted = {
            startUpdateWorker()
        },
        onDenied = {
            openSettingDevice()
        },
        onShowRationale = { permissionHelper ->
            showRationaleDialog(permissionHelper)
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.fragmentContainer) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onStart() {
        super.onStart()

        if (Build.VERSION.SDK_INT >= 33 && !isNotificationPermissionGranted()) notificationPermission.requestPermission()
        else startUpdateWorker()
    }

    private fun startUpdateWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<UpdatePokemonListWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager
            .getInstance(applicationContext)
            .beginUniqueWork("update_worker", ExistingWorkPolicy.KEEP, request)
            .enqueue()
    }

    private fun startUpdatePokemonListService() {
        if (!UpdatePokemonListService.isServiceActive) {
            startService(
                UpdatePokemonListService.createStartIntent(
                    context = this@MainActivity
                )
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun isNotificationPermissionGranted() =
        checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

    private fun openSettingDevice() {
        this@MainActivity.showToast(getString(R.string.this_permission_is_necessary))
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }

    private fun showRationaleDialog(permissionHelper: PermissionHelper) {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.necessary_permission))
            .setMessage(getString(R.string.this_permission_is_necessary))
            .setPositiveButton(getString(R.string.positive_button_text)) { _, _ ->
                permissionHelper.requestPermission()
            }
            .setNegativeButton(getString(R.string.negative_button_text), null)
            .setCancelable(false)
            .create()
            .show()
    }
}