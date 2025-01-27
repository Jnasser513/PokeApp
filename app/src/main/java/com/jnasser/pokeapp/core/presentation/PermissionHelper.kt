package com.jnasser.pokeapp.core.presentation

import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

class PermissionHelper(
    activity: ComponentActivity,
    private val permission: String,
    onGranted: () -> Unit,
    onDenied: () -> Unit,
    onShowRationale: (PermissionHelper) -> Unit
) {

    private val permissionLauncher =
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            when {
                isGranted -> onGranted()
                activity.shouldShowRequestPermissionRationale(permission) -> onShowRationale(this)
                else -> onDenied()
            }
        }

    fun requestPermission() = permissionLauncher.launch(permission)
}