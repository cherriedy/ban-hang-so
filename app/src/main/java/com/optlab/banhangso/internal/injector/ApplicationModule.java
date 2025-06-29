package com.optlab.banhangso.internal.injector;

import androidx.annotation.NonNull;
import com.optlab.banhangso.internal.utilities.errorhandler.ErrorHandler;
import com.optlab.banhangso.internal.utilities.errorhandler.ErrorHandlerImpl;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import javax.inject.Singleton;
import org.jetbrains.annotations.Contract;

@Module
@InstallIn(SingletonComponent.class)
public abstract class ApplicationModule {

  @NonNull @Contract(" -> new")
  @Provides
  @Singleton
  public static ErrorHandler providErrorHandler() {
    return new ErrorHandlerImpl();
  }
}
