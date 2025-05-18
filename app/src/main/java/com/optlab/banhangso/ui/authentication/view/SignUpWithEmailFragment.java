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
import androidx.navigation.fragment.NavHostFragment;

import com.optlab.banhangso.R;
import com.optlab.banhangso.databinding.FragmentSignUpWithEmailBinding;
import com.optlab.banhangso.ui.authentication.state.AuthValidationState;
import com.optlab.banhangso.ui.authentication.viewmodel.SignUpViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SignUpWithEmailFragment extends Fragment {
    private FragmentSignUpWithEmailBinding binding;
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

        viewModel.setAuthValidationState(new AuthValidationState(AuthValidationState.SIGNUP_EMAIL));
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentSignUpWithEmailBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        binding.setFragment(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void onNextButtonClick(@NonNull View view) {
        NavHostFragment.findNavController(this).navigate(R.id.setupPasswordFragment);
    }

    public void onSignInTextClick(@NonNull View view) {
        NavHostFragment.findNavController(this).navigateUp();
    }
}
