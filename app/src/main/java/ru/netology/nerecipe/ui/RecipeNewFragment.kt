package ru.netology.nerecipe.ui

import android.app.Activity
import android.app.ProgressDialog.show
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.doOnDetach
import androidx.core.view.doOnNextLayout
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.setFragmentResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this){
            goBackWithDialog()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.recipe_new_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.recipe_new_options_save -> {
                with(binding!!){
                    val curEditRecipe = viewModel.getEditedRecipe() ?: return false
                    val stepsList = viewModel.stepsFilteredData.value?.filter { it.recipeId == curEditRecipe.id }

                    if (recipeName.text.toString().isNullOrBlank() || authorName.text.toString().isNullOrBlank() || stepsList?.size == 0){
                        Toast.makeText(context, "Recipe name and Author name, or Steps list can not be empty!", Toast.LENGTH_SHORT)
                            .also { it.setGravity(Gravity.CENTER_VERTICAL, Gravity.AXIS_X_SHIFT, Gravity.AXIS_Y_SHIFT) }
                            .show()
                        return true
                    }

                    val selectedSpinner = viewModel.selectedSpinner
                    val catId = viewModel.getCatIdbyName(selectedSpinner) ?: 1L
                    val recipeSave = viewModel.getEditedRecipe()?.copy(name = recipeName.text.toString(),
                        author = authorName.text.toString(), category = catId,
                        isFavourite = imageFavourite.isChecked) ?: return false

                    authorName.requestFocus() // Saving the last edited step in any case with focusChange event in StepsAdapter

                    viewModel.onSaveEditedRecipe(recipeSave)
                }
                if (viewModel.isNewRecipe)
                    requireActivity().setTitle("NeRecipe")
                else
                    requireActivity().setTitle(" Recipe: " + viewModel.getEditedRecipe()?.name)

                val resultBundle = Bundle(1)
                val content = RESULT_VALUE
                resultBundle.putString(RESULT_KEY, content)
                setFragmentResult(REQUEST_KEY, resultBundle)

                parentFragmentManager.popBackStack()
                true
            }

            R.id.recipe_new_options_discard -> {
                goBackWithDialog()
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
        var selectedSpinner = viewModel.getCategoryName(recipe.category) ?: "No category set"

        if (viewModel.tempRecipe == null) {
            binding?.recipeName?.setText(recipe.name)
            binding?.authorName?.setText(recipe.author)
            binding?.imageFavourite?.isChecked = recipe.isFavourite
            viewModel.tempRecipe = recipe.copy()
        } else {
            val tr = viewModel.tempRecipe
            binding?.recipeName?.setText(tr?.name)
            binding?.authorName?.setText(tr?.author)
            binding?.imageFavourite?.isChecked = tr!!.isFavourite
            selectedSpinner = viewModel.getCategoryName(tr.category) ?: "No category set"
        }

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
        val adapter = StepsAdapter(viewModel, StepsAdapter.EDIT_ADAPTER)
        binding?.stepsListNew?.adapter = adapter

        val rw = binding?.stepsListNew ?: return binding?.root
        adapter.attachRecyclerView(rw)

        viewModel.stepsFilteredData.observe(viewLifecycleOwner) { steps ->
            adapter.submitList(steps.filter { it.recipeId == recipe.id })
        }

        // Button to add new step
        binding?.newStepButton?.setOnClickListener {
            viewModel.addNewEditedStep()
        }

        viewModel.navigateToNewStepEdit.observe(viewLifecycleOwner) {
            if (viewModel.getEditedStep() == null) return@observe
            parentFragmentManager.commit {
                addToBackStack(null)
                replace(R.id.app_fragment_container, StepNewFragment())
            }
        }

        binding?.authorName?.doOnTextChanged { text, start, before, count ->
            val selectedSpinner = viewModel.selectedSpinner
            val catId = viewModel.getCatIdbyName(selectedSpinner) ?: 1L
          viewModel.tempRecipe = viewModel.tempRecipe?.copy(author = text.toString(), category = catId) ?: Recipe(NEW_ITEM_ID, author = text.toString(), name = "", category = catId, isFavourite = false )
        }
        binding?.recipeName?.doOnTextChanged { text, start, before, count ->
            val selectedSpinner = viewModel.selectedSpinner
            val catId = viewModel.getCatIdbyName(selectedSpinner) ?: 1L
            viewModel.tempRecipe = viewModel.tempRecipe?.copy(name = text.toString(), category = catId) ?: Recipe(NEW_ITEM_ID, name = text.toString(), author = "", category = catId, isFavourite = false )
        }

        return binding?.root
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        viewModel.selectedSpinner = parent?.getItemAtPosition(pos) as String
        val selectedSpinner = viewModel.selectedSpinner
        val catId = viewModel.getCatIdbyName(selectedSpinner) ?: 1L
        viewModel.tempRecipe = viewModel.tempRecipe?.copy(category = catId) ?: Recipe(NEW_ITEM_ID, name = "", author = "", category = catId, isFavourite = false)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }


    private fun setTitlebarNameOnBack() {
        if (viewModel.isNewRecipe)
            requireActivity().setTitle("NeRecipe")
        else
            requireActivity().setTitle(" Recipe: " + viewModel.getEditedRecipe()?.name)
    }


    private fun goBackWithDialog() {
        val nameIsSame = viewModel.getEditedRecipe()?.name?.equals(binding?.recipeName?.text.toString()) ?: false
        val authorIsSame = viewModel.getEditedRecipe()?.author?.equals(binding?.authorName?.text.toString()) ?: false
        val selectedSpinner = viewModel.selectedSpinner
        val catId = viewModel.getCatIdbyName(selectedSpinner) ?: 1L
        val catChanged = viewModel.getEditedRecipe()?.category != catId
        val recipeDiscard = viewModel.getEditedRecipe()
        //val addedSteps = viewModel.stepIdsList.size > 0

        if (!nameIsSame || !authorIsSame || catChanged ) {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage("Are you sure to discard the changes?")
                .setNegativeButton("No, stay here"){ dialog, which ->

                }.setPositiveButton("Yes, discard."){ dialog, which ->
                    //viewModel.deleteUnsavedSteps()
                    if ( viewModel.isNewRecipe && recipeDiscard != null) viewModel.deleteRecipe(recipeDiscard)
                    setTitlebarNameOnBack()
                    viewModel.tempRecipe = null
                    parentFragmentManager.popBackStack()
                }.show()
        } else {
            //viewModel.deleteUnsavedSteps()
            if ( viewModel.isNewRecipe && recipeDiscard != null) viewModel.deleteRecipe(recipeDiscard)
            setTitlebarNameOnBack()
            viewModel.tempRecipe = null
            parentFragmentManager.popBackStack()
        }
    }


    companion object {
        const val REQUEST_KEY = ".edit fragment"
        const val RESULT_KEY = ".EDIT RESULT"
        const val RESULT_VALUE = ".EDITED OK"
    }
}