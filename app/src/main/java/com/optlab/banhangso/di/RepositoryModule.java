package com.optlab.banhangso.di;

import android.content.Context;

import com.google.firebase.firestore.FirebaseFirestore;
import com.optlab.banhangso.data.repository.BrandRepository;
import com.optlab.banhangso.data.repository.CategoryRepository;
import com.optlab.banhangso.data.repository.ProductRepository;
import com.optlab.banhangso.data.repository.SortOptionRepository;
import com.optlab.banhangso.data.repository.impl.BrandRepositoryImpl;
import com.optlab.banhangso.data.repository.impl.BrandSortOptionRepositoryImpl;
import com.optlab.banhangso.data.repository.impl.CategoryRepositoryImpl;
import com.optlab.banhangso.data.repository.impl.ProductRepositoryImpl;
import com.optlab.banhangso.data.repository.impl.ProductSortOptionRepositoryImpl;
import com.optlab.banhangso.data.repository.qualifier.BrandSortSelection;
import com.optlab.banhangso.data.repository.qualifier.ProductSortSelection;
import com.optlab.banhangso.util.UserPreferenceManager;

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
    public static FirebaseFirestore provideFirebaseFirestore() {
        return FirebaseFirestore.getInstance();
    }

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
    public static UserPreferenceManager provideUserPreferenceManager(
            @ApplicationContext Context context) {
        return new UserPreferenceManager(context);
    }
}
