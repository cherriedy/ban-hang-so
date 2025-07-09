package com.optlab.banhangso.features.main.customer.models.mappers;

import androidx.annotation.NonNull;
import com.optlab.banhangso.features.main.customer.models.CustomerUiModel;
import com.optlab.banhangso.models.domain.Customer;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CustomerUiModelMappers {

  @NonNull public static CustomerUiModel fromDomain(@NonNull Customer customer) {
    CustomerUiModel customerUiModel = new CustomerUiModel();
    customerUiModel.setId(customer.getId());
    customerUiModel.setName(customer.getName());
    customerUiModel.setEmail(customer.getEmail());
    customerUiModel.setPhone(customer.getPhone());
    customerUiModel.setAddress(customer.getAddress());
    customerUiModel.setImageUrl(customer.getImageUrl());
    customerUiModel.setDob(customer.getDob());
    customerUiModel.setCreatedAt(customer.getCreatedAt());
    customerUiModel.setUpdatedAt(customer.getUpdatedAt());
    return customerUiModel;
  }

  @NonNull public static Customer toDomain(@NonNull CustomerUiModel customerUiModel) {
    Customer customer = new Customer();
    customer.setId(customerUiModel.getId());
    customer.setName(customerUiModel.getName());
    customer.setEmail(customerUiModel.getEmail());
    customer.setPhone(customerUiModel.getPhone());
    customer.setAddress(customerUiModel.getAddress());
    customer.setImageUrl(customerUiModel.getImageUrl());
    customer.setDob(customerUiModel.getDob());
    customer.setCreatedAt(customerUiModel.getCreatedAt());
    customer.setUpdatedAt(customerUiModel.getUpdatedAt());
    return customer;
  }
}
