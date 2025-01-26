package com.jnasser.pokeapp.pokemonList.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.jnasser.pokeapp.core.data.pokemon.Pokemon
import com.jnasser.pokeapp.core.presentation.UIStatus
import com.jnasser.pokeapp.core.utils.extensions.hideView
import com.jnasser.pokeapp.core.utils.extensions.showView
import com.jnasser.pokeapp.databinding.FragmentPokemonListBinding
import com.jnasser.pokeapp.pokemonList.presentation.utils.UIStatusHandler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PokemonListFragment: Fragment() {

    private var mBinding: FragmentPokemonListBinding? = null
    private val binding get() = mBinding!!

    private val viewModel: PokemonListViewModel by viewModels()

    private val uiStatusHandler by lazy {
        UIStatusHandler(requireContext())
    }

    private val pokemonListAdapter = PokemonListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentPokemonListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getPokemonList()

        setUpObservers()
        initView()
    }

    private fun initView() {
        setUpPokemonListRecyclerview()

        binding.searchInputEdit.addTextChangedListener { query ->
            pokemonListAdapter.filter.filter(query.toString())
        }
    }

    private fun setUpObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.statusGetPokemonList.collect { status ->
                handlePokemonListStatus(status)
            }
        }
    }

    private fun handlePokemonListStatus(status: UIStatus<List<Pokemon>>) {
        uiStatusHandler.handleUIStatus(
            progressBar = binding.progress,
            status = status,
            onSuccess = { data ->
                data?.let {
                    pokemonListAdapter.setData(it)
                    binding.emptyState.hideView()
                }
            },
            onEmptyList = { data ->
                data?.let {
                    pokemonListAdapter.setData(it)
                    binding.emptyState.showView()
                }
            })
    }

    private fun setUpPokemonListRecyclerview() {
        binding.recyclerview.apply {
            adapter = pokemonListAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
            itemAnimator = DefaultItemAnimator()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

}