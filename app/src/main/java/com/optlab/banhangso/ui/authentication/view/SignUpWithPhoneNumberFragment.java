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
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.optlab.banhangso.NavGraphSignUpWithPhoneDirections;
import com.optlab.banhangso.R;
import com.optlab.banhangso.databinding.FragmentSignUpWithPhoneNumberBinding;
import com.optlab.banhangso.ui.authentication.viewmodel.SignUpWithPhoneNumberViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SignUpWithPhoneNumberFragment extends Fragment {
    private FragmentSignUpWithPhoneNumberBinding binding;
    private SignUpWithPhoneNumberViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModel();
    }

    private void initViewModel() {
        NavBackStackEntry navBackStackEntry =
                NavHostFragment.findNavController(this)
                        .getBackStackEntry(R.id.nav_graph_sign_up_with_phone);
        viewModel =
                new ViewModelProvider(navBackStackEntry).get(SignUpWithPhoneNumberViewModel.class);
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
        NavDirections action = NavGraphSignUpWithPhoneDirections.actionSignUpToSetUpPassword();
        Navigation.findNavController(view).navigate(action);
    }

    public void onSignInTextClick(@NonNull View view) {
        Navigation.findNavController(view).navigateUp();
    }
}
