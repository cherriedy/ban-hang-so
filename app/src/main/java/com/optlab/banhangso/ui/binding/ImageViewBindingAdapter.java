package com.optlab.banhangso.ui.binding;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.optlab.banhangso.R;

public class ImageViewBindingAdapter {
    @BindingAdapter("imageResource")
    public static void setImageResource(ImageView imageView, int imageResource) {
        Glide.with(imageView.getContext())
                .load(imageResource)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_error)
                .into(imageView);
    }
}
