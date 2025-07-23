package com.optlab.banhangso.features.main.brand.adapters

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.optlab.banhangso.databinding.ListItemBrandSelectionBinding
import com.optlab.banhangso.features.main.brand.models.BrandUiModel

/**
 * Brand selection adapter using Android's built-in SelectionTracker system.
 *
 * WHY SELECTION-TRACKER INSTEAD OF SIMPLE CLICK HANDLING?
 * =====================================================
 *
 * Simple approach would be:
 * ```
 * var selectedBrandId: String? = null
 * holder.itemView.setOnClickListener {
 *     selectedBrandId = brand.id
 *     notifyDataSetChanged()
 * }
 * ```
 *
 * But SelectionTracker provides:
 * - Multi-selection capabilities
 * - Keyboard navigation support
 * - Accessibility features (screen readers)
 * - Selection state persistence across configuration changes
 * - Professional animations and Material Design compliance
 * - Stable selection during data updates (pagination, sorting, filtering)
 *
 * THE CORE CONCEPT: STABLE SELECTION STORAGE
 * ==========================================
 *
 * SelectionTracker stores UNIQUE KEYS (brand IDs), not positions:
 * - Position-based: selectedPositions = {1, 3} ❌ Breaks when data changes
 * - Key-based: selectedKeys = {"brand-123", "brand-789"} ✅ Survives data changes
 *
 * Example: Initial: [Brand A, Brand B, Brand C] → User selects Brand B After Brand A deleted:
 * [Brand B, Brand C] → Brand B still selected ✅
 *
 * THREE REQUIRED COMPONENTS FOR SELECTION-TRACKER:
 * ==============================================
 * 1. BrandKeyProvider: Maps positions ↔ unique keys (brand IDs)
 * 2. BrandDetailsLookup: Maps touch coordinates ↔ item details
 * 3. SelectionTracker: Central coordinator that manages selection state
 */
class BrandSelectionAdapter :
    PagingDataAdapter<BrandUiModel, BrandSelectionAdapter.ViewHolder>(DiffCallback()) {
    var selectionTracker: SelectionTracker<String>? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding =
            ListItemBrandSelectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        getItem(position)?.let { brand: BrandUiModel ->
            // If brand is not null, bind it to the ViewHolder with selection state.
            holder.bind(brand = brand, isSelected = selectionTracker?.isSelected(brand.id) == true)
        }
    }

    /**
     * Public method to safely get an item at a specific position. This is needed because
     * PagingDataAdapter's getItem() is protected.
     *
     * @param position The position of the item to retrieve
     * @return The BrandUiModel at the given position, or null if invalid position or item not loaded
     */
    fun getBrandAt(position: Int): BrandUiModel? = getItem(position)

    inner class ViewHolder(private val binding: ListItemBrandSelectionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            brand: BrandUiModel,
            isSelected: Boolean,
        ) {
            binding.brand = brand

            // Set the checkbox state based on selection
            binding.rbSelect.isChecked = isSelected

            // Set the activated state for selection, which changes the background color
            itemView.isActivated = isSelected

            binding.executePendingBindings()
        }

        /**
         * Provides item details for SelectionTracker. This method is called by BrandDetailsLookup when
         * user touches the screen.
         *
         * Returns both position AND unique key because:
         * - Position: SelectionTracker needs to know which ViewHolder to update
         * - Key: SelectionTracker stores this key in its internal selection set
         */
        fun getItemDetails(): ItemDetailsLookup.ItemDetails<String> =
            object : ItemDetailsLookup.ItemDetails<String>() {
                // Get the position of the item in the adapter
                override fun getPosition(): Int = bindingAdapterPosition

                override fun getSelectionKey(): String? =
                    if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                        getItem(bindingAdapterPosition)?.id
                    } else {
                        null
                    }
            }
    }

    /**
     * COMPONENT 1: BrandKeyProvider - The "ID Manager"
     * ===============================================
     *
     * Role: Maps between POSITIONS and UNIQUE KEYS (brand IDs)
     *
     * Why needed:
     * - RecyclerView works with POSITIONS (0, 1, 2, 3...)
     * - SelectionTracker works with STABLE UNIQUE KEYS (brand IDs)
     * - When items are added/removed/reordered, positions change but IDs stay the same
     *
     * Example scenario: Initial: Position 0="abc", Position 1="def", Position 2="ghi" User selects
     * "def" → SelectionTracker stores: {"def"}
     *
     * After "abc" deleted: Position 0="def", Position 1="ghi" SelectionTracker asks: "Where is 'def'
     * now?" BrandKeyProvider answers: "Now at position 0"
     */
    class BrandKeyProvider(private val adapter: BrandSelectionAdapter) :
        ItemKeyProvider<String>(SCOPE_MAPPED) {
        /**
         * Returns the unique key for the item at the given position in the adapter. This key is used to
         * identify the item in the selection process.
         *
         * @param position The position of the item in the adapter.
         * @return The unique identifier of the item, or null if the position is invalid.
         */
        override fun getKey(position: Int): String? = adapter.getItem(position)?.id

        /**
         * Travels through the adapter to find the position of the item with the given key.
         *
         * @param key The unique identifier of the item to find.
         * @return The position of the item with the given key, or RecyclerView.NO_POSITION if not
         *   found.
         */
        override fun getPosition(key: String): Int =
            (0 until adapter.itemCount).firstOrNull { position -> adapter.getItem(position)?.id == key }
                ?: RecyclerView.NO_POSITION
    }

    /**
     * COMPONENT 2: BrandDetailsLookup - The "Touch Detective"
     * ======================================================
     *
     * Role: Converts TOUCH COORDINATES to ITEM INFORMATION
     *
     * Why needed:
     * - User touches screen at coordinates (x: 300px, y: 450px)
     * - SelectionTracker needs to know: "Which item was touched?"
     * - This class translates: touch location → ViewHolder → item details
     *
     * WHY NOT AUTOMATIC?
     * - RecyclerView ViewHolders can have multiple clickable areas
     * - Some ViewHolders might not be selectable (headers, footers)
     * - Complex layouts might need custom touch handling
     * - Android can't assume which parts of your layout are selectable
     *
     * Step-by-step process:
     * 1. User touches at (300, 450)
     * 2. BrandDetailsLookup finds which view is under those coordinates
     * 3. Gets the ViewHolder for that view
     * 4. Asks ViewHolder for item details (position + brand ID)
     * 5. Returns ItemDetails containing both position and key
     * 6. SelectionTracker uses this to update selection state
     */
    class BrandDetailsLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<String>() {
        override fun getItemDetails(event: MotionEvent): ItemDetails<String>? {
            // Find the view under the touch coordinates
            val view = recyclerView.findChildViewUnder(event.x, event.y)
            // If a view was found, get its ViewHolder and return item details
            return (view?.let { recyclerView.getChildViewHolder(it) } as? ViewHolder)?.getItemDetails()
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<BrandUiModel>() {
        override fun areItemsTheSame(
            oldItem: BrandUiModel,
            newItem: BrandUiModel,
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: BrandUiModel,
            newItem: BrandUiModel,
        ): Boolean {
            return oldItem == newItem
        }
    }
}
