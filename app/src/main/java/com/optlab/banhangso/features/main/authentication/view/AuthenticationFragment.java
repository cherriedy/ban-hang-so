package com.optlab.banhangso.features.main.authentication.view;

import static com.optlab.banhangso.features.main.authentication.Constants.KEY_EMAIL;
import static com.optlab.banhangso.features.main.authentication.Constants.KEY_PASSWORD;

import android.content.Context;
import android.os.Bundle;
import android.util.Pair;
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
import com.optlab.banhangso.features.main.activity.SharedViewModel;
import com.optlab.banhangso.features.main.authentication.viewmodel.AuthenticationViewModel;
import com.optlab.banhangso.features.shared.views.LoadingDialog;
import com.optlab.banhangso.internal.utilities.NavigationUtils;
import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;

@AndroidEntryPoint
public class AuthenticationFragment extends Fragment {

  private final LoadingDialog loadingDialog = new LoadingDialog();
  private FragmentAuthenticationBinding binding;
  private AuthenticationViewModel viewModel;
  private SharedViewModel sharedViewModel;
  private NavController navController;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    viewModel = new ViewModelProvider(this).get(AuthenticationViewModel.class);
    sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
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

  private void handleAuthStateChange(@NonNull Pair<Boolean, Boolean> authState) {
    Boolean isAuthenticated = authState.first;
    Boolean isStoreSelected = authState.second;

    Timber.d(
        "Auth state updated: isAuthenticated=%s, isStoreSelected=%s",
        isAuthenticated, isStoreSelected);

    if (isAuthenticated != null && isAuthenticated) {
      Timber.d("User is authenticated, checking store selection");
      if (isStoreSelected != null && isStoreSelected) {
        Timber.d("User has store selected, navigating to home");
        navigateToHome();
      } else {
        sharedViewModel.setIsChecking(false); // Disable splash screen checking.
        Timber.d("User has no store selected, navigating to select store");
        navigateToSelectStore();
      }
    } else {
      sharedViewModel.setIsChecking(false); // Disable splash screen checking.
      Timber.d("User is not authenticated, staying in authentication screen");
    }
  }

  @Override
  public void onDestroyView() {
    binding = null;
    super.onDestroyView();
  }

  private void observeViewModel() {
    viewModel.getAuthState().observe(getViewLifecycleOwner(), this::handleAuthStateChange);
    viewModel.isLoading().observe(getViewLifecycleOwner(), this::showLoadingDialog);
    viewModel.getRegistrationFlag().observe(getViewLifecycleOwner(), this::handleRegistrationFlag);
    viewModel.getErrorFlag().observe(getViewLifecycleOwner(), this::handleErrorFlag);
  }

  private void handleErrorFlag(Boolean result) {
    if (Boolean.TRUE.equals(result)) {
      Toast.makeText(requireContext(), getString(R.string.error_login_in), Toast.LENGTH_SHORT)
          .show();
    }
  }

  private void handleRegistrationFlag(Boolean flag) {
    if (Boolean.TRUE.equals(flag)) {
      navigateToRegistration();
      viewModel.setRegistrationFlag(false);
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
