package com.optlab.banhangso.di;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;
import com.optlab.banhangso.data.local.dao.StoreDao;
import com.optlab.banhangso.data.reference.UserPreferenceManager;
import com.optlab.banhangso.data.remote.service.FirebaseStoreService;
import com.optlab.banhangso.data.repository.BrandRepository;
import com.optlab.banhangso.data.repository.CategoryRepository;
import com.optlab.banhangso.data.repository.PreferenceRepository;
import com.optlab.banhangso.data.repository.ProductRepository;
import com.optlab.banhangso.data.repository.SortOptionRepository;
import com.optlab.banhangso.data.repository.StoreRepository;
import com.optlab.banhangso.data.repository.UserRepository;
import com.optlab.banhangso.data.repository.impl.BrandRepositoryImpl;
import com.optlab.banhangso.data.repository.impl.CategoryRepositoryImpl;
import com.optlab.banhangso.data.repository.impl.PreferenceRepositoryImpl;
import com.optlab.banhangso.data.repository.impl.ProductRepositoryImpl;
import com.optlab.banhangso.data.repository.impl.StoreRepositoryImpl;
import com.optlab.banhangso.data.repository.impl.UserRepositoryImpl;
import com.optlab.banhangso.data.repository.impl.sort.BrandSortOptionRepositoryImpl;
import com.optlab.banhangso.data.repository.impl.sort.CategorySortOptionRepositoryImpl;
import com.optlab.banhangso.data.repository.impl.sort.ProductSortOptionRepositoryImpl;
import com.optlab.banhangso.data.repository.qualifier.BrandSortSelection;
import com.optlab.banhangso.data.repository.qualifier.CategorySortSelection;
import com.optlab.banhangso.data.repository.qualifier.ProductSortSelection;

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
    public static UserPreferenceManager provideUserPreferenceManager(
            @ApplicationContext Context context) {
        return new UserPreferenceManager(context);
    }

    @Provides
    @Singleton
    public static UserRepository provideUserRepository(@NonNull FirebaseFirestore firestore) {
        return new UserRepositoryImpl(firestore);
    }

    @Provides
    @Singleton
    public static StoreRepository provideStoreRepository(
            @NonNull StoreDao storeDao, @NonNull FirebaseStoreService firebaseStoreService) {
        return new StoreRepositoryImpl(storeDao, firebaseStoreService);
    }

    @Provides
    @Singleton
    public static PreferenceRepository providePreferenceRepository(
            @ApplicationContext Context context) {
        return new PreferenceRepositoryImpl(context);
    }
}
