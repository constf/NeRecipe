package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import ru.netology.nerecipe.adapter.CategoriesAdapter
import ru.netology.nerecipe.databinding.FragmentCategoriesFeederBinding
import ru.netology.nerecipe.viewModel.RecipesViewModel

class CategoriesFeederFragment: Fragment() {
    private val viewModel: RecipesViewModel by activityViewModels<RecipesViewModel>()
    private var _binding: FragmentCategoriesFeederBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            Toast.makeText(context, "Please use bottom menu to switch between screens!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCategoriesFeederBinding.inflate(inflater)

        val adapter = CategoriesAdapter(viewModel)
        binding?.categoriesList?.adapter = adapter

        viewModel.catData.observe(viewLifecycleOwner){ categories ->
            adapter.submitList(categories)
        }


        return binding?.root
    }

    companion object {
        const val TAG = "CategoriesFeederFragment"
    }
}