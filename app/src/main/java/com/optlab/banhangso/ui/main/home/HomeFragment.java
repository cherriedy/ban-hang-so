package com.optlab.banhangso.ui.main.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.auth.FirebaseAuth;
import com.optlab.banhangso.R;
import com.optlab.banhangso.data.local.database.BanHangSoDatabase;
import com.optlab.banhangso.databinding.FragmentHomeBinding;
import com.optlab.banhangso.domain.repository.PreferenceRepository;

import dagger.hilt.android.AndroidEntryPoint;

import timber.log.Timber;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

@AndroidEntryPoint
public class HomeFragment extends Fragment {
    @Inject protected FirebaseAuth firebaseAuth;
    @Inject protected PreferenceRepository preferenceRepository;
    @Inject protected BanHangSoDatabase database;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setPrefRepo(preferenceRepository);
        binding.setFragment(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = NavHostFragment.findNavController(this);
        NavigationUI.setupWithNavController(binding.bnv, navController);
        setCurrentDate();
    }

    @SuppressLint("SetTextI18n")
    private void setCurrentDate() {
        ZoneId zoneId = ZoneId.of(TimeZone.getDefault().getID());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String currentDate = LocalDate.now(zoneId).format(formatter);
        binding.tvDay.setText(getString(R.string.today) + ": " + currentDate);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void onLogOutImageViewClick(View view) {
        if (firebaseAuth.getCurrentUser() != null) {
            firebaseAuth.signOut();
            preferenceRepository.clearAllPreferences();

            executor.execute(
                    () -> {
                        try {
                            database.clearAllData();
                            Timber.i("Room database cleared successfully during logout");
                        } catch (Exception e) {
                            Timber.e(e, "Error clearing Room database during logout");
                        }
                    });
        }

        Navigation.findNavController(view)
                .navigate(
                        R.id.action_to_authentication,
                        null,
                        new NavOptions.Builder().setPopUpTo(R.id.homeFragment, true).build());
    }

    @Override
    public void onDestroy() {
        if (!executor.isShutdown()) {
            executor.shutdown();
        }
        super.onDestroy();
    }

    public static class QuickShortcutClickHandler {
        public void onSellShortcutClick(View view) {}

        public void onOrderShortcutClick(View view) {}

        public void onCustomerShortcutClick(View view) {}

        public void onReportShortcutClick(View view) {}

        public void onWarehouseShortcutClick(View view) {}

        public void onMoreShortcutClick(View view) {}

        public void onEmployeeShortcutClick(View view) {}

        public void onStoreShortcutClick(View view) {}
    }
}
