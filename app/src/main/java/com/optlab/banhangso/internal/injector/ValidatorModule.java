package com.optlab.banhangso.internal.injector;

import android.content.Context;
import androidx.annotation.NonNull;
import com.optlab.banhangso.features.main.authentication.AuthValidator;
import com.optlab.banhangso.features.main.brand.BrandValidator;
import com.optlab.banhangso.features.main.category.CategoryValidator;
import com.optlab.banhangso.features.main.customer.CustomerValidator;
import com.optlab.banhangso.features.main.product.ProductValidator;
import com.optlab.banhangso.features.main.staff.StaffValidator;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ViewModelComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import org.jetbrains.annotations.Contract;

@Module
@InstallIn(ViewModelComponent.class)
public abstract class ValidatorModule {

  @NonNull @Contract("_ -> new")
  @Provides
  public static ProductValidator provideProductValidator(@ApplicationContext Context context) {
    return new ProductValidator(context);
  }

  @NonNull @Contract("_ -> new")
  @Provides
  public static BrandValidator provideBrandValidator(@ApplicationContext Context context) {
    return new BrandValidator(context);
  }

  @NonNull @Contract("_ -> new")
  @Provides
  public static CategoryValidator provideCategoryValidator(@ApplicationContext Context context) {
    return new CategoryValidator(context);
  }

  @NonNull @Contract("_ -> new")
  @Provides
  public static AuthValidator provideAccountValidator(@ApplicationContext Context context) {
    return new AuthValidator(context);
  }

  @NonNull @Contract("_ -> new")
  @Provides
  public static StaffValidator provideStaffValidator(@ApplicationContext Context context) {
    return new StaffValidator(context);
  }

  @NonNull @Contract("_ -> new")
  @Provides
  public static CustomerValidator provideCustomerValidator(@ApplicationContext Context context) {
    return new CustomerValidator(context);
  }
}
