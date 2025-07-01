package com.optlab.banhangso.internal.validators;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.optlab.banhangso.R;
import com.optlab.banhangso.models.domain.Brand;
import com.optlab.banhangso.models.domain.Category;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ProductValidator implements BaseValidator {

  private final Context context;
  private final Map<String, List<ValidationRule<String>>> stringRules = new HashMap<>();
  private final Map<String, List<ValidationRule<Double>>> doubleRules = new HashMap<>();
  private final List<ValidationRule<Brand>> brandRules = new ArrayList<>();
  private final List<ValidationRule<Category>> categoryRules = new ArrayList<>();

  @Override
  public Context getContext() {
    return context;
  }

  public ProductValidator(Context context) {
    this.context = context;
    setupDefaultRules();
  }

  private void setupDefaultRules() {
    // Product name rules
    List<ValidationRule<String>> nameRules = new ArrayList<>();
    nameRules.add(ValidationRules.notEmpty(R.string.alert_product_name_non_null));
    nameRules.add(ValidationRules.minLength(10, R.string.alert_product_name_min_chars));
    nameRules.add(ValidationRules.maxLength(100, R.string.alter_product_name_max_chars));
    stringRules.put("name", nameRules);

    // Description rules
    List<ValidationRule<String>> descRules = new ArrayList<>();
    descRules.add(ValidationRules.maxLength(120, R.string.alert_product_description_max_chars));
    stringRules.put("description", descRules);

    // Note rules
    List<ValidationRule<String>> noteRules = new ArrayList<>();
    noteRules.add(ValidationRules.maxLength(50, R.string.alert_product_note_max_chars));
    stringRules.put("note", noteRules);

    // Selling price rules
    List<ValidationRule<Double>> sellingPriceRules = new ArrayList<>();
    sellingPriceRules.add(ValidationRules.nonZero(R.string.alert_product_price_non_null));
    sellingPriceRules.add(ValidationRules.positiveNumber(R.string.alert_product_price_invalid));
    doubleRules.put("sellingPrice", sellingPriceRules);

    // Purchase price rules (basic, comparison with selling price done in validation method)
    List<ValidationRule<Double>> purchasePriceRules = new ArrayList<>();
    purchasePriceRules.add(ValidationRules.positiveNumber(R.string.alert_product_price_invalid));
    doubleRules.put("purchasePrice", purchasePriceRules);

    // Discount price rules (basic, comparison with selling price done in validation method)
    List<ValidationRule<Double>> discountPriceRules = new ArrayList<>();
    discountPriceRules.add(ValidationRules.positiveNumber(R.string.alert_product_price_invalid));
    doubleRules.put("discountPrice", discountPriceRules);

    // Brand rules
    brandRules.add(
        (brand, ctx) ->
            (brand == null || brand.isEmpty())
                ? ctx.getString(R.string.alert_product_brand_non_null)
                : "");

    // Category rules
    categoryRules.add(
        (category, ctx) ->
            (category == null || category.isEmpty())
                ? ctx.getString(R.string.alert_product_category_non_null)
                : "");
  }

  // Rule management methods
  public void addStringRuleForField(String fieldName, @NonNull ValidationRule<String> rule) {
    stringRules.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(rule);
  }

  public void addDoubleRuleForField(String fieldName, @NonNull ValidationRule<Double> rule) {
    doubleRules.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(rule);
  }

  public void addBrandRule(@NonNull ValidationRule<Brand> rule) {
    brandRules.add(rule);
  }

  public void addCategoryRule(@NonNull ValidationRule<Category> rule) {
    categoryRules.add(rule);
  }

  public void setCustomStringRulesForField(String fieldName, List<ValidationRule<String>> rules) {
    stringRules.put(fieldName, new ArrayList<>(rules));
  }

  public void setCustomDoubleRulesForField(String fieldName, List<ValidationRule<Double>> rules) {
    doubleRules.put(fieldName, new ArrayList<>(rules));
  }

  // Validation methods
  @NonNull public String validateName(@NonNull String name) {
    return validate(name, stringRules.get("name"));
  }

  @NonNull public String validateSellingPrice(@NonNull Double sellingPrice) {
    return validate(sellingPrice, doubleRules.get("sellingPrice"));
  }

  @NonNull public String validatePurchasePrice(@NonNull Double purchasePrice, @NonNull Double sellingPrice) {
    // First validate basic rules
    String basicValidation = validate(purchasePrice, doubleRules.get("purchasePrice"));
    if (!basicValidation.isEmpty()) return basicValidation;

    // Then validate comparison with selling price
    if (purchasePrice > sellingPrice) {
      return context.getString(R.string.alert_product_purchase_price_invalid);
    }
    return "";
  }

  @NonNull public String validateDiscountPrice(@NonNull Double discountPrice, @NonNull Double sellingPrice) {
    // First validate basic rules
    String basicValidation = validate(discountPrice, doubleRules.get("discountPrice"));
    if (!basicValidation.isEmpty()) return basicValidation;

    // Then validate comparison with selling price
    if (discountPrice > sellingPrice) {
      return context.getString(R.string.alert_product_discount_price_invalid);
    }
    return "";
  }

  @NonNull public String validateDescription(@Nullable String description) {
    if (description == null) return "";
    return validate(description, stringRules.get("description"));
  }

  @NonNull public String validateNote(@Nullable String note) {
    if (note == null) return "";
    return validate(note, stringRules.get("note"));
  }

  @NonNull public String validateBrand(Brand brand) {
    return validate(brand, brandRules);
  }

  @NonNull public String validateCategory(Category category) {
    return validate(category, categoryRules);
  }

  @NonNull private String validateStringWithRules(String value, List<ValidationRule<String>> rules) {
    if (rules == null) return "";
    for (ValidationRule<String> rule : rules) {
      String result = rule.validate(value, context);
      if (!result.isEmpty()) return result;
    }
    return "";
  }

  @NonNull private String validateDoubleWithRules(Double value, List<ValidationRule<Double>> rules) {
    if (rules == null) return "";
    for (ValidationRule<Double> rule : rules) {
      String result = rule.validate(value, context);
      if (!result.isEmpty()) return result;
    }
    return "";
  }
}
