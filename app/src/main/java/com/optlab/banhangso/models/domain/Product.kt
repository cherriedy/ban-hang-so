package com.optlab.banhangso.models.domain

import com.optlab.banhangso.models.application.SortOption.Displayable
import java.util.Date

data class Product
    @JvmOverloads
    constructor(
        var id: String? = null,
        var storeId: String? = null,
        var barcode: String? = null,
        var category: Category? = null,
        var brand: Brand? = null,
        var name: String? = null,
        var purchasePrice: Double = 0.0,
        var sellingPrice: Double = 0.0,
        var thumbnailUrl: String? = null,
        var imageUrls: List<String?>? = null,
        var stockQuantity: Int = 0,
        var description: String? = null,
        var status: Boolean = false,
        var discountPrice: Double = 0.0,
        var note: String? = null,
        var createdAt: Date? = null,
        var updatedAt: Date? = null,
    ) {
        enum class SortField(private val ascendingName: String, private val descendingName: String) :
            Displayable {
            NAME("Tên A -> Z", "Tên Z -> A"),
            SELLING_PRICE("Giá từ thấp tới cao", "Giá từ cao tới thấp"),
            ;

            override fun getDisplayName(isAscending: Boolean): String {
                return if (isAscending) ascendingName else descendingName
            }
        }
    }
