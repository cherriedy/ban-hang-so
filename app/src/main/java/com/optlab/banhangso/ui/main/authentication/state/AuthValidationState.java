package com.optlab.banhangso.ui.main.authentication.state;

import androidx.annotation.NonNull;

/**
 * Represents the validation state for authentication forms (sign in, sign up, forget password).
 * Holds error messages for each field and provides a flag indicating if the form is valid. Used by
 * the ViewModel to emit validation errors to the UI.
 */
public class AuthValidationState {
    public static final String SIGN_IN_EMAIL = "SIGN_IN_EMAIL";
    public static final String SIGN_UP_EMAIL = "SIGN_UP_EMAIL";
    public static final String FORGET_PASSWORD_EMAIL = "FORGET_PASSWORD_EMAIL";

    private String type;
    private String emailError;
    private String passwordError;
    private String confirmPasswordError;
    private boolean hasNoError = false;

    /**
     * Constructor initializing the validation state for a given type.
     *
     * @param type The type of authentication form (sign in, sign up, forget password)
     */
    public AuthValidationState(@NonNull String type) {
        this.type = type;
        updateHasNoError();
    }

    /**
     * Validates the fields based on the type and updates the hasNoError flag.
     *
     * @param type The type of authentication form
     */
    public void validateFields(String type) {
        this.type = type;
        updateHasNoError();
    }

    /**
     * @return The type of authentication form
     */
    @NonNull
    public String getType() {
        return type;
    }

    /**
     * @return Error message for email field, or null if no error
     */
    public String getEmailError() {
        return emailError;
    }

    /**
     * Sets the error message for the email field and updates the hasNoError flag.
     *
     * @param emailError Error message or null
     */
    public void setEmailError(String emailError) {
        this.emailError = emailError;
        updateHasNoError();
    }

    /**
     * @return Error message for password field, or null if no error
     */
    public String getPasswordError() {
        return passwordError;
    }

    /**
     * Sets the error message for the password field and updates the hasNoError flag.
     *
     * @param passwordError Error message or null
     */
    public void setPasswordError(String passwordError) {
        this.passwordError = passwordError;
        updateHasNoError();
    }

    /**
     * @return Error message for confirm password field, or null if no error
     */
  public String getConfirmPasswordError() {
      return confirmPasswordError;
  }

    /**
     * Sets the error message for the confirm password field and updates the hasNoError flag.
     *
     * @param confirmPasswordError Error message or null
     */
    public void setConfirmPasswordError(String confirmPasswordError) {
        this.confirmPasswordError = confirmPasswordError;
        updateHasNoError();
    }

    /**
     * @return true if there are no errors in any relevant field for the current type
   */
  public boolean hasNoError() {
      return hasNoError;
  }

    /**
     * Updates the hasNoError flag based on current error fields and type.
     */
    private void updateHasNoError() {
        switch (type) {
            case SIGN_IN_EMAIL:
            case FORGET_PASSWORD_EMAIL:
                hasNoError = isEmpty(emailError) && isEmpty(passwordError);
                break;
            case SIGN_UP_EMAIL:
                hasNoError = isEmpty(emailError) && isEmpty(passwordError) && isEmpty(confirmPasswordError);
                break;
            default:
                hasNoError = false;
        }
    }

    /**
     * Utility method to check if a string is null or empty after trimming.
     *
     * @param s The string to check
     * @return true if null or empty
     */
    private boolean isEmpty(String s) {
        if (s == null) {
            return false;
        }
        return s.trim().isEmpty();
    }

    @NonNull
    @Override
    public String toString() {
        return "AuthValidationState{"
                + "type='"
                + type
                + '\''
                + ", emailError='"
                + emailError
                + '\''
                + ", passwordError='"
                + passwordError
                + '\''
                + ", confirmPasswordError='"
                + confirmPasswordError
                + '\''
                + ", hasNoError="
        + hasNoError
        + '}';
  }
}
