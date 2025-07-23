package com.optlab.banhangso.internal.injector;

import android.content.Context;
import androidx.annotation.NonNull;
import com.optlab.banhangso.internal.utilities.errorhandler.ErrorHandler;
import com.optlab.banhangso.repositories.AuthRepositoryImpl;
import com.optlab.banhangso.repositories.BrandRepositoryImpl;
import com.optlab.banhangso.repositories.CategoryRepositoryImpl;
import com.optlab.banhangso.repositories.CustomerRepositoryImpl;
import com.optlab.banhangso.repositories.PreferencesRepositoryImpl;
import com.optlab.banhangso.repositories.PreferencesRepositoryKtImpl;
import com.optlab.banhangso.repositories.ProductRepositoryImpl;
import com.optlab.banhangso.repositories.ProductSaleRepositoryImpl;
import com.optlab.banhangso.repositories.ReportRepositoryImpl;
import com.optlab.banhangso.repositories.StaffRepositoryImpl;
import com.optlab.banhangso.repositories.StoreRepositoryImpl;
import com.optlab.banhangso.repositories.TransactionRepositoryImpl;
import com.optlab.banhangso.repositories.UserRepositoryImpl;
import com.optlab.banhangso.repositories.interfaces.AuthRepository;
import com.optlab.banhangso.repositories.interfaces.BrandRepository;
import com.optlab.banhangso.repositories.interfaces.CategoryRepository;
import com.optlab.banhangso.repositories.interfaces.CustomerRepository;
import com.optlab.banhangso.repositories.interfaces.PreferencesRepository;
import com.optlab.banhangso.repositories.interfaces.PreferencesRepositoryKt;
import com.optlab.banhangso.repositories.interfaces.ProductRepository;
import com.optlab.banhangso.repositories.interfaces.ProductSaleRepository;
import com.optlab.banhangso.repositories.interfaces.ReportRepository;
import com.optlab.banhangso.repositories.interfaces.StaffRepository;
import com.optlab.banhangso.repositories.interfaces.StoreRepository;
import com.optlab.banhangso.repositories.interfaces.TransactionRepository;
import com.optlab.banhangso.repositories.interfaces.UserRepository;
import com.optlab.banhangso.repositories.interfaces.preferences.AppPreferences;
import com.optlab.banhangso.repositories.interfaces.preferences.AppPreferencesKt;
import com.optlab.banhangso.repositories.perferences.AppPreferencesImpl;
import com.optlab.banhangso.services.TransactionService;
import com.optlab.banhangso.services.interfaces.AuthenticationService;
import com.optlab.banhangso.services.interfaces.BrandService;
import com.optlab.banhangso.services.interfaces.CategoryService;
import com.optlab.banhangso.services.interfaces.CustomerService;
import com.optlab.banhangso.services.interfaces.FirebaseAuthService;
import com.optlab.banhangso.services.interfaces.FirebaseUserService;
import com.optlab.banhangso.services.interfaces.ProductSaleService;
import com.optlab.banhangso.services.interfaces.ProductService;
import com.optlab.banhangso.services.interfaces.ReportService;
import com.optlab.banhangso.services.interfaces.StaffService;
import com.optlab.banhangso.services.interfaces.StoreService;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import javax.inject.Singleton;
import kotlinx.coroutines.DelicateCoroutinesApi;
import org.jetbrains.annotations.Contract;

@Module
@InstallIn(SingletonComponent.class)
public abstract class RepositoryModule {

  private RepositoryModule() {}

  @NonNull @Contract("_, _, _ -> new")
  @Provides
  @Singleton
  public static BrandRepository provideBrandRepository(
      PreferencesRepositoryKt preferencesRepository,
      BrandService brandService,
      ErrorHandler errorHandler) {
    return new BrandRepositoryImpl(preferencesRepository, brandService, errorHandler);
  }

  @NonNull @Contract("_, _, _ -> new")
  @Provides
  @Singleton
  public static CategoryRepository provideCategoryRepository(
      PreferencesRepositoryKt preferencesRepository,
      CategoryService categoryService,
      ErrorHandler errorHandler) {
    return new CategoryRepositoryImpl(preferencesRepository, categoryService, errorHandler);
  }

  @NonNull @Contract("_, _, _ -> new")
  @Provides
  @Singleton
  public static ProductRepository provideProductRepository(
      ProductService productService,
      ErrorHandler errorHandler,
      PreferencesRepositoryKt preferencesRepository) {
    return new ProductRepositoryImpl(productService, preferencesRepository, errorHandler);
  }

  @NonNull @Contract("_ -> new")
  @Provides
  @Singleton
  public static AppPreferences provideUserPreferenceStorage(@ApplicationContext Context context) {
    return new AppPreferencesImpl(context);
  }

  @NonNull @Contract("_ -> new")
  @Provides
  @Singleton
  public static PreferencesRepository providePreferencesRepository(AppPreferences appPreferences) {
    return new PreferencesRepositoryImpl(appPreferences);
  }

  @NonNull @Contract(value = "_, _ -> new", pure = true)
  @Provides
  @Singleton
  public static UserRepository provideUserRepository(
      @NonNull FirebaseUserService firebaseUserService, ErrorHandler errorHandler) {
    return new UserRepositoryImpl(firebaseUserService, errorHandler);
  }

  @NonNull @Contract(value = "_, _, _ -> new", pure = true)
  @Provides
  @Singleton
  public static StoreRepository provideStoreRepository(
      PreferencesRepositoryKt preferencesRepositoryKt,
      StoreService storeService,
      ErrorHandler errorHandler) {
    return new StoreRepositoryImpl(preferencesRepositoryKt, storeService, errorHandler);
  }

  @NonNull @Contract(value = "_, _, _, _, _ -> new", pure = true)
  @Provides
  @Singleton
  public static AuthRepository provideAuthRepository(
      FirebaseAuthService firebaseAuthService,
      AuthenticationService authenticationService,
      PreferencesRepositoryKt preferencesRepositoryKt,
      UserRepository userRepository,
      ErrorHandler errorHandler) {
    return new AuthRepositoryImpl(
        firebaseAuthService,
        authenticationService,
        preferencesRepositoryKt,
        userRepository,
        errorHandler);
  }

  @NonNull @Contract("_, _, _ -> new")
  @Provides
  @Singleton
  public static StaffRepository provideStaffRepository(
      StaffService staffService,
      ErrorHandler errorHandler,
      PreferencesRepositoryKt preferencesRepository) {
    return new StaffRepositoryImpl(staffService, errorHandler, preferencesRepository);
  }

  @NonNull @Contract("_, _, _ -> new")
  @Provides
  @Singleton
  public static CustomerRepository provideCustomerRepository(
      CustomerService customerService,
      ErrorHandler errorHandler,
      PreferencesRepositoryKt preferencesRepository) {
    return new CustomerRepositoryImpl(customerService, errorHandler, preferencesRepository);
  }

  @NonNull @Contract("_, _ -> new")
  @Provides
  @Singleton
  public static ProductSaleRepository provideProductSaleRepository(
      PreferencesRepositoryKt preferencesRepository, ProductSaleService productSaleService) {
    return new ProductSaleRepositoryImpl(preferencesRepository, productSaleService);
  }

  @NonNull @Contract("_, _, _ -> new")
  @Provides
  @Singleton
  public static TransactionRepository provideTransactionRepository(
      PreferencesRepositoryKt preferencesRepository,
      TransactionService transactionService,
      ErrorHandler errorHandler) {
    return new TransactionRepositoryImpl(preferencesRepository, transactionService, errorHandler);
  }

  @NonNull @Contract("_, _, _ -> new")
  @Provides
  @Singleton
  public static ReportRepository provideReportRepository(
      ReportService transactionService,
      ErrorHandler errorHandler,
      PreferencesRepositoryKt preferencesRepository) {
    return new ReportRepositoryImpl(transactionService, errorHandler, preferencesRepository);
  }

  @DelicateCoroutinesApi
  @NonNull @Contract("_ -> new")
  @Provides
  @Singleton
  public static PreferencesRepositoryKt providePreferencesRepositoryKt(
      @NonNull AppPreferencesKt appPreferencesKt) {
    return new PreferencesRepositoryKtImpl(appPreferencesKt);
  }
}
