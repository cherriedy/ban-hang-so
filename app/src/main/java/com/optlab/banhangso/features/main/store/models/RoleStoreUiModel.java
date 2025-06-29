package com.optlab.banhangso.features.main.store.models;

import java.util.Date;

public class RoleStoreUiModel extends StoreUiModel {
  private String role;

  public RoleStoreUiModel() {
    super();
  }

  public RoleStoreUiModel(
      String id,
      String name,
      String description,
      String imageUrl,
      Date createdAt,
      Date updatedAt,
      String role) {
    super(id, name, description, imageUrl, createdAt, updatedAt);
    this.role = role;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }
}
