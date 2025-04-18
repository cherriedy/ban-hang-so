package com.optlab.banhangso.ui.brand.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.Observable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.optlab.banhangso.BR;
import com.optlab.banhangso.R;
import com.optlab.banhangso.data.model.Brand;
import com.optlab.banhangso.databinding.FragmentBrandEditBinding;
import com.optlab.banhangso.ui.brand.viewmodel.BrandEditViewModel;
import com.optlab.banhangso.ui.common.view.AnimationLoadingDialog;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BrandEditFragment extends BottomSheetDialogFragment {
    private final AnimationLoadingDialog loadingDialog = new AnimationLoadingDialog();

    private FragmentBrandEditBinding binding;
    private BrandEditViewModel viewModel;
    private BrandEditFragmentArgs args;
    private Observable.OnPropertyChangedCallback changedCallback;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModels();
    }

    private void initViewModels() {
        viewModel = new ViewModelProvider(this).get(BrandEditViewModel.class);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBrandEditBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        binding.setFragment(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupNavigation();
        observeViewModels();
    }

    private void observeViewModels() {
        viewModel.getBrand().observe(getViewLifecycleOwner(), this::onPropertyChanged);

        observeLoadingState(viewModel.isCreating());
        observeLoadingState(viewModel.isUpdating());

        observeOperationState(
                viewModel.getCreateResult(),
                getString(R.string.notify_create_brand_successful),
                getString(R.string.notify_create_brand_fail));
        observeOperationState(
                viewModel.getUpdateResult(),
                getString(R.string.notify_update_brand_successful),
                getString(R.string.notify_update_brand_fail));
    }

    private void observeOperationState(
            @NonNull LiveData<Boolean> result,
            @NonNull String successMsg,
            @NonNull String errorMsg) {
        result.observe(
                getViewLifecycleOwner(),
                isSuccessful -> {
                    if (Boolean.TRUE.equals(isSuccessful)) {
                        showToast(successMsg);
                        NavHostFragment.findNavController(this).navigateUp();
                    } else {
                        showToast(errorMsg);
                    }
                });
    }

    private void showToast(String successMsg) {
        Toast.makeText(getContext(), successMsg, Toast.LENGTH_SHORT).show();
    }

    private void observeLoadingState(LiveData<Boolean> state) {
        state.observe(getViewLifecycleOwner(), this::toggleLoadingDialog);
    }

    private void toggleLoadingDialog(Boolean isLoading) {
        if (isLoading) {
            loadingDialog.show(getParentFragmentManager(), this.getClass().getSimpleName());
        } else {
            // Ensure the dialog can be dismissed even if the fragment is not in the foreground
            // due to the behaviour of BottomSheetDialogFragment.
            if (loadingDialog.isAdded()) {
                loadingDialog.dismissAllowingStateLoss();
            }
        }
    }

    @Override
    public void onDestroyView() {
        binding = null;
        changedCallback = null;
        super.onDestroyView();
    }

    private void setupNavigation() {
        args = BrandEditFragmentArgs.fromBundle(requireArguments());
        configureInteractionMode();
    }

    private void configureInteractionMode() {
        binding.setIsCreateMode(args.getIsCreateMode());
        binding.executePendingBindings();

        viewModel.loadBrandById(args.getBrandId());
    }

    private void onPropertyChanged(Brand brand) {
        if (changedCallback == null) {
            changedCallback =
                    new Observable.OnPropertyChangedCallback() {
                        @Override
                        public void onPropertyChanged(Observable sender, int propertyId) {
                            handlePropertyChanges((Brand) sender, propertyId);
                        }
                    };

            // Add the callback to the brand object to observe property changes
            brand.addOnPropertyChangedCallback(changedCallback);
        }

        // Initially validate the brand properties.
        viewModel.validateName(brand.getName());
    }

    private void handlePropertyChanges(Brand brand, int propertyId) {
        if (propertyId == BR.name) {
            viewModel.validateName(brand.getName());
        }
    }
}
