package com.optlab.banhangso.ui.home.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.auth.FirebaseAuth;
import com.optlab.banhangso.R;
import com.optlab.banhangso.databinding.FragmentHomeBinding;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment {
  @Inject
  protected FirebaseAuth firebaseAuth;
  private FragmentHomeBinding binding;

  @Override
  public View onCreateView(
          @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentHomeBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    NavController navController = NavHostFragment.findNavController(this);
    NavigationUI.setupWithNavController(binding.bnv, navController);
    binding.ivLogOut.setOnClickListener(
            v -> {
              if (firebaseAuth.getCurrentUser() != null) {
                firebaseAuth.signOut();
              }
              NavController controller = NavHostFragment.findNavController(this);
              controller.navigate(
                      R.id.action_to_authentication,
                      null,
                      new NavOptions.Builder().setPopUpTo(R.id.homeFragment, true).build());
            });
  }

  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }

  public static class QuickShortcutClickHandler {
    public void onSellShortcutClick(View view) {
    }

    public void onOrderShortcutClick(View view) {
    }

    public void onCustomerShortcutClick(View view) {
    }

    public void onReportShortcutClick(View view) {
    }

    public void onWarehouseShortcutClick(View view) {
    }

    public void onMoreShortcutClick(View view) {
    }

    public void onEmployeeShortcutClick(View view) {
    }

    public void onStoreShortcutClick(View view) {
    }
  }
}
