package com.optlab.banhangso.internal.utilities;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {

  public static final int ITEMS_PER_PAGE = 10;

  @UtilityClass
  public static class Auth {

    public static final String OWNER = "owner";
    public static final String STAFF = "staff";

    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_CONFIRM_PASSWORD = "confirmPassword";
    public static final String KEY_IS_SIGN_IN = "isSignIn";
    public static final String KEY_IS_OWNER = "isOwner";
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_USER_PHONE = "user_phone";
    public static final String KEY_STORE_NAME = "store_name";
    public static final String KEY_STORE_DESCRIPTION = "store_description";
    public static final String KEY_STORE_CODE = "store_code";

    public static final String ERROR_EMAIL = "errorEmail";
    public static final String ERROR_PASSWORD = "errorPassword";
    public static final String ERROR_CONFIRM_PASSWORD = "errorConfirmPassword";
    public static final String ERROR_USER_NAME = "errorUserName";
    public static final String ERROR_USER_PHONE = "errorUserPhone";
    public static final String ERROR_STORE_NAME = "errorStoreName";
    public static final String ERROR_STORE_DESCRIPTION = "errorStoreDescription";
    public static final String ERROR_STORE_CODE = "errorStoreCode";
    public static final String ERROR_TERMS_AND_CONDITIONS = "errorTermsAndConditions";
  }

  @UtilityClass
  public static class Product {

    public static final String KEY_NAME = "name";
    public static final String KEY_SELLING_PRICE = "sellingPrice";
    public static final String KEY_PURCHASE_PRICE = "purchasePrice";
    public static final String KEY_DISCOUNT_PRICE = "discountPrice";
    public static final String KEY_BRAND = "brand";
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_NOTE = "note";
  }

  @UtilityClass
  public static class Staff {

    public static final String KEY_NAME = "name";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_EMAIL = "email";

    public static final String ERROR_NAME = "error_name";
    public static final String ERROR_PHONE = "error_phone";
    public static final String ERROR_EMAIL = "error_email";
  }
}
