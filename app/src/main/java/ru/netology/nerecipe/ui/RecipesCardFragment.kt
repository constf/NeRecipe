package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.netology.nerecipe.R
import ru.netology.nerecipe.adapter.StepsAdapter
import ru.netology.nerecipe.databinding.FragmentRecipeCardBinding
import ru.netology.nerecipe.viewModel.RecipesViewModel

class RecipesCardFragment : Fragment() {
    private val viewModel: RecipesViewModel by activityViewModels()
    private var _binding: FragmentRecipeCardBinding? = null
    private val binding get() = _binding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this){
            findNavController().popBackStack()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val getRecipe = viewModel.showRecipe.value ?: return super.onCreateView(inflater, container, savedInstanceState)
        val recipe = viewModel.getRecipeById(getRecipe.id)

        _binding = FragmentRecipeCardBinding.inflate(inflater, container, false)

        val adapter = StepsAdapter(viewModel, StepsAdapter.SHOW_ADAPTER)

        binding?.recipeName?.text = recipe.name
        binding?.authorName?.text = recipe.author
        binding?.categoryText?.text = viewModel.getCatNameId(recipe.category)
        binding?.imageFavourite?.isChecked = recipe.isFavourite

        binding?.stepsList?.adapter = adapter

        viewModel.stepsFilteredData.observe(viewLifecycleOwner) { steps ->
            adapter.submitList(steps.filter { it.recipeId == recipe.id })
        }

        if (!viewModel.isFavouriteShow) {
            binding?.imageMore?.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.recipe_card_more)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.recipe_edit -> {
                                viewModel.onEditRecipe(recipe)
                                true
                            }
                            R.id.recipe_remove -> {
                                MaterialAlertDialogBuilder(it.context)
                                    .setMessage(getString(R.string.recipe_card_string01))
                                    .setNegativeButton(getString(R.string.recipe_card_string02)) { dialog, which -> }
                                    .setPositiveButton(getString(R.string.recipe_card_string03)) { dialog, which ->
                                        viewModel.deleteRecipe(recipe)
                                        findNavController().popBackStack()
                                    }.show()
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }
        } else {
            binding?.imageMore?.isVisible = false
        }

        viewModel.editRecipe.observe(viewLifecycleOwner) { recipe ->
            if (recipe == null) return@observe
            findNavController().navigate(R.id.action_recipesCardFragment_to_recipeNewFragment)
        }

//        viewModel.navigateToRecipeEditScreen.observe(viewLifecycleOwner) {
//            parentFragmentManager.commit {
//                addToBackStack(null)
//                replace(R.id.app_fragment_container, RecipeEditFragment())
//            }
//        }

        binding?.imageFavourite?.setOnClickListener {
            val button = it as MaterialButton
            viewModel.setFavourite(recipe.id, button.isChecked)
        }

        setFragmentResultListener(RecipeNewFragment.REQUEST_KEY){ requestKey, bundle ->
            if (requestKey != RecipeNewFragment.REQUEST_KEY) return@setFragmentResultListener
            val result = bundle.getString(RecipeNewFragment.RESULT_KEY) ?: return@setFragmentResultListener
            if (result != RecipeNewFragment.RESULT_VALUE) return@setFragmentResultListener

            val updateRecipe = viewModel.getRecipeById(getRecipe.id)
            binding?.recipeName?.text = updateRecipe.name
            binding?.authorName?.text = updateRecipe.author
            binding?.categoryText?.text = viewModel.getCatNameId(updateRecipe.category)
            binding?.imageFavourite?.isChecked = updateRecipe.isFavourite
        }


        return binding?.root
    }

}