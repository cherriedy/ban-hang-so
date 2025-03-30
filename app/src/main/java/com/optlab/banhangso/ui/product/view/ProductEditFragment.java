package com.optlab.banhangso.ui.product.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.optlab.banhangso.data.repository.BrandRepository;
import com.optlab.banhangso.data.repository.CategoryRepository;
import com.optlab.banhangso.databinding.FragmentProductEditBinding;
import com.optlab.banhangso.data.model.Brand;
import com.optlab.banhangso.data.model.Category;
import com.optlab.banhangso.data.model.Product;
import com.optlab.banhangso.ui.brand.view.BrandSelectionFragment;
import com.optlab.banhangso.ui.category.viewmodel.CategorySelectionFragment;
import com.optlab.banhangso.ui.product.viewmodel.ProductEditViewModel;

import java.util.Optional;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Fragment for editing a product.
 *
 * <p>This Fragment handles displaying and updating product details. It initializes the ViewModels,
 * binds data using View Binding, observes LiveData for product and brand updates, and configures
 * the UI accordingly.
 */
@AndroidEntryPoint
@SuppressWarnings({"SimplifiableIfStatement", "FieldCanBeLocal"})
public class ProductEditFragment extends Fragment {
  private FragmentProductEditBinding binding;
  private ProductEditFragmentArgs fragmentArgs;
  private NavController navController;
  private NavBackStackEntry navBackStackEntry;
  private ProductEditViewModel productEditViewModel;
  private Observable.OnPropertyChangedCallback productPropertyChangedCallback;

  @Inject protected BrandRepository brandRepository;
  @Inject protected CategoryRepository categoryRepository;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initViewModels();
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentProductEditBinding.inflate(inflater, container, false);
    binding.setLifecycleOwner(getViewLifecycleOwner());
    binding.setViewModel(productEditViewModel);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    getNavControllerAndBackStack();

    setupBrandSelection();
    setupCategorySelection();

    // Set up observers to handle changes in product properties and brand selection.
    observeProductPropertyChanges();
    observeBrandSelectionChanges();
    observeCategorySelectionChanges();

    // Configure the delete button's visibility based on whether we're editing an existing
    // product.
    configureDeleteButtonVisibility();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();

    // Avoid memory leaks by releasing the binding.
    binding = null;
  }

  private void initViewModels() {
    productEditViewModel = new ViewModelProvider(this).get(ProductEditViewModel.class);
  }

  private void getNavControllerAndBackStack() {
    navController = NavHostFragment.findNavController(this);
    navBackStackEntry = navController.getCurrentBackStackEntry();
  }

  /**
   * Observes changes to product properties and validates corresponding fields.
   *
   * <p>This observer also manages a property change callback to update validation in real-time.
   */
  private void observeProductPropertyChanges() {
    productEditViewModel
        .getProduct()
        .observe(
            getViewLifecycleOwner(),
            product -> {
              // Remove any previously attached property callback to avoid duplicate
              // updates.
              if (productPropertyChangedCallback != null) {
                product.removeOnPropertyChangedCallback(productPropertyChangedCallback);
              }

              // Create and attach a new property callback to handle changes.
              productPropertyChangedCallback =
                  new Observable.OnPropertyChangedCallback() {
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
              productEditViewModel.validateName(product.getName());
              productEditViewModel.validateDiscountPrice(product.getDiscountPrice());
              productEditViewModel.validatePurchasePrice(product.getPurchasePrice());
              productEditViewModel.validateSellingPrice(product.getSellingPrice());
              productEditViewModel.validateDescription(product.getDescription());
              productEditViewModel.validateNote(product.getNote());
            });
  }

  /**
   * Configures the visibility of the delete button.
   *
   * <p>If the fragment is in edit mode (not creating a new product), display the delete option.
   */
  private void configureDeleteButtonVisibility() {
    fragmentArgs = ProductEditFragmentArgs.fromBundle(requireArguments());
    // If not creating a new product, load product data and show delete button.
    if (!fragmentArgs.getIsCreate()) {
      productEditViewModel.loadProductById(fragmentArgs.getProductId());
      binding.viewDivider.setVisibility(View.VISIBLE);
      binding.btnDelete.setVisibility(View.VISIBLE);
    }
  }

  /**
   * Sets up the click listener for the brand selection field.
   *
   * <p>This method retrieves the current product and its brand, then navigates to the brand details
   * fragment. It also disables the clickable behavior on the end icon to prevent user accidentally
   * clicking it.
   */
  private void setupBrandSelection() {
    binding.actvBrand.setOnClickListener(
        v -> {
          // Retrieve the current product without creating a new observer, this is done to
          // avoid unnecessary UI updates.
          Product product = productEditViewModel.getProduct().getValue();

          // Get the brand id from the product, if it is null, set it to null.
          String brandId =
              (product == null || product.getBrand() == null) ? null : product.getBrand().getId();

          // Navigate to the brand selection screen, passing the selected brand id.
          NavDirections action = ProductEditFragmentDirections.actionToBrandSelection(brandId);
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
          Product product = productEditViewModel.getProduct().getValue();

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
              Product product = productEditViewModel.getProduct().getValue();
              if (product == null) return;

              Brand checkedBrand = brandRepository.getBrandByPosition(pos);
              if (checkedBrand != null && !checkedBrand.equals(product.getBrand())) {
                product.setBrand(checkedBrand);
                productEditViewModel.setProduct(product);
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

              Product product = productEditViewModel.getProduct().getValue();
              if (product == null) return;

              Category checkedCategory = categoryRepository.getCategoryByPosition(pos);
              if (checkedCategory != null && !checkedCategory.equals(product.getBrand())) {
                product.setCategory(checkedCategory);
                productEditViewModel.setProduct(product);
              }
            });
  }

  /**
   * Handles individual product property changes.
   *
   * <p>This method validates the changed property based on its identifier. For example, if the name
   * property changes, it triggers name validation in the ViewModel.
   *
   * @param product The product whose property has changed.
   * @param propertyId The identifier of the changed property.
   */
  private void handleProductPropertyChange(Product product, int propertyId) {
    switch (propertyId) {
      case BR.name:
        productEditViewModel.validateName(product.getName());
        break;
      case BR.sellingPrice:
        productEditViewModel.validateSellingPrice(product.getSellingPrice());
        break;
      case BR.purchasePrice:
        productEditViewModel.validatePurchasePrice(product.getPurchasePrice());
        break;
      case BR.discountPrice:
        productEditViewModel.validateDiscountPrice(product.getDiscountPrice());
        break;
      case BR.status:
        // Update UI elements based on stock status.
        updateRadioButtonColors(product.getStatus() == Product.ProductStatus.IN_STOCK);
        break;
      case BR.description:
        productEditViewModel.validateDescription(product.getDescription());
        break;
      case BR.note:
        productEditViewModel.validateNote(product.getNote());
        break;
    }
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
}
