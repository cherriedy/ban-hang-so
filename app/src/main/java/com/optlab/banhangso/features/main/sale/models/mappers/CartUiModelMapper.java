package com.optlab.banhangso.features.main.sale.models.mappers;

import androidx.annotation.NonNull;
import com.optlab.banhangso.features.main.customer.models.CustomerUiModel;
import com.optlab.banhangso.features.main.sale.models.CartUiModel;
import com.optlab.banhangso.models.domain.Cart;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CartUiModelMapper {

  @NonNull public static Cart toDomain(@NonNull CartUiModel cartUiModel) {
    Cart cart = new Cart();
    cart.setStaffId(""); // Set the staffId to an empty string as it is not used in the CartUiModel.

    CustomerUiModel customerUiModel = cartUiModel.getCustomer();
    // If the customer is null, set the customerId to an empty string.
    cart.setCustomerId(customerUiModel != null ? customerUiModel.getId() : "");

    cart.setItems(CartItemUiModelMapper.toDomains(cartUiModel.asList()));
    cart.setTotalPurchasePrices(cartUiModel.getTotalPurchasePrices());
    cart.setTotalSellingPrices(cartUiModel.getTotalSellingPrices());
    cart.setTotalDiscountPrices(cartUiModel.getTotalDiscountPrices());

    // Set the payment type from the CartUiModel to the Cart.
    cart.setPaymentMethod(cartUiModel.getPaymentType().name());

    cart.setFinalPrices(cartUiModel.getFinalPrices());
    cart.setTotalItems(cartUiModel.getTotalItems());
    cart.setNote(cartUiModel.getNote());
    return cart;
  }
}
