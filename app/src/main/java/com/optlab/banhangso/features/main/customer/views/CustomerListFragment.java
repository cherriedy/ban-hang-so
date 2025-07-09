package com.optlab.banhangso.features.main.customer.views;

import static com.optlab.banhangso.internal.utilities.ContactUtils.getDetails;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import com.optlab.banhangso.databinding.FragmentCustomerListBinding;
import com.optlab.banhangso.features.main.customer.adapters.CustomerListAdapter;
import com.optlab.banhangso.features.main.customer.models.CustomerUiModel;
import com.optlab.banhangso.features.main.customer.viewmodels.CustomerListViewModel;
import com.optlab.banhangso.internal.utilities.ContactUtils;
import com.optlab.banhangso.internal.utilities.itemspacing.LinearSpacingStrategy;
import com.optlab.banhangso.internal.utilities.itemspacing.SpacingItemDecoration;
import com.optlab.banhangso.internal.utilities.itemspacing.SpacingStrategy;

import org.jetbrains.annotations.Contract;

import java.util.EnumSet;
import java.util.Map;

import autodispose2.AutoDispose;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;

@AndroidEntryPoint
public class CustomerListFragment extends Fragment {

  private FragmentCustomerListBinding binding;
  private CustomerListViewModel viewModel;
  private CustomerListAdapter listAdapter;
  private NavController navController;

  private ActivityResultLauncher<Intent> pickContactLauncher;
  private ActivityResultLauncher<String> requestPermissionLauncher;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    viewModel = new ViewModelProvider(this).get(CustomerListViewModel.class);
    listAdapter =
        new CustomerListAdapter(
            this::navigateToDetail, this::navigateToDial, this::navigateToEmail);

    pickContactLauncher = registerContactPickerLauncher();
    requestPermissionLauncher = registerContactPermissionLauncher();
  }

  @NonNull private ActivityResultLauncher<String> registerContactPermissionLauncher() {
    return registerForActivityResult(
        new ActivityResultContracts.RequestPermission(),
        isGranted -> {
          if (Boolean.TRUE.equals(isGranted)) {
            launchContactPicker();
          } else {
            Timber.w("READ_CONTACTS permission denied");
          }
        });
  }

  @NonNull @Contract(" -> new")
  private ActivityResultLauncher<Intent> registerContactPickerLauncher() {
    return registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
          if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
            Uri contactUri = result.getData().getData();
            if (contactUri == null) {
              Timber.w("There was no URI returned from the contact picker.");
              return;
            }

            Map<String, String> details = getDetails(requireContext(), contactUri);
            CustomerUiModel customerUiModel =
                CustomerUiModel.builder()
                    .name(details.get(ContactUtils.KEY_NAME))
                    .phone(details.get(ContactUtils.KEY_PHONE))
                    .email(details.getOrDefault(ContactUtils.KEY_EMAIL, ""))
                    .address(details.getOrDefault(ContactUtils.KEY_ADDRESS, ""))
                    .imageUri(details.getOrDefault(ContactUtils.KEY_IMAGE_URI, ""))
                    .dob(details.getOrDefault(ContactUtils.KEY_DOB, ""))
                    .build();

            NavDirections action =
                CustomerListFragmentDirections.actionToEditWithParcelable(customerUiModel);
            navController.navigate(action);
          }
        });
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentCustomerListBinding.inflate(inflater, container, false);
    binding.setLifecycleOwner(getViewLifecycleOwner());
    binding.setViewModel(viewModel);
    binding.setFragment(this);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    navController = NavHostFragment.findNavController(this);
    setUpRecyclerView();
    observeViewModel();
  }

  /**
   * @noinspection unused
   */
  public void toggleCreateOptionsVisibility(@NonNull View view) {
    // Toggle the visibility of the overlay.
    toggleVisibility(binding.vOverlay);

    // Toggle the visibility of the import customer button and text.
    toggleVisibility(binding.fabImportContact);
    toggleVisibility(binding.tvImportContact);

    // Toggle the visibility of the add customer button and text.
    toggleVisibility(binding.fabAddCustomer);
    toggleVisibility(binding.tvAddCustomer);
  }

  /**
   * @noinspection unused
   */
  public void navigateToContacts(@NonNull View view) {
    toggleCreateOptionsVisibility(view);
    if (ContextCompat.checkSelfPermission(
            requireContext(), android.Manifest.permission.READ_CONTACTS)
        == PackageManager.PERMISSION_GRANTED) {
      launchContactPicker();
    } else {
      requestPermissionLauncher.launch(android.Manifest.permission.READ_CONTACTS);
    }
  }

  private void launchContactPicker() {
    pickContactLauncher.launch(
        new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI));
  }

  private void toggleVisibility(@NonNull View view) {
    int visibility = view.getVisibility();
    view.setVisibility(visibility == View.VISIBLE ? View.GONE : View.VISIBLE);
  }

  /**
   * @noinspection unused
   */
  public void navigateToCreate(@NonNull View view) {
    toggleCreateOptionsVisibility(view);
    NavDirections action = CustomerListFragmentDirections.actionToCreate();
    navController.navigate(action);
  }

  private void observeViewModel() {
    viewModel
        .getCustomers()
        .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
        .subscribe(
            pagingData -> {
              binding.srlCustomers.setRefreshing(false);
              listAdapter.submitData(getLifecycle(), pagingData);
            });
  }

  private void setUpRecyclerView() {
    binding.srlCustomers.setOnRefreshListener(listAdapter::refresh);

    SpacingStrategy spacingStrategy =
        new LinearSpacingStrategy(
            requireContext(), 8, EnumSet.allOf(LinearSpacingStrategy.Direction.class));
    binding.rvCustomers.addItemDecoration(new SpacingItemDecoration(spacingStrategy));

    binding.rvCustomers.setHasFixedSize(true);
    binding.rvCustomers.setAdapter(listAdapter);
  }

  private void navigateToDetail(@NonNull String customerId) {
    NavDirections action = CustomerListFragmentDirections.actionToEdit(customerId);
    navController.navigate(action);
  }

  private void navigateToDial(@NonNull String phoneNumber) {
    Intent intent = new Intent();
    intent.setAction(Intent.ACTION_DIAL);
    intent.setData(Uri.parse("tel:" + phoneNumber));
    startActivity(intent);
  }

  private void navigateToEmail(@NonNull String email) {
    Intent intent = new Intent();
    intent.setAction(Intent.ACTION_SENDTO);
    intent.setData(Uri.parse("mailto: "));
    intent.putExtra(Intent.EXTRA_EMAIL, new String[] {email});
    startActivity(intent);
  }
}
