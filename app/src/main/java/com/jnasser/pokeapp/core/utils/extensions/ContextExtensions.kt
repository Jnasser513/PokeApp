package com.jnasser.pokeapp.core.utils.extensions

import android.content.Context
import android.widget.Toast

fun Context.showToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()