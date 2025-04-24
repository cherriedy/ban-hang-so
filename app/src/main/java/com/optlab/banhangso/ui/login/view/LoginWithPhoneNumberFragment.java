package com.optlab.banhangso.ui.login.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.optlab.banhangso.databinding.FragmentLoginWithPhoneNumberBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginWithPhoneNumberFragment extends Fragment {
    private FragmentLoginWithPhoneNumberBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginWithPhoneNumberBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }
}
