package com.optlab.banhangso.features.shared.viewmodels;

import static timber.log.Timber.d;

import android.net.Uri;
import android.util.Pair;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.optlab.banhangso.models.application.UploadableImage;
import com.optlab.banhangso.services.ImageUploader;
import java.util.ArrayList;
import java.util.List;

public interface UploadViewModel {

  List<UploadableImage> getQueue();

  MutableLiveData<List<UploadableImage>> getImages();

  MutableLiveData<Pair<Integer, UploadableImage>> getImage();

  ImageUploader getImageUploader();

  default void removeImage(int position) {
    List<UploadableImage> queue = getQueue();
    MutableLiveData<List<UploadableImage>> images = getImages();
    if (position >= 0 && position < queue.size()) {
      queue.remove(position);
      images.setValue(new ArrayList<>(queue));
    }
  }

  default void onImagesSelected(@NonNull List<Uri> uris) {
    List<UploadableImage> queue = getQueue();
    MutableLiveData<List<UploadableImage>> images = getImages();
    uris.stream().map(UploadableImage::new).forEach(queue::add);
    images.setValue(new ArrayList<>(queue));
    processUploadImages();
  }

  default void processUploadImages() {
    List<UploadableImage> queue = getQueue();
    MutableLiveData<List<UploadableImage>> images = getImages();
    MutableLiveData<Pair<Integer, UploadableImage>> image = getImage();
    ImageUploader imageUploader = getImageUploader();

    List<Uri> pendingUris = new ArrayList<>();
    List<Integer> pendingIndices = new ArrayList<>();

    for (int i = 0; i < queue.size(); i++) {
      UploadableImage item = queue.get(i);
      if (item.getStatus() == UploadableImage.Status.PENDING) {
        pendingIndices.add(i);
        item.setStatus(UploadableImage.Status.UPLOADING);
        pendingUris.add(item.getContentUri());
      }
    }

    if (pendingUris.isEmpty()) {
      d("No pending images to upload.");
      return;
    }

    images.setValue(new ArrayList<>(queue));

    imageUploader.uploadImages(
        pendingUris,
        new ImageUploader.ImageUploadProgressListener() {
          @Override
          public void onProgress(int imageIndex, int percentage) {
            if (imageIndex >= 0 && imageIndex < pendingIndices.size()) {
              int actualIndex = pendingIndices.get(imageIndex);
              UploadableImage item = queue.get(actualIndex);
              item.setProgress(percentage);
              image.postValue(new Pair<>(actualIndex, item));
            }
          }

          @Override
          public void onSuccess(int imageIndex, String imageUrl) {
            if (imageIndex >= 0 && imageIndex < pendingIndices.size()) {
              int actualIndex = pendingIndices.get(imageIndex);
              d("Image %d uploaded successfully: %s", actualIndex, imageUrl);
              UploadableImage item = queue.get(actualIndex);
              item.setStatus(UploadableImage.Status.COMPLETED);
              item.setRemoteUrl(imageUrl);
              image.postValue(new Pair<>(actualIndex, item));
            }
          }

          @Override
          public void onFailed(int imageIndex, String errorMessage) {
            if (imageIndex >= 0 && imageIndex < pendingIndices.size()) {
              int actualIndex = pendingIndices.get(imageIndex);
              d("Image %d upload failed: %s", actualIndex, errorMessage);
              UploadableImage item = queue.get(actualIndex);
              item.setStatus(UploadableImage.Status.FAILED);
              item.setErrorMessage(errorMessage);
              image.postValue(new Pair<>(actualIndex, item));
            }
          }

          @Override
          public void onCompleted(List<String> imageUrls) {}
        });
  }
}
