package com.optlab.banhangso.models.domain

data class ProductSale
@JvmOverloads
constructor(
    var id: String? = null,
    var name: String? = null,
    var thumbnailUrl: String? = null,
    var sellingPrice: Double = 0.0,
    var discountPrice: Double = 0.0,
    var purchasePrice: Double = 0.0,
    var status: Boolean = false,
)
