package com.optlab.banhangso.ui.home.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.optlab.banhangso.R;
import com.optlab.banhangso.ui.adapter.QuickShortcutAdapter;
import com.optlab.banhangso.data.model.QuickShortcut;
import com.optlab.banhangso.ui.home.viewmodel.QuickShortcutViewModel;
import com.optlab.banhangso.databinding.FragmentHomeBinding;

import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private QuickShortcutViewModel shortcutViewModel;
    private QuickShortcutAdapter shortcutAdapter;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        configureStatusBar();
        initializeShortcutViewModel();
        observeShortcutViewModel();
    }

    private void configureStatusBar() {
        Window window = requireActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.white));
    }

    private void initializeShortcutViewModel() {
        shortcutViewModel = new ViewModelProvider(this).get(QuickShortcutViewModel.class);
        shortcutViewModel.setShortcutList(Arrays.asList(
                new QuickShortcut("Sản phẩm", R.drawable.ic_product),
                new QuickShortcut("Đơn hàng", R.drawable.ic_note)
        ));
    }

    private void observeShortcutViewModel() {
        shortcutViewModel.getShortcuts().observe(getViewLifecycleOwner(), this::updateShortcuts);

        shortcutViewModel.getClickedPosition().observe(getViewLifecycleOwner(), position -> {
            Toast.makeText(requireContext(), String.valueOf(position), Toast.LENGTH_SHORT).show();
        });
    }

    private void updateShortcuts(@Nullable List<QuickShortcut> shortcuts) {
        if (shortcuts == null || shortcuts.isEmpty()) {
            binding.gridShortcut.setVisibility(View.GONE);
            return;
        }

        if (shortcutAdapter == null) {
            shortcutAdapter = new QuickShortcutAdapter(shortcutViewModel, shortcuts);
            binding.gridShortcut.setAdapter(shortcutAdapter);
        } else {
            shortcutAdapter.setShortcutList(shortcuts);
            shortcutAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}