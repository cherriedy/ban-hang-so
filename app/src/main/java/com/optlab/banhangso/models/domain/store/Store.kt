package com.optlab.banhangso.models.domain.store

import com.google.gson.annotations.SerializedName
import java.util.Date

open class Store(
    @SerializedName("id")
    open var id: String? = null,
    @SerializedName("name")
    open var name: String? = null,
    @SerializedName("description")
    open var description: String? = null,
    @SerializedName("imageUrl")
    open var imageUrl: String? = null,
    @SerializedName("createdAt")
    open var createdAt: Date? = null,
    @SerializedName("updatedAt")
    open var updatedAt: Date? = null,
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

    fun isEmpty(store: Store): Boolean {
        return store.id?.isBlank() != false &&
            store.name?.isBlank() != false &&
            store.description?.isBlank() != false &&
            store.imageUrl?.isBlank() != false &&
            store.createdAt == null && store.updatedAt == null
    }
}
