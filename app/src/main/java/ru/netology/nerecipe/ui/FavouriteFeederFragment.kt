package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import ru.netology.nerecipe.R
import ru.netology.nerecipe.adapter.RecipesAdapter
import ru.netology.nerecipe.databinding.FragmentFavouriteFeederBinding
import ru.netology.nerecipe.databinding.FragmentRecipesFeederBinding
import ru.netology.nerecipe.viewModel.RecipesViewModel

class FavouriteFeederFragment : Fragment() {
    private val viewModel: RecipesViewModel by activityViewModels<RecipesViewModel>()
    private var _binding: FragmentFavouriteFeederBinding? = null
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
        _binding = FragmentFavouriteFeederBinding.inflate(inflater)

        val adapter = RecipesAdapter(viewModel, RecipesAdapter.RECIPES_ADAPTER)
        binding?.recipesList?.adapter = adapter

        viewModel.allRecipesData.observe(viewLifecycleOwner) { recipes ->
            adapter.submitList(recipes.filter{ it.isFavourite })
        }

        viewModel.showRecipe.observe(viewLifecycleOwner) { recipe ->
            if (recipe == null) return@observe
            parentFragmentManager.commit {
                addToBackStack(null)
                replace(R.id.app_fragment_container, RecipesCardFragment())
            }
        }

        return binding?.root
    }

    companion object {
        const val TAG = "FavouriteFeederFragment"
    }
}