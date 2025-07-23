package com.optlab.banhangso.features.main.product.views;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.paging.CombinedLoadStates;
import androidx.paging.LoadState;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import autodispose2.AutoDispose;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import com.optlab.banhangso.R;
import com.optlab.banhangso.databinding.FragmentProductListBinding;
import com.optlab.banhangso.features.main.product.adapters.ProductListAdapter;
import com.optlab.banhangso.features.main.product.viewmodels.ProductListViewModel;
import com.optlab.banhangso.internal.utilities.itemspacing.GridSpacingStrategy;
import com.optlab.banhangso.internal.utilities.itemspacing.LinearSpacingStrategy;
import com.optlab.banhangso.internal.utilities.itemspacing.SpacingItemDecoration;
import com.optlab.banhangso.internal.utilities.itemspacing.SpacingStrategy;
import com.optlab.banhangso.repositories.interfaces.CategoryRepository;
import dagger.hilt.android.AndroidEntryPoint;
import java.util.EnumSet;
import javax.inject.Inject;
import kotlin.Unit;
import timber.log.Timber;

@AndroidEntryPoint
public class ProductListFragment extends Fragment {

  @Inject CategoryRepository categoryRepository;

  private FragmentProductListBinding binding;
  private ProductListViewModel viewModel;
  private ProductListAdapter listAdapter;
  private NavController navController;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    viewModel = new ViewModelProvider(this).get(ProductListViewModel.class);
    listAdapter = new ProductListAdapter(this::navigateToEdit);
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
    navController = NavHostFragment.findNavController(this);

    setupRecyclerView();
    observeViewModel();
    registerProductListRefreshListener();
  }

  private void registerProductListRefreshListener() {
    getParentFragmentManager()
        .setFragmentResultListener(
            ProductEditFragment.PRODUCT_EDIT_REQUEST,
            getViewLifecycleOwner(),
            (requestKey, result) -> handleProductRefresh(result));
  }

  private void handleProductRefresh(@NonNull Bundle result) {
    if (result.getBoolean(ProductEditFragment.REFRESH_FLAG, false)) {
      listAdapter.refresh();
    }
  }

  private void setupRecyclerView() {
    setupProductLoadingState();
    setupProductItemSpacing();

    binding.rvProducts.setAdapter(listAdapter);
    binding.rvProducts.setHasFixedSize(true);
    binding.rvProducts.setLayoutManager(new LinearLayoutManager(requireContext()));
  }

  private void setupProductLoadingState() {
    binding.srlProducts.setOnRefreshListener(listAdapter::refresh);

    listAdapter.addLoadStateListener(
        loadStates -> {
          handlePagingLoadState(loadStates);
          return Unit.INSTANCE;
        });
  }

  private void handlePagingLoadState(@NonNull CombinedLoadStates loadStates) {
    if (binding == null) {
      Timber.w("Binding is null, skipping load state handling");
      return;
    }
    // Show loading state when refreshing or initial load is in progress
    boolean isLoading = loadStates.getRefresh() instanceof LoadState.Loading;
    binding.srlProducts.setRefreshing(isLoading);

    // Show empty state when not loading and adapter has no items
    boolean isEmpty = !isLoading && listAdapter.getItemCount() == 0;
    binding.tvEmptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
  }

  private void setupProductItemSpacing() {
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

  private void observeViewModel() {
    viewModel
        .getProducts()
        .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
        .subscribe(
            pagingData -> {
              if (binding != null) binding.srlProducts.setRefreshing(false);
              listAdapter.submitData(getLifecycle(), pagingData);
            });

    // Observe the selected category and update the adapter.
    viewModel.getLayoutMode().observe(getViewLifecycleOwner(), this::toggleItemLayout);
  }

  /**
   * Dynamically sets up the RecyclerView layout as grid or linear based on a boolean flag.
   *
   * @param isGrid the boolean flag to determine the layout
   */
  private void toggleItemLayout(Boolean isGrid) {
    Context context = requireContext();

    // Set the item layout resource for the adapter based on the layout type.
    listAdapter.setItemLayoutRes(
        Boolean.TRUE.equals(isGrid) ? R.layout.grid_item_product : R.layout.list_item_product);

    // Set the layout manager for the RecyclerView based on the layout type.
    binding.rvProducts.setLayoutManager(
        Boolean.TRUE.equals(isGrid)
            ? new GridLayoutManager(context, 2)
            : new LinearLayoutManager(context));

    // Add spacing decoration to the RecyclerView based on the layout type.
    addSpacingDecoration(
        binding.rvProducts,
        Boolean.TRUE.equals(isGrid)
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

  private void navigateToEdit(String productId) {
    NavDirections action = ProductListFragmentDirections.actionToProductEdit(productId, true);
    NavHostFragment.findNavController(this).navigate(action);
  }

  /**
   * @noinspection unused
   */
  public void navigateToCreate(@NonNull View view) {
    NavDirections action = ProductListFragmentDirections.actionToProductEdit("", false);
    navController.navigate(action);
  }
}
