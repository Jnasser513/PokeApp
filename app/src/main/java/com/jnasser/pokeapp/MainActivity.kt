package com.jnasser.pokeapp

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.jnasser.pokeapp.databinding.ActivityMainBinding
import com.jnasser.pokeapp.pokemonList.presentation.backgroundServices.UpdatePokemonListService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnStart.setOnClickListener {
            startUpdatePokemonListService()
        }
    }

    private fun startUpdatePokemonListService() {
        if(!UpdatePokemonListService.isServiceActive) {
            startService(
                UpdatePokemonListService.createStartIntent(
                    context = this@MainActivity
                )
            )
        }
    }
}