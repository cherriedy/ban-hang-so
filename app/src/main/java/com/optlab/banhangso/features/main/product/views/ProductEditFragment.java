package com.optlab.banhangso.features.main.product.views;

import static com.optlab.banhangso.internal.Config.MAX_IMAGE_UPLOADS;
import static com.optlab.banhangso.internal.utilities.itemspacing.LinearSpacingStrategy.Direction.LEFT;
import static com.optlab.banhangso.internal.utilities.itemspacing.LinearSpacingStrategy.Direction.RIGHT;

import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.optlab.banhangso.R;
import com.optlab.banhangso.databinding.FragmentProductEditBinding;
import com.optlab.banhangso.features.main.brand.models.BrandUiModel;
import com.optlab.banhangso.features.main.brand.view.BrandSelectionFragment;
import com.optlab.banhangso.features.main.category.models.CategoryUiModel;
import com.optlab.banhangso.features.main.category.views.CategorySelectionFragment;
import com.optlab.banhangso.features.main.product.models.ProductUiModel;
import com.optlab.banhangso.features.main.product.viewmodels.ProductEditViewModel;
import com.optlab.banhangso.features.shared.adapters.FileUploadAdapter;
import com.optlab.banhangso.features.shared.views.DeleteConfirmationDialog;
import com.optlab.banhangso.features.shared.views.ExitConfirmationDialog;
import com.optlab.banhangso.features.shared.views.LoadingDialog;
import com.optlab.banhangso.internal.utilities.itemspacing.LinearSpacingStrategy;
import com.optlab.banhangso.internal.utilities.itemspacing.SpacingItemDecoration;
import com.optlab.banhangso.models.application.UploadableImage;
import dagger.hilt.android.AndroidEntryPoint;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import timber.log.Timber;

@AndroidEntryPoint
public class ProductEditFragment extends Fragment {

  public static final String PRODUCT_EDIT_REQUEST = "PRODUCT_EDIT_REQUEST";
  public static final String REFRESH_FLAG = "REFRESH_FLAG";

  private final LoadingDialog loadingDialog = new LoadingDialog();
  private FragmentProductEditBinding binding;
  private ProductEditViewModel viewModel;
  private ProductEditFragmentArgs args;
  private NavController navController;
  private FileUploadAdapter fileUploadAdapter;
  private ActivityResultLauncher<PickVisualMediaRequest> pickMultipleImagesLauncher;
  private ActivityResultLauncher<ScanOptions> barcodeScannerLauncher;
  private ActivityResultLauncher<Uri> takePictureLauncher;
  private ActivityResultLauncher<String> requestCameraPermissionLauncher;
  private Uri capturedPictureUri;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    pickMultipleImagesLauncher = registerPickMultipleImagesLauncher();
    takePictureLauncher = registerTakePictureLauncher();
    barcodeScannerLauncher = registerScanContractLauncher();
    requestCameraPermissionLauncher = registerCameraPermissionLauncher();

    viewModel = new ViewModelProvider(this).get(ProductEditViewModel.class);
    args = ProductEditFragmentArgs.fromBundle(requireArguments());
    configureInteractionMode();

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
    navController = NavHostFragment.findNavController(this);
    binding.mtb.setNavigationOnClickListener(v -> navController.navigateUp());

    setupImageRecyclerView();

    registerExitConfirmationListener();
    registerDeleteConfirmationListener();
    registerBrandSelectionListener();
    registerCategorySelectionListener();
    observeViewModel();
  }

  private void setupImageRecyclerView() {
    fileUploadAdapter =
        new FileUploadAdapter(
            new FileUploadAdapter.ImageActionListener() {
              @Override
              public void onRemove(int position) {
                viewModel.removeImage(position);
              }

              @Override
              public void onRetry(int position) {
                viewModel.retryUpload(position);
              }
            });

    binding.rvImages.setLayoutManager(
        new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

    LinearSpacingStrategy linearSpacingStrategy =
        new LinearSpacingStrategy(requireContext(), 8, EnumSet.of(LEFT, RIGHT));
    binding.rvImages.addItemDecoration(new SpacingItemDecoration(linearSpacingStrategy));

    binding.rvImages.setAdapter(fileUploadAdapter);
  }

  /** Configures the interaction mode of the fragment based on the arguments passed to it. */
  private void configureInteractionMode() {
    // Set the interaction mode based on whether it's create or edit
    viewModel.setIsEditing(args.getIsEditing());
    // Load the product based on the passed product ID.
    viewModel.loadProductById(args.getProductId());
  }

  private void registerExitConfirmationListener() {
    getParentFragmentManager()
        .setFragmentResultListener(
            ExitConfirmationDialog.REQUEST,
            getViewLifecycleOwner(),
            (requestKey, result) -> {
              if (result.getBoolean(ExitConfirmationDialog.CONFIRMED)) {
                NavHostFragment.findNavController(this).navigateUp();
              }
            });
  }

  private void registerDeleteConfirmationListener() {
    getParentFragmentManager()
        .setFragmentResultListener(
            DeleteConfirmationDialog.REQUEST,
            getViewLifecycleOwner(),
            (requestKey, result) -> {
              if (result.getBoolean(DeleteConfirmationDialog.DELETED)) {
                viewModel.onDelete();
              }
            });
  }

  private void registerCategorySelectionListener() {
    getParentFragmentManager()
        .setFragmentResultListener(
            CategorySelectionFragment.CATEGORY_SELECTION_REQUEST,
            getViewLifecycleOwner(),
            (requestKey, result) -> {
              CategoryUiModel selectedCategory =
                  (CategoryUiModel)
                      result.getSerializable(CategorySelectionFragment.CATEGORY_SELECTION_RESULT);

              if (selectedCategory != null) {
                Timber.d("Selected category: %s", selectedCategory);
                viewModel.updateCategory(selectedCategory);
              } else {
                Timber.w("No brand selected or brand is null.");
              }
            });
  }

  private void registerBrandSelectionListener() {
    getParentFragmentManager()
        .setFragmentResultListener(
            BrandSelectionFragment.BRAND_SELECTION_REQUEST,
            getViewLifecycleOwner(),
            (requestKey, result) -> {
              BrandUiModel selectedBrand =
                  (BrandUiModel)
                      result.getSerializable(BrandSelectionFragment.BRAND_SELECTION_RESULT);

              if (selectedBrand != null) {
                Timber.d("Selected brand: %s", selectedBrand);
                viewModel.updateBrand(selectedBrand);
              } else {
                Timber.w("No brand selected or brand is null.");
              }
            });
  }

  /** Shows a confirmation dialog when the user attempts to exit the fragment. */
  private void showExitConfirmationDialog() {
    new ExitConfirmationDialog()
        .show(
            getParentFragmentManager(),
            "exitConfirmationDialog_" + this.getClass().getSimpleName());
  }

  private void observeViewModel() {
    viewModel.isLoading().observe(getViewLifecycleOwner(), this::handleLoadingState);
    viewModel.getMessageResId().observe(getViewLifecycleOwner(), this::showToast);

    viewModel
        .getOperationCompleted()
        .observe(getViewLifecycleOwner(), this::handleOperationCompleted);

    viewModel.getUploadableImage().observe(getViewLifecycleOwner(), fileUploadAdapter::setData);
    viewModel.getImagePair().observe(getViewLifecycleOwner(), this::updateItemProgress);
  }

  private void updateItemProgress(@NonNull Pair<Integer, UploadableImage> pair) {
    int position = pair.first;
    UploadableImage uploadableImage = pair.second;
    fileUploadAdapter.updateProgressAt(
        position, uploadableImage.getProgress(), uploadableImage.getStatus());
  }

  private void handleOperationCompleted(@NonNull Boolean completed) {
    if (completed) {
      Bundle result = new Bundle();
      result.putBoolean(REFRESH_FLAG, true);
      getParentFragmentManager().setFragmentResult(PRODUCT_EDIT_REQUEST, result);
    }
  }

  private void showToast(@NonNull Integer messageResId) {
    navController.navigateUp();
    Toast.makeText(requireContext(), getString(messageResId), Toast.LENGTH_SHORT).show();
  }

  /**
   * Handles the click event for the brand selection button. Retrieve the product's brand Id and
   * navigate to the brand selection screen.
   *
   * @param view The view that was clicked.
   */
  public void onBrandSelectionClick(View view) {
    ProductUiModel uiModel = viewModel.getUiModel().getValue();
    BrandUiModel brandUiModel = Objects.requireNonNull(uiModel).getBrand();
    String brandId =
        brandUiModel != null && brandUiModel.getId() != null ? brandUiModel.getId() : "";
    if (brandId.isBlank()) {
      Timber.w("Category ID is blank, there will be no category selected.");
    }
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
    ProductUiModel uiModel = viewModel.getProductUiModel().getValue();
    CategoryUiModel categoryUiModel = Objects.requireNonNull(uiModel).getCategory();
    String categoryId =
        categoryUiModel != null && categoryUiModel.getId() != null ? categoryUiModel.getId() : "";
    if (categoryId.isBlank()) {
      Timber.w("Category ID is blank, there will be no category selected.");
    }
    NavDirections action = ProductEditFragmentDirections.actionToCategorySelection(categoryId);
    Navigation.findNavController(view).navigate(action);
  }

  private void handleLoadingState(@NonNull Boolean isLoading) {
    if (isLoading) {
      loadingDialog.show(getParentFragmentManager(), this.getClass().getSimpleName());
    } else {
      loadingDialog.dismissAllowingStateLoss();
    }
  }

  /**
   * @noinspection unused
   */
  public void onDeleteClick(@NonNull View view) {
    DeleteConfirmationDialog deleteConfirmationDialog =
        DeleteConfirmationDialog.newInstance(
            getString(R.string.alert_delete_product), getString(R.string.alert_confirm_delete));

    deleteConfirmationDialog.show(getParentFragmentManager(), this.getClass().getSimpleName());
  }

  /**
   * @noinspection unused
   */
  public void launchMultipleImagePicker(@NonNull View view) {
    pickMultipleImagesLauncher.launch(
        new PickVisualMediaRequest.Builder()
            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
            .build());
  }

  @NonNull private ActivityResultLauncher<Uri> registerTakePictureLauncher() {
    return registerForActivityResult(
        new ActivityResultContracts.TakePicture(),
        success -> {
          if (Boolean.TRUE.equals(success)) {
            viewModel.onImagesProvided(List.of(capturedPictureUri));
          } else {
            Timber.w("User cancelled the image capture or an error occurred.");
          }
        });
  }

  @NonNull private ActivityResultLauncher<PickVisualMediaRequest> registerPickMultipleImagesLauncher() {
    return registerForActivityResult(
        new ActivityResultContracts.PickMultipleVisualMedia(MAX_IMAGE_UPLOADS),
        uris -> {
          if (uris != null && !uris.isEmpty()) {
            viewModel.onImagesProvided(uris);
          }
        });
  }

  @NonNull private ActivityResultLauncher<ScanOptions> registerScanContractLauncher() {
    return registerForActivityResult(
        new ScanContract(),
        result -> {
          if (result.getContents() != null) {
            handleQrScanResult(result.getContents());
          } else {
            Timber.d("QR scan was cancelled");
          }
        });
  }

  @NonNull private ActivityResultLauncher<String> registerCameraPermissionLauncher() {
    return registerForActivityResult(
        new ActivityResultContracts.RequestPermission(),
        isGranted -> {
          if (Boolean.TRUE.equals(isGranted)) {
            launchCameraForImageCapture();
          } else {
            Toast.makeText(
                    requireContext(), getString(R.string.camera_requirement), Toast.LENGTH_SHORT)
                .show();
          }
        });
  }

  /**
   * Handles the click event for the barcode scanner icon. This method will be called when the user
   * taps the scanner icon in the text input fields (barcode or description).
   *
   * @param view The view that was clicked.
   * @noinspection unused
   */
  public void launchBarcodeScanner(@NonNull View view) {
    ScanOptions options = new ScanOptions();
    options.setOrientationLocked(true);
    options.setBeepEnabled(true);
    options.setCaptureActivity(CaptureActivity.class);
    options.setDesiredBarcodeFormats(ScanOptions.ONE_D_CODE_TYPES);
    options.setPrompt(getString(R.string.scan_barcode));

    // Launch the barcode scanner
    barcodeScannerLauncher.launch(options);
  }

  /**
   * Handles the result of the barcode scan.
   *
   * @param scannedData The data scanned from the barcode.
   */
  private void handleQrScanResult(@NonNull String scannedData) {
    String trimmedScannedData = scannedData.trim();
    if (trimmedScannedData.isEmpty()) {
      Timber.w("Scanned data is empty, ignoring.");
      return;
    }
    Timber.d("Scanned data: %s", trimmedScannedData);
    viewModel.updateBarcode(trimmedScannedData);
  }

  /**
   * @noinspection unused
   */
  public void launchTakePicture(@NonNull View view) {
    if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
        != PackageManager.PERMISSION_GRANTED) {
      requestCameraPermissionLauncher.launch(android.Manifest.permission.CAMERA);
      return;
    }

    launchCameraForImageCapture();
  }

  private void launchCameraForImageCapture() {
    ContentValues contentValues = new ContentValues();

    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");

    // Add name with 'image_' prefix and random UUID to ensure uniqueness
    String displayName = "image_" + System.currentTimeMillis() + ".jpg";
    contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName);

    // Create a placeholder URI for the image taken by the camera.
    capturedPictureUri =
        requireContext()
            .getContentResolver()
            .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
    if (capturedPictureUri != null) {
      takePictureLauncher.launch(capturedPictureUri);
    } else {
      Timber.e("Failed to create a placeholder URI for the image.");
    }
  }
}
