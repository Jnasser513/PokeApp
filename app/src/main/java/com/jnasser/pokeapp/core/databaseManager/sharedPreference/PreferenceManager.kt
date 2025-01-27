package com.jnasser.pokeapp.core.databaseManager.sharedPreference

import android.content.SharedPreferences
import javax.inject.Inject

class PreferenceManager @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    /*Mediante este valor podremos acceder al ultimo pokemon insertado,
    en caso que el worker falle, se actualizara la lista de pokemons desde el ultimo insertado*/

    var lastPokemonInserted: Int
        get() = sharedPreferences.getInt(KEY_LAST_POKEMON, 0)
        set(value) = sharedPreferences.edit().putInt(KEY_LAST_POKEMON, value).apply()

    companion object {
        const val SHARED_PREFERENCE_NAME = "pokemon_sp"
        const val KEY_LAST_POKEMON = "key_last_pokemon"
    }
}