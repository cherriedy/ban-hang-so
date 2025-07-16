package com.optlab.banhangso.internal.injector;

import static com.optlab.banhangso.internal.Config.DEFAULT_TIMEOUT;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.optlab.banhangso.internal.network.DateTypeAdapter;
import com.optlab.banhangso.internal.network.inteceptors.AuthenticationInterceptor;
import com.optlab.banhangso.services.TransactionService;
import com.optlab.banhangso.services.interfaces.AuthenticationService;
import com.optlab.banhangso.services.interfaces.BrandService;
import com.optlab.banhangso.services.interfaces.CategoryService;
import com.optlab.banhangso.services.interfaces.CustomerService;
import com.optlab.banhangso.services.interfaces.ProductSaleService;
import com.optlab.banhangso.services.interfaces.ProductService;
import com.optlab.banhangso.services.interfaces.ReportService;
import com.optlab.banhangso.services.interfaces.StaffService;
import com.optlab.banhangso.services.interfaces.StoreService;

import org.jetbrains.annotations.Contract;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public abstract class NetworkModule {

  private NetworkModule() {}

  @NonNull @Contract(" -> new")
  @Provides
  @Singleton
  public static HttpLoggingInterceptor provideHttpLoggingInterceptor() {
    return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
  }

  @NonNull @Contract("_ -> new")
  @Provides
  @Singleton
  public static OkHttpClient provideOkHttpClient(HttpLoggingInterceptor httpLoggingInterceptor) {
    return new OkHttpClient.Builder()
        .addInterceptor(httpLoggingInterceptor)
        .addInterceptor(new AuthenticationInterceptor())
        .callTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
        .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build();
  }

  @NonNull @Contract(" -> new")
  @Provides
  @Singleton
  public static Gson provideGson() {
    return new GsonBuilder().registerTypeAdapter(Date.class, new DateTypeAdapter()).create();
  }

  @NonNull @Contract("_, _ -> new")
  @Provides
  @Singleton
  public static Retrofit provideRetrofit(OkHttpClient okHttpClient, Gson gson) {
    return new Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl("https://ban-hang-so-api.onrender.com")
        .baseUrl("http://10.0.2.2:8000")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build();
  }

  @NonNull @Provides
  @Singleton
  public static StoreService provideStoreService(@NonNull Retrofit retrofit) {
    return retrofit.create(StoreService.class);
  }

  @NonNull @Provides
  @Singleton
  public static ProductService provideProductService(@NonNull Retrofit retrofit) {
    return retrofit.create(ProductService.class);
  }

  @NonNull @Provides
  @Singleton
  public static AuthenticationService provideAuthenticationService(@NonNull Retrofit retrofit) {
    return retrofit.create(AuthenticationService.class);
  }

  @NonNull @Provides
  @Singleton
  public static StaffService provideStaffService(@NonNull Retrofit retrofit) {
    return retrofit.create(StaffService.class);
  }

  @NonNull @Provides
  @Singleton
  public static CustomerService provideCustomerService(@NonNull Retrofit retrofit) {
    return retrofit.create(CustomerService.class);
  }

  @NonNull @Provides
  @Singleton
  public static CategoryService provideCategoryService(@NonNull Retrofit retrofit) {
    return retrofit.create(CategoryService.class);
  }

  @NonNull @Provides
  @Singleton
  public static BrandService provideBrandService(@NonNull Retrofit retrofit) {
    return retrofit.create(BrandService.class);
  }

  @NonNull @Provides
  @Singleton
  public static ProductSaleService provideSaleService(@NonNull Retrofit retrofit) {
    return retrofit.create(ProductSaleService.class);
  }

  @NonNull @Provides
  @Singleton
  public static TransactionService provideTransactionService(@NonNull Retrofit retrofit) {
    return retrofit.create(TransactionService.class);
  }

  @NonNull @Provides
  @Singleton
  public static ReportService provideReportService(@NonNull Retrofit retrofit) {
    return retrofit.create(ReportService.class);
  }
}
