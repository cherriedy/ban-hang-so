package com.optlab.banhangso.ui.base.binding;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.optlab.banhangso.R;

import timber.log.Timber;

public class ImageViewBindingAdapter {
    @BindingAdapter(
            value = {"imageResource", "defaultImageResource"},
            requireAll = false)
    public static void setImageResource(
            ImageView imageView, int imageResource, int defaultImageResource) {
        if (imageResource == 0) {
            imageView.setImageResource(defaultImageResource);
            return;
        }
        Glide.with(imageView.getContext())
                .load(imageResource)
                .placeholder(R.drawable.drawable_loading_animation)
                .error(R.drawable.ic_empty)
                .into(imageView);
    }

    @BindingAdapter(
            value = {"imageResource", "defaultImageResource"},
            requireAll = false)
    public static void setImageResource(
            ImageView imageView, String imageUrl, Integer defaultImageResource) {
        if (defaultImageResource != null && (imageUrl == null || imageUrl.isEmpty())) {
            imageView.setImageResource(defaultImageResource);
            return;
        }
        Glide.with(imageView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.drawable_loading_animation)
                .error(R.drawable.ic_empty)
                .into(imageView);
    }

    @BindingAdapter(
            value = {"imageResource", "displayText"},
            requireAll = false)
    public static void setImageResource(ImageView imageView, String imageUrl, String displayText) {
        Timber.d("setImageResource: imageUrl=%s, displayText=%s", imageUrl, displayText);
        if (displayText != null && (imageUrl == null || imageUrl.isEmpty())) {
            String diceBearAvatar =
                    "https://api.dicebear.com/7.x/initials/png?seed="
                            + displayText
                            + "&backgroundColor=c0aede,d1d4f9,ffd5dc,ffdfbf";
            Glide.with(imageView.getContext())
                    .load(diceBearAvatar)
                    .placeholder(R.drawable.drawable_loading_animation)
                    .into(imageView);
        } else {
            Glide.with(imageView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.drawable_loading_animation)
                    .error(R.drawable.ic_empty)
                    .into(imageView);
        }
    }
}
