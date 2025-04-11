package com.optlab.banhangso.ui.product.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.Observable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.optlab.banhangso.R;
import com.optlab.banhangso.data.model.Brand;
import com.optlab.banhangso.data.model.Category;
import com.optlab.banhangso.data.model.Product;
import com.optlab.banhangso.data.repository.BrandRepository;
import com.optlab.banhangso.data.repository.CategoryRepository;
import com.optlab.banhangso.databinding.FragmentProductEditBinding;
import com.optlab.banhangso.ui.brand.view.BrandSelectionFragment;
import com.optlab.banhangso.ui.category.view.CategorySelectionFragment;
import com.optlab.banhangso.ui.common.view.DeleteConfirmationDialog;
import com.optlab.banhangso.ui.common.view.ExitConfirmationDialog;
import com.optlab.banhangso.ui.product.viewmodel.ProductEditViewModel;

import java.util.Optional;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;

/**
 * This Fragment handles displaying and updating product details. It initializes the ViewModels,
 * binds data using View Binding, observes LiveData for product and brand updates, and configures
 * the UI accordingly.
 */
@AndroidEntryPoint
@SuppressWarnings({"SimplifiableIfStatement", "FieldCanBeLocal"})
public class ProductEditFragment extends Fragment {
    @Inject protected BrandRepository brandRepository;
    @Inject protected CategoryRepository categoryRepository;
    private FragmentProductEditBinding binding;
    private ProductEditViewModel viewModel;
    private NavBackStackEntry navBackStackEntry;
    private ProductEditFragmentArgs productEditFragmentArgs;
    private Observable.OnPropertyChangedCallback productPropertyChangedCallback;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        requireActivity()
                .getOnBackPressedDispatcher()
                .addCallback(
                        this,
                        new OnBackPressedCallback(true) {
                            @Override
                            public void handleOnBackPressed() {
                                // When the back button is pressed, show a confirmation dialog to
                                // the user.
                                showExitConfirmationDialog();
                            }
                        });

        // Set up a listener for the exit confirmation dialog result, which is triggered when the
        // user confirms or cancels the exit action.
        getParentFragmentManager()
                .setFragmentResultListener(
                        ExitConfirmationDialog.REQUEST,
                        this,
                        (requestKey, result) -> {
                            if (result.getBoolean(ExitConfirmationDialog.CONFIRMED)) {
                                Timber.d("User confirmed exit without saving.");
                                NavHostFragment.findNavController(this).navigateUp();
                            } else {
                                Timber.d("User canceled exit without saving.");
                            }
                        });
    }

    /** Shows a confirmation dialog when the user attempts to exit the fragment. */
    private void showExitConfirmationDialog() {
        new ExitConfirmationDialog()
                .show(getParentFragmentManager(), this.getClass().getSimpleName());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProductEditViewModel.class);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProductEditBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setViewModel(viewModel);
        binding.setFragment(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupNavigation();
        configureInteractionMode();
        setupBrandSelection();
        setupCategorySelection();
        observeViewModels();
        observeBrandSelectionChanges();
        observeCategorySelectionChanges();
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }

    private void setupNavigation() {
        // Set up the navigation controller and back stack entry for this fragment.
        NavController navController = NavHostFragment.findNavController(this);
        navBackStackEntry = navController.getCurrentBackStackEntry();

        // Retrieve the arguments passed to this fragment.
        productEditFragmentArgs = ProductEditFragmentArgs.fromBundle(requireArguments());
    }

    /** Configures the interaction mode of the fragment based on the arguments passed to it. */
    private void configureInteractionMode() {
        boolean isCreateMode = productEditFragmentArgs.getIsCreateMode();
        String productId = productEditFragmentArgs.getProductId();

        binding.setIsCreateMode(isCreateMode);
        binding.executePendingBindings();

        // If the fragment is in create mode, hide the delete button and divider and set the update
        // button text to "Complete".
        Timber.d("isCreateMode: %s", isCreateMode);
        if (!isCreateMode) {
            binding.viewDivider.setVisibility(View.VISIBLE);
            binding.btnDelete.setVisibility(View.VISIBLE);
        } else {
            binding.btnUpdate.setText(R.string.complete);
        }

        // Load the product details using the provided product ID.
        Timber.d("Product ID from arguments: %s", productId);
        viewModel.loadProductById(productId);
    }

    private void observeViewModels() {
        // Observe the product LiveData from the ViewModel and update the UI accordingly.
        viewModel.getProduct().observe(getViewLifecycleOwner(), this::onProductPropertyChanged);

        // Observe the updating state to show or hide the progress bar.
        viewModel.getUpdateState().observe(getViewLifecycleOwner(), this::toggleProgressDialog);
        // Observe the update result to show a success or failure message.
        viewModel
                .getUpdateResult()
                .observe(
                        getViewLifecycleOwner(),
                        isSuccessful -> {
                            if (isSuccessful) {
                                showSuccessState("Cập nhật sản phẩm thành công");
                            } else {
                                showErrorState("Cập nhật sản phẩm thất bại");
                            }
                        });

        // Observe the deleting state to show or hide the progress bar.
        viewModel.getDeleteState().observe(getViewLifecycleOwner(), this::toggleProgressDialog);
        // Observe the delete result to show a success or failure message.
        viewModel
                .getDeleteResult()
                .observe(
                        getViewLifecycleOwner(),
                        isSuccessful -> {
                            if (isSuccessful) {
                                showSuccessState("Xóa sản phẩm thành công");
                            } else {
                                showErrorState("Xóa sản phẩm thất bại");
                            }
                        });

        // Observe the create state to show or hide the progress bar.
        viewModel.getCreateState().observe(getViewLifecycleOwner(), this::toggleProgressDialog);
        // Observe the create result to show a success or failure message.
        viewModel
                .getCreateResult()
                .observe(
                        getViewLifecycleOwner(),
                        isSuccessful -> {
                            if (isSuccessful) {
                                showSuccessState("Tạo sản phẩm thành công");
                            } else {
                                showErrorState("Tạo sản phẩm thất bại");
                            }
                        });
    }

    /**
     * Shows a success state with a message and navigates back to the previous screen.
     *
     * @param message The message to display in the success state.
     */
    private void showSuccessState(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        NavHostFragment.findNavController(this).navigateUp();
    }

    /**
     * Shows an error state with a message.
     *
     * @param message The message to display in the error state.
     */
    private void showErrorState(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Sets up the click listener for the brand selection field.
     *
     * <p>This method retrieves the current product and its brand, then navigates to the brand
     * details fragment. It also disables the clickable behavior on the end icon to prevent user
     * accidentally clicking it.
     */
    private void setupBrandSelection() {
        binding.actvBrand.setOnClickListener(
                v -> {
                    // Retrieve the current product without creating a new observer, this is done to
                    // avoid unnecessary UI updates.
                    Product product = viewModel.getProduct().getValue();

                    // Get the brand id from the product, if it is null, set it to null.
                    String brandId =
                            (product == null || product.getBrand() == null)
                                    ? null
                                    : product.getBrand().getId();

                    // Navigate to the brand selection screen, passing the selected brand id.
                    NavDirections action =
                            ProductEditFragmentDirections.actionToBrandSelection(brandId);
                    Navigation.findNavController(v).navigate(action);
                });

        // Disable clickable behavior on the end icon to prevent accidental actions.
        binding.tilBrand.setEndIconCheckable(false);
    }

    /**
     * Sets up the click listener for the category selection field.
     *
     * <p>This method retrieves the current product and its category, then navigates to the category
     * details fragment. It also disables the clickable behavior on the end icon to prevent user
     * accidentally clicking it.
     */
    private void setupCategorySelection() {
        binding.actvCategory.setOnClickListener(
                v -> {
                    // Retrieve the current product without creating a new observer, this is done to
                    // avoid unnecessary UI updates.
                    Product product = viewModel.getProduct().getValue();

                    // Get the category id from the product, if it is null, set it to null.
                    String categoryId =
                            (product == null || product.getCategory() == null)
                                    ? null
                                    : product.getCategory().getId();

                    // Navigate to the category selection screen, passing the selected category id.
                    NavDirections action =
                            ProductEditFragmentDirections.actionToCategorySelection(categoryId);
                    Navigation.findNavController(v).navigate(action);
                });

        // Disable clickable behavior on the end icon to prevent accidental actions.
        binding.tilCategory.setEndIconCheckable(false);
    }

    /** Observes changes in brand selection and updates the product accordingly. */
    private void observeBrandSelectionChanges() {
        navBackStackEntry
                .getSavedStateHandle()
                .getLiveData(BrandSelectionFragment.KEY_CHECKED_POSITION)
                .observe(
                        getViewLifecycleOwner(),
                        checkedPosition -> {
                            // Transform the checkPosition to primitive value, if it is null set
                            // RecyclerView.NO_POSITION (-1) as default.
                            int pos =
                                    Optional.ofNullable(checkedPosition)
                                            .map(cp -> (int) cp)
                                            .orElse(RecyclerView.NO_POSITION);

                            // If the checked position is invalid, doing nothing.
                            if (pos == RecyclerView.NO_POSITION) return;

                            // Get the current instance of product.
                            Product product = viewModel.getProduct().getValue();
                            if (product == null) return;

                            Brand checkedBrand = brandRepository.getBrandByPosition(pos);
                            if (checkedBrand != null) {
                                if (product.getBrand() == null
                                        || !checkedBrand.equals(product.getBrand())) {
                                    product.setBrand(checkedBrand);
                                    viewModel.setProduct(product);
                                }
                            } else {
                                Timber.e("Brand not found for position: %d", pos);
                            }
                        });
    }

    /** Observes changes in category selection and updates the product accordingly. */
    private void observeCategorySelectionChanges() {
        navBackStackEntry
                .getSavedStateHandle()
                .getLiveData(CategorySelectionFragment.KEY_CHECKED_POSITION)
                .observe(
                        getViewLifecycleOwner(),
                        checkedPosition -> {
                            int pos =
                                    Optional.ofNullable(checkedPosition)
                                            .map(cp -> (int) cp)
                                            .orElse(RecyclerView.NO_POSITION);

                            if (pos == RecyclerView.NO_POSITION) return;

                            Product product = viewModel.getProduct().getValue();
                            if (product == null) return;

                            Category checkedCategory =
                                    categoryRepository.getCategoryByPosition(pos);
                            if (checkedCategory != null) {
                                if (product.getCategory() == null
                                        || !checkedCategory.equals(product.getCategory())) {
                                    product.setCategory(checkedCategory);
                                    viewModel.setProduct(product);
                                }
                            } else {
                                Timber.e("Category not found for position: %d", pos);
                            }
                        });
    }

    /**
     * Updates the text colors of the radio buttons based on the product's stock status.
     *
     * <p>If in stock, the "In Stock" radio button gets a primary color while the "Out of Stock"
     * button uses a secondary title color. Otherwise, the colors are swapped to indicate a warning.
     *
     * @param inStock True if the product is in stock; false otherwise.
     */
    private void updateRadioButtonColors(boolean inStock) {
        Context context = requireContext();
        int primaryColor = ContextCompat.getColor(context, R.color.color_primary);
        int titleColor = ContextCompat.getColor(context, R.color.color_text_title);
        int warningColor = ContextCompat.getColor(context, R.color.color_warning);
        if (inStock) {
            binding.rbInStock.setTextColor(primaryColor);
            binding.rbOutStock.setTextColor(titleColor);
        } else {
            binding.rbInStock.setTextColor(titleColor);
            binding.rbOutStock.setTextColor(warningColor);
        }
    }

    /**
     * Sets up property change callback for a Product object.
     *
     * <p>This method adds a callback to the provided product that monitors changes to its
     * properties. When a property changes, it calls the appropriate validation method on the view
     * model or updates UI elements based on the specific property. Property changes are identified
     * by their BR (Binding Resource) ID.
     *
     * <p>Properties handled include:
     *
     * <ul>
     *   <li>name - Validates the product name
     *   <li>sellingPrice - Validates the product's selling price
     *   <li>purchasePrice - Validates the product's purchase price
     *   <li>discountPrice - Validates the product's discount price
     *   <li>description - Validates the product description
     *   <li>note - Validates product notes
     *   <li>status - Updates radio button colors,and the update button state
     *   <li>brand - Updates the update button state
     *   <li>category - Updates the update button state
     * </ul>
     *
     * @param product The product to observe for property changes
     */
    private void onProductPropertyChanged(Product product) {
        if (productPropertyChangedCallback != null) {
            product.removeOnPropertyChangedCallback(productPropertyChangedCallback);
        }

        productPropertyChangedCallback =
                new Observable.OnPropertyChangedCallback() {
                    @Override
                    public void onPropertyChanged(Observable sender, int propertyId) {
                        switch (propertyId) {
                            case BR.name -> {
                                Timber.d("onPropertyChanged: name");
                                viewModel.validateName(product.getName());
                            }

                            case BR.sellingPrice -> {
                                Timber.d("onPropertyChanged: sellingPrice");
                                viewModel.validateSellingPrice(product.getSellingPrice());
                            }

                            case BR.purchasePrice -> {
                                Timber.d("onPropertyChanged: purchasePrice");
                                viewModel.validatePurchasePrice(product.getPurchasePrice());
                            }

                            case BR.discountPrice -> {
                                Timber.d("onPropertyChanged: discountPrice");
                                viewModel.validateDiscountPrice(product.getDiscountPrice());
                            }

                            case BR.description -> {
                                Timber.d("onPropertyChanged: description");
                                viewModel.validateDescription(product.getDescription());
                            }

                            case BR.note -> {
                                Timber.d("onPropertyChanged: note");
                                viewModel.validateNote(product.getNote());
                            }

                            case BR.status -> {
                                Timber.d("onPropertyChanged: status");
                                updateRadioButtonColors(product.getStatus());
                            }

                            case BR.brand -> {
                                Timber.d("onPropertyChanged: brand");
                                viewModel.validateBrand(product.getBrand());
                            }

                            case BR.category -> {
                                Timber.d("onPropertyChanged: category");
                                viewModel.validateCategory(product.getCategory());
                            }
                        }
                    }
                };

        product.addOnPropertyChangedCallback(productPropertyChangedCallback);
    }

    /**
     * Toggles the visibility of the progress dialog based on the loading state.
     *
     * @param isLoading True if loading; false otherwise.
     */
    private void toggleProgressDialog(Boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    /**
     * Handles the click event for the update button.
     *
     * <p>Display the confirmation dialog to the user and navigate to the product list screen if the
     * user confirms the action.
     *
     * @param view The view that was clicked.
     * @noinspection unused
     */
    public void onDeleteButtonClick(@NonNull View view) {
        DeleteConfirmationDialog deleteConfirmationDialog =
                DeleteConfirmationDialog.newInstance(
                        getString(R.string.alert_delete_product),
                        getString(R.string.alert_confirm_delete),
                        getString(R.string.refuse),
                        getString(R.string.agree));

        deleteConfirmationDialog.show(getParentFragmentManager(), this.getClass().getSimpleName());

        // Set up a listener for the delete confirmation dialog result. This is triggered when
        // the user confirms or cancels the delete action.
        getParentFragmentManager()
                .setFragmentResultListener(
                        DeleteConfirmationDialog.REQUEST,
                        this,
                        (requestKey, result) -> {
                            if (result.getBoolean(DeleteConfirmationDialog.RESULT)) {
                                viewModel.deleteProduct();
                            }
                        });
    }
}
