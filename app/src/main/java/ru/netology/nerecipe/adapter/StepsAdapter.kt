package ru.netology.nerecipe.adapter

import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nerecipe.R
import ru.netology.nerecipe.databinding.RecipesDetailsBinding
import ru.netology.nerecipe.databinding.StepDetailsBinding
import ru.netology.nerecipe.dto.RecipeStep
import ru.netology.nerecipe.viewModel.StepsDetailsHelper

class StepsAdapter(private val helper: StepsDetailsHelper, private val bindType: String)
    : ListAdapter<RecipeStep, StepsAdapter.StepViewHolder>(StepsDiffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = StepDetailsBinding.inflate(inflater,parent, false)

        return StepViewHolder(binding)
    }


    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        when (bindType) {
            SHOW_ADAPTER -> holder.bindForShow(getItem(position))
            EDIT_ADAPTER -> holder.bindForEdit(getItem(position))
            else -> Log.d("StepsShowAdapter Viewholder", "Invalid bindType parameter")
        }
    }



    inner class StepViewHolder(private val binding: StepDetailsBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindForShow(step: RecipeStep) {
            with (binding) {
                // No editing, just show the text
                stepContent.setText(step.content)
                stepContent.inputType = InputType.TYPE_NULL

                // Setting the image to show. Sored in
                val picName = step.picture
                if (picName == "empty"){
                    stepPicture.isVisible = false
                    return@with
                }
                stepPicture.setImageResource(helper.getResId(picName))
            }

        }

        fun bindForEdit(step: RecipeStep) {

            with (binding) {
                // No editing, just show the text
                stepContent.setText(step.content)
                stepContent.inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE

                // Setting the image to show. Sored in
                val picName = step.picture
                if (picName != "empty"){
                    stepPicture.setImageResource(helper.getResId(picName))
                }

                menuMore.setOnClickListener {
                    PopupMenu(it.context, it).apply {
                        inflate(R.menu.step_edit_options)
                        setOnMenuItemClickListener { item ->
                            when(item.itemId) {
                                R.id.step_remove -> {
                                    helper.deleteEditedStep(step)
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