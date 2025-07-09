package com.optlab.banhangso.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import androidx.annotation.NonNull;
import com.optlab.banhangso.internal.network.ProgressRequestBody;
import com.optlab.banhangso.models.remote.responses.UploadResponse;
import com.optlab.banhangso.models.remote.responses.base.Response;
import com.optlab.banhangso.services.interfaces.ImageUploadService;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import timber.log.Timber;

public class ImageUploader {

  public static final int NO_POSITION = -1;
  public static final int MAX_QUALITY = 75;

  public interface ImageUploadProgressListener {
    void onProgress(int imageIndex, int percentage);

    void onSuccess(int imageIndex, String imageUrl);

    void onFailed(int imageIndex, String errorMessage);

    void onCompleted(List<String> imageUrls);
  }

  private final Context context;
  private final ImageUploadService imageUploadService;
  private final List<String> uploadedUrls = new ArrayList<>();
  private final CompositeDisposable disposables = new CompositeDisposable();

  public ImageUploader(@NonNull Context context, @NonNull ImageUploadService imageUploadService) {
    this.imageUploadService = imageUploadService;
    this.context = context;
  }

  @Override
  protected void finalize() throws Throwable {
    disposables.clear();
    super.finalize();
  }

  /**
   * @noinspection SimplifyStreamApiCallChains
   */
  public void uploadImages(
      @NonNull List<Uri> imageUris, @NonNull ImageUploadProgressListener listener) {
    if (imageUris.isEmpty()) {
      listener.onCompleted(new ArrayList<>());
      return;
    }

    // Initialize with nulls to maintain order
    uploadedUrls.addAll(
        IntStream.range(0, imageUris.size())
            .<String>mapToObj(i -> null)
            .collect(Collectors.toList()));

    AtomicInteger completedCount = new AtomicInteger(0);
    int totalImages = imageUris.size();

    // Create observables for all uploads
    List<Observable<String>> uploadObservables = new ArrayList<>();

    for (int i = 0; i < imageUris.size(); i++) {
      final int imageIndex = i;
      Uri imageUri = imageUris.get(i);

      Observable<String> uploadObservable =
          uploadImageWithProgress(imageIndex, imageUri, listener)
              .toObservable()
              .doOnNext(
                  imageUrl ->
                      handleOnNextUploadImageSuccess(
                          listener, imageUrl, imageIndex, completedCount, totalImages))
              .doOnError(
                  throwable ->
                      handleOnNextUploadImageError(
                          listener, throwable, imageIndex, completedCount, totalImages))
              .onErrorReturnItem(""); // Return empty string on error to avoid breaking the stream

      uploadObservables.add(uploadObservable);
    }

    Disposable disposable =
        Observable.merge(uploadObservables)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .filter(uri -> uri != null && !uri.isBlank())
            .doOnError(throwable -> handleOnUploadError(listener, throwable))
            .subscribe();

    disposables.add(disposable);
  }

  public void uploadImage(@NonNull Uri imageUri, @NonNull ImageUploadProgressListener listener) {
    uploadImage(NO_POSITION, imageUri, listener);
  }

  public void uploadImage(
      int imageIndex, @NonNull Uri imageUri, @NonNull ImageUploadProgressListener listener) {
    Disposable disposable =
        uploadImageWithProgress(imageIndex, imageUri, listener)
            .doOnSuccess(imageUrl -> listener.onSuccess(imageIndex, imageUrl))
            .doOnError(throwable -> handleOnUploadError(listener, throwable))
            .onErrorReturnItem("") // Return empty string on error to avoid breaking the stream
            .subscribe();
    disposables.add(disposable);
  }

  private void handleOnUploadError(
      @NonNull ImageUploadProgressListener listener, @NonNull Throwable throwable) {
    listener.onCompleted(new ArrayList<>());
    Timber.e(throwable, "There was an error uploading images: %s", throwable.getMessage());
  }

  private void handleOnNextUploadImageError(
      @NonNull ImageUploadProgressListener listener,
      @NonNull Throwable throwable,
      int imageIndex,
      @NonNull AtomicInteger completedCount,
      int totalImages) {

    Timber.e(
        throwable,
        "There was an error uploading image at index %d: %s",
        imageIndex,
        throwable.getMessage());

    listener.onFailed(imageIndex, throwable.getMessage());

    checkIfCompleted(completedCount, listener, totalImages);
  }

  private void handleOnNextUploadImageSuccess(
      @NonNull ImageUploadProgressListener listener,
      @NonNull String imageUrl,
      int imageIndex,
      @NonNull AtomicInteger completedCount,
      int totalImages) {

    synchronized (uploadedUrls) {
      uploadedUrls.set(imageIndex, imageUrl);
    }

    if (imageUrl.isBlank()) {
      Timber.w("Image upload at index %d returned an empty URL", imageIndex);
      listener.onFailed(imageIndex, "Image upload failed with empty URL");
    } else {
      listener.onSuccess(imageIndex, imageUrl);
    }

    checkIfCompleted(completedCount, listener, totalImages);
  }

  private void checkIfCompleted(
      @NonNull AtomicInteger completedCount,
      @NonNull ImageUploadProgressListener imageUploadProgressListener,
      int totalImages) {
    if (completedCount.incrementAndGet() == totalImages) {
      List<String> successfulUrls =
          uploadedUrls.stream()
              .filter(url -> url != null && !url.isBlank())
              .collect(Collectors.toList());

      imageUploadProgressListener.onCompleted(successfulUrls);
    }
  }

  @NonNull private Single<String> uploadImageWithProgress(
      int imageIndex,
      @NonNull Uri imageUri,
      @NonNull ImageUploadProgressListener imageUploadProgressListener) {
    return Single.fromCallable(() -> uriToFile(imageUri))
        .subscribeOn(Schedulers.io())
        .flatMap(
            imageFile ->
                processUploadImage(
                    imageIndex,
                    imageFile,
                    imageUploadProgressListener,
                    imageUploadService::uploadImage));
  }

  /**
   * @noinspection ResultOfMethodCallIgnored
   */
  @NonNull private Single<String> processUploadImage(
      int imageIndex,
      @NonNull File imageFile,
      @NonNull ImageUploadProgressListener imageUploadProgressListener,
      @NonNull Function<MultipartBody.Part, Single<Response<UploadResponse>>> uploadFunction) {

    RequestBody fileRequestBody = RequestBody.create(imageFile, MediaType.parse("image/*"));

    ProgressRequestBody progressRequestBody =
        new ProgressRequestBody(
            fileRequestBody,
            (bytesWritten, totalBytes, percentage) ->
                imageUploadProgressListener.onProgress(imageIndex, percentage));

    MultipartBody.Part imagePart =
        MultipartBody.Part.createFormData("file", imageFile.getName(), progressRequestBody);

    return uploadFunction
        .apply(imagePart)
        .doFinally(imageFile::delete)
        .map(response -> response.isSuccess() ? response.data().imageUrl() : "");
  }

  //  @NonNull private File uriToFile(@NonNull Uri uri) throws IOException {
  //    try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
  //      if (inputStream == null) {
  //        throw new IOException("Cannot open input stream for URI: " + uri);
  //      }
  //
  //      // Create a file in the cache directory to attach the image to the request body
  //      File tempFile =
  //          File.createTempFile(
  //              "upload_image_" + UUID.randomUUID().toString(), ".jpeg", context.getCacheDir());
  //
  //      try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
  //        byte[] buffer = new byte[8192]; // 8KB buffer
  //        int bytesRead; // to store the number of bytes read
  //
  //        // Read from the input stream each 8KB and write to the output stream until the end of
  // the
  //        // stream (-1). This is more efficient than reading byte by byte.
  //        while ((bytesRead = inputStream.read(buffer)) != -1) {
  //          outputStream.write(buffer, 0, bytesRead);
  //        }
  //      }
  //
  //      return tempFile;
  //    }
  //  }

  @NonNull private File uriToFile(@NonNull Uri uri) throws IOException {
    try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
      if (inputStream == null) {
        throw new IOException("Cannot open input stream for URI: " + uri);
      }

      // Decode the input stream to a Bitmap, so that we can compress it
      Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

      File tempFile =
          File.createTempFile(
              "upload_image_" + UUID.randomUUID().toString(), "jpeg", context.getCacheDir());

      try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
        // Compress the bitmap to JPEG format with the specified quality
        bitmap.compress(Bitmap.CompressFormat.JPEG, ImageUploader.MAX_QUALITY, outputStream);
      }

      return tempFile;
    }
  }
}
