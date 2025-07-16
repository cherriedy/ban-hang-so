package com.optlab.banhangso.features.main.home.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.optlab.banhangso.R;
import com.optlab.banhangso.databinding.FragmentHomeBinding;
import com.optlab.banhangso.features.main.home.viewmodels.HomeViewModel;
import com.optlab.banhangso.features.shared.views.LoadingDialog;
import com.optlab.banhangso.internal.utilities.NavigationUtils;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment implements View.OnClickListener {

  private final LoadingDialog loadingDialog = new LoadingDialog();
  private FragmentHomeBinding binding;
  private HomeViewModel viewModel;
  private NavController navController;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentHomeBinding.inflate(inflater, container, false);
    binding.setLifecycleOwner(getViewLifecycleOwner());
    binding.setViewModel(viewModel);
    binding.setFragment(this);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    navController = NavHostFragment.findNavController(this);
    NavigationUI.setupWithNavController(binding.bnv, navController);
    binding.srlMain.setOnRefreshListener(
        () -> {
          viewModel.fetchReportSummary();
          binding.srlMain.setRefreshing(false);
        });
    observeViewModel();
  }

  @Override
  public void onClick(View v) {
    NavController controller = Navigation.findNavController(v);
    int viewId = v.getId();
    if (viewId == R.id.ib_stores) {
      NavOptions options = NavigationUtils.getNavOptions(R.id.homeFragment, true);
      NavDirections action = HomeFragmentDirections.actionToStoreSelect();
      controller.navigate(action, options);
    } else if (viewId == R.id.mcv_staff) {
      NavDirections action = HomeFragmentDirections.actionToStaff();
      controller.navigate(action);
    } else if (viewId == R.id.mcv_customer) {
      NavDirections action = HomeFragmentDirections.actionToCustomer();
      controller.navigate(action);
    } else if (viewId == R.id.mcv_sale) {
      NavDirections action = HomeFragmentDirections.actionToSale();
      controller.navigate(action);
    } else if (viewId == R.id.mcv_transaction) {
      NavDirections action = HomeFragmentDirections.actionToTransaction();
      controller.navigate(action);
    } else if (viewId == R.id.mcv_store) {
      NavDirections action = HomeFragmentDirections.actionToEditStore(false);
      controller.navigate(action);
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }

  private void observeViewModel() {
    viewModel.getSignOutResult().observe(getViewLifecycleOwner(), this::handleSignOutResult);
    viewModel.isLoading().observe(getViewLifecycleOwner(), this::handleLoadingState);
  }

  private void handleLoadingState(@NonNull Boolean result) {
    // Always hide SwipeRefreshLayout spinner
    if (binding != null) {
      binding.srlMain.setRefreshing(false);
    }

    if (result) {
      loadingDialog.show(
          getParentFragmentManager(), "loadingDialog_" + this.getClass().getSimpleName());
    } else if (loadingDialog.isAdded()) {
      loadingDialog.dismiss();
    }
  }

  private void handleSignOutResult(@NonNull Boolean result) {
    if (result) {
      NavOptions options = NavigationUtils.getNavOptions(R.id.homeFragment, true);
      navController.navigate(R.id.action_to_authentication, null, options);
    } else {
      Toast.makeText(requireContext(), getString(R.string.error_sign_out), Toast.LENGTH_SHORT)
          .show();
    }
  }
}
