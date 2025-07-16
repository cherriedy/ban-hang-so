package com.optlab.banhangso.features.main.transaction.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.optlab.banhangso.databinding.FragmentTransactionDetailBinding;
import com.optlab.banhangso.features.main.transaction.adapters.TransactionRecordListAdapter;
import com.optlab.banhangso.features.main.transaction.viewmodels.TransactionDetailViewModel;
import com.optlab.banhangso.features.shared.views.LoadingDialog;
import com.optlab.banhangso.internal.utilities.itemspacing.LinearSpacingStrategy;
import com.optlab.banhangso.internal.utilities.itemspacing.SpacingItemDecoration;
import dagger.hilt.android.AndroidEntryPoint;
import java.util.EnumSet;

@AndroidEntryPoint
public class TransactionDetailFragment extends Fragment {

  private final LoadingDialog loadingDialog = new LoadingDialog();
  private FragmentTransactionDetailBinding binding;
  private TransactionDetailViewModel viewModel;
  private TransactionRecordListAdapter listAdapter;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    TransactionDetailFragmentArgs args =
        TransactionDetailFragmentArgs.fromBundle(requireArguments());
    viewModel = new ViewModelProvider(this).get(TransactionDetailViewModel.class);
    viewModel.getTransactionById(args.getTransactionId());
    listAdapter = new TransactionRecordListAdapter();
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentTransactionDetailBinding.inflate(inflater, container, false);
    binding.setLifecycleOwner(getViewLifecycleOwner());
    binding.setViewModel(viewModel);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    binding.mtb.setNavigationOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    setupRecyclerView();
    observeViewModel();
  }

  private void observeViewModel() {
    viewModel.isLoading().observe(getViewLifecycleOwner(), this::handleLoadingState);
    viewModel
        .getTransaction()
        .observe(
            getViewLifecycleOwner(), transaction -> listAdapter.submitList(transaction.getItems()));
  }

  private void handleLoadingState(Boolean isLoading) {
    if (!loadingDialog.isAdded()) {
      loadingDialog.show(
          getParentFragmentManager(), "loadingDialog_" + this.getClass().getSimpleName());
    } else {
      loadingDialog.dismissAllowingStateLoss();
    }
  }

  private void setupRecyclerView() {
    LinearSpacingStrategy linearSpacingStrategy =
        new LinearSpacingStrategy(
            requireContext(), 8, EnumSet.allOf(LinearSpacingStrategy.Direction.class));
    binding.rvTransactions.addItemDecoration(new SpacingItemDecoration(linearSpacingStrategy));

    binding.rvTransactions.setAdapter(listAdapter);
  }
}
