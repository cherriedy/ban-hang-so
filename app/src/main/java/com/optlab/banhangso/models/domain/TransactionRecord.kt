package com.optlab.banhangso.models.domain

import java.util.Date

data class TransactionRecord(
    val id: String,
    val customer: Customer,
    val staff: Staff,
    val totalItems: Int,
    val totalSellingPrices: Double,
    val totalPurchasePrices: Double,
    val totalDiscountPrices: Double,
    val finalPrices: Double,
    val paymentMethod: String,
    val items: List<Item>,
    val note: String? = "",
    val createdAt: Date,
) {
    data class Customer(
        val id: String,
        val name: String,
        val phone: String? = "",
        val email: String? = "",
    )

    data class Staff(
        val id: String,
        val name: String,
        val email: String,
        val phone: String? = "",
        val role: String,
    )

    data class Item(
        val id: String,
        val name: String,
        val thumbnailUrl: String,
        val sellingPrice: Double,
        val discountPrice: Double,
        val purchasePrice: Double,
        val quantity: Int,
        val barcode: String? = "",
        val brand: Brand,
        val category: Category,
    ) {
        data class Brand(val id: String, val name: String)

        data class Category(val id: String, val name: String)
    }
}
