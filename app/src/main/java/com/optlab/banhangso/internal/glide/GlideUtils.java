package com.optlab.banhangso.internal.glide;

import android.net.Uri;
import android.widget.ImageView;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.optlab.banhangso.R;
import lombok.experimental.UtilityClass;
import timber.log.Timber;

@UtilityClass
public class GlideUtils {

  public static void load(
      @NonNull ImageView view,
      @Nullable String imageUrl,
      @DrawableRes int errorRes,
      @DrawableRes int placeholderRes) {
    if (imageUrl == null || imageUrl.isEmpty()) {
      Timber.w("load: imageUrl is null or empty, using placeholder");
    }

    Glide.with(view.getContext())
        .load(imageUrl)
        .placeholder(placeholderRes)
        .error(errorRes)
        .into(view);
  }

  public static void load(@NonNull ImageView view, @Nullable String imageUrl) {
    Glide.with(view.getContext()).load(imageUrl).into(view);
  }

  public static void loadOrDefault(
      @NonNull ImageView view, @Nullable String imageUrl, @DrawableRes int defaultImageRes) {
    GlideUtils.load(view, imageUrl, defaultImageRes, R.drawable.drawable_loading_anim);
  }

  public static void load(
      @NonNull ImageView view,
      @Nullable Uri imageUri,
      @DrawableRes int errorRes,
      @DrawableRes int placeholderRes) {
    if (imageUri == null) {
      Timber.w("load: imageUri is null, using placeholder");
    }

    Glide.with(view.getContext())
        .load(imageUri)
        .placeholder(placeholderRes)
        .error(errorRes)
        .into(view);
  }

  public static void load(@NonNull ImageView view, @NonNull Uri imageUri) {
    Glide.with(view.getContext()).load(imageUri).into(view);
  }
}
