package ru.netology.nerecipe.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nerecipe.databinding.RecipesDetailsBinding
import ru.netology.nerecipe.dto.Recipe
import ru.netology.nerecipe.viewModel.RecipesFeederHelper
import ru.netology.nerecipe.viewModel.RecipesViewModel

class RecipesAdapter(val helper: RecipesFeederHelper) : ListAdapter<Recipe, RecipesAdapter.RecipeViewHolder>(RecipeDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding =RecipesDetailsBinding.inflate(inflater,parent, false)

        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RecipeViewHolder(private val binding: RecipesDetailsBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Recipe?) {
            if (item == null) return
            with (binding) {
                val catNumber = item.category
                recepieName.text = item.name
                authorName.text = item.author
                categoryText.text = helper.getCategoryName(item.category) ?: "Error fetching the Category!"
                recipeCardContainer.setOnClickListener {
                    helper.onRecipeClicked(item)
                }
            }
        }

    }

    private object RecipeDiffCallback:DiffUtil.ItemCallback<Recipe>(){
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem == newItem
        }

    }


}