package com.optlab.banhangso.features.main.customer.adapters

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.optlab.banhangso.databinding.ListItemCustomerSelectionBinding
import com.optlab.banhangso.features.main.customer.models.CustomerUiModel

class CustomerSelectionAdapter :
    PagingDataAdapter<CustomerUiModel, CustomerSelectionAdapter.ViewHolder>(DiffCallback()) {
    var selectionTracker: SelectionTracker<String>? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding =
            ListItemCustomerSelectionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        getItem(position)?.let { customer: CustomerUiModel ->
            holder.bind(customer, selectionTracker?.isSelected(customer.id) == true)
        }
    }

    fun getCustomerAt(position: Int): CustomerUiModel? = getItem(position)

    inner class ViewHolder(private val binding: ListItemCustomerSelectionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            customer: CustomerUiModel,
            isSelected: Boolean,
        ) {
            binding.customer = customer

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

    class CustomerKeyProvider(private val adapter: CustomerSelectionAdapter) :
        ItemKeyProvider<String>(SCOPE_MAPPED) {
        override fun getKey(position: Int): String? = adapter.getItem(position)?.id

        override fun getPosition(key: String): Int =
            (0 until adapter.itemCount).firstOrNull { position ->
                adapter.getItem(position)?.id == key
            } ?: RecyclerView.NO_POSITION
    }

    class CustomerDetailsLookup(private val recyclerView: RecyclerView) :
        ItemDetailsLookup<String>() {
        override fun getItemDetails(event: MotionEvent): ItemDetails<String>? {
            val view = recyclerView.findChildViewUnder(event.x, event.y)
            return (view?.let { recyclerView.getChildViewHolder(it) } as? ViewHolder)
                ?.getItemDetails()
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<CustomerUiModel>() {
        override fun areItemsTheSame(
            oldItem: CustomerUiModel,
            newItem: CustomerUiModel,
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: CustomerUiModel,
            newItem: CustomerUiModel,
        ): Boolean = oldItem == newItem
    }
}
