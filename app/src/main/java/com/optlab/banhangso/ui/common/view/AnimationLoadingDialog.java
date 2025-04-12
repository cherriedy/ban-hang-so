package com.optlab.banhangso.ui.common.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.optlab.banhangso.R;

import timber.log.Timber;

public class AnimationLoadingDialog extends DialogFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false); // Disable back button dismiss.
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setCancelable(false); // Disable back button dismiss.
        dialog.setCanceledOnTouchOutside(false); // Disable touch outside dismiss.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // Remove title bar.

        @SuppressLint("InflateParams")
        View view = getLayoutInflater().inflate(R.layout.fragment_animation_loading_dialog, null);
        dialog.setContentView(view);

        Window dialogWindow = dialog.getWindow();
        if (dialogWindow != null) {
            // Set the dialog window to be transparent to show the animation.
            dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } else {
            Timber.e("Dialog window is null");
        }
        return dialog;
    }
}
