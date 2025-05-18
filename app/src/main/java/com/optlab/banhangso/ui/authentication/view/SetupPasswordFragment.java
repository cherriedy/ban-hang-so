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
import com.optlab.banhangso.data.model.AuthData;
import com.optlab.banhangso.databinding.FragmentSetupPasswordBinding;
import com.optlab.banhangso.ui.authentication.viewmodel.SignUpViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SetupPasswordFragment extends Fragment {
    private FragmentSetupPasswordBinding binding;
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
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSetupPasswordBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        binding.setFragment(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showAuthData();
    }

    private void showAuthData() {
        AuthData authData = viewModel.getAuthData().getValue();
        NavBackStackEntry previousBackStackEntry =
                NavHostFragment.findNavController(this).getPreviousBackStackEntry();

        // Check if authData and previousBackStackEntry are not null
        if (authData == null || previousBackStackEntry == null) return;

        // Get the destination ID of the previous back stack entry
        int previousDestinationId = previousBackStackEntry.getDestination().getId();
        // Set the text based on the previous destination ID
        if (previousDestinationId == R.id.signUpWithEmailFragment)
            binding.tvPhoneNumberOrEmail.setText(authData.getEmail());
        else if (previousDestinationId == R.id.signUpWithPhoneNumberFragment) {
            binding.tvPhoneNumberOrEmail.setText(authData.getPhoneNumber());
        }
    }

    public void onNextButtonClick(@NonNull View view) {
        Navigation.findNavController(view).navigate(R.id.registerAccountFragment);
    }
}
