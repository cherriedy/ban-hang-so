package com.optlab.banhangso.di;

import android.content.Context;

import com.optlab.banhangso.util.validator.BrandValidator;
import com.optlab.banhangso.util.validator.ProductValidator;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ViewModelComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;

@Module
@InstallIn(ViewModelComponent.class)
public class ValidatorModule {
    @Provides
    public static ProductValidator provideProductValidator(@ApplicationContext Context context) {
        return new ProductValidator(context);
    }

    @Provides
    public static BrandValidator provideBrandValidator(@ApplicationContext Context context) {
        return new BrandValidator(context);
    }
}
