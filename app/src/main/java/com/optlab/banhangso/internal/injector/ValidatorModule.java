package com.optlab.banhangso.internal.injector;

import android.content.Context;
import com.optlab.banhangso.internal.validators.AuthValidator;
import com.optlab.banhangso.internal.validators.BrandValidator;
import com.optlab.banhangso.internal.validators.CategoryValidator;
import com.optlab.banhangso.internal.validators.ProductValidator;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ViewModelComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;

@Module
@InstallIn(ViewModelComponent.class)
public abstract class ValidatorModule {

    @Provides
    public static ProductValidator provideProductValidator(@ApplicationContext Context context) {
        return new ProductValidator(context);
    }

    @Provides
    public static BrandValidator provideBrandValidator(@ApplicationContext Context context) {
        return new BrandValidator(context);
    }

    @Provides
    public static CategoryValidator provideCategoryValidator(@ApplicationContext Context context) {
        return new CategoryValidator(context);
    }

    @Provides
    public static AuthValidator provideAccountValidator(@ApplicationContext Context context) {
        return new AuthValidator(context);
    }
}
