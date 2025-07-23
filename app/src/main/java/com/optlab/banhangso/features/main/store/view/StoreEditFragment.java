package com.optlab.banhangso.features.main.store.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.optlab.banhangso.databinding.FragmentStoreEditBinding;
import com.optlab.banhangso.features.main.store.viewmodel.StoreEditViewModel;
import com.optlab.banhangso.features.shared.views.LoadingDialog;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class StoreEditFragment extends Fragment {
  private final LoadingDialog loadingDialog = new LoadingDialog();
  private FragmentStoreEditBinding binding;
  private StoreEditFragmentArgs args;
  private StoreEditViewModel viewModel;
  private NavController navController;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    args = StoreEditFragmentArgs.fromBundle(requireArguments());
    viewModel = new ViewModelProvider(this).get(StoreEditViewModel.class);
    configureInteractionMode();
  }

  private void configureInteractionMode() {
    boolean isEditing = args.getIsEditing();
    viewModel.setIsEditing(isEditing);
    if (isEditing) viewModel.loadStore();
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentStoreEditBinding.inflate(inflater, container, false);
    binding.setLifecycleOwner(getViewLifecycleOwner());
    binding.setViewModel(viewModel);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    navController = NavHostFragment.findNavController(this);
    binding.mtb.setNavigationOnClickListener(v -> navController.navigateUp());
    observeViewModel();
  }

  private void observeViewModel() {
    viewModel.isLoading().observe(getViewLifecycleOwner(), this::handleLoadingState);
    viewModel.getMessageResId().observe(getViewLifecycleOwner(), this::showToast);
    viewModel.getOperationCompleted().observe(getViewLifecycleOwner(), this::handleRefreshRequest);
  }

  private void handleRefreshRequest(@NonNull Boolean completed) {
    if (completed) {
      Bundle result = new Bundle();
      result.putBoolean(SelectStoreFragment.STORE_REFRESH_KEY, true);
      requireActivity()
          .getSupportFragmentManager()
          .setFragmentResult(SelectStoreFragment.SELECT_STORE_REQUEST_KEY, result);
    }
  }

  private void showToast(int messageResId) {
    navController.navigateUp(); // Navigate up to close the fragment.
    Toast.makeText(requireContext(), getString(messageResId), Toast.LENGTH_SHORT).show();
  }

  private void handleLoadingState(@NonNull Boolean result) {
    if (result && !loadingDialog.isAdded()) {
      loadingDialog.show(
          getParentFragmentManager(), "loadingDialog_" + this.getClass().getSimpleName());
    } else if (loadingDialog.isAdded()) {
      loadingDialog.dismiss();
    }
  }
}
