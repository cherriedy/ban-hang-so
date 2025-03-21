package com.optlab.banhangso.ui.product.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.optlab.banhangso.ui.adapter.ProductSortSelectionAdapter;
import com.optlab.banhangso.databinding.FragmentProductSortSelectionBinding;
import com.optlab.banhangso.factory.ProductSortSelectionViewModelFactory;
import com.optlab.banhangso.data.model.Product;
import com.optlab.banhangso.data.repository.ProductSortOptionRepository;
import com.optlab.banhangso.data.model.SortOption;
import com.optlab.banhangso.util.UserPreferenceManager;
import com.optlab.banhangso.ui.product.viewmodel.ProductSortSelectionViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;
import java.util.Objects;

public class ProductSortSelectionFragment extends BottomSheetDialogFragment {

    private final ProductSortOptionRepository repository = ProductSortOptionRepository.getInstance();
    private FragmentProductSortSelectionBinding binding;
    private ProductSortSelectionViewModel viewModel;
    private ProductSortSelectionAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpViewModel();
        setUpRecyclerView();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProductSortSelectionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.rvSortSelection.setHasFixedSize(true);
        binding.rvSortSelection.setAdapter(adapter);
    }

    private void setUpRecyclerView() {
        // Get an instance of user preference manager to get saved sort option.
        UserPreferenceManager userPreferenceManager = new UserPreferenceManager(requireContext());

        // Retrieve the sort option list from ViewModel, throwing an exception whether it is null.
        List<SortOption<Product.SortField>> sortOptions = Objects.requireNonNull(viewModel.getSortOptions());

        adapter = new ProductSortSelectionAdapter(sortOptions, sortOption -> {
            userPreferenceManager.saveSortOption(sortOption);
            viewModel.setSortOption(sortOption);
        });

        SortOption<Product.SortField> savedSortOption = userPreferenceManager.getSortOption();
        adapter.setCheckedPosition(repository.getPosition(savedSortOption));
    }

    private void setUpViewModel() {
        // Find the associated controller with this fragment.
        NavController navController = NavHostFragment.findNavController(this);
        // Get the previous back stack entry for scoping to ViewModel.
        NavBackStackEntry previousBackStackEntry = Objects.requireNonNull(navController.getPreviousBackStackEntry());

        // Create the factory which receives repository as argument.
        ProductSortSelectionViewModelFactory factory = new ProductSortSelectionViewModelFactory(repository);

        viewModel = new ViewModelProvider(previousBackStackEntry, factory).get(ProductSortSelectionViewModel.class);
    }
}