package com.optlab.banhangso.features.main.authentication.view;

import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.optlab.banhangso.R;
import com.optlab.banhangso.databinding.FragmentRegistrationBinding;
import com.optlab.banhangso.features.main.authentication.viewmodel.RegistrationViewModel;
import com.optlab.banhangso.features.shared.view.AnimationLoadingDialog;
import com.optlab.banhangso.internal.utilities.NavigationUtils;
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
        viewModel
                .getIsCreating()
                .observe(
                        getViewLifecycleOwner(),
                        isCreating -> {
                            if (Boolean.TRUE.equals(isCreating)) {
                                showLoadingDialog();
                            }
                        });

        viewModel.getCreateResult().observe(getViewLifecycleOwner(), this::onSignUpCompleted);
    }

    private void showLoadingDialog() {
        loadingDialog.show(
                getChildFragmentManager(), "loadingDialog_" + this.getClass().getSimpleName());
    }

    private void onSignUpCompleted(Boolean createResult) {
        if (loadingDialog.isAdded()) {
            loadingDialog.dismiss();
        }

        Integer messageId = viewModel.getSignUpErrorResId().getValue();
        if (Boolean.FALSE.equals(createResult) && messageId != null) {
            Snackbar.make(requireView(), getString(messageId), BaseTransientBottomBar.LENGTH_SHORT)
                    .show();
        }

        if (Boolean.TRUE.equals(createResult)) {
            NavOptions options = NavigationUtils.getNavOptions(R.id.registrationFragment, true);
            NavHostFragment.findNavController(this).navigate(R.id.homeFragment, null, options);
        }
    }

    public void onUserRoleSelected(@NonNull RadioGroup group, int checkedId) {
        Context context = group.getContext();
        TypedValue typedValue = new TypedValue();
        context.getTheme()
                .resolveAttribute(
                        com.google.android.material.R.attr.colorPrimary, typedValue, true);
        int blue = typedValue.data;
        int gray = ContextCompat.getColor(context, R.color.million_gray);

        if (checkedId == R.id.mrb_store_owner) {
            viewModel.setIsAdmin(true);
            binding.mrbEmployee.setTextColor(gray);
            binding.mrbStoreOwner.setTextColor(blue);
            viewModel.updateUserRole("ADMIN");
        } else {
            viewModel.setIsAdmin(false);
            binding.mrbEmployee.setTextColor(blue);
            binding.mrbStoreOwner.setTextColor(gray);
            viewModel.updateUserRole("STAFF");
        }
    }

    public void onSignInTextClick(@NonNull View view) {
        NavHostFragment.findNavController(this).navigateUp();
    }
}
