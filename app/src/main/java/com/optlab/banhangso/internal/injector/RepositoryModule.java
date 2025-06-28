package com.optlab.banhangso.internal.injector;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;
import com.optlab.banhangso.internal.utilities.errorhandler.ErrorHandler;
import com.optlab.banhangso.repositories.AuthRepositoryImpl;
import com.optlab.banhangso.repositories.BrandRepositoryImpl;
import com.optlab.banhangso.repositories.CategoryRepositoryImpl;
import com.optlab.banhangso.repositories.PreferenceRepositoryImpl;
import com.optlab.banhangso.repositories.PreferencesRepositoryImpl;
import com.optlab.banhangso.repositories.ProductRepositoryImpl;
import com.optlab.banhangso.repositories.StoreRepositoryImpl;
import com.optlab.banhangso.repositories.UserRepositoryImpl;
import com.optlab.banhangso.repositories.interfaces.AuthRepository;
import com.optlab.banhangso.repositories.interfaces.BrandRepository;
import com.optlab.banhangso.repositories.interfaces.CategoryRepository;
import com.optlab.banhangso.repositories.interfaces.PreferenceRepository;
import com.optlab.banhangso.repositories.interfaces.ProductRepository;
import com.optlab.banhangso.repositories.interfaces.SortOptionRepository;
import com.optlab.banhangso.repositories.interfaces.StoreRepository;
import com.optlab.banhangso.repositories.interfaces.UserRepository;
import com.optlab.banhangso.repositories.interfaces.preferences.AppPreferences;
import com.optlab.banhangso.repositories.interfaces.preferences.PreferencesRepository;
import com.optlab.banhangso.repositories.perferences.AppPreferencesImpl;
import com.optlab.banhangso.repositories.sortoption.BrandSortOptionRepositoryImpl;
import com.optlab.banhangso.repositories.sortoption.CategorySortOptionRepositoryImpl;
import com.optlab.banhangso.repositories.sortoption.ProductSortOptionRepositoryImpl;
import com.optlab.banhangso.repositories.sortoption.qualifier.BrandSortSelection;
import com.optlab.banhangso.repositories.sortoption.qualifier.CategorySortSelection;
import com.optlab.banhangso.repositories.sortoption.qualifier.ProductSortSelection;
import com.optlab.banhangso.services.interfaces.FirebaseAuthService;
import com.optlab.banhangso.services.interfaces.FirebaseStoreService;
import com.optlab.banhangso.services.interfaces.FirebaseUserService;
import com.optlab.banhangso.services.interfaces.RenderProductService;
import com.optlab.banhangso.services.interfaces.RenderStoreService;

import org.jetbrains.annotations.Contract;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

/**
 * @noinspection rawtypes
 */
@Module
@InstallIn(SingletonComponent.class)
public abstract class RepositoryModule {

    private RepositoryModule() {}

    @NonNull @Contract("_ -> new")
    @Provides
    @Singleton
    public static BrandRepository provideBrandRepository(FirebaseFirestore firestore) {
        return new BrandRepositoryImpl(firestore);
    }

    @NonNull @Contract("_ -> new")
    @Provides
    @Singleton
    public static CategoryRepository provideCategoryRepository(FirebaseFirestore firestore) {
        return new CategoryRepositoryImpl(firestore);
    }

    @NonNull @Contract("_, _ -> new")
    @Provides
    @Singleton
    public static ProductRepository provideProductRepository(
            RenderProductService renderProductService, ErrorHandler errorHandler) {
        return new ProductRepositoryImpl(renderProductService, errorHandler);
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

    @NonNull @Contract(value = "_ -> new", pure = true)
    @Provides
    @Singleton
    public static PreferenceRepository providePreferenceRepository(
            @NonNull AppPreferences appPreferences) {
        return new PreferenceRepositoryImpl(appPreferences);
    }

    @NonNull
    @Contract("_ -> new")
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
            RenderStoreService renderStoreService,
            ErrorHandler errorHandler) {
        return new StoreRepositoryImpl(firebaseStoreService, renderStoreService, errorHandler);
    }

    @NonNull @Contract(value = "_, _, _, _ -> new", pure = true)
    @Provides
    @Singleton
    public static AuthRepository provideAuthRepository(
            FirebaseAuthService firebaseAuthService,
            PreferenceRepository preferenceRepository,
            UserRepository userRepository,
            ErrorHandler errorHandler) {
        return new AuthRepositoryImpl(
                firebaseAuthService, preferenceRepository, userRepository, errorHandler);
    }
}
