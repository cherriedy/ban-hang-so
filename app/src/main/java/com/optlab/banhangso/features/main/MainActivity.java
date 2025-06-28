package com.optlab.banhangso.features.main;

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

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(
                binding.getRoot(),
                (v, insets) -> {
                    Insets statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars());
                    ViewGroup.LayoutParams statusViewParams = binding.statusBar.getLayoutParams();
                    statusViewParams.height = statusBarInsets.top;
                    binding.statusBar.setLayoutParams(statusViewParams);

                    Insets navBarInsets =
                            insets.getInsets(WindowInsetsCompat.Type.navigationBars());
                    ViewGroup.LayoutParams navBarViewParams =
                            binding.navigationBar.getLayoutParams();
                    navBarViewParams.height = navBarInsets.bottom;
                    binding.navigationBar.setLayoutParams(navBarViewParams);
                    return insets;
                });
    }
}
