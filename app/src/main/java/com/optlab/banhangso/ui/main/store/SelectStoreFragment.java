package com.optlab.banhangso.ui.main.store;

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
import com.optlab.banhangso.R;
import com.optlab.banhangso.data.local.database.BanHangSoDatabase;
import com.optlab.banhangso.databinding.FragmentSelectStoreBinding;
import com.optlab.banhangso.domain.model.Store;
import com.optlab.banhangso.domain.repository.PreferenceRepository;
import com.optlab.banhangso.ui.base.adapter.StoreListAdapter;
import com.optlab.banhangso.ui.base.decoration.LinearSpacingStrategy;
import com.optlab.banhangso.ui.base.decoration.SpacingItemDecoration;
import com.optlab.banhangso.util.NavigationUtils;
import dagger.hilt.android.AndroidEntryPoint;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.inject.Inject;
import timber.log.Timber;

@AndroidEntryPoint
public class SelectStoreFragment extends Fragment {
    @Inject protected FirebaseAuth firebaseAuth;
    @Inject protected PreferenceRepository preferenceRepository;
    @Inject protected BanHangSoDatabase database;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private FragmentSelectStoreBinding binding;
    private SelectStoreViewModel viewModel;
    private StoreListAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disableBackNavigation();
        viewModel = new ViewModelProvider(this).get(SelectStoreViewModel.class);
        adapter = new StoreListAdapter(this::onStoreSelected);
    }

    private void onStoreSelected(Store store) {
        if (store == null) {
            Timber.e("Store is null, cannot proceed with selection");
            return;
        }
        preferenceRepository.setSelectedStoreId(store.getId());
//        preferenceRepository.setSelectedStoreName(store.getName());
//        Snackbar.make(requireView(), store.getName(), Snackbar.LENGTH_SHORT).show();
        NavOptions options = NavigationUtils.getNavOptions(R.id.selectStoreFragment, true);
        NavHostFragment.findNavController(this).navigate(R.id.homeFragment, options);
    }

    private void disableBackNavigation() {
        requireActivity()
                .getOnBackPressedDispatcher()
                .addCallback(
                        this,
                        new OnBackPressedCallback(true) {
                            @Override
                            public void handleOnBackPressed() {}
                        });
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSelectStoreBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setPrefRepo(preferenceRepository);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onNavigationButtonClick();
        setupStoreRecyclerList();
        observeViewModel();
    }

    @Override
    public void onDestroy() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
        super.onDestroy();
    }

    private void observeViewModel() {
        viewModel.getStores().observe(getViewLifecycleOwner(), adapter::submitList);
    }

    private void setupStoreRecyclerList() {
        binding.rvStores.setAdapter(adapter);
        LinearSpacingStrategy strategy = new LinearSpacingStrategy(requireContext(), 8);
        binding.rvStores.addItemDecoration(new SpacingItemDecoration(strategy));
    }

    private void onNavigationButtonClick() {
        binding.mtb.setNavigationOnClickListener(
                v -> {
                    firebaseAuth.signOut();
                    preferenceRepository.clearAllPreferences();
                    executor.execute(() -> database.clearAllTables());
                    NavOptions options =
                            NavigationUtils.getNavOptions(R.id.selectStoreFragment, true);
                    Navigation.findNavController(v)
                            .navigate(R.id.nav_graph_authentication, null, options);
                });
    }
}
