package com.optlab.banhangso.ui.main.authentication.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.optlab.banhangso.R;
import com.optlab.banhangso.databinding.FragmentAuthenticationBinding;
import com.optlab.banhangso.domain.repository.PreferenceRepository;
import com.optlab.banhangso.domain.util.AuthData;
import com.optlab.banhangso.ui.base.view.AnimationLoadingDialog;
import com.optlab.banhangso.ui.main.authentication.viewmodel.AuthenticationViewModel;
import com.optlab.banhangso.util.NavigationUtils;

import dagger.hilt.android.AndroidEntryPoint;

import timber.log.Timber;

import javax.inject.Inject;

@AndroidEntryPoint
public class AuthenticationFragment extends Fragment {
    @Inject protected PreferenceRepository preferenceRepository;
    @Inject protected FirebaseAuth firebaseAuth;

    private FragmentAuthenticationBinding binding;
    private AuthenticationViewModel viewModel;
    private AnimationLoadingDialog loadingDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (firebaseAuth.getCurrentUser() != null) {
            navigateToHome();
        }

        viewModel = new ViewModelProvider(this).get(AuthenticationViewModel.class);
        loadingDialog = new AnimationLoadingDialog();
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAuthenticationBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setFragment(this);
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity().getWindow().setDecorFitsSystemWindows(false);
        setupObservers();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (loadingDialog != null && loadingDialog.isAdded()) {
            loadingDialog.dismiss();
        }
        binding = null;
    }

    private void setupObservers() {
        viewModel.getIsAuthenticating().observe(getViewLifecycleOwner(), this::showLoadingDialog);
        viewModel
                .getAuthenticateResult()
                .observe(
                        getViewLifecycleOwner(),
                        shouldNavigate -> {
                            if (shouldNavigate) {
                                navigateToHome();
                            }
                        });
        viewModel
                .getShouldNavigateToSignUp()
                .observe(
                        getViewLifecycleOwner(),
                        shouldNavigate -> {
                            if (shouldNavigate) {
                                navigateToSignUp();
                                viewModel.setShouldNavigateToSignUp(false);
                            }
                        });
    }

    private void showLoadingDialog(Boolean shouldShow) {
        if (shouldShow) {
            loadingDialog.show(
                    getParentFragmentManager(), "loadingDialog_" + this.getClass().getSimpleName());
        }
        if (!shouldShow && loadingDialog.isAdded()) {
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
    public void onUserRoleSelected(RadioGroup group, int checkedId) {
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
        String storeId = preferenceRepository.getSelectedStoreId();
        if (storeId == null || storeId.isEmpty()) {
            selectStore();
            return;
        }
        NavOptions options = NavigationUtils.getNavOptions(R.id.authenticationFragment, true);
        NavHostFragment.findNavController(this).navigate(R.id.homeFragment, null, options);
    }

    private void selectStore() {
        NavDirections action = AuthenticationFragmentDirections.actionAuthenticationToSelectStore();
        NavOptions options = NavigationUtils.getNavOptions(R.id.authenticationFragment, true);
        NavHostFragment.findNavController(this).navigate(action, options);
    }

    private void navigateToSignUp() {
        AuthData authData = viewModel.getAuthData().getValue();
        if (authData != null) {
            NavDirections action =
                    AuthenticationFragmentDirections.actionAuthenticationToSignUp(
                            authData.getEmail(), authData.getPassword());
            NavHostFragment.findNavController(this).navigate(action);
        } else {
            Timber.e("AuthData is null, cannot navigate to SignUp");
        }
    }
}
