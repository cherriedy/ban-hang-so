package com.optlab.banhangso.features.main.brand.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.optlab.banhangso.databinding.FragmentBrandEditBinding;
import com.optlab.banhangso.features.main.brand.viewmodel.BrandEditViewModel;
import com.optlab.banhangso.features.shared.views.LoadingDialog;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BrandEditFragment extends BottomSheetDialogFragment {

  public static final String BRAND_EDIT_RESULT = "BRAND_EDIT_RESULT";
  public static final String REFRESH_FLAG = "REFRESH_FLAG";

  private final LoadingDialog loadingDialog = new LoadingDialog();

  private FragmentBrandEditBinding binding;
  private BrandEditViewModel viewModel;
  private BrandEditFragmentArgs args;
  private NavController navController;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    args = BrandEditFragmentArgs.fromBundle(requireArguments());
    viewModel = new ViewModelProvider(this).get(BrandEditViewModel.class);
    configureInteractionMode();
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentBrandEditBinding.inflate(inflater, container, false);
    binding.setLifecycleOwner(getViewLifecycleOwner());
    binding.setViewModel(viewModel);
    binding.setFragment(this);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    navController = NavHostFragment.findNavController(this);
    observeViewModel();
  }

  private void configureInteractionMode() {
    viewModel.setIsEditing(args.getIsEditing());
    viewModel.loadBrandById(args.getBrandId());
  }

  private void observeViewModel() {
    viewModel.isLoading().observe(getViewLifecycleOwner(), this::handleLoadingState);
    viewModel.getMessageResId().observe(getViewLifecycleOwner(), this::showToast);
    viewModel
        .getOperationCompleted()
        .observe(getViewLifecycleOwner(), this::handleOperationCompleted);
  }

  private void showToast(@NonNull Integer messageResId) {
    navController.navigateUp();
    Toast.makeText(getContext(), getString(messageResId), Toast.LENGTH_SHORT).show();
  }

  private void handleLoadingState(@NonNull Boolean isLoading) {
    if (isLoading) {
      loadingDialog.show(
          getParentFragmentManager(), "loadingDialog_" + this.getClass().getSimpleName());
    } else {
      loadingDialog.dismissAllowingStateLoss();
    }
  }

  private void handleOperationCompleted(@NonNull Boolean completed) {
    if (completed) {
      Bundle result = new Bundle();
      result.putBoolean(REFRESH_FLAG, true);
      requireActivity().getSupportFragmentManager().setFragmentResult(BRAND_EDIT_RESULT, result);
    }
  }
}
