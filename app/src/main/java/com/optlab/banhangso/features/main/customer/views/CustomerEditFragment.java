package com.optlab.banhangso.features.main.customer.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.optlab.banhangso.databinding.FragmentCustomerEditBinding;
import com.optlab.banhangso.features.main.customer.models.CustomerUiModel;
import com.optlab.banhangso.features.main.customer.viewmodels.CustomerEditViewModel;
import com.optlab.banhangso.features.shared.views.DatePickerDialog;
import com.optlab.banhangso.features.shared.views.DeleteConfirmationDialog;
import com.optlab.banhangso.features.shared.views.ExitConfirmationDialog;
import com.optlab.banhangso.features.shared.views.LoadingDialog;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CustomerEditFragment extends Fragment {

  private final LoadingDialog loadingDialog = new LoadingDialog();
  private final DatePickerDialog datePickerDialog = new DatePickerDialog();
  private final ExitConfirmationDialog exitConfirmationDialog = new ExitConfirmationDialog();
  private final DeleteConfirmationDialog deleteConfirmationDialog = new DeleteConfirmationDialog();

  private FragmentCustomerEditBinding binding;
  private CustomerEditFragmentArgs args;
  private CustomerEditViewModel viewModel;
  private NavController navController;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    args = CustomerEditFragmentArgs.fromBundle(requireArguments());
    viewModel = new ViewModelProvider(this).get(CustomerEditViewModel.class);
    configureInteractionMode();
    requireActivity()
        .getOnBackPressedDispatcher()
        .addCallback(
            new OnBackPressedCallback(true) {
              @Override
              public void handleOnBackPressed() {
                showExitConfirmationDialog();
              }
            });
  }

  private void configureInteractionMode() {
    viewModel.setIsEditing(args.getIsEditing());
    viewModel.loadCustomerById(args.getCustomerId());

    CustomerUiModel passedCustomer = args.getCustomer();
    if (passedCustomer != null) {
      viewModel.setUiModel(passedCustomer);
    }

  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentCustomerEditBinding.inflate(inflater, container, false);
    binding.setLifecycleOwner(getViewLifecycleOwner());
    binding.setViewModel(viewModel);
    binding.setFragment(this);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    navController = NavHostFragment.findNavController(this);
    binding.mtb.setNavigationOnClickListener(v -> showExitConfirmationDialog());

    registerExitConfirmationListener();
    registerDeleteConfirmationListener();
    registerDatePickerResultListener();
    observeViewModel();
  }

  private void registerDeleteConfirmationListener() {
    getParentFragmentManager()
        .setFragmentResultListener(
            DeleteConfirmationDialog.REQUEST,
            getViewLifecycleOwner(),
            (requestKey, result) -> {
              boolean deleted = result.getBoolean(DeleteConfirmationDialog.DELETED);
              if (deleted) {
                viewModel.onDelete();
              }
            });
  }

  private void registerExitConfirmationListener() {
    getParentFragmentManager()
        .setFragmentResultListener(
            ExitConfirmationDialog.REQUEST,
            getViewLifecycleOwner(),
            (requestKey, result) -> {
              boolean confirm = result.getBoolean(ExitConfirmationDialog.CONFIRMED);
              if (confirm) {
                navController.navigateUp();
              }
            });
  }

  private void registerDatePickerResultListener() {
    getParentFragmentManager()
        .setFragmentResultListener(
            DatePickerDialog.REQUEST,
            getViewLifecycleOwner(),
            (requestKey, result) -> handleSelectedDate(result));
  }

  private void showExitConfirmationDialog() {
    if (!exitConfirmationDialog.isAdded()) {
      exitConfirmationDialog.show(
          getParentFragmentManager(), "exitConfirmationDialog_" + this.getClass().getSimpleName());
    }
  }

  /**
   * @noinspection unused
   */
  public void onDatePickerClick(@NonNull View view) {
    datePickerDialog.show(
        getParentFragmentManager(), "datePickerDialog_" + this.getClass().getSimpleName());
  }

  /**
   * @noinspection unused
   */
  public void onDeleteClick(@NonNull View view) {
    deleteConfirmationDialog.show(
        getParentFragmentManager(), "deleteConfirmationDialog_" + this.getClass().getSimpleName());
  }

  private void observeViewModel() {
    viewModel.isLoading().observe(getViewLifecycleOwner(), this::handleLoadingState);
    viewModel.getMessageResId().observe(getViewLifecycleOwner(), this::showToast);
  }

  private void showToast(@NonNull Integer messageResId) {
    navController.navigateUp();
    Toast.makeText(requireContext(), getString(messageResId), Toast.LENGTH_SHORT).show();
  }

  private void handleLoadingState(@NonNull Boolean isLoading) {
    if (isLoading && !loadingDialog.isAdded()) {
      loadingDialog.show(
          getParentFragmentManager(), "loadingDialog_" + this.getClass().getSimpleName());
    } else if (loadingDialog.isAdded()) {
      loadingDialog.dismissAllowingStateLoss();
    }
  }

  private void handleSelectedDate(@NonNull Bundle result) {
    String dob = result.getString(DatePickerDialog.RESULT);
    viewModel.setDob(Objects.requireNonNull(dob));
  }
}
