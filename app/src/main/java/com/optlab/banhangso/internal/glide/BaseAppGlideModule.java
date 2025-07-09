package com.optlab.banhangso.internal.glide;

import static com.optlab.banhangso.internal.Config.DEFAULT_TIMEOUT;

import android.content.Context;
import androidx.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.optlab.banhangso.BuildConfig;
import com.optlab.banhangso.R;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import timber.log.Timber;

@GlideModule
public class BaseAppGlideModule extends AppGlideModule {

  private static final String TAG = BaseAppGlideModule.class.getSimpleName();
  private static final int MEMORY_CACHE_SIZE_BYTES = (int) (Runtime.getRuntime().maxMemory() / 8);
  private static final int BITMAP_POOL_SIZE_BYTES = (int) (Runtime.getRuntime().maxMemory() / 16);
  private static final int DISK_CACHE_SIZE_BYTES = 100 * 1024 * 1024; // 100 MB

  private static final OkHttpClient OK_HTTP_CLIENT;

  static {
    OK_HTTP_CLIENT =
        new OkHttpClient.Builder()
            .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build();
  }

  @Override
  public void registerComponents(
      @NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
    registry.replace(
        GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(OK_HTTP_CLIENT));
  }

  @Override
  public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
    // Set default request options for Glide.
    builder.setDefaultRequestOptions(
        new RequestOptions()
            .placeholder(R.drawable.drawable_loading_anim)
            .error(R.drawable.ic_empty)
            .format(DecodeFormat.PREFER_RGB_565)
            .centerCrop());

    // Set memory cache size to 1/8th of the available memory.
    builder.setMemoryCache(new LruResourceCache(MEMORY_CACHE_SIZE_BYTES));

    // Set bitmap pool size to 1/16th of the available memory.
    builder.setBitmapPool(new LruBitmapPool(BITMAP_POOL_SIZE_BYTES));

    // Set disk cache size to 100 MB.
    builder.setDiskCache(new InternalCacheDiskCacheFactory(context, DISK_CACHE_SIZE_BYTES));

    if (BuildConfig.DEBUG) {
      builder.setLogLevel(android.util.Log.DEBUG);
    } else {
      builder.setLogLevel(android.util.Log.ERROR);
    }

    Timber.tag(TAG).d(
        "Glide configuration applied: Memory Cache Size: %d bytes, Bitmap Pool Size: %d bytes, Disk Cache Size: %d bytes",
        MEMORY_CACHE_SIZE_BYTES, BITMAP_POOL_SIZE_BYTES, DISK_CACHE_SIZE_BYTES);
  }

  @Override
  public boolean isManifestParsingEnabled() {
    return false; // Disable manifest parsing for performance
  }
}
