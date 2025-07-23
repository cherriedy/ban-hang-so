package com.optlab.banhangso.features.main.activity;

import android.os.Bundle;
import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
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
    SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
    SharedViewModel viewModel = new ViewModelProvider(this).get(SharedViewModel.class);
    viewModel
        .isChecking()
        .observe(
            this,
            value -> {
              Timber.d("SplashScreen keepOnScreen condition: %s", value);
              splashScreen.setKeepOnScreenCondition(() -> value);
            });

    binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
    ViewCompat.setOnApplyWindowInsetsListener(
        binding.getRoot(),
        (v, insets) -> {
          Insets statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars());
          ViewGroup.LayoutParams statusViewParams = binding.statusBar.getLayoutParams();
          statusViewParams.height = statusBarInsets.top;
          binding.statusBar.setLayoutParams(statusViewParams);

          Insets navBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
          ViewGroup.LayoutParams navBarViewParams = binding.navigationBar.getLayoutParams();
          navBarViewParams.height = navBarInsets.bottom;
          binding.navigationBar.setLayoutParams(navBarViewParams);
          return insets;
        });
  }
}
