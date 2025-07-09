package com.optlab.banhangso.features.main.staff.models.mappers;

import com.optlab.banhangso.features.main.staff.models.StaffUiModel;
import com.optlab.banhangso.models.domain.Staff;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StaffUiModelMapper {

  @NonNull public static StaffUiModel fromDomain(@NonNull Staff staff) {
    StaffUiModel staffUiModel = new StaffUiModel();
    staffUiModel.setActive(staff.getActive());
    staffUiModel.setId(staff.getId());
    staffUiModel.setStoreId(staff.getStoreId());
    staffUiModel.setName(staff.getName());
    staffUiModel.setPhone(staff.getPhone());
    staffUiModel.setImageUrl(staff.getImageUrl());
    staffUiModel.setEmail(staff.getEmail());
    staffUiModel.setRole(staff.getRole());
    staffUiModel.setCreatedAt(staff.getCreatedAt());
    staffUiModel.setUpdatedAt(staff.getUpdatedAt());
    return staffUiModel;
  }

  @NonNull public static List<StaffUiModel> fromDomains(@NonNull List<Staff> staffs) {
    return staffs.stream().map(StaffUiModelMapper::fromDomain).collect(Collectors.toList());
  }

  @NonNull public static Staff toDomain(@NonNull StaffUiModel staffUiModel) {
    return new Staff(
        staffUiModel.getActive(),
        staffUiModel.getStoreId(),
        staffUiModel.getRole(),
        staffUiModel.getId(),
        staffUiModel.getName(),
        staffUiModel.getEmail(),
        staffUiModel.getPhone(),
        staffUiModel.getImageUrl(),
        staffUiModel.getCreatedAt(),
        staffUiModel.getUpdatedAt());
  }
}
