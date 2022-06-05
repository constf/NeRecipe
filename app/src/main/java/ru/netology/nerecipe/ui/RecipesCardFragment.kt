package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.button.MaterialButton
import ru.netology.nerecipe.R
import ru.netology.nerecipe.adapter.StepsAdapter
import ru.netology.nerecipe.databinding.FragmentRecipeCardBinding
import ru.netology.nerecipe.viewModel.RecipesViewModel

class RecipesCardFragment : Fragment() {
    private val viewModel: RecipesViewModel by activityViewModels<RecipesViewModel>()
    private var _binding: FragmentRecipeCardBinding? = null
    private val binding get() = _binding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val recipe = viewModel.showRecipe.value ?: return super.onCreateView(inflater, container, savedInstanceState)

        _binding = FragmentRecipeCardBinding.inflate(inflater, container, false)

        val adapter: StepsAdapter = StepsAdapter(viewModel, StepsAdapter.SHOW_ADAPTER)

        binding?.recipeName?.text = recipe.name
        binding?.authorName?.text = recipe.author
        binding?.categoryText?.text = viewModel.getCatNameId(recipe.category)
        binding?.imageFavourite?.isChecked = recipe.isFavourite

        binding?.stepsList?.adapter = adapter

        viewModel.stepsFilteredData.observe(viewLifecycleOwner) { steps ->
            adapter.submitList(steps.filter { it.recipeId == recipe.id })
        }

        binding?.imageMore?.setOnClickListener {
            PopupMenu(it.context, it).apply {
                inflate(R.menu.recipe_card_more)
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.recipe_edit -> {
                            true
                        }
                        R.id.recipe_remove -> {
                            viewModel.deleteRecipe(recipe)
                            parentFragmentManager.popBackStack()
                            true
                        }
                        else -> false
                    }
                }
            }.show()
        }

        binding?.imageFavourite?.setOnClickListener {
            val button = it as MaterialButton
            viewModel.setFavourite(recipe.id, button.isChecked)
        }

        return binding?.root
    }

}