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
    private var isEmptyState: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            Toast.makeText(context, getString(R.string.rec_feeder_string01), Toast.LENGTH_LONG).show()
            val exit = requireActivity().onBackPressedDispatcher.addCallback(){
                requireActivity().finish()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentRecipesFeederBinding.inflate(inflater, container, false)
        if (binding == null) return super.onCreateView(inflater, container, savedInstanceState)

        val adapter = RecipesAdapter(viewModel, RecipesAdapter.RECIPES_ADAPTER)

        binding?.recipesList?.adapter = adapter
        val rw = binding?.recipesList ?: return binding?.root
        adapter.attachRecyclerView(rw)

        viewModel.isFavouriteShow = false

        requireActivity().setTitle("NeRecipe")

        // set the filter string
        val filter = if (viewModel.recipeNamesFilter.value.isNullOrEmpty()) ""
                     else  viewModel.recipeNamesFilter.value
        binding?.recipeNameFilterEdit?.setText(filter)

        viewModel.catData.observe(viewLifecycleOwner){
            viewModel.initCategories()
        }

        // Submit the list of recipes for the RW with the account of possible applied filter
        viewModel.recData.observe(viewLifecycleOwner) { recipes ->

            if (recipes.size == 0)
                showEmptyState()
            else
                if (isEmptyState) hideEmptyState()

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
        binding?.addRecipeButton?.alpha = 0.90f
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

    private fun showEmptyState() {
        if (binding == null) return
        with(binding!!) {
            // Hide RW and filter edit field
            recipesList.visibility = View.GONE
            recipeNameFilterEdit.visibility = View.GONE

            // SHow Empty State pic and text
            emptyStatePicture.visibility = View.VISIBLE
            emptyStateText.visibility = View.VISIBLE
        }
        isEmptyState = true
    }

    private fun hideEmptyState() {
        if (binding == null) return
        with(binding!!) {
            // Show RW and filter edit field
            recipesList.visibility = View.VISIBLE
            recipeNameFilterEdit.visibility = View.VISIBLE

            // Hide Empty State pic and text
            emptyStatePicture.visibility = View.GONE
            emptyStateText.visibility = View.GONE
        }
        isEmptyState = false
    }


    companion object {
        const val TAG = "RecipesFeederFragment"
    }
}

