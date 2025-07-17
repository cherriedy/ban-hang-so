package com.optlab.banhangso.features.main.brand.view;

import static com.optlab.banhangso.features.shared.utilities.LoadStateErrorUtil.handleLoadStateError;

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
import androidx.navigation.fragment.NavHostFragment;
import androidx.paging.CombinedLoadStates;
import androidx.paging.LoadState;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.optlab.banhangso.R;
import com.optlab.banhangso.databinding.FragmentBrandListBinding;
import com.optlab.banhangso.features.main.brand.adapters.BrandListAdapter;
import com.optlab.banhangso.features.main.brand.callbacks.SwipeToDeleteCallback;
import com.optlab.banhangso.features.main.brand.viewmodel.BrandListViewModel;
import com.optlab.banhangso.features.main.product.views.ProductTabHostFragmentDirections;
import com.optlab.banhangso.features.shared.views.DeleteConfirmationDialog;
import com.optlab.banhangso.internal.utilities.itemspacing.LinearSpacingStrategy;
import com.optlab.banhangso.internal.utilities.itemspacing.SpacingItemDecoration;

import java.util.EnumSet;

import autodispose2.AutoDispose;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import dagger.hilt.android.AndroidEntryPoint;
import kotlin.Unit;
import timber.log.Timber;

@AndroidEntryPoint
public class BrandListFragment extends Fragment {

  public static final String BRAND_LIST_REQUEST_KEY = "BRAND_LIST_REQUEST_KEY";
  public static final String BRAND_REFRESH_FLAG = "BRAND_REFRESH_FLAG";

  private static final String PENDING_DELETE_BRAND_ID = "PENDING_DELETE_BRAND_ID";

  private FragmentBrandListBinding binding;
  private BrandListViewModel viewModel;
  private BrandListAdapter listAdapter;
  private NavController navController;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    viewModel = new ViewModelProvider(this).get(BrandListViewModel.class);
    listAdapter = new BrandListAdapter(this::navigateToEdit);
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentBrandListBinding.inflate(inflater, container, false);
    binding.setLifecycleOwner(getViewLifecycleOwner());
    binding.setViewModel(viewModel);
    binding.setFragment(this);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    navController = NavHostFragment.findNavController(this);

    initRecyclerView();
    observeViewModel();
    registerDeleteConfirmationListener();
    registerBrandListRefreshListener();
  }

  /**
   * @noinspection unused
   */
  public void navigateToCreate(@NonNull View view) {
    NavDirections action = ProductTabHostFragmentDirections.actionToBrandEdit("", false);
    navController.navigate(action);
  }

  private void registerDeleteConfirmationListener() {
    getParentFragmentManager()
        .setFragmentResultListener(
            DeleteConfirmationDialog.REQUEST,
            getViewLifecycleOwner(),
            (requestKey, result) -> handleDeleteConfirmation(result));
  }

  private void registerBrandListRefreshListener() {
    requireActivity()
        .getSupportFragmentManager()
        .setFragmentResultListener(
            BRAND_LIST_REQUEST_KEY,
            getViewLifecycleOwner(),
            (requestKey, result) -> handleBrandRefresh(result));
  }

  private void handleBrandRefresh(@NonNull Bundle result) {
    if (result.getBoolean(BRAND_REFRESH_FLAG, false)) {
      listAdapter.refresh();
    }
  }

  private void handleDeleteConfirmation(@NonNull Bundle result) {
    if (!result.getBoolean(DeleteConfirmationDialog.DELETED)) {
      listAdapter.refresh();
    } else {
      Bundle args = getArguments();
      if (args != null) {
        String brandId = args.getString(PENDING_DELETE_BRAND_ID);

        if (brandId != null) {
          viewModel.deleteBrand(brandId);
        } else {
          // No pending brand ID, log an error or handle it gracefully
          Timber.e("Unable to delete, there is no pending brand ID");
          showToast(R.string.error_unknown);
        }
      }
    }
  }

  private void showToast(@NonNull Integer messageResId) {
    Toast.makeText(requireContext(), getString(messageResId), Toast.LENGTH_SHORT).show();
  }

  private void observeViewModel() {
    viewModel
        .getBrands()
        .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
        .subscribe(
            categories -> {
              Timber.d("Received brands from ViewModel");
              listAdapter.submitData(getLifecycle(), categories);
            });

    viewModel.getMessageResId().observe(getViewLifecycleOwner(), this::showToast);
    viewModel
        .getDeletionCompleted()
        .observe(getViewLifecycleOwner(), unit -> listAdapter.refresh());
  }

  private void initRecyclerView() {
    setupBrandLoadingState();
    setupBrandItemSpacing();
    setupBrandSwipeToDelete();

    binding.rvBrands.setHasFixedSize(true);
    binding.rvBrands.setAdapter(listAdapter);
  }

  private void setupBrandLoadingState() {
    binding.srlBrands.setOnRefreshListener(listAdapter::refresh);

    listAdapter.addLoadStateListener(
        loadStates -> {
          handlePagingLoadState(loadStates);
          handleLoadStateError(requireContext(), loadStates);
          return Unit.INSTANCE;
        });
  }

  private void handlePagingLoadState(@NonNull CombinedLoadStates loadStates) {
    // Show loading state when refreshing or initial load is in progress
    boolean isLoading = loadStates.getRefresh() instanceof LoadState.Loading;
    binding.srlBrands.setRefreshing(isLoading);

    // Show empty state when not loading and adapter has no items
    boolean isEmpty = !isLoading && listAdapter.getItemCount() == 0;
    binding.tvEmptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
  }

  private void setupBrandSwipeToDelete() {
    SwipeToDeleteCallback swipeToDeleteCallback =
        new SwipeToDeleteCallback(
            requireContext(), listAdapter, this::showDeleteConfirmationDialog);
    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
    itemTouchHelper.attachToRecyclerView(binding.rvBrands);
  }

  private void showDeleteConfirmationDialog(@NonNull String brandId) {
    String title = getString(R.string.alter_brand_delete_title);
    String message = getString(R.string.alert_category_delete_message);
    DeleteConfirmationDialog deleteConfirmationDialog =
        DeleteConfirmationDialog.newInstance(title, message);

    Bundle args = new Bundle();
    args.putString(PENDING_DELETE_BRAND_ID, brandId);
    setArguments(args);

    deleteConfirmationDialog.show(
        getParentFragmentManager(), "deleteConfirmationDialog_" + this.getClass().getSimpleName());
  }

  private void setupBrandItemSpacing() {
    while (binding.rvBrands.getItemDecorationCount() > 0) {
      binding.rvBrands.removeItemDecorationAt(0);
    }
    binding.rvBrands.addItemDecoration(
        new SpacingItemDecoration(
            new LinearSpacingStrategy(
                requireContext(), 8, EnumSet.allOf(LinearSpacingStrategy.Direction.class))));
  }

  private void navigateToEdit(@NonNull String brandId) {
    NavDirections action = ProductTabHostFragmentDirections.actionToBrandEdit(brandId, true);
    navController.navigate(action);
  }
}
