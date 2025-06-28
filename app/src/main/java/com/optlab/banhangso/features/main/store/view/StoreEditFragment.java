package com.optlab.banhangso.features.main.store.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.optlab.banhangso.R;
import com.optlab.banhangso.databinding.FragmentStoreEditBinding;
import com.optlab.banhangso.features.main.store.viewmodel.StoreEditViewModel;
import com.optlab.banhangso.features.shared.view.AnimationLoadingDialog;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class StoreEditFragment extends Fragment {
    private final AnimationLoadingDialog loadingDialog = new AnimationLoadingDialog();
    private FragmentStoreEditBinding binding;
    private StoreEditFragmentArgs args;
    private StoreEditViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        args = StoreEditFragmentArgs.fromBundle(requireArguments());
        viewModel = new ViewModelProvider(this).get(StoreEditViewModel.class);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStoreEditBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setIsCreateMode(args.getIsCreateMode());
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.mtb.setNavigationOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        viewModel.loadStoreById(args.getStoreId()); // Load the store data based on id
        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), this::handleLoadingState);
        viewModel.getSaveResult().observe(getViewLifecycleOwner(), this::handleCreateStoreResult);
    }

    private void handleLoadingState(Boolean isProcessing) {
        if (Boolean.TRUE.equals(isProcessing)) {
            showLoadingDialog();
        } else {
            dismissLoadingDialog();
        }
    }

    private void handleCreateStoreResult(Boolean result) {
        if (Boolean.TRUE.equals(result)) {
            Snackbar.make(
                            requireView(),
                            R.string.notify_create_store_successful,
                            BaseTransientBottomBar.LENGTH_SHORT)
                    .show();
            NavHostFragment.findNavController(this).navigateUp();
        }
    }

    private void showLoadingDialog() {
        if (!loadingDialog.isAdded()) {
            loadingDialog.show(
                    getParentFragmentManager(), "loadingDialog_" + this.getClass().getSimpleName());
        }
    }

    private void dismissLoadingDialog() {
        if (loadingDialog.isAdded()) {
            loadingDialog.dismiss();
        }
    }
}
