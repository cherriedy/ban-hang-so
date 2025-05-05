package com.optlab.banhangso.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.optlab.banhangso.ui.authentication.view.SignInWithEmailFragment;
import com.optlab.banhangso.ui.authentication.view.SignInWithPhoneNumberFragment;

public class LoginViewPagerAdapter extends FragmentStateAdapter {
    public LoginViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return switch (position) {
            case 0 -> new SignInWithPhoneNumberFragment();
            case 1 -> new SignInWithEmailFragment();
            default -> throw new IllegalStateException("Unknown position: " + position);
        };
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
