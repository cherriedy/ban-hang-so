package com.optlab.banhangso.features.main.product.views;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import autodispose2.AutoDispose;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import com.optlab.banhangso.R;
import com.optlab.banhangso.databinding.FragmentProductListBinding;
import com.optlab.banhangso.features.main.product.adapter.ProductListAdapter;
import com.optlab.banhangso.features.main.product.viewmodels.ProductListViewModel;
import com.optlab.banhangso.internal.utilities.itemspacing.GridSpacingStrategy;
import com.optlab.banhangso.internal.utilities.itemspacing.LinearSpacingStrategy;
import com.optlab.banhangso.internal.utilities.itemspacing.SpacingItemDecoration;
import com.optlab.banhangso.internal.utilities.itemspacing.SpacingStrategy;
import com.optlab.banhangso.repositories.interfaces.CategoryRepository;
import dagger.hilt.android.AndroidEntryPoint;
import java.util.EnumSet;
import javax.inject.Inject;

@AndroidEntryPoint
public class ProductListFragment extends Fragment {
  @Inject CategoryRepository categoryRepository;

  private FragmentProductListBinding binding;
  private ProductListViewModel viewModel;
  private ProductListAdapter listAdapter;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initViewModels();
    initAdapters();
  }

  private void initAdapters() {
    // Setup adapter for RecyclerView of products with callback to edit a specific product.
    listAdapter = new ProductListAdapter(this::navigateToEditFragment);
  }

  /** Navigates to the EditFragment when a product is clicked. */
  private void navigateToEditFragment(String productId) {
    NavDirections action = ProductListFragmentDirections.actionToProductEdit(productId, false);
    NavHostFragment.findNavController(this).navigate(action);
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentProductListBinding.inflate(inflater, container, false);
    binding.setLifecycleOwner(getViewLifecycleOwner());
    binding.setViewModel(viewModel);
    binding.setFragment(this);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setupRecyclerViews();
    observeViewModels();
  }

  private void setupRecyclerViews() {
    // Trigger refreshing when user swipes the top of layout.
    binding.srlProducts.setOnRefreshListener(() -> listAdapter.refresh());

    binding.rvProducts.setAdapter(listAdapter);
    binding.rvProducts.setHasFixedSize(true);
    binding.rvProducts.setLayoutManager(new LinearLayoutManager(requireContext()));
    // Set the item layout resource for the adapter based on the layout type.
    SpacingStrategy spacingStrategy =
        new LinearSpacingStrategy(
            requireContext(), 8, EnumSet.allOf(LinearSpacingStrategy.Direction.class));
    addSpacingDecoration(binding.rvProducts, spacingStrategy);
  }

  @Override
  public void onDestroyView() {
    binding = null;
    super.onDestroyView();
  }

  private <T extends ViewModel> T getParentViewModel(Class<T> viewModelClass) {
    return new ViewModelProvider(requireParentFragment()).get(viewModelClass);
  }

  private void initViewModels() {
    viewModel = getParentViewModel(ProductListViewModel.class);
  }

  private void observeViewModels() {
    viewModel
        .getProducts()
        .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
        .subscribe(
            pagingData -> {
              binding.srlProducts.setRefreshing(false);
              listAdapter.submitData(getLifecycle(), pagingData);
            });

    // Observe the selected category and update the adapter.
    viewModel.getLayoutMode().observe(getViewLifecycleOwner(), this::toggleItemLayout);

    // Observe the selected sort option and update the ViewModel.
    //        tabHostSharedViewModel
    //                .getProductSortOption()
    //                .observe(getViewLifecycleOwner(), viewModel::setSortOption);
  }

  /**
   * Dynamically sets up the RecyclerView layout as grid or linear based on a boolean flag.
   *
   * @param isGrid the boolean flag to determine the layout
   */
  private void toggleItemLayout(Boolean isGrid) {
    Context context = requireContext();

    // Set the item layout resource for the adapter based on the layout type.
    listAdapter.setItemLayoutRes(isGrid ? R.layout.item_grid_product : R.layout.list_item_product);

    // Set the layout manager for the RecyclerView based on the layout type.
    binding.rvProducts.setLayoutManager(
        isGrid ? new GridLayoutManager(context, 2) : new LinearLayoutManager(context));

    // Add spacing decoration to the RecyclerView based on the layout type.
    addSpacingDecoration(
        binding.rvProducts,
        isGrid
            ? new GridSpacingStrategy(context, 8)
            : new LinearSpacingStrategy(
                context, 8, EnumSet.allOf(LinearSpacingStrategy.Direction.class)));
  }

  /**
   * Adds spacing decoration to the RecyclerView.
   *
   * @param view the RecyclerView to add the decoration to
   * @param spacingStrategy the spacing strategy to use for the decoration
   */
  private void addSpacingDecoration(@NonNull RecyclerView view, SpacingStrategy spacingStrategy) {
    while (view.getItemDecorationCount() > 0) {
      view.removeItemDecorationAt(0);
    }
    view.addItemDecoration(new SpacingItemDecoration(spacingStrategy));
  }

  /** Called when the user clicks the "Create" button to navigate to the EditFragment. */
  public void onCreateButtonClick(@NonNull View view) {
    NavDirections action = ProductListFragmentDirections.actionToProductEdit("", true);
    Navigation.findNavController(view).navigate(action);
  }
}
