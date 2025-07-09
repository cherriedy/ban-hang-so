package com.optlab.banhangso.features.shared.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.optlab.banhangso.databinding.ListItemImageUploadBinding;
import com.optlab.banhangso.models.application.UploadableImage;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

public class FileUploadAdapter extends RecyclerView.Adapter<FileUploadAdapter.ViewHolder> {

  public interface ImageActionListener {
    void onRemove(int position);

    void onRetry(int position);
  }

  @NonNull private final List<UploadableImage> uploadableImages = new ArrayList<>();

  @NonNull private final ImageActionListener imageActionListener;

  public FileUploadAdapter(@NonNull ImageActionListener imageActionListener) {
    this.imageActionListener = imageActionListener;
  }

  @NonNull @Override
  public FileUploadAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    ListItemImageUploadBinding binding =
        ListItemImageUploadBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
    return new ViewHolder(binding);
  }

  @Override
  public void onBindViewHolder(
      @NonNull FileUploadAdapter.ViewHolder holder, int position, @NonNull List<Object> payloads) {
    if (payloads.isEmpty()) {
      holder.bind(uploadableImages.get(position), position);
    } else {
      Object payload = payloads.get(0);
      if (payload instanceof Integer progress) {
        holder.updateProgress(progress, uploadableImages.get(position).getStatus());
      } else {
        holder.bind(uploadableImages.get(position), position);
      }
    }
  }

  @Override
  public void onBindViewHolder(@NonNull FileUploadAdapter.ViewHolder holder, int position) {
    holder.bind(uploadableImages.get(position), position);
  }

  @Override
  public int getItemCount() {
    return uploadableImages.size();
  }

  @SuppressLint("NotifyDataSetChanged")
  public void setData(@NonNull List<UploadableImage> uploadableImages) {
    this.uploadableImages.clear();
    this.uploadableImages.addAll(uploadableImages);
    notifyDataSetChanged();
  }

  public void updateProgressAt(int position, int progress, UploadableImage.Status status) {
    if (position >= 0 && position < uploadableImages.size()) {
      Timber.d("Updating item at position %d with new status %s", position, status);
      uploadableImages.get(position).setProgress(progress);
      uploadableImages.get(position).setStatus(status);
      notifyItemChanged(position, progress);
    }
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    private final ListItemImageUploadBinding binding;

    public ViewHolder(@NonNull ListItemImageUploadBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    public void bind(@NonNull UploadableImage item, int position) {
      if (item.getRemoteUrl() != null) {
        Timber.d("Binding item at position %d with URL %s", position, item.getRemoteUrl());
        Glide.with(itemView.getContext())
            .load(item.getRemoteUrl())
            .centerCrop()
            .into(binding.ivImage);
      } else if (item.getContentUri() != null) {
        Timber.d("Binding item at position %d with URI %s", position, item.getContentUri());
        Glide.with(itemView.getContext())
            .load(item.getContentUri())
            .centerCrop()
            .into(binding.ivImage);
      }

      binding.btnRemove.setOnClickListener(v -> imageActionListener.onRemove(position));
      binding.btnRetry.setOnClickListener(v -> imageActionListener.onRetry(position));

      updateProgress(item.getProgress(), item.getStatus());
    }

    private void updateProgress(int progress, @NonNull UploadableImage.Status status) {
      switch (status) {
        case UPLOADING -> {
          Timber.d("Updating progress to %d for status UPLOADING", progress);
          binding.progressBar.setVisibility(View.VISIBLE);
          binding.progressBar.setProgress(progress);
          binding.btnRetry.setVisibility(View.GONE);
          binding.overlay.setVisibility(View.VISIBLE);
        }
        case COMPLETED, PENDING -> {
          Timber.d("Updating progress to %d for status %s", progress, status);
          binding.progressBar.setVisibility(View.GONE);
          binding.btnRetry.setVisibility(View.GONE);
          binding.overlay.setVisibility(View.GONE);
        }
        case FAILED -> {
          Timber.d("Updating progress to %d for status FAILED", progress);
          binding.progressBar.setVisibility(View.GONE);
          binding.overlay.setVisibility(View.VISIBLE);
          binding.btnRetry.setVisibility(View.VISIBLE);
        }
      }
    }
  }
}
