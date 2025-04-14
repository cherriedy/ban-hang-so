package com.optlab.banhangso.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.optlab.banhangso.R;
import com.optlab.banhangso.databinding.ActivityMainBinding;

import dagger.hilt.android.AndroidEntryPoint;

import timber.log.Timber;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private NavHostFragment navHostFragment;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(
                binding.getRoot(),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(
                            systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                });

        initNavController();
        initBottomNavigation();
        addOnDestinationChangedListener();
    }

    private void initBottomNavigation() {
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
    }

    private void initNavController() {
        navHostFragment =
                (NavHostFragment)
                        getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        requireNavController();
    }

    /**
     * Ensures that the NavHostFragment is initialized and not null. If it is null, the activity is
     * finished.
     */
    private void requireNavHostFragment() {
        if (navHostFragment == null) {
            Timber.e("NavHostFragment is null");
            finish();
        }
    }

    /**
     * Ensures that the NavController is initialized and not null. If it is null, the activity is
     * finished.
     */
    private void requireNavController() {
        try {
            requireNavHostFragment(); // Ensure NavHostFragment is not null
            navController = navHostFragment.getNavController();
        } catch (NullPointerException e) {
            Timber.e("NavHostFragment is null");
            finish();
        }
    }

    /**
     * Adds a listener to the NavController to handle destination changes and show/hide the bottom
     * navigation bar accordingly.
     */
    private void addOnDestinationChangedListener() {
        navController.addOnDestinationChangedListener(
                (navController, navDestination, bundle) -> {
                    int destinationId = navDestination.getId();
                    if (destinationId == R.id.productEditFragment
                            || destinationId == R.id.productTabHostFragment) {
                        binding.bottomNavigation.setVisibility(View.GONE);
                    } else {
                        binding.bottomNavigation.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item)
                || NavigationUI.onNavDestinationSelected(item, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}
