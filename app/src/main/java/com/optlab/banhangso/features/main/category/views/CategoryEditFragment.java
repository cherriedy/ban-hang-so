package com.optlab.banhangso.features.main.category.views;

import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.optlab.banhangso.databinding.FragmentCategoryEditBinding;
import com.optlab.banhangso.features.main.category.viewmodel.CategoryEditViewModel;
import com.optlab.banhangso.features.shared.views.ExitConfirmationDialog;
import com.optlab.banhangso.features.shared.views.LoadingDialog;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CategoryEditFragment extends BottomSheetDialogFragment {

  private final LoadingDialog loadingDialog = new LoadingDialog();
  private final ExitConfirmationDialog exitConfirmationDialog = new ExitConfirmationDialog();

  private FragmentCategoryEditBinding binding;
  private CategoryEditViewModel viewModel;
  private CategoryEditFragmentArgs args;
  private NavController navController;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setCancelable(false);
    viewModel = new ViewModelProvider(this).get(CategoryEditViewModel.class);
    args = CategoryEditFragmentArgs.fromBundle(requireArguments());

    configureInteractionMode();
  }

  @NonNull @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
    dialog.setOnKeyListener(
        (dialogInterface, keyCode, event) -> {
          // KEYCODE_BACK is used to detect back button presses.
          // ACTION_UP is used to ensure the dialog is shown only when the button is released.
          if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            showExitConfirmationDialog();
            return true;
          }
          return false;
        });
    return dialog;
  }

  private void showExitConfirmationDialog() {
    exitConfirmationDialog.show(
        getParentFragmentManager(), "exitConfirmationDialog_" + this.getClass().getSimpleName());
  }

  /** Configure the interaction mode of the fragment based on the arguments passed. */
  private void configureInteractionMode() {
    viewModel.setIsEditing(args.getIsEditing());
    viewModel.getCategoryById(args.getCategoryId());
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentCategoryEditBinding.inflate(inflater, container, false);
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
    registerExitConfirmationListener();
  }

  private void registerExitConfirmationListener() {
    getParentFragmentManager()
        .setFragmentResultListener(
            ExitConfirmationDialog.REQUEST,
            getViewLifecycleOwner(),
            (requestKey, result) -> {
              if (result.getBoolean(ExitConfirmationDialog.CONFIRMED)) {
                navController.navigateUp();
              }
            });
  }

  private void observeViewModel() {
    viewModel.getMessageResId().observe(getViewLifecycleOwner(), this::showToast);
    viewModel.isLoading().observe(getViewLifecycleOwner(), this::handleLoadingState);
  }

  private void showToast(@NonNull Integer messageResId) {
    navController.navigateUp();
    Toast.makeText(requireContext(), getString(messageResId), Toast.LENGTH_SHORT).show();
  }

  private void handleLoadingState(@NonNull Boolean isLoading) {
    if (isLoading) {
      loadingDialog.show(
          getParentFragmentManager(), "loadingDialog_" + this.getClass().getSimpleName());
    } else {
      loadingDialog.dismissAllowingStateLoss();
    }
  }
}
