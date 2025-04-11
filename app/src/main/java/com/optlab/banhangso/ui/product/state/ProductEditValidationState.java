package com.optlab.banhangso.ui.product.state;

import android.text.TextUtils;

public class ProductEditValidationState {
    private String nameError;
    private String sellingPriceError;
    private String purchasePriceError;
    private String discountPriceError;
    private String brandError;
    private String categoryError;
    private String descriptionError;
    private String noteError;
    private boolean hasNoError = false;

    public ProductEditValidationState(
            String nameError,
            String sellingPriceError,
            String purchasePriceError,
            String discountPriceError,
            String descriptionError,
            String noteError) {
        this.nameError = nameError;
        this.sellingPriceError = sellingPriceError;
        this.purchasePriceError = purchasePriceError;
        this.discountPriceError = discountPriceError;
        this.descriptionError = descriptionError;
        this.noteError = noteError;
    }

    public static ProductEditValidationState empty() {
        return new ProductEditValidationState(null, null, null, null, null, null);
    }

    public String getNameError() {
        return nameError;
    }

    public void setNameError(String nameError) {
        this.nameError = nameError;
        setHasError();
    }

    public String getSellingPriceError() {
        return sellingPriceError;
    }

    public void setSellingPriceError(String sellingPriceError) {
        this.sellingPriceError = sellingPriceError;
        setHasError();
    }

    public String getPurchasePriceError() {
        return purchasePriceError;
    }

    public void setPurchasePriceError(String purchasePriceError) {
        this.purchasePriceError = purchasePriceError;
        setHasError();
    }

    public String getDiscountPriceError() {
        return discountPriceError;
    }

    public void setDiscountPriceError(String discountPriceError) {
        this.discountPriceError = discountPriceError;
        setHasError();
    }

    public String getBrandError() {
        return brandError;
    }

    public void setBrandError(String brandError) {
        this.brandError = brandError;
        setHasError();
    }

    public String getCategoryError() {
        return categoryError;
    }

    public void setCategoryError(String categoryError) {
        this.categoryError = categoryError;
        setHasError();
    }

    public String getDescriptionError() {
        return descriptionError;
    }

    public void setDescriptionError(String descriptionError) {
        this.descriptionError = descriptionError;
        setHasError();
    }

    public String getNoteError() {
        return noteError;
    }

    public void setNoteError(String noteError) {
        this.noteError = noteError;
        setHasError();
    }

    private void setHasError() {
        hasNoError =
                TextUtils.isEmpty(nameError)
                        && TextUtils.isEmpty(sellingPriceError)
                        && TextUtils.isEmpty(purchasePriceError)
                        && TextUtils.isEmpty(discountPriceError)
                        && TextUtils.isEmpty(descriptionError)
                        && TextUtils.isEmpty(noteError)
                        && TextUtils.isEmpty(brandError)
                        && TextUtils.isEmpty(categoryError);
    }

    public boolean hasNoError() {
        return hasNoError;
    }
}
