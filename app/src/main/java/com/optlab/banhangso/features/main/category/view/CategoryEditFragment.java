package com.optlab.banhangso.features.main.category.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.Observable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.optlab.banhangso.R;
import com.optlab.banhangso.databinding.FragmentCategoryEditBinding;
import com.optlab.banhangso.features.main.category.viewmodel.CategoryEditViewModel;
import com.optlab.banhangso.features.shared.view.AnimationLoadingDialog;
import com.optlab.banhangso.models.domain.Category;
import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;

@AndroidEntryPoint
public class CategoryEditFragment extends BottomSheetDialogFragment {
  private FragmentCategoryEditBinding binding;
  private CategoryEditViewModel viewModel;
  private CategoryEditFragmentArgs args;
  private AnimationLoadingDialog loadingDialog;
  private Observable.OnPropertyChangedCallback changedCallback;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initViewModels();
    loadingDialog = new AnimationLoadingDialog();
  }

  private void initNavigation() {
    args = CategoryEditFragmentArgs.fromBundle(requireArguments());
    configureInteractionMode();
  }

  /** Configure the interaction mode of the fragment based on the arguments passed. */
  private void configureInteractionMode() {
    if (args != null) {
      // Set the interaction mode based on the arguments passed to the fragment.
      binding.setIsCreateMode(args.getIsCreateMode());
      // Load the category data if the fragment is not in create mode.
      viewModel.loadCategoryById(args.getCategoryId());
    } else {
      binding.setIsCreateMode(true);
      Timber.e("Arguments are null");
    }

    // Execute pending bindings to ensure the UI is updated with the latest data.
    binding.executePendingBindings();
  }

  private void initViewModels() {
    viewModel = new ViewModelProvider(this).get(CategoryEditViewModel.class);
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentCategoryEditBinding.inflate(inflater, container, false);
    binding.setLifecycleOwner(this);
    binding.setViewModel(viewModel);
    binding.setFragment(this);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    initNavigation();
    observeViewModel();
  }

  @Override
  public void onDestroyView() {
    binding = null;
    changedCallback = null;
    super.onDestroyView();
  }

  @Override
  public void onDestroy() {
    loadingDialog = null;
    super.onDestroy();
  }

  private void observeViewModel() {
    viewModel.getCategory().observe(getViewLifecycleOwner(), this::onPropertyChanged);
    observeLoadingState(viewModel.isCreating());
    observeLoadingState(viewModel.isUpdating());

    observeOperationResult(
        viewModel.getUpdateResult(),
        getString(R.string.notify_create_category_successfully),
        getString(R.string.notify_create_category_fail));
    observeOperationResult(
        viewModel.getCreateResult(),
        getString(R.string.notify_update_category_successfully),
        getString(R.string.notify_update_category_fail));
  }

  /** Handle the property change of the category object. */
  private void onPropertyChanged(Category category) {
    if (changedCallback == null) {
      changedCallback =
          new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
              handlePropertyChange((Category) sender, propertyId);
            }
          };

      // Add the callback to the category object to observe property changes.
      category.addOnPropertyChangedCallback(changedCallback);
    }

    // Initially validate the category properties.
    viewModel.validateName(category.getName());
  }

  private void handlePropertyChange(Category sender, int propertyId) {
    if (propertyId == BR.name) {
      viewModel.validateName(sender.getName());
    }
  }

  /** Observe the result of an operation and show a toast message based on the result. */
  private void observeOperationResult(
      @NonNull MutableLiveData<Boolean> result,
      @NonNull String successMsg,
      @NonNull String errorMsg) {
    result.observe(
        getViewLifecycleOwner(),
        isSuccessful -> {
          if (Boolean.TRUE.equals(isSuccessful)) {
            showToast(successMsg);
          } else {
            showToast(errorMsg);
          }
        });
  }

  private void showToast(String message) {
    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
  }

  /** Show or hide the loading dialog based on the provided state. */
  private void toggleLoadingDialog(Boolean isLoading) {
    if (isLoading) {
      loadingDialog.show(getParentFragmentManager(), this.getClass().getSimpleName());
    } else {
      // Ensure the dialog can be dismissed even if the fragment is not in the foreground
      // due to the behaviour of BottomSheetDialogFragment.
      if (loadingDialog.isAdded()) {
        loadingDialog.dismissAllowingStateLoss();
      }
      NavHostFragment.findNavController(this).navigateUp();
    }
  }

  /** Observe the loading state of the provided MutableLiveData. */
  private void observeLoadingState(MutableLiveData<Boolean> state) {
    state.observe(getViewLifecycleOwner(), this::toggleLoadingDialog);
  }
}
