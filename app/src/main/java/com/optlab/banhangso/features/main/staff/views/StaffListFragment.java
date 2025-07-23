package com.optlab.banhangso.features.main.staff.views;

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
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import autodispose2.AutoDispose;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import com.optlab.banhangso.databinding.FragmentStaffListBinding;
import com.optlab.banhangso.features.main.staff.adapters.StaffListAdapter;
import com.optlab.banhangso.features.main.staff.models.StaffUiModel;
import com.optlab.banhangso.features.main.staff.viewmodels.StaffListViewModel;
import com.optlab.banhangso.internal.utilities.itemspacing.LinearSpacingStrategy;
import com.optlab.banhangso.internal.utilities.itemspacing.SpacingItemDecoration;
import com.optlab.banhangso.internal.utilities.itemspacing.SpacingStrategy;
import dagger.hilt.android.AndroidEntryPoint;
import java.util.EnumSet;
import kotlin.Unit;

@AndroidEntryPoint
public class StaffListFragment extends Fragment {

  public static final String STAFF_LIST_REQUEST_KEY = "STAFF_LIST_REQUEST_KEY";
  public static final String STAFF_REFRESH_FLAG = "STAFF_REFRESH_FLAG";

  private FragmentStaffListBinding binding;
  private StaffListViewModel viewModel;
  private StaffListAdapter listAdapter;
  private NavController navController;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    viewModel = new ViewModelProvider(this).get(StaffListViewModel.class);
    listAdapter = new StaffListAdapter(this::navigateToEdit);
  }

  private void navigateToEdit(@NonNull StaffUiModel staff) {
    NavDirections action = StaffListFragmentDirections.actionToStaffEdit(staff.getId(), false);
    navController.navigate(action);
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentStaffListBinding.inflate(inflater, container, false);
    binding.setLifecycleOwner(getViewLifecycleOwner());
    binding.setViewModel(viewModel);
    binding.setFragment(this);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    navController = NavHostFragment.findNavController(this);
    binding.mtb.setNavigationOnClickListener(v -> navController.navigateUp());
    setUpRecyclerView();
    observeViewModel();
  }

  /**
   * @noinspection unused
   */
  public void navigateToCreate(@NonNull View view) {
    NavDirections action = StaffListFragmentDirections.actionToStaffEdit("", true);
    navController.navigate(action);
  }

  private void observeViewModel() {
    viewModel
        .getStaffs()
        .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
        .subscribe(pagingData -> listAdapter.submitData(getLifecycle(), pagingData));

    registerStaffRefreshListener();
  }

  private void registerStaffRefreshListener() {
    requireActivity()
        .getSupportFragmentManager()
        .setFragmentResultListener(
            STAFF_LIST_REQUEST_KEY,
            getViewLifecycleOwner(),
            (requestKey, result) -> {
              boolean refresh = result.getBoolean(STAFF_REFRESH_FLAG, false);
              if (refresh) {
                listAdapter.refresh();
              }
            });
  }

  private void setUpRecyclerView() {
    binding.srlStaffs.setOnRefreshListener(listAdapter::refresh);

    setupStaffItemSpacing();
    setupStaffLoadState();

    binding.rvStaffs.setHasFixedSize(true);
    binding.rvStaffs.setAdapter(listAdapter);
  }

  private void setupStaffLoadState() {
    listAdapter.addLoadStateListener(
        loadState -> {
          boolean isLoading = isLoading(loadState);
          binding.srlStaffs.setRefreshing(isLoading);

          boolean isEmpty = !isLoading && listAdapter.getItemCount() == 0;
          binding.tvEmptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);

          handleLoadStateError(requireContext(), loadState);
          return Unit.INSTANCE;
        });
  }

  private void setupStaffItemSpacing() {
    SpacingStrategy spacingStrategy =
        new LinearSpacingStrategy(
            requireContext(), 8, EnumSet.allOf(LinearSpacingStrategy.Direction.class));
    binding.rvStaffs.addItemDecoration(new SpacingItemDecoration(spacingStrategy));
  }
}
