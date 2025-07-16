package com.optlab.banhangso.features.main.category.views;

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
import autodispose2.AutoDispose;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import com.optlab.banhangso.R;
import com.optlab.banhangso.databinding.FragmentCategoryListBinding;
import com.optlab.banhangso.features.main.category.adapters.CategoryListAdapter;
import com.optlab.banhangso.features.main.category.callbacks.SwipeToDeleteCallback;
import com.optlab.banhangso.features.main.category.viewmodel.CategoryListViewModel;
import com.optlab.banhangso.features.main.product.views.ProductTabHostFragmentDirections;
import com.optlab.banhangso.features.shared.views.DeleteConfirmationDialog;
import com.optlab.banhangso.internal.utilities.itemspacing.LinearSpacingStrategy;
import com.optlab.banhangso.internal.utilities.itemspacing.SpacingItemDecoration;
import dagger.hilt.android.AndroidEntryPoint;
import java.util.EnumSet;
import kotlin.Unit;
import timber.log.Timber;

@AndroidEntryPoint
public class CategoryListFragment extends Fragment {

  private static final String PENDING_DELETE_CATEGORY_ID = "PENDING_DELETE_CATEGORY_ID";

  private FragmentCategoryListBinding binding;
  private CategoryListViewModel viewModel;
  private CategoryListAdapter listAdapter;
  private NavController navController;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    viewModel = new ViewModelProvider(this).get(CategoryListViewModel.class);
    listAdapter = new CategoryListAdapter(this::navigateToEdit);
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentCategoryListBinding.inflate(inflater, container, false);
    binding.setLifecycleOwner(this);
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
  }

  /**
   * @noinspection unused
   */
  public void navigateToCreate(@NonNull View view) {
    NavDirections action = ProductTabHostFragmentDirections.actionToCategoryEdit("", false);
    navController.navigate(action);
  }

  private void observeViewModel() {
    viewModel
        .getCategories()
        .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
        .subscribe(categories -> listAdapter.submitData(getLifecycle(), categories));

    viewModel.getMessageResId().observe(getViewLifecycleOwner(), this::showToast);
    viewModel
        .getDeletionCompleted()
        .observe(getViewLifecycleOwner(), unit -> listAdapter.refresh());
  }

  private void showToast(@NonNull Integer messageResId) {
    Toast.makeText(requireContext(), getString(messageResId), Toast.LENGTH_SHORT).show();
  }

  private void initRecyclerView() {
    setupCategoryLoadingState();
    setupCategoryItemSpacing();
    setupCategorySwipeToDelete();

    binding.rvCategories.setHasFixedSize(true);
    binding.rvCategories.setAdapter(listAdapter);
  }

  private void setupCategoryLoadingState() {
    binding.srlCategories.setOnRefreshListener(listAdapter::refresh);

    listAdapter.addLoadStateListener(
        loadStates -> {
          handlePagingLoadState(loadStates);
          return Unit.INSTANCE;
        });
  }

  private void setupCategoryItemSpacing() {
    while (binding.rvCategories.getItemDecorationCount() > 0) {
      binding.rvCategories.removeItemDecorationAt(0);
    }

    binding.rvCategories.addItemDecoration(
        new SpacingItemDecoration(
            new LinearSpacingStrategy(
                requireContext(), 8, EnumSet.allOf(LinearSpacingStrategy.Direction.class))));
  }

  private void handlePagingLoadState(@NonNull CombinedLoadStates loadStates) {
    // Show loading state when refreshing or initial load is in progress
    boolean isLoading = loadStates.getRefresh() instanceof LoadState.Loading;
    binding.srlCategories.setRefreshing(isLoading);

    // Show empty state when not loading and adapter has no items
    boolean isEmpty = !isLoading && listAdapter.getItemCount() == 0;
    binding.tvEmptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
  }

  private void setupCategorySwipeToDelete() {
    SwipeToDeleteCallback swipeToDeleteCallback =
        new SwipeToDeleteCallback(
            requireContext(), listAdapter, this::showDeleteConfirmationDialog);
    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
    itemTouchHelper.attachToRecyclerView(binding.rvCategories);
  }

  private void showDeleteConfirmationDialog(@NonNull String categoryId) {
    String title = getString(R.string.alter_category_delete_title);
    String message = getString(R.string.alert_category_delete_message);
    DeleteConfirmationDialog deleteConfirmationDialog =
        DeleteConfirmationDialog.newInstance(title, message);

    Bundle args = new Bundle();
    args.putString(PENDING_DELETE_CATEGORY_ID, categoryId);
    setArguments(args);

    deleteConfirmationDialog.show(
        getParentFragmentManager(), "deleteConfirmationDialog_" + this.getClass().getSimpleName());
  }

  private void registerDeleteConfirmationListener() {
    getParentFragmentManager()
        .setFragmentResultListener(
            DeleteConfirmationDialog.REQUEST,
            getViewLifecycleOwner(),
            (requestKey, result) -> handleDeleteConfirmation(result));
  }

  private void handleDeleteConfirmation(@NonNull Bundle result) {
    if (!result.getBoolean(DeleteConfirmationDialog.DELETED)) {
      listAdapter.refresh(); // User cancelled, refresh the adapter to restore the item
    } else {
      Bundle args = getArguments();
      if (args != null) {
        String categoryId = args.getString(PENDING_DELETE_CATEGORY_ID);

        if (categoryId != null) {
          viewModel.deleteCategory(categoryId);
        } else {
          // No pending category ID, log an error or handle it gracefully
          Timber.e("Unable to delete, there is no pending category ID");
          showToast(R.string.error_unknown);
        }
      }
    }
  }

  private void navigateToEdit(@NonNull String id) {
    NavDirections action = ProductTabHostFragmentDirections.actionToCategoryEdit(id, true);
    navController.navigate(action);
  }
}
