package com.optlab.banhangso.features.main.store.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.optlab.banhangso.features.main.store.models.mappers.RoleStoreUiModelMapper;
import com.optlab.banhangso.features.main.store.viewmodel.SelectStoreViewModel;
import com.optlab.banhangso.features.shared.adapter.StoreListAdapter;
import com.optlab.banhangso.internal.utilities.NavigationUtils;
import com.optlab.banhangso.internal.utilities.itemspacing.LinearSpacingStrategy;
import com.optlab.banhangso.internal.utilities.itemspacing.SpacingItemDecoration;
import com.optlab.banhangso.repositories.interfaces.PreferenceRepository;
import dagger.hilt.android.AndroidEntryPoint;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

@AndroidEntryPoint
public class SelectStoreFragment extends Fragment implements View.OnClickListener {
    @Inject FirebaseAuth firebaseAuth;
    @Inject PreferenceRepository preferenceRepository;
    private FragmentSelectStoreBinding binding;
    private SelectStoreViewModel viewModel;
    private StoreListAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disableBackNavigation();
        viewModel = new ViewModelProvider(this).get(SelectStoreViewModel.class);
        adapter = new StoreListAdapter(this::handleStoreSelection);
    }

    private void handleStoreSelection(RoleStoreUiModel roleStoreUiModel) {
        if (roleStoreUiModel == null) {
            Timber.e("Store is null, cannot proceed with selection");
            return;
        }
        preferenceRepository.setStore(RoleStoreUiModelMapper.toDomain(roleStoreUiModel));
        NavOptions options = NavigationUtils.getNavOptions(R.id.selectStoreFragment, true);
        NavHostFragment.findNavController(this).navigate(R.id.homeFragment, null, options);
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
        viewModel.getStores().observe(getViewLifecycleOwner(), this::extracted);
        viewModel
                .getIsLoading()
                .observe(
                        getViewLifecycleOwner(),
                        isLoading -> binding.swStores.setRefreshing(isLoading));
    }

    private void extracted(List<RoleStoreUiModel> stores) {
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

    private void setupNavigationButtonClick() {
        binding.mtb.setNavigationOnClickListener(
                v -> {
                    firebaseAuth.signOut();
                    preferenceRepository.clearPreferences();
                    NavOptions options =
                            NavigationUtils.getNavOptions(R.id.selectStoreFragment, true);
                    NavHostFragment.findNavController(SelectStoreFragment.this)
                            .navigate(R.id.nav_graph_authentication, null, options);
                });
    }

    @Override
    public void onClick(@NonNull View v) {
        if (v.getId() == R.id.mb_add_store) {
            Navigation.findNavController(v).navigate(NavGraphDirections.actionToEditStore(true));
        }
    }
}
