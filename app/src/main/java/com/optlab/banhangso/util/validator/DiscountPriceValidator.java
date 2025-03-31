package com.optlab.banhangso.util.validator;

import com.optlab.banhangso.data.model.Product;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for checking if the discount price is less than the selling price. This validator is
 * applied to the Product class.
 */
public class DiscountPriceValidator implements ConstraintValidator<ValidDiscountPrice, Product> {
  @Override
  public boolean isValid(Product product, ConstraintValidatorContext context) {
    // No validation needed if product is null or selling price is zero.
    if (product == null || product.getSellingPrice() == 0) {
      return true;
    }

    // Check if discount price is greater than or equal to selling price.
    if (product.getDiscountPrice() >= product.getSellingPrice()) {
      context.disableDefaultConstraintViolation();
      context
          .buildConstraintViolationWithTemplate("{ValidDiscountPrice.product}")
          .addPropertyNode("discountPrice")
          .addConstraintViolation();
      return false;
    }
    return true;
  }
}
