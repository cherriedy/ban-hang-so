package com.optlab.banhangso.ui.authentication.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.optlab.banhangso.R;
import com.optlab.banhangso.databinding.FragmentSignUpWithPhoneNumberBinding;
import com.optlab.banhangso.ui.authentication.state.AuthValidationState;
import com.optlab.banhangso.ui.authentication.viewmodel.SignUpViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SignUpWithPhoneNumberFragment extends Fragment {
    private FragmentSignUpWithPhoneNumberBinding binding;
    private SignUpViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModel();
    }

    private void initViewModel() {
        NavBackStackEntry navBackStackEntry =
                NavHostFragment.findNavController(this).getBackStackEntry(R.id.nav_graph_sign_up);
        viewModel = new ViewModelProvider(navBackStackEntry).get(SignUpViewModel.class);

        viewModel.setAuthValidationState(new AuthValidationState(AuthValidationState.SIGNUP_PHONE));
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSignUpWithPhoneNumberBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        binding.setFragment(this);
        return binding.getRoot();
    }

    public void onNextButtonClick(@NonNull View view) {
        Navigation.findNavController(view).navigate(R.id.setupPasswordFragment);
    }

    public void onSignInTextClick(@NonNull View view) {
        Navigation.findNavController(view).navigateUp();
    }
}
