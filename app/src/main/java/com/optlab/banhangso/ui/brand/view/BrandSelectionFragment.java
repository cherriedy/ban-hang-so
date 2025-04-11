package com.optlab.banhangso.ui.brand.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.optlab.banhangso.data.repository.BrandRepository;
import com.optlab.banhangso.databinding.FragmentOptionSelectionBinding;
import com.optlab.banhangso.ui.adapter.BrandSelectionAdapter;
import com.optlab.banhangso.ui.brand.viewmodel.BrandSelectionViewModel;

import dagger.hilt.android.AndroidEntryPoint;

import timber.log.Timber;

import java.util.Collections;

import javax.inject.Inject;

/**
 * Fragment that displays a selectable list of brands with search functionality. This fragment
 * allows users to select a brand and passes the selection back to the previous fragment through the
 * Navigation component's SavedStateHandle.
 */
@AndroidEntryPoint
public class BrandSelectionFragment extends Fragment {
    /** Key used for storing the selected brand position in SavedStateHandle */
    public static final String KEY_CHECKED_POSITION = "checked_brand_position_key";
    @Inject protected BrandRepository repository;
    private FragmentOptionSelectionBinding binding;
    private BrandSelectionViewModel viewModel;
    private BrandSelectionAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentOptionSelectionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(BrandSelectionViewModel.class);
        setupBrandListAdapter();
        handleSearchView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * This method is called when an item in the adapter is selected. It retrieves the checked
     * position and sets it in the previous back stack entry's SavedStateHandle.
     *
     * @param checkedPosition The position of the selected item in the RecyclerView.
     */
    private void onItemSelected(int checkedPosition) {
        if (checkedPosition != RecyclerView.NO_POSITION) {
            NavController navController = NavHostFragment.findNavController(this);
            NavBackStackEntry prevBackStackEntry = navController.getPreviousBackStackEntry();

            if (prevBackStackEntry != null) {
                prevBackStackEntry
                        .getSavedStateHandle()
                        .set(
                                KEY_CHECKED_POSITION,
                                checkedPosition); // Set back the checked position
            } else {
                Timber.e("Previous back stack entry is null.");
            }
        }
    }

    /**
     * Sets up the RecyclerView adapter for displaying brands. Retrieves the brand ID from
     * arguments, initializes the adapter with the correct selection, and observes the brand list
     * from the ViewModel.
     */
    private void setupBrandListAdapter() {
        // Get the arguments passed to this fragment, specifically the brand ID.
        BrandSelectionFragmentArgs args = BrandSelectionFragmentArgs.fromBundle(requireArguments());

        // Initialize the RecyclerView adapter with a listener for item selection.
        adapter = new BrandSelectionAdapter(this::onItemSelected);

        // Set the initial checked position in the adapter based on the brand ID passed.
        adapter.setCheckedPosition(repository.getPositionById(args.getBrandId()));

        binding.listOption.setHasFixedSize(true);
        binding.listOption.setAdapter(adapter);

        // Observe the checked position in the ViewModel and update the adapter accordingly.
        viewModel
                .getBrands()
                .observe(
                        getViewLifecycleOwner(),
                        brands -> {
                            if (brands == null || brands.isEmpty()) {
                                adapter.submitList(Collections.emptyList());
                                Timber.e("Brand list is empty or null.");
                            } else {
                                adapter.submitList(brands);
                            }
                        });
    }

    /** Update the search query in the ViewModel when the text changes. */
    private void handleSearchView() {
        binding.svKeyword.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        viewModel.setSearchQuery(
                                newText); // Update the search query in the ViewModel
                        return true;
                    }
                });
    }
}
