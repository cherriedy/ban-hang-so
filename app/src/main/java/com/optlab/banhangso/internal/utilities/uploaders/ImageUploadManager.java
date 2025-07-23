package com.optlab.banhangso.internal.utilities.uploaders;

import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.optlab.banhangso.models.application.UploadableImage;
import com.optlab.banhangso.services.ImageUploader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

public class ImageUploadManager {

  private final ImageUploader imageUploader;
  private final List<UploadableImage> imagesQueue = new ArrayList<>();

  @Getter private final MutableLiveData<List<UploadableImage>> images = new MutableLiveData<>();

  public ImageUploadManager(@NonNull ImageUploader imageUploader) {
    this.imageUploader = imageUploader;
  }

  public void addImage(@NonNull Uri uri) {
    imagesQueue.add(new UploadableImage(uri));
    images.setValue(new ArrayList<>(imagesQueue));
  }

  public void uploadPendingOrFailedImages() {
    for (UploadableImage image : imagesQueue) {
      if (image.getStatus() == UploadableImage.Status.PENDING
          || image.getStatus() == UploadableImage.Status.FAILED) {
        processImageUpload(image);
      }
    }
  }

  public void restartUpload(int position) {
    if (position < 0 || position >= imagesQueue.size()) {
      return;
    }
    UploadableImage item = imagesQueue.get(position);
    item.setStatus(UploadableImage.Status.PENDING);
    item.setProgress(0);
    item.setErrorMessage(null);
    images.setValue(new ArrayList<>(imagesQueue));
    processImageUpload(item);
  }

  private void processImageUpload(@NonNull UploadableImage item) {
    item.setStatus(UploadableImage.Status.UPLOADING);
    imageUploader.uploadImage(
        item.getContentUri(),
        new ImageUploader.ImageUploadProgressListener() {
          @Override
          public void onProgress(int imageIndex, int progress) {
            item.setProgress(progress);
            images.postValue(new ArrayList<>(imagesQueue));
          }

          @Override
          public void onSuccess(int imageIndex, String url) {
            item.setStatus(UploadableImage.Status.COMPLETED);
            item.setRemoteUrl(url);
            item.setProgress(100);
            images.postValue(new ArrayList<>(imagesQueue));
          }

          @Override
          public void onFailed(int imageIndex, String errorMessage) {
            item.setStatus(UploadableImage.Status.FAILED);
            item.setErrorMessage(errorMessage);
            images.postValue(new ArrayList<>(imagesQueue));
          }

          @Override
          public void onCompleted(List<String> urls) {
            // Ignore this callback as we handle success in onSuccess.
          }
        });
  }

  public List<String> getUploadedUrls() {
    return imagesQueue.stream()
        .filter(
            image ->
                image.getStatus() == UploadableImage.Status.COMPLETED
                    && image.getRemoteUrl() != null)
        .map(UploadableImage::getRemoteUrl)
        .collect(Collectors.toList());
  }

  public void clear() {
    imagesQueue.clear();
    images.setValue(new ArrayList<>(imagesQueue));
  }
}
