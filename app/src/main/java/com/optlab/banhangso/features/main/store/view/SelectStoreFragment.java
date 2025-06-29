package com.optlab.banhangso.features.main.store.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.optlab.banhangso.NavGraphDirections;
import com.optlab.banhangso.R;
import com.optlab.banhangso.databinding.FragmentSelectStoreBinding;
import com.optlab.banhangso.features.main.store.models.RoleStoreUiModel;
import com.optlab.banhangso.features.main.store.viewmodel.SelectStoreViewModel;
import com.optlab.banhangso.features.shared.adapter.StoreListAdapter;
import com.optlab.banhangso.internal.utilities.NavigationUtils;
import com.optlab.banhangso.internal.utilities.itemspacing.LinearSpacingStrategy;
import com.optlab.banhangso.internal.utilities.itemspacing.SpacingItemDecoration;
import com.optlab.banhangso.repositories.interfaces.PreferencesRepository;
import dagger.hilt.android.AndroidEntryPoint;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.Contract;
import timber.log.Timber;

@AndroidEntryPoint
public class SelectStoreFragment extends Fragment implements View.OnClickListener {
  @Inject FirebaseAuth firebaseAuth;
  @Inject PreferencesRepository preferencesRepository;
  private FragmentSelectStoreBinding binding;
  private SelectStoreViewModel viewModel;
  private StoreListAdapter adapter;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    viewModel = new ViewModelProvider(this).get(SelectStoreViewModel.class);
    adapter = new StoreListAdapter(this::handleStoreSelection);

    disableBackNavigation();
  }

  @Override
  public void onDestroyView() {
    binding = null;
    super.onDestroyView();
  }

  private void handleStoreSelection(RoleStoreUiModel roleStoreUiModel) {
    if (roleStoreUiModel != null) {
      viewModel.setSelectedStore(roleStoreUiModel);
    } else {
      Toast.makeText(
              requireContext(), getString(R.string.error_while_selecting_store), Toast.LENGTH_SHORT)
          .show();
    }
  }

  private void disableBackNavigation() {
    requireActivity()
        .getOnBackPressedDispatcher()
        .addCallback(
            this,
            new OnBackPressedCallback(true) {
              @Override
              public void handleOnBackPressed() {
                Timber.d("Back navigation is disabled in SelectStoreFragment");
              }
            });
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentSelectStoreBinding.inflate(inflater, container, false);
    binding.setLifecycleOwner(getViewLifecycleOwner());
    binding.setViewModel(viewModel);
    binding.setFragment(this);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    SelectStoreFragmentArgs args = SelectStoreFragmentArgs.fromBundle(requireArguments());

    if (!args.getDisableNavigationButton()) {
      setupNavigationButtonClick();
    } else {
      binding.mtb.setNavigationIcon(null);
    }

    binding.swStores.setOnRefreshListener(() -> viewModel.retrieveStores());

    setupStoreRecyclerView();
    observeViewModel();
  }

  private void observeViewModel() {
    viewModel.getStores().observe(getViewLifecycleOwner(), this::handleFetchedStores);
    viewModel.getIsLoading().observe(getViewLifecycleOwner(), binding.swStores::setRefreshing);
    viewModel.getSetStoreResult().observe(getViewLifecycleOwner(), this::handleSetStoreResult);
  }

  @Contract(pure = true)
  private void handleSetStoreResult(@NonNull Boolean result) {
    if (result) {
      NavHostFragment.findNavController(this).navigate(R.id.action_to_home);
    }
  }

  private void handleFetchedStores(List<RoleStoreUiModel> stores) {
    if (stores != null && !stores.isEmpty()) {
      Timber.d("Stores fetched successfully: %s", stores);
      adapter.submitList(stores);
    } else {
      Timber.w("No stores found or stores list is empty");
      adapter.submitList(null);
    }
  }

  private void setupStoreRecyclerView() {
    binding.rvStores.setAdapter(adapter);
    LinearSpacingStrategy strategy = new LinearSpacingStrategy(requireContext(), 8);
    binding.rvStores.addItemDecoration(new SpacingItemDecoration(strategy));
  }

  @SuppressLint({"CheckResult", "AutoDispose"})
  private void setupNavigationButtonClick() {
    binding.mtb.setNavigationOnClickListener(
        v -> {
          firebaseAuth.signOut();
          preferencesRepository
              .clearPreferences()
              .subscribe(
                  () -> {
                    NavOptions options =
                        NavigationUtils.getNavOptions(R.id.selectStoreFragment, true);
                    NavHostFragment.findNavController(SelectStoreFragment.this)
                        .navigate(R.id.auth_navigation, null, options);
                  },
                  throwable -> Timber.e(throwable, "Failed to clear preferences"));
        });
  }

  @Override
  public void onClick(@NonNull View v) {
    if (v.getId() == R.id.mb_add_store) {
      Navigation.findNavController(v).navigate(NavGraphDirections.actionToEditStore(true));
    }
  }
}
