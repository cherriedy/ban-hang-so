package com.optlab.banhangso.models.domain;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
  private String id;
  private String name;
  private String phone;
  private String email;
  private String imageUrl;
  private List<Store> stores;
  private Date createdAt;
  private Date updatedAt;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Store {
    private String id;
    private String role;
  }
}
