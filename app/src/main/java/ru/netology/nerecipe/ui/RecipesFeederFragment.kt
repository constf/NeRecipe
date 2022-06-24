package ru.netology.nerecipe.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.google.android.material.snackbar.Snackbar
import ru.netology.nerecipe.R
import ru.netology.nerecipe.adapter.RecipesAdapter
import ru.netology.nerecipe.databinding.FragmentRecipesFeederBinding
import ru.netology.nerecipe.viewModel.RecipesViewModel

class RecipesFeederFragment : Fragment() {

    private val viewModel: RecipesViewModel by activityViewModels<RecipesViewModel>()
    private var _binding: FragmentRecipesFeederBinding? = null
    private val binding get() = _binding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            Toast.makeText(context, "Press BACK once more to exit the App!", Toast.LENGTH_LONG).show()
            val exit = requireActivity().onBackPressedDispatcher.addCallback(){
                requireActivity().finish()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentRecipesFeederBinding.inflate(inflater, container, false)

        val adapter = RecipesAdapter(viewModel, RecipesAdapter.RECIPES_ADAPTER)
        binding?.recipesList?.adapter = adapter

        viewModel.isFavouriteShow = false

        // set the filter string
        val filter = if (viewModel.recipeNamesFilter.value.isNullOrEmpty()) ""
                     else  viewModel.recipeNamesFilter.value
        binding?.recipeNameFilterEdit?.setText(filter)

        viewModel.catData.observe(viewLifecycleOwner){
            viewModel.initCategories()
        }

        // Submit the list of recipes for the RW with the account of possible applied filter
        viewModel.recData.observe(viewLifecycleOwner) { recipes ->
            val filter = viewModel.recipeNamesFilter.value
            if ( filter.isNullOrEmpty() ){
                adapter.submitList(recipes) // No filter applied
            } else {
                adapter.submitList(recipes.filter { rec ->
                    rec.name.contains(filter, true) // Apply a filer for recipes names
                })
            }
        }

        // Update the displayed by RW list of Recipes with the search filter entered by the user
        viewModel.recipeNamesFilter.observe(viewLifecycleOwner) { filter ->
            val recData = viewModel.recData.value
            if (filter.isNullOrEmpty()) {
                adapter.submitList(recData)
            } else{
                adapter.submitList(recData?.filter{ rec ->
                    rec.name.contains(filter, true)
                })
            }
        }

        // viewModel.createInitialDataSet() initial creation of recipes))) now we have enough in our db!


        // Callback to monitor the filter text input by user
        binding?.recipeNameFilterEdit?.doOnTextChanged { text, start, before, count ->
            val newText = text.toString().trim()
            // if  (newText == null ) return@doOnTextChanged
            viewModel.setRecipeNamesFilter(newText)
            Log.d("doOnTextChanged", newText)
        }

        // Show recipe card fragment
        viewModel.showRecipe.observe(viewLifecycleOwner) { recipe ->
            if (recipe == null) return@observe
            parentFragmentManager.commit {
                addToBackStack(null)
                replace(R.id.app_fragment_container, RecipesCardFragment())
            }
        }

        // Show Recipe New Fragment
        binding?.addRecipeButton?.setOnClickListener {
            viewModel.addNewRecipe()

        }
        viewModel.editRecipe.observe(viewLifecycleOwner) { recipe ->
            if (recipe == null) return@observe
            parentFragmentManager.commit {
                addToBackStack("RecipesFeeder")
                replace(R.id.app_fragment_container, RecipeNewFragment())
            }
        }

        if (viewModel.tempRecipe != null && viewModel.editRecipe.value != null){
            parentFragmentManager.commit {
                addToBackStack("RecipesFeeder")
                replace(R.id.app_fragment_container, RecipeNewFragment())
            }
        }

        return binding?.root
    }

    companion object {
        const val TAG = "RecipesFeederFragment"
    }
}