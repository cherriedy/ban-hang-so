package com.optlab.banhangso.internal.injector;

import androidx.annotation.NonNull;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.optlab.banhangso.internal.gson.DateTimeDeserializer;
import com.optlab.banhangso.internal.inteceptors.AuthenticationInterceptor;
import com.optlab.banhangso.services.interfaces.AuthenticationService;
import com.optlab.banhangso.services.interfaces.ProductService;
import com.optlab.banhangso.services.interfaces.StoreService;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.inject.Singleton;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.jetbrains.annotations.Contract;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public abstract class NetworkModule {

  private static final long TIMEOUT = 30L;

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
        .callTimeout(TIMEOUT, TimeUnit.SECONDS)
        .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build();
  }

  @NonNull @Contract(" -> new")
  @Provides
  @Singleton
  public static Gson provideGson() {
    return new GsonBuilder().registerTypeAdapter(Date.class, new DateTimeDeserializer()).create();
  }

  @NonNull @Contract("_, _ -> new")
  @Provides
  @Singleton
  public static Retrofit provideRetrofit(OkHttpClient okHttpClient, Gson gson) {
    return new Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl("https://ban-hang-so-api.onrender.com")
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
}
