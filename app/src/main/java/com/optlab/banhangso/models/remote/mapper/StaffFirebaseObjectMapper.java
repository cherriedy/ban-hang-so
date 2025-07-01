package com.optlab.banhangso.models.remote.mapper;

import androidx.annotation.NonNull;
import com.optlab.banhangso.models.domain.Staff;
import com.optlab.banhangso.models.remote.StaffFirebaseObject;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Contract;

public class StaffFirebaseObjectMapper {

  private StaffFirebaseObjectMapper() {}

  @NonNull @Contract("_ -> new")
  public static Staff toDomain(@NonNull StaffFirebaseObject staffFirebaseObject) {
    return new Staff(
        staffFirebaseObject.getActive(),
        staffFirebaseObject.getStoreId(),
        staffFirebaseObject.getRole(),
        staffFirebaseObject.getId(),
        staffFirebaseObject.getName(),
        staffFirebaseObject.getEmail(),
        staffFirebaseObject.getPhone(),
        staffFirebaseObject.getImageUrl(),
        staffFirebaseObject.getCreatedAt(),
        staffFirebaseObject.getUpdatedAt());
  }

  @NonNull public static List<Staff> toDomains(@NonNull List<StaffFirebaseObject> staffFirebaseObjects) {
    return staffFirebaseObjects.stream()
        .map(StaffFirebaseObjectMapper::toDomain)
        .collect(Collectors.toList());
  }

  @NonNull @Contract("_ -> new")
  public static StaffFirebaseObject fromDomain(@NonNull Staff staff) {
    return new StaffFirebaseObject(
        staff.getId(),
        staff.getStoreId(),
        staff.getEmail(),
        staff.getPhone(),
        staff.getName(),
        staff.getImageUrl(),
        staff.getActive(),
        staff.getRole(),
        staff.getCreatedAt(),
        staff.getUpdatedAt());
  }
}
