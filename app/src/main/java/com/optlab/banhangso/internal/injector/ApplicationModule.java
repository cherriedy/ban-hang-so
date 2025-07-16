package com.optlab.banhangso.internal.injector;

import android.content.Context;
import androidx.annotation.NonNull;
import com.optlab.banhangso.internal.utilities.errorhandler.ErrorHandler;
import com.optlab.banhangso.internal.utilities.errorhandler.ErrorHandlerImpl;
import com.optlab.banhangso.internal.utilities.uploaders.qualifiers.ProductImageUploader;
import com.optlab.banhangso.services.ImageUploader;
import com.optlab.banhangso.services.interfaces.ProductService;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import javax.inject.Singleton;

@Module
@InstallIn(SingletonComponent.class)
public abstract class ApplicationModule {

  @NonNull @Provides
  @Singleton
  public static ErrorHandler providErrorHandler() {
    return new ErrorHandlerImpl();
  }

  @NonNull @Provides
  @Singleton
  @ProductImageUploader
  public static ImageUploader provideProductImageUploader(
      @ApplicationContext Context context, ProductService productService) {
    return new ImageUploader(context, productService);
  }
}
