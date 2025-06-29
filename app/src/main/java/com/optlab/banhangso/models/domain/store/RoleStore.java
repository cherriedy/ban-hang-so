package com.optlab.banhangso.models.domain.store;

import androidx.annotation.NonNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.Contract;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RoleStore extends Store {
  private String role;

  @NonNull @Contract(" -> new")
  public static RoleStore empty() {
    return RoleStore.builder()
        .id("")
        .name("")
        .description("")
        .imageUrl("")
        .createdAt(null)
        .updatedAt(null)
        .role("")
        .build();
  }

  public boolean isEmpty() {
    return this.equals(RoleStore.empty());
  }
}
