package com.optlab.banhangso.ui.category.state;

import android.text.TextUtils;

public class CategoryEditValidationState {
    private String nameError;
    private boolean hasError = false;

    public CategoryEditValidationState(String nameError) {
        this.nameError = nameError;
    }

    public static CategoryEditValidationState empty() {
        return new CategoryEditValidationState("");
    }

    public String getNameError() {
        return nameError;
    }

    public void setNameError(String nameError) {
        this.nameError = nameError;
        setHasError();
    }

    public boolean hasError() {
        return hasError;
    }

    private void setHasError() {
        hasError = TextUtils.isEmpty(nameError);
    }
}
