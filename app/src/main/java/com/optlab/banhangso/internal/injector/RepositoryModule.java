package com.optlab.banhangso.internal.injector;

import android.content.Context;
import androidx.annotation.NonNull;
import com.optlab.banhangso.internal.utilities.errorhandler.ErrorHandler;
import com.optlab.banhangso.repositories.AuthRepositoryImpl;
import com.optlab.banhangso.repositories.BrandRepositoryImpl;
import com.optlab.banhangso.repositories.CategoryRepositoryImpl;
import com.optlab.banhangso.repositories.CustomerRepositoryImpl;
import com.optlab.banhangso.repositories.PreferencesRepositoryImpl;
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
import com.optlab.banhangso.repositories.interfaces.ProductRepository;
import com.optlab.banhangso.repositories.interfaces.ProductSaleRepository;
import com.optlab.banhangso.repositories.interfaces.ReportRepository;
import com.optlab.banhangso.repositories.interfaces.SortOptionRepository;
import com.optlab.banhangso.repositories.interfaces.StaffRepository;
import com.optlab.banhangso.repositories.interfaces.StoreRepository;
import com.optlab.banhangso.repositories.interfaces.TransactionRepository;
import com.optlab.banhangso.repositories.interfaces.UserRepository;
import com.optlab.banhangso.repositories.interfaces.preferences.AppPreferences;
import com.optlab.banhangso.repositories.perferences.AppPreferencesImpl;
import com.optlab.banhangso.repositories.sortoption.BrandSortOptionRepositoryImpl;
import com.optlab.banhangso.repositories.sortoption.CategorySortOptionRepositoryImpl;
import com.optlab.banhangso.repositories.sortoption.ProductSortOptionRepositoryImpl;
import com.optlab.banhangso.repositories.sortoption.qualifier.BrandSortSelection;
import com.optlab.banhangso.repositories.sortoption.qualifier.CategorySortSelection;
import com.optlab.banhangso.repositories.sortoption.qualifier.ProductSortSelection;
import com.optlab.banhangso.services.TransactionService;
import com.optlab.banhangso.services.interfaces.AuthenticationService;
import com.optlab.banhangso.services.interfaces.BrandService;
import com.optlab.banhangso.services.interfaces.CategoryService;
import com.optlab.banhangso.services.interfaces.CustomerService;
import com.optlab.banhangso.services.interfaces.FirebaseAuthService;
import com.optlab.banhangso.services.interfaces.FirebaseStoreService;
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
import org.jetbrains.annotations.Contract;

/**
 * @noinspection rawtypes
 */
@Module
@InstallIn(SingletonComponent.class)
public abstract class RepositoryModule {

  private RepositoryModule() {}

  @NonNull @Contract("_, _, _ -> new")
  @Provides
  @Singleton
  public static BrandRepository provideBrandRepository(
      PreferencesRepository preferencesRepository,
      BrandService brandService,
      ErrorHandler errorHandler) {
    return new BrandRepositoryImpl(preferencesRepository, brandService, errorHandler);
  }

  @NonNull @Contract("_, _, _ -> new")
  @Provides
  @Singleton
  public static CategoryRepository provideCategoryRepository(
      PreferencesRepository preferencesRepository,
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
      PreferencesRepository preferencesRepository) {
    return new ProductRepositoryImpl(productService, preferencesRepository, errorHandler);
  }

  @NonNull @Contract(value = " -> new", pure = true)
  @Provides
  @Singleton
  @ProductSortSelection
  public static SortOptionRepository provideProductSortOptionRepository() {
    return new ProductSortOptionRepositoryImpl();
  }

  @NonNull @Contract(" -> new")
  @Provides
  @Singleton
  @BrandSortSelection
  public static SortOptionRepository provideBrandSortOptionRepository() {
    return new BrandSortOptionRepositoryImpl();
  }

  @NonNull @Contract(" -> new")
  @Provides
  @Singleton
  @CategorySortSelection
  public static SortOptionRepository provideCategorySortOptionRepository() {
    return new CategorySortOptionRepositoryImpl();
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
      @NonNull FirebaseStoreService firebaseStoreService,
      StoreService storeService,
      ErrorHandler errorHandler) {
    return new StoreRepositoryImpl(firebaseStoreService, storeService, errorHandler);
  }

  @NonNull @Contract(value = "_, _, _, _, _ -> new", pure = true)
  @Provides
  @Singleton
  public static AuthRepository provideAuthRepository(
      FirebaseAuthService firebaseAuthService,
      AuthenticationService authenticationService,
      PreferencesRepository preferenceRepository,
      UserRepository userRepository,
      ErrorHandler errorHandler) {
    return new AuthRepositoryImpl(
        firebaseAuthService,
        authenticationService,
        preferenceRepository,
        userRepository,
        errorHandler);
  }

  @NonNull @Contract("_, _, _ -> new")
  @Provides
  @Singleton
  public static StaffRepository provideStaffRepository(
      StaffService staffService,
      ErrorHandler errorHandler,
      PreferencesRepository preferencesRepository) {
    return new StaffRepositoryImpl(staffService, errorHandler, preferencesRepository);
  }

  @NonNull @Contract("_, _, _ -> new")
  @Provides
  @Singleton
  public static CustomerRepository provideCustomerRepository(
      CustomerService customerService,
      ErrorHandler errorHandler,
      PreferencesRepository preferencesRepository) {
    return new CustomerRepositoryImpl(customerService, errorHandler, preferencesRepository);
  }

  @NonNull @Contract("_, _ -> new")
  @Provides
  @Singleton
  public static ProductSaleRepository provideProductSaleRepository(
      PreferencesRepository preferencesRepository, ProductSaleService productSaleService) {
    return new ProductSaleRepositoryImpl(preferencesRepository, productSaleService);
  }

  @NonNull @Contract("_, _, _ -> new")
  @Provides
  @Singleton
  public static TransactionRepository provideTransactionRepository(
      PreferencesRepository preferencesRepository,
      TransactionService transactionService,
      ErrorHandler errorHandler) {
    return new TransactionRepositoryImpl(preferencesRepository, transactionService, errorHandler);
  }

  @NonNull @Contract("_, _, _ -> new")
  @Provides
  @Singleton
  public static ReportRepository provideReportRepository(
      PreferencesRepository preferencesRepository,
      ReportService transactionService,
      ErrorHandler errorHandler) {
    return new ReportRepositoryImpl(transactionService, errorHandler, preferencesRepository);
  }
}
