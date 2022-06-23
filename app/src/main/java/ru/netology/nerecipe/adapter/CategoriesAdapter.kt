package ru.netology.nerecipe.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nerecipe.databinding.CategoryDetailCheckboxBinding
import ru.netology.nerecipe.dto.RecCategory
import java.util.*

class CategoriesAdapter: ListAdapter<RecCategory, CategoriesAdapter.CatViewHolder>(CategoryDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CategoryDetailCheckboxBinding.inflate(inflater, parent, false)

        return CatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CatViewHolder(private val binding: CategoryDetailCheckboxBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: RecCategory?) {
            if (item == null) return
            with(binding) {
                checkboxCategory.text = item.name
                checkboxCategory.isChecked = item.showOrNot

                checkboxCategory.setOnCheckedChangeListener { buttonView, isChecked ->

                }

                checkboxCategory.setOnClickListener {

                }
            }
        }

    }

    private object CategoryDiffCallback: DiffUtil.ItemCallback<RecCategory>(){
        override fun areItemsTheSame(oldItem: RecCategory, newItem: RecCategory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RecCategory, newItem: RecCategory): Boolean {
            return oldItem == newItem
        }
    }
}