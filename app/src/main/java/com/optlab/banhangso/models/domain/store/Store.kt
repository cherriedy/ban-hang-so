package com.optlab.banhangso.models.domain.store

import com.google.gson.annotations.SerializedName
import java.util.Date

open class Store(
    @SerializedName("id") open var id: String? = null,
    @SerializedName("name") open var name: String? = null,
    @SerializedName("description") open var description: String? = null,
    @SerializedName("imageUrl") open var imageUrl: String? = null,
    @SerializedName("createdAt") open var createdAt: Date? = null,
    @SerializedName("updatedAt") open var updatedAt: Date? = null,
) {
    fun empty(): Store {
        return Store(
            id = "",
            name = "",
            description = "",
            imageUrl = "",
            createdAt = null,
            updatedAt = null,
        )
    }

    open fun isEmpty(): Boolean =
        id.isNullOrEmpty() &&
            name.isNullOrEmpty() &&
            description.isNullOrEmpty() &&
            imageUrl.isNullOrEmpty() &&
            createdAt == null &&
            updatedAt == null

    override fun toString(): String {
        return "Store(id=$id, name=$name, description=$description, imageUrl=$imageUrl, createdAt=$createdAt, updatedAt=$updatedAt)"
    }
}
