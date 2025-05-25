package com.optlab.banhangso.di;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;
import com.optlab.banhangso.data.local.dao.StoreDao;
import com.optlab.banhangso.data.local.dao.UserDao;
import com.optlab.banhangso.data.remote.service.FirebaseStoreService;
import com.optlab.banhangso.data.remote.service.FirebaseUserService;
import com.optlab.banhangso.data.repository.BrandRepositoryImpl;
import com.optlab.banhangso.data.repository.CategoryRepositoryImpl;
import com.optlab.banhangso.data.repository.PreferenceRepositoryImpl;
import com.optlab.banhangso.data.repository.ProductRepositoryImpl;
import com.optlab.banhangso.data.repository.StoreRepositoryImpl;
import com.optlab.banhangso.data.repository.UserRepositoryImpl;
import com.optlab.banhangso.data.repository.qualifier.BrandSortSelection;
import com.optlab.banhangso.data.repository.qualifier.CategorySortSelection;
import com.optlab.banhangso.data.repository.qualifier.ProductSortSelection;
import com.optlab.banhangso.data.repository.references.UserPreferenceManager;
import com.optlab.banhangso.data.repository.sort.BrandSortOptionRepositoryImpl;
import com.optlab.banhangso.data.repository.sort.CategorySortOptionRepositoryImpl;
import com.optlab.banhangso.data.repository.sort.ProductSortOptionRepositoryImpl;
import com.optlab.banhangso.domain.repository.BrandRepository;
import com.optlab.banhangso.domain.repository.CategoryRepository;
import com.optlab.banhangso.domain.repository.PreferenceRepository;
import com.optlab.banhangso.domain.repository.ProductRepository;
import com.optlab.banhangso.domain.repository.SortOptionRepository;
import com.optlab.banhangso.domain.repository.StoreRepository;
import com.optlab.banhangso.domain.repository.UserRepository;
import com.optlab.banhangso.domain.repository.preferences.UserPreferenceStorage;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

import javax.inject.Singleton;

/**
 * @noinspection rawtypes
 */
@Module
@InstallIn(SingletonComponent.class)
public class RepositoryModule {
    @Provides
    @Singleton
    public static BrandRepository provideBrandRepository(FirebaseFirestore firestore) {
        return new BrandRepositoryImpl(firestore);
    }

    @Provides
    @Singleton
    public static CategoryRepository provideCategoryRepository(FirebaseFirestore firestore) {
        return new CategoryRepositoryImpl(firestore);
    }

    @Provides
    @Singleton
    public static ProductRepository provideProductRepository(FirebaseFirestore firestore) {
        return new ProductRepositoryImpl(firestore);
    }

    @Provides
    @Singleton
    @ProductSortSelection
    public static SortOptionRepository provideProductSortOptionRepository() {
        return new ProductSortOptionRepositoryImpl();
    }

    @Provides
    @Singleton
    @BrandSortSelection
    public static SortOptionRepository provideBrandSortOptionRepository() {
        return new BrandSortOptionRepositoryImpl();
    }

    @Provides
    @Singleton
    @CategorySortSelection
    public static SortOptionRepository provideCategorySortOptionRepository() {
        return new CategorySortOptionRepositoryImpl();
    }

    @Provides
    @Singleton
    public static UserPreferenceStorage provideUserPreferenceStorage(
            @ApplicationContext Context context) {
        return new UserPreferenceManager(context);
    }

    @Provides
    @Singleton
    public static PreferenceRepository providePreferenceRepository(
            @NonNull UserPreferenceStorage userPreferenceStorage) {
        return new PreferenceRepositoryImpl(userPreferenceStorage);
    }

    @Provides
    @Singleton
    public static UserRepository provideUserRepository(
            @NonNull UserDao userDao, @NonNull FirebaseUserService firebaseUserService) {
        return new UserRepositoryImpl(userDao, firebaseUserService);
    }

    @Provides
    @Singleton
    public static StoreRepository provideStoreRepository(
            @NonNull StoreDao storeDao,
            @NonNull FirebaseStoreService firebaseStoreService,
            @NonNull PreferenceRepository preferenceRepository) {
        return new StoreRepositoryImpl(storeDao, firebaseStoreService, preferenceRepository);
    }
}
