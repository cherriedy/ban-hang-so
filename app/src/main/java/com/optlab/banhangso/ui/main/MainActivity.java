package com.optlab.banhangso.ui.main;

import android.os.Bundle;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import com.optlab.banhangso.R;
import com.optlab.banhangso.databinding.ActivityMainBinding;

import dagger.hilt.android.AndroidEntryPoint;

import timber.log.Timber;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Timber.tag("Testing").d("MainActivity onCreate called");
        ViewCompat.setOnApplyWindowInsetsListener(
                binding.getRoot(),
                (v, insets) -> {
                    Insets statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars());
                    ViewGroup.LayoutParams statusViewParams = binding.statusBar.getLayoutParams();
                    statusViewParams.height = statusBarInsets.top;
                    binding.statusBar.setLayoutParams(statusViewParams);
                    return insets;
                });
    }
}
