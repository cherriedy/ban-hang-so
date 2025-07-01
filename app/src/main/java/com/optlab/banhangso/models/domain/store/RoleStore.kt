package com.optlab.banhangso.models.domain.store

import com.google.gson.annotations.SerializedName
import java.util.Date

data class RoleStore(
    @SerializedName("role")
    val role: String? = null,
) : Store() {
    constructor(
        role: String? = null,
        id: String? = null,
        name: String? = null,
        description: String? = null,
        imageUrl: String? = null,
        createdAt: Date? = null,
        updatedAt: Date? = null,
    ) : this(role) {
        this.id = id
        this.name = name
        this.description = description
        this.imageUrl = imageUrl
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }

    // Secondary constructor for backwards compatibility
    constructor(store: Store, role: String?) : this(role) {
        this.id = store.id
        this.name = store.name
        this.description = store.description
        this.imageUrl = store.imageUrl
        this.createdAt = store.createdAt
        this.updatedAt = store.updatedAt
    }

    val isEmpty: Boolean
        get() = super.isEmpty(this) && role?.isBlank() != false

    companion object {
        @JvmStatic
        fun empty(): RoleStore {
            return RoleStore(
                role = "",
                id = "",
                name = "",
                description = "",
                imageUrl = "",
                createdAt = null,
                updatedAt = null,
            )
        }
    }
}
