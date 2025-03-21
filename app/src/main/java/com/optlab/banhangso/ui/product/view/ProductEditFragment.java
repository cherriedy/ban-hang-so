package com.optlab.banhangso.ui.product.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

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
import com.optlab.banhangso.databinding.FragmentProductEditBinding;
import com.optlab.banhangso.factory.ProductEditViewModelFactory;
import com.optlab.banhangso.data.model.Brand;
import com.optlab.banhangso.data.model.Category;
import com.optlab.banhangso.data.model.Product;
import com.optlab.banhangso.data.repository.BrandRepository;
import com.optlab.banhangso.data.repository.CategoryRepository;
import com.optlab.banhangso.data.repository.ProductRepository;
import com.optlab.banhangso.ui.brand.view.BrandSelectionFragment;
import com.optlab.banhangso.ui.category.viewmodel.CategorySelectionFragment;
import com.optlab.banhangso.ui.brand.viewmodel.BrandSelectionViewModel;
import com.optlab.banhangso.ui.category.viewmodel.CategorySelectionViewModel;
import com.optlab.banhangso.ui.product.viewmodel.ProductEditViewModel;

import java.util.Optional;

/**
 * Fragment for editing a product.
 *
 * <p>This Fragment handles displaying and updating product details. It initializes the ViewModels,
 * binds data using View Binding, observes LiveData for product and brand updates, and configures the UI accordingly.</p>
 */
@SuppressWarnings({"SimplifiableIfStatement", "FieldCanBeLocal"})
public class ProductEditFragment extends Fragment {

    // Repository singletons used to access product, brand and category data.
    private final ProductRepository repository = ProductRepository.getInstance();
    private final BrandRepository brandRepository = BrandRepository.getInstance();
    private final CategoryRepository categoryRepository = CategoryRepository.getInstance();

    // View Binding instance for the fragment layout.
    private FragmentProductEditBinding binding;
    // Holds arguments passed to this fragment (e.g., productId, isCreate flag).
    private ProductEditFragmentArgs fragmentArgs;
    // Callback for observing property changes in the product.
    private Observable.OnPropertyChangedCallback productPropertyChangedCallback;

    // Find the current NavController and NavBackStackEntry associated with fragment.
    private NavController navController;
    private NavBackStackEntry navBackStackEntry;

    // ViewModel instances for product editing, brand and category selection.
    private ProductEditViewModel viewModel;
    private BrandSelectionViewModel brandSelectionViewModel;
    private CategorySelectionViewModel categorySelectionViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewModels with custom factories to inject required dependencies.
        initProductEditViewModel();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout using View Binding.
        binding = FragmentProductEditBinding.inflate(inflater, container, false);

        // Attempt to restore focus to the previously focused view (if any)
        // This improves user experience during configuration changes.
        Integer focusedViewId = viewModel.getKeyFocusedView();
        if (focusedViewId != null) {
            EditText focusedEditText = binding.getRoot().findViewById(focusedViewId);
            if (focusedEditText != null) {
                focusedEditText.requestFocus();
                showSoftKeyboard(focusedEditText);
            }
        }

        // Create a common focus change listener for all EditText fields.
        // When a view gains focus, save its ID in the ViewModel.
        View.OnFocusChangeListener focusChangeListener = (v, hasFocus) -> {
            if (hasFocus) {
                viewModel.setKeyFocusedView(v.getId());
            }
        };

        // Attach the focus change listener to all relevant EditText fields.
        binding.etName.setOnFocusChangeListener(focusChangeListener);
        binding.etDiscountPrice.setOnFocusChangeListener(focusChangeListener);
        binding.etPurchasePrice.setOnFocusChangeListener(focusChangeListener);
        binding.etSellingPrice.setOnFocusChangeListener(focusChangeListener);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bind the ViewModel to the layout so that UI components can observe LiveData.
        bindViewModel();

        // Get the NavController and NavBackStackEntry associated with the fragment.
        getNavControllerAndBackStack();

        // Configure click listeners for brand and category selection.
        setupBrandSelectionClickListener();
        setupCategorySelectionClickListener();

        // Set up observers to handle changes in product properties and brand selection.
        observeProductPropertyChanges();
        observeBrandSelectionChanges();
        observeCategorySelectionChanges();

        // Configure the delete button's visibility based on whether we're editing an existing product.
        configureDeleteButtonVisibility();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Avoid memory leaks by releasing the binding.
        binding = null;
    }

    /**
     * Displays the soft keyboard for a given view.
     *
     * @param view The view that should gain focus and trigger the keyboard.
     */
    private void showSoftKeyboard(View view) {
        // Ensure the view is focusable, then request focus and show the keyboard.
        if (view.requestFocus()) {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(binding.getRoot(), InputMethodManager.SHOW_IMPLICIT);
        }
    }

    /**
     * Initializes the ProductEditViewModel using a custom factory.
     *
     * <p>This ViewModel manages the product data and related business logic for the editing screen.</p>
     */
    private void initProductEditViewModel() {
        ProductEditViewModelFactory factory = new ProductEditViewModelFactory(this, repository);
        viewModel = new ViewModelProvider(this, factory).get(ProductEditViewModel.class);
    }

    /**
     * Binds the ViewModel to the layout and sets the lifecycle owner.
     *
     * <p>Data binding ensures that the UI components automatically update when LiveData changes.</p>
     */
    private void bindViewModel() {
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setViewModel(viewModel);
    }

    private void getNavControllerAndBackStack() {
        navController = NavHostFragment.findNavController(this);
        navBackStackEntry = navController.getCurrentBackStackEntry();
    }

    /**
     * Observes changes to product properties and validates corresponding fields.
     *
     * <p>This observer also manages a property change callback to update validation in real-time.</p>
     */
    private void observeProductPropertyChanges() {
        viewModel.getProduct().observe(getViewLifecycleOwner(), product -> {
            // Remove any previously attached property callback to avoid duplicate updates.
            if (productPropertyChangedCallback != null) {
                product.removeOnPropertyChangedCallback(productPropertyChangedCallback);
            }

            // Create and attach a new property callback to handle changes.
            productPropertyChangedCallback = new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                    // Ensure binding is still available before updating UI.
                    if (binding != null) {
                        Product observedProduct = (Product) sender;
                        handleProductPropertyChange(observedProduct, propertyId);
                    }
                }
            };

            product.addOnPropertyChangedCallback(productPropertyChangedCallback);

            // Trigger initial validation for all product fields.
            viewModel.validateName(product.getName());
            viewModel.validateDiscountPrice(product.getDiscountPrice());
            viewModel.validatePurchasePrice(product.getPurchasePrice());
            viewModel.validateSellingPrice(product.getSellingPrice());
            viewModel.validateDescription(product.getDescription());
            viewModel.validateNote(product.getNote());
        });
    }

    /**
     * Configures the visibility of the delete button.
     *
     * <p>If the fragment is in edit mode (not creating a new product), display the delete option.</p>
     */
    private void configureDeleteButtonVisibility() {
        fragmentArgs = ProductEditFragmentArgs.fromBundle(requireArguments());
        // If not creating a new product, load product data and show delete button.
        if (!fragmentArgs.getIsCreate()) {
            viewModel.loadProductById(fragmentArgs.getProductId());
            binding.viewDivider.setVisibility(View.VISIBLE);
            binding.btnDelete.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Sets up the click listener for the brand selection field.
     *
     * <p>When clicked, it navigates to the brand selection screen, passing the currently selected brand id.</p>
     */
    private void setupBrandSelectionClickListener() {
        binding.actvBrand.setOnClickListener(v -> {
            // Retrieve the current product without creating a new observer.
            Product product = viewModel.getProduct().getValue();

            // Determine the selection id; use -1 if no brand is selected.
            int selectionId = (product == null || product.getBrand() == null)
                    ? RecyclerView.NO_POSITION : product.getBrand().getId();

            // Create navigation directions and trigger the navigation action.
            NavDirections action = ProductEditFragmentDirections.actionToBrandSelection(selectionId);
            Navigation.findNavController(v).navigate(action);
        });

        // Disable clickable behavior on the end icon to prevent accidental actions.
        binding.tilBrand.setEndIconCheckable(false);
    }

    /**
     * Sets up the click listener for the category selection field.
     *
     * <p>This is a placeholder for future category selection implementation.</p>
     */
    private void setupCategorySelectionClickListener() {
        binding.actvCategory.setOnClickListener(v -> {
            Product product = viewModel.getProduct().getValue();

            int selectionId = (product == null || product.getCategory() == null)
                    ? RecyclerView.NO_POSITION : product.getCategory().getId();
            NavDirections action = ProductEditFragmentDirections.actionToCategorySelection(selectionId);
            Navigation.findNavController(v).navigate(action);
        });

        // Disable clickable behavior on the end icon to prevent accidental actions.
        binding.tilCategory.setEndIconCheckable(false);
    }

    /**
     * Observes changes in brand selection and updates the product accordingly.
     */
    private void observeBrandSelectionChanges() {
        navBackStackEntry.getSavedStateHandle()
                .getLiveData(BrandSelectionFragment.KEY_CHECKED_POSITION)
                .observe(getViewLifecycleOwner(), checkedPosition -> {
                    // Transform the checkPosition to primitive value, if it is null set
                    // RecyclerView.NO_POSITION (-1) as default.
                    int pos = Optional.ofNullable(checkedPosition)
                            .map(cp -> (int) cp)
                            .orElse(RecyclerView.NO_POSITION);

                    // If the checked position is invalid, doing nothing.
                    if (pos == RecyclerView.NO_POSITION) return;

                    // Get the current instance of product.
                    Product product = viewModel.getProduct().getValue();
                    if (product == null) return;

                    Brand checkedBrand = brandRepository.getBrandByPosition(pos);
                    if (checkedBrand != null && !checkedBrand.equals(product.getBrand())) {
                        product.setBrand(checkedBrand);
                        viewModel.setProduct(product);
                    }
                });
    }

    /**
     * Observes changes in category selection and updates the product accordingly.
     */
    private void observeCategorySelectionChanges() {
        navBackStackEntry.getSavedStateHandle()
                .getLiveData(CategorySelectionFragment.KEY_CHECKED_POSITION)
                .observe(getViewLifecycleOwner(), checkedPosition -> {
                    int pos = Optional.ofNullable(checkedPosition)
                            .map(cp -> (int) cp)
                            .orElse(RecyclerView.NO_POSITION);

                    if (pos == RecyclerView.NO_POSITION) return;

                    Product product = viewModel.getProduct().getValue();
                    if (product == null) return;

                    Category checkedCategory = categoryRepository.getCategoryByPosition(pos);
                    if (checkedCategory != null && !checkedCategory.equals(product.getBrand())) {
                        product.setCategory(checkedCategory);
                        viewModel.setProduct(product);
                    }
                });
    }

    /**
     * Handles individual product property changes.
     *
     * <p>This method validates the changed property based on its identifier.
     * For example, if the name property changes, it triggers name validation in the ViewModel.</p>
     *
     * @param product    The product whose property has changed.
     * @param propertyId The identifier of the changed property.
     */
    private void handleProductPropertyChange(Product product, int propertyId) {
        switch (propertyId) {
            case BR.name:
                viewModel.validateName(product.getName());
                break;
            case BR.sellingPrice:
                viewModel.validateSellingPrice(product.getSellingPrice());
                break;
            case BR.purchasePrice:
                viewModel.validatePurchasePrice(product.getPurchasePrice());
                break;
            case BR.discountPrice:
                viewModel.validateDiscountPrice(product.getDiscountPrice());
                break;
            case BR.status:
                // Update UI elements based on stock status.
                updateRadioButtonColors(product.getStatus() == Product.ProductStatus.IN_STOCK);
                break;
            case BR.description:
                viewModel.validateDescription(product.getDescription());
                break;
            case BR.note:
                viewModel.validateNote(product.getNote());
                break;
        }
    }

    /**
     * Updates the text colors of the radio buttons based on the product's stock status.
     *
     * <p>If in stock, the "In Stock" radio button gets a primary color while the "Out of Stock"
     * button uses a secondary title color. Otherwise, the colors are swapped to indicate a warning.</p>
     *
     * @param inStock True if the product is in stock; false otherwise.
     */
    private void updateRadioButtonColors(boolean inStock) {
        if (inStock) {
            binding.rbInStock.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_primary));
            binding.rbOutStock.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_text_title));
        } else {
            binding.rbInStock.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_text_title));
            binding.rbOutStock.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_warning));
        }
    }
}
