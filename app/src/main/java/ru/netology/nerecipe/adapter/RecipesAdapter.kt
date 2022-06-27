package ru.netology.nerecipe.adapter


import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nerecipe.databinding.RecipesDetailsBinding
import ru.netology.nerecipe.dto.Recipe
import ru.netology.nerecipe.viewModel.RecipesFeederHelper
import ru.netology.nerecipe.viewModel.RecipesViewModel
import java.lang.Integer.min
import kotlin.math.max

class RecipesAdapter(val helper: RecipesFeederHelper, private val bindType: String) : ListAdapter<Recipe, RecipesAdapter.RecipeViewHolder>(RecipeDiffCallback) {

    private val helperCallback = RecipesHelperCallback()
    private val mTouchHelper = ItemTouchHelper(helperCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding =RecipesDetailsBinding.inflate(inflater,parent, false)

        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun attachRecyclerView(rw: RecyclerView){
        mTouchHelper.attachToRecyclerView(rw)
        val en = helperCallback.isLongPressDragEnabled
        val ch = en
    }

    inner class RecipeViewHolder(val binding: RecipesDetailsBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Recipe?) {
            if (item == null) return
            with(binding) {
                val catNumber = item.category
                recepieName.text = item.name
                authorName.text = item.author
                categoryText.text =
                    helper.getCategoryName(item.category) ?: "Error fetching the Category!"
                if (item.isFavourite) imageLikeShow.isVisible = true

                if (bindType == RECIPES_ADAPTER) { // if we show recipes scree
                    recipeCardContainer.setOnClickListener {
                        helper.onRecipeClicked(item)
                    }
                } else if (bindType == FAVOURITE_ADAPTER) { // if we show favourites screen
                    recipeCardContainer.setOnClickListener {
                        helper.onFavouriteClicked(item)
                    }
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

    companion object {
        const val RECIPES_ADAPTER = ".recipes"
        const val FAVOURITE_ADAPTER = ".favourite"
    }


    inner class RecipesHelperCallback: ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){

        var dragFrom: Int = -1
        var dragTo: Int = -1

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            val fromPosition = viewHolder.bindingAdapterPosition
            val toPosition = target.bindingAdapterPosition

            val adapter: RecipesAdapter = recyclerView.adapter as RecipesAdapter
            adapter.notifyItemMoved(fromPosition, toPosition)

            if (dragFrom == -1) dragFrom = fromPosition
            dragTo = toPosition

            return true
        }


        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)

            if (dragFrom == -1 || dragTo == -1 || dragFrom == dragTo) return

            val adapter: RecipesAdapter = recyclerView.adapter as RecipesAdapter

            val oldRecipe= adapter.getItem(dragFrom) ?: return
            val targetRecipe = adapter.getItem(dragTo) ?: return

            val list = adapter.currentList

            helper.updateRepoWithNewListFromTo(list, dragFrom, dragTo)

            dragFrom = -1; dragTo = -1;
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

        }
    }


}

