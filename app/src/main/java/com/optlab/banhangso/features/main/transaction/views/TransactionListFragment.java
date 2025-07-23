package com.optlab.banhangso.features.main.transaction.views;

import static com.optlab.banhangso.features.shared.utilities.LoadStateUtils.handleLoadStateError;
import static com.optlab.banhangso.features.shared.utilities.LoadStateUtils.isLoading;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import autodispose2.AutoDispose;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import com.optlab.banhangso.R;
import com.optlab.banhangso.databinding.FragmentTransactionListBinding;
import com.optlab.banhangso.features.main.transaction.adapters.TransactionSummaryListAdapter;
import com.optlab.banhangso.features.main.transaction.viewmodels.TransactionFiltersViewModel;
import com.optlab.banhangso.features.main.transaction.viewmodels.TransactionListViewModel;
import com.optlab.banhangso.internal.utilities.itemspacing.LinearSpacingStrategy;
import com.optlab.banhangso.internal.utilities.itemspacing.SpacingItemDecoration;
import dagger.hilt.android.AndroidEntryPoint;
import java.util.EnumSet;
import kotlin.Unit;

@AndroidEntryPoint
public class TransactionListFragment extends Fragment {

  private FragmentTransactionListBinding binding;
  private TransactionListViewModel viewModel;
  private TransactionSummaryListAdapter listAdapter;
  private NavController navController;
  private TransactionFiltersViewModel filtersViewModel;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    viewModel = new ViewModelProvider(this).get(TransactionListViewModel.class);

    navController = NavHostFragment.findNavController(this);
    // Scope the filters view model to the back stack entry of the transaction list fragment.
    NavBackStackEntry stackEntry = navController.getBackStackEntry(R.id.transactionListFragment);
    filtersViewModel = new ViewModelProvider(stackEntry).get(TransactionFiltersViewModel.class);

    listAdapter =
        new TransactionSummaryListAdapter(
            transactionId -> {
              NavDirections action =
                  TransactionListFragmentDirections.actionToDetail(transactionId);
              navController.navigate(action);
            });
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentTransactionListBinding.inflate(inflater, container, false);
    binding.setLifecycleOwner(getViewLifecycleOwner());
    binding.setViewModel(viewModel);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    binding.srlTransactions.setOnRefreshListener(listAdapter::refresh);

    setupToolbar();
    setupRecyclerView();
    observeViewModel();
  }

  private void setupToolbar() {
    binding.mtb.setNavigationOnClickListener(v -> navController.navigateUp());
    binding.mtb.inflateMenu(R.menu.menu_transaction_toolbar);
    binding.mtb.setOnMenuItemClickListener(
        item -> {
          int itemId = item.getItemId();
          if (itemId == R.id.action_toggle_search) {
            toggleSearchViewVisibility(binding.mcvQuery.getVisibility() == View.VISIBLE);
            return true;
          } else if (itemId == R.id.action_select_filters) {
            NavDirections action = TransactionListFragmentDirections.actionToFilters();
            navController.navigate(action);
          }
          return false;
        });
  }

  private void toggleSearchViewVisibility(boolean visible) {
    if (visible) {
      binding
          .mcvQuery
          .animate()
          .alpha(0f)
          .setDuration(200)
          .withEndAction(() -> binding.mcvQuery.setVisibility(View.GONE))
          .start();
    } else {
      binding.mcvQuery.setAlpha(0f);
      binding.mcvQuery.setVisibility(View.VISIBLE);
      binding.mcvQuery.animate().alpha(1f).setDuration(200).start();
    }
  }

  private void observeViewModel() {
    viewModel
        .getTransactions()
        .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
        .subscribe(transactions -> listAdapter.submitData(getLifecycle(), transactions));

    // Observe the filter parameters from the filters view model.
    filtersViewModel.getFilterParams().observe(getViewLifecycleOwner(), viewModel::setFilterParams);
  }

  private void setupRecyclerView() {
    setupTransactionItemSpacing();
    setupTransactionLoadingState();
    binding.rvTransactions.setHasFixedSize(true);
    binding.rvTransactions.setAdapter(listAdapter);
  }

  private void setupTransactionLoadingState() {
    listAdapter.addLoadStateListener(
        loadStates -> {
          boolean isLoading = isLoading(loadStates);
          binding.srlTransactions.setRefreshing(isLoading);

          boolean isEmpty = !isLoading && listAdapter.getItemCount() == 0;
          binding.tvEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);

          handleLoadStateError(requireContext(), loadStates);

          return Unit.INSTANCE;
        });
  }

  private void setupTransactionItemSpacing() {
    LinearSpacingStrategy linearSpacingStrategy =
        new LinearSpacingStrategy(
            requireContext(), 8, EnumSet.allOf(LinearSpacingStrategy.Direction.class));
    binding.rvTransactions.addItemDecoration(new SpacingItemDecoration(linearSpacingStrategy));
  }
}
