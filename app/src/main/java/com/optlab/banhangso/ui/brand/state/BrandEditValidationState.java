package com.optlab.banhangso.ui.brand.state;

import android.text.TextUtils;

public class BrandEditValidationState {
    private String nameError;
    private boolean hasNoError = false;

    public BrandEditValidationState(String nameError) {
        this.nameError = nameError;
    }

    public static BrandEditValidationState empty() {
        return new BrandEditValidationState(null);
    }

    public void setNameError(String nameError) {
        this.nameError = nameError;
        setHasError();
    }

    private void setHasError() {
        hasNoError = TextUtils.isEmpty(nameError);
    }

    public String getNameError() {
        return nameError;
    }

    public boolean hasNoError() {
        return hasNoError;
    }
}
