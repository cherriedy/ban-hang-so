package com.optlab.banhangso.models.application;

import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.UUID;
import lombok.Data;

@Data
public class UploadableImage {

  public enum Status {
    PENDING,
    UPLOADING,
    COMPLETED,
    FAILED
  }

  private final String uuid = UUID.randomUUID().toString();

  private Uri contentUri;
  private String remoteUrl;
  private Status status;
  private int progress;
  private String errorMessage;

  public UploadableImage(@NonNull Uri contentUri) {
    this.contentUri = contentUri;
    this.status = Status.PENDING;
    this.progress = 0;
  }

  public UploadableImage(@NonNull String remoteUrl) {
    this.remoteUrl = remoteUrl;
    this.status = Status.COMPLETED;
    this.progress = 0;
  }

  public boolean isCompleted() {
    return status == Status.COMPLETED;
  }

  public boolean isUploading() {
    return status == Status.UPLOADING;
  }

  public boolean isFailed() {
    return status == Status.FAILED;
  }
}
