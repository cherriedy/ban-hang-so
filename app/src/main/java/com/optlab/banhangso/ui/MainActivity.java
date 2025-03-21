package com.optlab.banhangso.ui;

import android.os.Bundle;
import android.util.Log;
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

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ActivityMainBinding binding;
    private NavHostFragment navHostFragment;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            addOnDestinationChangedListener();
        } else {
            Log.e(TAG, "Can't find NavHostFragment, check XML again");
            finish();
        }

//        ProductRepository productRepository = ProductRepository.getInstance();
//        ProductEditViewModelFactory editViewModelFactory = new ProductEditViewModelFactory(this, productRepository);
//        ProductEditViewModel productEditViewModel = new ViewModelProvider(this, editViewModelFactory).get(ProductEditViewModel.class);
//
//        Long savedProductId = productEditViewModel.getKeySelectedProduct();
//        if (savedProductId != null) {
//            Bundle args = new Bundle();
//            args.putLong("productId", savedProductId);
//            args.putBoolean("isCreate", false);
//            navController.navigate(R.id.productEditFragment, args);
//        } else {
//            Toast.makeText(this, "Product ID not restored! Navigating to home.", Toast.LENGTH_SHORT).show();
//        }

        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
    }

    private void addOnDestinationChangedListener() {
        navController.addOnDestinationChangedListener((navController, navDestination, bundle) -> {
            int destinationId = navDestination.getId();
            if (destinationId == R.id.productEditFragment || destinationId == R.id.productTabsFragment) {
                binding.bottomNavigation.setVisibility(View.GONE);
            } else {
                binding.bottomNavigation.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item) || NavigationUI.onNavDestinationSelected(item, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}