package com.optlab.banhangso.ui.category.viewmodel;

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

import com.optlab.banhangso.ui.adapter.CategorySelectionAdapter;
import com.optlab.banhangso.databinding.FragmentOptionSelectionBinding;
import com.optlab.banhangso.factory.CategorySelectionViewModelFactory;
import com.optlab.banhangso.data.model.Category;
import com.optlab.banhangso.data.repository.CategoryRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import timber.log.Timber;

public class CategorySelectionFragment extends Fragment {

    public static final String KEY_CHECKED_POSITION = "checked_category_position_key";

    private final CategoryRepository repository = CategoryRepository.getInstance();

    private FragmentOptionSelectionBinding binding;
    private CategorySelectionViewModel viewModel;
    private CategorySelectionAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOptionSelectionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViewModel();

        setupCategoryListAdapter();

        handleSearchView();
    }

    private void setupCategoryListAdapter() {

        CategorySelectionFragmentArgs args = CategorySelectionFragmentArgs.fromBundle(requireArguments());

        adapter = new CategorySelectionAdapter(checkedPosition -> {
            if (checkedPosition != RecyclerView.NO_POSITION) {
                // Get the current NavController associated with the fragment.
                NavController navController = NavHostFragment.findNavController(this);

                // Find the previous back stack to send the data back.
                NavBackStackEntry previousBackStackEntry = navController.getPreviousBackStackEntry();

                // Send the data back to previous fragment (ProductEditFragment).
                assert previousBackStackEntry != null;
                previousBackStackEntry.getSavedStateHandle().set(KEY_CHECKED_POSITION, checkedPosition);
            }
        });

        adapter.setCheckedPosition(repository.getPositionById(args.getCategoryId()));

        binding.listOption.setAdapter(adapter);

        binding.listOption.setHasFixedSize(true);

        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories == null) {
                Timber.e("Received an empty list of category from repository.");
                adapter.submitList(Collections.emptyList());
            } else {
                adapter.submitList(categories);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Set binding to null to prevent memory leak
        binding = null;
    }

    private void initViewModel() {
        CategorySelectionViewModelFactory factory = new CategorySelectionViewModelFactory(repository);
        viewModel = new ViewModelProvider(this, factory).get(CategorySelectionViewModel.class);
    }

    /**
     * Add the query text listener to the search view.
     */
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
     * Collect the matching categories to the given keyword.
     *
     * <p>Retrieve the full list from view model, if it is null return an empty collection. If the keyword is empty,
     * the full list of categories is displayed. Otherwise, the filtered list gathers category matched the keyword.</p>
     *
     * @param keyword Type the keyword contained in the finding category.
     */
    private void getFilteredList(String keyword) {
        // Retrieve the categories which contains keyword. In case the initial list is null, return an empty list.
        List<Category> filteredCategories = Optional.ofNullable(viewModel.getCategories().getValue())
                // If the keyword is empty, return the retrieved list instead.
                .map(categories -> keyword.isEmpty()
                        ? categories
                        : categories.stream()
                        .filter(category -> category.getName()
                                .toLowerCase()
                                .contains(keyword.toLowerCase()))
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());

        // Submit the filtered list to the adapter for displaying.
        adapter.submitList(filteredCategories);
    }
}
