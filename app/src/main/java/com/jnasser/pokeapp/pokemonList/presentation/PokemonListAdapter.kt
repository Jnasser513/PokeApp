package com.jnasser.pokeapp.pokemonList.presentation

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.jnasser.pokeapp.BuildConfig
import com.jnasser.pokeapp.core.data.pokemon.Pokemon
import com.jnasser.pokeapp.core.utils.extensions.hideView
import com.jnasser.pokeapp.databinding.ItemPokemonBinding

class PokemonListAdapter : RecyclerView.Adapter<PokemonListAdapter.PokemonListViewHolder>(),
    Filterable {

    private val pokemonList: MutableList<Pokemon> = mutableListOf()
    private val listFiltered: MutableList<Pokemon> = mutableListOf()

    //DiffUtil para calcular diferencias entre listas
    inner class PokemonListDiffCallback(
        private val oldList: List<Pokemon>, private val newList: List<Pokemon>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].url == newList[newItemPosition].url
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }

    inner class PokemonListViewHolder(private val binding: ItemPokemonBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Pokemon) {
            binding.pokemon = item

            val pokemonPosition = extractPokemonIdFromUrl(item.url)
            Glide.with(binding.root.context)
                .load("${BuildConfig.IMG_BASE_URL}$pokemonPosition.png")
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.progress.hideView()
                        return false
                    }
                })
                .into(binding.pokemonImage)
        }

        private fun extractPokemonIdFromUrl(url: String): Int {
            val idPattern = "(\\d+)/$".toRegex()
            val matchResult = idPattern.find(url)
            return matchResult?.groupValues?.get(1)?.toInt() ?: 0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPokemonBinding.inflate(inflater, parent, false)
        return PokemonListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PokemonListViewHolder, position: Int) {
        val item = listFiltered[position]
        holder.bind(item)
    }

    override fun getItemCount() = listFiltered.size

    // Filtro para buscar Pokémon por nombre
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.trim()?.lowercase()
                val filteredList = if (query.isNullOrEmpty()) {
                    pokemonList // Si no hay filtro, muestra toda la lista
                } else {
                    pokemonList.filter {
                        it.name.lowercase().contains(query)
                    }
                }

                return FilterResults().apply { values = filteredList }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                listFiltered.clear()
                listFiltered.addAll(results?.values as List<Pokemon>)
                notifyDataSetChanged()
            }
        }
    }

    // Metodo para actualizar los datos
    fun setData(newList: List<Pokemon>) {
        val diffResult = DiffUtil.calculateDiff(PokemonListDiffCallback(listFiltered, newList))
        pokemonList.clear()
        pokemonList.addAll(newList)
        listFiltered.clear()
        listFiltered.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }
}
