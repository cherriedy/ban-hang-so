package com.optlab.banhangso.models.remote.mappers;

import androidx.annotation.NonNull;
import com.optlab.banhangso.models.domain.Customer;
import com.optlab.banhangso.models.remote.CustomerFirebaseObject;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CustomerFirebaseObjectMapper {

  @NonNull public static Customer toDomain(@NonNull CustomerFirebaseObject customerFirebaseObject) {
    Customer customer = new Customer();
    customer.setId(customerFirebaseObject.getId());
    customer.setName(customerFirebaseObject.getName());
    customer.setStoreId(customerFirebaseObject.getStoreId());
    customer.setPhone(customerFirebaseObject.getPhone());
    customer.setEmail(customerFirebaseObject.getEmail());
    customer.setAddress(customerFirebaseObject.getAddress());
    customer.setImageUrl(customerFirebaseObject.getImageUrl());
    customer.setDob(customerFirebaseObject.getDob());
    customer.setCreatedAt(customerFirebaseObject.getCreatedAt());
    customer.setUpdatedAt(customerFirebaseObject.getUpdatedAt());
    return customer;
  }

  @NonNull public static CustomerFirebaseObject fromDomain(@NonNull Customer customer) {
    CustomerFirebaseObject customerFirebaseObject = new CustomerFirebaseObject();
    customerFirebaseObject.setId(customer.getId());
    customerFirebaseObject.setName(customer.getName());
    customerFirebaseObject.setStoreId(customer.getStoreId());
    customerFirebaseObject.setPhone(customer.getPhone());
    customerFirebaseObject.setEmail(customer.getEmail());
    customerFirebaseObject.setAddress(customer.getAddress());
    customerFirebaseObject.setImageUrl(customer.getImageUrl());
    customerFirebaseObject.setDob(customer.getDob());
    customerFirebaseObject.setCreatedAt(customer.getCreatedAt());
    customerFirebaseObject.setUpdatedAt(customer.getUpdatedAt());
    return customerFirebaseObject;
  }
}
