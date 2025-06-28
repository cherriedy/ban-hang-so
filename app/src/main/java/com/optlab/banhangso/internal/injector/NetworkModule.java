package com.optlab.banhangso.internal.injector;

import androidx.annotation.NonNull;

import com.optlab.banhangso.internal.inteceptors.AuthenticationInterceptor;
import com.optlab.banhangso.services.interfaces.RenderProductService;
import com.optlab.banhangso.services.interfaces.RenderStoreService;

import org.jetbrains.annotations.Contract;

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

    private static final long TIMEOUT = 30L;

    private NetworkModule() {
    }

    @NonNull
    @Contract(" -> new")
    @Provides
    @Singleton
    public static HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    @NonNull
    @Contract("_ -> new")
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

    @NonNull
    @Contract("_ -> new")
    @Provides
    @Singleton
    public static Retrofit provideRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://ban-hang-so-api.onrender.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
    }

    @NonNull
    @Provides
    @Singleton
    public static RenderStoreService provideRenderStoreService(@NonNull Retrofit retrofit) {
        return retrofit.create(RenderStoreService.class);
    }

    @NonNull
    @Provides
    @Singleton
    public static RenderProductService provideRenderProductService(@NonNull Retrofit retrofit) {
        return retrofit.create(RenderProductService.class);
    }
}
