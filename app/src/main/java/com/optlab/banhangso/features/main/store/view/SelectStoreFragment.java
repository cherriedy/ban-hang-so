package com.optlab.banhangso.features.main.store.view;

import static com.optlab.banhangso.features.shared.utilities.LoadStateUtils.isLoading;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import autodispose2.AutoDispose;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import com.optlab.banhangso.NavGraphDirections;
import com.optlab.banhangso.R;
import com.optlab.banhangso.databinding.FragmentSelectStoreBinding;
import com.optlab.banhangso.features.main.store.adapters.RoleStoreListAdapter;
import com.optlab.banhangso.features.main.store.callbacks.SwipeToDeleteCallback;
import com.optlab.banhangso.features.main.store.models.RoleStoreUiModel;
import com.optlab.banhangso.features.main.store.viewmodel.SelectStoreViewModel;
import com.optlab.banhangso.features.shared.views.DeleteConfirmationDialog;
import com.optlab.banhangso.features.shared.views.LoadingDialog;
import com.optlab.banhangso.internal.utilities.itemspacing.LinearSpacingStrategy;
import com.optlab.banhangso.internal.utilities.itemspacing.SpacingItemDecoration;
import dagger.hilt.android.AndroidEntryPoint;
import kotlin.Unit;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

@AndroidEntryPoint
public class SelectStoreFragment extends Fragment implements View.OnClickListener {

  public static final String SELECT_STORE_REQUEST_KEY = "SELECT_STORE_REQUEST_KEY";
  public static final String STORE_REFRESH_KEY = "STORE_REFRESH_KEY";

  private static final String PENDING_DELETE_STORE_ID = "PENDING_DELETE_STORE_ID";

  private final LoadingDialog loadingDialog = new LoadingDialog();

  private FragmentSelectStoreBinding binding;
  private SelectStoreViewModel viewModel;
  private RoleStoreListAdapter listAdapter;
  private NavController navController;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    viewModel = new ViewModelProvider(this).get(SelectStoreViewModel.class);
    listAdapter = new RoleStoreListAdapter(this::handleStoreSelection);
    disableBackNavigation();
  }

  @Override
  public void onDestroyView() {
    binding = null;
    super.onDestroyView();
  }

  private void handleStoreSelection(RoleStoreUiModel roleStoreUiModel) {
    if (roleStoreUiModel != null) {
      viewModel.setSelectedStore(roleStoreUiModel);
    } else {
      Toast.makeText(
              requireContext(), getString(R.string.error_while_selecting_store), Toast.LENGTH_SHORT)
          .show();
    }
  }

  private void disableBackNavigation() {
    requireActivity()
        .getOnBackPressedDispatcher()
        .addCallback(
            this,
            new OnBackPressedCallback(true) {
              @Override
              public void handleOnBackPressed() {
                Timber.d("Back navigation is disabled in SelectStoreFragment");
              }
            });
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentSelectStoreBinding.inflate(inflater, container, false);
    binding.setLifecycleOwner(getViewLifecycleOwner());
    binding.setViewModel(viewModel);
    binding.setFragment(this);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    navController = NavHostFragment.findNavController(this);
    binding.srlStores.setOnRefreshListener(() -> listAdapter.refresh());
    setupNavigationButton();
    setupStoreRecyclerView();

    observeViewModel();

    registerStoreRefreshListener();
    registerDeleteConfirmationListener();
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
        String storeId = args.getString(PENDING_DELETE_STORE_ID);

        if (storeId != null) {
          viewModel.deleteStore(storeId);
        } else {
          // No pending store ID, log an error or handle it gracefully
          Timber.e("Unable to delete, there is no pending store ID");
          showToast(R.string.error_unknown);
        }
      }
    }
  }

  private void registerStoreRefreshListener() {
    requireActivity()
        .getSupportFragmentManager()
        .setFragmentResultListener(
            SELECT_STORE_REQUEST_KEY,
            getViewLifecycleOwner(),
            (requestKey, result) -> {
              boolean refresh = result.getBoolean(STORE_REFRESH_KEY, false);
              if (refresh) {
                listAdapter.refresh();
              }
            });
  }

  private void setupNavigationButton() {
    SelectStoreFragmentArgs args = SelectStoreFragmentArgs.fromBundle(requireArguments());
    if (args.getDisableNavigationButton()) {
      binding.mtb.setNavigationIcon(null);
    } else {
      binding.mtb.setNavigationOnClickListener(v -> viewModel.onSignOut());
    }
  }

  private void observeViewModel() {
    viewModel.isLoading().observe(getViewLifecycleOwner(), this::handleLoadingState);
    viewModel.getSelectStoreResult().observe(getViewLifecycleOwner(), this::handleSetStoreResult);
    viewModel.getRefresh().observe(getViewLifecycleOwner(), this::triggerRefresh);
    viewModel.getMessageResId().observe(getViewLifecycleOwner(), this::showToast);
    viewModel
        .getStores()
        .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
        .subscribe(stores -> listAdapter.submitData(getLifecycle(), stores));
  }

  private void triggerRefresh(@NotNull Boolean shouldRefresh) {
    if (shouldRefresh) {
      listAdapter.refresh();
    }
  }

  private void handleLoadingState(@NonNull Boolean result) {
    if (result && !loadingDialog.isAdded()) {
      loadingDialog.show(
          getParentFragmentManager(), "loadingDialog_" + this.getClass().getSimpleName());
    } else if (loadingDialog.isAdded()) {
      loadingDialog.dismiss();
    }
  }

  private void showToast(int messageResId) {
    Toast.makeText(requireContext(), messageResId, Toast.LENGTH_SHORT).show();
  }

  private void handleSetStoreResult(@NonNull Boolean result) {
    if (result) {
      // Navigation to home screen after store selection.
      navController.navigate(R.id.action_to_home);
    }
  }

  private void setupStoreRecyclerView() {
    setupStoreItemSpacing();
    setupResultLoadingState();
    setupSwipeToDeleteCallback();

    binding.rvStores.setAdapter(listAdapter);
  }

  private void setupSwipeToDeleteCallback() {
    SwipeToDeleteCallback swipeToDeleteCallback =
        new SwipeToDeleteCallback(
            requireContext(), listAdapter, this::showDeleteConfirmationDialog);
    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
    itemTouchHelper.attachToRecyclerView(binding.rvStores);
  }

  private void showDeleteConfirmationDialog(@NonNull String storeId) {
    String title = getString(R.string.alter_store_delete_title);
    String message = getString(R.string.alter_store_delete_message);
    DeleteConfirmationDialog deleteConfirmationDialog =
        DeleteConfirmationDialog.newInstance(title, message);

    Bundle args = new Bundle();
    args.putString(PENDING_DELETE_STORE_ID, storeId);
    setArguments(args); // Set the pending store ID in the fragment arguments

    deleteConfirmationDialog.show(
        getParentFragmentManager(), "deleteConfirmationDialog_" + this.getClass().getSimpleName());
  }

  private void setupResultLoadingState() {
    listAdapter.addLoadStateListener(
        loadState -> {
          boolean isLoading = isLoading(loadState);
          binding.srlStores.setRefreshing(isLoading);

          boolean isEmpty = !isLoading && listAdapter.getItemCount() == 0;
          binding.rvStores.setVisibility(isEmpty ? View.GONE : View.VISIBLE);

          return Unit.INSTANCE;
        });
  }

  private void setupStoreItemSpacing() {
    LinearSpacingStrategy linearSpacingStrategy = new LinearSpacingStrategy(requireContext(), 8);
    binding.rvStores.addItemDecoration(new SpacingItemDecoration(linearSpacingStrategy));
  }

  @Override
  public void onClick(@NonNull View view) {
    if (view.getId() == R.id.mb_add_store) {
      navController.navigate(NavGraphDirections.actionToEditStore(false));
    }
  }
}
