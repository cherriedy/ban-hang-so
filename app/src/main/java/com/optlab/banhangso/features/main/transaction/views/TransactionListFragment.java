package com.optlab.banhangso.features.main.transaction.views;

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
import androidx.paging.LoadState;
import autodispose2.AutoDispose;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import com.optlab.banhangso.R;
import com.optlab.banhangso.databinding.FragmentTransactionListBinding;
import com.optlab.banhangso.features.main.transaction.adapters.TransactionSummaryListAdapter;
import com.optlab.banhangso.features.main.transaction.viewmodels.TransactionListViewModel;
import com.optlab.banhangso.internal.utilities.itemspacing.LinearSpacingStrategy;
import com.optlab.banhangso.internal.utilities.itemspacing.SpacingItemDecoration;
import com.optlab.banhangso.models.application.FilterParams;
import dagger.hilt.android.AndroidEntryPoint;
import java.util.EnumSet;
import kotlin.Unit;

@AndroidEntryPoint
public class TransactionListFragment extends Fragment {

  private FragmentTransactionListBinding binding;
  private TransactionListViewModel viewModel;
  private TransactionSummaryListAdapter listAdapter;
  private NavController navController;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    viewModel = new ViewModelProvider(this).get(TransactionListViewModel.class);
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
    navController = NavHostFragment.findNavController(this);
    binding.srlTransactions.setOnRefreshListener(listAdapter::refresh);

    setupToolbar();
    setupRecyclerView();
    observeViewModel();

    registerFiltersSelectedListener();
  }

  private void registerFiltersSelectedListener() {
    getParentFragmentManager()
        .setFragmentResultListener(
            TransactionFiltersBottomSheet.GET_FILTERS_REQUEST,
            getViewLifecycleOwner(),
            (requestKey, result) -> {
              FilterParams.FilterParamsBuilder builder = FilterParams.builder();

              handleIntervalFilterResult(result, builder);
              handlePaymentFilterResult(result, builder);
              handleDateFromFilterResult(result, builder);
              handleDateToFilterResult(result, builder);
              handlePriceFromFilterResult(result, builder);
              handlePriceToFilterResult(result, builder);

              FilterParams filterParams = builder.build();
              viewModel.setFilterParams(filterParams);
            });
  }

  private void handlePriceToFilterResult(
      @NonNull Bundle result, @NonNull FilterParams.FilterParamsBuilder builder) {
    if (result.containsKey(TransactionFiltersBottomSheet.PRICE_RANGE_TO)) {
      Double priceTo = result.getDouble(TransactionFiltersBottomSheet.PRICE_RANGE_TO);
      Toast.makeText(requireContext(), String.valueOf(priceTo), Toast.LENGTH_SHORT).show();
      builder.priceTo(priceTo);
    }
  }

  private void handlePriceFromFilterResult(
      @NonNull Bundle result, @NonNull FilterParams.FilterParamsBuilder builder) {
    if (result.containsKey(TransactionFiltersBottomSheet.PRICE_RANGE_FROM)) {
      Double priceFrom = result.getDouble(TransactionFiltersBottomSheet.PRICE_RANGE_FROM);
      Toast.makeText(requireContext(), String.valueOf(priceFrom), Toast.LENGTH_SHORT).show();
      builder.priceFrom(priceFrom);
    }
  }

  private void handleDateFromFilterResult(
      @NonNull Bundle result, @NonNull FilterParams.FilterParamsBuilder builder) {
    if (result.containsKey(TransactionFiltersBottomSheet.DATE_FROM_RESULT)) {
      String dateFrom = result.getString(TransactionFiltersBottomSheet.DATE_FROM_RESULT);
      if (dateFrom != null) {
        Toast.makeText(requireContext(), dateFrom, Toast.LENGTH_SHORT).show();
        builder.startDate(dateFrom);
      }
    }
  }

  private void handleDateToFilterResult(
      @NonNull Bundle result, @NonNull FilterParams.FilterParamsBuilder builder) {
    if (result.containsKey(TransactionFiltersBottomSheet.DATE_TO_RESULT)) {
      String dateTo = result.getString(TransactionFiltersBottomSheet.DATE_TO_RESULT);
      if (dateTo != null) {
        Toast.makeText(requireContext(), dateTo, Toast.LENGTH_SHORT).show();
        builder.endDate(dateTo);
      }
    }
  }

  private void handlePaymentFilterResult(
      @NonNull Bundle result, @NonNull FilterParams.FilterParamsBuilder builder) {
    if (result.containsKey(TransactionFiltersBottomSheet.PAYMENT_FILTER_RESULT)) {
      String paymentMethod = result.getString(TransactionFiltersBottomSheet.PAYMENT_FILTER_RESULT);
      if (paymentMethod != null) {
        Toast.makeText(requireContext(), paymentMethod, Toast.LENGTH_SHORT).show();
        builder.payment(paymentMethod);
      }
    }
  }

  private void handleIntervalFilterResult(
      @NonNull Bundle result, @NonNull FilterParams.FilterParamsBuilder builder) {
    if (result.containsKey(TransactionFiltersBottomSheet.INTERVAL_FILTER_RESULT)) {
      String interval = result.getString(TransactionFiltersBottomSheet.INTERVAL_FILTER_RESULT);
      if (interval != null) {
        Toast.makeText(requireContext(), interval, Toast.LENGTH_SHORT).show();
        builder.endDate(interval);
      }
    }
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
          boolean isLoading = loadStates.getRefresh() instanceof LoadState.Loading;
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
