package com.optlab.banhangso.repositories.perferences

import com.optlab.banhangso.datastore.RoleStoreProto
import com.optlab.banhangso.datastore.UserProto
import com.optlab.banhangso.models.domain.User
import com.optlab.banhangso.models.domain.store.RoleStore

object ProtoObjectsMapper {
    @JvmStatic
    fun UserProto.toDomain(): User {
        val user = User()
        user.id = this.id
        user.name = this.name
        user.phone = this.phone
        user.email = this.email
        user.imageUrl = this.avatar
        return user
    }

    @JvmStatic
    fun User.toProto(): UserProto {
        return UserProto.newBuilder()
            .setId(this.id ?: "")
            .setName(this.name ?: "")
            .setEmail(this.email ?: "")
            .setPhone(this.phone ?: "")
            .setAvatar(this.imageUrl ?: "")
            .build()
    }

    @JvmStatic
    fun RoleStoreProto.toDomain(): RoleStore {
        return RoleStore(
            role = this.role.toString(),
            id = this.id,
            name = this.name,
            description = this.description,
            imageUrl = this.imageUrl,
        )
    }

    @JvmStatic
    fun RoleStore.toProto(): RoleStoreProto {
        return RoleStoreProto.newBuilder()
            .setId(this.id ?: "")
            .setName(this.name ?: "")
            .setDescription(this.description ?: "")
            .setImageUrl(this.imageUrl ?: "")
            .setPhoneNumber("")
            .setRole("")
            .build()
    }
}
