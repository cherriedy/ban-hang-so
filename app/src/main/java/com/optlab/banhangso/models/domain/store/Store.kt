package com.optlab.banhangso.models.domain.store;

import androidx.annotation.NonNull;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.Contract;

@Data
@SuperBuilder
@NoArgsConstructor
public class Store {
  private String id;
  private String name;
  private String description;
  private String imageUrl;
  private Date createdAt;
  private Date updatedAt;

  @NonNull @Contract(" -> new")
  public static Store empty() {
    return Store.builder()
        .id("")
        .name("")
        .description("")
        .imageUrl("")
        .createdAt(null)
        .updatedAt(null)
        .build();
  }

  public boolean isEmpty(@NonNull Store store) {
    return store.getId().isBlank()
        && store.getName().isBlank()
        && store.getDescription().isBlank()
        && store.getImageUrl().isBlank()
        && store.getCreatedAt() == null
        && store.getUpdatedAt() == null;
  }
}
