package com.optlab.banhangso.features.main.authentication.view;

import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import com.optlab.banhangso.R;
import com.optlab.banhangso.databinding.FragmentRegistrationBinding;
import com.optlab.banhangso.features.main.authentication.viewmodel.RegistrationViewModel;
import com.optlab.banhangso.features.shared.view.AnimationLoadingDialog;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RegistrationFragment extends Fragment {
  private FragmentRegistrationBinding binding;
  private RegistrationViewModel viewModel;
  private AnimationLoadingDialog loadingDialog;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initViewModel();
    RegistrationFragmentArgs args = RegistrationFragmentArgs.fromBundle(requireArguments());
    viewModel.setEmail(args.getEmail());
    viewModel.setPassword(args.getPassword());
    loadingDialog = new AnimationLoadingDialog();
  }

  private void initViewModel() {
    viewModel = new ViewModelProvider(this).get(RegistrationViewModel.class);
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentRegistrationBinding.inflate(inflater, container, false);
    binding.setLifecycleOwner(getViewLifecycleOwner());
    binding.setViewModel(viewModel);
    binding.setFragment(this);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    binding.mtb.setNavigationOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    observeViewModel();
  }

  private void observeViewModel() {
    viewModel.getIsLoading().observe(getViewLifecycleOwner(), this::handleLoadingState);
    viewModel.getErrorMessageResId().observe(getViewLifecycleOwner(), this::handleErrorMessage);
    viewModel.getSignUpResult().observe(getViewLifecycleOwner(), this::handleSignUpResult);
  }

  private void handleSignUpResult(@NonNull Boolean result) {
    if (result) {
      NavDirections action = RegistrationFragmentDirections.actionToStoreSelect();
      NavHostFragment.findNavController(this).navigate(action);
    }
  }

  private void handleErrorMessage(@NonNull @StringRes Integer errorMessageResId) {
    Toast.makeText(requireContext(), getString(errorMessageResId), Toast.LENGTH_SHORT).show();
  }

  private void handleLoadingState(Boolean isLoading) {
    if (Boolean.TRUE.equals(isLoading)) {
      loadingDialog.show(
          getChildFragmentManager(), "loadingDialog_" + this.getClass().getSimpleName());
    } else {
      loadingDialog.dismiss();
    }
  }

  public void onUserRoleSelected(@NonNull RadioGroup group, int checkedId) {
    Context context = group.getContext();
    TypedValue typedValue = new TypedValue();
    context
        .getTheme()
        .resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValue, true);
    int blue = typedValue.data;
    int gray = ContextCompat.getColor(context, R.color.million_gray);

    if (checkedId == R.id.mrb_store_owner) {
      viewModel.setIsAdmin(true);
      binding.mrbEmployee.setTextColor(gray);
      binding.mrbStoreOwner.setTextColor(blue);
    } else {
      viewModel.setIsAdmin(false);
      binding.mrbEmployee.setTextColor(blue);
      binding.mrbStoreOwner.setTextColor(gray);
    }
  }

  public void onSignInTextClick(@NonNull View view) {
    NavHostFragment.findNavController(this).navigateUp();
  }
}
