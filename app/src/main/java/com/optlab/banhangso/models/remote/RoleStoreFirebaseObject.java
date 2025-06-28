package com.optlab.banhangso.models.remote;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RoleStoreFirebaseObject extends StoreFirebaseObject {
    @SerializedName("role")
    private String role;
}
