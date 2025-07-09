package com.optlab.banhangso.models.domain

import java.util.Date

data class Customer(
    var id: String? = null,
    var storeId: String? = null,
    var name: String? = null,
    var phone: String? = null,
    var email: String? = null,
    var address: String? = null,
    var imageUrl: String? = null,
    var dob: String? = null,
    var createdAt: Date? = null,
    var updatedAt: Date? = null,
)
