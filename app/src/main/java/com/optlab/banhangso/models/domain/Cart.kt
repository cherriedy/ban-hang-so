package com.optlab.banhangso.models.domain

/**
 * Represents a shopping cart.
 *
 * @property staffId The ID of the staff handling the cart.
 * @property customerId The ID of the customer.
 * @property totalSellingPrices The total selling prices of items in the cart.
 * @property totalDiscountPrices The total discount prices applied to items in the cart.
 * @property totalPurchasePrices The total purchase prices of items in the cart.
 * @property finalPrices The final prices after all calculations.
 * @property note Additional notes for the cart.
 * @property items The list of items in the cart.
 * @property totalItems The total number of items in the cart.
 * @property paymentMethod The type of payment used.
 */
data class Cart(
    var staffId: String? = null,
    var customerId: String? = null,
    var totalSellingPrices: Double? = null,
    var totalDiscountPrices: Double? = null,
    var totalPurchasePrices: Double? = null,
    var finalPrices: Double? = null,
    var note: String? = null,
    var items: List<Item>? = null,
    var totalItems: Int? = null,
    var paymentMethod: String? = null,
) {
    /**
     * Represents an item in the cart.
     *
     * @property id The ID of the item.
     * @property quantity The quantity of the item.
     */
    data class Item(var id: String? = null, var quantity: Int? = null)
}
