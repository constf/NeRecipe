package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import ru.netology.nerecipe.R
import ru.netology.nerecipe.adapter.StepsAdapter
import ru.netology.nerecipe.databinding.FragmentRecipeNewBinding
import ru.netology.nerecipe.viewModel.RecipesViewModel

class RecipeNewFragment :
    Fragment(), AdapterView.OnItemSelectedListener {

    private val viewModel: RecipesViewModel by activityViewModels<RecipesViewModel>()
    private var _binding: FragmentRecipeNewBinding? = null
    private val binding get() = _binding

    private var selectedSpinner: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecipeNewBinding.inflate(inflater, container, false)

        // Initiate TextEdit fields


        // Creating a drop down list for categories
        val spinner = binding?.categoryChoose
        val arrAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item)

        viewModel.catData.observe(viewLifecycleOwner) { catList ->
            arrAdapter.clear()
            arrAdapter.addAll(catList.map { it.name })

            arrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner?.adapter = arrAdapter
            spinner?.onItemSelectedListener = this
        }

        // List of steps for this recipe
        val adapter = StepsAdapter(viewModel, StepsAdapter.EDIT_ADAPTER)
        binding?.stepsListNew?.adapter = adapter

        viewModel.editedStepsList.observe(viewLifecycleOwner) { newSteps ->
            adapter.submitList(newSteps)
        }

        // Button to add new step
        binding?.newStepButton?.setOnClickListener {
            viewModel.addNewEditedStep()
        }


        return binding?.root
    }

    override fun onDestroyView() {
        viewModel.clearEditedSteps()
        super.onDestroyView()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        selectedSpinner = parent?.getItemAtPosition(pos) as String
        binding?.authorName?.clearFocus()
        binding?.recipeName?.clearFocus()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

}