package com.optlab.banhangso.features.main.category.adapters

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.optlab.banhangso.databinding.ListItemCategorySelectionBinding
import com.optlab.banhangso.features.main.category.models.CategoryUiModel

class CategorySelectionAdapter :
    PagingDataAdapter<CategoryUiModel, CategorySelectionAdapter.ViewHolder>(DiffCallback()) {
    var selectionTracker: SelectionTracker<String>? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding =
            ListItemCategorySelectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        getItem(position)?.let { category: CategoryUiModel ->
            // If category is not null, bind it to the ViewHolder with selection state.
            holder.bind(category, selectionTracker?.isSelected(category.id) == true)
        }
    }

    fun getCategoryAt(position: Int): CategoryUiModel? = getItem(position)

    inner class ViewHolder(private val binding: ListItemCategorySelectionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            category: CategoryUiModel,
            isSelected: Boolean,
        ) {
            binding.category = category

            // Set the checkbox state based on selection
            binding.rbSelect.isChecked = isSelected

            // Set the activated state for selection, which changes the background color
            itemView.isActivated = isSelected

            binding.executePendingBindings()
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<String> =
            object : ItemDetailsLookup.ItemDetails<String>() {
                override fun getPosition(): Int = bindingAdapterPosition

                override fun getSelectionKey(): String? =
                    if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                        getItem(bindingAdapterPosition)?.id
                    } else {
                        null
                    }
            }
    }

    class CategoryKeyProvider(private val adapter: CategorySelectionAdapter) :
        ItemKeyProvider<String>(SCOPE_MAPPED) {
        override fun getKey(position: Int): String? = adapter.getItem(position)?.id

        override fun getPosition(key: String): Int =
            (0 until adapter.itemCount).firstOrNull { position -> adapter.getItem(position)?.id == key }
                ?: RecyclerView.NO_POSITION
    }

    class CategoryDetailsLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<String>() {
        override fun getItemDetails(event: MotionEvent): ItemDetails<String>? {
            val view = recyclerView.findChildViewUnder(event.x, event.y)
            return (view?.let { recyclerView.getChildViewHolder(it) } as? ViewHolder)?.getItemDetails()
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<CategoryUiModel>() {
        override fun areItemsTheSame(
            oldItem: CategoryUiModel,
            newItem: CategoryUiModel,
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: CategoryUiModel,
            newItem: CategoryUiModel,
        ): Boolean = oldItem == newItem
    }
}
