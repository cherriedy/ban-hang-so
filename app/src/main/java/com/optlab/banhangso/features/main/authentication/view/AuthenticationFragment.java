package com.optlab.banhangso.features.main.authentication.view;

import static android.accounts.AccountManager.KEY_PASSWORD;
import static com.optlab.banhangso.internal.utilities.Constants.Auth.KEY_EMAIL;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import com.optlab.banhangso.R;
import com.optlab.banhangso.databinding.FragmentAuthenticationBinding;
import com.optlab.banhangso.features.main.authentication.viewmodel.AuthenticationViewModel;
import com.optlab.banhangso.features.shared.view.AnimationLoadingDialog;
import com.optlab.banhangso.internal.utilities.NavigationUtils;
import com.optlab.banhangso.models.domain.store.RoleStore;
import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;

@AndroidEntryPoint
public class AuthenticationFragment extends Fragment {

  private final AnimationLoadingDialog loadingDialog = new AnimationLoadingDialog();
  private FragmentAuthenticationBinding binding;
  private AuthenticationViewModel viewModel;
  private NavController navController;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    viewModel = new ViewModelProvider(this).get(AuthenticationViewModel.class);
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentAuthenticationBinding.inflate(inflater, container, false);
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

  @Override
  public void onDestroyView() {
    binding = null;
    super.onDestroyView();
  }

  private void observeViewModel() {
    viewModel.isAuthenticated().observe(getViewLifecycleOwner(), this::handleIsAuthenticatedState);
    viewModel.getIsLoading().observe(getViewLifecycleOwner(), this::showLoadingDialog);
    viewModel.getAuthResult().observe(getViewLifecycleOwner(), this::handleAuthResult);
    viewModel.getRegistrationFlag().observe(getViewLifecycleOwner(), this::handleRegistrationFlag);
    viewModel.getErrorFlag().observe(getViewLifecycleOwner(), this::handleErrorFlag);
  }

  private void handleStoreUpdate(@NonNull RoleStore store) {
    if (store.isEmpty()) {
      Timber.w("Store is empty, navigating to SelectStoreFragment");
      navigateToSelectStore();
    } else {
      Timber.d("Store is not null, navigating to HomeFragment");
      navigateToHome();
    }
  }

  private void handleIsAuthenticatedState(@NonNull Boolean result) {
    if (result) {
      // If the user is already authenticated, check if they have a store selected
      viewModel.getStore().observe(getViewLifecycleOwner(), this::handleStoreUpdate);
    }
  }

  private void handleErrorFlag(Boolean result) {
    if (Boolean.TRUE.equals(result)) {
      Toast.makeText(requireContext(), "Có lỗi trong quá trình đăng nhập", Toast.LENGTH_SHORT)
          .show();
    }
  }

  private void handleRegistrationFlag(Boolean flag) {
    if (Boolean.TRUE.equals(flag)) {
      navigateToRegistration();
      viewModel.setRegistrationFlag(false);
    }
  }

  private void handleAuthResult(Boolean result) {
    if (Boolean.TRUE.equals(result)) {
      navigateToHome();
    }
  }

  private void showLoadingDialog(Boolean shouldShow) {
    if (Boolean.TRUE.equals(shouldShow)) {
      loadingDialog.show(
          getParentFragmentManager(), "loadingDialog_" + this.getClass().getSimpleName());
    }
    if (Boolean.FALSE.equals(shouldShow) && loadingDialog.isAdded()) {
      loadingDialog.dismiss();
    }
  }

  /**
   * Updates the UI based on the selected radio button in the RadioGroup.
   *
   * <p>This method changes the text color of the selected radio button to blue and the other to
   * gray. It also shows or hides the options layout based on the selected radio button.
   *
   * @param group The RadioGroup containing the radio buttons.
   * @param checkedId The ID of the selected radio button.
   */
  public void onUserRoleSelected(@NonNull RadioGroup group, int checkedId) {
    Context context = group.getContext();
    int blue = ContextCompat.getColor(context, R.color.boston_blue);
    int gray = ContextCompat.getColor(context, R.color.million_gray);

    if (checkedId == R.id.mrb_sign_in) {
      viewModel.setIsSignIn(true);
      binding.mrbSignIn.setTextColor(blue);
      binding.mrbSignUp.setTextColor(gray);
    } else {
      viewModel.setIsSignIn(false);
      binding.mrbSignIn.setTextColor(gray);
      binding.mrbSignUp.setTextColor(blue);
    }
  }

  private void navigateToHome() {
    NavOptions options = NavigationUtils.getNavOptions(R.id.authenticationFragment, true);
    navController.navigate(R.id.homeFragment, null, options);
  }

  private void navigateToSelectStore() {
    NavDirections action = AuthenticationFragmentDirections.actionAuthenticationToSelectStore();
    NavOptions options = NavigationUtils.getNavOptions(R.id.authenticationFragment, true);
    navController.navigate(action, options);
  }

  private void navigateToRegistration() {
    String email = viewModel.getInputFields().get(KEY_EMAIL);
    String password = viewModel.getInputFields().get(KEY_PASSWORD);

    if (email != null && password != null) {
      NavDirections action =
          AuthenticationFragmentDirections.actionAuthenticationToRegistration(email, password);
      navController.navigate(action);
    } else {
      Timber.e("Authentication data is null, cannot navigate to SignUp");
    }
  }
}
