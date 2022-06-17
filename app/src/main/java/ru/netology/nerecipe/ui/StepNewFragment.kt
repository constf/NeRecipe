package ru.netology.nerecipe.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import ru.netology.nerecipe.R
import ru.netology.nerecipe.databinding.FragmentRecipeNewBinding
import ru.netology.nerecipe.databinding.StepDetailsEditBinding
import ru.netology.nerecipe.viewModel.RecipesViewModel

class StepNewFragment: Fragment() {

    private val viewModel: RecipesViewModel by activityViewModels<RecipesViewModel>()
    private var _binding: StepDetailsEditBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this){
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = StepDetailsEditBinding.inflate(inflater, container, false)
        activity?.actionBar?.title = "New Step"

        val step = viewModel.getEditedStep()

        with(binding){
            stepContent.setText(step?.content)

            val picName = step?.picture
            val picUri = step?.pUri

            if (picName != "empty" && picUri == null) {
                stepPicture.setImageResource(viewModel.getResId(picName!!))
            } else if (picUri != null) {
                stepPicture.setImageURI(picUri)
            }

            stepPicture.setOnClickListener {
                viewModel.onChoosePictureClicked(step)
            }

            viewModel.chooseThePicture.observe(viewLifecycleOwner) { stepForPicture ->
                if (stepForPicture == null) return@observe
                val intent = Intent().apply {
                    action = Intent.ACTION_PICK
                    type = "image/*"
                }
                startActivityForResult(intent, IMAGE_PICK_KEY, null)
            }

            menuMore.isVisible = false
        }


        return binding?.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.recipe_new_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.recipe_new_options_save -> {
                with(binding){
                    if (stepContent.text.toString().isNullOrBlank()){
                        Toast.makeText(context, "Recipe step description not be empty!", Toast.LENGTH_SHORT)
                            .also { it.setGravity(Gravity.CENTER_VERTICAL, Gravity.AXIS_X_SHIFT, Gravity.AXIS_Y_SHIFT) }
                            .show()
                        return true
                    }
                    val newStep = viewModel.getEditedStep()?.copy(content = stepContent.text.toString())
                    viewModel.onSaveEditedStep(newStep!!)
                }
                parentFragmentManager.popBackStack()
                true
            }

            R.id.recipe_new_options_discard -> {
                parentFragmentManager.popBackStack()
                true
            }

            else -> return super.onOptionsItemSelected(item)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != IMAGE_PICK_KEY || resultCode != Activity.RESULT_OK) return
        viewModel.setEditedStepsPicture(data?.data)

        Log.d("onActivityResult-URI", data?.data.toString())
    }


}