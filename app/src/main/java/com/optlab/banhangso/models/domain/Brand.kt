package com.optlab.banhangso.models.domain

import com.optlab.banhangso.models.application.SortOption
import java.util.Date

data class Brand
    @JvmOverloads
    constructor(
        var id: String? = null,
        var storeId: String? = null,
        var name: String? = null,
        var productCount: Int = 0,
        var createdAt: Date? = null,
        var updatedAt: Date? = null,
    ) {
        enum class SortField(private val ascendingName: String, private val descendingName: String) :
            SortOption.Displayable {
            UPDATE_TIME("Cũ nhất", "Mới nhất"),
            NAME("Tên A -> Z", "Tên Z -> A"),
            ;

            override fun getDisplayName(isAscending: Boolean): String {
                return if (isAscending) ascendingName else descendingName
            }
        }

        companion object {
            @JvmStatic
            fun empty(): Brand = Brand("", "", "", 0, null, null)
        }

        fun isEmpty(): Boolean =
            id?.isBlank() == true &&
                storeId?.isBlank() == true &&
                name?.isBlank() == true &&
                productCount == 0 &&
                createdAt == null &&
                updatedAt == null
    }
