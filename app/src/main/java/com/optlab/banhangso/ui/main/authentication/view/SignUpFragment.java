package com.optlab.banhangso.ui.main.authentication.view;

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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.optlab.banhangso.R;
import com.optlab.banhangso.domain.model.User;
import com.optlab.banhangso.databinding.FragmentSignUpBinding;
import com.optlab.banhangso.ui.main.authentication.viewmodel.SignUpViewModel;
import com.optlab.banhangso.ui.base.view.AnimationLoadingDialog;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SignUpFragment extends Fragment {
    private FragmentSignUpBinding binding;
    private SignUpViewModel viewModel;
    private AnimationLoadingDialog loadingDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModel();
        SignUpFragmentArgs args = SignUpFragmentArgs.fromBundle(requireArguments());
        viewModel.setEmail(args.getEmail());
        viewModel.setPassword(args.getPassword());
        loadingDialog = new AnimationLoadingDialog();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setViewModel(viewModel);
        binding.setFragment(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.mtb.setNavigationOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        viewModel
                .getIsCreating()
                .observe(
                        getViewLifecycleOwner(),
                        isCreating -> {
                            if (isCreating) {
                                loadingDialog.show(
                                        getChildFragmentManager(), "loadingDialog_" + this.getClass().getSimpleName());
                            }
                        });
        viewModel
                .getCreateResult()
                .observe(
                        getViewLifecycleOwner(),
                        createResult -> {
                            if (loadingDialog.isAdded()) {
                                loadingDialog.dismiss();
                            }

                            Integer messageId = viewModel.getSignUpErrorResId().getValue();
                            if (!createResult && messageId != null) {
                                Toast.makeText(requireContext(), getString(messageId), Toast.LENGTH_SHORT).show();
                            }

                            if (createResult) {
                                NavHostFragment.findNavController(this)
                                        .navigate(
                                                R.id.homeFragment,
                                                null,
                                                new NavOptions.Builder().setPopUpTo(R.id.signUpFragment, true).build());
                            }
                        });
    }

    public void onUserRoleSelected(RadioGroup group, int checkedId) {
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
            viewModel.updateUserRole(User.Store.ADMIN);
        } else {
            viewModel.setIsAdmin(false);
            binding.mrbEmployee.setTextColor(blue);
            binding.mrbStoreOwner.setTextColor(gray);
            viewModel.updateUserRole(User.Store.STAFF);
        }
    }

    public void onSignInTextClick(@NonNull View view) {
        NavHostFragment.findNavController(this).navigateUp();
    }
}
