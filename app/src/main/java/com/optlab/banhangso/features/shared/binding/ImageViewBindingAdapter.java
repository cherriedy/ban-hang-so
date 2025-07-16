package com.optlab.banhangso.features.shared.binding;

import android.widget.ImageView;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import com.bumptech.glide.Glide;
import com.optlab.banhangso.R;
import com.optlab.banhangso.internal.glide.GlideUtils;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ImageViewBindingAdapter {

  @BindingAdapter(
      value = {"imageResource", "defaultImageResource"},
      requireAll = false)
  public static void setImageResource(
      @NonNull ImageView imageView, @Nullable String imageUrl, @DrawableRes int defaultImageRes) {
    GlideUtils.loadOrDefault(imageView, imageUrl, defaultImageRes);
  }

  @BindingAdapter(value = "imageResource")
  public static void setImageResource(@NonNull ImageView imageView, @DrawableRes int imageResId) {
    GlideUtils.load(imageView, imageResId);
  }

  @BindingAdapter(
      value = {"imageResource", "displayText"},
      requireAll = false)
  public static void setImageResource(ImageView imageView, String imageUrl, String displayText) {
    if (displayText != null && (imageUrl == null || imageUrl.isEmpty())) {
      String diceBearAvatar =
          "https://api.dicebear.com/7.x/initials/png?seed="
              + displayText
              + "&backgroundColor=c0aede,d1d4f9,ffd5dc,ffdfbf";
      Glide.with(imageView.getContext())
          .load(diceBearAvatar)
          .placeholder(R.drawable.drawable_loading_anim)
          .into(imageView);
    } else {
      Glide.with(imageView.getContext())
          .load(imageUrl)
          .placeholder(R.drawable.drawable_loading_anim)
          .error(R.drawable.ic_empty)
          .into(imageView);
    }
  }
}
