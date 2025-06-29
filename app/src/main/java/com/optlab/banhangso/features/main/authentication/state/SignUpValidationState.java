package com.optlab.banhangso.features.main.authentication.state;

import androidx.annotation.NonNull;

/**
 * Represents the validation state for the sign-up form. Holds error messages for each field and
 * provides a flag indicating if the form is valid. Used by the ViewModel to emit validation errors
 * to the UI.
 */
public class SignUpValidationState {
  private String contactNameError;
  private String contactPhoneError;
  private String storeNameError;
  private String storeDescriptionError;
  private String storeCodeError;
  private String termsAndConditionsError;
  private String role = "ADMIN";
  private boolean hasNoError = false;

  /** Default constructor initializing all errors to null (no error). */
  public SignUpValidationState() {
    this(null, null, null, null, null, null);
  }

  /**
   * Constructor to initialize all error fields.
   *
   * @param contactNameError Error message for contact name
   * @param contactPhoneError Error message for contact phone
   * @param storeNameError Error message for store name
   * @param storeDescriptionError Error message for store description
   * @param storeCodeError Error message for store code
   * @param termsAndConditionsError Error message for terms and conditions
   */
  public SignUpValidationState(
      String contactNameError,
      String contactPhoneError,
      String storeNameError,
      String storeDescriptionError,
      String storeCodeError,
      String termsAndConditionsError) {
    this.contactNameError = contactNameError;
    this.contactPhoneError = contactPhoneError;
    this.storeNameError = storeNameError;
    this.storeDescriptionError = storeDescriptionError;
    this.storeCodeError = storeCodeError;
    this.termsAndConditionsError = termsAndConditionsError;
  }

  public String getContactNameError() {
    return contactNameError;
  }

  public void setContactNameError(String contactNameError) {
    this.contactNameError = contactNameError;
    updateHasNoError();
  }

  public String getContactPhoneError() {
    return contactPhoneError;
  }

  public void setContactPhoneError(String contactPhoneError) {
    this.contactPhoneError = contactPhoneError;
    updateHasNoError();
  }

  public String getStoreNameError() {
    return storeNameError;
  }

  public void setStoreNameError(String storeNameError) {
    this.storeNameError = storeNameError;
    updateHasNoError();
  }

  public String getStoreDescriptionError() {
    return storeDescriptionError;
  }

  public void setStoreDescriptionError(String storeDescriptionError) {
    this.storeDescriptionError = storeDescriptionError;
    updateHasNoError();
  }

  public String getStoreCodeError() {
    return storeCodeError;
  }

  public void setStoreCodeError(String storeCodeError) {
    this.storeCodeError = storeCodeError;
    updateHasNoError();
  }

  public String getTermsAndConditionsError() {
    return termsAndConditionsError;
  }

  public void setTermsAndConditionsError(String termsAndConditionsError) {
    this.termsAndConditionsError = termsAndConditionsError;
    updateHasNoError();
  }

  public boolean isHasNoError() {
    return hasNoError;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
    updateHasNoError();
  }

  /**
   * @return true if there are no errors in any field.
   */
  public boolean hasNoError() {
    return hasNoError;
  }

  /** Updates the hasNoError flag based on current error fields. */
  private void updateHasNoError() {
    switch (role) {
      case "ADMIN" ->
          hasNoError =
              isEmpty(contactNameError)
                  && isEmpty(contactPhoneError)
                  && isEmpty(storeNameError)
                  && isEmpty(storeDescriptionError)
                  && isEmpty(termsAndConditionsError);
      case "STAFF" ->
          hasNoError =
              isEmpty(contactNameError)
                  && isEmpty(contactPhoneError)
                  && isEmpty(storeCodeError)
                  && isEmpty(termsAndConditionsError);
    }
  }

  private boolean isEmpty(String s) {
    if (s == null) {
      return false;
    }
    return s.trim().isEmpty();
  }

  @NonNull @Override
  public String toString() {
    return "SignUpValidationState{"
        + "contactNameError='"
        + contactNameError
        + '\''
        + ", contactPhoneError='"
        + contactPhoneError
        + '\''
        + ", storeNameError='"
        + storeNameError
        + '\''
        + ", storeDescriptionError='"
        + storeDescriptionError
        + '\''
        + ", storeCodeError='"
        + storeCodeError
        + '\''
        + ", termsAndConditionsError='"
        + termsAndConditionsError
        + '\''
        + ", hasNoError="
        + hasNoError
        + ", role='"
        + role
        + '\''
        + '}';
  }
}
