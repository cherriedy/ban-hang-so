package com.optlab.banhangso.ui.home.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.optlab.banhangso.R;
import com.optlab.banhangso.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;

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

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = NavHostFragment.findNavController(this);
        NavigationUI.setupWithNavController(binding.bnv, navController);
        configureStatusBar();
    }

    private void configureStatusBar() {
        Window window = requireActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.boston_blue));
    }

    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
