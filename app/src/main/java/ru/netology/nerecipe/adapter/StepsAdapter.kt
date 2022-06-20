package ru.netology.nerecipe.adapter

import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.doOnDetach
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import ru.netology.nerecipe.R
import ru.netology.nerecipe.databinding.RecipesDetailsBinding
import ru.netology.nerecipe.databinding.StepDetailsShowBinding
import ru.netology.nerecipe.dto.RecipeStep
import ru.netology.nerecipe.viewModel.StepsDetailsHelper

class StepsAdapter(private val helper: StepsDetailsHelper, private val bindType: String)
    : ListAdapter<RecipeStep, StepsAdapter.StepViewHolder>(StepsDiffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = StepDetailsShowBinding.inflate(inflater,parent, false)

        return StepViewHolder(binding)
    }


    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        when (bindType) {
            SHOW_ADAPTER -> holder.bindForShow(getItem(position))
            EDIT_ADAPTER -> holder.bindForEdit(getItem(position))
            else -> Log.d("StepsShowAdapter Viewholder", "Invalid bindType parameter")
        }
    }



    inner class StepViewHolder(private val binding: StepDetailsShowBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindForShow(step: RecipeStep) {
            with (binding) {
                stepContent.text = step.content
                menuMore.isVisible = false

                // Setting the image to show.
                val name = step.picture
                if (name == "empty"){
                    stepPicture.isVisible = false
                } else {
                    stepPicture.isVisible = true
                    stepPicture.setImageBitmap(helper.getBitmapFromFile(name))
                }
            }
        }

        fun bindForEdit(step: RecipeStep) {

            with (binding) {
                // No editing, just show the text
                stepContent.text = step.content
                menuMore.isVisible = true

                // Setting the image to show. Either R.id or uri
                val name = step.picture

                val picUri = step.pUri

                if (name == "empty"){
                    stepPicture.isVisible = false
                } else if (picUri == null) {
                    stepPicture.isVisible = true
                    stepPicture.setImageBitmap(helper.getBitmapFromFile(name))
                } else {
                    stepPicture.isVisible = true
                    stepPicture.setImageURI(picUri)
                }

                stepDetailsCard.setOnClickListener {
                    helper.editStep(step)
                }

                menuMore.setOnClickListener {
                    PopupMenu(it.context, it).apply {
                        inflate(R.menu.step_edit_options)
                        setOnMenuItemClickListener { item ->
                            when(item.itemId) {
                                R.id.step_remove -> {
                                    MaterialAlertDialogBuilder(it.context)
                                        .setMessage("Are you sure to delete this step description?")
                                        .setNegativeButton("No, let it stay"){ dialog, which -> }
                                        .setPositiveButton("Yes, delete!"){ dialog, which ->
                                            helper.deleteEditedStep(step)
                                        }.show()
                                    true
                                }
                                else -> false
                            }
                        }
                    }.show()
                }
            }
        }
    }

    private object StepsDiffCallback: DiffUtil.ItemCallback<RecipeStep>() {
        override fun areItemsTheSame(oldItem: RecipeStep, newItem: RecipeStep): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RecipeStep, newItem: RecipeStep): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        const val SHOW_ADAPTER = ".show"
        const val EDIT_ADAPTER = ".edit"
    }
}