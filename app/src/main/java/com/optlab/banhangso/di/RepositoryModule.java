package com.optlab.banhangso.di;

import com.google.firebase.firestore.FirebaseFirestore;
import com.optlab.banhangso.data.repository.BrandRepository;
import com.optlab.banhangso.data.repository.CategoryRepository;
import com.optlab.banhangso.data.repository.ProductRepository;
import com.optlab.banhangso.data.repository.impl.CategoryRepositoryImpl;
import com.optlab.banhangso.data.repository.impl.ProductRepositoryImpl;
import com.optlab.banhangso.data.repository.impl.BrandRepositoryImpl;
import com.optlab.banhangso.data.repository.ProductSortOptionRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

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
  public static ProductSortOptionRepository provideProductSortOptionRepository() {
    return new ProductSortOptionRepository();
  }
}
