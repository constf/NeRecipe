package ru.netology.nerecipe.ui

import android.app.Activity
import android.app.ProgressDialog.show
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import ru.netology.nerecipe.R
import ru.netology.nerecipe.adapter.StepsAdapter
import ru.netology.nerecipe.databinding.FragmentRecipeNewBinding
import ru.netology.nerecipe.dto.Recipe
import ru.netology.nerecipe.dto.RecipeStep
import ru.netology.nerecipe.viewModel.NEW_ITEM_ID
import ru.netology.nerecipe.viewModel.RecipesViewModel

const val IMAGE_PICK_KEY = 10001

class RecipeNewFragment :
    Fragment(), AdapterView.OnItemSelectedListener {

    private val viewModel: RecipesViewModel by activityViewModels<RecipesViewModel>()
    private var _binding: FragmentRecipeNewBinding? = null
    private val binding get() = _binding

    private var recipeEdited: Recipe? = null

    private var selectedSpinner: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this){
            if (viewModel.isNewRecipe)
                requireActivity().setTitle("NeRecipe")
            else
                requireActivity().setTitle(" Recipe: " + recipeEdited?.name)

            parentFragmentManager.popBackStack()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.recipe_new_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.recipe_new_options_save -> {
                with(binding!!){
                    if (recipeName.text.toString().isNullOrBlank() || authorName.text.toString().isNullOrBlank()){
                        Toast.makeText(context, "Recipe name and Author name can not be empty!", Toast.LENGTH_SHORT)
                            .also { it.setGravity(Gravity.CENTER_VERTICAL, Gravity.AXIS_X_SHIFT, Gravity.AXIS_Y_SHIFT) }
                            .show()
                        return true
                    }

                    val catId = viewModel.getCatIdbyName(selectedSpinner) ?: 0L
                    val recipe = recipeEdited?.copy(name = recipeName.text.toString(),
                        author = authorName.text.toString(), category = catId,
                        isFavourite = imageFavourite.isChecked) ?: return false

                    authorName.requestFocus() // Saving the last edited step in any case with focusChange event in StepsAdapter

                    viewModel.onSaveEditedRecipe(recipe)
                }
                if (viewModel.isNewRecipe)
                    requireActivity().setTitle("NeRecipe")
                else
                    requireActivity().setTitle(" Recipe: " + recipeEdited?.name)
                parentFragmentManager.popBackStack()
                true
            }

            R.id.recipe_new_options_discard -> {
                if (viewModel.isNewRecipe)
                    requireActivity().setTitle("NeRecipe")
                else
                    requireActivity().setTitle(" Recipe: " + recipeEdited?.name)
                parentFragmentManager.popBackStack()
                true
            }

            else -> super.onOptionsItemSelected(item)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecipeNewBinding.inflate(inflater, container, false)

        activity?.actionBar?.title = "New Recipe"


        // Initializing the View fileds
        val recipe = viewModel.getEditedRecipe() ?: return binding?.root
        recipeEdited = recipe

        binding?.recipeName?.setText(recipe.name)
        binding?.authorName?.setText(recipe.author)
        binding?.imageFavourite?.isChecked = recipe.isFavourite
        selectedSpinner = viewModel.getCategoryName(recipe.category) ?: "No category set"

        requireActivity().setTitle(
            if ( recipe.name.isBlank() || recipe.name.isEmpty() )
                " Creating new recipe"
            else
                " Editing: " + recipe.name
        )

        // Creating a drop down list for categories
        val spinner = binding?.categoryChoose
        val arrAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item)

        viewModel.catData.observe(viewLifecycleOwner) { catList ->
            arrAdapter.clear()
            arrAdapter.addAll(catList.map { it.name })

            arrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner?.adapter = arrAdapter
            spinner?.onItemSelectedListener = this

            val indx = arrAdapter.getPosition(selectedSpinner) // -1 if not found
            spinner?.setSelection(indx) // if -1, empty value
        }

        // List of steps for this recipe
        val adapter = StepsAdapter(viewModel, StepsAdapter.SHOW_ADAPTER)
        binding?.stepsListNew?.adapter = adapter

        viewModel.stepsFilteredData.observe(viewLifecycleOwner) { steps ->
            adapter.submitList(steps.filter { it.recipeId == recipe.id })
        }

        // Button to add new step
        binding?.newStepButton?.setOnClickListener {
            viewModel.addNewEditedStep()
        }

        viewModel.editStep.observe(viewLifecycleOwner) { editedStep ->
            if (editedStep == null) return@observe
            parentFragmentManager.commit {
                addToBackStack(null)
                replace(R.id.app_fragment_container, StepNewFragment())
            }
        }

        // Choose the picture from the Gallery
        viewModel.chooseThePicture.observe(viewLifecycleOwner) { step ->
            if (step == null) return@observe
            val intent = Intent().apply {
                action = Intent.ACTION_PICK
                type = "image/*"
            }
            startActivityForResult(intent, IMAGE_PICK_KEY, null)
        }

        return binding?.root
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        selectedSpinner = parent?.getItemAtPosition(pos) as String
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != IMAGE_PICK_KEY || resultCode != Activity.RESULT_OK) return
        viewModel.setEditedStepsPicture(data?.data)

        Log.d("onActivityResult-URI", data?.data.toString())
    }
}