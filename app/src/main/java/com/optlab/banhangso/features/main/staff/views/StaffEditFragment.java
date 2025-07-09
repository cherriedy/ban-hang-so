package com.optlab.banhangso.features.main.staff.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import com.optlab.banhangso.databinding.FragmentStaffEditBinding;
import com.optlab.banhangso.features.main.staff.viewmodels.StaffEditViewModel;
import com.optlab.banhangso.features.shared.views.LoadingDialog;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class StaffEditFragment extends Fragment {

  private final LoadingDialog loadingDialog = new LoadingDialog();

  private FragmentStaffEditBinding binding;
  private StaffEditFragmentArgs args;
  private StaffEditViewModel viewModel;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    viewModel = new ViewModelProvider(this).get(StaffEditViewModel.class);
    args = StaffEditFragmentArgs.fromBundle(requireArguments());
    configureInteractionMode();
  }

  private void configureInteractionMode() {
    // Set the interaction mode based on whether it's create or edit
    viewModel.setIsEditing(!args.getIsCreateMode());
    // Load staff data if not in create mode
    viewModel.loadStaffById(args.getStaffId());
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentStaffEditBinding.inflate(inflater, container, false);
    binding.setLifecycleOwner(getViewLifecycleOwner());
    binding.setViewModel(viewModel);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    observeViewModel();
  }

  private void observeViewModel() {
    viewModel.getMessageResId().observe(getViewLifecycleOwner(), this::showToast);
    viewModel.isLoading().observe(getViewLifecycleOwner(), this::handleLoadingState);
  }

  private void handleLoadingState(@NonNull Boolean isLoading) {
    if (isLoading && !loadingDialog.isAdded()) {
      loadingDialog.show(
          getParentFragmentManager(), "loadingDialog_" + this.getClass().getSimpleName());
    } else if (loadingDialog.isAdded()) {
      loadingDialog.dismiss();
    }
  }

  private void showToast(@NonNull Integer messageResId) {
    NavHostFragment.findNavController(this).navigateUp();
    Toast.makeText(requireContext(), getString(messageResId), Toast.LENGTH_SHORT).show();
  }
}
