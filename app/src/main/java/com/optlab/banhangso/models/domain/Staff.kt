package com.optlab.banhangso.models.domain

import java.util.Date

data class Staff(
    val active: Boolean? = false,
    val storeId: String? = null,
    val role: String? = null,
) : Person() {
    constructor(
        active: Boolean? = null,
        storeId: String? = null,
        role: String? = null,
        id: String? = null,
        name: String? = null,
        email: String? = null,
        phone: String? = null,
        imageUrl: String? = null,
        createdAt: Date? = null,
        updatedAt: Date? = null,
    ) : this(active, storeId, role) {
        this.id = id
        this.name = name
        this.email = email
        this.phone = phone
        this.imageUrl = imageUrl
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }
}
