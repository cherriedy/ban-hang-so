package com.optlab.banhangso.ui.brand.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.optlab.banhangso.ui.adapter.BrandSelectionAdapter;
import com.optlab.banhangso.databinding.FragmentOptionSelectionBinding;
import com.optlab.banhangso.factory.BrandSelectionViewModelFactory;
import com.optlab.banhangso.data.model.Brand;
import com.optlab.banhangso.data.repository.BrandRepository;
import com.optlab.banhangso.ui.brand.viewmodel.BrandSelectionViewModel;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import timber.log.Timber;

public class BrandSelectionFragment extends Fragment {

    public static final String KEY_CHECKED_POSITION = "checked_brand_position_key";

    private final BrandRepository repository = BrandRepository.getInstance();

    private FragmentOptionSelectionBinding binding;
    private BrandSelectionViewModel viewModel;
    private BrandSelectionAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOptionSelectionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViewModel();

        setupBrandListAdapter();

        handleSearchView();

        viewModel.getCheckedPosition().observe(getViewLifecycleOwner(), position -> {
            Toast.makeText(requireContext(), "Position: " + position, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initViewModel() {
        BrandSelectionViewModelFactory factory = new BrandSelectionViewModelFactory(repository);
        viewModel = new ViewModelProvider(this, factory).get(BrandSelectionViewModel.class);
    }

    /**
     * Initializes and binds the BrandSelectionAdapter to the RecyclerView.
     *
     * <p>This method sets up the adapter with an item click listener that updates the selected brand position in the ViewModel.
     * It configures the RecyclerView once and observes the brand list from the repository, updating the adapter accordingly.
     * If the brand list is empty or null, it logs an error and submits an empty list to clear any previous data.</p>
     */
    private void setupBrandListAdapter() {

        BrandSelectionFragmentArgs args = BrandSelectionFragmentArgs.fromBundle(requireArguments());

        // Initialize the adapter with an on-item-click listener that updates the ViewModel.
        adapter = new BrandSelectionAdapter(checkedPosition -> {
            if (checkedPosition != RecyclerView.NO_POSITION) {
                // Find the NavController associated with this fragment.
                NavController navController = NavHostFragment.findNavController(this);

                // Get the previous back stack entry (ProductEditFragment).
                NavBackStackEntry previousBackStackEntry = navController.getPreviousBackStackEntry();

                // Send the data back to previous fragment (ProductEditFragment).
                assert previousBackStackEntry != null;
                previousBackStackEntry.getSavedStateHandle().set(KEY_CHECKED_POSITION, checkedPosition);
            }
        });

        adapter.setCheckedPosition(repository.getPositionById(args.getBrandId()));

        binding.listOption.setHasFixedSize(true);

        // Bind the adapter to the `RecyclerView`.
        binding.listOption.setAdapter(adapter);

        // Observe the brand list from the repository.
        repository.getBrands().observe(getViewLifecycleOwner(), brands -> {
            // If brands list is null or empty, log an error and submit an empty list.
            if (brands == null) {
                Timber.e("Received an empty list of brand from repository.");
                // Submit the list of brands to the adapter to display
                adapter.submitList(Collections.emptyList());
            } else {
                // Otherwise, submit the received list to the adapter.
                adapter.submitList(brands);
            }
        });
    }

    private void handleSearchView() {
        binding.svKeyword.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getFilteredList(newText);
                return true;
            }
        });
    }

    /**
     * Filters the list of brands based on the provided keyword and updates the adapter.
     *
     * <p>If the keyword is empty, the full list of brands is displayed. Otherwise, it filters the list
     * to include only those brands whose names contain the keyword (case-insensitive).</p>
     *
     * @param keyword The search keyword used to filter the brands.
     */
    private void getFilteredList(String keyword) {
        // Retrieve the current list of brands from the ViewModel,
        // if the list is null, use an empty list as the fallback
        List<Brand> filterBrands = Optional.ofNullable(viewModel.getBrands().getValue())
                // Map the non-null list to either the original or a filtered list based on the keyword
                .map(brands -> keyword.isEmpty()
                        // If the keyword is empty, return the original list of brands
                        ? brands
                        // Otherwise, filter the list to include only brands whose names contain the keyword
                        : brands.stream()
                        // Convert brand name to lowercase for a case-insensitive comparison
                        .filter(brand -> brand.getName()
                                .toLowerCase()
                                .contains(keyword.toLowerCase()))
                        // Collect the filtered brands into a new list
                        .collect(Collectors.toList()))
                // If the ViewModel's brand list is null, default to an empty list
                .orElse(Collections.emptyList());

        // Update the adapter with the filtered list, triggering a UI refresh.
        adapter.submitList(filterBrands);
    }
}