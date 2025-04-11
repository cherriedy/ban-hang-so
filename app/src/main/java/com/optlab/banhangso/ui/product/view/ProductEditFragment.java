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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.optlab.banhangso.R;
import com.optlab.banhangso.data.model.Brand;
import com.optlab.banhangso.data.model.Category;
import com.optlab.banhangso.data.model.Product;
import com.optlab.banhangso.data.repository.BrandRepository;
import com.optlab.banhangso.data.repository.CategoryRepository;
import com.optlab.banhangso.databinding.FragmentProductEditBinding;
import com.optlab.banhangso.ui.common.view.DeleteConfirmationDialog;
import com.optlab.banhangso.ui.common.view.ExitConfirmationDialog;
import com.optlab.banhangso.ui.product.viewmodel.ProductEditSharedViewModel;
import com.optlab.banhangso.ui.product.viewmodel.ProductEditViewModel;

import java.util.function.Consumer;

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
    private ProductEditFragmentArgs args;
    private Observable.OnPropertyChangedCallback changedCallback;
    private ProductEditSharedViewModel sharedViewModel;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        setupExitConfimationDialog();
    }

    /** Sets up a callback for the back button press to show an exit confirmation dialog. */
    private void setupExitConfimationDialog() {
        requireActivity()
                .getOnBackPressedDispatcher()
                .addCallback(
                        this,
                        new OnBackPressedCallback(true) {
                            @Override
                            public void handleOnBackPressed() {
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
        initViewModels();
    }

    private void initViewModels() {
        viewModel = new ViewModelProvider(this).get(ProductEditViewModel.class);

        // Retrieve the back stack entry for the product edit graph and initialize the shared
        // ViewModel for getting the selected brand and category.
        NavBackStackEntry productEditBackEntry =
                NavHostFragment.findNavController(this)
                        .getBackStackEntry(R.id.nav_graph_product_edit);
        sharedViewModel =
                new ViewModelProvider(productEditBackEntry).get(ProductEditSharedViewModel.class);
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
        observeViewModels();
    }

    @Override
    public void onDestroyView() {
        binding = null;
        removePropertyChangedCallback();
        super.onDestroyView();
    }

    /** Removes the property changed callback from the product object to prevent memory leaks. */
    private void removePropertyChangedCallback() {
        viewModel.getProduct().getValue().removeOnPropertyChangedCallback(changedCallback);
        changedCallback = null;
    }

    private void setupNavigation() {
        args = ProductEditFragmentArgs.fromBundle(requireArguments());
        configureInteractionMode();
    }

    /** Configures the interaction mode of the fragment based on the arguments passed to it. */
    private void configureInteractionMode() {
        binding.setIsCreateMode(args.getIsCreateMode());
        binding.executePendingBindings();

        // Load the product based on the passed product ID.
        viewModel.loadProductById(args.getProductId());
    }

    private void observeViewModels() {
        observeBrandSelectionChanged();
        observeCategorySelectionChanged();

        viewModel.getProduct().observe(getViewLifecycleOwner(), this::onProductPropertyChanged);

        observeLoadingState(viewModel.getCreateState());
        observeLoadingState(viewModel.getUpdateState());
        observeLoadingState(viewModel.getDeleteState());

        observeOperationResult(
                viewModel.getCreateResult(),
                getString(R.string.notify_create_product_successfully),
                getString(R.string.notify_create_product_fail));
        observeOperationResult(
                viewModel.getUpdateResult(),
                getString(R.string.notify_update_product_successfully),
                getString(R.string.notify_update_product_fail));
        observeOperationResult(
                viewModel.getDeleteResult(),
                getString(R.string.notify_delete_product_successfully),
                getString(R.string.notify_delete_product_fail));
    }

    /**
     * Observes the loading state of a LiveData object and toggles the progress dialog visibility
     * based on the state.
     *
     * @param state The LiveData object representing the loading state.
     */
    private void observeLoadingState(LiveData<Boolean> state) {
        state.observe(getViewLifecycleOwner(), this::toggleProgressDialog);
    }

    /**
     * Observes the result of an operation (create, update, delete) and shows a toast message
     * indicating success or failure.
     *
     * @param result The LiveData object representing the result of the operation.
     * @param successMsg The message to show on success.
     * @param errorMsg The message to show on error.
     */
    private void observeOperationResult(
            LiveData<Boolean> result, String successMsg, String errorMsg) {
        result.observe(
                getViewLifecycleOwner(),
                success -> {
                    if (Boolean.TRUE.equals(success)) {
                        showToast(successMsg);
                        NavHostFragment.findNavController(this).navigateUp();
                    } else {
                        showToast(errorMsg);
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Handles the click event for the brand selection button. Retrieve the product's brand Id and
     * navigate to the brand selection screen.
     *
     * @param view The view that was clicked.
     */
    public void onBrandSelectionClick(View view) {
        Product product = viewModel.getProduct().getValue();
        String brandId = product.getBrand() == null ? null : product.getBrand().getId();

        // Navigate to the brand selection screen, passing the selected brand id.
        NavDirections action = ProductEditFragmentDirections.actionToBrandSelection(brandId);
        Navigation.findNavController(view).navigate(action);
    }

    /**
     * Handles the click event for the category selection button. Retrieve the product's category Id
     * and navigate to the category selection screen.
     *
     * @param view The view that was clicked.
     */
    public void onCategorySelectionClick(View view) {
        Product product = viewModel.getProduct().getValue();
        String categoryId = product.getCategory() == null ? null : product.getCategory().getId();

        // Navigate to the category selection screen, passing the selected category id.
        NavDirections action = ProductEditFragmentDirections.actionToCategorySelection(categoryId);
        Navigation.findNavController(view).navigate(action);
    }

    /**
     * Handles the click event for the update button. Validates the product and triggers the update
     *
     * @param updateFunction The function to update the product fields.
     */
    private void updateProductProperty(Consumer<Product> updateFunction) {
        Product product = viewModel.getProduct().getValue();
        if (product != null) {
            updateFunction.accept(product);
        } else {
            Timber.e("Product is null, cannot update fields.");
        }
    }

    /** Observes changes in brand selection and updates the product accordingly. */
    private void observeBrandSelectionChanged() {
        sharedViewModel
                .getSelectedBrandPosition()
                .observe(
                        getViewLifecycleOwner(),
                        position -> {
                            Brand brand = brandRepository.getBrandByPosition(position);
                            updateProductProperty(product -> product.setBrand(brand));
                        });
    }

    /** Observes changes in category selection and updates the product accordingly. */
    private void observeCategorySelectionChanged() {
        sharedViewModel
                .getSelectedCategoryPosition()
                .observe(
                        getViewLifecycleOwner(),
                        position -> {
                            Category category = categoryRepository.getCategoryByPosition(position);
                            updateProductProperty(product -> product.setCategory(category));
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
        binding.rbInStock.setTextColor(inStock ? primaryColor : titleColor);
        binding.rbOutStock.setTextColor(inStock ? titleColor : warningColor);
    }

    /**
     * Observes changes in the product's properties and validates them.
     *
     * <p>Sets up a callback to validate the product's properties when they change. This includes
     * name, selling price, purchase price, discount price, description, note, brand, and category.
     *
     * @param product The product whose properties have changed.
     */
    private void onProductPropertyChanged(Product product) {
        if (changedCallback == null) {
            // Set up a callback to observe changes in the product's properties. This callback will
            // be triggered when any of the product's properties change. It will validate the
            // corresponding property in the ViewModel.
            changedCallback =
                    new Observable.OnPropertyChangedCallback() {
                        @Override
                        public void onPropertyChanged(Observable sender, int propertyId) {
                            handlePropertyChange((Product) sender, propertyId);
                        }
                    };
            product.addOnPropertyChangedCallback(changedCallback);
        }

        // Initially validate the product properties.
        viewModel.validateName(product.getName());
        viewModel.validateSellingPrice(product.getSellingPrice());
        viewModel.validatePurchasePrice(product.getPurchasePrice());
        viewModel.validateDiscountPrice(product.getDiscountPrice());
        viewModel.validateDescription(product.getDescription());
        viewModel.validateNote(product.getNote());
        viewModel.validateBrand(product.getBrand());
        viewModel.validateCategory(product.getCategory());
    }

    private void handlePropertyChange(Product product, int propertyId) {
        switch (propertyId) {
            case BR.name -> {
                viewModel.validateName(product.getName());
            }
            case BR.sellingPrice -> {
                viewModel.validateSellingPrice(product.getSellingPrice());
            }
            case BR.purchasePrice -> {
                viewModel.validatePurchasePrice(product.getPurchasePrice());
            }
            case BR.discountPrice -> {
                viewModel.validateDiscountPrice(product.getDiscountPrice());
            }
            case BR.description -> {
                viewModel.validateDescription(product.getDescription());
            }
            case BR.note -> {
                viewModel.validateNote(product.getNote());
            }
            case BR.status -> {
                updateRadioButtonColors(product.getStatus());
            }
        }
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
